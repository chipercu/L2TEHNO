//package Interface.impl;
package l2open.gameserver.serverpackets.custom;

import java.util.List;

import emudev.model.InterfaceSetting;
import emudev.managers.InterfaceSettingManager;
import l2open.gameserver.serverpackets.L2GameServerPacket;

public class ConfigPacket extends L2GameServerPacket {
	private List<InterfaceSetting> settings;
	
	public ConfigPacket() 
	{
		settings = InterfaceSettingManager.getInstance().getSettings();
	}
	
	@Override
	protected final void writeImpl() 
	{
		writeEx(0xF6);
		writeH(settings.size());
		for(InterfaceSetting s : settings)
		{
			writeS(s.Name);
			writeC(s.Type.ordinal());
			switch(s.Type)
			{
				case TYPE_CHAR:
					writeC(s.CharValue);
					break;
				case TYPE_SHORT:
					writeH(s.ShortValue);
					break;
				case TYPE_INT:
					writeD(s.IntValue);
					break;
				case TYPE_LONG:
					writeQ(s.LongValue);
					break;
				case TYPE_DOUBLE:
					writeF(s.DoubleValue);
					break;
				case TYPE_TEXT:
					writeS(s.TextValue);
					break;					
				case TYPE_ARR_CHAR:
					writeD(s.ArrSize);
					for(int i = 0; i < s.ArrSize; i++)
						writeC(s.CharValueArr[i]);
					break;
				case TYPE_ARR_SHORT:
					writeD(s.ArrSize);
					for(int i = 0; i < s.ArrSize; i++)
						writeH(s.ShortValueArr[i]);
					break;
				case TYPE_ARR_INT:
					writeD(s.ArrSize);
					for(int i = 0; i < s.ArrSize; i++)
						writeD(s.IntValueArr[i]);
					break;
				case TYPE_ARR_LONG:
					writeD(s.ArrSize);
					for(int i = 0; i < s.ArrSize; i++)
						writeQ(s.LongValueArr[i]);
					break;
				case TYPE_ARR_DOUBLE:
					writeD(s.ArrSize);
					for(int i = 0; i < s.ArrSize; i++)
						writeF(s.DoubleValueArr[i]);
					break;
				case TYPE_ARR_TEXT:
					writeD(s.ArrSize);
					for(int i = 0; i < s.ArrSize; i++)
						writeS(s.TextValueArr[i]);
					break;
			}
		}
    }
}
