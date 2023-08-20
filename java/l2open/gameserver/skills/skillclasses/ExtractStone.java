package l2open.gameserver.skills.skillclasses;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.RateService;
import l2open.util.Rnd;

import java.util.StringTokenizer;

public class ExtractStone extends L2Skill
{
	private final static int ExtractScrollSkill = 2630;
	private final static int ExtractedCoarseRedStarStone = 13858;
	private final static int ExtractedCoarseBlueStarStone = 13859;
	private final static int ExtractedCoarseGreenStarStone = 13860;

	private final static int ExtractedRedStarStone = 14009;
	private final static int ExtractedBlueStarStone = 14010;
	private final static int ExtractedGreenStarStone = 14011;

	private final static int RedStarStone1 = 18684;
	private final static int RedStarStone2 = 18685;
	private final static int RedStarStone3 = 18686;

	private final static int BlueStarStone1 = 18687;
	private final static int BlueStarStone2 = 18688;
	private final static int BlueStarStone3 = 18689;

	private final static int GreenStarStone1 = 18690;
	private final static int GreenStarStone2 = 18691;
	private final static int GreenStarStone3 = 18692;

	private final static int FireEnergyCompressionStone = 14015;
	private final static int WaterEnergyCompressionStone = 14016;
	private final static int WindEnergyCompressionStone = 14017;
	private final static int EarthEnergyCompressionStone = 14018;
	private final static int DarknessEnergyCompressionStone = 14019;
	private final static int SacredEnergyCompressionStone = 14020;

	private final static int SeedFire = 18679;
	private final static int SeedWater = 18678;
	private final static int SeedWind = 18680;
	private final static int SeedEarth = 18681;
	private final static int SeedDarkness = 18683;
	private final static int SeedDivinity = 18682;

	private static final int[][] ANNIHILATION_SUPRISE_MOB_IDS = { {22746,22747,22748,22749},
		{22754,22755,22756}, {22760,22761,22762}
	};

	private GArray<Integer> _npcIds = new GArray<Integer>();

	public ExtractStone(StatsSet set)
	{
		super(set);
		StringTokenizer st = new StringTokenizer(set.getString("npcIds", ""), ";");
		while(st.hasMoreTokens())
			_npcIds.add(Integer.valueOf(st.nextToken()));
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(activeChar.getLevel() < 70)
			return false;
		if(target == null || !target.isNpc() || getItemId(target.getNpcId()) == 0 || target.isDead())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		if(!_npcIds.isEmpty() && !_npcIds.contains(new Integer(target.getNpcId())))
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	/**
	 * Возвращает ID предмета получаемого из npcId.
	 * @return
	 */
	private int getItemId(int npcId)
	{
		switch(npcId)
		{
			case RedStarStone1:
			case RedStarStone2:
			case RedStarStone3:
				if(_id == ExtractScrollSkill)
					return ExtractedCoarseRedStarStone;
				return ExtractedRedStarStone;
			case BlueStarStone1:
			case BlueStarStone2:
			case BlueStarStone3:
				if(_id == ExtractScrollSkill)
					return ExtractedCoarseBlueStarStone;
				return ExtractedBlueStarStone;
			case GreenStarStone1:
			case GreenStarStone2:
			case GreenStarStone3:
				if(_id == ExtractScrollSkill)
					return ExtractedCoarseGreenStarStone;
				return ExtractedGreenStarStone;
			case SeedFire:
				return FireEnergyCompressionStone;
			case SeedWater:
				return WaterEnergyCompressionStone;
			case SeedWind:
				return WindEnergyCompressionStone;
			case SeedEarth:
				return EarthEnergyCompressionStone;
			case SeedDarkness:
				return DarknessEnergyCompressionStone;
			case SeedDivinity:
				return SacredEnergyCompressionStone;
			default:
				return 0;
		}
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		L2Player player = activeChar.getPlayer();
		if(player == null)
			return;
        if (player.isFakeDeath())
            return;

		for(L2Character target : targets)
			if(target != null && getItemId(target.getNpcId()) != 0 && !target.isDead())
			{
				double rate = RateService.getRateDropItems(player) * player.getBonus().RATE_DROP_ITEMS*player.getAltBonus();
				long count = _id == ExtractScrollSkill ? 1 : Math.min(10, Rnd.get((int) (getLevel() * rate + 1)));
				int itemId = getItemId(target.getNpcId());

				if(count > 0)
				{
					player.getInventory().addItem(itemId, count);
					player.sendPacket(new PlaySound(Quest.SOUND_ITEMGET));
					player.sendPacket(SystemMessage.obtainItems(itemId, count, 0));
					player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
				}
				else
					player.sendPacket(Msg.THE_COLLECTION_HAS_FAILED);

				switch(target.getAI().LOC_NUMBER)
				{
					case 1:
					{
						if (Rnd.get(100) > 50)
						{
							L2NpcInstance npc = (L2NpcInstance)target;
							L2MonsterInstance mob = spawnSupriseMob(npc, player, ANNIHILATION_SUPRISE_MOB_IDS[0][Rnd.get(ANNIHILATION_SUPRISE_MOB_IDS[0].length)]);
						}
						else
							target.doDie(player);
						break;
					}
					case 2:
					{
						if (Rnd.get(100) > 50)
						{
							L2NpcInstance npc = (L2NpcInstance)target;
							L2MonsterInstance mob = spawnSupriseMob(npc, player, ANNIHILATION_SUPRISE_MOB_IDS[1][Rnd.get(ANNIHILATION_SUPRISE_MOB_IDS[1].length)]);
						}
						else
							target.doDie(player);
						break;
					}
					case 3:
					{
						if (Rnd.get(100) > 50)
						{
							L2NpcInstance npc = (L2NpcInstance)target;
							L2MonsterInstance mob = spawnSupriseMob(npc, player, ANNIHILATION_SUPRISE_MOB_IDS[2][Rnd.get(ANNIHILATION_SUPRISE_MOB_IDS[2].length)]);
						}
						else
							target.doDie(player);
						break;
					}
					case 4:
					{
						target.doDie(player);
						break;
					}
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}

	private L2MonsterInstance spawnSupriseMob(L2NpcInstance energy, L2Player killer, int npcId)
	{
		L2NpcInstance mob;
		L2NpcTemplate supriseMobTemplate = NpcTable.getInstance().getTemplate(npcId);

		L2MonsterInstance monster = new L2MonsterInstance(IdFactory.getInstance().getNextId(), supriseMobTemplate);

		monster.setSpawnedLoc(energy.getLoc());
		monster.setHeading(energy.getHeading());
		monster.onSpawn();
		monster.setCurrentHpMp(monster.getMaxHp(), monster.getMaxMp());
		monster.setShowSpawnAnimation(1);
		monster.spawnMe(energy.getLoc());
		mob = monster;
		mob.setRunning();
		mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 999);
		mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, killer);

		energy.doDie(killer);

		ThreadPoolManager.getInstance().schedule(new despawnTask(monster), 30000);

		return monster;
	}

	public class despawnTask extends l2open.common.RunnableImpl
	{
		L2MonsterInstance _monster = null;

		public despawnTask(L2MonsterInstance monster)
		{
			_monster = monster;
		}

		public void runImpl()
		{
			try
			{
				if(_monster.isInCombat())
					ThreadPoolManager.getInstance().schedule(new despawnTask(_monster), 30000);
				else
					_monster.deleteMe();
			}
			catch(Throwable t)
			{}
		}
	}
}