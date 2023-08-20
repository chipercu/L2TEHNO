package l2open.gameserver.skills.skillclasses;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.CubicManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Cubic;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncAdd;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Location;

public class Summon extends L2Skill 
{
    private final SummonType _summonType;

    private final float _expPenalty;
    private final int _itemConsumeIdInTime;
    private final int _itemConsumeCountInTime;
    private final int _itemConsumeDelay;
    private final int _lifeTime;
    private final int npcLevel;

    private static enum SummonType 
	{
		PET,
		CUBIC,
		AGATHION,
		TRAP,
		EVENT_TRAP,
		DECOY,
		MERCHANT
    }

    public Summon(StatsSet set) 
	{
        super(set);

        _summonType = Enum.valueOf(SummonType.class, set.getString("summonType", "PET").toUpperCase());
        _expPenalty = set.getFloat("expPenalty", 0.f);
        _itemConsumeIdInTime = set.getInteger("itemConsumeIdInTime", 0);
        _itemConsumeCountInTime = set.getInteger("itemConsumeCountInTime", 0);
        _itemConsumeDelay = set.getInteger("itemConsumeDelay", 240) * 1000;
        _lifeTime = set.getInteger("lifeTime", 1200) * 1000;
        npcLevel = set.getInteger("npcLevel", 0);
    }

    @Override
    public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first) 
	{
        if(_summonType == SummonType.CUBIC && !target.isPlayer())
            return false;

        L2Player player = _summonType == SummonType.CUBIC ? target.getPlayer() : activeChar.getPlayer();
        if (player == null)
            return false;

        // Siege Golem, Wild Hog Cannon, Swoop Cannon
        if(_id == 13 || _id == 299 || _id == 448) 
		{
			if(ConfigValue.SummonSiegeZone && !player.isInZone(L2Zone.ZoneType.Siege))
			{
                player.sendMessage("Использовать можно только в осадной зоне.");
                return false;
            }
            SystemMessage sm = null;
            Siege siege = SiegeManager.getSiege(player, true);
            if(siege == null)
                sm = Msg.YOU_ARE_NOT_IN_SIEGE;
            else if(player.getClanId() != 0 && siege.getAttackerClan(player.getClan()) == null)
                sm = Msg.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE;
            if(sm != null) 
			{
                player.sendPacket(sm);
                return false;
            }
        }

        switch (_summonType) 
		{
            case CUBIC:
                break;
            case AGATHION:
				if(player.isInEvent() == 14)
					return false;
                if(player.getAgathion() != null && getNpcId() != 0) 
				{
					
					player.sendPacket(Msg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
                    return false;
                }
                // Попытка использования скила отзыва без вызванного agathion-а.
                if (player.getAgathion() == null && getNpcId() == 0) 
				{
                    activeChar.sendPacket(Msg.AGATHION_SKILLS_CAN_BE_USED_ONLY_WHEN_AGATHION_IS_SUMMONED);
                    return false;
                }
                break;
            case TRAP:
            case EVENT_TRAP:
                if(player.isInZonePeace()) 
				{
                    activeChar.sendPacket(Msg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
                    return false;
                }
				for(L2NpcInstance npc : L2World.getAroundNpc(player, 150, 50))
					if(npc != null && npc.getNpcId() == getNpcId())
					{
						player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
						return false;
					}
                break;
            case PET:
                if (player.getPet() != null || player.isMounted()) 
				{
                    player.sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
                    return false;
                }
                break;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(L2Character caster, GArray<L2Character> targets) 
	{
        L2Player activeChar = caster.getPlayer();

        if (_summonType != SummonType.CUBIC && activeChar == null)
		{
            System.out.println("Non player character has summon skill!!! skill id: " + getId());
            return;
        }

        if (getNpcId() == 0 && _summonType != SummonType.AGATHION) 
		{
            caster.sendMessage("Summon skill " + getId() + " not described yet");
            return;
        }

        switch (_summonType) 
		{
            case AGATHION:
                activeChar.setAgathion(getNpcId());
                break;
            case CUBIC:
                for (L2Character targ : targets)
                    if (targ != null) 
					{
                        if (!targ.isPlayer())
                            continue;
                        L2Player target = (L2Player) targ;

                        int mastery = target.getSkillLevel(L2Skill.SKILL_CUBIC_MASTERY);
                        if (mastery < 0)
                            mastery = 0;

                        L2Cubic cubic = CubicManager.getInstance().getCubic(getNpcId(), npcLevel);
                        if(cubic == null) 
						{
                            target.sendPacket(Msg.CUBIC_SUMMONING_FAILED);
                            continue;
                        }
                        if(target.getCubics().size() > mastery && target.getCubic(cubic.getSlot()) == null) 
						{
                            L2Cubic removedCubic = target.getCubics().removeFirst();
                            if(removedCubic != null)
                                removedCubic.deleteMe();
                        }
                        cubic.setIsMyCubic(activeChar != null && activeChar.getObjectId() == target.getObjectId());
                        target.addCubic(cubic);
                        cubic.initialize(target);
                        getEffects(caster, target, getActivateRate() > 0, false);
                    }
                break;
            case TRAP:
                L2Skill trapSkill = getFirstAddedSkill();
                if(trapSkill == null) 
				{
                    System.out.println("Not implemented trap skill, id = " + getId());
                    return;
                }
                activeChar.addTrap(new L2TrapInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(getNpcId()), activeChar, trapSkill, activeChar.getLoc(), false));
				activeChar.startAttackStanceTask();
                break;
            case EVENT_TRAP:
                new L2EventTrapInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(getNpcId()), activeChar, activeChar.getLoc());
                break;
            case PET:
                // Удаление трупа, если идет суммон из трупа.
                Location loc = null;
                if (_targetType == SkillTargetType.TARGET_CORPSE)
                    for (L2Character target : targets)
                        if (target != null && target.isDead() && target.isNpc()) 
						{
                            activeChar.getAI().setAttackTarget(null);
                            loc = target.getLoc();
                            target.endDecayTask();
                        }

                if (activeChar.getPet() != null || activeChar.isMounted())
                    return;

                L2NpcTemplate summonTemplate = NpcTable.getTemplate(getNpcId());

                if (summonTemplate == null) 
				{
                    System.out.println("Null summon template for skill " + this);
                    return;
                }

                L2SummonInstance summon = new L2SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, activeChar, _lifeTime, _itemConsumeIdInTime, _itemConsumeCountInTime, _itemConsumeDelay);

                summon.setTitle(activeChar.getName());
                summon.setExpPenalty(_expPenalty);
                summon.setExp(Experience.LEVEL[Math.min(summon.getLevel(), Experience.getMaxLevel() + 1)]);
                summon.setCurrentHp(summon.getMaxHp(), false);
                summon.setCurrentMp(summon.getMaxMp());
                summon.setHeading(activeChar.getHeading());
                summon.setRunning();

                activeChar.setPet(summon);

                summon.spawnMe(loc == null ? GeoEngine.findPointToStayPet(activeChar, 100, 150, activeChar.getReflection().getGeoIndex()) : loc);

                if (summon.getSkillLevel(4140) > 0)
                    summon.altUseSkill(SkillTable.getInstance().getInfo(4140, summon.getSkillLevel(4140)), activeChar);

                if (summon.getName().equalsIgnoreCase("Shadow"))
                    summon.addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 0x40, this, 15));

                summon.setFollowStatus(true, true);
                break;
            case DECOY:
                L2NpcTemplate DecoyTemplate = NpcTable.getTemplate(getNpcId());
                L2DecoyInstance decoy = new L2DecoyInstance(IdFactory.getInstance().getNextId(), DecoyTemplate, activeChar, _lifeTime);

                decoy.setCurrentHp(decoy.getMaxHp(), false);
                decoy.setCurrentMp(decoy.getMaxMp());
                decoy.setHeading(activeChar.getHeading());
                decoy.setReflection(activeChar.getReflection());

                activeChar.setDecoy(decoy);

                decoy.spawnMe(activeChar.getLoc());
                break;
            case MERCHANT:
                if (activeChar.getPet() != null || activeChar.isMounted())
                    return;

                L2NpcTemplate merchantTemplate = NpcTable.getTemplate(getNpcId());
                L2MerchantInstance merchant = new L2MerchantInstance(IdFactory.getInstance().getNextId(), merchantTemplate);

                merchant.setCurrentHp(merchant.getMaxHp(), false);
                merchant.setCurrentMp(merchant.getMaxMp());
                merchant.setHeading(activeChar.getHeading());
                merchant.setReflection(activeChar.getReflection());
                merchant.spawnMe(activeChar.getLoc());

                ThreadPoolManager.getInstance().schedule(new DeleteMerchantTask(merchant), _lifeTime, true);
                break;
        }

        if (isSSPossible())
            caster.unChargeShots(isMagic());
    }

    public class DeleteMerchantTask extends l2open.common.RunnableImpl 
	{
        L2MerchantInstance _merchant;

        public DeleteMerchantTask(L2MerchantInstance merchant) 
		{
            _merchant = merchant;
        }

        public void runImpl() 
		{
            if (_merchant != null)
                _merchant.deleteMe();
        }
    }

    @Override
    public boolean isOffensive()
	{
        return _targetType == SkillTargetType.TARGET_CORPSE;
    }
}