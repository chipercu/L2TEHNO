package events.FightClub;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.list.array.TIntArrayList;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.*;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;
import l2open.gameserver.serverpackets.ExAutoSoulShot;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.Revive;
import l2open.gameserver.serverpackets.SkillCoolTime;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.skills.SkillTimeStamp;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import l2open.util.Rnd;
import java.util.logging.Logger;

public class FightClubManager extends Functions implements ScriptFile
{
	private static Logger _log = Logger.getLogger(FightClubManager.class.getName());
	private static TIntObjectHashMap<Rate> _rate = new TIntObjectHashMap<Rate>();
	private static TIntObjectHashMap<Location> _loc = new TIntObjectHashMap<Location>();
	
	private static List<FightClubArena> fcArena = new ArrayList<FightClubArena>();
	private static List<Integer> _playerList = new ArrayList<Integer>();
	private static List<Integer> _regList = new ArrayList<Integer>();
	private static StringBuilder _itemList = new StringBuilder();
	private static Map<String, Integer> _item = new HashMap<String, Integer>();

	static
	{
		for(String str1 : ConfigValue.AllowedItems.replace(" ", "").split(","))
		{
			String str2 = ItemTemplates.getInstance().createItem(Integer.parseInt(str1)).getName();
			_itemList.append(str2).append(";");
			_item.put(str2, Integer.valueOf(Integer.parseInt(str1)));
		}
	}

	private static void removeReg(L2Player player)
	{
		if(player != null)
		{
			player.setEventReg(false);
			player.setTeam(0, false);
			player.setIsInEvent((byte) 0);
			if(_playerList.contains(player.getObjectId()))
				_playerList.remove(Integer.valueOf(player.getObjectId()));
			if(_regList.contains(player.getObjectId()))
			{
				_rate.remove(player.getObjectId());
				_regList.remove(Integer.valueOf(player.getObjectId()));
			}
			if(_loc.containsKey(player.getObjectId()))
				_loc.remove(player.getObjectId());
			if(ConfigValue.FightClubOlympiadItems || ConfigValue.FightClubForbiddenItems.length > 0)
				player.getInventory().unlock();
		}
	}

	public void onPlayerTeleport(L2Character player, Location loc)
	{
		if(!ConfigValue.FightClubEnabled)
			return;
		if(player != null && player.isPlayer() && player.getPlayer().isInEvent() == 1 && _playerList.contains(player.getObjectId()))
		{
			removeReg((L2Player) player);
			for(FightClubArena fca : fcArena)
				if(fca != null)
					fca.onPlayerTeleport(player.getPlayer());
		}
	}

	public void OnPlayerExit(L2Player player)
	{
		if(!ConfigValue.FightClubEnabled)
			return;
		if(_regList.contains(player.getObjectId()))
			removeReg(player);
		if(player != null && player.isPlayer() && player.isInEvent() == 1 && _playerList.contains(player.getObjectId()))
		{
			removeReg(player);
			for(FightClubArena fca : fcArena)
				if(fca != null)
					fca.OnPlayerExit(player);
		}
	}

	private static void lockItems(L2Player player)
	{
		if(ConfigValue.FightClubOlympiadItems || ConfigValue.FightClubForbiddenItems.length > 0)
		{
			TIntArrayList items = new TIntArrayList();

			if(ConfigValue.FightClubForbiddenItems.length > 0)
			{
				for(int i = 0; i < ConfigValue.FightClubForbiddenItems.length; i++)
				{
					items.add(ConfigValue.FightClubForbiddenItems[i]);
				}
			}

			if(ConfigValue.FightClubOlympiadItems)
			{
				for(L2ItemInstance item : player.getInventory().getItems())
				{
					if(!item.getOlympiadUse())
						items.add(item.getItemId());
				}
			}

			player.getInventory().lockItems(LockType.INCLUDE, items.toArray());
		}
	}

	public void OnDie(L2Character self, L2Character killer)
	{
		if(!ConfigValue.FightClubEnabled)
			return;
		if(self != null && self.isPlayer() && self.getPlayer().isInEvent() == 1 && self.getTeam() > 0)
			for(FightClubArena fca : fcArena)
				fca.OnDie(self);
	}

	@Override
	public void onLoad()
	{
		if(!ConfigValue.FightClubEnabled)
			return;
		_log.info("Loaded Event: Fight Club");
	}

	@Override
	public void onReload()
	{
		for(FightClubArena arena : fcArena)
			arena.stopEndTask();
		fcArena.clear();
		_rate.clear();
		_playerList.clear();
		_regList.clear();
	}

	@Override
	public void onShutdown()
	{
		onReload();
	}

	public static String addApplication(L2Player player, String name, long count)
	{
		if(!checkPlayer(player, true))
			return null;
		if(isRegistered(player))
			return "reg";
		if(Functions.getItemCount(player, _item.get(name)) < count)
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledItems", player), player);
			return "NoItems";
		}
		player.setEventReg(true);
		final Rate rate = new Rate(player, _item.get(name), count);
		_rate.put(player.getObjectId(), rate);
		_regList.add(0, player.getObjectId());
		if(ConfigValue.AnnounceRate)
		{
			boolean announce = true;
			if(ConfigValue.AnnounceRateItem.length > 0)
			{
				announce = false;
				for(int i=0;i < ConfigValue.AnnounceRateItem.length;i++)
					if(ConfigValue.AnnounceRateItem[i] == rate.getItemId() && rate.getItemCount() >= ConfigValue.AnnounceRateItemCount[i])
						announce = true;
			}
			if(announce)
			{
				final String[] args = {
						player.getName(),
						String.valueOf(player.getLevel()),
						String.valueOf(NumberFormat.getInstance().format(rate.getItemCount())),
						name };
				Announcements.getInstance().announceByCustomMessage("scripts.events.fightclub.Announce", args, 18);
			}
		}
		return "OK";
	}

	public static boolean requestConfirmation(L2Player player1, L2Player player2)
	{
		if(!checkPlayer(player2, true))
			return false;
		if((player1.getLevel() - player2.getLevel() > ConfigValue.MaximumLevelDifference) || (player2.getLevel() - player1.getLevel() > ConfigValue.MaximumLevelDifference))
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledLevel", player2, ConfigValue.MinimumLevel, ConfigValue.MaximumLevel, ConfigValue.MaximumLevelDifference), player2);
			return false;
		}
		Object[] arrayOfObject = { player1, player2 };
		player1.scriptRequest(new CustomMessage("scripts.events.fightclub.AskPlayer", player1, player2.getName(), Integer.parseInt("" + player2.getLevel())).toString(), "events.FightClub.FightClubManager:doStart", arrayOfObject);
		return true;
	}

	public static void doStart(L2Player player1, L2Player player2)
	{
		if(_rate.get(player1.getObjectId()) == null)
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledOpponent", player2), player2);
			return;
		}
		int itemId = _rate.get(player1.getObjectId()).getItemId();
		long itemCount = _rate.get(player1.getObjectId()).getItemCount();
		if(!validItemCount(player1, player2, itemId, itemCount))
			return;
		if(!checkPlayer(player1, false))
			return;
		if(!checkPlayer(player2, true))
			return;
		player1.setEventReg(true);
		player2.setEventReg(true);
		if(_regList.contains(player2.getObjectId()))
		{
			_regList.remove(Integer.valueOf(player2.getObjectId()));
			_rate.remove(player2.getObjectId());
		}
		_regList.remove(Integer.valueOf(player1.getObjectId()));
		_rate.remove(player1.getObjectId());
		_loc.put(player1.getObjectId(), new Location(player1.getX(), player1.getY(), player1.getZ()));
		_loc.put(player2.getObjectId(), new Location(player2.getX(), player2.getY(), player2.getZ()));
		Functions.removeItem(player1, itemId, itemCount);
		Functions.removeItem(player2, itemId, itemCount);
		fillRef(player1, player2, itemId, itemCount);
	}

	private static void playerClear(L2Player player)
	{
		if(player == null)
			return;
		if(player.inObserverMode())
			player.leaveObserverMode(Olympiad.getGameBySpectator(player));
		if(ConfigValue.RemoveClanSkills && player.getClan() != null)
			for(L2Skill skill : player.getClan().getAllSkills())
				player.removeSkill(skill, false, true);
		if(ConfigValue.RemoveHeroSkills && player.isHero())
			Hero.removeSkills(player);
		if(ConfigValue.CancelBuffs)
		{
			try
			{
				if(player.isCastingNow())
					player.abortCast(true);
				player.getEffectList().stopAllEffects();
				if(player.getPet() != null)
				{
					L2Summon summon = player.getPet();
					summon.getEffectList().stopAllEffects();
					if(summon.isPet() && ConfigValue.UnsummonPets)
						summon.unSummon();
					else if(summon.isSummon() && ConfigValue.UnsummonSummons)
						summon.unSummon();
				}
				if(player.getAgathion() != null)
					player.setAgathion(0);
				player.sendPacket(new SkillList(player));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(player.getPet() != null && ConfigValue.CancelBuffs)
			player.getPet().getEffectList().stopAllEffects();
		if(player.getAgathion() != null)
			player.setAgathion(0);
		for(SkillTimeStamp sts : player.getSkillReuseTimeStamps().values())
			if(sts.getReuseBasic() <= 900000)
				player.enableSkill(ConfigValue.SkillReuseType == 0 ? sts.getSkill()*65536L+sts.getLevel() : sts.getSkill());
		player.sendPacket(new SkillCoolTime(player));

		// remove bsps/sps/ss automation
		ConcurrentSkipListSet<Integer> activeSoulShots = player.getAutoSoulShot();
		for(int itemId : activeSoulShots)
		{
			player.removeAutoSoulShot(itemId);
			player.sendPacket(new ExAutoSoulShot(itemId, false));
		}

		// Разряжаем заряженные соул и спирит шоты
		L2ItemInstance weapon = player.getActiveWeaponInstance();
		if(weapon != null)
		{
			weapon.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
			weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
		}

		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new Revive(player));
		}
		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.broadcastUserInfo(true);
		lockItems(player);
	}

	private static boolean validItemCount(L2Player player1, L2Player player2, int count1, long count2)
	{
		if(Functions.getItemCount(player1, count1) < count2)
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledItems", player1), player1);
			show(new CustomMessage("scripts.events.fightclub.CancelledOpponent", player2), player2);
			return false;
		}
		if(Functions.getItemCount(player2, count1) < count2)
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledItems", player2), player2);
			return false;
		}
		if(_playerList.contains(player1.getObjectId()))
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledOpponent", player2), player2);
			return false;
		}
		return true;
	}

	private static void fillRef(L2Player player1, L2Player player2, int count1, long count2)
	{
		_playerList.add(player1.getObjectId());
		_playerList.add(player2.getObjectId());
		int i = Rnd.get(147, 150);

		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(i);
		Reflection ref = new Reflection(izs.get(0).getName());
		ref.setInstancedZoneId(i);
		for(InstancedZone in : izs.values())
			ref.FillDoors(in.getDoors());

		fcArena.add(new FightClubArena(player1, player2, count1, count2, ref));
	}

	public static synchronized void deleteArena(FightClubArena paramFightClubArena)
	{
		removeReg(paramFightClubArena.getPlayer1());
		removeReg(paramFightClubArena.getPlayer2());
		paramFightClubArena.getReflection().collapse();
		fcArena.remove(paramFightClubArena);
	}

	public static boolean checkPlayer(L2Player player, boolean isReg)
	{
		if(TerritorySiege.isInProgress())
		{
			show("Во время проведения Территориальных Битв, Бойцовский Клуб не доступен.", player, null);
			return false;
		}
		for(Castle castle : CastleManager.getInstance().getCastles().values())
			if(castle != null && castle.getSiege().isInProgress())
			{
				show("Во время проведения Осад, Бойцовский Клуб не доступен.", player, null);
				return false;
			}
		if(player.isDead())
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledDead", player), player);
			return false;
		}
		if(player.getTeam() != 0)
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledOtherEvent", player), player);
			return false;
		}
		if(player.getLevel() < ConfigValue.MinimumLevel || player.getLevel() > ConfigValue.MaximumLevel)
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledLevel", player), player);
			return false;
		}
		if(player.isMounted())
		{
			show(new CustomMessage("scripts.events.fightclub.Cancelled", player), player);
			return false;
		}
		if(player.isCursedWeaponEquipped() || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped() || player.getTerritorySiege() > 0)
		{
			show(new CustomMessage("scripts.events.fightclub.Cancelled", player), player);
			return false;
		}
		if(player.isInDuel())
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledDuel", player), player);
			return false;
		}
		if(player.getOlympiadGame() != null || Olympiad.isRegistered(player))
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledOlympiad", player), player);
			return false;
		}
		if(player.isInParty() && player.getParty().isInDimensionalRift())
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledOtherEvent", player), player);
			return false;
		}
		if(player.inObserverMode())
		{
			show(new CustomMessage("scripts.event.fightclub.CancelledObserver", player), player);
			return false;
		}
		if(player.isTeleporting())
		{
			show(new CustomMessage("scripts.events.fightclub.CancelledTeleport", player), player);
			return false;
		}
		return true;
	}

	public static boolean isRegistered(L2Player player)
	{
		return _regList.contains(player.getObjectId());
	}

	public static Rate getRateByIndex(int index)
	{
		return _rate.get(_regList.get(index));
	}

	public static Rate getRateByStoredId(int obj_id)
	{
		return _rate.get(obj_id);
	}

	public static String getItemsList()
	{
		return _itemList.toString();
	}

	public static void deleteRegistration(L2Player player)
	{
		removeReg(player);
	}

	public static int getRatesCount()
	{
		return _regList.size();
	}

	public static void teleportPlayersBack(L2Player player1, L2Player player2)
	{
		teleportPlayerBack(player1);
		teleportPlayerBack(player2);
		player1.unsetVar("reflection");
		player2.unsetVar("reflection");
		player1.unsetVar("backCoords");
		player2.unsetVar("backCoords");
	}

	public static void teleportPlayerBack(L2Player player)
	{
		if(player == null)
			return;
		player.setEventReg(false);
		if(player.getPet() != null)
			player.getPet().getEffectList().stopAllEffects();
		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new Revive(player));
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);
		if(ConfigValue.RemoveClanSkills && player.getClan() != null && player.getClan().getReputationScore() >= 0)
			for(L2Skill localL2Skill : player.getClan().getAllSkills())
				player.addSkill(localL2Skill);
		if(ConfigValue.RemoveHeroSkills && player.isHero() && (!player.isSubClassActive() || ConfigValue.Multi_Enable))
			Hero.addSkills(player);
		player.sendPacket(new SkillList(player));
		player.getEffectList().stopAllEffects();
		if(player != null && _loc.containsKey(player.getObjectId()))
			player.teleToLocation(_loc.get(player.getObjectId()), 0);
	}

	public static synchronized void sayToPlayers(String paramString, Object paramObject, boolean paramBoolean, L2Player... paramArrayOfL2Player)
	{
		for(L2Player localL2Player : paramArrayOfL2Player)
			localL2Player.sendPacket(new ExShowScreenMessage(new CustomMessage(paramString, localL2Player, paramObject).toString(), 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, paramBoolean));
	}

	public static synchronized void sayToPlayers(String paramString, boolean paramBoolean, L2Player... paramArrayOfL2Player)
	{
		for(L2Player localL2Player : paramArrayOfL2Player)
			localL2Player.sendPacket(new ExShowScreenMessage(new CustomMessage(paramString, localL2Player).toString(), 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, paramBoolean));
	}

	public static synchronized void sayToPlayer(L2Player player, String paramString, boolean paramBoolean, Object... paramArrayOfObject)
	{
		player.sendPacket(new ExShowScreenMessage(new CustomMessage(paramString, player, paramArrayOfObject).toString(), 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, paramBoolean));
	}

	public static void startBattle(L2Player player1, L2Player player2)
	{
		player1.setTeam(1, true);
		player2.setTeam(2, true);
		sayToPlayers("scripts.events.fightclub.Start", true, player1, player2);
	}

	public static synchronized void teleportPlayersToColliseum(L2Player player1, L2Player player2, Reflection ref)
	{
		L2Zone zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.OlympiadStadia, ref.getInstancedZoneId() - 146, true);

		int[] arrayOfInt1 = zone.getSpawns().get(0);
		int[] arrayOfInt2 = zone.getSpawns().get(1);
		playerClear(player1);
		if(player1 != null)
		{
			player1.teleToLocation(arrayOfInt1[0], arrayOfInt1[1], arrayOfInt2[2], ref.getId());
			player1.setVar("backCoords", "" + player1.getLoc().toXYZString());
			player1.setVar("reflection", "" + ref.getId());
		}
		playerClear(player2);
		if(player2 != null)
		{
			player2.teleToLocation(arrayOfInt2[0], arrayOfInt2[1], arrayOfInt2[2], ref.getId());
			player2.setVar("backCoords", "" + player2.getLoc().toXYZString());
			player2.setVar("reflection", "" + ref.getId());
		}
		player1.setIsInEvent((byte) 1);
		player2.setIsInEvent((byte) 1);
	}
}