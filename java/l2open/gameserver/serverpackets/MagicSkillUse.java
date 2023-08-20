package l2open.gameserver.serverpackets;

import l2open.config.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;

/**
 * Format:   dddddddddh [h] h [ddd]
 * Пример пакета:
 * 48
 * 86 99 00 4F  86 99 00 4F
 * EF 08 00 00  01 00 00 00
 * 00 00 00 00  00 00 00 00
 * F9 B5 FF FF  7D E0 01 00  68 F3 FF FF
 * 00 00 00 00
 */
public class MagicSkillUse extends L2GameServerPacket
{
	private int _targetId;
	private int _skillId;
	private int _skillLevel;
	private int _hitTime;
	private long _reuseDelay;
	private int _chaId, _x, _y, _z, _tx, _ty, _tz;

	public int getSkillId()
	{
		return _skillId;
	}

	public MagicSkillUse(L2Character cha, L2Character target, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		_chaId = cha.getObjectId();
		_targetId = target.getObjectId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = reuseDelay;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
		if(ConfigValue.SayCastingSkillName && cha.isNpc())
		{
			L2Skill sk = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
			Functions.npcSay((L2NpcInstance) cha, "Casting " + sk.getName() + "[" + _skillId + "." + _skillLevel + "]");
		}
	}

	public MagicSkillUse(L2Character cha, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		_chaId = cha.getObjectId();
		_targetId = cha.getTargetId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = reuseDelay;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = cha.getX();
		_ty = cha.getY();
		_tz = cha.getZ();
		if(ConfigValue.SayCastingSkillName && cha.isNpc())
		{
			L2Skill sk = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
			Functions.npcSay((L2NpcInstance) cha, "Casting " + sk.getName() + "[" + _skillId + "." + _skillLevel + "]");
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x48);
		writeD(_chaId);
		writeD(_targetId);
		writeD(_skillId);
		writeD(_skillLevel);
		writeD(_hitTime);
		writeD((int) _reuseDelay);
		writeD(_x);
		writeD(_y);
		writeD(_z);

		writeH(0x00); // количество елементов чего-то [h]
		writeH(0x00); // количество елементов чего-то [ddd]

		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x48);
		writeD(0x00); // _isDoubleCasting
		writeD(_chaId);
		writeD(_targetId);
		writeC(0x00); // L2WT GOD
		writeD(_skillId);
		writeD(_skillLevel);
		writeD(_hitTime);
		writeD(-1); // getSkillReplace
		writeD((int) _reuseDelay);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(0x00); // L2WT GOD
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
		return true;
	}
}