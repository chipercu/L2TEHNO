package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.MonsterRace;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;

public class L2RaceManagerInstance extends L2NpcInstance
{
	public static final int LANES = 8;
	public static final int WINDOW_START = 0;

	@SuppressWarnings("unused")
	private static GArray<Race> history;
	private static GArray<L2RaceManagerInstance> managers;
	private static int _raceNumber = 1;

	//Time Constants
	private final static long SECOND = 1000;
	private final static long MINUTE = 60 * SECOND;

	private static int minutes = 5;

	//States
	private static final int ACCEPTING_BETS = 0;
	private static final int WAITING = 1;
	private static final int STARTING_RACE = 2;
	private static final int RACE_END = 3;
	private static int state = RACE_END;

	protected static final int[][] codes = { { -1, 0 }, { 0, 15322 }, { 13765, -1 } };
	private static boolean notInitialized = true;
	protected static MonRaceInfo packet;
	protected static int cost[] = { 100, 500, 1000, 5000, 10000, 20000, 50000, 100000 };

	public L2RaceManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		//System.out.println("L2RaceManagerInstance start...");
		if(notInitialized)
		{
			//System.out.println("L2RaceManagerInstance start2...");
			notInitialized = false;

			_raceNumber = ServerVariables.getInt("monster_race", 1);
			history = new GArray<Race>();
			managers = new GArray<L2RaceManagerInstance>();

			ThreadPoolManager s = ThreadPoolManager.getInstance();
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE), 0, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE), 30 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE), MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE), MINUTE + 30 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 2 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 3 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 4 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 5 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED), 6 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED), 7 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES), 7 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES), 8 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S1TH_MONSTER_RACE_WILL_BEGIN_IN_30_SECONDS), 8 * MINUTE + 30 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S1TH_MONSTER_RACE_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS), 8 * MINUTE + 50 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 55 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 56 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 57 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 58 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 59 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THEYRE_OFF), 9 * MINUTE, 10 * MINUTE);
		}
		if(managers == null)
		{
			managers = new GArray<L2RaceManagerInstance>();
			System.out.println("L2RaceManagerInstance error...");
		}
		managers.add(this);
	}

	public void removeKnownPlayer(L2Player player)
	{
		for(int i = 0; i < 8; i++)
			player.sendPacket(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
	}

	class Announcement extends l2open.common.RunnableImpl
	{
		private int type;

		public Announcement(int type)
		{
			this.type = type;
		}

		public void runImpl()
		{
			makeAnnouncement(type);
		}
	}

	public void makeAnnouncement(int type)
	{
		SystemMessage sm = new SystemMessage(type);
		switch(type)
		{
			case SystemMessage.TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE:
			case SystemMessage.WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE:
				if(state != ACCEPTING_BETS)
				{
					state = ACCEPTING_BETS;
					startRace();
				}
				sm.addNumber(_raceNumber);
				break;
			case SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S:
			case SystemMessage.THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES:
			case SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS:
				sm.addNumber(minutes);
				sm.addNumber(_raceNumber);
				minutes--;
				break;
			case SystemMessage.TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED:
				//System.out.println("Sales closed");
				sm.addNumber(_raceNumber);
				state = WAITING;
				minutes = 2;
				break;
			case SystemMessage.THE_S1TH_MONSTER_RACE_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS:
			case SystemMessage.MONSTER_RACE_S1_IS_FINISHED:
				sm.addNumber(_raceNumber);
				minutes = 5;
				break;
			case SystemMessage.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2:
				//System.out.println("Placing");
				state = RACE_END;
				sm.addNumber(MonsterRace.getInstance().getFirstPlace());
				sm.addNumber(MonsterRace.getInstance().getSecondPlace());
				break;
		}

		broadcast(sm);

		if(type == SystemMessage.THEYRE_OFF)
		{
			state = STARTING_RACE;
			startRace();
			minutes = 5;
		}
	}

	protected void broadcast(L2GameServerPacket pkt)
	{
		for(L2RaceManagerInstance manager : managers)
			if(!manager.isDead())
				manager.broadcastPacketToOthers(pkt);
	}

	public void sendMonsterInfo()
	{
		broadcast(packet);
	}

	private void startRace()
	{
		MonsterRace race = MonsterRace.getInstance();
		if(state == STARTING_RACE)
		{
			//state++;
			PlaySound SRace = new PlaySound("S_Race");
			broadcast(SRace);
			PlaySound SRace2 = new PlaySound(0, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559));
			broadcast(SRace2);
			packet = new MonRaceInfo(codes[1][0], codes[1][1], race.getMonsters(), race.getSpeeds());
			sendMonsterInfo();

			ThreadPoolManager.getInstance().schedule(new RunRace(), 5000, false);
		}
		else
		{
			//state++;
			race.newRace();
			race.newSpeeds();
			packet = new MonRaceInfo(codes[0][0], codes[0][1], race.getMonsters(), race.getSpeeds());
			sendMonsterInfo();
		}

	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("BuyTicket") && state != ACCEPTING_BETS)
		{
			player.sendPacket(Msg.MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
			command = "Chat 0";
		}
		if(command.startsWith("ShowOdds") && state == ACCEPTING_BETS)
		{
			player.sendPacket(Msg.MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
			command = "Chat 0";
		}

		if(command.startsWith("BuyTicket"))
		{
			int val = Integer.parseInt(command.substring(10));
			if(val == 0)
			{
				player.setRace(0, 0);
				player.setRace(1, 0);
			}
			if(val == 10 && player.getRace(0) == 0 || val == 20 && player.getRace(0) == 0 && player.getRace(1) == 0)
				val = 0;
			showBuyTicket(player, val);
		}
		else if(command.equals("ShowOdds"))
			showOdds(player);
		else if(command.equals("ShowInfo"))
			showMonsterInfo(player);
		else if(command.equals("calculateWin"))
		{
			//displayCalculateWinnings(player);
		}
		else if(command.equals("viewHistory"))
		{
			//displayHistory(player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void showOdds(L2Player player)
	{
		if(state == ACCEPTING_BETS)
			return;
		int npcId = getTemplate().npcId;
		String filename, search;
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		filename = getHtmlPath(npcId, 5);
		html.setFile(filename);
		for(int i = 0; i < 8; i++)
		{
			int n = i + 1;
			search = "Mob" + n;
			html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
		}
		html.replace("1race", String.valueOf(_raceNumber));
		player.sendPacket(html);
		player.sendActionFailed();
	}

	public void showMonsterInfo(L2Player player)
	{
		int npcId = getTemplate().npcId;
		String filename, search;
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		filename = getHtmlPath(npcId, 6);
		html.setFile(filename);
		for(int i = 0; i < 8; i++)
		{
			int n = i + 1;
			search = "Mob" + n;
			html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
		}
		player.sendPacket(html);
		player.sendActionFailed();
	}

	public void showBuyTicket(L2Player player, int val)
	{
		if(state != ACCEPTING_BETS)
			return;
		int npcId = getTemplate().npcId;
		String filename, search, replace;
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		if(val < 10)
		{
			filename = getHtmlPath(npcId, 2);
			html.setFile(filename);
			for(int i = 0; i < 8; i++)
			{
				int n = i + 1;
				search = "Mob" + n;
				html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
			}
			search = "No1";
			if(val == 0)
				html.replace(search, "");
			else
			{
				html.replace(search, "" + val);
				player.setRace(0, val);
			}
		}
		else if(val < 20)
		{
			if(player.getRace(0) == 0)
				return;
			filename = getHtmlPath(npcId, 3);
			html.setFile(filename);
			html.replace("0place", "" + player.getRace(0));
			search = "Mob1";
			replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().name;
			html.replace(search, replace);
			search = "0adena";
			if(val == 10)
				html.replace(search, "");
			else
			{
				html.replace(search, "" + cost[val - 11]);
				player.setRace(1, val - 10);
			}
		}
		else if(val == 20)
		{
			if(player.getRace(0) == 0 || player.getRace(1) == 0)
				return;
			filename = getHtmlPath(npcId, 4);
			html.setFile(filename);
			html.replace("0place", "" + player.getRace(0));
			search = "Mob1";
			replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().name;
			html.replace(search, replace);
			search = "0adena";
			int price = cost[player.getRace(1) - 1];
			html.replace(search, "" + price);
			search = "0tax";
			int tax = 0;
			html.replace(search, "" + tax);
			search = "0total";
			int total = price + tax;
			html.replace(search, "" + total);
		}
		else
		{
			if(player.getRace(0) == 0 || player.getRace(1) == 0)
				return;
			if(player.getAdena() < cost[player.getRace(1) - 1])
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
			int ticket = player.getRace(0);
			int priceId = player.getRace(1);
			player.setRace(0, 0);
			player.setRace(1, 0);
			player.reduceAdena(cost[priceId - 1], true);
			SystemMessage sm = new SystemMessage(SystemMessage.ACQUIRED__S1_S2);
			sm.addNumber(_raceNumber);
			sm.addItemName(4443);
			player.sendPacket(sm);
			L2ItemInstance item = ItemTemplates.getInstance().createItem(4443);
			item.setEnchantLevel(_raceNumber);
			item.setCustomType1(ticket);
			item.setCustomType2(cost[priceId - 1] / 100);
			player.getInventory().addItem(item);
			return;
		}
		html.replace("1race", String.valueOf(_raceNumber));
		player.sendPacket(html);
		player.sendActionFailed();
	}

	public class Race
	{
		private Info[] info;

		public Race(Info[] info)
		{
			this.info = info;
		}

		public Info getLaneInfo(int lane)
		{
			return info[lane];
		}

		public class Info
		{
			private int id;
			private int place;
			private int odds;
			private int payout;

			public Info(int id, int place, int odds, int payout)
			{
				this.id = id;
				this.place = place;
				this.odds = odds;
				this.payout = payout;
			}

			public int getId()
			{
				return id;
			}

			public int getOdds()
			{
				return odds;
			}

			public int getPayout()
			{
				return payout;
			}

			public int getPlace()
			{
				return place;
			}
		}
	}

	class RunRace extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			packet = new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
			sendMonsterInfo();
			ThreadPoolManager.getInstance().schedule(new RunEnd(), 30000);
		}
	}

	class RunEnd extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			makeAnnouncement(SystemMessage.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2);
			makeAnnouncement(SystemMessage.MONSTER_RACE_S1_IS_FINISHED);
			_raceNumber++;
			ServerVariables.set("monster_race", _raceNumber);

			for(int i = 0; i < 8; i++)
				broadcast(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
		}
	}

	public MonRaceInfo getPacket()
	{
		return packet;
	}
}