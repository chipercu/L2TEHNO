package l2open.loginserver;

import l2open.common.ThreadPoolManager;
import l2open.extensions.network.*;
import l2open.loginserver.L2LoginClient.LoginClientState;
import l2open.loginserver.clientpackets.*;
import l2open.loginserver.serverpackets.Init;
import l2open.loginserver.serverpackets.LoginFail.LoginFailReason;
import l2open.util.Util;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public final class L2LoginPacketHandler implements IPacketHandler<L2LoginClient>, IClientFactory<L2LoginClient>, IMMOExecutor<L2LoginClient> {
    private static final Logger _log = Logger.getLogger(L2LoginPacketHandler.class.getName());

    // fi
    @Override
    public L2LoginClient create(MMOConnection<L2LoginClient> con) {
        if (LoginController.getInstance().isBannedAddress(con.getSocket().getInetAddress()))
            return null;
        L2LoginClient client = new L2LoginClient(con);
        client.sendPacket(new Init(client));
        return client;
    }

    // fi
    @Override
    public void execute(Runnable r) {
        ThreadPoolManager.getInstance().execute(r);
    }

    @Override
    public ReceivablePacket<L2LoginClient> handlePacket(ByteBuffer buf, L2LoginClient client) {



        int opcode = buf.get() & 0xFF;

        ReceivablePacket<L2LoginClient> packet = null;
        LoginClientState state = client.getState();

        switch (state) {
            // B4
            case CONNECTED:
                if (opcode == 0x07)
                    packet = new AuthGameGuard();
                else if (opcode == 0xF0)
                    ;
                else
                    debugOpcode(opcode, state, client, buf);
                break;
            case AUTHED_GG:
                if (opcode == 0x00)
                    packet = new RequestAuthLogin();
                else if (opcode == 0xF0)
                    ;
                else if (opcode == 0x0B)
                    packet = new RequestCmdLogin();
                else if (opcode != 0x05) //на случай когда клиент зажимает ентер
                    debugOpcode(opcode, state, client, buf);
                break;
            case AUTHED_LOGIN:
                if (opcode == 0x05)
                    packet = new RequestServerList();
                else if (opcode == 0x02)
                    packet = new RequestServerLogin();
                else
                    debugOpcode(opcode, state, client, buf);
                break;
            case FAKE_LOGIN:
                if (opcode == 0x05)
                    packet = new RequestServerList();
                else if (opcode == 0x02) {
                    packet = new RequestServerLogin();
                    client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
                } else
                    debugOpcode(opcode, state, client, buf);
                break;
        }
        return packet;
    }

    private void debugOpcode(int opcode, LoginClientState state, L2LoginClient client, ByteBuffer buf) {
        int sz = buf.remaining();
        byte[] arr = new byte[sz];
        buf.get(arr);
        _log.warning("Unknown Opcode: " + opcode + " for state: " + state.name() + " from IP: " + client);
        _log.warning(Util.printData(arr, sz));
    }
}