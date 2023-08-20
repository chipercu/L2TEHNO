package l2open.gameserver.model.entity;

import l2open.gameserver.model.L2Player;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;

public class HeroDiary
{
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:** dd.MM.yyyy");
	public static final int ACTION_RAID_KILLED = 1;
	public static final int ACTION_HERO_GAINED = 2;
	public static final int ACTION_CASTLE_TAKEN = 3;
	private int _id;
	private long _time;
	private int _param;

	public HeroDiary(int id, long time, int param)
	{
		_id = id;
		_time = time;
		_param = param;
	}

	public AbstractMap.SimpleEntry<String, String> toString(L2Player player)
	{
		String message = null;
		switch(_id)
		{
			case ACTION_RAID_KILLED:
				message = player.getLang().equals("ru") ? htmlNpcName(_param) + " потерпел поражение." : htmlNpcName(_param) + " was defeated.";
				break;
			case ACTION_HERO_GAINED:
				message = player.getLang().equals("ru") ? "Получил статус героя." : "Gained Hero status.";
				break;
			case ACTION_CASTLE_TAKEN:
				message =  player.getLang().equals("ru") ? htmlResidenceName(_param) + " был взят." : htmlResidenceName(_param) + " was successfuly taken.";
				break;
			default:
				return null;
		}
		return new AbstractMap.SimpleEntry<String, String>(SIMPLE_FORMAT.format(_time), message);
	}

	public static String htmlNpcName(int npcId)
	{
		return "&@" + npcId + ";";
	}

	public static String htmlResidenceName(int id)
	{
		return "&%" + id + ";";
	}
}
