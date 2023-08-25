package l2open.gameserver.model.items.listeners;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;

public final class BraceletListener implements PaperdollListener
{
	private Inventory _inv;

	public BraceletListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		L2Player owner = (L2Player) _inv.getOwner();

		if(item.getBodyPart() == L2Item.SLOT_L_BRACELET && item.getItem().getAttachedSkills() != null)
		{
			int agathionId = owner.getAgathion() == null ? 0 : owner.getAgathion().getId();
			int transformNpcId = owner.getTransformationTemplate();
			for(L2Skill skill : item.getItem().getAttachedSkills())
			{
				if(agathionId > 0 && skill.getNpcId() == agathionId)
					owner.setAgathion(0);
				if(skill.getNpcId() == transformNpcId && skill.isTransformation())
				{
					owner.setTransformation(0);
					for(L2Skill sk : item.getItem().getAttachedSkills())
						if(owner != null && owner.getEffectList().getEffectsBySkill(sk) != null)
						{
							for(L2Effect ef : owner.getEffectList().getEffectsBySkill(sk))
								ef.exit(false, false);
						}
				}
			}
			if(update_icon)
				owner.updateEffectIcons();
			owner.getPlayer().removeSkillById(L2Skill.SKILL_DISMISS_AGATHION); // Удаляем скилл Dismiss Agathion
		}
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		if(item.getBodyPart() == L2Item.SLOT_L_BRACELET && item.getItem().getAttachedSkills() != null)
			_inv.getOwner().getPlayer().addSkill(SkillTable.getInstance().getInfo(L2Skill.SKILL_DISMISS_AGATHION, 1), false); // Выдаем скилл Dismiss Agathion
	}
}