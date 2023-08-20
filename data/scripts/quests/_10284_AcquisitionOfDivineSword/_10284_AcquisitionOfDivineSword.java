package quests._10284_AcquisitionOfDivineSword;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Util;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Diagod
 * 2011-06-11
 **/
public class _10284_AcquisitionOfDivineSword extends Quest implements ScriptFile
{
	public _10284_AcquisitionOfDivineSword()
	{
		super(false);

		addStartNpc(_rafforty);
		addTalkId(_rafforty);
		addTalkId(_jinia);
		addTalkId(_kroon);
		addTalkId(_taroon);
		addTalkId(KEGOR_IN_CAVE);
		addKillId(KEGOR_IN_CAVE);
		addKillId(MONSTER);
	}

	private static final int _rafforty = 32020;
	private static final int _jinia = 32760;
	private static final int _kroon = 32653;
	private static final int _taroon = 32654;
	
	public class World
	{
		public int instanceId = 0;
		public boolean underAttack = false;
		public L2NpcInstance KEGOR = null;
		public List<L2MonsterInstance> liveMobs = new FastList<L2MonsterInstance>();
		public World()
		{
		}
	}
	public static FastMap<Integer, World> worlds = new FastMap<Integer, World>();
	public static void addWorld(int id, World world)
	{
		worlds.put(id, world);
	}
	public static World getWorld(int id)
	{
		World world = worlds.get(id);
		if(world != null)
			return world;
		return null;
	}
	private static final int KEGOR_IN_CAVE = 18846;
	private static final int MONSTER = 22766;

	private static final int ANTIDOTE = 15514;

	private static final int BUFF = 6286;

	private static final int[][] MOB_SPAWNS = 
	{
		{ 185216, -184112, -3308, -15396 },
		{ 185456, -184240, -3308, -19668 },
		{ 185712, -184384, -3308, -26696 },
		{ 185920, -184544, -3308, -32544 },
		{ 185664, -184720, -3308, 27892 },
	};

	private static final ReentrantLock lock = new ReentrantLock();

	private boolean checkConditions(L2Player player)
	{
		SystemMessage sm;
		if(player.getLevel() < 82 || player.getLevel() > 85)
		{
			sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player);
			player.sendPacket(sm);
			return false;
		}
		else if(player.isCursedWeaponEquipped() || player.isInFlyingTransform() || player.isDead())
		{
			sm = new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player);
			player.sendPacket(sm);
			return false;
		}
		return true; 
	}

	protected int enterInstance(L2Player player)
	{
		int templateId = 138;
		if(!checkConditions(player))
			return 0;
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(templateId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return 0;
		}
		InstancedZone iz = izs.get(0);
		if(iz == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return 0;
		}

		String name = iz.getName();
		int time = iz.getTimelimit();
		Reflection ref = new Reflection(name);
		ref.setInstancedZoneId(templateId);
		for(InstancedZone i : izs.values())
		{
			if (ref.getReturnLoc() == null)
				ref.setReturnLoc(i.getReturnCoords());
			if (ref.getTeleportLoc() == null)
				ref.setTeleportLoc(i.getTeleportCoords());
			ref.FillSpawns(i.getSpawnsInfo());
		}
		World world = new World();
		world.instanceId = ref.getId();
		lock.lock();
		try
		{
			addWorld(ref.getId(), world);
		}
		finally
		{
			lock.unlock();
		}
		player.setReflection(ref);
		player.teleToLocation(iz.getTeleportCoords());
		player.setVar("backCoords", player.getLoc().toXYZString());
		ref.startCollapseTimer(time * 60000);
		return templateId;
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(st == null)
			return htmltext;
		if(npc.getNpcId() == _rafforty)
		{
			if(event.equalsIgnoreCase("32020-04.htm"))
			{
				st.setState(STARTED);
				st.set("progress", "1");
				st.set("cond", "1");
				st.set("jinia_themes", "102030"); //theme ID - state - something like 1-0, 2-0, 3-0
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if(npc.getNpcId() == _kroon || npc.getNpcId() == _taroon)
		{
			if(event.equalsIgnoreCase("enter"))
			{
				if(enterInstance(st.getPlayer()) > 0)
				{
					htmltext = "";
					if(st.getInt("progress") == 2 && st.getQuestItemsCount(ANTIDOTE) == 0)
					{
						st.giveItems(ANTIDOTE, 1);
						st.playSound("ItemSound.quest_middle");
						st.set("cond", "4");
					}
				}
				else
					htmltext = npc.getNpcId() == _kroon ? "kroon_q10284_07.htm" : "32654-07.htm";
			}
		}
		else if(npc.getNpcId() == _jinia)
		{
			if(event.equalsIgnoreCase("32760-05.htm"))
			{
				switch(st.getInt("jinia_themes"))
				{
					case 112030: //1st theme have been readed
						htmltext = "32760-05a.htm";
						break;
					case 102130: //2nd theme have been readed
						htmltext = "32760-05b.htm";
						break;
					case 102031: //3rd theme have been readed
						htmltext = "32760-05c.htm";
						break;
					case 102131: //2nd and 3rd theme have been readed
						htmltext = "32760-05d.htm";
						break;
					case 112031: //1st and 3rd theme have been readed
						htmltext = "32760-05e.htm";
						break;
					case 112130: //1st and 2nd theme have been readed
						htmltext = "32760-05f.htm";
						break;
					case 112131: //all three themes have been readed
						htmltext = "32760-05g.htm";
				}
			}
			else if(event.equalsIgnoreCase("32760-02c.htm"))
			{
				int jinia_themes = st.getInt("jinia_themes");
				jinia_themes += 10000; //mark 1st theme as readed
				st.set("jinia_themes", Integer.toString(jinia_themes));
			}
			else if(event.equalsIgnoreCase("32760-03c.htm"))
			{
				int jinia_themes = st.getInt("jinia_themes");
				jinia_themes += 100; //mark 2nd theme as readed
				st.set("jinia_themes", Integer.toString(jinia_themes));
			}
			else if(event.equalsIgnoreCase("32760-04c.htm"))
			{
				int jinia_themes = st.getInt("jinia_themes");
				jinia_themes += 1; //mark 3rd theme as readed
				st.set("jinia_themes", Integer.toString(jinia_themes));
			}
			else if(event.equalsIgnoreCase("32760-07.htm"))
			{
				st.set("jinia_themes","102030");
				st.set("progress", "2");
				st.set("cond", "3");
				st.playSound("ItemSound.quest_middle");

				st.getPlayer().getReflection().startCollapseTimer(60*1000);
			}
		}
		else if(npc.getNpcId() == KEGOR_IN_CAVE)
		{
			World world = getWorld(npc.getReflectionId());
			if(world != null)
			{
				if(event.equalsIgnoreCase("spawn"))
				{
					htmltext = "";
					for(int[] spawn : MOB_SPAWNS)
					{
						L2MonsterInstance spawnedMob = (L2MonsterInstance)addSpawnToInstance(MONSTER, new Location(spawn[0], spawn[1], spawn[2], spawn[3]), 0, world.instanceId);
						world.liveMobs.add(spawnedMob);
					}
				}
				else if(event.equalsIgnoreCase("buff"))
				{
					htmltext = "";
					if(world != null && world.liveMobs != null && !world.liveMobs.isEmpty())
					{
						for(L2MonsterInstance monster : world.liveMobs)
							if(monster.getObjectId() == npc.getObjectId())
							{
								monster.addDamageHate(npc, 0, 999);
								monster.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null); // Переводим в состояние атаки
								monster.getAI().addTaskAttack(npc); // Добавляем отложенное задание атаки, сработает в самом конце движения
							}
							else
								monster.getAI().addTaskMove(new Location(npc.getX(), npc.getY(), npc.getZ(), 0), false);
						if(L2World.getAroundPlayers(npc).size() == 1)
						{
							L2Skill buff = SkillTable.getInstance().getInfo(BUFF,1);
							if(buff != null)
								for(L2Player pl : L2World.getAroundPlayers(npc))
									if(Util.checkIfInRange(buff.getCastRange(), npc, pl, false))
									{
										//npc.setTarget(pl);
										if(!buff.checkSkillAbnormal(pl) && !buff.isBlockedByChar(pl, buff))
											try
											{
												for (EffectTemplate et : buff.getEffectTemplates())
												{
													Env env = new Env(npc, pl, buff);
													L2Effect effect = et.getEffect(env);
													pl.getEffectList().addEffect(effect);
												}
											}
											catch (Exception e)
											{}
									}
						}		
						startQuestTimer("buff", 30000, npc, st.getPlayer());
					}
				}
				/*
				else if(event.equalsIgnoreCase("attack_mobs"))
				{
					if(_liveMobs != null && !_liveMobs.isEmpty())
					{
						int idx = Rnd.get(_liveMobs.size());

						if(npc.getKnownList().knowsObject(_liveMobs.get(idx)))
						{
							((L2MonsterInstance)npc).addDamageHate(_liveMobs.get(idx), 0, 999);
							((L2MonsterInstance)npc).getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, _liveMobs.get(idx), null); // Переводим в состояние атаки
							((L2MonsterInstance)npc).getAI().addTaskAttack(npc); // Добавляем отложенное задание атаки, сработает в самом конце движения
						}
						startQuestTimer("attack_mobs", 10000, KEGOR, st.getPlayer());
					}
				}
				if (npcId == _mob && KEGOR != null)
				{
					if (getQuestTimer("attack_mobs", KEGOR, null) == null)
						startQuestTimer("attack_mobs", 10000, KEGOR, null);
				}*/
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(npc.getNpcId() == _rafforty)
		{
			switch(st.getState())
			{
				case COMPLETED:
					return "32020-02.htm";
				case CREATED:
					QuestState _prev = st.getPlayer().getQuestState("_10283_RequestOfIceMerchant");
					if(_prev != null && _prev.getState() == COMPLETED && st.getPlayer().getLevel() >= 82) 
						return "32020-01.htm"; // первый базар, после чего ТП к ссире)
					else
						return "32020-03.htm";
				case STARTED:
					if(st.getInt("progress") == 1)
						return "32020-05.htm";
					else if(st.getInt("progress") == 2)
						return "32020-09.htm";
			}
		}
		else if(npc.getNpcId() == _jinia)
		{
			if(st.getState() != STARTED)
				return "noquest";
			if(st.getInt("progress") == 1)
			{
				int jinia_themes = st.getInt("jinia_themes");
				//look above for explanation 
				switch(jinia_themes)
				{
					case 102030:
						return "32760-01.htm"; 
					case 112030:
						return "32760-01a.htm"; 
					case 102130:
						return "32760-01b.htm"; 
					case 102031:
						return "32760-01c.htm"; 
					case 102131:
						return "32760-01d.htm"; 
					case 112031:
						return "32760-01e.htm"; 
					case 112130:
						return "32760-01f.htm"; 
					case 112131:
						return "32760-01g.htm"; 
				}
			}
		}
		else if(npc.getNpcId() == _kroon || npc.getNpcId() == _taroon)
		{
			if(st.getState() != STARTED)
				return "noquest";
			if(st.getInt("progress") == 2)
				return npc.getNpcId() == _kroon ? "32653-01.htm" : "32654-01.htm";
		}
		else if(npc.getNpcId() == KEGOR_IN_CAVE)
		{
			World world = getWorld(st.getPlayer().getReflectionId());
			if(world != null)
			{
				if(st.getInt("progress") == 2 && st.getQuestItemsCount(ANTIDOTE) > 0 && !world.underAttack)
				{
					st.takeItems(ANTIDOTE, st.getQuestItemsCount(ANTIDOTE));
					st.playSound("ItemSound.quest_middle");
					st.set("cond", "5");
					htmltext = "kegor_savedun_q10284_01.htm";
					world.underAttack = true;
					npc.setIsInvul(false);
					startQuestTimer("spawn", 3000, npc,st.getPlayer());
					startQuestTimer("buff", 3500, npc,st.getPlayer());
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		QuestState hostQuest = st.getPlayer().getQuestState(_10284_AcquisitionOfDivineSword.class);
		if(hostQuest == null || hostQuest.getState() != STARTED)
			return null;
		World world = getWorld(npc.getReflectionId());
		if(world != null)
		{
			if(npc.getNpcId() == MONSTER)
			{
				if(world.liveMobs != null && !world.liveMobs.isEmpty())
				{
					try
					{
						if(world.liveMobs.contains((L2MonsterInstance) npc))
						{
							world.liveMobs.remove((L2MonsterInstance) npc);
							if(world.liveMobs.isEmpty() && world.KEGOR != null && !world.KEGOR.isDead() && hostQuest.getInt("progress") == 2)
							{
								world.underAttack = false;
								world.liveMobs = null;
								cancelQuestTimer("buff", st.getPlayer());
								world.KEGOR.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), ConfigValue.FollowRange);
								NpcSay cs = new NpcSay(world.KEGOR, Say2C.NPC_ALL, 1801099);
								world.KEGOR.broadcastPacket(cs);
								hostQuest.set("progress", "3");
								hostQuest.set("cond", "6");
								hostQuest.playSound("ItemSound.quest_middle");
								st.getPlayer().getReflection().startCollapseTimer(3 * 60000);
							}
						}
					}
					catch(NullPointerException e)
					{
						st.getPlayer().getReflection().startCollapseTimer(30000);
					}
				}
				else if(npc.getNpcId() == KEGOR_IN_CAVE)
				{
					world.KEGOR = null;
					NpcSay cs = new NpcSay(npc, Say2C.NPC_ALL, 1801098);
					npc.broadcastPacket(cs);
					st.getPlayer().getReflection().startCollapseTimer(3 * 60000);
				}
			}
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}