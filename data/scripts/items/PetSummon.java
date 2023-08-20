package items;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.extensions.listeners.MethodCollection;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2PetBabyInstance;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SetupGauge;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.logging.Logger;

public class PetSummon implements IItemHandler, ScriptFile
{
	protected static Logger _log = Logger.getLogger(PetSummon.class.getName());

	// all the items ids that this handler knowns
	private static final int _skillId = 2046;
	private static final int MAX_RADIUS = 150;
	private static final int MIN_RADIUS = 100;

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		// pets resummon fast fix
		if(System.currentTimeMillis() <= player.lastDiceThrown)
			return;
		player.lastDiceThrown = System.currentTimeMillis() + 6000L;

		if(!checks(player, item, true))
			return;

		player.stopMove();
		//player.block();
		player.broadcastSkill(new MagicSkillUse(player, player, _skillId, 1, 5000, 600000), true);
		player.sendPacket(new SetupGauge(player.getObjectId(), SetupGauge.BLUE, 5000, 5000));
		player.sendPacket(Msg.SUMMON_A_PET);

		player.fireMethodInvoked(MethodCollection.onStartCast, new Object[] { null, player, true });

		// continue execution in 5 seconds
		player._skillTask = ThreadPoolManager.getInstance().schedule(new SummonFinalizer(player, item), 5000);
		player._castInterruptTime = System.currentTimeMillis() + 4500;
	}

	static class SummonFinalizer extends l2open.common.RunnableImpl
	{
		private final L2Player _player;
		private final L2ItemInstance _item;

		SummonFinalizer(L2Player player, L2ItemInstance item)
		{
			_player = player;
			_item = item;
		}

		public void runImpl()
		{
			try
			{
				if(!checks(_player, _item, false))
					return;

				int npcId = PetDataTable.getSummonId(_item);
				if(npcId == 0)
					return;

				L2NpcTemplate petTemplate = NpcTable.getTemplate(npcId);
				if(petTemplate == null)
					return;

				L2PetInstance pet = L2PetInstance.spawnPet(petTemplate, _player, _item);
				if(pet == null)
					return;

				_player.setPet(pet);
				pet.setTitle(_player.getName());

				if(!pet.isRespawned())
					try
					{
						pet.setCurrentHp(pet.getMaxHp(), false);
						pet.setCurrentMp(pet.getMaxMp());
						pet.setExp(pet.getExpForThisLevel());
						pet.setCurrentFed(pet.getMaxFed());
						PlayerData.getInstance().store_pet(pet);
					}
					catch(NullPointerException e)
					{
						_log.warning("PetSummon: failed set stats for summon " + npcId + ".");
						return;
					}

				_player.sendPacket(new MagicSkillLaunched(_player.getObjectId(), 2046, 1, pet, true));
                pet.spawnMe(GeoEngine.findPointToStayPet(_player, MIN_RADIUS, MAX_RADIUS, _player.getReflection().getGeoIndex()));
				pet.setRunning();
				pet.setFollowStatus(true, true);

				if(pet instanceof L2PetBabyInstance)
					((L2PetBabyInstance) pet).startBuffTask();
			}
			catch(Throwable e)
			{
				_log.severe(e.toString());
			}
			finally
			{
				//if(_player != null)
				//	_player.unblock();
				_player.onCastEndTime(null, _player, _player, false);
			}
		}
	}

	public static boolean checks(L2Player player, L2ItemInstance item, boolean first)
	{
		if(player.isInTransaction() || player.isInFlyingTransform())
			return false;
		
		if(player.getInventory().getItemByObjectId(item.getObjectId()) == null)
			return false;

		if(player.isSitting())
		{
			player.sendPacket(Msg.A_STRIDER_CAN_BE_RIDDEN_ONLY_WHEN_STANDING);
			return false;
		}

		if(player.isInOlympiadMode())
		{
			player.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return false;
		}

		if(first && (player.isCastingNow() || player.isActionsDisabled()))
			return false;

		L2Skill skill = SkillTable.getInstance().getInfo(_skillId, 1);
		if(!skill.checkCondition(player, player, false, true, true))
			return false;

		if(player.getPet() != null)
		{
			player.sendPacket(Msg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
			return false;
		}

		if(player.isMounted() || player.isInVehicle())
		{
			player.sendPacket(Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
			return false;
		}

		if(player.isCursedWeaponEquipped())
		{
			// You can't mount while weilding a cursed weapon
			player.sendPacket(Msg.A_STRIDER_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE);
			return false;
		}

		int npcId = PetDataTable.getSummonId(item);
		if(npcId == 0)
			return false;

		if(ConfigValue.DontAllowPetsOnSiege && (PetDataTable.isBabyPet(npcId) || PetDataTable.isImprovedBabyPet(npcId)) && SiegeManager.getSiege(player, true) != null)
		{
			player.sendMessage("Этих питомцев запрещено использовать в зонах осад.");
			return false;
		}

		for(L2Object o : L2World.getAroundObjects(player, MAX_RADIUS + 50, 200))
			if(o.isDoor())
			{
				player.sendPacket(Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
				return false;
			}

		if(item.getItemId() == 10611 || item.getItemId() == 10307 || item.getItemId() == 10308 || item.getItemId() == 10309 || item.getItemId() == 10310)
		{
			if(player.getClan() != null)
				switch(player.getClan().getHasHideout())
				{
					case 36:
					case 37:
					case 38:
					case 39:
					case 40:
					case 41:
					case 51:
					case 52:
					case 53:
					case 54:
					case 55:
					case 56:
					case 57:
						return true;
				}
			player.sendPacket(new SystemMessage(SystemMessage.YOU_CAN_SUMMON_THE_PET_YOU_ARE_TRYING_TO_SUMMON_NOW_ONLY_WHEN_YOU_OWN_AN_AGIT));
			return false;
		}
		return true;
	}

	public final int[] getItemIds()
	{
		return PetDataTable.getPetControlItems();
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}