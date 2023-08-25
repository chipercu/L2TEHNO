package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;

public class RequestMagicSkillUse extends L2GameClientPacket
{
	private Integer _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	/**
	 * packet type id 0x39
	 * format:		cddc
	 */
	@Override
	public void readImpl()
	{
		_magicId = readD();
		_ctrlPressed = readD() != 0;
		_shiftPressed = readC() != 0;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;
			
		activeChar.setActive();
		if(System.currentTimeMillis() - activeChar.getLastRequestMagicSkillUsePacket() < ConfigValue.RequestMagicSkillUsePacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestMagicSkillUsePacket();

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Skill skill = SkillTable.getInstance().getInfo(_magicId, activeChar.getSkillLevel(_magicId));
		if(skill != null)
		{
			if(!skill.isActive() && !skill.isToggle())
				return;

			// В режиме трансформации доступны только скилы трансформы
			if(activeChar.getTransformation() != 0 && !activeChar.getAllSkills().contains(skill))
				return;

			if(skill.isToggle())
				if(activeChar.getEffectList().getEffectsBySkill(skill) != null)
				{
					if(skill.getId() != 226)
						activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_ABORTED).addSkillName(skill.getId(), skill.getLevel()));
					activeChar.getListeners().onMagicUse(skill, activeChar, false);
					activeChar.getEffectList().stopEffect(skill.getId());
					activeChar.sendActionFailed();
					return;
				}

			if(ConfigValue.DisableSkillOnJail && activeChar.getVar("jailed") != null)
				return;

			L2Character target = skill.getAimingTarget(activeChar, activeChar.getTarget());

			activeChar.setGroundSkillLoc(null);
			activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
		}
		else
			activeChar.sendActionFailed();
	}

	public String getType()
	{
		return "[C] " + getClass().getSimpleName()+"["+_magicId+"]["+_ctrlPressed+"]["+_shiftPressed+"]";
	}
}