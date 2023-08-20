package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.AcquireSkillInfo;
import l2open.gameserver.serverpackets.AcquireSkillList;
import l2open.gameserver.serverpackets.ExAcquireSkillInfo;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;

public class RequestAquireSkillInfo extends L2GameClientPacket
{
	// format: cddd
	private int _id;
	private byte _level;
	private int _skillType;

	@Override
	public void readImpl()
	{
		_id = readD();
		_level = (byte) readD();
		_skillType = readD();// normal(0) learn or fisherman(1) clan(2) ? (3) transformation (4)
		//_log.info("RequestAquireSkillInfo: _id="+_id+" _skillType="+_skillType);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getTransformation() != 0 || SkillTable.getInstance().getInfo(_id, _level) == null)
		{
			//_log.info("RequestAquireSkillInfo: 1");
			return;
		}
		L2NpcInstance trainer = activeChar.getLastNpc();
		if((trainer == null || activeChar.getDistance(trainer.getX(), trainer.getY()) > trainer.INTERACTION_DISTANCE+trainer.BYPASS_DISTANCE_ADD) && !activeChar.isGM() && _skillType > AcquireSkillList.OTHER && !getClient().isLindvior() && !ConfigValue.EnableSkillLearnToBbs)
		{
			//_log.info("RequestAquireSkillInfo: 2");
			return;
		}
		if(_skillType == AcquireSkillList.CLAN || _skillType == AcquireSkillList.CLAN_ADDITIONAL)
			sendPacket(new AcquireSkillInfo(_id, _level, activeChar.getClassId(), activeChar.getClan(), _skillType));
		else if((_skillType == AcquireSkillList.USUAL /*|| _skillType <= AcquireSkillList.OTHER*/) && activeChar.isLindvior())
		{
			L2SkillLearn skillLearn = SkillTreeTable.getSkillLearn(_id, _level, ClassId.values()[activeChar.getActiveClassId()], null, false, false, _skillType <= AcquireSkillList.OTHER);
			if(skillLearn == null)
			{
				//_log.info("RequestAquireSkillInfo: 3");
				return;
			}
			sendPacket(new ExAcquireSkillInfo(activeChar, skillLearn));
		}
		else
			sendPacket(new AcquireSkillInfo(_id, _level, activeChar.getClassId(), null, _skillType));
	}
}