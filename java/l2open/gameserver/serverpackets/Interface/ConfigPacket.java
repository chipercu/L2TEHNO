//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package l2open.gameserver.serverpackets.Interface;

import emudev.managers.InterfaceSettingManager;
import emudev.model.InterfaceSetting;
import java.util.Iterator;
import java.util.List;
import l2open.gameserver.serverpackets.L2GameServerPacket;

public class ConfigPacket extends L2GameServerPacket {
	private List<InterfaceSetting> settings = InterfaceSettingManager.getInstance().getSettings();

	public ConfigPacket() {
	}

	protected final void writeImpl() {
		this.writeEx(246);
		this.writeH(this.settings.size());
		Iterator var1 = this.settings.iterator();

		while(true) {
			label66:
			while(var1.hasNext()) {
				InterfaceSetting s = (InterfaceSetting)var1.next();
				this.writeS(s.Name);
				this.writeC(s.Type.ordinal());
				int i;
				switch (s.Type) {
					case TYPE_CHAR:
						this.writeC(s.CharValue);
						break;
					case TYPE_SHORT:
						this.writeH(s.ShortValue);
						break;
					case TYPE_INT:
						this.writeD(s.IntValue);
						break;
					case TYPE_LONG:
						this.writeQ(s.LongValue);
						break;
					case TYPE_DOUBLE:
						this.writeF(s.DoubleValue);
						break;
					case TYPE_TEXT:
						this.writeS(s.TextValue);
						break;
					case TYPE_ARR_CHAR:
						this.writeD(s.ArrSize);
						i = 0;

						while(true) {
							if (i >= s.ArrSize) {
								continue label66;
							}

							this.writeC(s.CharValueArr[i]);
							++i;
						}
					case TYPE_ARR_SHORT:
						this.writeD(s.ArrSize);
						i = 0;

						while(true) {
							if (i >= s.ArrSize) {
								continue label66;
							}

							this.writeH(s.ShortValueArr[i]);
							++i;
						}
					case TYPE_ARR_INT:
						this.writeD(s.ArrSize);
						i = 0;

						while(true) {
							if (i >= s.ArrSize) {
								continue label66;
							}

							this.writeD(s.IntValueArr[i]);
							++i;
						}
					case TYPE_ARR_LONG:
						this.writeD(s.ArrSize);
						i = 0;

						while(true) {
							if (i >= s.ArrSize) {
								continue label66;
							}

							this.writeQ(s.LongValueArr[i]);
							++i;
						}
					case TYPE_ARR_DOUBLE:
						this.writeD(s.ArrSize);
						i = 0;

						while(true) {
							if (i >= s.ArrSize) {
								continue label66;
							}

							this.writeF(s.DoubleValueArr[i]);
							++i;
						}
					case TYPE_ARR_TEXT:
						this.writeD(s.ArrSize);

						for(i = 0; i < s.ArrSize; ++i) {
							this.writeS(s.TextValueArr[i]);
						}
				}
			}

			return;
		}
	}
}
