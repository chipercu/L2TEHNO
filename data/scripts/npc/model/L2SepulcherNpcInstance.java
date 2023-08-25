package npc.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import bosses.FourSepulchersManager;
import bosses.FourSepulchersSpawn;
import bosses.FourSepulchersSpawn.GateKeeper;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Util;

public class L2SepulcherNpcInstance extends L2NpcInstance
{
	protected static Map<Integer, Integer> _hallGateKeepers = new FastMap<Integer, Integer>();

	protected Future<?> _closeTask = null, _spawnMonsterTask = null;

	private final static String HTML_FILE_PATH = "data/html/SepulcherNpc/";

	private final static int HALLS_KEY = 7260;

	public L2SepulcherNpcInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);

		if(_closeTask != null)
			_closeTask.cancel(true);
		if(_spawnMonsterTask != null)
			_spawnMonsterTask.cancel(true);
		_closeTask = null;
		_spawnMonsterTask = null;
	}

	@Override
	public void deleteMe()
	{
		if(_closeTask != null)
		{
			_closeTask.cancel(true);
			_closeTask = null;
		}
		if(_spawnMonsterTask != null)
		{
			_spawnMonsterTask.cancel(true);
			_spawnMonsterTask = null;
		}
		super.deleteMe();
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(isDead())
		{
			player.sendActionFailed();
			return;
		}

		switch(getNpcId())
		{
			case 31468:
			case 31469:
			case 31470:
			case 31471:
			case 31472:
			case 31473:
			case 31474:
			case 31475:
			case 31476:
			case 31477:
			case 31478:
			case 31479:
			case 31480:
			case 31481:
			case 31482:
			case 31483:
			case 31484:
			case 31485:
			case 31486:
			case 31487:
				doDie(player);
				if(_spawnMonsterTask != null)
					_spawnMonsterTask.cancel(true);
				_spawnMonsterTask = ThreadPoolManager.getInstance().schedule(new SpawnMonster(getNpcId()), 3500);
				return;

			case 31455:
			case 31456:
			case 31457:
			case 31458:
			case 31459:
			case 31460:
			case 31461:
			case 31462:
			case 31463:
			case 31464:
			case 31465:
			case 31466:
			case 31467:
				doDie(player);
				if(player.getParty() != null && !player.getParty().isLeader(player))
					player = player.getParty().getPartyLeader();
				Functions.addItem(player, HALLS_KEY, 1);
				return;
		}

		super.showChatWindow(player, val);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;
		return HTML_FILE_PATH + pom + ".htm";
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(command.startsWith("open_gate"))
		{
			L2ItemInstance hallsKey = player.getInventory().getItemByItemId(HALLS_KEY);
			if(hallsKey == null)
				showHtmlFile(player, "Gatekeeper-no.htm");
			else if(FourSepulchersManager.isAttackTime())
			{
				switch(getNpcId())
				{
					case 31929:
					case 31934:
					case 31939:
					case 31944:
						FourSepulchersSpawn.spawnShadow(getNpcId());
				}

				// Moved here from switch-default
				openNextDoor(getNpcId());
				if(player.getParty() != null)
					for(L2Player mem : player.getParty().getPartyMembers())
					{
						hallsKey = mem.getInventory().getItemByItemId(HALLS_KEY);
						if(hallsKey != null)
							Functions.removeItem(mem, HALLS_KEY, hallsKey.getCount());
					}
				else
					Functions.removeItem(player, HALLS_KEY, hallsKey.getCount());
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void openNextDoor(int npcId)
	{
		GateKeeper gk = FourSepulchersManager.getHallGateKeeper(npcId);
		gk.door.openMe();

		if(_closeTask != null)
			_closeTask.cancel(true);
		_closeTask = ThreadPoolManager.getInstance().schedule(new CloseNextDoor(gk), 10000);
	}

	private class CloseNextDoor extends l2open.common.RunnableImpl
	{
		private final GateKeeper _gk;
		private int state = 0;

		public CloseNextDoor(GateKeeper gk)
		{
			_gk = gk;
		}

		public void runImpl()
		{
			if(state == 0)
			{
				try
				{
					_gk.door.closeMe();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				state++;
				_closeTask = ThreadPoolManager.getInstance().schedule(this, 10000);
			}
			else if(state == 1)
				FourSepulchersSpawn.spawnMysteriousBox(_gk.template.npcId);
		}
	}

	private class SpawnMonster extends l2open.common.RunnableImpl
	{
		private final int _NpcId;

		public SpawnMonster(int npcId)
		{
			_NpcId = npcId;
		}

		public void runImpl()
		{
			FourSepulchersSpawn.spawnMonster(_NpcId);
		}
	}

	public void sayInShout(String msg)
	{
		if(msg == null || msg.isEmpty())
			return; //wrong usage

		Collection<L2Player> knownPlayers = L2ObjectsStorage.getPlayers();
		if(knownPlayers == null || knownPlayers.isEmpty())
			return;
		Say2 sm = new Say2(0, Say2C.SHOUT, getName(), msg);
		for(L2Player player : knownPlayers)
		{
			if(player == null)
				continue;
			if(Util.checkIfInRange(15000, player, this, true))
				player.sendPacket(sm);
		}
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/SepulcherNpc/" + file);
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
}