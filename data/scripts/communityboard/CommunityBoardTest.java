package communityboard;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.*;
import l2open.gameserver.cache.*;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.handler.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.*;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.text.*;
import java.util.*;
import java.util.logging.Logger;

public class CommunityBoardTest extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(CommunityBoardTest.class.getName());

	private static enum Commands
	{
		_bbs_test_v_list,
		_bbs_test_v_list_ok,
	}

	@Override
	public void parsecmd(String command, final L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		else if(command.startsWith("_bbs_test_v_list_ok")) // bypass -h _bbs_test_v_list_ok:id_item:count_item:visual_id:body_type
		{
			String[] param = command.split(":");
			int id_item = Integer.parseInt(param[1]);
			int count_item = Integer.parseInt(param[2]);
			int visual_id = Integer.parseInt(param[3]);
			int body_type = Integer.parseInt(param[4]);

			player._paperdoll_test = new int[26];

			switch(body_type)
			{
				case L2Item.SLOT_LR_HAND:
				case L2Item.SLOT_R_HAND:
					player._paperdoll_test[Inventory.PAPERDOLL_RHAND] = visual_id;
					break;
				case L2Item.SLOT_L_HAND:
					player._paperdoll_test[Inventory.PAPERDOLL_LHAND] = visual_id;
					break;
				case L2Item.SLOT_FULL_ARMOR:
				case L2Item.SLOT_CHEST:
				case L2Item.SLOT_FORMAL_WEAR:
				case L2Item.SLOT_SIGIL:
					player._paperdoll_test[Inventory.PAPERDOLL_CHEST] = visual_id;
					break;
				case L2Item.SLOT_LEGS:
					player._paperdoll_test[Inventory.PAPERDOLL_LEGS] = visual_id;
					break;
				case L2Item.SLOT_FEET:
					player._paperdoll_test[Inventory.PAPERDOLL_FEET] = visual_id;
					break;
				case L2Item.SLOT_GLOVES:
					player._paperdoll_test[Inventory.PAPERDOLL_GLOVES] = visual_id;
					break;
				case L2Item.SLOT_HEAD:
					player._paperdoll_test[Inventory.PAPERDOLL_HEAD] = visual_id;
					break;
				case L2Item.SLOT_HAIR:
					player._paperdoll_test[Inventory.PAPERDOLL_HAIR] = visual_id;
					break;
				case L2Item.SLOT_DHAIR:
				case L2Item.SLOT_HAIRALL:
					player._paperdoll_test[Inventory.PAPERDOLL_DHAIR] = visual_id;
					break;
				case L2Item.SLOT_UNDERWEAR:
					player._paperdoll_test[Inventory.PAPERDOLL_UNDER] = visual_id;
					break;
				case L2Item.SLOT_BACK:
					player._paperdoll_test[Inventory.PAPERDOLL_BACK] = visual_id;
					break;
				case L2Item.SLOT_BELT:
					player._paperdoll_test[Inventory.PAPERDOLL_BELT] = visual_id;
					break;
			}
			if(player._test_task != null)
			{
				player._test_task.cancel(false);
				player._test_task = null;
			}
			player._test_task = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				public void runImpl()
				{
					player._paperdoll_test = null;
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new UserInfo(player));
					//player.sendUserInfo(true);
					//player.sendUserInfo(true);
				}
			}, ConfigValue.VisualWearTime*1000);
			player.sendPacket(new UserInfo(player));
			player.sendPacket(new UserInfo(player));
		}
		else if(command.startsWith("_bbs_test_v_list"))
		{
			String[] param = command.split(":");
			int page = param.length == 1 ? 0 : Integer.parseInt(param[1]);
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"visual_test_item_list.htm", player);

			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			List<L2Item> list = new ArrayList<L2Item>();

			for(int item_id : ConfigValue.VisualWearList)
				list.add(ItemTemplates.getInstance().getTemplate(item_id));

			int MaxCharactersPerPage = 7;
			int MaxPages = list.size() / MaxCharactersPerPage;

			if(list.size() > MaxCharactersPerPage * MaxPages)
				MaxPages++;

			if(page > MaxPages)
				page = MaxPages;

			int CharactersStart = MaxCharactersPerPage * page;
			int CharactersEnd = list.size();
			if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
				CharactersEnd = CharactersStart + MaxCharactersPerPage;

			int i = CharactersStart;

			sb.append("<table width=320 height=40>");
			for(;i<CharactersEnd;i++)
			{
				L2Item item = list.get(i);
				String icon = item.getIcon();
				if(icon == null || icon.isEmpty())
					icon = "icon.etc_question_mark_i00";

				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0 cellspacing=4 cellpadding=3 bgcolor="+(i%2==0 ? "333333" : "1a1a1a>"));
				sb.append("			<tr>");
				sb.append("				<td FIXWIDTH=50 align=right valign=top>");
				sb.append("					<img src=\""+icon+"\" width=32 height=32>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=200 align=left valign=top>");
				sb.append("					<br>");
				sb.append("					<font color=\"0099FF\">"+item.getName()+"</font>&nbsp;");
				sb.append("					<br>");
				sb.append("				</td>");
				sb.append("				<td FIXWIDTH=95 align=center valign=top>");
				sb.append("					<table>");
				sb.append("						<tr>");
				sb.append("							<td>");
				sb.append("								<button value=\""+(player.isLangRus() ? "Примерить" : "Sample")+"\" action=\"bypass -h _bbs_test_v_list_ok:"+ConfigValue.VisualPriceId+":"+ConfigValue.VisualPriceCount+":"+item.getItemId()+":"+item.getBodyPart()+"\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"74\" height=\"25\"/>");
				sb.append("							</td>");
				sb.append("						</tr>");
				sb.append("					</table>");
				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("		<br>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			
			if(MaxPages > 1)
			{
				// Раскоментировать если нужны будут странички:)
				sb.append("<tr>");
				sb.append("	<td WIDTH=345 align=center valign=top>");
				sb.append("		<table border=0>");
				sb.append("			<tr>");
				sb.append("				<td WIDTH=180 height=35 align=left valign=top>");

				//for(int x = MaxPages-1;x>=0;x--)
				for(int x = 0;x < MaxPages;x++)
				{
					int pagenr = x + 1;
					sb.append("<a action=\"bypass -h _bbs_test_v_list:"+x+"\"> "+(x == page ? "["+pagenr+"]" : pagenr)+"&nbsp;</a> ");
				}

				sb.append("				</td>");
				sb.append("			</tr>");
				sb.append("		</table>");
				sb.append("	</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");

			content = content.replace("<?item_list?>", sb.toString());
			content = content.replace("<?allready?>", sb2.toString());
			separateAndSend(content, player);
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, command), player);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}