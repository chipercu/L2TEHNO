package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillSpellbookTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;

import java.util.ArrayList;

public class Spellbooks implements IItemHandler, ScriptFile
{
	private static int[] _itemIds = null;

	public Spellbooks()
	{
		_itemIds = new int[SkillSpellbookTable.getSpellbookHandlers().size()];
		int i = 0;
		for(int id : SkillSpellbookTable.getSpellbookHandlers().keySet())
		{
			_itemIds[i] = id;
			i++;
		}
	}

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		if(item == null || item.getCount() < 1)
		{
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		if(item.getItemId() == 17030)
		{
			if(player.getSkillLevel(172) == 9 && player.getLevel() >= SkillSpellbookTable.getMinLevel(item.getItemId()))
			{
				L2Skill skill = SkillTable.getInstance().getInfo(172, 10);
				L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
				player.addSkill(skill, true);
				player.updateStats();
				player.sendChanges();
				player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1), new SkillList(player));
				player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
			}
			return;
		}
		else if(item.getItemId() == 13552)
		{
			if(player.getLevel() >= SkillSpellbookTable.getMinLevel(item.getItemId()) && player.getRace() == Race.kamael)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(840, 10);
				L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
				player.addSkill(skill, true);
				player.updateStats();
				player.sendChanges();
				player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1), new SkillList(player));
				player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
			}
			return;
		}
		else if(item.getItemId() == 10591)
		{
			if(player.getLevel() >= SkillSpellbookTable.getMinLevel(item.getItemId()) && SkillTreeTable.getInstance().isSkillPossible(player, 784, 1) && item.getCount() >= 1)
			{
				if(player.getSkillLevel(784) <= 0)
				{
					L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
					player.addSkill(SkillTable.getInstance().getInfo(784, 1), true);
					player.addSkill(SkillTable.getInstance().getInfo(785, 1), true);
					player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1));
				}

				player.sendPacket(new SkillList(player));
				player.updateStats();
				player.sendChanges();
				player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
			}
			return;
		}
		else if(item.getItemId() == 10592)
		{
			if(player.getLevel() >= SkillSpellbookTable.getMinLevel(item.getItemId()) && SkillTreeTable.getInstance().isSkillPossible(player, 786, 1) && item.getCount() >= 1)
			{
				if(player.getSkillLevel(786) <= 0)
				{
					L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
					player.addSkill(SkillTable.getInstance().getInfo(786, 1), true);
					player.addSkill(SkillTable.getInstance().getInfo(787, 1), true);
					player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1));
				}

				player.sendPacket(new SkillList(player));
				player.updateStats();
				player.sendChanges();
				player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
			}
			return;
		}
		else if(item.getItemId() == 10593)
		{
			if(player.getLevel() >= SkillSpellbookTable.getMinLevel(item.getItemId()) && SkillTreeTable.getInstance().isSkillPossible(player, 788, 1) && item.getCount() >= 1)
			{
				if(player.getSkillLevel(788) <= 0)
				{
					L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
					player.addSkill(SkillTable.getInstance().getInfo(788, 1), true);
					player.addSkill(SkillTable.getInstance().getInfo(789, 1), true);
					player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1));
				}

				player.sendPacket(new SkillList(player));
				player.updateStats();
				player.sendChanges();
				player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
			}
			return;
		}
		else if(item.getItemId() == 10555 || item.getItemId() == 14170)
		{
			if(player.getLevel() >= SkillSpellbookTable.getMinLevel(item.getItemId()) && SkillTreeTable.getInstance().isSkillPossible(player, 761, 1) && item.getCount() >= 1)
			{
				if(player.getSkillLevel(761) <= 0)
				{
					L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
					player.addSkill(SkillTable.getInstance().getInfo(761, 1), true);
					player.addSkill(SkillTable.getInstance().getInfo(762, 1), true);
					player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1));
				}

				player.sendPacket(new SkillList(player));
				player.updateStats();
				player.sendChanges();
				player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
			}
			return;
		}

		ArrayList<Integer> skill_ids = SkillSpellbookTable.getSpellbookHandlers().get(item.getItemId());
		for(int skill_id : skill_ids)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, 1);
			if(skill == null)
				continue;

			if(player.getSkillLevel(skill_id) > 0)
				continue;

			if(!(skill.isCommon() || SkillTreeTable.getInstance().isSkillPossible(player, skill_id, 1)))
				continue;

			if(player.getLevel() < SkillSpellbookTable.getMinLevel(item.getItemId()))
				return;

			if(item.getCount() < 1)
				return;

			L2ItemInstance ri = player.getInventory().destroyItem(item, 1, true);
			player.addSkill(skill, true);
			player.updateStats();
			player.sendChanges();
			player.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1), new SkillList(player));

			// Анимация изучения книги над головой чара (на самом деле, для каждой книги своя анимация, но они одинаковые)
			player.broadcastSkill(new MagicSkillUse(player, player, 2790, 1, 1, 0), true);
		}
	}

	public int[] getItemIds()
	{
		return _itemIds;
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