package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;

/**
 * @author VISTALL
 */
public class RequestExBR_LectureMark extends L2GameClientPacket
{
	public static final int INITIAL_MARK = 1;
	public static final int EVANGELIST_MARK = 2;
	public static final int OFF_MARK = 3;

	private int _mark;

	@Override
	protected void readImpl() throws Exception
	{
		_mark = readC();
	}

	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		if(System.currentTimeMillis() - player.getLastRequestExBR_LectureMarkPacket() < ConfigValue.RequestExBR_LectureMarkPacketDelay)
		{
			player.sendActionFailed();
			return;
		}
		player.setLastRequestExBR_LectureMarkPacket();

		switch(_mark)
		{
			case INITIAL_MARK:
			case EVANGELIST_MARK:
			case OFF_MARK:
				//TODO [VISTALL] проверить ли можно включать - от первого чара 6 месяцев
				player.setLectureMark(_mark);
				player.broadcastUserInfo(true);
				break;
		}
	}
}