package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class ExBrExtraUserInfo extends L2GameServerPacket
{
	private int _charId;
	private String _name;
	//private int _abnormalEffect;
	//private int _abnormalEffect2;
	private int _abnormalEffect3;
	private int _lectureMark;

	public ExBrExtraUserInfo(L2Player cha)
	{
		_charId = cha.getObjectId();
		//_abnormalEffect = cha.getAbnormalEffect();
		//_abnormalEffect2 = cha.getAbnormalEffect2();
		_abnormalEffect3 = cha.getAbnormalEffect3();
		_lectureMark = cha.getLectureMark();
		_name = cha.getName();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0xDA);
		writeD(_charId);

		writeD(_abnormalEffect3); // event effect id
		writeC(_lectureMark);
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xFE);
		writeH(0xDB);
		writeD(_charId);
		writeD(0x00);
		writeC(_lectureMark);
		return true;
	}

	public String getType()
	{
        return "[S] ExBrExtraUserInfo["+_name+"]";
    }
}