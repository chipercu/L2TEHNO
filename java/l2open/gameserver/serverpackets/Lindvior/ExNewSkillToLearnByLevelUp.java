package l2open.gameserver.serverpackets;

public class ExNewSkillToLearnByLevelUp extends L2GameServerPacket
{
    public static final L2GameServerPacket STATIC = new ExNewSkillToLearnByLevelUp();

    @Override
    protected void writeImpl()
	{
		writeEx(0xFD);
    }
}
