package l2open.gameserver.model.barahlo;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.util.Rnd;

public class ProtectFunction
{
	private static ProtectFunction _inst = new ProtectFunction();

	public static ProtectFunction getInstance()
	{
		return _inst;
	}

	private static String[] _key =
	{
		"CharacterPassword_DF_Key0",
		"CharacterPassword_DF_Key1",
		"CharacterPassword_DF_Key2",
		"CharacterPassword_DF_Key3",
		"CharacterPassword_DF_Key4",
		"CharacterPassword_DF_Key5",
		"CharacterPassword_DF_Key6",
		"CharacterPassword_DF_Key7",
		"CharacterPassword_DF_Key8",
		"CharacterPassword_DF_Key9",
		"CharacterPassword_DF_KeyBack",
		"CharacterPassword_DF_KeyC"
	};

	/**
	 *<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background="icon.armor_t97_g_i00">
	 *	<tr>
	 *		<td width=32 align=center valign=top>
	 *			тут ссылка на вторую картинку
	 *		</td>
	 *	</tr>
	 * </table>
	 **/
	// "ICON_DF_CHARACTERTURN_LEFT", "ICON_DF_CHARACTERTURN_RIGHT", "ICON_DF_CHARACTERTURN_ZOOMIN", "ICON_DF_CHARACTERTURN_ZOOMOUT",
	private static String[] _image =
	{
		"SkillWnd_DF_Btn_SkillResearch",
		"SystemMenuWnd_df_Board",
		"SystemMenuWnd_df_Exit",
		"SystemMenuWnd_df_Help",
		"SystemMenuWnd_df_Homepage",
		"SystemMenuWnd_df_Macro",
		"SystemMenuWnd_df_Option",
		"SystemMenuWnd_df_Petition",
		"SystemMenuWnd_df_Post",
		"SystemMenuWnd_df_ProductInventory",
		"SystemMenuWnd_df_ReStart",
		"MiniGame_DF_Icon_Dark",
		"MiniGame_DF_Icon_Divine",
		"MiniGame_DF_Icon_Earth",
		"MiniGame_DF_Icon_Fire",
		"MiniGame_DF_Icon_Water",
		"MiniGame_DF_Icon_Wind",
		"EventBtnWnd_DF_LastRankBtn",
		"EventBtnWnd_DF_PresentRankBtn",
		"Icon_DF_MenuWnd_Action",
		"Icon_DF_MenuWnd_Character",
		"Icon_DF_MenuWnd_Clan",
		"Icon_DF_MenuWnd_Inventory",
		"Icon_DF_MenuWnd_Map",
		"Icon_DF_MenuWnd_Quest",
		"Icon_DF_MenuWnd_Skill",
		"Icon_DF_MenuWnd_SystemMenu"
	};

	public void getEnchantProtect(L2Player player, boolean isImage)
	{
		String imgSuc = isImage ? getRndImage() : getRndKey();
		player._setImage = imgSuc;

		StringBuilder sb = new StringBuilder();

		sb.append("<html><body><br><br>");
		// -------------------------------------------
		sb.append("<center>Добрый день, это защита от автоматической заточки вещей.<br1>");
		sb.append("Ваша задача, в поле \"<font color=\"ff0000\">Выбор:</font>\" выбрать такую же картинку, которая отображена в поле \"<font color=\"00ff00\">Пример:</font>\" с номером 5.<br><br1>");
		sb.append("<table>");
		sb.append("	<tr><td></td></tr>");
		sb.append("	<tr>");
		sb.append("		<td width=50>");
		sb.append("			<font color=\"00ff00\">Пример:</font>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("<table border=1 bgcolor=\"deba73\" cellspacing=0 cellpadding=0 width=36 height=36 background=\"L2UI_CT1." + imgSuc + "\">");
		sb.append("	<tr>");
		sb.append("		<td width=32 align=center valign=top>");
		sb.append("			<img src=\"L2UI_CT1.Inventory_DF_CloakSlot_Disable\" width=32 height=32/>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");

		sb.append("<br><br><table>");
		sb.append("	<tr><td></td></tr>");
		sb.append("	<tr>");
		sb.append("		<td width=50>");
		sb.append("			<font color=\"ff0000\">Выбор:</font>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("<center><table>");

		int itr = 0;
		int rnd = Rnd.get(ConfigValue.EnchantProtectImageCount * ConfigValue.EnchantProtectImageCount);
		for(int i = 0; i < ConfigValue.EnchantProtectImageCount; i++)
		{
			sb.append("<tr>");
			for(int i1 = 0; i1 < ConfigValue.EnchantProtectImageCount; i1++)
			{
				itr++;
				String img;
				if(itr == rnd)
					img = imgSuc;
				else
					img = isImage ? getRndImage() : getRndKey();
				sb.append("<td>");
				sb.append("<button action=\"bypass -h _bbsenchantver;"+(isImage ? "image" : "key")+";" + img + "\" width=32 height=32 back=L2UI_CT1." + img + " fore=L2UI_CT1." + img + ">");
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table></center>");
		// -------------------------------------------
		sb.append("</body></html>");
		ShowBoard.separateAndSend(sb.toString(), player);
	}

	public void getBotProtect(L2Player player)
	{
		String imgSuc = getRndKey();
		player._setImage = imgSuc;

		StringBuilder sb = new StringBuilder();

		sb.append("<html><body><br><br>");
		// -------------------------------------------
		sb.append("<center>Уважаемый игрок, вам необходимо пройти проверку антибот.<br1>");
		sb.append("Ваша задача, в поле \"<font color=\"ff0000\">Выбор:</font>\" выбрать такую же картинку, которая отображена в поле \"<font color=\"00ff00\">Пример:</font>\".<br><br1>");
		sb.append("<table>");
		sb.append("	<tr><td></td></tr>");
		sb.append("	<tr>");
		sb.append("		<td width=50>");
		sb.append("			<font color=\"00ff00\">Пример:</font>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("<table border=1 bgcolor=\"deba73\" cellspacing=0 cellpadding=0 width=36 height=36 background=\"L2UI_CT1." + imgSuc + "\">");
		sb.append("	<tr>");
		sb.append("		<td width=32 align=center valign=top>");
		sb.append("			<img src=\"L2UI_CT1.Inventory_DF_CloakSlot_Disable\" width=32 height=32/>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");

		sb.append("<br><br><table>");
		sb.append("	<tr><td></td></tr>");
		sb.append("	<tr>");
		sb.append("		<td width=50>");
		sb.append("			<font color=\"ff0000\">Выбор:</font>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("<center><table>");

		int itr = 0;
		int rnd = Rnd.get(9 * 9);
		for(int i = 0; i < 9; i++)
		{
			sb.append("<tr>");
			for(int i1 = 0; i1 < 9; i1++)
			{
				itr++;
				String img;
				if(itr == rnd)
					img = imgSuc;
				else
					img = getRndKey();
				sb.append("<td>");
				sb.append("<button action=\"bypass -h _bbsenchantver;bot;" + img + "\" width=32 height=32 back=L2UI_CT1." + img + " fore=L2UI_CT1." + img + ">");
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table></center>");
		// -------------------------------------------
		sb.append("</body></html>");
		ShowBoard.separateAndSend(sb.toString(), player);
	}

	public void getFishProtect(L2Player player)
	{
		String imgSuc = getRndImage();
		player._setImage = imgSuc;

		StringBuilder sb = new StringBuilder();

		sb.append("<html><body><br><br>");
		// -------------------------------------------
		sb.append("<center>Уважаемый игрок, вам необходимо пройти проверку антибот.<br1>");
		sb.append("Ваша задача, в поле \"<font color=\"ff0000\">Выбор:</font>\" выбрать такую же картинку, которая отображена в поле \"<font color=\"00ff00\">Пример:</font>\".<br><br1>");
		sb.append("<table>");
		sb.append("	<tr><td></td></tr>");
		sb.append("	<tr>");
		sb.append("		<td width=50>");
		sb.append("			<font color=\"00ff00\">Пример:</font>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("<table border=1 bgcolor=\"deba73\" cellspacing=0 cellpadding=0 width=36 height=36 background=\"L2UI_CT1." + imgSuc + "\">");
		sb.append("	<tr>");
		sb.append("		<td width=32 align=center valign=top>");
		sb.append("			<img src=\"L2UI_CT1.Inventory_DF_CloakSlot_Disable\" width=32 height=32/>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");

		sb.append("<br><br><table>");
		sb.append("	<tr><td></td></tr>");
		sb.append("	<tr>");
		sb.append("		<td width=50>");
		sb.append("			<font color=\"ff0000\">Выбор:</font>");
		sb.append("		</td>");
		sb.append("	</tr>");
		sb.append("</table>");
		sb.append("<center><table>");

		int itr = 0;
		int rnd = Rnd.get(ConfigValue.FishingProtectImageCount * ConfigValue.FishingProtectImageCount);
		for(int i = 0; i < ConfigValue.FishingProtectImageCount; i++)
		{
			sb.append("<tr>");
			for(int i1 = 0; i1 < ConfigValue.FishingProtectImageCount; i1++)
			{
				itr++;
				String img;
				if(itr == rnd)
					img = imgSuc;
				else
					img = getRndImage();
				sb.append("<td>");
				sb.append("<button action=\"bypass -h _bbsenchantver;fishing;" + img + "\" width=32 height=32 back=L2UI_CT1." + img + " fore=L2UI_CT1." + img + ">");
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table></center>");
		// -------------------------------------------
		sb.append("</body></html>");
		ShowBoard.separateAndSend(sb.toString(), player);
	}

	private String getRndImage()
	{
		return _image[Rnd.get(_image.length)];
	}

	private String getRndKey()
	{
		return _key[Rnd.get(_key.length)];
	}
}