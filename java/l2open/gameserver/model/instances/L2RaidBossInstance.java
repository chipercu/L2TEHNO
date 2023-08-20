package l2open.gameserver.model.instances;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.instancemanager.RaidBossSpawnManager.Status;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.HeroDiary;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L2RaidBossInstance extends L2MonsterInstance
{
	protected static Logger _log = Logger.getLogger(L2RaidBossInstance.class.getName());

	private ScheduledFuture<?> minionMaintainTask;

	private static final int RAIDBOSS_MAINTENANCE_INTERVAL = 60000;
	private static final int MINION_UNSPAWN_INTERVAL = 5000; //time to unspawn minions when boss is dead, msec

	private RaidBossSpawnManager.Status _raidStatus;
	private long attack_time = 0;
	private ScheduledFuture<?> _CCChecker;
	private ScheduledFuture<?> _CCChecker1;

	public L2RaidBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected int getMaintenanceInterval()
	{
		return RAIDBOSS_MAINTENANCE_INTERVAL;
	}

	protected int getMinionUnspawnInterval()
	{
		return MINION_UNSPAWN_INTERVAL;
	}

	protected int getKilledInterval(L2MinionInstance minion)
	{
		//Уебищная затычка для респа миньонов АК...
		if(minion.getNpcId() == 29003)
			return 10000;
		else if (minion.getNpcId() == 29005)
			return (280 + Rnd.get(40)) * 1000;
		else
			return ConfigValue.RespawnTimeRaidBossMinion; //6 minutes to respawn
	}

	@Override
	public void notifyMinionDied(L2MinionInstance minion)
	{
		minionMaintainTask = ThreadPoolManager.getInstance().schedule(new maintainKilledMinion(minion.getNpcId()), getKilledInterval(minion), false);
		super.notifyMinionDied(minion);
	}

	private class maintainKilledMinion extends l2open.common.RunnableImpl
	{
		private int _minion;

		public maintainKilledMinion(int minion)
		{
			_minion = minion;
		}

		public void runImpl()
		{
			try
			{
				if(!L2RaidBossInstance.this.isDead())
				{
					MinionList list = L2RaidBossInstance.this.getMinionList();
					if(list != null)
						list.spawnSingleMinionSync(_minion);
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	static class DamagerInfo
	{
		double damage;
		GArray<String> skills = new GArray<String>();

		public DamagerInfo(double _damage)
		{
			damage = _damage;
		}

		public DamagerInfo()
		{
			this(0);
		}

		@Override
		public String toString()
		{
			String result = String.valueOf((int) damage);
			if(skills.size() > 0)
			{
				result += " | Skills: " + skills.removeFirst();
				for(String skill : skills)
					result += ", " + skill;
			}
			return result;
		}

		public String toTime()
		{
			return Util.formatTime((int) ((System.currentTimeMillis() - damage) / 1000));
		}
	}

	private final FastMap<String, DamagerInfo> lastDamagers = new FastMap<String, DamagerInfo>().setShared(true);

	@Override
	public void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		// ...
		if(attacker == null || attacker.getPlayer() == null || (attacker == this || ConfigValue.EnableRbBlockOverDamage && i > getMaxHp() / 10) && !attacker.getPlayer().isGM())
			return;
		String attackerName = attacker.getPlayer().getName();
		DamagerInfo di;
		synchronized (lastDamagers)
		{
			di = lastDamagers.get(attackerName);
			if(di == null)
			{
				di = new DamagerInfo();
				lastDamagers.put(attackerName, di);
			}
			di.damage += i;
			if(skill != null && !di.skills.contains(skill.getName()))
				di.skills.add(skill.getName());
			if(!lastDamagers.containsKey("@"))
				lastDamagers.put("@", new DamagerInfo(System.currentTimeMillis()));
		}
		if(attacker.getParty() != null && attacker.getParty().getCommandChannel() != null)
		{
			if(getMasterPartyRouting() == 1)
				attack_time = System.currentTimeMillis();

			if(getNpcId() == 29001) //Ant Queen
			{
				if((attacker.getParty().getCommandChannel().getMemberCount() >= ConfigValue.MinChannelMembersAntQueen || attacker.getPlayer().isGM()) && getMasterPartyRouting() == 0)
				{
					MPCC_SetMasterPartyRouting(attacker.getParty().getCommandChannel(),1);
					BroadcastOnScreenMsgStr(this,4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800001,attacker.getParty().getCommandChannel().getChannelLeader().getName());
					_CCChecker = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CCChecker(this), 61 * 1000, 61 * 1000);
				}
				else if(getMasterPartyRouting() == 0)
					setMasterPartyRouting(1);
			}
			else if(getNpcId() == 29006) //Core
			{
				if((attacker.getParty().getCommandChannel().getMemberCount() >= ConfigValue.MinChannelMembersCore || attacker.getPlayer().isGM()) && getMasterPartyRouting() == 0)
				{
					MPCC_SetMasterPartyRouting(attacker.getParty().getCommandChannel(),1);
					BroadcastOnScreenMsgStr(this,4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800002,attacker.getParty().getCommandChannel().getChannelLeader().getName());
					_CCChecker = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CCChecker(this), 61 * 1000, 61 * 1000);
				}
				else if(getMasterPartyRouting() == 0)
					setMasterPartyRouting(1);
			}
			else if(getNpcId() == 29014) //Orfen
			{
				if((attacker.getParty().getCommandChannel().getMemberCount() >= ConfigValue.MinChannelMembersOrfen || attacker.getPlayer().isGM()) && getMasterPartyRouting() == 0)
				{
					MPCC_SetMasterPartyRouting(attacker.getParty().getCommandChannel(),1);
					BroadcastOnScreenMsgStr(this,4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800003,attacker.getParty().getCommandChannel().getChannelLeader().getName());
					_CCChecker = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CCChecker(this), 61 * 1000, 61 * 1000);
				}
				else if(getMasterPartyRouting() == 0)
					setMasterPartyRouting(1);
			}
			else if(getNpcId() == 29022 || getNpcId() == 29176 || getNpcId() == 29181) //Zaken
			{
				if((attacker.getParty().getCommandChannel().getMemberCount() >= ConfigValue.MinChannelMembersZaken || attacker.getPlayer().isGM()) && getMasterPartyRouting() == 0)
				{
					MPCC_SetMasterPartyRouting(attacker.getParty().getCommandChannel(),1);
					BroadcastOnScreenMsgStr(this,4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800004,attacker.getParty().getCommandChannel().getChannelLeader().getName());
					_CCChecker = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CCChecker(this), 61 * 1000, 61 * 1000);
				}
				else if(getMasterPartyRouting() == 0)
					setMasterPartyRouting(1);
			}
			else
			{
				if(getNpcId() != 29068 && getNpcId() != 29028 && getNpcId() != 29047 && getNpcId() != 29020)
				{
					if((attacker.getParty().getCommandChannel().getMemberCount() >= ConfigValue.MinChannelMembersOtherRaidBoss || attacker.getPlayer().isGM()) && getMasterPartyRouting() == 0)
					{
						MPCC_SetMasterPartyRouting(attacker.getParty().getCommandChannel(),1);
						BroadcastOnScreenMsgStr(this,4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800009,attacker.getParty().getCommandChannel().getChannelLeader().getName());
						_CCChecker = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CCChecker(this), 61 * 1000, 61 * 1000);
					}
					else if(getMasterPartyRouting() == 0)
						setMasterPartyRouting(1);
				}
			}
		}
		else
		{
			if(getNpcId() != 29068 && getNpcId() != 29028 && getNpcId() != 29047 && getNpcId() != 29020)
			{
				if(getMasterPartyRouting() == 1)
					attack_time = System.currentTimeMillis();
				if(getMasterPartyRouting() == 0)
				{
					setMasterPartyRouting(1);
					_CCChecker1 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CCChecker1(), 61 * 1000, 61 * 1000);
				}
			}
		}
		if(attacker.getPlayer() != null && attacker.getPlayer().getPvpFlag() <= 0 && ConfigValue.RaidToPvpFlag.length > 0 && Util.contains(ConfigValue.RaidToPvpFlag, getNpcId()))
			attacker.getPlayer().startPvPFlag(null);
		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
	}

	public class CCChecker1 extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if((System.currentTimeMillis() > (attack_time + 60 * 5 * 1000)) && getMasterPartyRouting() == 1)
			{
				attack_time = 0;
				if(_CCChecker1 != null)
				{
					_CCChecker1.cancel(false);
					_CCChecker1 = null;
				}
				setMasterPartyRouting(0);
			}
		}
	}

	public class CCChecker extends l2open.common.RunnableImpl
	{
		private final L2NpcInstance npc;

		public CCChecker(L2NpcInstance _npc)
		{
			npc = _npc;
		}
		public void runImpl()
		{
			if((System.currentTimeMillis() > (attack_time + 60 * 5 * 1000)) && getMasterPartyRouting() == 1)
			{
				if(npc.getNpcId() == 29001)
					BroadcastOnScreenMsgStr(npc,4000,ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800005, "");
				else if(npc.getNpcId() == 29006)
					BroadcastOnScreenMsgStr(npc,4000,ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800006, "");
				else if(npc.getNpcId() == 29014)
					BroadcastOnScreenMsgStr(npc,4000,ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800007, "");
				else if(npc.getNpcId() == 29022 || npc.getNpcId() == 29176 || npc.getNpcId() == 29181)
					BroadcastOnScreenMsgStr(npc,4000,ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800008, "");
				else
					BroadcastOnScreenMsgStr(npc,4000,ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,4000,true,true,1800010, "");
				attack_time = 0;
				if(_CCChecker != null)
				{
					_CCChecker.cancel(false);
					_CCChecker = null;
				}
				MPCC_SetMasterPartyRouting(null, 0);
			}
		}
	}

	public void BroadcastOnScreenMsgStr(L2Character actor, int radius, ExShowScreenMessage.ScreenMessageAlign text_align, int time, boolean bigfont, boolean bool, int msgId, String name)
	{
		for(L2Player player : L2World.getAroundPlayers(actor, radius, 500))
			player.sendPacket(new ExShowScreenMessage(msgId, time, text_align, true, 1, bool, name));
	}

	/*@Override
	public void doRegen()
	{
		super.doRegen();
		if(isInCombat() || !isCurrentHpFull() || lastDamagers.isEmpty())
			return;
		lastDamagers.clear();
	}*/

	@Override
	public void doDie(L2Character killer)
	{
		if(minionMaintainTask != null)
		{
			minionMaintainTask.cancel(false);
			minionMaintainTask = null;
		}

		int points = RaidBossSpawnManager.getInstance().getPoinstForRaid(getNpcId());
		if(points > 0)
			calcRaidPointsReward(points);

		synchronized (lastDamagers)
		{
			String killTime = lastDamagers.containsKey("@") ? lastDamagers.remove("@").toTime() : "-";
			Log.add(PrintfFormat.LOG_BOSS_KILLED, new Object[] { getTypeName(), getName(), getNpcId(), killer, getX(), getY(), getZ(), killTime }, "bosses");
			for(String damagerName : lastDamagers.keySet())
				Log.add("\tDamager [" + damagerName + "] = " + lastDamagers.get(damagerName), "bosses");
			lastDamagers.clear();
		}

		if(isRefRaid() && !isEpicRaid())
		{
			super.doDie(killer);
			return;
		}

		if(killer.isPlayable())
		{
			L2Player player = killer.getPlayer();
			if (player.isInParty())
			{
				for (L2Player member : player.getParty().getPartyMembers())
					if (member.isNoble())
						Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
				player.getParty().broadcastToPartyMembers(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
			}
			else
			{
				if (player.isNoble())
					Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
				player.sendPacket(Msg.CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL);
			}

			Quest q = QuestManager.getQuest(508);
			if(q != null)
			{
				String qn = q.getName();
				if(player.getClan() != null && player.getClan().getLeader().isOnline() && player.getClan().getLeader().getPlayer().getQuestState(qn) != null)
				{
					QuestState st = player.getClan().getLeader().getPlayer().getQuestState(qn);
					st.getQuest().onKill(this, st);
				}
			}
		}

		if(isRefRaid())
		{
			super.doDie(killer);
			return;
		}

		unspawnMinions();

		int boxId = 0;
		int box_despawn_time = 60000;
		switch(getNpcId())
		{
			case 25035: // Shilens Messenger Cabrio
				boxId = 31027;
				box_despawn_time = ConfigValue.BoxDespawnTime31027;
				break;
			case 25054: // Demon Kernon
				boxId = 31028;
				box_despawn_time = ConfigValue.BoxDespawnTime31028;
				break;
			case 25126: // Golkonda, the Longhorn General
				boxId = 31029;
				box_despawn_time = ConfigValue.BoxDespawnTime31029;
				break;
			case 25220: // Death Lord Hallate
				boxId = 31030;
				box_despawn_time = ConfigValue.BoxDespawnTime31030;
				break;
		}

		if(boxId != 0)
		{
			L2NpcTemplate boxTemplate = NpcTable.getTemplate(boxId);
			if(boxTemplate != null)
			{
				final L2NpcInstance box = new L2NpcInstance(IdFactory.getInstance().getNextId(), boxTemplate);
				box.onSpawn();
				box.spawnMe(getLoc());
				box.setSpawnedLoc(getLoc());

				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
					public void runImpl()
					{
						box.deleteMe();
					}
				}, box_despawn_time);
			}
		}

		super.doDie(killer);
		setRaidStatus(Status.DEAD);
	}

	@SuppressWarnings("unchecked")
	private void calcRaidPointsReward(int totalPoints)
	{
		// Object groupkey (L2Party/L2CommandChannel/L2Player) | [GArray<L2Player> group, Long GroupDdamage]
		HashMap<Object, Object[]> participants = new HashMap<Object, Object[]>();
		double totalHp = getMaxHp();

		// Разбиваем игроков по группам. По возможности используем наибольшую из доступных групп: Command Channel → Party → StandAlone (сам плюс пет :)
		for(AggroInfo ai : getAggroList())
		{
			L2Player player = ai.attacker.getPlayer();
			Object key = player.getParty() != null ? player.getParty().getCommandChannel() != null ? player.getParty().getCommandChannel() : player.getParty() : player.getPlayer();
			Object[] info = participants.get(key);
			if(info == null)
			{
				info = new Object[] { new HashSet<L2Player>(), new Long(0) };
				participants.put(key, info);
			}

			// если это пати или командный канал то берем оттуда весь список участвующих, даже тех кто не в аггролисте
			// дубликаты не страшны - это хашсет
			if(key instanceof L2CommandChannel)
			{
				for(L2Player p : ((L2CommandChannel) key).getMembers())
					if(p.isInRangeZ(this, ConfigValue.AltPartyDistributionRange))
						((HashSet<L2Player>) info[0]).add(p);
			}
			else if(key instanceof L2Party)
			{
				for(L2Player p : ((L2Party) key).getPartyMembers())
					if(p.isInRangeZ(this, ConfigValue.AltPartyDistributionRange))
						((HashSet<L2Player>) info[0]).add(p);
			}
			else
				((HashSet<L2Player>) info[0]).add(player);

			info[1] = ((Long) info[1]).longValue() + ai.damage;
		}

		for(Object[] groupInfo : participants.values())
		{
			HashSet<L2Player> players = (HashSet<L2Player>) groupInfo[0];
			// это та часть, которую игрок заслужил дамагом группы, но на нее может быть наложен штраф от уровня игрока
			int perPlayer = (int) Math.round(totalPoints * ((Long) groupInfo[1]).longValue() / (totalHp * players.size()));
			for(L2Player player : players)
			{
				if(player.getLevel() >= ConfigValue.MinLevelToAddRaidPoint && player.getLevel() <= ConfigValue.MaxLevelToAddRaidPoint)
				{
					int playerReward = perPlayer;
					// применяем штраф если нужен
					playerReward = (int) Math.round(playerReward * Experience.penaltyModifier(calculateLevelDiffForDrop(player.getLevel(), false), 9));
					if(playerReward == 0)
						continue;
					player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_RAID_POINTS).addNumber(playerReward));
					RaidBossSpawnManager.getInstance().addPoints(player, getNpcId(), playerReward);
				}
			}
		}

		RaidBossSpawnManager.getInstance().updatePointsDb();
		RaidBossSpawnManager.getInstance().calculateRanking();
	}

	@Override
	public void onDecay()
	{
		super.onDecay();
		RaidBossSpawnManager.getInstance().onBossDespawned(this);
	}

	public void unspawnMinions()
	{
		if(hasMinions())
			ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
				public void runImpl()
				{
					try
					{
						removeMinions();
					}
					catch(Throwable e)
					{
						_log.log(Level.SEVERE, "", e);
						e.printStackTrace();
					}
				}
			}, getMinionUnspawnInterval(), false);
	}

	@Override
	public void onSpawn()
	{
		addSkill(SkillTable.getInstance().getInfo(4045, 1)); // Resist Full Magic Attack
		RaidBossSpawnManager.getInstance().onBossSpawned(this);
		super.onSpawn();
	}

	public void setRaidStatus(RaidBossSpawnManager.Status status)
	{
		_raidStatus = status;
	}

	public RaidBossSpawnManager.Status getRaidStatus()
	{
		return _raidStatus;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	@Override
	public boolean hasRandomWalk()
	{
		return false;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isRaid()
	{
		return true;
	}
}