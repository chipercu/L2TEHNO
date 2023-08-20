package communityboard;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.ChangePassword;
import l2open.gameserver.model.L2Player;
import l2open.util.Rnd;

public class CommunityBoardCabinet
{
	private static String OFF = "<font color=\"FF0000\">OFF</font>";
	private static String ON = "<font color=\"00CC00\">ON</font>";

	public static void changePassword(L2Player player, String old, String newPass1, String newPass2, String number1, String number2, String captcha)
	{
		if(player == null)
			return;

		if(old.equals(newPass1))
		{
			player.setPasswordResult(new CustomMessage("communityboard.cabinet.password.incorrect.newisold", player).toString());
			return;
		}

		if(newPass1.length() < 4 || newPass1.length() > 20)
		{
			player.setPasswordResult(new CustomMessage("communityboard.cabinet.password.incorrect.size", player).toString());
			return;
		}

		if(!newPass1.equals(newPass2))
		{
			player.setPasswordResult(new CustomMessage("communityboard.cabinet.password.incorrect.confirmation", player).toString());
			return;
		}

		if(Integer.valueOf(number1) + Integer.valueOf(number2) != Integer.valueOf(captcha))
		{
			int captchaA = Integer.valueOf(number1) + Integer.valueOf(number2);
			player.setPasswordResult(new CustomMessage("communityboard.cabinet.password.incorrect.captcha", player).addNumber(captchaA).toString());
			return;
		}

		LSConnection.getInstance().sendPacket(new ChangePassword(player.getAccountName(), old, newPass1, "null"));
		return;
	}

	public static int doCaptcha(boolean n1, boolean n2)
	{
		int captcha = 0;
		if(n1)
			captcha = Rnd.get(1, 499);
		if(n2)
			captcha = Rnd.get(1, 499);

		return captcha;
	}

	public static String lang(L2Player player)
	{
		return "<font color=\"339966\">" + player.getVar("lang@") + "</font>";
	}

	public static String DroplistIcons(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("DroplistIcons"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:droplisticons:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:droplisticons:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("DroplistIcons") ? ON : OFF;
		return _msg;
	}

	public static String NoExp(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("NoExp"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:exp:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:exp:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("NoExp") ? ON : OFF;
		return _msg;
	}

	public static String NotShowTraders(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("notraders"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:notraders:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:notraders:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("notraders") ? ON : OFF;
		return _msg;
	}

	public static String notShowBuffAnim(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("notShowBuffAnim"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:showbuffanim:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:showbuffanim:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("notShowBuffAnim") ? ON : OFF;
		return _msg;
	}

	public static String SkillsHideChance(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("SkillsHideChance"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:skillchance:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:skillchance:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("SkillsHideChance") ? ON : OFF;
		return _msg;
	}

	public static String MonsterSkillsHideChance(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("SkillsMobChance"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:monsterskillchance:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:monsterskillchance:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("SkillsMobChance") ? ON : OFF;
		return _msg;
	}

	public static String AutoLoot(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.isAutoLootEnabled())
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:autoloot:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:autoloot:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.isAutoLootEnabled() ? ON : OFF;
		return _msg;
	}

	public static String AutoLootHerbs(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.isAutoLootHerbsEnabled())
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:autolootherbs:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:autolootherbs:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.isAutoLootHerbsEnabled() ? ON : OFF;
		return _msg;
	}

	public static String noShift(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("noShift"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:noshift:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:noshift:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("noShift") ? ON : OFF;
		return _msg;
	}

	public static String trace(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(player.getVarB("trace"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:trace:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:trace:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = player.getVarB("trace") ? ON : OFF;
		return _msg;
	}

	public static String pathfind(L2Player player, boolean button)
	{
		String _msg;
		if(button)
		{
			if(!player.getVarB("no_pf"))
				_msg = "<button value=\"OFF\" action=\"bypass -h _bbscabinet:cfg:pathfind:off\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
			else
				_msg = "<button value=\"ON\" action=\"bypass -h _bbscabinet:cfg:pathfind:on\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"35\" height=\"25\"/>";
		}
		else
			_msg = !player.getVarB("no_pf") ? ON : OFF;
		return _msg;
	}
}
