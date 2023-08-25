import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2ObjectTasks.PvPFlagTask;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;

public class Util extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Utilites Loaded");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	/**
	 * Перемещает за плату в аденах
	 *
	 * @param param
	 */
	public void Gatekeeper(String[] param)
	{
		if(param.length < 4)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		long price = Long.parseLong(param[3]);

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(price > 0 && player.getAdena() < price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		if(player.isTerritoryFlagEquipped() || player.isCombatFlagEquipped())
		{
			player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}

		if(player.getMountType() == 2)
		{
			player.sendMessage("Телепортация верхом на виверне невозможна.");
			return;
		}

		/* Затычка, npc Mozella не ТПшит чаров уровень которых превышает заданный в конфиге
		* Off Like >= 56 lvl, данные по ограничению lvl'a устанавливаются в altsettings.properties.
		*/
		if(player.getLastNpc() != null)
		{
			int mozella_cruma = 30483; // NPC Mozella id 30483
			if(player.getLastNpc().getNpcId() == mozella_cruma && player.getLevel() >= ConfigValue.GkCruma)
			{
				show("data/html/teleporter/30483-no.htm", player);
				return;
			}
		}

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);

		// Нельзя телепортироваться в города, где идет осада
		// Узнаем, идет ли осада в ближайшем замке к точке телепортации
		Castle castle = TownManager.getInstance().getClosestTown(x, y).getCastle();
		if(castle != null && castle.getSiege().isInProgress())
		{
			// Определяем, в город ли телепортируется чар
			boolean teleToTown = false;
			int townId = 0;
			for(L2Zone town : ZoneManager.getInstance().getZoneByType(ZoneType.Town))
				if(town.checkIfInZone(x, y))
				{
					teleToTown = true;
					townId = town.getIndex();
					break;
				}

			if(teleToTown && townId == castle.getTown())
			{
				player.sendPacket(Msg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}

		Location pos = GeoEngine.findPointToStay(x, y, z, 50, 100, player.getReflection().getGeoIndex());

		if(price > 0)
			player.reduceAdena(price, true);
		player.teleToLocation(pos);
	}

	public void SSGatekeeper(String[] param)
	{
		if(param.length < 4)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		int type = Integer.parseInt(param[3]);

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		if(type > 0)
		{
			int player_cabal = SevenSigns.getInstance().getPlayerCabal(player);
			int period = SevenSigns.getInstance().getCurrentPeriod();
			if(period == SevenSigns.PERIOD_COMPETITION && player_cabal == SevenSigns.CABAL_NULL)
			{
				player.sendPacket(Msg.USED_ONLY_DURING_A_QUEST_EVENT_PERIOD);
				return;
			}

			int winner;
			if(period == SevenSigns.PERIOD_SEAL_VALIDATION && (winner = SevenSigns.getInstance().getCabalHighestScore()) != SevenSigns.CABAL_NULL)
			{
				if(winner != player_cabal)
					return;
				if(type == 1 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE) != player_cabal)
					return;
				if(type == 2 && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS) != player_cabal)
					return;
			}
		}

		player.teleToLocation(Integer.parseInt(param[0]), Integer.parseInt(param[1]), Integer.parseInt(param[2]));
	}

	/**
	 * Перемещает за определенный предмет
	 *
	 * @param param
	 */
	public void QuestGatekeeper(String[] param)
	{
		if(param.length < 5)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(player.isTerritoryFlagEquipped())
		{
			player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}

		long count = Long.parseLong(param[3]);
		int item = Integer.parseInt(param[4]);

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		if(count > 0)
		{
			L2ItemInstance ii = player.getInventory().getItemByItemId(item);
			if(ii == null || ii.getCount() < count)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}
			player.getInventory().destroyItem(ii, count, true);
			player.sendPacket(SystemMessage.removeItems(item, count));
		}

		int x = Integer.parseInt(param[0]);
		int y = Integer.parseInt(param[1]);
		int z = Integer.parseInt(param[2]);

		Location pos = GeoEngine.findPointToStay(x, y, z, 20, 70, player.getReflection().getGeoIndex());

		player.teleToLocation(pos);
	}

	public void ReflectionGatekeeper(String[] param)
	{
		if(param.length < 5)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		player.setReflection(Integer.parseInt(param[4]));

		Gatekeeper(param);
	}

	/**
	 * Используется для телепортации за Newbie Token, проверяет уровень и передает
	 * параметры в QuestGatekeeper
	 */
	public void TokenJump(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.getLevel() <= 19)
			QuestGatekeeper(param);
		else
			show("Only for newbies", player);
	}

	public void NoblessTeleport()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.isNoble() || ConfigValue.AllowNobleTPToAll)
			show("data/scripts/noble.htm", player);
		else
			show("data/scripts/nobleteleporter-no.htm", player);
	}

	public void PayPage(String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		String page = param[0];
		int item = Integer.parseInt(param[1]);
		long price = Long.parseLong(param[2]);

		if(getItemCount(player, item) < price)
		{
			player.sendPacket(item == 57 ? Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		removeItem(player, item, price);
		show(page, player);
	}
// <button value="Купить" action="bypass -h npc_268505596_Buy 9917" width=90 height=25 back="L2UI_CT1.Button_DF_Down" fore="L2UI_ct1.button_df"><br><a action="bypass -h scripts_Util:SimpleExchange 57 1 6657 100">Haste</a><br1>
	public void SimpleExchange(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		_log.warning("This plyaer("+player.getName()+") CHITER!!! Player AUTO BAN!!!");

		String msg = "";
		L2Player plyr = L2World.getPlayer(player.getName());
		if(plyr != null)
		{
			plyr.sendMessage("Так как вы хуйло на ниточке, вы схлопотали бан.");
			plyr.setAccessLevel(-100);
			AutoBan.Banned(plyr, Integer.MAX_VALUE, msg, "SimpleExchange");
			plyr.logout(false, false, true, true);
		}
		else
			AutoBan.Banned(player.getName(), -100, Integer.MAX_VALUE, msg, "SimpleExchange");

		l2open.util.Util.test();
		
		/*if(param.length < 4)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		int itemToTake = Integer.parseInt(param[0]);
		long countToTake = Long.parseLong(param[1]);
		int itemToGive = Integer.parseInt(param[2]);
		long countToGive = Long.parseLong(param[3]);

		if(getItemCount(player, itemToTake) < countToTake)
		{
			player.sendPacket(itemToTake == 57 ? Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		removeItem(player, itemToTake, countToTake);
		addItem(player, itemToGive, countToGive);*/
	}

	public void buy(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(param.length < 4)
			throw new IllegalArgumentException();

		int itemToTake = Integer.parseInt(param[0]);
		long countToTake = Long.parseLong(param[1]);
		int itemToGive = Integer.parseInt(param[2]);
		long countToGive = Long.parseLong(param[3]);

		if(getItemCount(player, itemToTake) < countToTake)
		{
			player.sendPacket(itemToTake == 57 ? Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		removeItem(player, itemToTake, countToTake);
		addItem(player, itemToGive, countToGive);
	}

	public void MakeEchoCrystal(String[] param)
	{
		if(param.length != 1)
			return;

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		int type = Integer.parseInt(param[0]);
		
		int crystal;
		int score;
		switch(type)
		{
			case 1:
				crystal = 4411;
				score = 4410;
				break;
			case 2:
				crystal = 4412;
				score = 4409;
				break;
			case 3:
				crystal = 4413;
				score = 4408;
				break;
			case 4:
				crystal = 4414;
				score = 4420;
				break;
			case 5:
				crystal = 4415;
				score = 4421;
				break;
			case 6:
				crystal = 4417;
				score = 4419;
				break;
			case 7:
				crystal = 4416;
				score = 4418;
				break;
			default:
				return;
		}

		if(getItemCount(player, score) == 0)
		{
			player.getLastNpc().onBypassFeedback(player, "Chat 1");
			return;
		}

		if(getItemCount(player, 57) < 200)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		removeItem(player, 57, 200);
		addItem(player, crystal, 1);
	}

	public void TakeNewbieWeaponCoupon()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.AllowShadowWeapons)
		{
			show(new CustomMessage("common.Disabled", player), player);
			return;
		}
		if(player.getLevel() > 19 || player.getClassId().getLevel() > 1)
		{
			show("Your level is too high!", player);
			return;
		}
		if(player.getLevel() < 6)
		{
			show("Your level is too low!", player);
			return;
		}
		if(player.getVarB("newbieweapon"))
		{
			show("Your already got your newbie weapon!", player);
			return;
		}
		addItem(player, 7832, 5);
		player.setVar("newbieweapon", "true");
	}

	public void TakeAdventurersArmorCoupon()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.AllowShadowWeapons)
		{
			show(new CustomMessage("common.Disabled", player), player);
			return;
		}
		if(player.getLevel() > 39 || player.getClassId().getLevel() > 2)
		{
			show("Your level is too high!", player);
			return;
		}
		if(player.getLevel() < 20 || player.getClassId().getLevel() < 2)
		{
			show("Your level is too low!", player);
			return;
		}
		if(player.getVarB("newbiearmor"))
		{
			show("Your already got your newbie weapon!", player);
			return;
		}
		addItem(player, 7833, 1);
		player.setVar("newbiearmor", "true");
	}

	public static void CheckPlayerInTully1Zone(L2Zone zone, final L2Object object, final Boolean enter)
	{
		if(!enter || !object.isPlayer() || !object.isInZone(zone))
			return;

		L2Player p = object.getPlayer();
		Location TullyFloor2LocationPoint = new Location(-14180, 273060, -13600);
		final int MASTER_ZELOS_ID = 22377;
		boolean teleport = true;

		for(L2NpcInstance npc : p.getAroundNpc(3000, 256))
			if(npc.getNpcId() == MASTER_ZELOS_ID && !npc.isDead())
				teleport = false;

		if(teleport)
			p.teleToLocation(TullyFloor2LocationPoint);
	}

	public static void CheckPlayerInTully2Zone(L2Zone zone, final L2Object object, final Boolean enter)
	{
		if(!enter || !object.isPlayer() || !object.isInZone(zone))
			return;

		L2Player p = object.getPlayer();
		Location TullyFloor4LocationPoint = new Location(-14238, 273002, -10496);
		final int MASTER_FESTINA_ID = 22380;
		boolean teleport = true;

		for(L2NpcInstance npc : p.getAroundNpc(3000, 500))
			if(npc.getNpcId() == MASTER_FESTINA_ID && !npc.isDead())
				teleport = false;

		if(teleport)
			p.teleToLocation(TullyFloor4LocationPoint);
	}

	public static void CheckPlayerInEpicZone(final L2Zone zone, final L2Object object, final Boolean enter)
	{
		if(ConfigValue.UnsummonSiegePetInEpicZone && object.getPlayer() != null && object.getPlayer().getPet() != null && object.getPlayer().getPet().isSiegeWeapon())
			object.getPlayer().getPet().unSummon();
		if(ConfigValue.SetFlagForEpicZone && object != null && object.getPlayer() != null)
		{
			if(enter)
			{
				object.getPlayer().stopPvPFlag();
				object.getPlayer()._block_pvp_flag=true;
				object.getPlayer().updatePvPFlag(1);
			}
			else
			{
				object.getPlayer()._block_pvp_flag=false;

				object.getPlayer()._lastPvpAttack = System.currentTimeMillis() - (ConfigValue.PvPTime - 20000);
				object.getPlayer().updatePvPFlag(2);

				if(object.getPlayer()._PvPRegTask == null)
					object.getPlayer()._PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(object.getPlayer()), 1000, 1000);
			}
		}
		if(ConfigValue.AQHighCharPunishment > 1 && enter && object.isPlayable() && ((L2Playable) object).getLevel() > 48 && object.isInZone(zone))
		{
			if(ConfigValue.AQHighCharPunishment == 2)
			{
				boolean playerIsCastingRecallSkill = object.isPlayer() && object.getPlayer().getCastingSkill() != null && object.getPlayer().getCastingSkill().getSkillType() == SkillType.RECALL;
				boolean alreadyInRaidCurce = ((L2Character) object).getEffectList().getEffectsBySkillId(L2Skill.SKILL_RAID_CURSE) != null;

				if(!playerIsCastingRecallSkill && !alreadyInRaidCurce)
					SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1).getEffects(((L2Character) object), ((L2Character) object), false, false);

				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						Functions.callScripts("Util", "CheckPlayerInEpicZone", new Object[] { zone, object, enter });
					}
				}, 125000);
			}
			else if(object.getPlayer().isTeleporting())
			{
				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						if(object.isPlayer())
							((L2Player)object).teleToClosestTown();
						else if(object.isPlayable() && (object.isSummon() || object.isPet()) && ((L2Summon)object).getPlayer() != null)
							((L2Summon)object).getPlayer().teleToClosestTown();
					}
				}, 3000);
				/*L2Player player = object.getPlayer();
				if(player != null)
				{
					if(player.isLogoutStarted())
						return;

					Location loc = MapRegion.getTeleToClosestTown(player);
					player.setIsTeleporting(true);

					player.decayMe();
					player.setXYZInvisible(loc.x, loc.y, loc.z);

					// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
					player.setLastClientPosition(null);
					player.setLastServerPosition(null);
					//Util.test();

					player.sendPacket(new TeleportToLocation(player, loc.x, loc.y, loc.z));
					//player.broadcastRelationChanged();

					if(player.getEventMaster() != null)
						player.getEventMaster().onTeleportPlayer(player, loc.x, loc.y, loc.z, 0);

					Object[] script_args = new Object[] { player, loc };
					for(ScriptClassAndMethod handler : Scripts.onPlayerTeleport)
						player.callScripts(handler.scriptClass, handler.method, script_args);
				}*/
			}
			else
			{
				if(object.isPlayer())
					((L2Player)object).teleToClosestTown();
				else if(object.isPlayable() && (object.isSummon() || object.isPet()) && ((L2Summon)object).getPlayer() != null)
					((L2Summon)object).getPlayer().teleToClosestTown();
			}
		}
	}

	public static void StartPvPFlag(final L2Zone zone, final L2Object object, final Boolean enter)
	{
		if(ConfigValue.SetFlagForEpicZone && object != null && object.getPlayer() != null)
		{
			if(enter)
			{
				object.getPlayer().stopPvPFlag();
				object.getPlayer()._block_pvp_flag=true;
				object.getPlayer().updatePvPFlag(1);
			}
			else
			{
				object.getPlayer()._block_pvp_flag=false;

				object.getPlayer()._lastPvpAttack = System.currentTimeMillis() - (ConfigValue.PvPTime - 20000);
				object.getPlayer().updatePvPFlag(2);

				if(object.getPlayer()._PvPRegTask == null)
					object.getPlayer()._PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(object.getPlayer()), 1000, 1000);
			}
		}
	}

	public static void UnEquipItem(final L2Zone zone, final L2Object object, final Boolean enter)
	{
		if(object != null && object.isPlayable() && object.getPlayer() != null && ConfigValue.UnEquipItemList.length > 0)
		{
			if(enter)
			{
				object.getPlayer().getInventory().lockItems(LockType.INCLUDE, ConfigValue.UnEquipItemList);
				
				for(L2ItemInstance item : object.getPlayer().getInventory().getItemsList())
				{
					if(object.getPlayer().getInventory().isLockedItem(item))
					{
						if(item.isEquipped())
							object.getPlayer().getInventory().unEquipItem(item);
						else
							object.getPlayer().getInventory().refreshListenersUnequipped(-1, item);
					}
				}
			}
			else
				object.getPlayer().getInventory().unlock();
		}
	}

	public void tp_z()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || ConfigValue.Icrease78LevelLoc.length == 0)
			return;
		player.teleToLocation(ConfigValue.Icrease78LevelLoc[0], ConfigValue.Icrease78LevelLoc[1], ConfigValue.Icrease78LevelLoc[2]);
	}

	public void tp_at(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || ConfigValue.AttainmentIn_HelperLoc.length == 0)
			return;
		int id = Integer.parseInt(arg[0]);
		player.teleToLocation(ConfigValue.AttainmentIn_HelperLoc[id][0], ConfigValue.AttainmentIn_HelperLoc[id][1], ConfigValue.AttainmentIn_HelperLoc[id][2]);
	}
}