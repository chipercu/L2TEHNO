package l2open.gameserver.serverpackets.Interface;

import java.util.List;

import emudev.managers.ScreenTextInfoManager;
import emudev.model.ScreenTextInfo;
import l2open.gameserver.serverpackets.L2GameServerPacket;

public class ScreenTextInfoPacket extends L2GameServerPacket
{
	private List<ScreenTextInfo> textInfos;
	private ScreenTextInfo textInfo;
	private int type;
	private int _index;
	
	public ScreenTextInfoPacket sendTextInfos()
	{
		textInfos = ScreenTextInfoManager.getInstance().getInfos();
		type = textInfos.size();
		return this;
	}
	
	public ScreenTextInfoPacket updateTextInfo(int index, ScreenTextInfo info)
	{
		type = 9999;
		_index = index;
		textInfo = info;
		return this;
	}
	
	@Override
	protected void writeImpl() 
	{
		writeEx(0xEA);//ch[0xFE:0xEA]
		writeH(type);
		if(type < 9999)
		{
			for(ScreenTextInfo info : textInfos)
			{
				writeH(info.id);
				writeC(info.enabled == true ? 1 : 0);
				writeC(info.type.ordinal());
				writeS(info.text_en);
				writeS(info.text_ru);
				writeS(info.font_name);
				writeD(info.font_color);
				writeC(info.anchor_point.ordinal());
				writeC(info.relative_point.ordinal());
				writeH(info.offset_x);
				writeH(info.offset_y);
				writeC(info.alpha);
			}
		}
		else if(type == 9999)
		{
			writeH(_index);
			writeC(textInfo.enabled == true ? 1 : 0);
			writeC(textInfo.type.ordinal());
			writeS(textInfo.text_en);
			writeS(textInfo.text_ru);
			writeS(textInfo.font_name);
			writeD(textInfo.font_color);
			writeC(textInfo.anchor_point.ordinal());
			writeC(textInfo.relative_point.ordinal());
			writeH(textInfo.offset_x);
			writeH(textInfo.offset_y);
			writeC(textInfo.alpha);
		}
	}
}
