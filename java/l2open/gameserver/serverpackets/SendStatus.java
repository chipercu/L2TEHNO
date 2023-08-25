package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.FakePlayersTable;

public final class SendStatus extends L2GameServerPacket
{
    private static final long MIN_UPDATE_PERIOD = 30000;
    private static int online_players = 0;
    private static int max_online_players = 0;
    private static int online_priv_store = 0;
    private static long last_update = 0;

    public SendStatus()
	{
        int i = 0;
        int j = 0;
        for (L2Player player : L2ObjectsStorage.getPlayers())
            if (player != null)
			{
                i++;
                if (player.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
                    j++;
            }
        online_players = i + FakePlayersTable.getFakePlayersCount();
        online_priv_store = j;
		online_players = (online_players * (ConfigValue.SendStatusAddPerOnline+100))/100;
        max_online_players = Math.max(max_online_players, online_players);
		
    }

    @Override
    protected void writeImpl()
	{
      //  if (System.currentTimeMillis() - last_update < MIN_UPDATE_PERIOD)
      //      return;
        last_update = System.currentTimeMillis();
        writeC(0x2E);
        writeD(0x01);
        writeD(max_online_players);
        writeD(online_players + 2);
        writeD(online_players);
        writeD(884);//writeD(online_priv_store);//?
        writeH(48);
        writeH(44);
        writeH(53);
        writeH(49);
        writeH(48);
        writeH(44);
        writeH(55);
        writeH(55);
        writeH(55);
        writeH(53);
        writeH(56);
        writeH(44);
        writeH(54);
        writeH(53);
        writeH(48);
        writeD(54);
        writeD(119);
        writeD(183);
        writeQ(159);
        writeD(0);
        writeH(65);
        writeH(117);
        writeH(103);
        writeH(32);
        writeH(50);
        writeH(57);
        writeH(32);
        writeH(50);
        writeH(48);
        writeH(48);
        writeD(57);
        writeH(48);
        writeH(50);
        writeH(58);
        writeH(52);
        writeH(48);
        writeH(58);
        writeH(52);
        writeD(51);
        writeD(87);
        writeC(17);
        writeC(93);
        writeC(31);
        writeC(96);
    }
}