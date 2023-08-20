package npc.model;

import javolution.util.FastMap;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.SoulConsumeTask;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.quest.*;
import l2open.gameserver.serverpackets.SpawnEmitter;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

import java.util.HashMap;

public class I7RBInstance extends L2RaidBossInstance
{
	public I7RBInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	/*@Override
	public void doDie(final L2Character killer)
	{
		HashMap<L2Playable, AggroInfo> aggroList = getAggroMap();

		if(aggroList != null && !aggroList.isEmpty())
		{
			Quest quest = QuestManager.getQuest(246);
			for(L2Playable pl : aggroList.keySet())
				if(pl.isPlayer() && pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(killer, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
				{
					QuestState qs = pl.getPlayer().getQuestState(quest.getName());
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
		}
		super.doDie(killer);
		
	}*/

	@Override
	public void calculateRewards(L2Character lastAttacker)
	{
		HashMap<L2Playable, AggroInfo> aggroList = getAggroMap();
		L2Character topDamager = getTopDamager(aggroList.values());
		if(lastAttacker == null && topDamager != null)
			lastAttacker = topDamager;
		if(lastAttacker == null || aggroList.isEmpty())
			return;
		L2Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		if(topDamager == null)
			topDamager = lastAttacker;

		// Notify the Quest Engine of the L2NpcInstance death if necessary
		try
		{
			if(ConfigValue.KillCounter)
				killer.incrementKillsCounter(getNpcId());
			getTemplate().killscount++;

			Quest[] arrayOfQuest = null;
			if(getTemplate().hasQuestEvents() && ((arrayOfQuest = getTemplate().getEventQuests(QuestEventType.MOBKILLED)) != null))
			{
				L2Player killer2 = getTopToDrop();
				GArray<L2Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
				if(killer2.getParty() != null) // если пати то собираем всех кто подходит
				{
					players = new GArray<L2Player>(killer2.getParty().getMemberCount());
					for(L2Player pl : killer2.getParty().getPartyMembers())
						if(pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(killer2, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
							players.add(pl);
				}

				for(Quest quest : arrayOfQuest)
				{
					L2Player toReward = killer2;
					if(quest.getParty() != Quest.PARTY_NONE && players != null)
						if(isRaid() || isEpicRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
						{
							for(L2Player pl : players)
							{
								QuestState qs = pl.getQuestState(quest.getName());
								if(qs != null && !qs.isCompleted())
									quest.notifyKill(this, qs);
							}
							toReward = null;
						}
						else
						{ // иначе выбираем одного
							GArray<L2Player> interested = new GArray<L2Player>(players.size());
							for(L2Player pl : players)
							{
								QuestState qs = pl.getQuestState(quest.getName());
								if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
									interested.add(pl);
							}

							if(interested.isEmpty())
								continue;

							toReward = interested.get(Rnd.get(interested.size()));
							if(toReward == null)
								toReward = killer2;
						}

					// Уебищная затычка для квестов Путь Лорда...По другому просто нужно пихать в АИ мобов выдачу награды, а мне лень...Мб потом уберу...
					if(toReward != null && quest.getQuestIntId() >= 708 && quest.getQuestIntId() <= 716 && toReward.getClan() != null && toReward.getClan().getLeader().isOnline() && toReward.getClan().getLeader().getPlayer().getQuestState(quest.getName()) != null)
					{
						QuestState qs = toReward.getQuestState(quest.getName());
						if(qs == null)
							QuestManager.getQuest(quest.getQuestIntId()).newQuestState(toReward, 0);
						if(qs != null && !qs.isCompleted())
							quest.notifyKill(this, qs);
					}
					else if(toReward != null)
					{
						QuestState qs = toReward.getQuestState(quest.getName());
						if(qs != null && !qs.isCompleted())
							quest.notifyKill(this, qs);
					}
				}
			}
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}

		// Distribute Exp and SP rewards to L2Player (including Summon owner) that hit the L2NpcInstance and to their Party members
		FastMap<L2Player, RewardInfo> rewards = new FastMap<L2Player, RewardInfo>().setShared(true);
		boolean boss = (isRaid() || isBoss() || isRefRaid()) && !isEpicRaid();

		for(AggroInfo info : aggroList.values())
		{
			if(info.damage <= 1)
				continue;
			L2Character attacker = info.attacker;
			if(attacker == null || !attacker.isPlayer())
				continue;
			L2Player player = attacker.getPlayer();
			if(player != null)
			{
				if((boss || isEpicRaid()) && player.getAttainment() != null && player.isInRange(this, 2000))
					player.getAttainment().setKillRaid(this);
				RewardInfo reward = rewards.get(player);
				if(reward == null)
					rewards.put(player, new RewardInfo(player, info.damage));
				else
					reward.addDamage(info.damage);
			}
		}

		// Сначала дроп, а потом лвлАп, а то расчет дропа идет уже по новому лвлу)
		// Manage Base, Quests and Special Events drops of the L2NpcInstance
		if(lastAttacker.getLevel() > getLevel()- ConfigValue.DropPenaltyDiff)
			doItemDrop(topDamager);

		// Manage Sweep drops of the L2NpcInstance
		if(isSpoiled())
			doSweepDrop(spoiler_ref.get(), topDamager);	

		for(FastMap.Entry<L2Player, RewardInfo> e = rewards.head(), end = rewards.tail(); e != null && (e = e.getNext()) != end && e != null;)
		{
			L2Player attacker = e.getKey();
			RewardInfo reward = e.getValue();
			if(attacker == null || attacker.isDead() || reward == null)
				continue;
			L2Party party = attacker.getParty();
			int maxHp = getMaxHp();
			if(party == null)
			{
				int damage = Math.min(reward._dmg, maxHp);
				if(damage > 0)
				{
					double[] xpsp = calculateExpAndSp(attacker, attacker.getLevel(), damage);
					double neededExp = attacker.calcStat(Stats.SOULS_CONSUME_EXP, 0, this, null); // Начисление душ камаэлянам
					if(neededExp > 0 && xpsp[0] > neededExp)
					{
						broadcastPacket(new SpawnEmitter(this, attacker));
						ThreadPoolManager.getInstance().schedule(new SoulConsumeTask(attacker), 1000);
					}
					xpsp[0] = applyOverhit(killer, xpsp[0]);
					xpsp = attacker.applyVitality(this, xpsp[0], xpsp[1], 1.0);

					if(ConfigValue.RangEnable)
					{
						long point = (long)((xpsp[0]/100)*ConfigValue.RangPercentAddPointMob[attacker.getRangId()]);
						if(point < 1)
							point = 1;
						attacker.addRangPoint(point);
						attacker.sendMessage("Получено "+point+" Очков Воина. Всего "+attacker.getRangPoint()+" Очков Воина.");
					}
					if(attacker.getLevel() > getLevel()- ConfigValue.ExpSpPenaltyDiff)
						attacker.addExpAndSp((long) xpsp[0], (long) xpsp[1], false, true, (long) xpsp[2], (long) xpsp[3], this);
				}
				rewards.remove(attacker);
			}
			else
			{
				int partyDmg = 0;
				int partylevel = 1;
				GArray<L2Player> rewardedMembers = new GArray<L2Player>();
				for(L2Player partyMember : party.getPartyMembers())
				{
					RewardInfo ai = rewards.remove(partyMember);
					if(partyMember.isDead() || !partyMember.isInRange(lastAttacker, ConfigValue.AltPartyDistributionRange))
						continue;
					if(ai != null)
						partyDmg += ai._dmg;
					rewardedMembers.add(partyMember);
					if(partyMember.getLevel() > partylevel)
						partylevel = partyMember.getLevel();
				}
				partyDmg = Math.min(partyDmg, maxHp);
				if(partyDmg > 0)
				{
					double[] xpsp = calculateExpAndSp(attacker, partylevel, partyDmg);
					double partyMul = (double) partyDmg / maxHp;
					xpsp[0] *= partyMul;
					xpsp[1] *= partyMul;
					xpsp[0] = applyOverhit(killer, xpsp[0]);
					party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
				}
			}
		}

		// Check the drop of a cursed weapon
		CursedWeaponsManager.getInstance().dropAttackable(this, killer);

		if(!isRaid()) // С рейдов падают только топовые лайфстоны
		{
			double chancemod = ((L2NpcTemplate) _template).rateHp * Experience.penaltyModifier(calculateLevelDiffForDrop(topDamager.getLevel(), false), 9);

			// Дополнительный дроп материалов
			if(ConfigValue.AltMatherialsDrop && chancemod > 0 && (!isSeeded() || _seeded.isAltSeed()))
				for(L2DropData d : _matdrop)
					if(getLevel() >= d.getMinLevel())
					{
						long count = Util.rollDrop(d.getMinDrop(), d.getMaxDrop(), d.getChance() * chancemod * RateService.getRateDropItems(killer) * killer.getRateItems(), true, killer);
						if(count > 0)
							dropItem(killer, d.getItemId(), count);
					}
		}
	}
}