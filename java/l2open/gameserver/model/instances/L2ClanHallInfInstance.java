package l2open.gameserver.model.instances;

import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.siege.clanhall.BanditStrongholdSiege;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.model.entity.siege.clanhall.WildBeastFarmSiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class L2ClanHallInfInstance extends L2NpcInstance
{
	private static final Logger log = Logger.getLogger(L2NpcInstance.class.getName());

	public L2ClanHallInfInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void onBypassFeedback(L2Player player, String command)
	{
		if(command.startsWith("Registration"))
		{
			L2Clan playerClan = player.getClan();
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			StringBuilder str = new StringBuilder("<html><body>Вестник!<br>");

			switch(getTemplate().getNpcId())
			{
				case 35437:
					if(!BanditStrongholdSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					if(playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
					{
						showChatWindow(player, 1);
						return;
					}
					if(BanditStrongholdSiege.getInstance().clanhall.getOwner() == playerClan)
					{
						str.append("Ваш Клан уже зарегестрирован на осаду, что вы еще хотите от меня?<br>");
						str.append("<a action=\"bypass -h npc_%objectId%_PlayerList\">Добавить/удалить участника осады</a><br>");
					}
					else if(BanditStrongholdSiege.getInstance().isClanOnSiege(playerClan))
					{
						str.append("Ваш Клан уже зарегестрирован на осаду, что вы еще хотите от меня?<br>");
						str.append("<a action=\"bypass -h npc_%objectId%_UnRegister\">Отменить регистрацию</a><br>");
						str.append("<a action=\"bypass -h npc_%objectId%_PlayerList\">Добавить/удалить участника осады</a><br>");
					}
					else
					{
						if(player.getClanHall() != null)
						{
							str.append("Ваша заявка на участие в соревновании за обладание Холл Кланом не принята, вы уже владеете другим Холл Клана.");
						}
						else
						{
							int res = BanditStrongholdSiege.getInstance().registerClanOnSiege(player, playerClan);
							if(res == 0)
							{
								str.append("Ваш клан : <font color=\"LEVEL\">").append(player.getClan().getName()).append("</font>, успешно зарегистрирован на осаду Холл Клана.<br>");
								str.append("Теперь Вам необходимо выбрать не более 18 игоков, которые примут участие в осаде, из членов вашего клана.<br>");
								str.append("<a action=\"bypass -h npc_%objectId%_PlayerList\">Выбрать участников осады</a><br>");
							}
							else if(res == 1)
							{
								str.append("Вы не прошли испытание и не получили Право на участие в осаде Крепости Разбойников<br>");
								str.append("Возвращайтесь когда все будет готово.");
							}
							else if(res == 2)
							{
								str.append("К сожалению вы опоздали. Пять лидеров кланов уже подали заявки на регистрацию.<br>");
								str.append("В следующий раз будьте более разторопны.");
							}
						}
					}
				break;
				case 35627:
					if(!WildBeastFarmSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					if(playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
					{
						showChatWindow(player, 1);
						return;
					}
					if(WildBeastFarmSiege.getInstance().clanhall.getOwner() == playerClan)
					{
						str.append("Ваш Клан уже зарегестрирован на осаду, что вы еще хотите от меня?<br>");
						str.append("<a action=\"bypass -h npc_%objectId%_PlayerList\">Добавить/удалить участника осады</a><br>");
					}
					else if(WildBeastFarmSiege.getInstance().isClanOnSiege(playerClan))
					{
						str.append("Ваш Клан уже зарегестрирован на осаду, что вы еще хотите от меня?<br>");
						str.append("<a action=\"bypass -h npc_%objectId%_UnRegister\">Отменить регистрацию</a><br>");
						str.append("<a action=\"bypass -h npc_%objectId%_PlayerList\">Добавить/удалить участника осады</a><br>");
					}
					else
					{
						if(player.getClanHall() != null)
						{
							str.append("Ваша заявка на участие в соревновании за обладание Холл Кланом не принята, вы уже владеете другим Холл Клана.");
						}
						else
						{
							int res = WildBeastFarmSiege.getInstance().registerClanOnSiege(player, playerClan);
							if(res == 0)
							{
								str.append("Ваш клан : <font color=\"LEVEL\">").append(player.getClan().getName()).append("</font>, успешно зарегистрирован на осаду Холл Клана.<br>");
								str.append("Теперь Вам необходимо выбрать не более 18 игоков, которые примут участие в осаде, из членов вашего клана.<br>");
								str.append("<a action=\"bypass -h npc_%objectId%_PlayerList\">Выбрать участников осады</a><br>");
							}
							else if(res == 1)
							{
								str.append("Вы не прошли испытание и не получили Право на участие в осаде Крепости Разбойников<br>");
								str.append("Возвращайтесь когда все будет готово.");
							}
							else if(res == 2)
							{
								str.append("К сожалению вы опоздали. Пять лидеров кланов уже подали заявки на регистрацию.<br>");
								str.append("В следующий раз будьте более разторопны.");
							}
						}
					}
				break;
				case 35604:
					if(!RainbowSpringSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 6);
						return;
					}
					if(playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
					{
						showChatWindow(player, 4);
						return;
					}
					if(RainbowSpringSiege.getInstance().clanhall.getOwner() == playerClan)
					{
						str.append("Ваш Клан уже зарегестрирован на осаду, что вы еще хотите от меня?<br>");
					}
					else if(RainbowSpringSiege.getInstance().isClanOnSiege(playerClan))
					{
						str.append("Ваш Клан уже подал заявку на участие в соревновании за обладание Холл Кланом, что вы еще хотите от меня?<br>");
					}
					else
					{
						if(player.getClanHall() != null)
						{
							str.append("Ваша заявка на участие в соревновании за обладание Холл Кланом не принята, вы уже владеете другим Холл Клана.");
						}
						else
						{
							int res = RainbowSpringSiege.getInstance().registerClanOnSiege(player, playerClan);
							if(res > 0)
							{
								str.append("Ваша заявка на участие в соревновании за обладание Холл Кланом принята, вы внесли <font color=\"LEVEL\">").append(res).append(" Свидетельство Участия в Войне за Холл Клана Горячего Источника</font>.<br>");
							}
							else
							{
								str.append("Для подачи заявки на участие в соревновании за обладание Холл Кланом, необходимо добыть как можно больше <font color=\"LEVEL\">Свидетельств Участия в Войне за Холл Клана Горячего Источника</font>.<br>");
							}
						}
					}
				break;
			}

			str.append("</body></html>");
			html.setHtml(str.toString());
			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if(command.startsWith("UnRegister"))
		{
			L2Clan playerClan = player.getClan();

			StringBuilder str = new StringBuilder("<html><body>Вестник!<br>");
			if(playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
			{
				log.warning(new StringBuilder().append("Attention!!! player ").append(player.getName()).append(" use packet hack, try unregister clan.").toString());
				return;
			}
			NpcHtmlMessage html;
			switch(getTemplate().getNpcId())
			{
				case 35437:
					if(!BanditStrongholdSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					html = new NpcHtmlMessage(getObjectId());
					if(BanditStrongholdSiege.getInstance().isClanOnSiege(playerClan))
					{
						if(BanditStrongholdSiege.getInstance().unRegisterClan(playerClan))
						{
							str.append("Ваш клан : <font color=\"LEVEL\">").append(player.getClan().getName()).append("</font>, успешно снят с регистрации на осаду Холл Клана.<br>");
							str.append("</body></html>");
							html.setHtml(str.toString());
							html.replace("%objectId%", String.valueOf(getObjectId()));
							player.sendPacket(html);
						}

					}
					else
					{
						log.warning(new StringBuilder().append("Attention!!! player ").append(player.getName()).append(" use packet hack, try unregister clan.").toString());
					}
				break;
				case 35627:
					if(!WildBeastFarmSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					html = new NpcHtmlMessage(getObjectId());
					if(WildBeastFarmSiege.getInstance().isClanOnSiege(playerClan))
					{
						if(WildBeastFarmSiege.getInstance().unRegisterClan(playerClan))
						{
							str.append("Ваш клан : <font color=\"LEVEL\">").append(player.getClan().getName()).append("</font>, успешно снят с регистрации на осаду Холл Клана.<br>");
							str.append("</body></html>");
							html.setHtml(str.toString());
							html.replace("%objectId%", String.valueOf(getObjectId()));
							player.sendPacket(html);
						}
					}
					else
					{
						log.warning(new StringBuilder().append("Attention!!! player ").append(player.getName()).append(" use packet hack, try unregister clan.").toString());
					}
				break;
				case 35604:
					if(!RainbowSpringSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 6);
						return;
					}
					html = new NpcHtmlMessage(getObjectId());
					if(RainbowSpringSiege.getInstance().isClanOnSiege(playerClan) && RainbowSpringSiege.getInstance().unRegisterClan(player))
					{
						str.append("Ваш клан : <font color=\"LEVEL\">").append(player.getClan().getName()).append("</font>, успешно снят с регистрации на осаду Холл Клана.<br>");
						str.append("</body></html>");
						html.setHtml(str.toString());
						html.replace("%objectId%", String.valueOf(getObjectId()));
						player.sendPacket(html);
					}
				break;
			}
		}
		else if(command.startsWith("PlayerList"))
		{
			L2Clan playerClan = player.getClan();
			if (playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
			{
				return;
			}
			switch (getTemplate().getNpcId())
			{
				case 35627:
					if(!WildBeastFarmSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					if(WildBeastFarmSiege.getInstance().isClanOnSiege(playerClan))
						showPlayersList(playerClan, player);
				break;
				case 35437:
					if(!BanditStrongholdSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					if(BanditStrongholdSiege.getInstance().isClanOnSiege(playerClan))
					{
						showPlayersList(playerClan, player);
					}
				break;
			}
		}
		else if(command.startsWith("addPlayer"))
		{
			L2Clan playerClan = player.getClan();
			String val = command.substring(10);
			if(playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
			{
				return;
			}
				switch(getTemplate().getNpcId())
				{
					case 35627:
						if(!WildBeastFarmSiege.getInstance().isRegistrationPeriod())
						{
							showChatWindow(player, 3);
							return;
						}
						if(playerClan.getClanMember(val) == null)
						{
							return;
						}
						WildBeastFarmSiege.getInstance().addPlayer(playerClan, val);
						if(WildBeastFarmSiege.getInstance().isClanOnSiege(playerClan))
							showPlayersList(playerClan, player);
					break;
					case 35437:
						if(!BanditStrongholdSiege.getInstance().isRegistrationPeriod())
						{
							showChatWindow(player, 3);
							return;
						}
						if(playerClan.getClanMember(val) == null)
						{
							return;
						}
						BanditStrongholdSiege.getInstance().addPlayer(playerClan, val);
						if(BanditStrongholdSiege.getInstance().isClanOnSiege(playerClan))
						{
							showPlayersList(playerClan, player);
						}
					break;
				}
		}
		else if(command.startsWith("removePlayer"))
		{
			L2Clan playerClan = player.getClan();
			String val = command.substring(13);
			if(playerClan == null || !playerClan.getLeaderName().equals(player.getName()) || playerClan.getLevel() < 4)
			{
				return;
			}
			switch(getTemplate().getNpcId())
			{
				case 35627:
					if(!WildBeastFarmSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					if(playerClan.getClanMember(val) != null)
					{
						WildBeastFarmSiege.getInstance().removePlayer(playerClan, val);
					}
					if(WildBeastFarmSiege.getInstance().isClanOnSiege(playerClan))
						showPlayersList(playerClan, player);
				break;
				case 35437:
					if(!BanditStrongholdSiege.getInstance().isRegistrationPeriod())
					{
						showChatWindow(player, 3);
						return;
					}
					if(playerClan.getClanMember(val) != null)
					{
						BanditStrongholdSiege.getInstance().removePlayer(playerClan, val);
					}
					if(BanditStrongholdSiege.getInstance().isClanOnSiege(playerClan))
					{
						showPlayersList(playerClan, player);
					}
				break;
			}
		}
		else if(command.startsWith("RegClanlist"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			StringBuilder str = new StringBuilder();
			str.append("<html><body>Вестник!<br>");
			switch(getTemplate().getNpcId())
			{
				case 35627:
					if(!WildBeastFarmSiege.getInstance().getRegisteredClans().isEmpty())
					{
						str.append("Список кланов зарегистрированных на осаду:<br><br>");
						for(String temp : WildBeastFarmSiege.getInstance().getRegisteredClans())
						{
							str.append(temp); str.append("<br>");
						}
					}
					else
					{
						str.append("Период регистрации еще не начался");
					}
					str.append("</body></html>");
				break;
				case 35437:
					if(!BanditStrongholdSiege.getInstance().getRegisteredClans().isEmpty())
					{
						str.append("Список кланов зарегистрированных на осаду:<br><br>");
						for(String temp : BanditStrongholdSiege.getInstance().getRegisteredClans())
						{
							str.append(temp); str.append("<br>");
						}
					}
					else
					{
						str.append("Период регистрации еще не начался");
					}
					str.append("</body></html>");
				break;
			}
			html.setHtml(str.toString());
			player.sendPacket(html);
		}
		else if(command.startsWith("EnterHotSprArena"))
		{
			if(!RainbowSpringSiege.getInstance().enterOnArena(player))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				StringBuilder str = new StringBuilder();
				str.append("<html><body>Вестник!<br>");
				str.append("Вы не выполнили условия для входа<br><br>");
				str.append("</body></html>");
				html.setHtml(str.toString());
				player.sendPacket(html);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	public void showPlayersList(L2Clan playerClan, L2Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		StringBuilder str = new StringBuilder("<html><body>Вестник!<br>");
		str.append("Ваш клан : <font color=\"LEVEL\">").append(player.getClan().getName()).append("</font>. выберите участников для осады.<br><br>");
		str.append("<img src=\"L2UI.SquareWhite\" width=280 height=1>");
		str.append("<table width=280 border=0 bgcolor=\"000000\"><tr><td width=170 align=center>Зарегестрированные</td><td width=110 align=center>действие</td></tr></table>");
		str.append("<img src=\"L2UI.SquareWhite\" width=280 height=1>");
		str.append("<table width=280 border=0>");

		switch(getTemplate().getNpcId())
		{
			case 35627:
				for(String temp : WildBeastFarmSiege.getInstance().getRegisteredPlayers(playerClan))
				{
					str.append("<tr><td width=170>").append(temp).append("</td><td width=110 align=center><a action=\"bypass -h npc_%objectId%_removePlayer ").append(temp).append("\"> Удалить</a></td></tr>");
				}
				str.append("</table>");
				str.append("<img src=\"L2UI.SquareWhite\" width=280 height=1>");
				str.append("<table width=280 border=0 bgcolor=\"000000\"><tr><td width=170 align=center>Члены Клана</td><td width=110 align=center>действие</td></tr></table>");
				str.append("<img src=\"L2UI.SquareWhite\" width=280 height=1>");
				str.append("<table width=280 border=0>");
				for(L2ClanMember temp : playerClan.getMembers())
				{
					if(WildBeastFarmSiege.getInstance().getRegisteredPlayers(playerClan).contains(temp.getName()))
						continue;
					str.append("<tr><td width=170>").append(temp.getName()).append("</td><td width=110 align=center><a action=\"bypass -h npc_%objectId%_addPlayer ").append(temp.getName()).append("\"> Добавить</a></td></tr>");
				}
			break;
			case 35437:
				for(String temp : BanditStrongholdSiege.getInstance().getRegisteredPlayers(playerClan))
				{
					str.append("<tr><td width=170>").append(temp).append("</td><td width=110 align=center><a action=\"bypass -h npc_%objectId%_removePlayer ").append(temp).append("\"> Удалить</a></td></tr>");
				}
				str.append("</table>");
				str.append("<img src=\"L2UI.SquareWhite\" width=280 height=1>");
				str.append("<table width=280 border=0 bgcolor=\"000000\"><tr><td width=170 align=center>Члены Клана</td><td width=110 align=center>действие</td></tr></table>");
				str.append("<img src=\"L2UI.SquareWhite\" width=280 height=1>");
				str.append("<table width=280 border=0>");
				for(L2ClanMember temp : playerClan.getMembers())
				{
					if(BanditStrongholdSiege.getInstance().getRegisteredPlayers(playerClan).contains(temp.getName()))
						continue;
					str.append("<tr><td width=170>").append(temp.getName()).append("</td><td width=110 align=center><a action=\"bypass -h npc_%objectId%_addPlayer ").append(temp.getName()).append("\"> Добавить</a></td></tr>");
				}
			break;
		}
		str.append("</table>");
		str.append("</body></html>");
		html.setHtml(str.toString());
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}

	public void showChatWindow(L2Player player, int val)
	{
		player.sendActionFailed();
		long startSiege = 0;
		int npcId = getTemplate().getNpcId();
		StringBuilder filename = new StringBuilder("data/html/default/");
		if(val == 0)
		{
			filename.append(npcId).append(".htm");
		}
		else
		{
			filename.append(npcId).append("-").append(val).append(".htm");
		}
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());

		html.setFile(filename.toString());

		if(npcId == 35437 || npcId == 35627 || npcId == 35604)
		{
			ClanHall clanhall = null;
			StringBuilder clans = new StringBuilder("<table width=280 border=0>");

			int clanCount = 0;

			switch (npcId)
			{
				case 35437:
					clanhall = ClanHallManager.getInstance().getClanHall(35);
					startSiege = BanditStrongholdSiege.getInstance().getSiegeDate().getTimeInMillis() - 3600000;
					for(String a : BanditStrongholdSiege.getInstance().getRegisteredClans())
					{
						clanCount++;
						clans.append("<tr><td><font color=\"LEVEL\">").append(a).append("</font>  (Количество :").append(BanditStrongholdSiege.getInstance().getPlayersCount(a)).append("чел.)</td></tr>");
					}
				break;
				case 35627:
					clanhall = ClanHallManager.getInstance().getClanHall(63);
					startSiege = WildBeastFarmSiege.getInstance().getSiegeDate().getTimeInMillis() - 3600000;
					for(String a : WildBeastFarmSiege.getInstance().getRegisteredClans())
					{
						clanCount++;
						clans.append("<tr><td><font color=\"LEVEL\">").append(a).append("</font>  (Количество :").append(WildBeastFarmSiege.getInstance().getPlayersCount(a)).append("чел.)</td></tr>");
					}
				break;
				case 35604:
					clanhall = ClanHallManager.getInstance().getClanHall(62);
					startSiege = RainbowSpringSiege.getInstance().getSiegeDate().getTimeInMillis();
				break;
			}

			while(clanCount < 5)
			{
				clans.append("<tr><td><font color=\"LEVEL\">**Не зарегистрирован**</font>  (Количество : чел.)</td></tr>");
				clanCount++;
			}
			clans.append("</table>");
			html.replace("%clan%", String.valueOf(clans));
			L2Clan clan = clanhall.getOwner();
			String clanName;
			if(clan == null)
			{
				clanName = "НПЦ";
			}
			else
			{
				clanName = clan.getName();
			}
			html.replace("%clanname%", String.valueOf(clanName));
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		html.replace("%SiegeDate%", String.valueOf(format.format(startSiege)));
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
}
