package l2open.gameserver.serverpackets;

import java.security.InvalidParameterException;

public class RegisterStringPacket extends L2GameServerPacket
{
	private final int _Id;
	private final String _text;
	private final int _font;
	private final int _pdMsg;
	private final int _color_a;
	private final int _color_r;
	private final int _color_g;
	private final int _color_b;
	private final int _screenPos;
	private final int _style;
	private short offsetX = 0;
	private short offsetY = 0;
	private final int _fadeInMs;
	private final int _showMs;
	private final int _fadeOutMs;

	public RegisterStringPacket(int Id, String text, int font, int pdMsg, int[] color, int screenPos, int style, int fadeInMs, int showMs, int fadeOutMs)
	{
		_Id = Id;
		/*if(_text == null || _text.length() == 0)
			throw new InvalidParameterException("Text can not be null or empty.");
		if(_text.length() > 2048)
			throw new InvalidParameterException("Text too long, please specify up to 2048 characters.");
		if(_fadeInMs < 0 || _showMs < 0 || _fadeOutMs < 0)
			throw new InvalidParameterException();*/

		_text = text;
		_font = font;
		_pdMsg = pdMsg;
		_color_a = color[0];
		_color_r = color[1];
		_color_g = color[2];
		_color_b = color[3];
		_screenPos = screenPos;
		_style = style;
		_fadeInMs = fadeInMs;
		_showMs = showMs;
		_fadeOutMs = fadeOutMs;
	}

	public short getOffsetY()
	{
		return offsetY;
	}

	public void setOffsetY(short offsetY)
	{
		offsetY = offsetY;
	}

	public short getOffsetX()
	{
		return offsetX;
	}

	public void setOffsetX(short offsetX)
	{
		offsetX = offsetX;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFF);
		writeC(0x02);

		writeD(_Id);
		writeS(_text);
		writeH(_font); // Small,  Normal,  Large,  VeryLarge,  Giant
		writeH(_pdMsg); // None, FPS
		writeC(_color_a);
		writeC(_color_r);
		writeC(_color_g);
		writeC(_color_b);
		writeD(_screenPos); // TopRightRelative(1024),  TopRight(2),  TopLeft(0),  TopCenter(1),  MiddleRight(6),  MiddleLeft(4),  MiddleCenter(5),  BottomRight(10),  BottomLeft(8),  BottomCenter(9)
		writeD(_style); // Normal,  Shadowed
		writeD(_fadeInMs);
		writeD(_showMs);
		writeD(_fadeOutMs);
		if(_screenPos == 1024)
		{
			writeH(offsetX);
			writeH(offsetY);
		}
	}
}
