package l2open.gameserver.model.entity.siege.clanhall;

import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.instances.L2ChestInstance;
import l2open.gameserver.model.instances.L2HotSpringSquashInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SpawnTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class RainbowSpringSiege extends CHSiege
{
	private static final Logger _log = Logger.getLogger(RainbowSpringSiege.class.getName());
	private static RainbowSpringSiege _instance;
	private boolean _registrationPeriod = false;
	public ClanHall clanhall = ClanHallManager.getInstance().getClanHall(62);
	private Map<Integer, clanPlayersInfo> _clansInfo = new HashMap<Integer, clanPlayersInfo>();
	private L2NpcInstance[] eti = { null, null, null, null };
	private int[] potionsApply = { 0, 0, 0, 0 };
	private L2HotSpringSquashInstance[] squash = { null, null, null, null };

	private int[] arenaChestsCnt = { 0, 0, 0, 0 };
	private int currArena;
	private ArrayList<Integer> _playersOnArena = new ArrayList<Integer>();
	private L2NpcInstance teleporter;
	private ScheduledFuture<?> _chestsSpawnTask;
	private int teamWiner = -1;
	private ArrayList<L2ChestInstance> arena1chests = new ArrayList<L2ChestInstance>();
	private ArrayList<L2ChestInstance> arena2chests = new ArrayList<L2ChestInstance>();
	private ArrayList<L2ChestInstance> arena3chests = new ArrayList<L2ChestInstance>();
	private ArrayList<L2ChestInstance> arena4chests = new ArrayList<L2ChestInstance>();

	private int[] _skillsId = { 1092, 1160, 1170, 1064, 1222, 1069, 1206, 1201 };
	private int[] _skillsLvl = { 19, 15, 13, 14, 15, 42, 19, 33 };

	private final ExclusiveTask _startSiegeTask = new ExclusiveTask(true)
	{
		protected void onElapsed()
		{
			if(getIsInProgress())
			{
				cancel();
				return;
			}
			Calendar siegeStart = Calendar.getInstance();
			siegeStart.setTimeInMillis(getSiegeDate().getTimeInMillis());
			long registerTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis() - 3600000;
			long siegeTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis();
			long remaining = registerTimeRemaining;
			if(registerTimeRemaining <= 0 &&  !isRegistrationPeriod())
			{
				setRegistrationPeriod(true);
				if(clanhall.getOwnerId() != 0)
				{
					clanPlayersInfo regPlayers = _clansInfo.get(clanhall.getOwnerId());
					if(regPlayers == null)
					{
						regPlayers = new clanPlayersInfo();
						regPlayers._clanName = clanhall.getOwner().getName();
						regPlayers._decreeCnt = 0;
						_clansInfo.put(clanhall.getOwnerId(), regPlayers);
					}
				}
				anonce("Внимание!!! Начался период регистрации на осаду Холл Клана, Дворец Радужных Источников.");
				anonce("Внимание!!! Битва за Холл Клана, Дворец Радужных Источников начнется через час.");
				remaining = siegeTimeRemaining;
			}

			if(siegeTimeRemaining <= 0)
			{
				setRegistrationPeriod(false);
				startSiege();
				cancel();
				return;
			}
			schedule(remaining);
		}
	};

	private final ExclusiveTask _endSiegeTask = new ExclusiveTask(true)
	{
		protected void onElapsed()
		{
			if(!getIsInProgress())
			{
				cancel();
				return;
			}
			long timeRemaining = _siegeEndDate.getTimeInMillis() - System.currentTimeMillis();
			if(timeRemaining <= 0)
			{
				endSiege(false); //возможно true
				cancel();
				return;
			}
			schedule(timeRemaining);
		}
	};

	private final ExclusiveTask _firstStepSiegeTask = new ExclusiveTask(true)
	{
		protected void onElapsed()
		{
			startFirstStep();
		}
	};

	public static RainbowSpringSiege getInstance()
	{
		if(_instance == null)
		{
			_instance = new RainbowSpringSiege();
		}
		return _instance;
	}

	public RainbowSpringSiege()
	{
		long siegeDate = restoreSiegeDate(62);
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.setTimeInMillis(siegeDate);
		setSiegeDate(tmpDate);
		setNewSiegeDate(siegeDate, 62, ConfigValue.RainbowSprSiegeHour, ConfigValue.RainbowSprSiegeDay);
		_startSiegeTask.schedule(1000);
		_log.info("Siege of Rainbow Springs Chateau : " + tmpDate.getTime());
	}

	public void startSiege()
	{
		if(_startSiegeTask.isScheduled())
		{
			_startSiegeTask.cancel();
		}
		if(_clansInfo.size() == 0)
		{
			endSiege(false);
			return;
		}
		if(_clansInfo.size() > 4)
		{
			for (int x = 1; x < _clansInfo.size() - 4; x++)
			{
				clanPlayersInfo minClan = null;
				int minVal = Integer.MAX_VALUE;
				for(clanPlayersInfo cl : _clansInfo.values())
				{
					if(cl._decreeCnt < minVal)
					{
						minVal = cl._decreeCnt;
						minClan = cl;
					}
				}
				if(minClan != null)
					_clansInfo.remove(minClan._clanId);
			}
		}
		else if(_clansInfo.size() < 2)
		{
			shutdown();
			anonce("Внимание!!! Холл Клана, Дворец Радужных Источников не получил нового владельца");
			endSiege(false);
			return;
		}
		for(L2Spawn sp : SpawnTable.getInstance().getSpawnTable())
		{
			if(sp.getTemplate().getNpcId() == 35603)
			{
				teleporter = sp.getLastSpawn();
			}
		}
		teamWiner = -1;
		currArena = 0;
		setIsInProgress(true);
		anonce("Внимание!!! соревнование за Холл Клана, Дворец Радужных Источников начнется через 5 минут.");
		anonce("Внимание!!! представителям кланов необходимо войти на арену.");
		for(clanPlayersInfo cl : _clansInfo.values())
		{
			L2Clan clan = ClanTable.getInstance().getClanByName(cl._clanName);
			L2Player clanLeader = clan.getLeader().getPlayer();
			if(clanLeader != null)
			{
				clanLeader.sendMessage("Ваш Клан принимает участие в соревновании. Пройдите на арену.");
			}
		}
		_firstStepSiegeTask.schedule(300000);

		_siegeEndDate = Calendar.getInstance();
		_siegeEndDate.add(12, 65);
		_endSiegeTask.schedule(1000);
	}


	public void startFirstStep()
	{
		L2NpcTemplate template = NpcTable.getTemplate(35596);
		Location loc = new Location();

		for (int x = 0; x <= 3; x++)
		{
			eti[x] = new L2NpcInstance(IdFactory.getInstance().getNextId(), template);
			eti[x].setCurrentHpMp(eti[x].getMaxHp(), eti[x].getMaxMp());
		}
		loc.set(153129, -125337, -2221);
		eti[0].spawnMe(loc);
		loc.set(153884, -127534, -2221);
		eti[1].spawnMe(loc);
		loc.set(151560, -127075, -2221);
		eti[2].spawnMe(loc);
		loc.set(155657, -125753, -2221);
		eti[3].spawnMe(loc);
		template = NpcTable.getTemplate(35588);
		for(int x = 3; x >= 0; x--)
		{
			squash[x] = new L2HotSpringSquashInstance(IdFactory.getInstance().getNextId(), template);
			squash[x].setCurrentHpMp(squash[x].getMaxHp(), squash[x].getMaxMp());
		}
		loc.set(153179, -125287, -2221);
		squash[0].spawnMe(loc);
		squash[0].setIsInvul(true);
		loc.set(153934, -127484, -2221);
		squash[1].spawnMe(loc);
		squash[1].setIsInvul(true);
		loc.set(151610, -127025, -2221);
		squash[2].spawnMe(loc);
		squash[2].setIsInvul(true);
		loc.set(155707, -125703, -2221);
		squash[3].spawnMe(loc);
		squash[3].setIsInvul(true);

		_chestsSpawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ChestsSpawn(), 5000, 5000);
	}

	public void chestDie(L2Character killer, L2ChestInstance chest)
	{
		if(arena1chests.contains(chest))
		{
			arenaChestsCnt[0] -= 1;
			arena1chests.remove(chest);
		}
		if(arena2chests.contains(chest))
		{
			arenaChestsCnt[1] -= 1;
			arena2chests.remove(chest);
		}
		if(arena3chests.contains(chest))
		{
			arenaChestsCnt[2] -= 1;
			arena3chests.remove(chest);
		}
		if(!arena4chests.contains(chest))
			return;
		arenaChestsCnt[3] -= 1;
		arena4chests.remove(chest);
	}

	public void exchangeItem(L2Player player, int val)
	{
		if(val == 1)
		{
			if(player.getInventory().getItemByItemId(8054) != null  && player.getInventory().getItemByItemId(8035) != null && player.getInventory().getItemByItemId(8052) != null && player.getInventory().getItemByItemId(8039) != null && player.getInventory().getItemByItemId(8050) != null && player.getInventory().getItemByItemId(8051) != null)
			{
				player.getInventory().destroyItemByItemId(8054, 1, true);
				player.getInventory().destroyItemByItemId(8035, 1, true);
				player.getInventory().destroyItemByItemId(8052, 1, true);
				player.getInventory().destroyItemByItemId(8039, 1, true);
				player.getInventory().destroyItemByItemId(8050, 1, true);
				player.getInventory().destroyItemByItemId(8051, 1, true);
				player.getInventory().addItem(8032,1);
				player.sendPacket(SystemMessage.obtainItems(8032,1,0));
			}
			else
			{
				player.sendMessage("Недостаточно квестовых предметов");
				return;
			}
		}
		if(val == 2)
		{
			if(player.getInventory().getItemByItemId(8054) != null && player.getInventory().getItemByItemId(8035) != null && player.getInventory().getItemByItemId(8052) != null && player.getInventory().getItemByItemId(8039) != null && player.getInventory().getItemByItemId(8050) != null && player.getInventory().getItemByItemId(8051) != null)
			{
				player.getInventory().destroyItemByItemId(8054,1,true);
				player.getInventory().destroyItemByItemId(8035, 1, true);
				player.getInventory().destroyItemByItemId(8052, 1, true);
				player.getInventory().destroyItemByItemId(8039, 1, true);
				player.getInventory().destroyItemByItemId(8050, 1, true);
				player.getInventory().destroyItemByItemId(8051, 1, true);
				player.getInventory().addItem(8031,1);
				player.sendPacket(SystemMessage.obtainItems(8031,1,0));
			}
			else
			{
				player.sendMessage("Недостаточно квестовых предметов");
				return;
			}
		}

		if(val == 3)
		{
			if(player.getInventory().getItemByItemId(8047) != null  && player.getInventory().getItemByItemId(8039) != null && player.getInventory().getItemByItemId(8037) != null && player.getInventory().getItemByItemId(8052) != null && player.getInventory().getItemByItemId(8035) != null && player.getInventory().getItemByItemId(8050) != null)
			{
				player.getInventory().destroyItemByItemId(8047, 1, true);
				player.getInventory().destroyItemByItemId(8039, 1, true);
				player.getInventory().destroyItemByItemId(8037, 1, true);
				player.getInventory().destroyItemByItemId(8052, 1, true);
				player.getInventory().destroyItemByItemId(8035, 1, true);
				player.getInventory().destroyItemByItemId(8050, 1, true);
				player.getInventory().addItem(8030,1);
				player.sendPacket(SystemMessage.obtainItems(8030,1,0));
			}
			else
			{
				player.sendMessage("Недостаточно квестовых предметов");
				return;
			}
		}
		if(val== 4)
		{
			if(player.getInventory().getItemByItemId(8051) != null  && player.getInventory().getItemByItemId(8053) != null && player.getInventory().getItemByItemId(8046) != null && player.getInventory().getItemByItemId(8040) != null && player.getInventory().getItemByItemId(8050) != null)
			{
				player.getInventory().destroyItemByItemId(8051, 1, true);
				player.getInventory().destroyItemByItemId(8053, 1, true);
				player.getInventory().destroyItemByItemId(8046, 1, true);
				player.getInventory().destroyItemByItemId(8040, 1, true);
				player.getInventory().destroyItemByItemId(8050, 1, true);
				player.getInventory().addItem(8033,1);
				player.sendPacket(SystemMessage.obtainItems(8033,1,0));
			}
			else
			{
				player.sendMessage("Недостаточно квестовых предметов");
			}
		}
	}

	public synchronized void onDieSquash(L2HotSpringSquashInstance par)
	{
		if(!getIsInProgress())
			return;
		for(int x = 0; x < squash.length; x++)
		{
			if(squash[x] == par)
				teamWiner = x;
		}
		if(teamWiner >= 0)
		{
			anonce("Внимание !!! Один из участников соревнований, успешно справился с испытанием.");
			anonce("О результатах соревнований за обладание Холл Кланом Горячих источников будет сообщено через 2 минуты.");
			setIsInProgress(false);
			unspawnQusetNPC();
			_endSiegeTask.cancel();
			ThreadPoolManager.getInstance().schedule(new EndSiegeTaks(), 120000);
		}
	}

	private void unspawnQusetNPC()
	{
		if(_chestsSpawnTask != null)
			_chestsSpawnTask.cancel(true);
		for(L2ChestInstance ch : arena1chests)
			ch.deleteMe();
		for(L2ChestInstance ch : arena2chests)
			ch.deleteMe();
		for(L2ChestInstance ch : arena3chests)
			ch.deleteMe();
		for(L2ChestInstance ch : arena4chests)
			ch.deleteMe();
		for(int x = 0; x < 4; x++)
		{
			if(squash[x] != null)
				squash[x].deleteMe();
		}
	}

	public boolean usePotion(L2Playable activeChar, int potionId)
	{
		if(activeChar.isPlayer() && isPlayerInArena((L2Player)activeChar) && activeChar.getTarget().isNpc() && ((L2NpcInstance)activeChar.getTarget()).getTemplate().getNpcId() == 35596)
		{
			int action = 0;
			switch(potionId)
			{
				case 8030:
					action = 3;
				break;
				case 8031:
					action = 1;
				break;
				case 8032:
					action = 4;
				break;
				case 8033:
					action = 2;
				break;
			}

			if(action == 0)
				return false;

			L2Clan plClan = ((L2Player)activeChar).getClan();
			if(plClan == null)
				return false;

			int playerArena = -1;
			for(clanPlayersInfo cl : _clansInfo.values())
			{
				if(plClan.getName().equalsIgnoreCase(cl._clanName))
					playerArena = cl._arenaNumber;
			}
			if(playerArena == -1)
				return false;

			if(action == 1) //done
			{
				int rndArena = Rnd.get(0, _clansInfo.size() - 1);
				if (rndArena == playerArena)
				  rndArena++;
				if (rndArena > _clansInfo.size() - 1)
				  rndArena = 0;
				double hp = squash[rndArena].getMaxHp() / 100 * Rnd.get(5, 15);
				squash[rndArena].setIsInvul(false);
				squash[rndArena].setCurrentHp(squash[rndArena].getCurrentHp() + hp, false);
				squash[rndArena].setIsInvul(true);
				activeChar.sendMessage("Вы восстановили здоровье тыквы противника");
			}
			else if(action == 2) //done
			{
				// Запоминаем тыквы в новый массив
				L2HotSpringSquashInstance[] squashAll = { null, null, null, null };
				squashAll[0] = squash[0];
				squashAll[1] = squash[1];
				squashAll[2] = squash[2];
				squashAll[3] = squash[3];
				// Выбираем рамдомно с кем поменяеся местом первая тыква...
				int newLoc = Rnd.get(1, 3);
				// Координаты для первой тыквы...
				Location newLoc0sc = squash[newLoc].getLoc();
				Location newLoc1sc = new Location();
				Location newLoc2sc = new Location();
				Location newLoc3sc = new Location();
				switch(newLoc)
				{
					case 1:
						newLoc1sc = squash[0].getLoc();
						newLoc2sc = squash[3].getLoc();
						newLoc3sc = squash[2].getLoc();
						// Телепортируем тыквы на свои места...
						squash[0].teleToLocation(newLoc0sc);
						squash[1].teleToLocation(newLoc1sc);
						squash[2].teleToLocation(newLoc2sc);
						squash[3].teleToLocation(newLoc3sc);
						squash[0] = squashAll[1];
						squash[1] = squashAll[0];
						squash[2] = squashAll[3];
						squash[3] = squashAll[2];
					break;
					case 2:
						newLoc1sc = squash[3].getLoc();
						newLoc2sc = squash[0].getLoc();
						newLoc3sc = squash[1].getLoc();
						// Телепортируем тыквы на свои места...
						squash[0].teleToLocation(newLoc0sc);
						squash[1].teleToLocation(newLoc1sc);
						squash[2].teleToLocation(newLoc2sc);
						squash[3].teleToLocation(newLoc3sc);
						squash[0] = squashAll[2];
						squash[1] = squashAll[3];
						squash[2] = squashAll[0];
						squash[3] = squashAll[1];
					break;
					case 3:
						newLoc1sc = squash[2].getLoc();
						newLoc2sc = squash[1].getLoc();
						newLoc3sc = squash[0].getLoc();
						// Телепортируем тыквы на свои места...
						squash[0].teleToLocation(newLoc0sc);
						squash[1].teleToLocation(newLoc1sc);
						squash[2].teleToLocation(newLoc2sc);
						squash[3].teleToLocation(newLoc3sc);
						squash[0] = squashAll[3];
						squash[1] = squashAll[2];
						squash[2] = squashAll[1];
						squash[3] = squashAll[0];
					break;
				}
				activeChar.sendMessage("Вы поменяли тыквы местами.");
			}
			else if(action == 3) // done
			{
				double damage = squash[playerArena].getMaxHp() / 100 * Rnd.get(5, 15);
				squash[playerArena].setIsInvul(false);
				squash[playerArena].reduceCurrentHp(damage, activeChar, null, false, false, false, false, false, damage, true, false, false, false);
				squash[playerArena].setIsInvul(true);
				activeChar.sendMessage("Вы нанесли урон тыкве");
			}
			else if(action == 4) //done
			{
				int rndArena = Rnd.get(0, _clansInfo.size() - 1);
				String clName = "";
				if(rndArena == playerArena)
					rndArena++;
				if(rndArena > _clansInfo.size() - 1)
					rndArena = 0;
				for(clanPlayersInfo cl : _clansInfo.values())
				{
					if(cl._arenaNumber == rndArena)
						clName = cl._clanName;
				}
				for (int id : _playersOnArena)
				{
					L2Player pl = L2ObjectsStorage.getPlayer(id);
					if (pl != null && pl.getClan().getName().equalsIgnoreCase(clName))
						skillsControl(pl);
				}
				activeChar.sendMessage("На вашего противника наложен отрицательный эффект");
			}
			return true;
		}
		return false;
	}

	private void skillsControl(L2Player pl)
	{
		if(pl == null)
			return;
		int x = Rnd.get(0, _skillsId.length - 1);
		L2Skill skill = SkillTable.getInstance().getInfo(_skillsId[x], _skillsLvl[x]);
		if(skill != null)
			skill.getEffects(pl, pl, false, false);
	}

	public void endSiege(boolean par)
	{
		if(!par)
		{
			setIsInProgress(false);
			unspawnQusetNPC();
			anonce("Осада Холл Клана: " + clanhall.getName() + " окончена.");
			anonce("Владелец Холл Клана остался прежний");
		}
		else
		{
			for(clanPlayersInfo ci : _clansInfo.values())
			{
				if(ci != null && ci._arenaNumber == teamWiner)
				{
					L2Clan clan = ClanTable.getInstance().getClanByName(ci._clanName);
					if(clan != null)
					{
						clanhall.changeOwner(clan);
						anonce("Осада Холл Клана: " + clanhall.getName() + " окончена.");
						anonce("Владельцем Холл Клана стал клан " + clan.getName());
					}
				}
			}
		}
		_clansInfo.clear();
		for(int id : _playersOnArena)
		{
			L2Player pl = L2ObjectsStorage.getPlayer(id);
			if(pl != null)
				pl.teleToLocation(150717, -124818, -2355);
		}
		_playersOnArena = new ArrayList<Integer>();
		setNewSiegeDate(getSiegeDate().getTimeInMillis(), 62, ConfigValue.RainbowSprSiegeHour, ConfigValue.RainbowSprSiegeDay);
		_startSiegeTask.schedule(1000);
	}

	public boolean isRegistrationPeriod()
	{
		return _registrationPeriod;
	}

	public void setRegistrationPeriod(boolean par)
	{
		_registrationPeriod = par;
	}

	public boolean isClanOnSiege(L2Clan playerClan)
	{
		if(playerClan == clanhall.getOwner())
		{
			return true;
		}
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		return regPlayers != null;
	}

	public synchronized int registerClanOnSiege(L2Player player, L2Clan playerClan)
	{
		L2ItemInstance item = player.getInventory().getItemByItemId(8034);
		int itemCnt = 0;
		if(item != null)
		{
			itemCnt = (int)item.getCount();
			if(itemCnt < 100)
				return 0;
			if(player.getInventory().destroyItem(item, itemCnt, true) != null)
			{
				clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
				if(regPlayers == null)
				{
					regPlayers = new clanPlayersInfo();
					regPlayers._clanName = playerClan.getName();
					regPlayers._clanId = playerClan.getClanId();
					regPlayers._decreeCnt = itemCnt;
					_clansInfo.put(playerClan.getClanId(), regPlayers);
				}
			}
		}
		else
		{
			return 0;
		}
		return itemCnt;
	}

	public boolean isPlayerInArena(L2Player pl)
	{
		return _playersOnArena.contains(pl.getObjectId());
	}

	public void removeFromArena(L2Player pl)
	{
		if(!_playersOnArena.contains(pl.getObjectId()))
			return;
		pl.teleToLocation(150717, -124818, -2355);
	}

	public synchronized boolean enterOnArena(L2Player pl)
	{
		L2Clan clan = pl.getClan();
		L2Party party = pl.getParty();
		if(clan == null || party == null)
		{
			return false;
		}
		if(!isClanOnSiege(clan) || !getIsInProgress() || currArena > 3 || !pl.isClanLeader() || party.getMemberCount() < 5)
		{
			return false;
		}

		clanPlayersInfo ci = _clansInfo.get(clan.getClanId());
		if(ci == null)
		{
			return false;
		}
		for(L2Player pm : party.getPartyMembers())
		{
			if(pm == null || pm.getDistance(teleporter) > 500.0)
			{
				return false;
			}
			if(pm.getClan() != clan)
			{
				return false;
			}
		}

		ci._arenaNumber = currArena;
		currArena += 1;

		for(L2Player pm : party.getPartyMembers())
		{
			if(pm.getPet() != null)
			{
				pm.getPet().unSummon();
			}
			for(L2Effect e : pm.getEffectList().getAllEffects())
				e.exit(false, false);
			pm.updateEffectIcons();

			_playersOnArena.add(pm.getObjectId());

			switch(ci._arenaNumber)
			{
				case 0:
					pm.teleToLocation(153129 + Rnd.get(-400, 400), -125337 + Rnd.get(-400, 400), -2221);
				break;
				case 1:
					pm.teleToLocation(153884 + Rnd.get(-400, 400), -127534 + Rnd.get(-400, 400), -2221);
				break;
				case 2:
					pm.teleToLocation(151560 + Rnd.get(-400, 400), -127075 + Rnd.get(-400, 400), -2221);
				break;
				case 3:
					pm.teleToLocation(155657 + Rnd.get(-400, 400), -125753 + Rnd.get(-400, 400), -2221);
				break;
			}
		}
		return true;
	}

	public synchronized boolean unRegisterClan(L2Player player)
	{
		L2Clan playerClan = player.getClan();
		if(_clansInfo.containsKey(playerClan.getClanId()))
		{
			long decreeCnt = (_clansInfo.get(playerClan.getClanId()))._decreeCnt / 2;
			if(decreeCnt > 0)
			{
				L2ItemInstance item = player.getInventory().addItem(8034, decreeCnt);
				player.sendPacket(SystemMessage.obtainItems(item));
			}
			return true;
		}
		return false;
	}

	public void anonce(String text)
	{
		NpcSay cs = new NpcSay(L2ObjectsStorage.getByNpcId(35604), Say2C.NPC_SHOUT, text);
		GArray<L2Player> res = L2World.getAroundPlayers(L2ObjectsStorage.getByNpcId(35604), 25000, 5000);
		if(res != null)
		{
			for (L2Player player : res)
			{
				player.sendPacket(cs);
			}
		}
		NpcSay css = new NpcSay(L2ObjectsStorage.getByNpcId(35603), Say2C.NPC_SHOUT, text);
		GArray<L2Player> ress = L2World.getAroundPlayers(L2ObjectsStorage.getByNpcId(35603), 25000, 5000);
		if(ress != null)
		{
			for (L2Player player : ress)
			{
				player.sendPacket(css);
			}
		}
	}

	public void shutdown()
	{
		if(isRegistrationPeriod())
		{
			for (clanPlayersInfo cl : _clansInfo.values())
			{
				L2Clan clan = ClanTable.getInstance().getClanByName(cl._clanName);
				if(clan != null && cl._decreeCnt > 0)
				{
					L2Player pl = L2World.getPlayer(clan.getLeaderName());
					if(pl != null)
					{
						pl.sendMessage("В хранилище Клана возвращены Свидетельства Участия в Войне за Холл Клана Горячего Источника");
					}
					clan.getWarehouse().addItem(8034, cl._decreeCnt, "Rainbow Springs Items Returned");
				}
			}
		}
		for (int id : _playersOnArena)
		{
			L2Player pl = L2ObjectsStorage.getPlayer(id);
			if(pl != null)
				pl.teleToLocation(150717, -124818, -2355);
		}

	}

	private class clanPlayersInfo
	{
		public String _clanName;
		public int _clanId;
		public int _decreeCnt;
		public int _arenaNumber;

		private clanPlayersInfo()
		{
		}
	}

	private final class ChestsSpawn extends l2open.common.RunnableImpl
	{
		private ChestsSpawn()
		{
		}

		public void runImpl()
		{
			if(arenaChestsCnt[0] < 4)
			{
				L2NpcTemplate template = NpcTable.getTemplate(35593);
				L2ChestInstance newChest = new L2ChestInstance(IdFactory.getInstance().getNextId(), template);
				newChest.setCurrentHpMp(newChest.getMaxHp(), newChest.getMaxMp());
				Location loc = new Location();
				loc.set(153129 + Rnd.get(-400, 400), -125337 + Rnd.get(-400, 400), -2221);
				newChest.spawnMe(loc);
				arena1chests.add(newChest);
				arenaChestsCnt[0] += 1;
			}
			if(arenaChestsCnt[1] < 4)
			{
				L2NpcTemplate template = NpcTable.getTemplate(35593);
				L2ChestInstance newChest = new L2ChestInstance(IdFactory.getInstance().getNextId(), template);
				newChest.setCurrentHpMp(newChest.getMaxHp(), newChest.getMaxMp());
				Location loc = new Location();
				loc.set(153884 + Rnd.get(-400, 400), -127534 + Rnd.get(-400, 400), -2221);
				newChest.spawnMe(loc);
				arena2chests.add(newChest);
				arenaChestsCnt[1] += 1;
			}
			if(arenaChestsCnt[2] < 4)
			{
				L2NpcTemplate template = NpcTable.getTemplate(35593);
				L2ChestInstance newChest = new L2ChestInstance(IdFactory.getInstance().getNextId(), template);
				newChest.setCurrentHpMp(newChest.getMaxHp(), newChest.getMaxMp());
				Location loc = new Location();
				loc.set(151560 + Rnd.get(-400, 400), -127075 + Rnd.get(-400, 400), -2221);
				newChest.spawnMe(loc);
				arena3chests.add(newChest);
				arenaChestsCnt[2] += 1;
			}
			if(arenaChestsCnt[3] >= 4)
			{
				return;
			}
			L2NpcTemplate template = NpcTable.getTemplate(35593);
			L2ChestInstance newChest = new L2ChestInstance(IdFactory.getInstance().getNextId(), template);
			newChest.setCurrentHpMp(newChest.getMaxHp(), newChest.getMaxMp());
			Location loc = new Location();
			loc.set(155657 + Rnd.get(-400, 400), -125753 + Rnd.get(-400, 400), -2221);
			newChest.spawnMe(loc);
			arena4chests.add(newChest);
			arenaChestsCnt[3] += 1;
		}
	}

	private final class EndSiegeTaks extends l2open.common.RunnableImpl
	{
		private EndSiegeTaks()
		{
		}

		public void runImpl()
		{
			endSiege(true);
		}
	}
}
