package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;

public class NpcInfoPoly extends L2GameServerPacket
{
	//   ddddddddddddddddddffffdddcccccSSddd dddddccffddddccd
	private L2Object _obj;
	private int _x, _y, _z, _heading;
	private int _npcId;
	private boolean _isSummoned, _isRunning, _isInCombat, _isAlikeDead;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private int _rhand, _lhand;
	private String _name, _title;
	private int _abnormalEffect, _abnormalEffect2, team;
	private float colRadius, colHeight;

	public NpcInfoPoly(L2Object cha)
	{
		_obj = cha;
		_npcId = cha.getPolyid();
		L2NpcTemplate _template = NpcTable.getTemplate(_npcId);
		if(_template == null)
			return;
		_rhand = 0;
		_lhand = 0;
		_isSummoned = false;
		colRadius = _template.collisionRadius;
		colHeight = _template.collisionHeight;
		_x = _obj.getX();
		_y = _obj.getY();
		_z = _obj.getZ();

		if(_obj.isCharacter())
		{
			L2Character _cha = (L2Character) cha;
			_rhand = _template.rhand;
			_lhand = _template.lhand;
			_heading = _cha.getHeading();
			_mAtkSpd = (int)_cha.getMAtkSpd();
			_pAtkSpd = (int)_cha.getPAtkSpd();
			_runSpd = _cha.getRunSpeed();
			_walkSpd = _cha.getWalkSpeed();
			_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
			_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
			_isRunning = _cha.isRunning();
			_isInCombat = _cha.isInCombat();
			_isAlikeDead = _cha.isAlikeDead();
			_name = _cha.getName();
			_title = _cha.getTitle();
			_abnormalEffect = _cha.getAbnormalEffect();
			_abnormalEffect2 = _cha.getAbnormalEffect2();
			team = _cha.getTeam();
		}
		else
		{
			_heading = 0;
			_mAtkSpd = 100; //yes, an item can be dread as death
			_pAtkSpd = 100;
			_runSpd = 120;
			_walkSpd = 80;
			_swimRunSpd = _flRunSpd = _flyRunSpd = _runSpd;
			_swimWalkSpd = _flWalkSpd = _flyWalkSpd = _walkSpd;
			_isRunning = _isInCombat = _isAlikeDead = false;
			_name = "item";
			_title = "polymorphed";
			_abnormalEffect = 0;
			_abnormalEffect2 = 0;
			team = 0;
		}
	}

	@Override
	protected final void writeImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		writeC(0x0c);
		//ddddddddddddddddddffffdddcccccSSddddddddccffddddccd
		writeD(_obj.getObjectId());
		writeD(_npcId + 1000000); // npctype id
		if(_obj.isCharacter())
			writeD(_obj.isAutoAttackable(activeChar) ? 1 : 0);
		else
			writeD(1);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(0x00);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd/*0x32*/); // swimspeed
		writeD(_swimWalkSpd/*0x32*/); // swimspeed
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(1.100000023841858); // взято из клиента
		writeF(_pAtkSpd / 277.478340719);
		writeF(colRadius);
		writeF(colHeight);
		writeD(_rhand); // right hand weapon
		writeD(0);
		writeD(_lhand); // left hand weapon
		writeC(1/*_isNameAbove ? 1 : 0*/); // 2.2: name above char 1=true ... ??; 2.3: 1 - normal, 2 - dead
		writeC(_isRunning ? 1 : 0);
		writeC(_isInCombat ? 1 : 0);
		writeC(_isAlikeDead ? 1 : 0);
		writeC(_isSummoned ? 2 : 0); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeS(_name);
		writeS(_title);
		writeD(0);
		writeD(0);
		writeD(0000); // hmm karma ??

		writeD(_abnormalEffect);

		writeD(0000); // C2
		writeD(0000); // C2
		writeD(0000); // C2
		writeD(0000); // C2
		writeC(0000); // C2
		writeC(team);
		writeF(colRadius); // тут что-то связанное с colRadius
		writeF(colHeight); // тут что-то связанное с colHeight
		writeD(0x00); // C4
		writeD(0x00); // как-то связано с высотой
		writeD(0x00);
		writeD(0x00); // maybe show great wolf type ?

		writeC(0x00); //?GraciaFinal
		writeC(0x00); //?GraciaFinal
		writeD(_abnormalEffect2);
	}
}