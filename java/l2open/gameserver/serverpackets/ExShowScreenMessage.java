package l2open.gameserver.serverpackets;

public class ExShowScreenMessage extends L2GameServerPacket
{
	public static enum ScreenMessageAlign
	{
		TOP_LEFT, // 1 - Вверху, слева.
		TOP_CENTER, // 2 - Вверху, по-центру.
		TOP_RIGHT, // 3 - Вверху, справа.
		MIDDLE_LEFT, // 4 - По-центру, слева.
		MIDDLE_CENTER, // 5 - В центе.
		MIDDLE_RIGHT, // 6 - По-центру, справа.
		BOTTOM_CENTER, // 7 - Внизу по-центру.
		BOTTOM_RIGHT, // 8 - Внизу справа.
	}

	private int _type, _sysMessageId;
	private boolean _big_font, _effect;
	private ScreenMessageAlign _text_align;
	private int _clientMessageId = -1;
	private int _time;

	private int _unk1 = 0;
	private int _unk2 = 0;
	private int _unk3 = 0;
	private int _unk4 = 1;

	private final String[] _parameters = new String[5];

	public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font)
	{
		_type = 1;
		_sysMessageId = -1;
		_time = time;
		_text_align = text_align;
		_big_font = big_font;
		_effect = false;
		_parameters[0] = text;
	}

	public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align)
	{
		this(text, time, text_align, true);
	}

	public ExShowScreenMessage(String text, int time)
	{
		this(text, time, ScreenMessageAlign.MIDDLE_CENTER);
	}

	public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font, int type, int messageId, boolean showEffect)
	{
		_type = type;
		_sysMessageId = messageId;
		_time = time;
		_text_align = text_align;
		_big_font = big_font;
		_effect = showEffect;
		_parameters[0] = text;
	}

	public ExShowScreenMessage(int messageId, int time, ScreenMessageAlign text_align, boolean big_font, int type, boolean showEffect)
	{
		_type = type;
		_sysMessageId = -1;
		_time = time;
		_text_align = text_align;
		_big_font = big_font;
		_effect = showEffect;
		_clientMessageId = messageId;
	}

	public ExShowScreenMessage(int messageId, int time, ScreenMessageAlign text_align, boolean big_font, boolean showEffect, String... params)
	{
		_type = 1;
		_sysMessageId = -1;
		_time = time;
		_text_align = text_align;
		_big_font = big_font;
		_effect = showEffect;
		_clientMessageId = messageId;
		System.arraycopy(params, 0, _parameters, 0, params.length);
	}

	public ExShowScreenMessage(int messageId, int time, ScreenMessageAlign text_align, boolean big_font, int type, boolean showEffect, String... params)
	{
		_type = type;
		_sysMessageId = -1;
		_time = time;
		_text_align = text_align;
		_big_font = big_font;
		_effect = showEffect;
		_clientMessageId = messageId;
		System.arraycopy(params, 0, _parameters, 0, params.length);
	}

	public ExShowScreenMessage(int clientMsgId, int time, ScreenMessageAlign text_align, boolean big_font, int type, int messageId, boolean showEffect)
	{
		_type = type;
		_sysMessageId = messageId;
		_time = time;
		_text_align = text_align;
		_big_font = big_font;
		_effect = showEffect;
		_clientMessageId = clientMsgId;
	}

	public ExShowScreenMessage(String text, int time, ScreenMessageAlign text_align, boolean big_font, int type, int messageId, boolean showEffect, int unk1, int unk2, int unk3, int unk4)
	{
		this(text, time, text_align, big_font, type, messageId, showEffect);
		_unk1 = unk1;
		_unk2 = unk2;
		_unk3 = unk3;
		_unk4 = unk4;
	}

	public ExShowScreenMessage(int nPosition, int nUnk1, int nSize, int nUnk2, int nUnk3, int nEffect, int nTime, int nUnk4, String pwsMsg)
	{
		switch(nPosition)
		{
			case 1:
				_text_align = ScreenMessageAlign.TOP_LEFT;
				break;
			case 2:
				_text_align = ScreenMessageAlign.TOP_CENTER;
				break;
			case 3:
				_text_align = ScreenMessageAlign.TOP_RIGHT;
				break;
			case 4:
				_text_align = ScreenMessageAlign.MIDDLE_LEFT;
				break;
			case 5:
				_text_align = ScreenMessageAlign.MIDDLE_CENTER;
				break;
			case 6:
				_text_align = ScreenMessageAlign.MIDDLE_RIGHT;
				break;
			case 7:
				_text_align = ScreenMessageAlign.BOTTOM_CENTER;
				break;
			case 8:
				_text_align = ScreenMessageAlign.BOTTOM_RIGHT;
				break;
		}

		_type = 1;
		_sysMessageId = -1;
		_big_font = nSize == 0;
		_effect = nEffect == 1;
		_time = nTime;
		_parameters[0] = pwsMsg;
		_unk1 = nUnk1;
		_unk2 = nUnk2;
		_unk3 = nUnk3;
		_unk4 = nUnk4;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x39);

		writeD(_type); // 0 - system messages, 1 - your defined text
		writeD(_sysMessageId); // system message id (_type must be 0 otherwise no effect)
		writeD(_text_align.ordinal() + 1); // размещение текста
		writeD(_unk1); // ?
		writeD(_big_font ? 0 : 1); // размер текста 0 - нормальный, 1 - маленький
		writeD(_unk2); // ?
		writeD(_unk3); // ?
		writeD(_effect == true ? 1 : 0); // upper effect (0 - disabled, 1 enabled) - _position must be 2 (center) otherwise no effect
		writeD(_time); // время отображения сообщения в милисекундах
		writeD(_unk4); // ?
		writeD(_clientMessageId);// NpcStrings
		for(String _text : _parameters)
			writeS(_text); // текст сообщения
	}
}