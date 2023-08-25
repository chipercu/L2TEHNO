package communityboard.manager;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.*;
import l2open.gameserver.cache.*;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.handler.*;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.*;
import l2open.gameserver.model.entity.ItemBroker.ItemAuction;
import l2open.gameserver.model.entity.ItemBroker.ItemAuctionInstance;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Diagod
 */
public class BBSManager extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(BBSManager.class.getName());

	private static enum Commands
	{
		_bbs_addrec,
		_bbs_remove_pc,
		_bbs_remove_karma,
		_bbs_clear_pc,
		_bbs_clear_karma,
		_bbs_raug_,
		_bbs_aug_,
		_bbs_buy_fame,
		_bbsclanExpRes,
		_bbseventlist,
		_bbsenchantver,
		_bbsolypoint,
		_bbs_clanpoint,
		_bbs_clanlevel,
		_bbshtmlpa,
		_bbs_by_double_class,
		_bbs_referal_get,
		_bbs_referal_html,
		_bbs_sell,
		_bbs_buy_quest_subclass,
		_bbs_buy_quest_noble,
		_bbs_lottery,
		_bbs_npc_auction,
		_bbs_close,
		_bbs_points,
		_bbs_siege,
		_bbs_changelang
	}

	private static SimpleDateFormat form = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(command.startsWith("_bbs_changelang"))
		{
			String lang = command.substring(16);
			if(player.isLangRus() && lang.equalsIgnoreCase("en"))
			{
				player.setVar("lang@", "en");
				TopBBSManager.getInstance().showTopPage(player, "index", null);
			}
			else if(!player.isLangRus() && lang.equalsIgnoreCase("ru"))
			{
				player.setVar("lang@", "ru");
				TopBBSManager.getInstance().showTopPage(player, "index", null);
			}
		}
		else if(command.startsWith("_bbs_siege"))
		{
			String[] param = command.split(":");
			
			int _unitId = Integer.parseInt(param[1]);
			Residence unit = CastleManager.getInstance().getCastleByIndex(_unitId);
			if(unit == null)
				unit = FortressManager.getInstance().getFortressByIndex(_unitId);
			if(unit == null)
				unit = ClanHallManager.getInstance().getClanHall(_unitId);
			if(unit != null && !unit.getSiege().isInProgress() && !TerritorySiege.isInProgress())
				unit.getSiege().listRegisterClan(player);
		}
		else if(command.startsWith("_bbs_points")) // bypass -h _bbs_points:point_count:id_item:count_item
		{
			String[] param = command.split(":");

			if(param.length != 4)
			{
				String html = Files.read("data/html/points.htm", player);

				html = html.replace("<?points?>", String.valueOf(player.getPoint(false)));
				html = html.replace("<?points_add?>", String.valueOf(ConfigValue.BuyPointsForOneItem));
				html = html.replace("<?points_all?>", String.valueOf(ConfigValue.BuyPointsForOneItem*player.getInventory().getCountOf(ConfigValue.BuyPointsItemId[0])));

				Functions.show(html, player, null);
				player.sendActionFailed();
			}
			else
			{
				int add_count = Integer.parseInt(param[1]);
				int count_item = Integer.parseInt(param[3]);
		
				if(add_count > 0 && player.getInventory().getCountOf(Integer.parseInt(param[2])) >= count_item)
				{
					if(player.getInventory().destroyItemByItemId(Integer.parseInt(param[2]), count_item, true) != null)
					{
						addPoints(player, add_count, false);
						player.sendMessage("Получено "+add_count+" point.");
					}
				}
				else if(add_count > 0)
					player.sendMessage("Недостаточно предметов, в наличии "+player.getInventory().getCountOf(ConfigValue.BuyPointsItemId[0])+".");
			}
		}
		else if(command.startsWith("_bbs_addrec")) // bypass -h _bbs_addrec:rec_count:id_item:count_item
		{
			String[] param = command.split(":");

			if(player.getRecommendation().getRecomHave() < 255 && DifferentMethods.getPay(player, Integer.parseInt(param[2]), Long.parseLong(param[3]), true))
				player.getRecommendation().addRecomHave(Integer.parseInt(param[1]));
			//separateAndSend(readHtml(ConfigValue.CommunityBoardHtmlRoot + param[3] + ".htm", player), player);
		}
		else if(command.startsWith("_bbs_remove_pc")) // bypass -h _bbs_remove_pc:pk_count:id_item:count_item
		{
			String[] param = command.split(":");

			if(player.getPkKills() > 0 && DifferentMethods.getPay(player, Integer.parseInt(param[2]), Long.parseLong(param[3]), true))
				player.setPkKills(Math.max(0, player.getPkKills()-Integer.parseInt(param[1])));
			else if(player.getPkKills() == 0)
				player.sendMessage("У вас нету PK.");
			//separateAndSend(readHtml(ConfigValue.CommunityBoardHtmlRoot + param[3] + ".htm", player), player);
		}
		else if(command.startsWith("_bbs_remove_karma")) // bypass -h _bbs_remove_karma:pk_count:id_item:count_item
		{
			String[] param = command.split(":");

			if(player.getKarma() > 0 && DifferentMethods.getPay(player, Integer.parseInt(param[2]), Long.parseLong(param[3]), true))
				player.setKarma(Math.max(0, player.getKarma()-Integer.parseInt(param[1])));
			else if(player.getKarma() == 0)
				player.sendMessage("У вас нету кармы.");
			//separateAndSend(readHtml(ConfigValue.CommunityBoardHtmlRoot + param[3] + ".htm", player), player);
		}
		else if(command.equals("_bbs_close"))
			player.sendPacket(new ShowBoard(0));
		else if(command.startsWith("_bbs_npc_auction"))
		{
			String[] param = command.split(":");

			if(param.length == 2)
			{
				NpcHtmlMessage html = new NpcHtmlMessage(player, null);
				String content = Files.read(ConfigValue.CommunityBoardHtmlRoot+"npc_auction/itembroker.htm", player);
				content = content.replace("<?auc_id?>", param[1]);
				html.setHtml(content);
				player.sendPacket(html);
			}
			else
			{
				if(param[2].equals("1"))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(player, null);
					String content = Files.read(ConfigValue.CommunityBoardHtmlRoot+"npc_auction/itembroker_1.htm", player);
					content = content.replace("<?auc_id?>", param[1]);
					html.setHtml(content);
					player.sendPacket(html);
					return;
				}
				int auc_id = Integer.parseInt(param[1]);
				ItemAuctionInstance _instance = ItemAuctionManager.getInstance().getManagerInstance(32319+auc_id);
				if(param[2].equals("cancel"))
				{
					if(param.length == 4)
					{
						int auctionId = 0;

						try
						{
							auctionId = Integer.parseInt(param[3]);
						}
						catch(NumberFormatException e)
						{
							e.printStackTrace();
							return;
						}

						final ItemAuction auction = _instance.getAuction(auctionId);
						if(auction != null)
							auction.cancelBid(player);
						else
							player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU));
					}
					else
					{
						final ItemAuction[] auctions = _instance.getAuctionsByBidder(player.getObjectId());
						for(final ItemAuction auction : auctions)
							auction.cancelBid(player);
					}
				}
				else if(param[2].equals("show"))
				{
					final ItemAuction currentAuction = _instance.getCurrentAuction();
					final ItemAuction nextAuction = _instance.getNextAuction();

					if(currentAuction == null)
					{
						player.sendPacket(Msg.IT_IS_NOT_AN_AUCTION_PERIOD);

						if(nextAuction != null)
							player.sendMessage("The next auction will begin on the " + fmt.format(new Date(nextAuction.getStartingTime())) + ".");
						return;
					}

					if(!player.getAndSetLastItemAuctionRequest())
					{
						player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR));
						return;
					}

					player.sendPacket(new ExItemAuctionInfoPacket(false, currentAuction, nextAuction));
				}
			}
		}
		else if(command.startsWith("_bbs_lottery"))
		{
			String[] param = command.split(":");
			if(param.length == 1)
				showLotoWindow(player, -1);
			else
				showLotoWindow(player, Integer.parseInt(param[1]));
		}
		else if(command.equals("_bbs_buy_quest_noble")) // bypass -h _bbs_buy_quest_noble
		{
			if(player.isNoble())
			{
				player.sendMessage("Вы уже Дворянин.");
				return;
			}
			Quest q = QuestManager.getQuest("_246_PossessorOfaPreciousSoul3");
			QuestState qs = player.getQuestState(q.getClass());
			if(qs != null)
			{
				if(qs.getState() == Quest.COMPLETED)
				{
					player.sendMessage("Вы уже прошли квест.");
					return;
				}
				qs.exitCurrentQuest(true);
			}
			if(!player.getVarB("buy_quest_noble", false)) // ???
			{
				q.newQuestState(player, Quest.STARTED);

				qs = player.getQuestState(q.getClass());
				qs.setCond(4);
				qs.set("staff_select", 0);
				player.setVar("buy_quest_noble", String.valueOf(true));
			}
			else
				player.sendMessage("Вы уже взяли упрощеный квест.");
		}
		else if(command.equals("_bbs_buy_quest_subclass")) // bypass -h _bbs_buy_quest_noble
		{
			Quest q = QuestManager.getQuest("_234_FatesWhisper");
			QuestState qs = player.getQuestState(q.getClass());
			if(qs != null)
			{
				if(qs.getState() == Quest.COMPLETED)
				{
					player.sendMessage("Вы уже прошли квест.");
					return;
				}
				qs.exitCurrentQuest(true);
			}
			if(!player.getVarB("buy_quest_subclass", false)) // ???
			{
				q.newQuestState(player, Quest.STARTED);

				qs = player.getQuestState(q.getClass());
				qs.setCond(1);
				player.setVar("buy_quest_subclass", String.valueOf(true));
			}
			else
				player.sendMessage("Вы уже взяли упрощеный квест.");
		}
		else if(command.equals("_bbs_sell")) // bypass -h _bbs_sell
		{
			player.sendPacket(new ExBuySellList(null, player, 0));
			player.setLastBbsOperaion("sell");
		}
		else if(command.startsWith("_bbs_clear_pc")) // bypass -h _bbs_clear_pc:id_item:count_item:next_open_html
		{
			String[] param = command.split(":");

			if(player.getPkKills() > 0 && DifferentMethods.getPay(player, Integer.parseInt(param[1]), Long.parseLong(param[2]), true))
				player.setPkKills(0);

			separateAndSend(readHtml(ConfigValue.CommunityBoardHtmlRoot + param[3] + ".htm", player), player);
		}
		else if(command.startsWith("_bbs_clear_karma")) // bypass -h _bbs_clear_karma:id_item:count_item:next_open_html
		{
			String[] param = command.split(":");

			if(player.getKarma() > 0 && DifferentMethods.getPay(player, Integer.parseInt(param[1]), Long.parseLong(param[2]), true))
				player.setKarma(0);

			separateAndSend(readHtml(ConfigValue.CommunityBoardHtmlRoot + param[3] + ".htm", player), player);
		}
		else if(command.startsWith("_bbs_by_double_class"))
		{
			if(!player.isSubClassActive())
			{
				separateAndSend("<html><body><br><br><center><font color=\"ff0000\">Перейдите на сабкласс который хотите сделать дабл основой.</font></center><br><br></body></html>", player);
				return;
			}
			else if(!player.isNoble())
			{
				separateAndSend("<html><body><br><br><center><font color=\"ff0000\">Доступно только дворянинам.</font></center><br><br></body></html>", player);
				return;
			}
			else if(player.getVarInt("DoubleBaseClass", 0) != 0)
			{
				separateAndSend("<html><body><br><br><center><font color=\"ff0000\">Вы уже получил дабл основу.</font></center><br><br></body></html>", player);
				return;
			}
			else if(Functions.getItemCount(player, (int)ConfigValue.DoubleBaseClassPrice[0]) < ConfigValue.DoubleBaseClassPrice[1])
			{
				separateAndSend("<html><body><br><br><center><font color=\"ff0000\">У вас нет необходимых предметов!</font></center><br><br></body></html>", player);
				return;
			}
			else if(player.class_id() == 135 || player.class_id() == 136)
			{
				separateAndSend("<html><body><br><br><center><font color=\"ff0000\">Не доступно для инспектора.</font></center><br><br></body></html>", player);
				return;
			}
			Functions.removeItem(player, (int)ConfigValue.DoubleBaseClassPrice[0], ConfigValue.DoubleBaseClassPrice[1]);
			player.getActiveClass().setBase2(true);
			player.setVar("DoubleBaseClass", "1");
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));
			separateAndSend("<html><body><br><br><center><font color=\"00ff00\">Вы получили двойную основу.</font></center><br><br></body></html>", player);	
		}
		else if(command.equals("_bbs_raug_"))
		{
			player.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, Msg.ExShowVariationCancelWindow);
		}
		else if(command.equals("_bbs_aug_"))
		{
			player.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, Msg.ExShowVariationMakeWindow);
		}
		else if(command.startsWith("_bbseventlist;all"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot+"eventtime.htm", player);
			content = content.replace("%result%", initTimer());
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbseventlist;"))
		{
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot+"eventtime.htm", player);
			content = content.replace("%result%", initTimer(Integer.parseInt(command.split(";")[1])));
			separateAndSend(content, player);
		}
		else if(command.startsWith("_bbsenchantver;image;"))
		{
			if(player._setImage.equals(command.substring(21)))
			{
				player._enchantDisable = false;
				player._setImage = "";
				player.sendPacket(new ShowBoard(0));
				player._enchantCount = 0;
			}
			else
			{
				player._setImage = "";
				player.logout(false, false, false, true);
			}
			
		}
		else if(command.startsWith("_bbsenchantver;key;"))
		{
			if(player._setImage.equals(command.substring(19)))
			{
				player._enchantDisable = false;
				player._setImage = "";
				player.sendPacket(new ShowBoard(0));
				player._enchantCount = 0;
			}
			else
			{
				player._setImage = "";
				player.logout(false, false, false, true);
			}
		}
		else if(command.startsWith("_bbsenchantver;bot;"))
		{
			if(player._setImage.equals(command.substring(19)))
			{
				if(player._bot_kick != null)
					player._bot_kick.cancel(false);
				player._setImage = "";
				player.sendPacket(new ShowBoard(0));
				player.startBotCheck(Rnd.get(ConfigValue.BotProtectTimeMin, ConfigValue.BotProtectTimeMax));
			}
			else
			{
				player._setImage = "";
				player.logout(false, false, false, true);
			}
		}
		else if(command.startsWith("_bbsolypoint:") && ConfigValue.OlympiadPointsSellEnabled) // bypass -h _bbsolypoint:item_id:item_count:add_oly_point
		{
			StatsSet nobleInfo = Olympiad._nobles.get(player.getObjectId());
			if(nobleInfo != null)
			{
				if(nobleInfo.getInteger("olympiad_points") <= ConfigValue.OlympiadPointsSellLimit)
				{
					String[] t = command.split(":");
					if(Functions.getItemCount(player, Integer.parseInt(t[1])) >= Long.parseLong(t[2]))
					{
						Functions.removeItem(player, Integer.parseInt(t[1]), Long.parseLong(t[2]));
						nobleInfo.set("olympiad_points", nobleInfo.getInteger("olympiad_points") + Integer.parseInt(t[3]));
					}
					else
						separateAndSend("<html noscrollbar><body><br><br><br><center><font color=\"LEVEL\">У вас нету необходимых итемов!</font></center><br><br></body></html>", player);
				}
				else
					separateAndSend("<html noscrollbar><body><br><br><br><center><font color=\"LEVEL\">Доступно только когда у вас "+ConfigValue.OlympiadPointsSellLimit+" point!</font></center><br><br></body></html>", player);
			}
			else
				separateAndSend("<html noscrollbar><body><br><br><br><center><font color=\"LEVEL\">Ошибка!</font></center><br><br></body></html>", player);
		}
		else if(command.startsWith("_bbsclanExpRes:"))
		{
			if(player.getClan() != null)
			{
				if(Functions.getItemCount(player, Integer.parseInt(command.split(":")[1])) >= Long.parseLong(command.split(":")[2]))
				{
					Functions.removeItem(player, Integer.parseInt(command.split(":")[1]), Long.parseLong(command.split(":")[2]));
					player.getClan().setExpelledMemberTime(0);
				}
				else
					separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">У вас нету необходимых итемов!</font></center><br><br></body></html>", player);
			}
			else
				separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">Ошибка!</font></center><br><br></body></html>", player);
		}
		else if(command.startsWith("_bbs_clanlevel"))
		{
			if(player.getClan() == null)
				separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">У вас нет клана.</font></center><br><br></body></html>", player);
			if(player.getClan().getLevel() >= 11)
				separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">Вы достигли максимальный уровень клана.</font></center><br><br></body></html>", player);
			else
			{
				if(Functions.getItemCount(player, Integer.parseInt(command.split(":")[1])) >= Long.parseLong(command.split(":")[2]))
				{
					Functions.removeItem(player, Integer.parseInt(command.split(":")[1]), Long.parseLong(command.split(":")[2]));
					byte level = Byte.parseByte(command.split(":")[3]);
					L2Clan clan = player.getClan();

					clan.setLevel((byte)(clan.getLevel() + level));
					PlayerData.getInstance().updateClanInDB(clan);

					if(clan.getLevel() < CastleSiegeManager.getSiegeClanMinLevel())
						SiegeManager.removeSiegeSkills(player);
					else
						SiegeManager.addSiegeSkills(player);

					if(clan.getLevel() == 5)
						player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);

					PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
					PledgeStatusChanged ps = new PledgeStatusChanged(clan);

					for(L2Player member : clan.getOnlineMembers(0))
					{
						member.updatePledgeClass();
						member.sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps);
						member.broadcastUserInfo(true);
					}
				}
				else
					separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">У вас нету необходимых итемов!</font></center><br><br></body></html>", player);
			}
		}
		else if(command.startsWith("_bbs_clanpoint"))
		{
			
			if(player.getClan() != null)
			{
				if(Functions.getItemCount(player, Integer.parseInt(command.split(":")[1])) >= Long.parseLong(command.split(":")[2]))
				{
					Functions.removeItem(player, Integer.parseInt(command.split(":")[1]), Long.parseLong(command.split(":")[2]));
					int rep = Integer.parseInt(command.split(":")[3]);
					player.getClan().incReputation(rep, false, "_bbs_clanpoint");
				}
				else
					separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">У вас нету необходимых итемов!</font></center><br><br></body></html>", player);
			}
			else
				separateAndSend("<html><body><br><br><center><font color=\"LEVEL\">Ошибка!</font></center><br><br></body></html>", player);
		}
		else if(command.startsWith("_bbshtmlpa"))
		{
			String html = "<html><body><br><br><center><font color=\"LEVEL\">Доступно только обладаьтелям Премиум-аккаунта!</font></center><br><br></body></html>";
			if(player.hasBonus())
				html = readHtml(ConfigValue.CommunityBoardHtmlRoot + command.substring(11) + ".htm", player);
			separateAndSend(html, player);
		}
		// bypass -h _bbs_buy_fame:count_fame:id_item:count_item:next_open_html
		else if(command.startsWith("_bbs_buy_fame"))
		{
			String[] param = command.split(":");

			if(DifferentMethods.getPay(player, Integer.parseInt(param[2]), Long.parseLong(param[3]), true))
				player.setFame(player.getFame() + Integer.parseInt(param[1]), "buy");

			//separateAndSend(readHtml(ConfigValue.CommunityBoardHtmlRoot + param[4] + ".htm", player), player);
		}
		else if(command.startsWith("_bbs_referal_get"))
		{
			List<String> res = getRefAccList(player, false);
			if(res.size() > 0)
			{
				try
				{
					int count = player.getVarInt("referal_count", 0);
					for(String name : res)
					{
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `stress_referal` SET `success`='1' WHERE `account`=? LIMIT 1", name);
						player.sendMessage("Вы получили награду, за приглашение '***"+name.substring(3)+"'.");
						for(int i=0; i < ConfigValue.VidakReferalRewardToRefer.length; i+=2)
							player.getInventory().addItem((int)ConfigValue.VidakReferalRewardToRefer[i], ConfigValue.VidakReferalRewardToRefer[i+1]);
					}
				}
				catch(SQLException e)
				{
					_log.warning("Unable to process referrals for player " + player);
					e.printStackTrace();
				}
			}

			res = getRefAccList(player, true);
			if(res.size() >= 10)
			{
				try
				{
					for(int i=0;i<(res.size()/10*10);i++)
					{
						String name = res.get(i);
						mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `stress_referal` SET `success`='2' WHERE `account`=? LIMIT 1", name);
						if(i%10==0)
						{
							player.sendMessage("Вы получили награду, за каждого 10-го, приглашеного игрока.");
							for(int i2=0; i2 < ConfigValue.VidakReferalRewardFor10Ref.length; i2+=2)
								player.getInventory().addItem((int)ConfigValue.VidakReferalRewardFor10Ref[i2], ConfigValue.VidakReferalRewardFor10Ref[i2+1]);
						}
					}
				}
				catch(SQLException e)
				{
					_log.warning("Unable to process referrals for player " + player);
					e.printStackTrace();
				}
			}
		}
		else if(command.startsWith("_bbs_referal_html"))
		{
			String content = readHtml(ConfigValue.CommunityBoardHtmlRoot+"referal.htm", player);
			
			int[] referal = getRefStat(player);
			content = content.replace("%referal_all%", String.valueOf(referal[0]));
			content = content.replace("%referal_noble%", String.valueOf(referal[1]));
			content = content.replace("%referal_new_noble%", (referal[2] > 0 ? "<font color=00FF00>" : "<font color=FF0000>")+referal[2]+"</font>");
			separateAndSend(content, player);
		}
		else
			separateAndSend("<html><body><br><br><center>В bbsbuff функция: " + command + " пока не реализована</center><br><br></body></html>", player);
	}

	public void showLotoWindow(L2Player player, int val)
	{
		SystemMessage sm;
		NpcHtmlMessage html = new NpcHtmlMessage(player, null);

		// if loto
		if(val == -1)
			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery.htm");
		else if(val == 0)
			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery_1.htm");
		else if(val >= 1 && val <= 21)
		{
			if(!LotteryManager.getInstance().isStarted())
			{
				/** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
				player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
				return;
			}
			if(!LotteryManager.getInstance().isSellableTickets())
			{
				/** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
				player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
				return;
			}

			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery_5.htm");

			int count = 0;
			int found = 0;

			// counting buttons and unsetting button if found
			for(int i = 0; i < 5; i++)
				if(player.getLoto(i) == val)
				{
					// unsetting button
					player.setLoto(i, 0);
					found = 1;
				}
				else if(player.getLoto(i) > 0)
					count++;

			// if not rearched limit 5 and not unseted value
			if(count < 5 && found == 0 && val <= 20)
				for(int i = 0; i < 5; i++)
					if(player.getLoto(i) == 0)
					{
						player.setLoto(i, val);
						break;
					}

			//setting pusshed buttons
			count = 0;
			for(int i = 0; i < 5; i++)
				if(player.getLoto(i) > 0)
				{
					count++;
					String button = String.valueOf(player.getLoto(i));
					if(player.getLoto(i) < 10)
						button = "0" + button;
					String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
					String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
					html.replace(search, replace);
				}
			if(count == 5)
			{
				String search = "0\">Return";
				String replace = "22\">The winner selected the numbers above.";
				html.replace(search, replace);
			}
			player.sendPacket(html);
			return; // ???
		}

		if(val == 22)
		{
			if(!LotteryManager.getInstance().isStarted())
			{
				/** LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD **/
				player.sendPacket(Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
				return;
			}
			if(!LotteryManager.getInstance().isSellableTickets())
			{
				/** TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE **/
				player.sendPacket(Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
				return;
			}

			int price = ConfigValue.AltLotteryPrice;
			int lotonumber = LotteryManager.getInstance().getId();
			int enchant = 0;
			int type2 = 0;
			for(int i = 0; i < 5; i++)
			{
				if(player.getLoto(i) == 0)
					return;
				if(player.getLoto(i) < 17)
					enchant += Math.pow(2, player.getLoto(i) - 1);
				else
					type2 += Math.pow(2, player.getLoto(i) - 17);
			}
			if(player.getAdena() < price)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
			player.reduceAdena(price, true);
			sm = new SystemMessage(SystemMessage.ACQUIRED__S1_S2);
			sm.addNumber(lotonumber);
			sm.addItemName(4442);
			player.sendPacket(sm);
			L2ItemInstance item = ItemTemplates.getInstance().createItem(4442);
			item.setCustomType1(lotonumber);
			item.setEnchantLevel(enchant);
			item.setCustomType2(type2);
			player.getInventory().addItem(item);
			Log.LogItem(player, Log.BuyItem, item);
			//html.setHtml("<html><body>Lottery Ticket Seller:<br>Thank you for playing the lottery<br>The winners will be announced at 7:00 pm <br><center><a action=\"bypass -h npc_%objectId%_Chat 0\">Back</a></center></body></html>");
			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery_22.htm");
		}
		else if(val == 23) //23 - current lottery jackpot
			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery_3.htm");
		else if(val == 24)
		{
			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery_4.htm");

			int lotonumber = LotteryManager.getInstance().getId();
			String message = "";

			for(L2ItemInstance item : player.getInventory().getItems())
			{
				if(item == null)
					continue;
				if(item.getItemId() == 4442 && item.getCustomType1() < lotonumber)
				{
					message = message + "<a action=\"bypass -h bbs:_bbs_lottery:" + item.getObjectId() + "\">" + item.getCustomType1() + " Event Number ";
					int[] numbers = LotteryManager.getInstance().decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
					for(int i = 0; i < 5; i++)
						message += numbers[i] + " ";
					int[] check = LotteryManager.getInstance().checkTicket(item);
					if(check[0] > 0)
					{
						switch(check[0])
						{
							case 1:
								message += "- 1st Prize";
								break;
							case 2:
								message += "- 2nd Prize";
								break;
							case 3:
								message += "- 3th Prize";
								break;
							case 4:
								message += "- 4th Prize";
								break;
						}
						message += " " + check[1] + "a.";
					}
					message += "</a><br>";
				}
			}
			if(message == "")
				message += "There is no winning lottery ticket...<br>";
			html.replace("%result%", message);
		}
		else if(val == 25)
			html.setFile(ConfigValue.CommunityBoardHtmlRoot+"lottery/lottery_2.htm");
		else if(val > 25)
		{
			int lotonumber = LotteryManager.getInstance().getId();
			L2ItemInstance item = player.getInventory().getItemByObjectId(val);
			if(item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber)
				return;
			int[] check = LotteryManager.getInstance().checkTicket(item);

			player.sendPacket(SystemMessage.removeItems(4442, 1));

			int adena = check[1];
			if(adena > 0)
				player.addAdena(adena);
			player.getInventory().destroyItem(item, 1, true);
			return;
		}

		//html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%race%", "" + LotteryManager.getInstance().getId());
		html.replace("%adena%", "" + LotteryManager.getInstance().getPrize());
		html.replace("%ticket_price%", "" + ConfigValue.LotteryTicketPrice);
		html.replace("%prize5%", "" + ConfigValue.Lottery5NumberRate * 100);
		html.replace("%prize4%", "" + ConfigValue.Lottery4NumberRate * 100);
		html.replace("%prize3%", "" + ConfigValue.Lottery3NumberRate * 100);
		html.replace("%prize2%", "" + ConfigValue.Lottery2and1NumberPrize);
		html.replace("%enddate%", "" + DateFormat.getDateInstance().format(LotteryManager.getInstance().getEndDate()));

		player.sendPacket(html);
		player.sendActionFailed();
	}

	private int[] getRefStat(L2Player player)
	{
		int all = 0;
		int noble = 0;
		int new_noble = 0;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT is_noobles, success FROM stress_referal WHERE referal='"+player.getAccountName()+"'");
			rset = statement.executeQuery();

			while(rset.next())
			{
				all++;
				int is_noobles = rset.getInt("is_noobles");
				int success = rset.getInt("success");
				if(is_noobles == 1)
				{
					if(success == 0)
						new_noble++;
					noble++;
				}
			}
		}
		catch(Exception e)
		{
			_log.warning("mSGI: Error in query:" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		return new int[]{all, noble, new_noble};
	}
	/**
	DROP TABLE IF EXISTS `stress_referal`;
	CREATE TABLE `stress_referal` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `account` varchar(100) NOT NULL,
	  `referal` varchar(100) NOT NULL,
	  `is_third_class` enum('0','1') NOT NULL DEFAULT '0',
	  `is_noobles` enum('0','1') NOT NULL DEFAULT '0',
	  `success` enum('0','1','2') NOT NULL DEFAULT '0',
	  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`),
	  KEY `account_referer` (`account`) USING BTREE,
	  KEY `success` (`success`),
	  KEY `is_third_class` (`is_third_class`),
	  KEY `is_noobles` (`is_noobles`)
	) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	**/
	// Пусть будет так, мне лень делать все в одном списке...
	private List<String> getRefAccList(L2Player player, boolean is_10)
	{
		List<String> res = new ArrayList<String>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			statement = con.prepareStatement("SELECT account FROM stress_referal WHERE referal='"+player.getAccountName()+"' AND is_noobles='1' AND success='"+(is_10 ? "1" : "0")+"'");
			rset = statement.executeQuery();

			if(rset.next())
				res.add(rset.getString("account"));
		}
		catch(Exception e)
		{
			_log.warning("mSGI: Error in query:" + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return res;
	}

	private static String initTimer()
	{
		Calendar ca = Calendar.getInstance();
		String times = "<br><br><center><font color=\"LEVEL\">Текущее время:</font> "+form.format(ca.getTime())+"</center><br><br>";

		times += "<br><center><font color=\"LEVEL\">Последний Герой(Last Hero):</font></center><br1>";
		times += initTimer(0);

		times += "<br><center><font color=\"LEVEL\">Команда на Команду(TvT):</font></center><br1>";
		times += initTimer(1);

		times += "<br><center><font color=\"LEVEL\">Захват Флага(CtF):</font></center><br1>";
		times += initTimer(2);

		times += "<br><center><font color=\"LEVEL\">Группа на Группу(GvG):</font></center><br1>";
		times += initTimer(3);

		if(ConfigValue.FightClubEnabled)
			times += "<br><center><font color=\"LEVEL\">Бойцовский Клуб(Fight Club):</font> <font color=00ff00>Активен</font></center><br1>";
		else
			times += "<br><center><font color=\"LEVEL\">Бойцовский Клуб(Fight Club):</font> <font color=ff0000>Отключен</font></center><br1>";

		if(Functions.IsActive("CofferofShadows"))
			times += "<center><font color=\"LEVEL\">Сундук Теней(Coffer of Shadows):</font> <font color=00ff00>Активен</font></center><br1>";
		else
			times += "<center><font color=\"LEVEL\">Сундук Теней(Coffer of Shadows):</font> <font color=ff0000>Отключен</font></center><br1>";

		if(Functions.IsActive("glitter"))
			times += "<center><font color=\"LEVEL\">Сверкающие Медали(Glittering Medals):</font> <font color=00ff00>Активен</font></center><br1>";
		else
			times += "<center><font color=\"LEVEL\">Сверкающие Медали(Glittering Medals):</font> <font color=ff0000>Отключен</font></center><br1>";

		if(Functions.IsActive("TheFallHarvest"))
			times += "<center><font color=\"LEVEL\">Сбор Урожая(The Fall Harvest):</font> <font color=00ff00>Активен</font></center><br1>";
		else
			times += "<center><font color=\"LEVEL\">Сбор Урожая(The Fall Harvest):</font> <font color=ff0000>Отключен</font></center><br1>";

		if(Functions.IsActive("MasterOfEnchanting"))
			times += "<center><font color=\"LEVEL\">Мастер Заточки(Master Of Enchanting):</font> <font color=00ff00>Активен</font></center><br1>";
		else
			times += "<center><font color=\"LEVEL\">Мастер Заточки(Master Of Enchanting):</font> <font color=ff0000>Отключен</font></center><br1>";
		return times;
	}

	// Возвращает расписание ивентов @id = 0 - LasrHero, @id = 1 - TvT, @id = 2 - CtF
	private static String initTimer(int id)
	{
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy - HH:mm");

		int [] time = new int[0];
		boolean active = false;
		switch(id)
		{
			case 0:
				time = ConfigValue.LastHeroStartTime;
				active = events.lastHero.LastHero.isRunned();
				break;
			case 1:
				time = ConfigValue.TeamvsTeamStartTime;
				active = events.TvT.TvT.isRunned();
				break;
			case 2:
				time = ConfigValue.CaptureTheFlagStartTime;
				active = events.CtF.CtF.isRunned();
				break;
			case 3:
				time = ConfigValue.GvG_StartTimeList;
				active = events.GvG.GvG.isActive();
				break;
		}
		List<Long> time2 = new ArrayList<Long>();
		for(int i=0;i<time.length;i+=2)
		{
			Calendar ci = Calendar.getInstance();
			ci.set(Calendar.HOUR_OF_DAY, time[i]);
			ci.set(Calendar.MINUTE, time[i+1]);
			ci.set(Calendar.SECOND, 00);

			long delay = ci.getTimeInMillis() - System.currentTimeMillis();
			if(delay > -900000)
				time2.add(delay);
			ci = null;
		}
		Collections.sort(time2);
		String times = "<font color=ff0000>Сегодня больше не проводится.</font>";
		for(long time3 : time2)
		{
			if(times.equals("<font color=ff0000>Сегодня больше не проводится.</font>"))
				times = "";

			c.add(Calendar.MILLISECOND, (int)time3);
			if(time3 > 0)
				times += format.format(c.getTime())+"<br1>";
			else if(active)
				times += "<font color=00ff00>"+format.format(c.getTime())+" (Активен)</font><br1>";
			c.add(Calendar.MILLISECOND, -(int)time3);
		}
		c=null;
		return times;
	}

	public void addPoints(L2Player player, int add, boolean set_game)
	{
        try
		{
            mysql.setEx(set_game ? L2DatabaseFactory.getInstance() : L2DatabaseFactory.getInstanceLogin(), "UPDATE "+(set_game ? "market_point" : "`accounts`")+" SET `points`=points+? WHERE `login`=?", add, player.getAccountName());
        }
		catch (SQLException e)
		{
            e.printStackTrace();
        }
        player.sendPacket(new ExBR_GamePoint(player.getObjectId(), player.getPoint(false)));
    }

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
	}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}