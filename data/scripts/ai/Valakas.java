package ai;

import bosses.ValakasManager;
import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.*;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

public class Valakas extends DefaultAI {
    // Self skills
    final L2Skill s_lava_skin = getSkill(4680, 1), s_fear = getSkill(4689, 1), s_defence_down = getSkill(5864, 1), s_berserk = getSkill(5865, 1), s_regen = getSkill(4691, 1);

    // Offensive damage skills
    final L2Skill s_tremple_left = getSkill(4681, 1), s_tremple_right = getSkill(4682, 1), s_tail_stomp_a = getSkill(4685, 1), s_tail_lash = getSkill(4688, 1), s_meteor = getSkill(4690, 1), s_breath_low = getSkill(4683, 1), s_breath_high = getSkill(4684, 1);

    // Offensive percentage skills
    final L2Skill s_destroy_body = getSkill(5860, 1), s_destroy_soul = getSkill(5861, 1), s_destroy_body2 = getSkill(5862, 1), s_destroy_soul2 = getSkill(5863, 1);

    // Timers
    private long defenceDownTimer = Long.MAX_VALUE;

    // Timer reuses
    private final long defenceDownReuse = 120000L;

    // Vars
    private double _rangedAttacksIndex, _counterAttackIndex, _attacksIndex;
    private int _hpStage = 0;
    private GArray<L2NpcInstance> minions = new GArray<L2NpcInstance>();

    public Valakas(L2Character actor) {
        super(actor);
    }

    @Override
    protected void ATTACKED(L2Character attacker, int damage, L2Skill skill) {
        L2NpcInstance actor = getActor();
        ValakasManager.setLastAttackTime();
        for (L2Playable p : ValakasManager.getZone().getInsidePlayers()) {
            if (p != null)
                notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1);
        }
        if (damage > 100) {
            if (attacker.getDistance(actor) > 400)
                _rangedAttacksIndex += damage / 1000D;
            else
                _counterAttackIndex += damage / 1000D;
        }
        _attacksIndex += damage / 1000D;
        super.ATTACKED(attacker, damage, skill);
    }

    @Override
    protected void onEvtArrived() {
        if (getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
            for (L2Player p : ValakasManager.getZone().getInsidePlayers()) {
                if (!p.isDead())
                    notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
            }
            changeIntention(CtrlIntention.AI_INTENTION_ATTACK, ValakasManager.getRandomPlayer(), null);
        }
        super.onEvtArrived();
    }

    @Override
    protected boolean createNewTask() {
        clearTasks();
        L2Character target;
        if ((target = prepareTarget()) == null) {
            setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            return false;
        }

        L2NpcInstance actor = getActor();
        if (actor.isDead())
            return false;

        double distance = actor.getDistance(target);

        // Buffs and stats
        double chp = actor.getCurrentHpPercents();
        if (_hpStage == 0) {
            actor.altOnMagicUseTimer(actor, getSkill(4691, 1));
            _hpStage = 1;
        } else if (chp < 80 && _hpStage == 1) {
            actor.altOnMagicUseTimer(actor, getSkill(4691, 2));
            defenceDownTimer = System.currentTimeMillis();
            _hpStage = 2;
        } else if (chp < 50 && _hpStage == 2) {
            actor.altOnMagicUseTimer(actor, getSkill(4691, 3));
            _hpStage = 3;
        } else if (chp < 30 && _hpStage == 3) {
            actor.altOnMagicUseTimer(actor, getSkill(4691, 4));
            _hpStage = 4;
        } else if (chp < 10 && _hpStage == 4) {
            actor.altOnMagicUseTimer(actor, getSkill(4691, 5));
            _hpStage = 5;
        }

        // Minions spawn
        if (getAliveMinionsCount() < ConfigValue.MaxMinionOfValakas && Rnd.chance(5)) {
            L2NpcInstance minion = Functions.spawn(Location.findPointToStay(actor.getLoc(), 400, 700, actor.getReflection().getGeoIndex()), 29029);  // Valakas Minions
            minions.add(minion);
            ValakasManager.addValakasMinion(minion);
        }

        // Tactical Movements
        if (_counterAttackIndex > 2000) {
            ValakasManager.broadcastScreenMessage(1801075);
            _counterAttackIndex = 0;
            return chooseTaskAndTargets(s_berserk, actor, 0);
        } else if (_rangedAttacksIndex > 2000) {
            if (Rnd.chance(60)) {
                L2Character randomHated = actor.getRandomHated();
                if (randomHated != null) {
                    setAttackTarget(randomHated);
                    actor.startConfused();
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            L2NpcInstance actor = getActor();
                            if (actor != null)
                                actor.stopConfused();
                            _madnessTask = null;
                        }
                    }, 20000L);
                }
                ValakasManager.broadcastScreenMessage(1801076);
                _rangedAttacksIndex = 0;
            } else {
                ValakasManager.broadcastScreenMessage(1801074);
                _rangedAttacksIndex = 0;
                return chooseTaskAndTargets(s_berserk, actor, 0);
            }
        } else if (_attacksIndex > 3000) {
            ValakasManager.broadcastScreenMessage(1801072);
            _attacksIndex = 0;
            return chooseTaskAndTargets(s_defence_down, actor, 0);
        } else if (defenceDownTimer < System.currentTimeMillis()) {
            ValakasManager.broadcastScreenMessage(1801071);
            defenceDownTimer = System.currentTimeMillis() + defenceDownReuse + Rnd.get(60) * 1000L;
            return chooseTaskAndTargets(s_fear, target, distance);
        }

        // Basic Attack
        if (Rnd.chance(50))
            return chooseTaskAndTargets(Rnd.chance(50) ? s_tremple_left : s_tremple_right, target, distance);

        // Stage based skill attacks
        FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();
        switch (_hpStage) {
            case 1:
                addDesiredSkill(d_skill, target, distance, s_breath_low);
                addDesiredSkill(d_skill, target, distance, s_tail_stomp_a);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear);
                break;
            case 2:
            case 3:
                addDesiredSkill(d_skill, target, distance, s_breath_low);
                addDesiredSkill(d_skill, target, distance, s_tail_stomp_a);
                addDesiredSkill(d_skill, target, distance, s_breath_high);
                addDesiredSkill(d_skill, target, distance, s_tail_lash);
                addDesiredSkill(d_skill, target, distance, s_destroy_body);
                addDesiredSkill(d_skill, target, distance, s_destroy_soul);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear);
                break;
            case 4:
            case 5:
                addDesiredSkill(d_skill, target, distance, s_breath_low);
                addDesiredSkill(d_skill, target, distance, s_tail_stomp_a);
                addDesiredSkill(d_skill, target, distance, s_breath_high);
                addDesiredSkill(d_skill, target, distance, s_tail_lash);
                addDesiredSkill(d_skill, target, distance, s_destroy_body);
                addDesiredSkill(d_skill, target, distance, s_destroy_soul);
                addDesiredSkill(d_skill, target, distance, s_meteor);
                addDesiredSkill(d_skill, target, distance, s_fear);
                addDesiredSkill(d_skill, target, distance, Rnd.chance(60) ? s_destroy_soul2 : s_destroy_body2);
                break;
        }
        L2Skill r_skill = selectTopSkill(d_skill);
        if (r_skill != null && !r_skill.isOffensive())
            target = actor;

        return chooseTaskAndTargets(r_skill, target, distance);
    }

    @Override
    protected void thinkAttack() {
        L2NpcInstance actor = getActor();
        // Lava buff
        if (actor.isInZone(L2Zone.ZoneType.poison))
            if (actor.getEffectList() != null && actor.getEffectList().getEffectsBySkill(s_lava_skin) == null)
                actor.altOnMagicUseTimer(actor, s_lava_skin);
        super.thinkAttack();
    }

    private L2Skill getSkill(int id, int level) {
        return SkillTable.getInstance().getInfo(id, level);
    }

    private int getAliveMinionsCount() {
        int i = 0;
        for (L2NpcInstance n : minions)
            if (n != null && !n.isDead())
                i++;
        return i;
    }

    @Override
    protected void MY_DYING(L2Character killer) {
        if (minions != null && !minions.isEmpty())
            for (L2NpcInstance n : minions)
                n.deleteMe();
        //mysql.set("UPDATE `bos_debug` SET `attacked`=0 where bos_id=29028");
        super.MY_DYING(killer);
    }
}