package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.ImagesChache;
import l2open.gameserver.model.*;
import l2open.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ShowBoard extends L2GameServerPacket {
    private static Logger _log = Logger.getLogger(ShowBoard.class.getName());
    private String _htmlCode;
    private String _id;
    private int _open = 1;
    private GArray<String> _arg;

    static final ShowBoard CACHE_NULL_102 = new ShowBoard(null, "102"), CACHE_NULL_103 = new ShowBoard(null, "103");

    public static void separateAndSend(String html, L2Player activeChar) {
        activeChar.cleanBypasses(true, false);

        for (String htm : ConfigValue.CommunityBoardReplaceHtm) {
            String ht = Files.read(ConfigValue.CommunityBoardHtmlRoot + htm + ".htm", activeChar);
            if (ht != null && !ht.isEmpty())
//                html = html.replace("<?"+htm+"?>", activeChar.encodeBypasses(ht, true, false));
                html = html.replace("<?" + htm + "?>", ht);
        }

        try {
            html = html.replace("%menu%", Files.read(ConfigValue.CommunityBoardHtmlRoot + "menu.htm", activeChar));
        } catch (Exception ignored) {
        }
        if (activeChar.isLangRus()) {
            html = html.replace("<?en_stat?>", "on");
            html = html.replace("<?ru_stat?>", "off");
        } else {
            html = html.replace("<?en_stat?>", "off");
            html = html.replace("<?ru_stat?>", "on");
        }
        String menuAll = Files.read(ConfigValue.CommunityBoardHtmlRoot + "menuAll.htm", activeChar);
        if (menuAll != null && !menuAll.isEmpty())
            html = activeChar.encodeBypasses("<html><center>" + menuAll + "</center>" + html, true, false);
        else
            html = activeChar.encodeBypasses(html, true, false);


        html = html.replace("<?copyright?>", ConfigValue.Copyright);

        Matcher m = ImagesChache.HTML_PATTERN.matcher(html);
        while (m.find()) {
            String image_name = m.group(1);
            int image_id = ImagesChache.getInstance().getImageId(image_name);
            html = html.replaceAll("%img:" + image_name + "%", "Crest.crest_" + ConfigValue.RequestServerID + "_" + image_id);
            byte[] image = ImagesChache.getInstance().getImage(image_id);
            if (image != null)
                activeChar.sendPacket(new PledgeCrest(image_id, image));
        }

        if (html.length() < 10240)
            activeChar.sendPacket(new ShowBoard(html, "101"), CACHE_NULL_102, CACHE_NULL_103);
        else if (html.length() < 10240 * 2)
            activeChar.sendPacket(new ShowBoard(html.substring(0, 10240), "101"), new ShowBoard(html.substring(10240, html.length()), "102"), CACHE_NULL_103);
        else if (html.length() < 10240 * 3)
            activeChar.sendPacket(new ShowBoard(html.substring(0, 10240), "101"), new ShowBoard(html.substring(10240, 10240 * 2), "102"), new ShowBoard(html.substring(10240 * 2, html.length()), "103"));
        else
            // Устаревшая инфа, клиент больше не критует...Макс размер я конечн хз, но 30к+ без проблем принимает...
            // 51709/52214
            activeChar.sendPacket(new ShowBoard(html.substring(0, html.length() / 3), "101"), new ShowBoard(html.substring(html.length() / 3, html.length() / 3 * 2), "102"), new ShowBoard(html.substring(html.length() / 3 * 2, html.length()), "103"));
        //_log.info("ShowBoard["+html.length()+"]: \n"+html);
    }

    public static void send1001(String html, L2Player activeChar) {
        if (html.length() < 10240)
            activeChar.sendPacket(new ShowBoard("<html><center>" + Files.read(ConfigValue.CommunityBoardHtmlRoot + "menuAll.htm", activeChar) + "</center>" + html, "1001"));
    }

    public static void send1002(L2Player activeChar, String string, String string2, String string3) {
        GArray<String> _arg = new GArray<String>();
        _arg.add("0");
        _arg.add("0");
        _arg.add("0");
        _arg.add("0");
        _arg.add("0");
        _arg.add("0");
        _arg.add(activeChar.getName());
        _arg.add(Integer.toString(activeChar.getObjectId()));
        _arg.add(activeChar.getAccountName());
        _arg.add("9");
        _arg.add(string2);
        _arg.add(string2);
        _arg.add(string);
        _arg.add(string3);
        _arg.add(string3);
        _arg.add("0");
        _arg.add("0");
        activeChar.sendPacket(new ShowBoard(_arg));
    }

    private ShowBoard(String htmlCode, String id) {
        _id = id;
        _htmlCode = htmlCode;
    }

    private ShowBoard(GArray<String> arg) {
        _id = "1002";
        _htmlCode = null;
        _arg = arg;
    }

    public ShowBoard(int arg) {
        _open = 0;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x7b);
        writeC(_open);
        if (_open == 1) {
            writeS("bypass -h " + ConfigValue.BBSDefault);
            writeS("bypass -h " + ConfigValue.BBSFavorites);
            writeS("bypass -h " + ConfigValue.BBSRegion);
            writeS("bypass -h " + ConfigValue.BBSClan);
            writeS("bypass -h " + ConfigValue.BBSMemo);
            writeS("bypass -h " + ConfigValue.BBSMail);
            writeS("bypass -h " + ConfigValue.BBSFriendList);
            writeS("bypass -h " + ConfigValue.BBSWebsite);
            StringBuilder str = new StringBuilder(_id + "\u0008");
            if (_id.equals("1002")){
                for (String arg : _arg){
                    str.append(arg).append(" \u0008");
                }
            } else if (_htmlCode != null){
                str.append(_htmlCode);
            }
            final String string = str.toString();



//            getClient().getActiveChar().setCurrentEncryptKey();


            writeS(string);
        }
    }
}