package commands.voiced;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.skillclasses.Call;

public class Relocate extends Functions implements IVoicedCommandHandler, ScriptFile {
    public static int SUMMON_PRICE = 5;

    private final String[] _commandList = new String[]{"km-all-to-me"};

    public String[] getVoicedCommandList() {
        return _commandList;
    }

    public boolean useVoicedCommand(String command, L2Player activeChar, String target) {
        if (command.equalsIgnoreCase("km-all-to-me")) {
            if (!activeChar.isClanLeader()) {
                activeChar.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
                return false;
            }
            SystemMessage msg = Call.canSummonHere(activeChar);
            if (msg != null) {
                activeChar.sendPacket(msg);
                return false;
            } else if (activeChar.isAlikeDead()) {
                activeChar.sendMessage(new CustomMessage("scripts.commands.voiced.Relocate.Dead", activeChar));
                return false;
            } else if (activeChar.getVarLong("KmAllToMe", 0L) > System.currentTimeMillis()) {
                activeChar.sendMessage("Команду можно использовать раз в " + ConfigValue.KmAllToMeReuse + " секунд.");
                return false;
            }

            activeChar.setVar("KmAllToMe", String.valueOf(System.currentTimeMillis() + ConfigValue.KmAllToMeReuse * 1000));
            L2Player[] clan = activeChar.getClan().getOnlineMembers(activeChar.getObjectId());

            for (L2Player pl : clan) {
                if (Call.canBeSummoned(pl) == null && activeChar.getReflectionId() == pl.getReflectionId()) {
                    // Спрашиваем, согласие на призыв
                    pl.summonCharacterRequest(activeChar, GeoEngine.findPointToStayPet(activeChar.getPlayer(), 100, 150, activeChar.getReflection().getGeoIndex()), SUMMON_PRICE);
                }
            }

            return true;
        }
        return false;
    }

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }
}