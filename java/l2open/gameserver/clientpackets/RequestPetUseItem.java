package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;

public class RequestPetUseItem extends L2GameClientPacket
{
	private int _objectId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		L2PetInstance pet = (L2PetInstance) activeChar.getPet();
		if(pet == null)
			return;

		L2ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);

		if(item == null || item.getCount() <= 0)
			return;

		activeChar.setActive();

		if(activeChar.isAlikeDead() || pet.isDead())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return;
		}

		if(!item.getOlympiadUse() && activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		else if(activeChar.getEventMaster() != null && !activeChar.getEventMaster().canUseItem(activeChar, item))
		{
			//activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return;
		}

		final byte event_id = activeChar.isInEvent();
		if(event_id != 0)
		{
			boolean check = false;

			if(event_id == 1)
			{
				if(ConfigValue.FightClubForbiddenItems.length > 0 && DifferentMethods.findInIntList(ConfigValue.FightClubForbiddenItems, item.getItemId()))
					check = true;

				if(ConfigValue.FightClubOlympiadItems && !item.getOlympiadUse())
					check = true;
			}
			else if(event_id == 2)
			{
				if(ConfigValue.LastHeroForbiddenItems.length > 0 && DifferentMethods.findInIntList(ConfigValue.LastHeroForbiddenItems, item.getItemId()))
					check = true;

				if(ConfigValue.LastHeroOlympiadItems && !item.getOlympiadUse())
					check = true;
			}
			else if(event_id == 3)
			{
				if(ConfigValue.CaptureTheFlagForbiddenItems.length > 0 && DifferentMethods.findInIntList(ConfigValue.CaptureTheFlagForbiddenItems, item.getItemId()))
					check = true;

				if(ConfigValue.CaptureTheFlagOlympiadItems && !item.getOlympiadUse())
					check = true;
			}
			else if(event_id == 4)
			{
				if(ConfigValue.TeamvsTeamForbiddenItems.length > 0 && DifferentMethods.findInIntList(ConfigValue.TeamvsTeamForbiddenItems, item.getItemId()))
					check = true;

				if(ConfigValue.TeamvsTeamOlympiadItems && !item.getOlympiadUse())
					check = true;
			}
			else if(event_id == 5)
			{
				if(ConfigValue.Tournament_ForbiddenItems.length > 0 && DifferentMethods.findInIntList(ConfigValue.Tournament_ForbiddenItems, item.getItemId()))
					check = true;

				if(ConfigValue.Tournament_OlympiadItems && !item.getOlympiadUse())
					check = true;
			}
			else if(event_id == 6)
			{
				if(ConfigValue.CubicLohForbiddenItems.length > 0 && DifferentMethods.findInIntList(ConfigValue.CubicLohForbiddenItems, item.getItemId()))
					check = true;

				if(ConfigValue.CubicLohOlympiadItems && !item.getOlympiadUse())
					check = true;
			}

			if(check)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
				return;
			}
		}

		if(pet.tryEquipItem(item, true))
			return;

		// manual pet feeding
		if(pet.tryFeedItem(item, true))
			return;

		L2Skill[] skills = item.getItem().getAttachedSkills();
		if(item.getItem().isConsume())
			if(item == null || item.getCount() < 1)
				return;

		int item_disable_id;
		int grp_id = item.getItem().delay_share_group;

		if(grp_id > 0)
			item_disable_id = (grp_id * 65536 + item.getItemId()) * -1;
		else
		{
			item_disable_id = item.getItemId() * -1;
			grp_id = item.getItemId();
		}

		if(item.getItem().immediate_effect == 1 && skills != null && skills.length > 0)
		{
			if(!pet.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? item_disable_id*65536L+1 : item_disable_id))
			{
				boolean _consume = false;
				for(L2Skill skill : skills)
				{
					L2Character aimingTarget = skill.getAimingTarget(pet, pet.getTarget());
					int condResult = skill.condition(pet, aimingTarget, true);
					if(condResult == -1)
						return;
					if(item.getItem().isConsume() && !_consume)
					{
						if(pet.getInventory().destroyItem(item, 1, false) == null)
							return;
						_consume = true;
					}
					pet.altUseSkill(skill, aimingTarget, false);
					pet.disableItem(item_disable_id, 1/*skill.getLevel()*/, item.getItemId(), grp_id, item.getReuseDelay(), item.getReuseDelay());
				}
			}
		}
		else
			activeChar.sendPacket(Msg.ITEM_NOT_AVAILABLE_FOR_PETS);
	}
}