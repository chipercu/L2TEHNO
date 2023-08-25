package l2open.gameserver.model.items.listeners;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.skills.triggers.TriggerInfo;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.OptionDataTemplate;
import l2open.gameserver.xml.loader.XmlOptionDataLoader;

public final class ItemEnchantListener implements PaperdollListener
{
	private Inventory _inv;

	public ItemEnchantListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(!item.isEquipable())
			return;

		L2Player player = _inv.getOwner().getPlayer();

		boolean needSendInfo = false;
		for(int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(i);
			if(template == null)
				continue;

			player.removeStatsOwner(template);
			for(L2Skill skill : template.getSkills())
			{
				player.removeSkill(skill, false, false);
				needSendInfo = true;
			}
			for(TriggerInfo triggerInfo : template.getTriggerList())
				player.removeTrigger(triggerInfo);
		}

		if(needSendInfo)
		{
			if(update_icon)
				player.updateEffectIcons();
			player.sendPacket(new SkillList(player));
		}
		player.sendChanges();
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(!item.isEquipable())
			return;
		L2Player player = _inv.getOwner().getPlayer();

		boolean needSendInfo = false;
		for(int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(i);
			if(template == null)
				continue;

			player.addStatFuncs(template.getStatFuncs(template));
			for(L2Skill skill : template.getSkills())
			{
				player.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), false);
				needSendInfo = true;
			}
			for(TriggerInfo triggerInfo : template.getTriggerList())
				player.addTrigger(triggerInfo);
		}

		if(needSendInfo)
			player.sendPacket(new SkillList(player));
		player.sendChanges();
	}
}