package commands.admin;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.siege.*;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.tables.AugmentationData;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.reference.HardReference;
import l2open.util.*;
import l2open.extensions.network.SelectorThread;
import l2open.gameserver.model.barahlo.VoteManager;

import java.util.*;
import java.util.logging.Level;
import java.sql.ResultSet;

import l2open.gameserver.network.L2GameClient.GameClientState;

public class AdminAdmin implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_skil,
		admin_admin,
		admin_play_sounds,
		admin_play_sound,
		admin_silence,
		admin_tradeoff,
		admin_show_html,
		admin_printcm,
		admin_aug,
		admin_raug,
		admin_augall,
		admin_ping2,
		admin_ping,
		admin_vote,
		admin_showparty,
		admin_quiet,
		admin_mail,
		admin_mail2,
		admin_mail3,
		admin_st_debug,
		admin_npc_debug,
		admin_sit,
		admin_form,
		admin_tp_pm,
		admin_valid,
		admin_bot_check,
		admin_spam,
		admin_io,
		admin_hwidunlock,
		admin_dellhq,
		admin_dell,
		admin_l1,
		admin_observ,
		admin_sm
	}

	public static HashMap<Integer, MailInfo> _mail_list = new HashMap<Integer, MailInfo>();
	public static class MailInfo
	{
		public int type=0;
		public String title=null;
		public String sender_name=null;
		public String body=null;

		GArray<L2ItemInstance> attachments = new GArray<L2ItemInstance>();

		public void addItem(int item_id, long item_count, int item_enchant)
		{
			L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
			if(item.isStackable())
				item.setCount(item_count);
			else
				item.setEnchantLevel(item_enchant);
			attachments.add(item);
		}

		public void remItem(int obj_id)
		{
			for(L2ItemInstance item : attachments)
				if(item.getObjectId() == obj_id)
				{
					attachments.remove(item);
					break;
				}
		}

		public void sendMail(L2Player activeChar)
		{
			{
				if(type == 0)
				{
					L2Object target = activeChar.getTarget();
					if(target != null && target.isPlayer())
					{
						MailParcelController.Letter mail = new MailParcelController.Letter();
						mail.senderId = 1;
						mail.senderName = sender_name;
						mail.receiverId = target.getPlayer().getObjectId();
						mail.receiverName = target.getPlayer().getName();
						mail.topic = title;
						mail.body = body;
						mail.price = 0;
						mail.unread = 1;
						mail.system = sender_name == null ? 1 : 0;
						mail.hideSender = 0;
						mail.validtime = 7*24*60*60 + (int) (System.currentTimeMillis() / 1000L);

						GArray<L2ItemInstance> attach = new GArray<L2ItemInstance>();
						for(L2ItemInstance items : attachments)
						{
							L2ItemInstance item = ItemTemplates.getInstance().createItem(items.getItemId());
							if(item.isStackable())
								item.setCount(items.getCount());
							else
								item.setEnchantLevel(items.getEnchantLevel());
							attach.add(item);
						}
						MailParcelController.getInstance().sendLetter(mail, attach);
						target.getPlayer().sendPacket(new ExNoticePostArrived(1));
					}
				}
				else if(type == 1)
				{
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(player != null)
						{
							MailParcelController.Letter mail = new MailParcelController.Letter();
							mail.senderId = 1;
							mail.senderName = sender_name;
							mail.receiverId = player.getObjectId();
							mail.receiverName = player.getName();
							mail.topic = title;
							mail.body = body;
							mail.price = 0;
							mail.unread = 1;
							mail.system = sender_name == null ? 1 : 0;
							mail.hideSender = 0;
							mail.validtime = 7*24*60*60 + (int) (System.currentTimeMillis() / 1000L);

							GArray<L2ItemInstance> attach = new GArray<L2ItemInstance>();
							for(L2ItemInstance items : attachments)
							{
								L2ItemInstance item = ItemTemplates.getInstance().createItem(items.getItemId());
								if(item.isStackable())
									item.setCount(items.getCount());
								else
									item.setEnchantLevel(items.getEnchantLevel());
								attach.add(item);
							}
							MailParcelController.getInstance().sendLetter(mail, attach);
							player.sendPacket(new ExNoticePostArrived(1));
						}
						activeChar.sendPacket(new ExReplyWritePost(1));
				}
				else if(type == 2)
				{
					for(String[] info : getCharList())
					{
						int object_id = Integer.parseInt(info[0]);
						String name = info[1];

						MailParcelController.Letter mail = new MailParcelController.Letter();
						mail.senderId = 1;
						mail.senderName = sender_name;
						mail.receiverId = object_id;
						mail.receiverName = name;
						mail.topic = title;
						mail.body = body;
						mail.price = 0;
						mail.unread = 1;
						mail.system = sender_name == null ? 1 : 0;
						mail.hideSender = 0;
						mail.validtime = 7*24*60*60 + (int) (System.currentTimeMillis() / 1000L);

						GArray<L2ItemInstance> attach = new GArray<L2ItemInstance>();
						for(L2ItemInstance items : attachments)
						{
							L2ItemInstance item = ItemTemplates.getInstance().createItem(items.getItemId());
							if(item.isStackable())
								item.setCount(items.getCount());
							else
								item.setEnchantLevel(items.getEnchantLevel());
							attach.add(item);
						}
						MailParcelController.getInstance().sendLetter(mail, attach);
						L2Player player = L2ObjectsStorage.getPlayer(object_id);
						if(player != null)
							player.sendPacket(new ExNoticePostArrived(1));
					}
					activeChar.sendPacket(new ExReplyWritePost(1));
				}
			}
		}

		public String getHtml(L2Player activeChar)
		{
			StringBuffer html = new StringBuffer();
			
			html.append("<html noscrollbar><title>L2Open Admin Menu</title><body>");
			html.append("<center><br><br><br><br><br><br><br><br><br><br><br><br>");
			html.append("	<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Black\">");
			html.append("	<tr>");
			html.append("		<td valign=\"top\">");
			html.append("<table width=292 border=0 bgcolor=\"FFFF84\">");
			html.append("	<tr>");
			html.append("		<td><button value=\"Char Menu\" action=\"bypass -h admin_char_manage\" width=65 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			html.append("		<td><button value=\"Events\" action=\"bypass -h admin_show_html events/events.htm\" width=65 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			html.append("		<td><button value=\"Server\" action=\"bypass -h admin_show_html admserver.htm\" width=65 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			html.append("		<td><button value=\"Other\" action=\"bypass -h admin_show_html other.htm\" width=65 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			html.append("	</tr>");
			html.append("</table>");
			html.append("<center>");
			html.append("	<table width=220 border=0 cellspacing=5 cellpadding=0>");
			html.append("		<tr>");
			html.append("			<td align=right valign=top>");
			html.append("				<font color=\"00ff00\">Отправка почты:</font>");
			html.append("			</td>");
			html.append("			<td align=left valign=top>");
			if(type == 1)
				html.append("				<combobox width=80 var=\"target\" list=\"Онлайн;Всем;Таргет\">");
			else if(type == 2)
				html.append("				<combobox width=80 var=\"target\" list=\"Всем;Таргет;Онлайн\">");
			else
				html.append("				<combobox width=80 var=\"target\" list=\"Таргет;Онлайн;Всем\">");
			html.append("			</td>");
			html.append("			<td>");
			html.append("				<button value=\"\" action=\"bypass -h admin_sm set_target $target\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Input_Down\" fore=\"L2UI_CT1.Button_DF_Input\">");
			html.append("			</td>");
			html.append("		</tr>");
			html.append("	</table>");
			html.append("</center>");
			html.append("<table width=270>");
			html.append("	<tr>");
			html.append("		<td>");
			html.append("			<center><font color=\"00ff00\">От кого: </font></center>");
			html.append("		</td>");
			if(sender_name == null)
			{
				html.append("		<td>");
				html.append("			<center><edit var=\"sender\" width=200></center>");
				html.append("		</td>");
				html.append("		<td>");
				html.append("			<button value=\"\" action=\"bypass -h admin_sm set_name $sender\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Input_Down\" fore=\"L2UI_CT1.Button_DF_Input\">");
				html.append("		</td>");
			}
			else
			{
				html.append("		<td>");
				html.append("			<center>"+sender_name+"</center>");
				html.append("		</td>");
				html.append("		<td>");
				html.append("			<button value=\"\" action=\"bypass -h admin_sm rem_name 0\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Delete_Down\" fore=\"L2UI_CT1.Button_DF_Delete\">");
				html.append("		</td>");
			}
			html.append("	</tr>");
			html.append("	<tr>");
			html.append("		<td>");
			html.append("			<center><font color=\"00ff00\">Тема: </font></center>");
			html.append("		</td>");
			if(title == null)
			{
				html.append("		<td>");
				html.append("			<center><edit var=\"title\" width=200></center>");
				html.append("		</td>");
				html.append("		<td>");
				html.append("			<button value=\"\" action=\"bypass -h admin_sm set_title $title\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Input_Down\" fore=\"L2UI_CT1.Button_DF_Input\">");
				html.append("		</td>");
			}
			else
			{
				html.append("		<td>");
				html.append("			<center>"+title+"</center>");
				html.append("		</td>");
				html.append("		<td>");
				html.append("			<button value=\"\" action=\"bypass -h admin_sm rem_title 0\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Delete_Down\" fore=\"L2UI_CT1.Button_DF_Delete\">");
				html.append("		</td>");
			}
			html.append("	</tr>");
			html.append("</table><br>");
			html.append("<center>");
			html.append("	<font color=\"LEVEL\">Сообщение:</font>");
			html.append("	<multiedit var=\"send_mail\" width=250 height=80>");
			html.append("	<table width=\"200\">");
			html.append("		<tr>");
			html.append("			<td><center><button value=\"Отправить\" action=\"bypass -h admin_sm s $send_mail\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td>");
			html.append("		</tr>");
			html.append("	</table><br>");
			html.append("	<font color=\"00ff00\">Вложения:</font><br1>");
			if(attachments.size() > 0)
			{
				html.append("	<table width=250 height=16 cellspacing=4 cellpadding=0 border=0>");
				for(L2ItemInstance item : attachments)
				{
					html.append("		<tr>");
					html.append("			<td>");
					html.append("				<center>"+item.getName()+"["+item.getItemId()+"]</center>");
					html.append("			</td>");
					html.append("			<td>");
					html.append("				<center>"+item.getCount()+"</center>");
					html.append("			</td>");
					html.append("			<td>");
					html.append("				<center>"+item.getEnchantLevel()+"</center>");
					html.append("			</td>");
					html.append("			<td align=right valign=middle width=20 height=16>");
					html.append("				<button value=\"\" action=\"bypass -h admin_sm rem_item "+item.getObjectId()+"\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Delete_Down\" fore=\"L2UI_CT1.Button_DF_Delete\">");
					html.append("			</td>");
					html.append("		</tr>");
				}
				html.append("	</table>");
			}
			html.append("	<table width=250 height=16 cellspacing=4 cellpadding=0 border=0>");
			html.append("		<tr>");
			html.append("			<td height=16>");
			html.append("				<center><font color=\"00ff00\">Добавить: </font></center>");
			html.append("			</td>");
			html.append("			<td>");
			html.append("				<center><edit type=number var=\"item_id\" width=60></center>");
			html.append("			</td>");
			html.append("			<td>");
			html.append("				<center><edit type=number var=\"item_count\" width=60></center>");
			html.append("			</td>");
			html.append("			<td>");
			html.append("				<center><edit type=number var=\"item_enchant\" width=40></center>");
			html.append("			</td>");
			html.append("			<td align=right valign=middle width=20 height=16>");
			html.append("				<button value=\"\" action=\"bypass -h admin_sm add_item $item_id $item_count $item_enchant\" width=16 height=16 back=\"L2UI_CT1.Button_DF_Input_Down\" fore=\"L2UI_CT1.Button_DF_Input\">");
			html.append("			</td>");
			html.append("		</tr>");
			html.append("	</table><br>");
			html.append("</center>");
			html.append("		</td>");
			html.append("	</tr>");
			html.append("</table>");
			html.append("</center>");
			html.append("</body></html>");
			
			return html.toString();
		}
	}

	public static void question(String text)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && player.getLevel() >= 76 && player.getVar("jailed") == null)
				player.scriptRequest(text, "Vote:addResult", new Object[0]);
	}

	public static class BotCheck extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> owner_ref;
		private HardReference<L2Player> admin_ref;

		public BotCheck(L2Player player, L2Player admin)
		{
			owner_ref = player.getRef();
			admin_ref = admin.getRef();
		}

		public void runImpl()
		{
			L2Player pl = owner_ref.get();
			L2Player admin = admin_ref.get();
			if(pl == null || admin == null)
				return;
			if(pl.getVarB("admin_bot_check", false))
				admin.sendMessage("Игрок '"+pl.getName()+"' ответил на вашу проверку.");
			else
				admin.sendMessage("Игрок '"+pl.getName()+"' НЕ ответил на вашу проверку.");
			pl.unsetVar("admin_bot_check");
		}
	}

	public boolean useAdminCommand(Enum comm, final String[] wordList, String fullString, final L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(activeChar.getPlayerAccess().Menu)
		{
			switch(command)
			{
				case admin_sm:
					NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

					MailInfo mi;
					if(_mail_list.containsKey(activeChar.getObjectId()))
						mi = _mail_list.get(activeChar.getObjectId());
					else
						mi = new MailInfo();

					if(wordList.length > 2)
					{
						if(wordList[1].equals("set_target")) // 9+
						{
							if(wordList[2].equals("Таргет"))
								mi.type=0;
							else if(wordList[2].equals("Онлайн"))
								mi.type=1;
							else if(wordList[2].equals("Всем"))
								mi.type=2;
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("set_name")) // 18
						{
							mi.sender_name = fullString.substring(18);
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("rem_name"))
						{
							mi.sender_name = null;
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("set_title")) // 19
						{
							mi.title = fullString.substring(19);
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("rem_title"))
						{
							mi.title = null;
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("add_item") && wordList.length == 5) // 15
						{
							mi.addItem(Integer.parseInt(wordList[2]), Long.parseLong(wordList[3]), Integer.parseInt(wordList[4]));
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("rem_item")) // 15
						{
							mi.remItem(Integer.parseInt(wordList[2]));
							_mail_list.put(activeChar.getObjectId(), mi);
						}
						else if(wordList[1].equals("s")) // 11
						{
							mi.body = fullString.substring(11);
							mi.sendMail(activeChar);
							_mail_list.remove(activeChar.getObjectId());
							mi = new MailInfo();
						}
					}
					//adminReply.setHtml(mi.getHtml(activeChar));
					//activeChar.sendPacket(adminReply);
					
					ShowBoard.separateAndSend(mi.getHtml(activeChar), activeChar);
					break;
				case admin_observ:
					int x = 212968;
					int y=179784;
					int z=-258;

					try
					{
						x = Integer.parseInt(wordList[1]);
						y = Integer.parseInt(wordList[2]);
						z = Integer.parseInt(wordList[3]);
					}
					catch(Exception e)
					{
						x = 212968;
						y=179784;
						z=-258;
					}

					activeChar.enterObserverMode(new Location(212968, 179784, -330), null, 0, false);
					break;
				case admin_l1:
				

				CharacterSelectionInfo cl = new CharacterSelectionInfo(activeChar.getNetConnection().getLoginName(), activeChar.getNetConnection().getSessionId().playOkID1);
				activeChar.sendPacket(RestartResponse.OK, cl);
				activeChar.getNetConnection().setCharSelection(cl.getCharInfo());
				if(activeChar.getNetConnection() != null)
					activeChar.getNetConnection().setState(GameClientState.AUTHED);

				activeChar.logout(false, true, false, false, 60000L);
					break;
				// itemID Count (если можно чтоб при указании количества удаляло написаное, а при неуказанном кол-ве удаляло все, желательно чтоб с нестопковыми тоже работало если это не геморойно)
				case admin_dell:
					if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
					{
						int item_id = -1;
						long item_count = -1;
						L2Player target = activeChar.getTarget().getPlayer();
						if(wordList.length == 2)
						{
							item_id = Integer.parseInt(wordList[1]);
						}
						else if(wordList.length == 3)
						{
							item_id = Integer.parseInt(wordList[1]);
							item_count = Long.parseLong(wordList[2]);
						}
						if(item_id > 0)
						{
							L2ItemInstance item = target.getInventory().getItemByItemId(item_id);
							if(item.isStackable())
								target.getInventory().destroyItem(item, item_count == -1 ? item.getCount() : Math.min(item_count, item.getCount()), true);
							else if(item_count == 1)
								target.getInventory().destroyItemByItemId(item_id, 1L, true);
							else
							{
								L2ItemInstance[] list = target.getInventory().getAllItemsById(item_id);
								if(item_count == -1)
								{
									for(L2ItemInstance i : target.getInventory().getItemsList())
										if(i.getItemId() == item_id)
											target.getInventory().destroyItem(i, 1L, true);
								}
								else
								{
									int count=0;
									for(L2ItemInstance i : target.getInventory().getItemsList())
										if(i.getItemId() == item_id)
										{
											count++;
											target.getInventory().destroyItem(i, 1L, true);
											if(item_count >= count)
												break;
										}
								}
							}
						}
					}
					else
						activeChar.sendMessage("Не верный таргет.");
					break;
				case admin_dellhq:
					if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
					{
						L2Player target = activeChar.getTarget().getPlayer();
						if(target.getClan() != null)
						{	
							SiegeClan sc = null;
							Siege siege = SiegeManager.getSiege(target, true);
							if(siege == null && target.getTerritorySiege() > -1)
							{
								sc = TerritorySiege.getSiegeClan(target.getClan());
								if(sc != null && sc.removeHeadquarter())
									activeChar.sendMessage("Вы успешно демонтировали лагерь.");
								break;
							}
							else if(siege != null)
							{
								sc = siege.getAttackerClan(target.getClan());
								if(sc == null)
									sc = siege.getDefenderClan(target.getClan());
								if(sc != null && sc.removeHeadquarter())
									activeChar.sendMessage("Вы успешно демонтировали лагерь.");
							}
						}
						else
							activeChar.sendMessage("Не верный таргет. Игрок не состоит в клане.");
					}
					else
						activeChar.sendMessage("Не верный таргет.");
					break;
				case admin_hwidunlock:
				
					if(!fullString.equals("admin_hwidunlock"))
					{
						ThreadConnection con = null;
						FiltredPreparedStatement statement = null;
						try
						{
							con = L2DatabaseFactory.getInstance().getConnection();
							statement = con.prepareStatement("DELETE FROM hwid_lock WHERE login=?");
							statement.setString(1, fullString.substring(17));
							statement.executeUpdate();
						}
						catch(final Exception e)
						{
							_log.log(Level.WARNING, "AdminAdmin clearHwidLock:", e);
						}
						finally
						{
							activeChar.sendMessage("Вы сняли привязку с аккаунта '"+fullString.substring(17)+"'.");
							DatabaseUtils.closeDatabaseCS(con, statement);
						}
					}
					break;
				case admin_io:
					activeChar.sendMessage(SelectorThread.getStats().toString());
					break;
				case admin_spam:
					for(int i=0;i<Integer.parseInt(wordList[1]);i++)
					new Thread(new l2open.common.RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							activeChar.sendMessage("Спам старт.");
							for(int i2=0;i2<10000;i2++)
							{
								activeChar.broadcastCharInfo();
								/*try
								{
									Thread.sleep(1);
								}
								catch(InterruptedException e)
								{
									e.printStackTrace();
								}*/
							}
							activeChar.sendMessage("Спам финиш.");
							
						}
					}).start();
					
				break;
				case admin_bot_check:
					if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
					{
						L2Player target = activeChar.getTarget().getPlayer();
						target._bot_check = ThreadPoolManager.getInstance().schedule(new BotCheck(target, activeChar), 30000);
						String file = Files.read("data/html/bot_check.htm", target).replaceAll("n1", "" + Rnd.get(1, 100)).replaceAll("n2", "" + Rnd.get(1, 100));

						NpcHtmlMessage html = new NpcHtmlMessage(5);
						html.setHtml(file);
						target.sendPacket(html);		
					}
					break;
				case admin_valid:
					activeChar.validateLocation(1);
					break;
				case admin_tp_pm:
					Say2C._online.clear();
					if(wordList.length == 1)
					{
						if(activeChar.getVarB("CharTeleMy", false))
						{
							activeChar.setVar("CharTeleMy", String.valueOf(false));
							activeChar.setVar("CharTeleMyCount", "-10");
							activeChar.sendMessage("Функция телепортации игроков выключена.");
						}
						else
						{
							activeChar.setVar("CharTeleMy", String.valueOf(true));
							activeChar.setVar("CharTeleMyCount", "-10");
							activeChar.sendMessage("Функция телепортации игроков включена.");
						}
					}
					else
					{
						int p_count = Integer.parseInt(wordList[1]);
						activeChar.setVar("CharTeleMy", String.valueOf(true));
						activeChar.setVar("CharTeleMyCount", String.valueOf(p_count));
						activeChar.sendMessage("Функция телепортации игроков включена, телепортация первых "+p_count+" игроков.");
					}
					break;
				case admin_form:
					if(activeChar.getTarget() != null && (activeChar.getTarget() instanceof L2BlockInstance))
					{
						L2BlockInstance block = ((L2BlockInstance)activeChar.getTarget());
						block.form_id = wordList.length == 1 ? 0 : Integer.parseInt(wordList[1]);
						block.broadcastPacket(new NpcInfo(block, null));

					}
					else
						activeChar.sendMessage("Не верный таргет.");

					break;
				case admin_sit:
					if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
					{
						L2Player player = activeChar.getTarget().getPlayer();
						if(player.isSitting())
							player.standUp();
						else
							player.sitDown(false);
					}
					else
						activeChar.sendMessage("Не верный таргет.");
					break;
				case admin_npc_debug:
					if(activeChar.getTarget() != null)
					{
						if(((L2Character)activeChar.getTarget()).i_ai0 != 1994575)
						{
							((L2Character)activeChar.getTarget()).i_ai0 = 1994575;
							activeChar.sendMessage("Npc Debug: on");
						}
						else
						{
							((L2Character)activeChar.getTarget()).i_ai0 = 0;
							activeChar.sendMessage("Npc Debug: off");
						}
					}
					break;
				case admin_st_debug:
					L2ObjectsStorage.getDump();
					break;
				case admin_mail:
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/mail.htm"));
					if(fullString.length() > 12)
					{
						for(L2Player player : L2ObjectsStorage.getPlayers())
							if(player != null)
							{
								MailParcelController.Letter mail = new MailParcelController.Letter();
								mail.senderId = 1;
								mail.senderName = "Администрация L2Name.ru";
								mail.receiverId = player.getObjectId();
								mail.receiverName = player.getName();
								mail.topic = "Информация";
								mail.body = fullString.substring(11);
								mail.price = 0;
								mail.unread = 1;
								mail.system = 1;
								mail.hideSender = 0;
								mail.validtime = 7*24*60*60 + (int) (System.currentTimeMillis() / 1000L);

								MailParcelController.getInstance().sendLetter(mail);
								player.sendPacket(new ExNoticePostArrived(1));
							}
							activeChar.sendPacket(new ExReplyWritePost(1));
					}
					break;
				case admin_mail2:
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/mail2.htm"));
					if(fullString.length() > 12)
					{
						for(String[] info : getCharList())
						{
							int object_id = Integer.parseInt(info[0]);
							String name = info[1];

							MailParcelController.Letter mail = new MailParcelController.Letter();
							mail.senderId = 1;
							mail.senderName = "Администрация L2Name.ru";
							mail.receiverId = object_id;
							mail.receiverName = name;
							mail.topic = "Информация";
							mail.body = fullString.substring(11);
							mail.price = 0;
							mail.unread = 1;
							mail.system = 1;
							mail.hideSender = 0;
							mail.validtime = 7*24*60*60 + (int) (System.currentTimeMillis() / 1000L);

							MailParcelController.getInstance().sendLetter(mail);
							L2Player player = L2ObjectsStorage.getPlayer(object_id);
							if(player != null)
								player.sendPacket(new ExNoticePostArrived(1));
						}
						activeChar.sendPacket(new ExReplyWritePost(1));
					}
					break;
				case admin_mail3:
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/mail2.htm"));
					if(fullString.length() > 12)
					{
						for(L2Player player : L2ObjectsStorage.getPlayers())
							if(player != null)
							{
								MailParcelController.Letter mail = new MailParcelController.Letter();
								mail.senderId = 1;
								mail.senderName = "Администрация L2Name.ru";
								mail.receiverId = player.getObjectId();
								mail.receiverName = player.getName();
								mail.topic = "Информация";
								mail.body = fullString.substring(11);
								mail.price = 0;
								mail.unread = 1;
								mail.system = 1;
								mail.hideSender = 0;
								mail.validtime = 7*24*60*60 + (int) (System.currentTimeMillis() / 1000L);

								L2ItemInstance reward1 = ItemTemplates.getInstance().createItem(4037);
								reward1.setCount(1);

								GArray<L2ItemInstance> attachments = new GArray<L2ItemInstance>();
								attachments.add(reward1);

								MailParcelController.getInstance().sendLetter(mail, attachments);
								player.sendPacket(new ExNoticePostArrived(1));
							}
							activeChar.sendPacket(new ExReplyWritePost(1));
					}
					break;
				case admin_showparty:
					if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
					{
						L2Player player = activeChar.getTarget().getPlayer();

						adminReply = new NpcHtmlMessage(5);
						int CharactersFound = 0;

						StringBuffer replyMSG = new StringBuffer("<html><body>");
						replyMSG.append("<table width=260><tr>");
						replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
						replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
						replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
						replyMSG.append("</tr></table>");
						replyMSG.append("<br><br><br><br>");

						if(player.getParty() == null)
						{
							replyMSG.append("<br><br><br><br><table width=270>");
							replyMSG.append("<tr><td width=270><center><font color=ff0000>Данный игрок <font color=00ff00><a action=\"bypass -h admin_character_list " + player.getName() + "\">"+player.getName()+"</a></font> не состоит в группе.</font></center></td></tr>");
							replyMSG.append("</table><br>");
						}
						else
						{	
							replyMSG.append("<table width=270>");
							replyMSG.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td><td width=40>Leader</td></tr>");
							replyMSG.append("</table>");
							for(L2Player element : player.getParty().getPartyMembers())
							{
								replyMSG.append("<table width=270>");
								replyMSG.append("<tr><td width=80><font color=00ff00><a action=\"bypass -h admin_character_list " + element.getName() + "\">" + element.getName() + "</a></font></td><td width=110>" + element.getTemplate().className + "</td><td width=40>" + element.getLevel() + "</td><td width=40>"+(player.getParty().isLeader(element) ? "<font color=00ff00>Yes" : "<font color=ff0000>No")+"</font></td></tr>");
								replyMSG.append("</table>");
							}
						}

						replyMSG.append("</center></body></html>");

						adminReply.setHtml(replyMSG.toString());
						activeChar.sendPacket(adminReply);						
					}
					else
						activeChar.sendMessage("Не верный таргет.");
					break;
				case admin_quiet:
					String message;
					if(!Say2C.quiet.isEmpty())
					{
						Say2C.quiet = "";
						message = "Чат всего игрового мира разблокировал гм: "+activeChar.getName();
					}
					else
					{
						Say2C.quiet = activeChar.getName();
						message = "Чат всего игрового мира временно заблокировал гм: "+Say2C.quiet;
					}
					for(L2Player player : L2ObjectsStorage.getPlayers())
					{
						player.sendPacket(new ExShowScreenMessage(message, 3000, ScreenMessageAlign.TOP_CENTER, true));
						player.sendMessage(message);
					}
					break;
				case admin_vote:
					VoteManager.getInstance().voteAnsver(fullString.substring(11));
					activeChar.sendMessage("Сообщение админу: ");
					break;
				case admin_ping:
					activeChar.getNetConnection().ping_send = 0;
					activeChar.sendPacket(new NetPing((int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime())));
					break;
				case admin_ping2:
					if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
						activeChar.getTarget().getPlayer().sendPacket(new NetPing());
					new Thread(new l2open.common.RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							try
							{
								Thread.sleep(2000);
							}
							catch(InterruptedException e)
							{
								e.printStackTrace();
							}
							if(activeChar.getTarget() != null && activeChar.getTarget().getPlayer() != null && activeChar.getTarget().getPlayer().getNetConnection() != null)
								activeChar.sendMessage("pingTime: "+activeChar.getTarget().getPlayer().getNetConnection().pingTime+" ping_send: "+activeChar.getTarget().getPlayer().getNetConnection().ping_send);
							else
								activeChar.sendMessage("pingTime: Error");
						}
					}).start();
					break;
				case admin_augall:
					for(L2ItemInstance i : activeChar.getInventory().getItemsList())
					{
						i.setAugmentation(AugmentationData.getInstance().generateRandomAugmentation(14, 3, i.getItem().getBodyPart()));
						if(i.isEquipped())
							i.getAugmentation().applyBoni(activeChar, true);
						activeChar.sendPacket(new InventoryUpdate().addModifiedItem(i));
					}
					activeChar.updateStats();
					activeChar.sendUserInfo(false);
					break;
				case admin_aug:
					activeChar.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, Msg.ExShowVariationMakeWindow);
					break;
				case admin_raug:
					activeChar.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, Msg.ExShowVariationCancelWindow);
					break;
				case admin_skil:
					System.out.println("LOL1!!!");
					new Thread(new l2open.common.RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							for(int i=0;i<Integer.parseInt(wordList[1]);i++)
							{
								for(L2Player player : L2ObjectsStorage.getPlayers())
								{
									if(player.getObjectId() == activeChar.getObjectId())
										continue;
									player.broadcastPacket(new MagicSkillUse(player, player, 834, 1, 60, 60));
									player.broadcastPacket(new MagicSkillLaunched(player.getObjectId(), 834, 1, player, false));
									AbnormalStatusUpdate mi = new AbnormalStatusUpdate();
									mi.addEffect(834, 1, 59);
									player.sendPacket(mi);
								}
							
								try
								{
									Thread.sleep(Integer.parseInt(wordList[2]));
								}
								catch(InterruptedException e)
								{
									e.printStackTrace();
								}
							}
						}
					}).start();
					break;
				case admin_admin:
					String file = Files.read("data/html/admin/admin.htm", activeChar);

					file = file.replace("<?donate?>",String.valueOf(getDonate()));
					file = file.replace("<?donate_clear?>",String.valueOf(getDonate()-40000));
					NpcHtmlMessage html_h = new NpcHtmlMessage(5);
					html_h.setHtml(file);
					activeChar.sendPacket(html_h);	
					break;
				case admin_play_sounds:
					if(wordList.length == 1)
						activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/songs/songs.htm"));
					else
						try
						{
							activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/songs/songs" + wordList[1] + ".htm"));
						}
						catch(StringIndexOutOfBoundsException e)
						{}
					break;
				case admin_play_sound:
					try
					{
						playAdminSound(activeChar, wordList[1]);
					}
					catch(StringIndexOutOfBoundsException e)
					{}
					break;
				case admin_silence:
					if(activeChar.getMessageRefusal()) // already in message refusal
					// mode
					{
						activeChar.unsetVar("gm_silence");
						activeChar.setMessageRefusal(false);
						activeChar.sendPacket(Msg.MESSAGE_ACCEPTANCE_MODE);
					}
					else
					{
						if(ConfigValue.SaveGMEffects)
							activeChar.setVar("gm_silence", "true");
						activeChar.setMessageRefusal(true);
						activeChar.sendPacket(Msg.MESSAGE_REFUSAL_MODE);
					}
					break;
				case admin_tradeoff:
					try
					{
						if(wordList[1].equalsIgnoreCase("on"))
						{
							activeChar.setTradeRefusal(true);
							activeChar.sendMessage("tradeoff enabled");
						}
						else if(wordList[1].equalsIgnoreCase("off"))
						{
							activeChar.setTradeRefusal(false);
							activeChar.sendMessage("tradeoff disabled");
						}
					}
					catch(Exception ex)
					{
						if(activeChar.getTradeRefusal())
							activeChar.sendMessage("tradeoff currently enabled");
						else
							activeChar.sendMessage("tradeoff currently disabled");
					}
					break;
				case admin_show_html:
					try
					{
						String html = wordList[1];
						if(html != null)
							activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/" + html));
						else
							activeChar.sendMessage("Html page not found");
					}
					catch(Exception npe)
					{
						activeChar.sendMessage("Html page not found");
					}
					break;
				case admin_printcm:
					System.out.println("Admin command list: ");
					for(String cm : AdminCommandHandler.getInstance().getAllCommands())
						System.out.println(cm+";");
					break;
			}
			return true;
		}

		if(activeChar.getPlayerAccess().CanTeleport)
		{
			switch(command)
			{
				case admin_show_html:
					String html = wordList[1];
					try
					{
						if(html != null)
							if(html.startsWith("tele"))
								activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/" + html));
							else
								activeChar.sendMessage("Access denied");
						else
							activeChar.sendMessage("Html page not found");
					}
					catch(Exception npe)
					{
						activeChar.sendMessage("Html page not found");
					}
					break;
			}
			return true;
		}

		return false;
	}

	private static List<String[]> getCharList()
	{
		List<String[]> result = new ArrayList<String[]>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT obj_Id, char_name, online FROM characters");
			rset = statement.executeQuery();
			while(rset.next())
				result.add(new String[]{String.valueOf(rset.getInt("obj_Id")), rset.getString("char_name"), String.valueOf(rset.getInt("online"))});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return result;
	}

	private static int getDonate()
	{
		float result = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `sum` FROM unitpay_payments WHERE status='1' AND id > '774'");
			rset = statement.executeQuery();
			while(rset.next())
				result += rset.getFloat("sum");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return (int)result;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void playAdminSound(L2Player activeChar, String sound)
	{
		activeChar.broadcastPacket(new PlaySound(1, sound, 0));
		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/admin.htm"));
		activeChar.sendMessage("Playing " + sound + ".");
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}