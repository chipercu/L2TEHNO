package quests._350_EnhanceYourWeapon;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Rnd;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class _350_EnhanceYourWeapon extends Quest implements ScriptFile
{
	private static final FastMap<Integer, SoulCrystal> _soulCrystals = new FastMap<Integer, SoulCrystal>();
	// <npcid, <level, LevelingInfo>>
	private static final FastMap<Integer, FastMap<Integer, LevelingInfo>> _npcLevelingInfos = new FastMap<Integer, FastMap<Integer, LevelingInfo>>();
	protected static Logger _log = Logger.getLogger(_350_EnhanceYourWeapon.class.getName());

	private static enum AbsorbCrystalType
	{
		LAST_HIT,
		FULL_PARTY,
		PARTY_ONE_RANDOM
	}

	private static final class SoulCrystal
	{
		private final int _level;
		private final int _itemId;
		private final int _leveledItemId;

		public SoulCrystal(int level, int itemId, int leveledItemId)
		{
			_level = level;
			_itemId = itemId;
			_leveledItemId = leveledItemId;
		}

		public final int getLevel()
		{
			return _level;
		}

		public final int getItemId()
		{
			return _itemId;
		}

		public final int getLeveledItemId()
		{
			return _leveledItemId;
		}
	}

	private static final class LevelingInfo
	{
		private final AbsorbCrystalType _absorbCrystalType;
		private final boolean _isSkillNeeded;
		private final int _chance;

		public LevelingInfo(AbsorbCrystalType absorbCrystalType, boolean isSkillNeeded, int chance)
		{
			_absorbCrystalType = absorbCrystalType;
			_isSkillNeeded = isSkillNeeded;
			_chance = chance;
		}

		public final AbsorbCrystalType getAbsorbCrystalType()
		{
			return _absorbCrystalType;
		}

		public final boolean isSkillNeeded()
		{
			return _isSkillNeeded;
		}

		public final int getChance()
		{
			return _chance;
		}
	}

	private static void load()
	{
		if(_npcLevelingInfos.size() > 0)
			return;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(ConfigValue.DatapackRoot, "data/xml/levelUpCrystalData.xml");
			if (!file.exists())
			{
				_log.severe("[EnhanceYourWeapon] Missing levelUpCrystalData.xml. The quest wont work without it!");
				return;
			}

			Document doc = factory.newDocumentBuilder().parse(file);
			Node first = doc.getFirstChild();
			if ((first != null) && "list".equalsIgnoreCase(first.getNodeName()))
			{
				for (Node n = first.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if ("crystal".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("item".equalsIgnoreCase(d.getNodeName()))
							{
								NamedNodeMap attrs = d.getAttributes();
								Node att = attrs.getNamedItem("itemId");
								if (att == null)
								{
									_log.severe("[EnhanceYourWeapon] Missing itemId in Crystal List, skipping");
									continue;
								}
								int itemId = Integer.parseInt(attrs.getNamedItem("itemId").getNodeValue());

								att = attrs.getNamedItem("level");
								if (att == null)
								{
									_log.severe("[EnhanceYourWeapon] Missing level in Crystal List itemId: " + itemId + ", skipping");
									continue;
								}
								int level = Integer.parseInt(attrs.getNamedItem("level").getNodeValue());

								att = attrs.getNamedItem("leveledItemId");
								if (att == null)
								{
									_log.severe("[EnhanceYourWeapon] Missing leveledItemId in Crystal List itemId: " + itemId + ", skipping");
									continue;
								}
								int leveledItemId = Integer.parseInt(attrs.getNamedItem("leveledItemId").getNodeValue());

								_soulCrystals.put(itemId, new SoulCrystal(level, itemId, leveledItemId));
							}
						}
					}
					else if ("npc".equalsIgnoreCase(n.getNodeName()))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if ("item".equalsIgnoreCase(d.getNodeName()))
							{
								NamedNodeMap attrs = d.getAttributes();
								Node att = attrs.getNamedItem("npcId");
								if (att == null)
								{
									_log.severe("[EnhanceYourWeapon] Missing npcId in NPC List, skipping");
									continue;
								}
								int npcId = Integer.parseInt(att.getNodeValue());

								FastMap<Integer, LevelingInfo> temp = new FastMap<Integer, LevelingInfo>();

								for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
								{
									boolean isSkillNeeded = false;
									int chance = 5;
									AbsorbCrystalType absorbType = AbsorbCrystalType.LAST_HIT;

									if ("detail".equalsIgnoreCase(cd.getNodeName()))
									{
										attrs = cd.getAttributes();

										att = attrs.getNamedItem("absorbType");
										if (att != null)
										{
											absorbType = Enum.valueOf(AbsorbCrystalType.class, att.getNodeValue());
										}

										att = attrs.getNamedItem("chance");
										if (att != null)
										{
											chance = Integer.parseInt(att.getNodeValue());
										}

										att = attrs.getNamedItem("skill");
										if (att != null)
										{
											isSkillNeeded = Boolean.parseBoolean(att.getNodeValue());
										}

										Node att1 = attrs.getNamedItem("maxLevel");
										Node att2 = attrs.getNamedItem("levelList");
										if ((att1 == null) && (att2 == null))
										{
											_log.severe("[EnhanceYourWeapon] Missing maxlevel/levelList in NPC List npcId: " + npcId + ", skipping");
											continue;
										}
										LevelingInfo info = new LevelingInfo(absorbType, isSkillNeeded, chance);
										if (att1 != null)
										{
											int maxLevel = Integer.parseInt(att1.getNodeValue());
											for (int i = 0; i <= maxLevel; i++)
											{
												temp.put(i, info);
											}
										}
										else
										{
											StringTokenizer st = new StringTokenizer(att2.getNodeValue(), ",");
											int tokenCount = st.countTokens();
											for (int i = 0; i < tokenCount; i++)
											{
												Integer value = Integer.decode(st.nextToken().trim());
												if (value == null)
												{
													_log.severe("[EnhanceYourWeapon] Bad Level value!! npcId: " + npcId + " token: " + i);
													value = 0;
												}
												temp.put(value, info);
											}
										}
									}
								}

								if (temp.isEmpty())
								{
									_log.severe("[EnhanceYourWeapon] No leveling info for npcId: " + npcId + ", skipping");
									continue;
								}
								_npcLevelingInfos.put(npcId, temp);
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "[EnhanceYourWeapon] Could not parse levelUpCrystalData.xml file: " + e.getMessage(), e);
		}
		_log.info("[EnhanceYourWeapon] Loaded " + _soulCrystals.size() + " Soul Crystal data.");
		_log.info("[EnhanceYourWeapon] Loaded " + _npcLevelingInfos.size() + " npc Leveling info data.");
	}

	private static final int RED_SOUL_CRYSTAL0_ID = 4629;
	private static final int GREEN_SOUL_CRYSTAL0_ID = 4640;
	private static final int BLUE_SOUL_CRYSTAL0_ID = 4651;

	private static final int Jurek = 30115;
	private static final int Gideon = 30194;
	private static final int Winonin = 30856;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _350_EnhanceYourWeapon()
	{
		super(false);
		addStartNpc(Jurek);
		addStartNpc(Gideon);
		addStartNpc(Winonin);

		load();

		for(int npcId : _npcLevelingInfos.keySet())
		{
			addSkillUseId(npcId);
			addKillId(npcId);
		}
	}

	@Override
	public String onSkillUse(L2NpcInstance npc, L2Skill skill, QuestState qs)
	{
		super.onSkillUse(npc,skill,qs);
		if(skill == null || skill.getId() != 2096)
			return null;
		else if(qs.getPlayer() == null || qs.getPlayer().isDead())
			return null;
		if(!(npc instanceof L2MonsterInstance) || npc.isDead() || !_npcLevelingInfos.containsKey(npc.getNpcId()))
			return null;

		try
		{
			((L2MonsterInstance) npc).addAbsorber(qs.getPlayer());
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "", e);
		}
		return null;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if((npc instanceof L2MonsterInstance) && _npcLevelingInfos.containsKey(npc.getNpcId()))
		{
			levelSoulCrystals((L2MonsterInstance) npc, st.getPlayer());
		}
		return null;
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		if(event.equalsIgnoreCase(Jurek + "-04.htm") || event.equalsIgnoreCase(Gideon + "-04.htm") || event.equalsIgnoreCase(Winonin + "-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase(Jurek + "-09.htm") || event.equalsIgnoreCase(Gideon + "-09.htm") || event.equalsIgnoreCase(Winonin + "-09.htm"))
			st.giveItems(RED_SOUL_CRYSTAL0_ID, 1);
		if(event.equalsIgnoreCase(Jurek + "-10.htm") || event.equalsIgnoreCase(Gideon + "-10.htm") || event.equalsIgnoreCase(Winonin + "-10.htm"))
			st.giveItems(GREEN_SOUL_CRYSTAL0_ID, 1);
		if(event.equalsIgnoreCase(Jurek + "-11.htm") || event.equalsIgnoreCase(Gideon + "-11.htm") || event.equalsIgnoreCase(Winonin + "-11.htm"))
			st.giveItems(BLUE_SOUL_CRYSTAL0_ID, 1);
		if(event.equalsIgnoreCase("exit.htm"))
			st.exitCurrentQuest(true);
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String npcId = str(npc.getNpcId());
		String htmltext = "noquest";
		int id = st.getState();
		if(st.getQuestItemsCount(RED_SOUL_CRYSTAL0_ID) == 0 && st.getQuestItemsCount(GREEN_SOUL_CRYSTAL0_ID) == 0 && st.getQuestItemsCount(BLUE_SOUL_CRYSTAL0_ID) == 0)
			if(id == CREATED)
				htmltext = npcId + "-01.htm";
			else
				htmltext = npcId + "-21.htm";
		else
		{
			if(id == CREATED)
			{
				st.setCond(1);
				st.setState(STARTED);
			}
			htmltext = npcId + "-03.htm";
		}
		return htmltext;
	}

	/**
	 * Calculate the leveling chance of Soul Crystals based on the attacker that killed this L2Attackable
	 * @param mob
	 * @param killer The player that last killed this L2Attackable $ Rewrite 06.12.06 - Yesod $ Rewrite 08.01.10 - Gigiikun
	 */
	public void levelSoulCrystals(L2MonsterInstance mob, L2Player killer)
	{
		// Only L2PcInstance can absorb a soul
		if(killer == null)
		{
			mob.resetAbsorbList();
			return;
		}

		FastMap<L2Player, SoulCrystal> players = FastMap.newInstance();
		int maxSCLevel = 0;

		// TODO: what if mob support last_hit + party?
		if(isPartyLevelingMonster(mob.getNpcId()) && (killer.getParty() != null))
		{
			// firts get the list of players who has one Soul Cry and the quest
			for(L2Player pl : killer.getParty().getPartyMembers())
			{
				if(pl == null)
					continue;

				SoulCrystal sc = getSCForPlayer(pl);
				if(sc == null)
					continue;

				players.put(pl, sc);
				if(maxSCLevel < sc.getLevel() && _npcLevelingInfos.get(mob.getNpcId()).containsKey(sc.getLevel()))
					maxSCLevel = sc.getLevel();
			}
		}
		else
		{
			SoulCrystal sc = getSCForPlayer(killer);
			if(sc != null)
			{
				players.put(killer, sc);
				if(maxSCLevel < sc.getLevel() && _npcLevelingInfos.get(mob.getNpcId()).containsKey(sc.getLevel()))
					maxSCLevel = sc.getLevel();
			}
		}
		// Init some useful vars
		LevelingInfo mainlvlInfo = _npcLevelingInfos.get(mob.getNpcId()).get(maxSCLevel);

		if(mainlvlInfo == null)
		{
			/* throw new NullPointerException("Target: "+mob+ " player: "+killer+" level: "+maxSCLevel); */
			return;
		}

		// If this mob is not require skill, then skip some checkings
		if(mainlvlInfo.isSkillNeeded())
		{
			// Fail if this L2Attackable isn't absorbed or there's no one in its _absorbersList
			if(!mob.isAbsorbed() /* || _absorbersList == null */)
			{
				mob.resetAbsorbList();
				return;
			}

			// Fail if the killer isn't in the _absorbersList of this L2Attackable and mob is not boss
			L2MonsterInstance.AbsorberInfo ai = mob.getAbsorbersList().get(killer.getObjectId());
			boolean isSuccess = true;
			if(ai == null || ai._objId != killer.getObjectId())
				isSuccess = false;

			// Check if the soul crystal was used when HP of this L2Attackable wasn't higher than half of it
			if(ai != null && ai._absorbedHP > (mob.getMaxHp() / 2.0))
				isSuccess = false;

			if(!isSuccess)
			{
				mob.resetAbsorbList();
				return;
			}
		}

		switch(mainlvlInfo.getAbsorbCrystalType())
		{
			case PARTY_ONE_RANDOM:
				// This is a naive method for selecting a random member. It gets any random party member and
				// then checks if the member has a valid crystal. It does not select the random party member
				// among those who have crystals, only. However, this might actually be correct (same as retail).
				if(killer.getParty() != null)
				{
					L2Player lucky = killer.getParty().getPartyMembers().get(getRandom(killer.getParty().getMemberCount()));
					tryToLevelCrystal(lucky, players.get(lucky), mob);
				}
				else
					tryToLevelCrystal(killer, players.get(killer), mob);
				break;
			case FULL_PARTY:
				if(killer.getParty() != null)
					for(L2Player pl : killer.getParty().getPartyMembers())
						tryToLevelCrystal(pl, players.get(pl), mob);
				else
					tryToLevelCrystal(killer, players.get(killer), mob);
				break;
			case LAST_HIT:
				tryToLevelCrystal(killer, players.get(killer), mob);
				break;
		}
		FastMap.recycle(players);
	}

	private boolean isPartyLevelingMonster(int npcId)
	{
		for(LevelingInfo li : _npcLevelingInfos.get(npcId).values())
			if(li.getAbsorbCrystalType() != AbsorbCrystalType.LAST_HIT)
				return true;
		return false;
	}

	private SoulCrystal getSCForPlayer(L2Player player)
	{
		QuestState st = player.getQuestState(getName());
		if ((st == null) || (st.getState() != STARTED))
		{
			return null;
		}

		L2ItemInstance[] inv = player.getInventory().getItems();
		SoulCrystal ret = null;
		for (L2ItemInstance item : inv)
		{
			int itemId = item.getItemId();
			if (!_soulCrystals.containsKey(itemId))
			{
				continue;
			}

			if(OwnItemCount(player,4651) + OwnItemCount(player,4652) + OwnItemCount(player,4653) + OwnItemCount(player,4654) + OwnItemCount(player,4655) + OwnItemCount(player,4656) + OwnItemCount(player,4657) + OwnItemCount(player,4658) + OwnItemCount(player,4659) + OwnItemCount(player,4660) + OwnItemCount(player,4661) + OwnItemCount(player,5579) + OwnItemCount(player,5582) + OwnItemCount(player,5914) + OwnItemCount(player,4629) + OwnItemCount(player,4630) + OwnItemCount(player,4631) + OwnItemCount(player,4632) + OwnItemCount(player,4633) + OwnItemCount(player,4634) + OwnItemCount(player,4635) + OwnItemCount(player,4636) + OwnItemCount(player,4637) + OwnItemCount(player,4638) + OwnItemCount(player,4639) + OwnItemCount(player,5577) + OwnItemCount(player,5580) + OwnItemCount(player,5908) + OwnItemCount(player,4640) + OwnItemCount(player,4641) + OwnItemCount(player,4642) + OwnItemCount(player,4643) + OwnItemCount(player,4644) + OwnItemCount(player,4645) + OwnItemCount(player,4646) + OwnItemCount(player,4647) + OwnItemCount(player,4648) + OwnItemCount(player,4649) + OwnItemCount(player,4650) + OwnItemCount(player,5578) + OwnItemCount(player,5581) + OwnItemCount(player,5911) + OwnItemCount(player,9571) + OwnItemCount(player,10161) + OwnItemCount(player,9570) + OwnItemCount(player,10160) + OwnItemCount(player,9572) + OwnItemCount(player,10162) + OwnItemCount(player,10482) + OwnItemCount(player,10481) + OwnItemCount(player,10480) + OwnItemCount(player,13072) + OwnItemCount(player,13073) + OwnItemCount(player,13071) + OwnItemCount(player,15542) + OwnItemCount(player,15543) + OwnItemCount(player,15541) == 1)
				ret = _soulCrystals.get(itemId);
		}
		return ret;
	}

	private void tryToLevelCrystal(L2Player player, SoulCrystal sc, L2MonsterInstance mob)
	{
		if ((sc == null) || !_npcLevelingInfos.containsKey(mob.getNpcId()))
		{
			return;
		}

		// If the crystal level is way too high for this mob, say that we can't increase it
		if (!_npcLevelingInfos.get(mob.getNpcId()).containsKey(sc.getLevel()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_A_SOUL));
			return;
		}

		if (getRandom(100) <= _npcLevelingInfos.get(mob.getNpcId()).get(sc.getLevel()).getChance())
		{
			exchangeCrystal(player, mob, sc.getItemId(), sc.getLeveledItemId(), false);
		}
		else
		{
			player.sendPacket(new SystemMessage(SystemMessage.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_A_SOUL));
		}
	}

	private void exchangeCrystal(L2Player player, L2MonsterInstance mob, int takeid, int giveid, boolean broke)
	{
		L2ItemInstance Item;
		if(player.getInventory().getCountOf(takeid) > 0)
		{
			Item = player.getInventory().destroyItemByItemId(takeid, 1, true);
		}
		else
		{
			return;
		}

		if (Item != null)
		{
			// Prepare inventory update packet
			InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addRemovedItem(Item);

			// Add new crystal to the killer's inventory
			Item = player.getInventory().addItem(giveid, 1);
			playerIU.addItem(Item);

			// Send a sound event and text message to the player
			if (broke)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THE_SOUL_CRYSTAL_BROKE_BECAUSE_IT_WAS_NOT_ABLE_TO_ENDURE_THE_SOUL_ENERGY));
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage.THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL));
			}

			// Send system message
			SystemMessage sms = new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1);
			sms.addItemName(giveid);
			player.sendPacket(sms);

			// Send inventory update packet
			player.sendPacket(playerIU);
		}
	}

	public static int getRandom(int max)
	{
		return Rnd.get(max);
	}

	public static long OwnItemCount(L2Player player, int itemId)
	{
		return player.getInventory().getCountOf(itemId);
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(ConfigValue.AutoSetQuest350)
		{
			Quest q = QuestManager.getQuest("_350_EnhanceYourWeapon");
			QuestState qs = player.getQuestState(q.getClass());
			if(qs == null)
			{
				q.newQuestState(player, Quest.STARTED);

				qs = player.getQuestState(q.getClass());
				qs.setCond(1);
			}
		}
	}
}