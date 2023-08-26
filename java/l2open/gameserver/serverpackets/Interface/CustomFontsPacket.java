package l2open.gameserver.serverpackets.Interface;

import java.util.List;

import emudev.managers.CustomFontManager;
import emudev.model.fonts.CustomFont;
import l2open.gameserver.serverpackets.L2GameServerPacket;

public class CustomFontsPacket extends L2GameServerPacket
{
	private List<CustomFont> fontInfos;
	
	public CustomFontsPacket sendFontInfos()
	{
		fontInfos = CustomFontManager.getInstance().getInfos();
		return this;
	}
	
	@Override
	protected void writeImpl() 
	{
		writeEx(0xE9);//ch[0xFE:0xE9]
		writeH(fontInfos.size());
		for(CustomFont info : fontInfos)
		{
			writeS(info.font_name);
			writeS(info.font_file_name);
			writeC(info.loc.ordinal());
			writeC(info.size);
			writeC(info.index);
			writeC(info.index_on);
			writeC(info.use_shadow ? 1 : 0);
			writeC(info.shadow_x);
			writeC(info.shadow_y);
			writeC(info.stroke);
			writeC(info.stroke_large);
			writeH(info.line_gap);
			writeH(info.underline_offset);
		}
	}
}
