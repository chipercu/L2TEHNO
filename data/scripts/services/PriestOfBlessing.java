package services;

import l2open.gameserver.Constants;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Rnd;

/**
 * @author : Ragnarok
 * @date : 14.10.2010   0:55:28
 */
public class PriestOfBlessing extends Functions implements ScriptFile {

    public String DialogAppend_32783(Integer val)
	{
        if(val == 0)
		{
            L2Player player = (L2Player) getSelf();
            String append = "";
            int i = 0;
            for(;player.getLevel() >= Constants.NEVITS_HOURGlASS[i][0];i++);
            append += "[scripts_services.PriestOfBlessing:buyNevitVoise|";
            append += getStringNevitVoise(player.getVar("lang@").equalsIgnoreCase("ru"));
            append += "<br>";
            append += "[scripts_services.PriestOfBlessing:buyNevitHourGlass " + i + "|";
            append += getStringNevitHourGlass1(player.getVar("lang@").equalsIgnoreCase("ru"), i);
            append += "]<br>" + getStringNevitHourGlass2(player.getVar("lang@").equalsIgnoreCase("ru"), i);
            append += "<br>";
            append += getAppendByLang(player.getVar("lang@").equalsIgnoreCase("ru"));
            return append;
        }
        return "";
    }

    private static String getAppendByLang(boolean ru) {
        String append = "";
        if (ru) {
            append += "[npc_%objectId%_Chat 1|Спросить о Голосе Невитта.]<br>";
            append += "[npc_%objectId%_Chat 2|Спросить о Песочных Часах Невитта.]";
        } else {
            append += "[npc_%objectId%_Chat 1|Ask about Nevit's Voice.]<br>";
            append += "[npc_%objectId%_Chat 2|Ask about Nevit's Hourglass.]";
        }
        return append;
    }

    private static String getStringNevitHourGlass2(boolean ru, int i) {
        if (ru)
            return "(Песочные Часы Невитта " + getLevelById(i) + " уровней)";
        return "(Nevit's Hourglass for " + getLevelById(i) + " levels)";
    }

    private static String getLevelById(int i) {
        switch (i) {
            case 0:
                return "1~19";
            case 1:
                return "20~39";
            case 2:
                return "40~51";
            case 3:
                return "52~60";
            case 4:
                return "61~75";
            case 5:
                return "76~79";
            case 6:
                return "80~85";
        }
        return "";
    }

    public static String getStringNevitHourGlass1(boolean ru, int id) {
        if (ru)
            return "Пожертвовать " + getAdenaById(id) + " аден.";
        return "Donate " + getAdenaById(id) + " Adena.";
    }

    public static String getAdenaById(int i) {
        switch (i) {
            case 0:
                return "4,000";
            case 1:
                return "30,000";
            case 2:
                return "110,000";
            case 3:
                return "310,000";
            case 4:
                return "970,000";
            case 5:
                return "2,160,000";
            case 6:
                return "5,000,000";
        }
        return "";
    }

    public static String getStringNevitVoise(boolean ru) {
        if (ru)
            return "Пожертвовать 100,000 аден.]<br>(Голос Невитта)";
        return "Donate 100,000 Adena.]<br>(Nevit's Voice)";
    }

    public void buyNevitVoise()
	{
        L2Player player = (L2Player) getSelf();
        if(player.getAdena() < 100000)
		{
            player.sendPacket(new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            return;
        }
        long req = (player.getVarLong("NextByNevitVoise") - System.currentTimeMillis())/1000;
        if(req > 0)
		{
			long minutes = req/60%60;
			long hour = req/3600%24;

			if(hour > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hour).addNumber(minutes));
			else
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
            return;
        }

        player.reduceAdena(100000, false);
        player.getInventory().addItem(17094, 1);
        player.setVar("NextByNevitVoise", "" + (System.currentTimeMillis() + 72000000));
    }

    public void buyNevitHourGlass(String[] args)
	{
        L2Player player = (L2Player) getSelf();
        int id = Integer.parseInt(args[0]);
        if(player.getAdena() < Constants.NEVITS_HOURGlASS[id][1])
		{
            player.sendPacket(new SystemMessage(SystemMessage.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            return;
        }
		long req = (player.getVarLong("NextByNevitHourGlass"+id) - System.currentTimeMillis())/1000;
        if(req > 0)
		{
			long minutes = req/60%60;
			long hour = req/3600%24;

			if(hour > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hour).addNumber(minutes));
			else
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
			return;
        }

        player.reduceAdena(Constants.NEVITS_HOURGlASS[id][1], false);
        player.getInventory().addItem(Constants.NEVITS_HOURGlASS[id][Rnd.get(2, 6)], 1);
        player.setVar("NextByNevitHourGlass"+id, "" + (System.currentTimeMillis() + 72000000));
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}
