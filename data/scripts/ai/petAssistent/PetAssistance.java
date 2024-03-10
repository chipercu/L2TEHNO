package ai.petAssistent;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Log;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class PetAssistance extends DefaultAI {

    private boolean _thinking = false;
    private ScheduledFuture<?> _actionTask;
    private final L2Player master;

    private static final String pet_spoil_force = "pet_spoil_force";
    private static final String pet_spoil_single = "pet_spoil_single";
    private static final String pet_pick = "pet_pick";
    private static final String pet_pick_only_adena = "pet_pick_only_adena";
    private static final String pet_pick_herb = "pet_pick_herb";


    public enum TASK {
        FOLLOW, SPOIL, SWEEP, PICK
    }


    public PetAssistance(L2Character actor, L2Player master) {
        super(actor);
        this.master = master;
    }

    @Override
    protected void onEvtThink() {
        L2NpcInstance actor = getActor();
        if (_thinking || actor.isActionsDisabled() || actor.isAfraid() || actor.isDead() || actor.isMovementDisabled()) {
            return;
        }

        _thinking = true;
        L2Object masterTarget = master.getTarget();

        try {
            if (checkSweepMobs()) {
                setTask(TASK.SWEEP, master);
            } else if (checkDroppesItems()) {
                setTask(TASK.PICK, master);
            } else if (masterTarget != null && masterTarget.isMonster()) {
                L2MonsterInstance monster = (L2MonsterInstance) masterTarget;
                if (!monster.isDead() && !monster.isSpoiled(master)) {
                    setTask(TASK.SPOIL, monster);
                } else {
                    setTask(TASK.FOLLOW, master);
                }
            } else {
                setTask(TASK.FOLLOW, master);
            }

        } catch (Exception e) {
            _log.warning("Pet_Assistant: ");
            e.printStackTrace();
        } finally {
            _thinking = false;
        }
    }

    protected boolean checkDroppesItems() {
        if (!master.getVarB(pet_pick)){
            return false;
        }

        boolean onlyAdena = master.getVarB(pet_pick_only_adena);
        boolean pickHerb = master.getVarB(pet_pick_herb);

        if (onlyAdena){
            return L2World.getAroundObjects(master, 1000, 200)
                    .stream()
                    .filter(L2Object::isItem)
                    .map(l2Object -> (L2ItemInstance) l2Object).anyMatch(l2ItemInstance -> l2ItemInstance.getItemId() == 57);
        }else {
            if (pickHerb){
                return L2World.getAroundObjects(master, 1000, 200).stream()
                        .anyMatch(L2Object::isItem);
            }else {
                return L2World.getAroundObjects(master, 1000, 200).stream()
                        .filter(L2Object::isItem)
                        .map(l2Object -> (L2ItemInstance) l2Object)
                        .anyMatch(l2ItemInstance -> !l2ItemInstance.isHerb());
            }
        }
    }

    protected boolean checkSweepMobs() {
        long count = L2World.getAroundObjects(master, 1000, 200)
                .stream()
                .filter(L2Object::isMonster)
                .map(l2Object -> (L2MonsterInstance) l2Object)
                .filter(l2MonsterInstance -> l2MonsterInstance.isSpoiled(master))
                .filter(L2Character::isDead)
                .count();
        return count > 0;
    }


    protected List<L2MonsterInstance> findSweepMobs() {
        return L2World.getAroundObjects(master, 1000, 200)
                .stream()
                .filter(L2Object::isMonster)
                .map(l2Object -> (L2MonsterInstance) l2Object)
                .filter(l2MonsterInstance -> l2MonsterInstance.isSpoiled(master)).collect(Collectors.toList());
    }


    protected L2Object findDroppedItem() {
        boolean onlyAdena = master.getVarB(pet_pick_only_adena);
        boolean pickHerb = master.getVarB(pet_pick_herb);
        if (onlyAdena){
            return L2World.getAroundObjects(master, 1000, 200).stream()
                    .filter(L2Object::isItem)
                    .map(l2Object -> (L2ItemInstance) l2Object)
                    .filter(l2ItemInstance -> l2ItemInstance.getItemId() == 57)
                    .findFirst().orElse(null);
        }else {
            if (pickHerb){
                return L2World.getAroundObjects(master, 1000, 200).stream().filter(L2Object::isItem).findFirst().orElse(null);
            }else {
                return L2World.getAroundObjects(master, 1000, 200).stream()
                        .filter(L2Object::isItem)
                        .map(l2Object -> (L2ItemInstance) l2Object)
                        .filter(l2ItemInstance -> !l2ItemInstance.isHerb())
                        .findFirst().orElse(null);
            }
        }
    }


    protected void setTask(TASK task, L2Character target) {
        if (_actionTask != null) {
            _actionTask.cancel(false);
            _actionTask = null;
        }
        _actionTask = ThreadPoolManager.getInstance().schedule(new Action(task, master, target), 250L);
    }

    @Override
    protected boolean thinkActive() {
        L2NpcInstance actor = getActor();
        L2Character following = actor.getFollowTarget();
        if (following == null || !actor.isFollow) {
            L2Playable master = getMaster();
            if (master != null) {
                actor.setFollowTarget(master);
                actor.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, master, 100);
            }
        }
        super.thinkActive();
        return false;
    }

    protected void thinkFollow() {
        L2NpcInstance actor = getActor();
        L2Character target = actor.getFollowTarget();

        //Находимся слишком далеко цели, либо цель не пригодна для следования, либо не можем перемещаться
        if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000 || actor.isMovementDisabled()) {
            actor.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            return;
        }

        //Уже следуем за этой целью
        if (actor.isFollow && actor.getFollowTarget() == target) {
            clientActionFailed();
            return;
        }

        //Находимся достаточно близко
        if (actor.isInRange(target, 100 + 20)) {
            clientActionFailed();
            return;
        }

        actor.setRunning();
        actor.followToCharacter(target, 100, false, true);
    }

    protected void thinkSpoil(L2MonsterInstance target) {
        boolean force = master.getVarB(pet_spoil_force);
        if (force) {
            useSpoil(target);
        } else {
            if (target.getAggroListPlayable().contains(master)) {
                useSpoil(target);
            }else {
                setTask(TASK.FOLLOW, master);
            }
        }
    }

    private void useSpoil(L2MonsterInstance target) {
        L2Skill skill;
        boolean isSingleSpoil = master.getVarB(pet_spoil_single);
        if (isSingleSpoil) {
            skill = SkillTable.getInstance().getInfo(254, 1);
        } else {
            skill = SkillTable.getInstance().getInfo(302, 1);
        }

        if (getActor().getDistance3D(target) > skill.getCastRange()) {
            getActor().moveToLocation(target.getLoc(), 20, true);
        } else {
            if (!isSingleSpoil){
                target.getAroundNpc(250, 100).stream()
                        .filter(L2Object::isMonster)
                        .map(l2NpcInstance -> (L2MonsterInstance) l2NpcInstance)
                        .filter(l2MonsterInstance -> !l2MonsterInstance.isDead())
                        .filter(l2MonsterInstance -> !l2MonsterInstance.isSpoiled())
                        .forEach(l2MonsterInstance -> {
                            getActor().doCast(skill, l2MonsterInstance, false);
                            l2MonsterInstance.setSpoiled(true, master);
                            l2MonsterInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, master, 100);
                            l2MonsterInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, master);
                        });
            }
            getActor().doCast(skill, target, false);
            target.setSpoiled(true, master);
            target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, master, 100);
            target.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, master);
        }
    }

    protected void thinkPick(L2ItemInstance item) {
        if (isPickUp(item)) {
            if (getActor().getDistance3D(item) > 50) {
                getActor().moveToLocation(item.getLoc(), 20, true);
            } else {
                // Herbs
                if (item.isHerb()) {
                    L2Skill[] skills = item.getItem().getAttachedSkills();
                    if (skills != null && skills.length > 0)
                        for (L2Skill skill : skills) {
                            master.altUseSkill(skill, master);
                            if (master.getPet() != null && master.getPet().isSummon() && !master.getPet().isDead() && skill.getAbnormalLv() == -1) {
                                master.getPet().altUseSkill(skill, master.getPet());
                            }
                        }
                    item.deleteMe();
                    return;
                }
                boolean equip = (item.getCustomFlags() & L2ItemInstance.FLAG_EQUIP_ON_PICKUP) == L2ItemInstance.FLAG_EQUIP_ON_PICKUP;

                if (!master.isInParty() || equip) {
                    if (!master.getInventory().validateWeight(item)) {
                        master.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        return;
                    }

                    if (!master.getInventory().validateCapacity(item)) {
                        master.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                        return;
                    }

                    if (!item.pickupMe(master)) {
                        return;
                    }

                    master.sendPacket(SystemMessage.obtainItems(item));
                    Log.LogItem(master, Log.PickupItem, master.getInventory().addItem(item));

                    if (equip) {
                        master.getInventory().equipItem(item, true);
                    }
                    master.sendChanges();
                } else if (item.getItemId() == 57) {
                    if (!item.pickupMe(master)) {
                        return;
                    }
                    master.getParty().distributeAdena(item, master);
                } else {
                    // Нужно обязательно сначало удалить предмет с земли.
                    if (!item.pickupMe(null)) {
                        return;
                    }
                    master.getParty().distributeItem(master, item);
                }
                item.setItemDropOwner(null, 0);
                master.broadcastPickUpMsg(item);
            }
        }

    }


    protected boolean isPickUp(L2ItemInstance item) {
        if (item.getDropTimeOwner() != 0 && item.getItemDropOwner() != null && item.getDropTimeOwner() > System.currentTimeMillis() && master != item.getItemDropOwner() && (!item.getItemDropOwner().isInParty() || !master.isInParty() || master.isInParty() && item.getItemDropOwner().isInParty() && master.getParty() != item.getItemDropOwner().getParty())) {
            SystemMessage sm;
            if (item.getItemId() == 57) {
                sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                sm.addNumber(item.getCount());
            } else {
                sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                sm.addItemName(item.getItemId());
            }
            master.sendPacket(sm);
            return false;
        }
        return true;
    }

    protected void useSweep() {
        List<L2MonsterInstance> sweepMobs = findSweepMobs();
        for (L2MonsterInstance monster : findSweepMobs()) {
            thinkSweep(monster);
        }
    }


    protected void thinkSweep(L2MonsterInstance target) {
        if (getActor().getDistance3D(target) > 50) {
            getActor().moveToLocation(target.getLoc(), 20, true);
            return;
        }

        if (target == null || !target.isDead() || !target.isSpoiled()) {
            return;
        }
        if (!target.isSpoiled(master)) {
            return;
        }

        L2ItemInstance[] items = target.takeSweep();

        if (items == null) {
            master.getAI().setAttackTarget(null);
            target.endDecayTask();
            return;
        }

        target.setSpoiled(false, null);

        for (L2ItemInstance item : items) {
            if (master.isInParty() && master.getParty().isDistributeSpoilLoot()) {
                master.getParty().distributeItem(master, item);
                continue;
            }

            long itemCount = item.getCount();
            if (master.getInventoryLimit() <= master.getInventory().getSize() && (!item.isStackable() || master.getInventory().getItemByItemId(item.getItemId()) == null)) {
                item.dropToTheGround(master, target);
                continue;
            }

            item = master.getInventory().addItem(item);
            Log.LogItem(master, target, Log.SweepItem, item);

            SystemMessage smsg;
            if (itemCount == 1) {
                smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1);
                smsg.addItemName(item.getItemId());
                master.sendPacket(smsg);
            } else {
                smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1);
                smsg.addItemName(item.getItemId());
                smsg.addNumber(itemCount);
                master.sendPacket(smsg);
            }
            if (master.isInParty())
                if (itemCount == 1) {
                    smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_S2_BY_USING_SWEEPER);
                    smsg.addString(master.getName());
                    smsg.addItemName(item.getItemId());
                    master.getParty().broadcastToPartyMembers(master, smsg);
                } else {
                    smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_3_S2_S_BY_USING_SWEEPER);
                    smsg.addString(master.getName());
                    smsg.addItemName(item.getItemId());
                    smsg.addNumber(itemCount);
                    master.getParty().broadcastToPartyMembers(master, smsg);
                }
        }
        master.getAI().setAttackTarget(null);
        target.endDecayTask();
    }


    protected class Action extends RunnableImpl {

        private final TASK task;
        private final L2Player master;
        private final L2Character target;

        public Action(TASK task, L2Player master, L2Character target) {
            this.task = task;
            this.master = master;
            this.target = target;
        }

        @Override
        public void runImpl() throws Exception {
            if (getActor() == null) {
                return;
            }

            switch (task) {
                case FOLLOW: {
                    getActor().setFollowTarget(master);
                    thinkFollow();
                    break;
                }
                case SPOIL: {
                    thinkSpoil((L2MonsterInstance) target);
                    break;
                }
                case SWEEP: {
//                    thinkSweep((L2MonsterInstance) target);
                    useSweep();
                    break;
                }
                case PICK: {
                    L2Object droppedItem = findDroppedItem();
                    if (droppedItem != null && droppedItem.isItem()) {
                        thinkPick((L2ItemInstance) droppedItem);
                    }
//                    master.sendMessage("pick");
                    break;
                }
            }
        }
    }

    @Override
    protected void onIntentionPickUp(L2Object item) {
        super.onIntentionPickUp(item);
    }

    public L2Playable getMaster() {
        return master;
    }

}
