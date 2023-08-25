package l2open.gameserver.serverpackets;

import l2open.gameserver.clientpackets.RequestExRequestReceivedPostList;

/**
 * Уведомление о получении почты. При нажатии на него клиент отправляет {@link RequestExRequestReceivedPostList}.
 */
public class ExNoticePostArrived extends L2GameServerPacket
{
	// TODO использовать эти сообщения:
	public static final int RESULT_NOT_ENOUGH_POINTS = -1;
	public static final int RESULT_WRONG_PRODUCT = -2; // also -5
	public static final int RESULT_INVENTORY_FULL = -4;
	public static final int RESULT_SALE_PERIOD_ENDED = -7; // also -8
	public static final int RESULT_WRONG_USER_STATE = -9; // also -11
	public static final int RESULT_WRONG_PRODUCT_ITEM = -10;

	private int _anim;

	public ExNoticePostArrived(int useAnim)
	{
		_anim = useAnim;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0xAA : 0xA9);

		writeD(_anim); // 0 - просто показать уведомление, 1 - с красивой анимацией
	}
}