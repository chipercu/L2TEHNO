
import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import l2open.extensions.scripts.ScriptFile;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.L2GameServerPacket;

public class SendServerTitle extends L2GameServerPacket implements ScriptFile, Runnable {
    private static ScheduledFuture _task = null;
    private static Calendar create = Calendar.getInstance();

    public void runImpl() {
        if (ConfigValue.SendServerTitleTimeResend > 0)
            for (L2Player player : L2ObjectsStorage.getPlayers())
                player.sendPacket(this);
    }

    public SendServerTitle() {
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb0);
        writeC(0xA0);
        writeC(0x01);

        // --------------------------------------------------------------------------------
        writeD(ConfigValue.SendServerTitleX); // Координата Х в клиенте
        writeD(ConfigValue.SendServerTitleY); // Координата Y в клиенте
        writeD(Integer.decode("0x" + ConfigValue.SendServerTitleColor)); // Цвет текста 0xFFBBGGRR
        writeS(ConfigValue.SendServerTitle);  // wide string max len = 25
        // --------------------------------------------------------------------------------
    }

    public void onLoad() {
        if (ConfigValue.SendServerTitleTimeResend > 0)
            _task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SendServerTitle(), 30000L, ConfigValue.SendServerTitleTimeResend);
    }

    public void onReload() {
        if (_task != null) {
            _task.cancel(true);
            _task = null;
        }
    }

    public void onShutdown() {
        if (_task != null) {
            _task.cancel(true);
            _task = null;
        }
    }

    @Override
    public void run() {
		runImpl();
    }
}