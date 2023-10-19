package ai.FakePlayersAI.ClassAI;

import ai.FakePlayersAI.BaseFakeAI;
import l2open.gameserver.model.L2Player;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.TradeController;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.L2PlayerAI;
import l2open.gameserver.clientpackets.RequestPrivateStoreQuitBuy;
import l2open.gameserver.common.Buff;
import l2open.gameserver.common.BuffScheme;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Transaction;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.gameserver.templates.L2Item;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Util;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class FemaleSoulHoundAI extends BaseFakeAI {

    enum Skills {

        SoulVortex(1512),

        Blink(1448),
        Warp(628),


        HardMarsh(479),


        SoulCleance(1510),
        StealDivivnity(1440),


        PrideofKamael(1444),
        LightingBarrier(1515),

        DeathMark(1435),
        SoulofPain(1436),
        CursOfDivinity(1439),
        AnnihilationCircle(1438);


        public int getId() {
            return id;
        }

        private int id;

        Skills(int i) {
            id = i;
        }
    }

    private ScheduledFuture<?> _botTask;
    private ScheduledFuture<?> _updateTask;
    private ScheduledFuture<?> _buffTask;
    private Transaction tradeTransaction;
    private static final int _botTaskDelay = 250;
    private static final int _updateTaskDelay = 1000;
    private static final int _buffTaskDelay = 1000;
    private L2Character target;
    private L2Player master;
    private L2Player bot;
    private int spirit_shot;

    private static final int[] soulshot = {
            1464,   //Soulshot: C-grade
            1465,   //Soulshot: B-grade
            1466,   //Soulshot: A-grade
            1467,   //Soulshot: S-grade
    };
    private static final int[] spiritshot = {
            3949,   //spiritshot: C-grade
            3950,   //spiritshot: B-grade
            3951,   //spiritshot: A-grade
            3952,   //spiritshot: S-grade
    };


    public FemaleSoulHoundAI(L2Character actor) {
        super((L2Player) actor);

        bot = getActor();
        if (bot.isMageClass()) {
            for (Buff buffId : BuffScheme.buffSchemes.get(1).getBuffIds()) {
                buff(buffId.getId(), buffId.getLevel(), (L2Playable) actor);
            }
        } else {
            for (Buff buffId : BuffScheme.buffSchemes.get(2).getBuffIds()) {
                buff(buffId.getId(), buffId.getLevel(), (L2Playable) actor);
            }
        }


//        _botTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BotTask(getActor()), 500, 500);
//        _updateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new UpdateTask(getActor()), 333, 333);
//        _buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SelfBuffTask(getActor()), 500, 500);
        _botTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BotTask(getActor()), _botTaskDelay, _botTaskDelay);
        _updateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new UpdateTask(getActor()), _updateTaskDelay, _updateTaskDelay);
        _buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SelfBuffTask(getActor()), _buffTaskDelay, _buffTaskDelay);
    }

    private static void buff(int id, int level, L2Playable playable) {
        if (id < 20)
            return;

        if (playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel)) {
            playable.sendMessage("Баффер доступен для игроков с уровней не ниже " + ConfigValue.BufferMinLevel + " и не выше " + ConfigValue.BufferMaxLevel + ".");
            return;
        }

        final double hp = playable.getCurrentHp();
        final double mp = playable.getCurrentMp();
        final double cp = playable.getCurrentCp();
        int buff_level = level > 0 ? level : SkillTable.getInstance().getBaseLevel(id);
        L2Skill skill = SkillTable.getInstance().getInfo(id, buff_level > 100 ? 1 : buff_level);
        if (buff_level > 100) {
            buff_level = SkillTreeTable.convertEnchantLevel(SkillTable.getInstance().getBaseLevel(id), buff_level, skill.getEnchantLevelCount());
            skill = SkillTable.getInstance().getInfo(id, buff_level);
        }

        if (!skill.checkSkillAbnormal(playable) && !skill.isBlockedByChar(playable, skill)) {
            for (EffectTemplate et : skill.getEffectTemplates()) {
                int result;
                Env env = new Env(playable, playable, skill);
                L2Effect effect = et.getEffect(env);
                if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                    // Эффекты однократного действия не шедулятся, а применяются немедленно
                    // Как правило это побочные эффекты для скиллов моментального действия
                    effect.onStart();
                    effect.onActionTime();
                    effect.onExit();
                } else if (effect != null && !effect.getEffected().p_block_buff.get()) {
                    if (ConfigValue.BufferTime > 0)
                        effect.setPeriod(ConfigValue.BufferTime * 60000);
                    if ((result = playable.getEffectList().addEffect(effect)) > 0) {
                        if ((result & 2) == 2)
                            playable.setCurrentHp(hp, false);
                        if ((result & 4) == 4)
                            playable.setCurrentMp(mp);
                        if ((result & 8) == 8)
                            playable.setCurrentCp(cp);
                    }
                }
            }
        }
        //skill.getEffects(playable, playable, false, false, ConfigValue.BBS_BUFFER_ALT_TIME * 60000, 0, false);
    }


    public void autoSoulshot(L2Player bot) {
        L2Item.Grade crystalType;
        int soul = 0;
        int spirit = 0;
        if (bot.getActiveWeaponInstance() != null) {
            crystalType = bot.getActiveWeaponInstance().getCrystalType();
            switch (crystalType) {
                case NONE:
                    break;
                case D:
                    break;
                case C:
                    soul = soulshot[0];
                    spirit = spiritshot[0];
                    break;
                case B:
                    soul = soulshot[1];
                    spirit = spiritshot[1];
                    break;
                case A:
                    soul = soulshot[2];
                    spirit = spiritshot[2];
                    break;
                case S:
                case S80:
                case S84:
                    soul = soulshot[3];
                    spirit = spiritshot[3];
                    break;
            }
        }

        if (bot.getInventory().getItemByItemId(soul) != null) {
            IItemHandler handler = ItemHandler.getInstance().getItemHandler(soul);
            handler.useItem(bot, bot.getInventory().getItemByItemId(soul), false);
        }
        if (bot.getInventory().getItemByItemId(spirit) != null) {
            IItemHandler handler = ItemHandler.getInstance().getItemHandler(spirit);
            handler.useItem(bot, bot.getInventory().getItemByItemId(spirit), false);
        }
    }

    private void debag(String msg) {
        if (master != null){
            master.sendPacket(new Say2(bot.getObjectId(), 0, bot.getName(), msg));
        }
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected void ATTACKED(L2Character attacker, int damage, L2Skill skill) {
        super.ATTACKED(attacker, damage, skill);
    }

    public class BotTask extends RunnableImpl {

        long delay = System.currentTimeMillis();

        public BotTask(L2Player _bot) {
            bot = _bot;
        }

        private L2Skill getSkill(Skills skill) {
            return bot.getKnownSkill(skill.getId());
        }

        private boolean skillIsCD(Skills skill) {
            return !bot.isSkillDisabled((long) getSkill(skill).getId());
        }

        private void attack(L2Character target, boolean force) {
            bot.setTarget(target);
            L2Skill skill = null;
            double dist = bot.getRealDistance3D(target);

            if (skillIsCD(Skills.DeathMark) && !target.getEffectList().containEffectFromSkills(new int[]{Skills.DeathMark.getId()})) {
                //проверка на дебаф
                skill = getSkill(Skills.DeathMark);

            } else if (skillIsCD(Skills.SoulVortex)) {
                skill = getSkill(Skills.SoulVortex);
            } else if (skillIsCD(Skills.StealDivivnity) && target.isPlayer()) {
                skill = getSkill(Skills.StealDivivnity);
            } else if (skillIsCD(Skills.SoulofPain)) {
                skill = getSkill(Skills.SoulofPain);
            } else if (skillIsCD(Skills.AnnihilationCircle)) {
                skill = getSkill(Skills.AnnihilationCircle);
            } else if (skillIsCD(Skills.CursOfDivinity)) {
                skill = getSkill(Skills.CursOfDivinity);
            }


            if (dist < 90 && skillIsCD(Skills.Blink)) {
                skill = getSkill(Skills.Blink);
            }
            if (dist > 1000 && skillIsCD(Skills.Warp)) {
                skill = getSkill(Skills.Warp);
            }
            if (skill != null && bot.getCurrentMp() > skill.getMpConsume()) {
                autoSoulshot(bot);
                bot.getAI().Cast(skill, (L2Character) bot.getTarget(), force, false);
            }
        }


        public void runImpl() {

            if (bot.isInParty() && master != null) {
                if (!bot.isInCombat() && delay < System.currentTimeMillis() - 10000) {
                    bot.moveToLocation(Location.coordsRandomize(master.getLoc(), 50, 200), 0, true);
                    delay = System.currentTimeMillis();
                }
//                for (L2Player player : bot.getParty().getPartyMembers()) {
//                    player.sendPacket(new PartySpelled(bot, true));
//                }
            }

            if (bot.isInTransaction()) {
                final Transaction transaction = bot.getTransaction();
                master = transaction.getOtherPlayer(bot);

                if (bot.getTransaction().isTypeOf(Transaction.TransactionType.PARTY)) {
                    L2Party party = master.getParty();
                    bot.joinParty(party);
                    bot.setTitle(master.getName(), false);
                    bot.setFollowTarget(master);
                    bot.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, master, 50);
                    master.sendMessage("bot kinul paty");
                    transaction.cancel();
                } else if (bot.getTransaction().isTypeOf(Transaction.TransactionType.TRADE_REQUEST)) {
                    tradeTransaction = new Transaction(Transaction.TransactionType.TRADE, master, bot);
                    master.sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(bot.getName()), new TradeStart(master, bot));
                    master.sendMessage("botu kinuli trade");


//                    bot.sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(master.getName()), new TradeStart(bot, master));
//                    master.sendPacket(new SystemMessage(SystemMessage.YOU_BEGIN_TRADING_WITH_C1).addString(bot.getName()), new TradeStart(master, bot));


                }else if (bot.getTransaction().isTypeOf(Transaction.TransactionType.TRADE)){
                    //master.sendMessage("trade");

                    //_botTask.cancel(true);
                    //_buffTask.cancel(true);
                    //_updateTask.cancel(true);
                    if (tradeTransaction.isConfirmed(master)){
                        tradeTransaction.confirm(bot);
                        tradeTransaction.tradeItems();
                        transaction.cancel();
                    }

                }
                else if (bot.getTransaction().isTypeOf(Transaction.TransactionType.CLAN)) {
                    transaction.cancel();
                } else if (bot.getTransaction().isTypeOf(Transaction.TransactionType.FRIEND)) {
                    transaction.cancel();
                } else if (bot.getTransaction().isTypeOf(Transaction.TransactionType.DUEL)) {
                    new Duel(master, bot, false);
                    transaction.cancel();
                }

            }

            if (bot.isInParty() && !bot.isDead()) {
                double distPlayerTarget = 0;
                master = bot.getParty().getPartyLeader();
                L2Object masterTarget = master.getTarget();
                if (masterTarget != null) {
                    if (masterTarget.isMonster()) {
                        target = (L2Character) masterTarget;
                    }
                }
                if (bot.isInDuel()) {
                    target = master;
                }
                if (target != null) {
                    distPlayerTarget = master.getRealDistance3D(target);
                }

                if (target != null && !target.isDead() && distPlayerTarget < 1300) {
                    if (target.isMonster()) {
                        attack(target, false);
                    }
                    if (master.isInDuel()) {
                        attack(master, true);
                    }

                }
                int maxPartyRange = (int) bot.getRealDistance3D(master);
                //debag(String.valueOf(maxPartyRange));
                if (maxPartyRange > 500) {
                    bot.setFollowTarget(master);
                    bot.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, master, 50);
                    if (skillIsCD(Skills.Warp)) {
                        bot.altUseSkill(getSkill(Skills.Warp), null);
                        debag("warp");
                    }
                    bot.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, master, 50);
                }
            }
        }
    }

    public class UpdateTask extends RunnableImpl {
        L2Player bot;
        L2Character target;
        L2Player player;
        L2Party party;


        public UpdateTask(L2Player actor) {
            bot = actor;
        }

        @Override
        public void runImpl() throws Exception {
            if (bot.isInParty()) {
                for (L2Player player : bot.getParty().getPartyMembers()) {
                    player.sendPacket(new PartySpelled(bot, true));
                }
            }
        }
    }

    public class SelfBuffTask extends RunnableImpl {
        L2Player bot;
        Skills[] buffs = {Skills.LightingBarrier, Skills.PrideofKamael, Skills.HardMarsh};

        public SelfBuffTask(L2Player actor) {
            this.bot = actor;
        }

        private L2Skill getSkill(Skills skill) {
            return bot.getKnownSkill(skill.getId());
        }

        private boolean skillIsCD(Skills skill) {
            return !bot.isSkillDisabled((long) getSkill(skill).getId());
        }

        @Override
        public void runImpl() throws Exception {
            List<L2Skill> _buffList = new ArrayList<L2Skill>();
            for (Skills skills : buffs) {
                L2Skill self = bot.getKnownSkill(skills.getId());
                if (bot.getEffectList().getEffectBySkillId(self.getId()) == null) {
//                    if (self.isToggle()){
//                        bot.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, self);
//                    }else {
//                        bot.getAI().Cast(self, bot, false, true);
//                    }
                    //debag(self.getName());
                    bot.getAI().Cast(self, bot, false, true);
                    if (self.isToggle()) {
                        bot.altUseSkill(self, bot);
                    }
                }
            }
            for (L2Effect e : bot.getEffectList().getAllEffects()) {
                if (e.isOffensive() && e.getSkill().isCancelable() && e.getSkill().getId() != 2530 && e.getSkill().getId() != 5660 && e.getSkill().getId() != 4515 && e.getSkill().getId() != 4215 && !_buffList.contains(e.getSkill()))
                    _buffList.add(e.getSkill());
            }
            if (_buffList.size() > 0 && skillIsCD(Skills.SoulCleance)) {
                //debag("SoulCleance");
                L2Skill knownSkill = bot.getKnownSkill(Skills.SoulCleance.id);
                bot.altUseSkill(knownSkill, bot);
                //bot.getAI().Cast(knownSkill, bot, false, true);
            }
            if (bot.getEffectList().getEffectByType(EffectType.i_resurrection) != null) {
                bot.doRevive();
                // debag("i_resurrection");
            }
        }
    }
}