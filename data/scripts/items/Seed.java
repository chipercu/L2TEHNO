package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.L2Manor;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.instances.L2ChestInstance;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.SkillTable;

public class Seed implements IItemHandler, ScriptFile
{
	private static int[] _itemIds = {};

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		// Цель не выбрана
		if(playable.getTarget() == null)
		{
			player.sendActionFailed();
			return;
		}

		// Цель не моб, РБ или миньон
		if(!player.getTarget().isMonster() || player.getTarget() instanceof L2RaidBossInstance || player.getTarget() instanceof L2MinionInstance && ((L2MinionInstance) player.getTarget()).getLeader() instanceof L2RaidBossInstance || player.getTarget() instanceof L2ChestInstance || ((L2MonsterInstance) playable.getTarget()).getChampion() > 0 && !item.isAltSeed())
		{
			player.sendPacket(Msg.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return;
		}

		L2MonsterInstance target = (L2MonsterInstance) playable.getTarget();

		if(target == null)
		{
			player.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		// Моб мертв
		if(target.isDead())
		{
			player.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		// Уже посеяно
		if(target.isSeeded())
		{
			player.sendPacket(Msg.THE_SEED_HAS_BEEN_SOWN);
			return;
		}

		int seedId = item.getItemId();
		if(seedId == 0 || player.getInventory().getItemByItemId(item.getItemId()) == null)
		{
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		int castleId = TownManager.getInstance().getClosestTown(player).getCastleIndex();
		if(castleId < 0)
			castleId = 1; // gludio manor dy default
		else
		{
			Residence castle = CastleManager.getInstance().getCastleByIndex(castleId);
			if(castle != null)
				castleId = castle.getId();
		}

		//System.out.println(CastleManager.getInstance().findNearestCastleIndex(activeChar) + " " +castleId + " " + L2Manor.getInstance().getSeedManorId(_seedId));
		// Несовпадение зоны
		if(L2Manor.getInstance().getCastleIdForSeed(seedId) != castleId)
		{
			//System.out.println("seed (" + _seedId + ") zone " + L2Manor.getInstance().getSeedManorId(_seedId) + " != castle_zone " + castleId);
			player.sendPacket(Msg.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return;
		}

		// use Sowing skill, id 2097
		L2Skill skill = SkillTable.getInstance().getInfo(2097, 1);
		if(skill == null)
		{
			player.sendActionFailed();
			return;
		}

		if(skill.checkCondition(player, target, false, false, true))
		{
			player.setUseSeed(seedId);
			player.getAI().Cast(skill, target);
		}
	}

	public final int[] getItemIds()
	{
		return _itemIds;
	}

	public void onLoad()
	{
		_itemIds = new int[L2Manor.getInstance().getAllSeeds().size()];
		int id = 0;
		for(Integer s : L2Manor.getInstance().getAllSeeds().keySet())
			_itemIds[id++] = s.shortValue();
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}