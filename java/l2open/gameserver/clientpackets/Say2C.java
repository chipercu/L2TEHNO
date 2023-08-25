package l2open.gameserver.clientpackets;

import l2open.config.*;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.*;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExRpItemLink;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.FakePlayersTable;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.player.PlayerData;
import l2open.status.GameStatusThread;
import l2open.status.Status;
import l2open.util.*;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Say2C extends L2GameClientPacket
{
	private static Logger _log = Logger.getLogger(Say2C.class.getName());
	public static HashMap<String, String> _online = new HashMap<String, String>();

	/** RegExp для кэширования линков предметов */
	private static final Pattern EX_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+[\\s]+\tID=([0-9]+)[\\s]+\tColor=[0-9]+[\\s]+\tUnderline=[0-9]+[\\s]+\tTitle=\033(.[^\033]*)[^\b]");
	private static final Pattern SKIP_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+(.[^\b]*)[\b]");


	public static String quiet = "";
	public final static int ALL = 0;
	public final static int ALL_CHAT_RANGE = 1250; //Дальность белого чата
	public final static int SHOUT = 1; //!
	public final static int TELL = 2; //\"
	public final static int PARTY = 3; //#
	public final static int CLAN = 4; //@
	public final static int GM = 5;
	public final static int PETITION_PLAYER = 6; //& used for petition
	public final static int PETITION_GM = 7; //* used for petition
	public final static int TRADE = 8; //+
	public final static int ALLIANCE = 9; //$
	public final static int ANNOUNCEMENT = 10;
	public final static int SYSTEM_MESSAGE = 11;
	public final static int L2FRIEND = 12;
	public final static int MSNCHAT = 13;
	public final static int PARTY_ROOM = 14;
	public final static int COMMANDCHANNEL_ALL = 15; //`` (pink) команды лидера СС
	public final static int COMMANDCHANNEL_COMMANDER = 16; //` (yellow) чат лидеров партий в СС
	public final static int HERO_VOICE = 17; //%
	public final static int CRITICAL_ANNOUNCEMENT = 18; //dark cyan
	public final static int UNKNOWN = 19; //?
	public final static int BATTLEFIELD = 20; //^
	public final static int MPCC_ROOM = 21; // добавлен в епилоге, подобия PARTY_ROOM ток для СС
	public final static int NPC_ALL = 22; // Аналог ALL но для npc
	public final static int NPC_SHOUT = 23; // Аналог SHOUT но для npc

	public static String[] chatNames = { "ALL	", "SHOUT", "TELL ", "PARTY", "CLAN ", "GM	 ", "PETITION_PLAYER",
			"PETITION_GM", "TRADE", "ALLIANCE", "ANNOUNCEMENT", "SYSTEM_MESSAGE", "L2FRIEND", "MSNCHAT", "PARTY_ROOM", "COMMANDCHANNEL_ALL",
			"COMMANDCHANNEL_COMMANDER", "HERO_VOICE", "CRITICAL_ANNOUNCEMENT", "UNKNOWN", "BATTLEFIELD", "MPCC_ROOM", "NPC_ALL", "NPC_SHOUT" };

	protected static GArray<String> _banned = new GArray<String>();
	private String _text;
	private int _type;
	private String _target;

	/**
	 * packet type id 0x49
	 * format:		cSd (S)
	 * @param
	 */
	@Override
	public void readImpl()
	{
		_text = readS(ConfigValue.ChatMessageLimit);
		_type = readD();
		_target = _type == TELL ? readS(ConfigValue.cNameMaxLen) : null;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_type < 0 || _type > chatNames.length || _text == null || _text.length() == 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!quiet.isEmpty() && !activeChar.isGM())
		{
			activeChar.sendMessage("Чат всего игрового мира временно заблокировал гм: "+quiet);
			return;
		}
		
		if(activeChar.isInEvent() == 11)
		{
			activeChar.sendMessage("На ивенте 'Фестиваль Хаоса', чат не доступен.");
			return;
		}
		
		_text = _text.replaceAll("\\\\n", "\n");

		if(_text.contains("\n"))
		{
			String[] lines = _text.split("\n");
			_text = "";
			for(int i = 0; i < lines.length && i < ConfigValue.ChatMaxLines; i++)
			{
				lines[i] = lines[i].trim();
				if(lines[i].length() == 0)
					continue;
				if(_text.length() > 0)
					_text += "\n  >";
				if(ConfigValue.ChatLineLength > 0 && lines[i].length() > ConfigValue.ChatLineLength)
					i++;
				_text += lines[i];
			}
		}

		if(_text.length() == 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(ConfigValue.LogTelnet)
		{
			String line_output;

			if(_type == TELL)
				line_output = chatNames[_type] + "[" + activeChar.getName() + " to " + _target + "] " + _text;
			else
				line_output = chatNames[_type] + "[" + activeChar.getName() + "] " + _text;
			telnet_output(line_output, _type);
		}

		if(_text.startsWith("."))
		{
			String fullcmd = _text.substring(1).trim();
			String command = fullcmd.split("\\s+")[0];
			String args = fullcmd.substring(command.length()).trim();

			if(command.length() > 0)
			{
				// then check for VoicedCommands
				IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
				if(vch != null)
				{
					vch.useVoicedCommand(command, activeChar, args);
					return;
				}
			}
			activeChar.sendMessage("Wrong command"+(activeChar.isGM() ? " '"+command+"' arg='"+args+"'" : ""));
			return;
		}
		else if(_text.startsWith("==") || _text.startsWith("--"))
			return;

		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockChat(activeChar, _type))
			return;
		boolean globalchat = _type != ALLIANCE && _type != CLAN && _type != PARTY;
		boolean chan_banned = false;
		for(int type : ConfigValue.MAT_BAN_CHANNEL)
			if(_type == type)
				chan_banned = true;
		if((globalchat || chan_banned || ConfigValue.AllChatBan) && activeChar.getNoChannel() != 0)
		{
			if(activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0)
			{
				if(activeChar.getNoChannel() > 0)
				{
					int timeRemained = Math.round(activeChar.getNoChannelRemained() / 60000);
					activeChar.sendMessage(new CustomMessage("common.ChatBanned", activeChar).addNumber(timeRemained));
				}
				else
					activeChar.sendMessage(new CustomMessage("common.ChatBannedPermanently", activeChar));
				activeChar.sendActionFailed();
				return;
			}
			PlayerData.getInstance().updateNoChannel(activeChar, 0);
		}

		if(globalchat)
		{
			if(ConfigValue.MAT_REPLACE)
			{
				if(ConfigSystem.containsMat(_text))
				{
					_text = ConfigValue.MAT_REPLACE_STRING;
					activeChar.sendActionFailed();
				}
			}
			else if(ConfigValue.MAT_BANCHAT && ConfigSystem.containsMat(_text))
			{
				activeChar.sendMessage("You are banned in all chats. Time to unban: " + ConfigValue.Timer_to_UnBan * 60 + "sec.");
				Log.add("" + activeChar + ": " + _text, "abuse");
				PlayerData.getInstance().updateNoChannel(activeChar, ConfigValue.Timer_to_UnBan * 60000);
				activeChar.sendActionFailed();
				return;
			}
			else if(ConfigValue.MatAddItemId.length > 0 && ConfigValue.MatAddItemId[0] > 0 && ConfigSystem.containsMat(_text))
			{
				if(activeChar.getInventory().getCountOf((int)ConfigValue.MatAddItemId[0]) >= ConfigValue.MatAddItemId[2])
				{
					//Log.add("Char: " + getName() + ", Punishment: Ban after " + ConfigValue.ByPassFailPunishmentTime + " day, bypass: '" + text + "'", "bypass_fail");
					activeChar.setAccessLevel(-100);
					AutoBan.Banned(activeChar, ConfigValue.ChatBanDay, "Fail Bypass", "ByPassFail");
					activeChar.logout(false, false, true, true);
					return;
				}
				else
				{
					activeChar.getInventory().addItem((int)ConfigValue.MatAddItemId[0], ConfigValue.MatAddItemId[1]);
					Log.add("" + activeChar + ": " + _text, "abuse");
					activeChar.sendMessage("Вы получили блокировку возможности писать в чат. Время блокировки: " + ConfigValue.Timer_to_UnBan + " минут(ы).");
					activeChar.sendActionFailed();
					return;
				}
			}
		}

		Matcher m = EX_ITEM_LINK_PATTERN.matcher(_text);

		boolean shift_item=false;
		while(m.find())
		{
			int objectId = Integer.parseInt(m.group(1));
			L2ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);

			if(item == null)
				return;

			shift_item=true;
			ItemInfoCache.getInstance().put(item);
			if(ConfigValue.ShiftItemToTradeChats && _type == SHOUT)
				_type = TRADE;
		}

		if((ConfigValue.TradeChatsReplaceFromAll && _type == ALL) || (ConfigValue.TradeChatsReplaceFromShout && _type == SHOUT))
			for(Pattern TRADE_WORD : ConfigSystem.TRADE_WORDS)
				if(TRADE_WORD.matcher(_text).matches())
				{
					_type = TRADE;
					break;
				}

		LogChat.add(_text, chatNames[_type], activeChar.getName(), _type == TELL ? _target : null);

		String translit = activeChar.getVar("translit");
		if(translit != null)
		{
			m = SKIP_ITEM_LINK_PATTERN.matcher(_text);
			StringBuilder sb = new StringBuilder();
			int end = 0;
			while(m.find())
			{
				sb.append(Strings.fromTranslit(_text.substring(end, end = m.start()), translit.equals("tl") ? 1 : 2));
				sb.append(_text.substring(end, end = m.end()));
			}

			_text = sb.append(Strings.fromTranslit(_text.substring(end, _text.length()), translit.equals("tl") ? 1 : 2)).toString();
		}

		Say2 cs = new Say2(activeChar.getObjectId(), _type, activeChar.getName(), _text);
		int mapregion = MapRegion.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
		long curTime = System.currentTimeMillis();

		// 
		if(activeChar.chat_time+ConfigValue.ChatMessageInterval > curTime)
		{
			// TODO: бан чара/чата, по желанию.
			activeChar.chat_time = curTime;
			return;
		}
		else if(activeChar.chat_time+5000 > curTime && !activeChar.chat_text.isEmpty() && activeChar.chat_text.equals(_text.toLowerCase()))
		{
			// TODO: бан чара/чата, по желанию.
			activeChar.chat_time = curTime;
			return;
		}
		// getOnlineTime
		else if(ConfigValue.VidakChatCharLife > 0 && activeChar.getCreateTime() + (ConfigValue.VidakChatCharLife*1000) > curTime && _type != ALL && _type != HERO_VOICE)
		{
			activeChar.sendMessage("Новым персонажам запрещено писать в общий чат или пм в течении "+(ConfigValue.VidakChatCharLife/60)+" минут.");
			return;
		}
		activeChar.chat_time = curTime;
		activeChar.chat_text = _text.toLowerCase();
		switch(_type)
		{
			case TELL:
				if(activeChar.getLevel() < ConfigValue.minTELLlevel /*&& !activeChar.isSubClassActive()*/ && activeChar.canPenaltyChat())
				{
					activeChar.sendMessage("Tell chat is allowed only for characters level higher "+ConfigValue.minTELLlevel+".");
					return;
				}
				else if(ConfigValue.ChatTellCharLife > 0 && activeChar.getCreateTime() + (ConfigValue.ChatTellCharLife*1000) > curTime)
				{
					activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatTellCharLife/60)+" минут.");
					return;
				}
				else if(ConfigValue.ChatTellCharOnline > 0 && activeChar.getOnlineTime() < (ConfigValue.ChatTellCharOnline*1000))
				{
					activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatTellCharOnline/60)+" минут.");
					return;
				}
				if(ConfigValue.ChatTellFilterEnable)
					activeChar.addChat(_text, 2);
				L2Player receiver = L2World.getPlayer(_target);
				if(receiver == null && ConfigValue.AllowFakePlayers && FakePlayersTable.getActiveFakePlayers().contains(_target.toLowerCase()))
				{
					cs = new Say2(activeChar.getObjectId(), _type, "->" + _target, _text);
					activeChar.sendPacket(cs);
					return;
				}
				else if(receiver != null && receiver.isInOfflineMode())
				{
					activeChar.sendMessage("The person is in offline trade mode");
					activeChar.sendActionFailed();
				}
				else if(receiver != null && !receiver.isInBlockList(activeChar) && !receiver.isBlockAll())
				{
					if(activeChar.TellChatLaunched + (activeChar.getLevel() >= 20 ? 1000L : 10000L) > curTime)
					{
						activeChar.sendMessage("Tell chat is allowed once per " + (activeChar.getLevel() >= 20 ? "1 second." : "10 seconds."));
						return;
					}
					activeChar.TellChatLaunched = curTime;

					if(!receiver.getMessageRefusal())
					{
						receiver.sendPacket(cs);
						cs = new Say2(activeChar.getObjectId(), _type, "->" + receiver.getName(), _text);
						activeChar.sendPacket(cs);
						//if(receiver.isPhantom())
						//	((phantoms.model.Phantom)receiver).doAction(new phantoms.action.ChatAnswerAction(activeChar));

						if(receiver.isGM() && receiver.getVarB("CharTeleMy", false) && !activeChar.isInRange(receiver, 500) && !_online.containsKey(activeChar.getHWIDs()) && (receiver.getVarInt("CharTeleMyCount", -10) == -10 || receiver.getVarInt("CharTeleMyCount", -10) > _online.size()))
						{
							_online.put(activeChar.getHWIDs(), activeChar.getHWIDs());
							activeChar.teleToLocation(Location.coordsRandomize(receiver.getX(), receiver.getY(), receiver.getZ(), 0, 100, 0));
						}
					}
					else
						activeChar.sendPacket(Msg.THE_PERSON_IS_IN_A_MESSAGE_REFUSAL_MODE);
				}
				else if(receiver == null)
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_CURRENTLY_LOGGED_IN).addString(_target), Msg.ActionFail);
				else
					activeChar.sendPacket(Msg.YOU_HAVE_BEEN_BLOCKED_FROM_THE_CONTACT_YOU_SELECTED, Msg.ActionFail);
				break;
			case SHOUT:
				if(activeChar.getLevel() < ConfigValue.minSHOUTlevel /*&& !activeChar.isSubClassActive()*/ && activeChar.canPenaltyChat())
				{
					activeChar.sendMessage("Shout chat is allowed only for characters level higher "+ConfigValue.minSHOUTlevel+".");
					return;
				}
				else if(activeChar.isCursedWeaponEquipped())
				{
					activeChar.sendPacket(Msg.SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON);
					return;
				}
				else if(activeChar.inObserverMode())
				{
					activeChar.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
					return;
				}
				else if(activeChar.ShoutChatLaunched + ConfigValue.ShoutChatLaunched > curTime)
				{
					activeChar.sendMessage("Shout chat is allowed once per 5 seconds.");
					return;
				}
				else if(ConfigValue.ChatShoutCharLife > 0 && activeChar.getCreateTime() + (ConfigValue.ChatShoutCharLife*1000) > curTime)
				{
					activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatShoutCharLife/60)+" минут.");
					return;
				}
				else if(ConfigValue.ChatShoutCharOnline > 0 && activeChar.getOnlineTime() < (ConfigValue.ChatShoutCharOnline*1000))
				{
					activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatShoutCharOnline/60)+" минут.");
					return;
				}
				activeChar.ShoutChatLaunched = curTime;

				if((activeChar.hasBonus() && ConfigValue.GlobalChatForPremium) || activeChar.getLevel() >= ConfigValue.GlobalChat || activeChar.isGM() && ConfigValue.GlobalChat < Experience.getMaxLevel())
				{
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!player.isInBlockList(activeChar) && !player.isBlockAll())
							player.sendPacket(cs);
				}
				else
				{
					if(ConfigValue.ShoutChatMode == 1)
					{
						for(L2Player player : L2World.getAroundPlayers(activeChar, ConfigValue.ShoutChatRadius, 1500))
							if(!player.isInBlockList(activeChar) && !player.isBlockAll() && player != activeChar)
								player.sendPacket(cs);
					}
					else
						for(L2Player player : L2ObjectsStorage.getPlayers())
							if(MapRegion.getInstance().getMapRegion(player.getX(), player.getY()) == mapregion && !player.isInBlockList(activeChar) && !player.isBlockAll() && player != activeChar)
								player.sendPacket(cs);
					activeChar.sendPacket(cs);
				}
				break;
			case TRADE:
				if(activeChar.getLevel() < ConfigValue.minTRADElevel /*&& !activeChar.isSubClassActive()*/ && activeChar.canPenaltyChat())
				{
					activeChar.sendMessage("Trade chat is allowed only for characters level higher "+ConfigValue.minTRADElevel+".");
					return;
				}
				else if(activeChar.isCursedWeaponEquipped())
				{
					activeChar.sendPacket(Msg.SHOUT_AND_TRADE_CHATING_CANNOT_BE_USED_SHILE_POSSESSING_A_CURSED_WEAPON);
					return;
				}
				else if(activeChar.inObserverMode())
				{
					activeChar.sendPacket(Msg.YOU_CANNOT_CHAT_LOCALLY_WHILE_OBSERVING);
					return;
				}
				else if(activeChar.TradeChatLaunched + ConfigValue.TradeChatLaunched > curTime)
				{
					activeChar.sendMessage("Trade chat is allowed once per 5 seconds.");
					return;
				}
				else if(ConfigValue.ChatTradeCharLife > 0 && activeChar.getCreateTime() + (ConfigValue.ChatTradeCharLife*1000) > curTime)
				{
					activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatTradeCharLife/60)+" минут.");
					return;
				}
				else if(ConfigValue.ChatTradeCharOnline > 0 && activeChar.getOnlineTime() < (ConfigValue.ChatTradeCharOnline*1000))
				{
					activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatTradeCharOnline/60)+" минут.");
					return;
				}
				activeChar.TradeChatLaunched = curTime;

				if((activeChar.hasBonus() && ConfigValue.GlobalChatForPremium) || activeChar.getLevel() >= ConfigValue.GlobalTradeChat || activeChar.isGM() && ConfigValue.GlobalTradeChat < Experience.getMaxLevel())
				{
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!player.isInBlockList(activeChar) && !player.isBlockAll())
							if(!ConfigValue.EnableFactionMod)
								player.sendPacket(cs);
				}
				else
				{
					if(ConfigValue.TradeChatMode == 1)
					{
						for(L2Player player : L2World.getAroundPlayers(activeChar, ConfigValue.TradeChatRadius, 1500))
							if(!player.isInBlockList(activeChar) && !player.isBlockAll() && player != activeChar)
								player.sendPacket(cs);
					}
					else
						for(L2Player player : L2ObjectsStorage.getPlayers())
							if(MapRegion.getInstance().getMapRegion(player.getX(), player.getY()) == mapregion && !player.isInBlockList(activeChar) && !player.isBlockAll() && player != activeChar)
								player.sendPacket(cs);
					activeChar.sendPacket(cs);
				}
				break;
			case ALL:
				if(!ConfigValue.ChatFilterEnable || activeChar.addChat(_text, 0))
				{
					if(activeChar.getLevel() < ConfigValue.minALLlevel /*&& !activeChar.isSubClassActive()*/ && activeChar.canPenaltyChat())
					{
						activeChar.sendMessage("All chat is allowed only for characters level higher "+ConfigValue.minALLlevel+".");
						return;
					}
					else if(ConfigValue.ChatAllCharLife > 0 && activeChar.getCreateTime() + (ConfigValue.ChatAllCharLife*1000) > curTime)
					{
						activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatAllCharLife/60)+" минут.");
						return;
					}
					else if(ConfigValue.ChatAllCharOnline > 0 && activeChar.getOnlineTime() < (ConfigValue.ChatAllCharOnline*1000))
					{
						activeChar.sendMessage("Новым персонажам запрещено писать в чат в течении "+(ConfigValue.ChatAllCharOnline/60)+" минут.");
						return;
					}

					if(activeChar.isCursedWeaponEquipped())
						cs = new Say2(activeChar.getObjectId(), _type, activeChar.getTransformationName(), _text);
					if(activeChar.inObserverMode() && activeChar.getObservNeighbor() != null)
					{
						GArray<L2Player> result = new GArray<L2Player>(50);
						for(L2WorldRegion neighbor : activeChar.getObservNeighbor().getNeighbors())
							neighbor.getPlayersList(result, activeChar.getObjectId(), activeChar.getReflection(), activeChar.getX(), activeChar.getY(), activeChar.getZ(), ALL_CHAT_RANGE * ALL_CHAT_RANGE, 400, false);

						for(L2Player player : result)
							if(!player.isInBlockList(activeChar) && !player.isBlockAll() && player != activeChar)
								player.sendPacket(cs);
					}
					else
						for(L2Player player : L2World.getAroundPlayers(activeChar, ALL_CHAT_RANGE, 400))
							if(!player.isInBlockList(activeChar) && !player.isBlockAll() && player != activeChar)
								player.sendPacket(cs);

					activeChar.sendPacket(cs);
				}
				else if(ConfigValue.ChatFilterEnable)
				{
					activeChar.sendMessage("You are banned in all chats. Time to unban: " + ConfigValue.ChatFilterToUnban + "sec.");
					Log.add("" + activeChar + ": " + _text, "abuse2");
					PlayerData.getInstance().updateNoChannel(activeChar, ConfigValue.ChatFilterToUnban * 1000);
				}
				break;
			case CLAN:
				if(curTime < activeChar.getVarLong("ClanChatBan", -1))
				{
					activeChar.sendMessage("Вы не можете писать в клан чат.");
					activeChar.sendActionFailed();
					return;
				}
				if(activeChar.getClan() != null)
					activeChar.getClan().broadcastCSToOnlineMembers(cs, activeChar);
				else
					activeChar.sendActionFailed();
				break;
			case ALLIANCE:
				if(activeChar.getClan() != null && activeChar.getClan().getAlliance() != null)
					activeChar.getClan().getAlliance().broadcastCSToOnlineMembers(cs, activeChar);
				else
					activeChar.sendActionFailed();
				break;
			case PARTY:
				if(activeChar.isInParty())
					activeChar.getParty().broadcastCSToPartyMembers(activeChar, cs);
				else
					activeChar.sendActionFailed();
				break;
			case PARTY_ROOM:
				// Пусть будет так...
				if(activeChar.getTerritorySiege() > -1 && TerritorySiege.isTerritoryChatAccessible())
				{
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!player.isInBlockList(activeChar) && !player.isBlockAll() && activeChar.getTerritorySiege() == player.getTerritorySiege())
							player.sendPacket(cs);
				}
				if(activeChar.getPartyRoom() <= 0)
				{
					activeChar.sendActionFailed();
					return;
				}
				PartyRoom room = PartyRoomManager.getInstance().getRooms().get(activeChar.getPartyRoom());
				if(room == null)
				{
					activeChar.sendActionFailed();
					return;
				}
				room.broadcastCSPacket(cs, activeChar);
				break;
			case COMMANDCHANNEL_ALL:
				if(!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
				{
					activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
					return;
				}
				if(activeChar.getParty().getCommandChannel().getChannelLeader() == activeChar)
					activeChar.getParty().getCommandChannel().broadcastCSToChannelMembers(cs, activeChar);
				else
					activeChar.sendPacket(Msg.ONLY_CHANNEL_OPENER_CAN_GIVE_ALL_COMMAND);
				break;
			case COMMANDCHANNEL_COMMANDER:
				if(!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
				{
					activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
					return;
				}
				if(activeChar.getParty().isLeader(activeChar))
					activeChar.getParty().getCommandChannel().broadcastToChannelPartyLeaders(cs);
				else
					activeChar.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL);
				break;
			case HERO_VOICE:
				if(activeChar.isHero() || activeChar.getPlayerAccess().CanAnnounce)
				{
					// Ограничение только для героев, гм-мы пускай говорят.
					if(!activeChar.getPlayerAccess().CanAnnounce)
					{
						if(activeChar.HeroChatLaunched + 10000L > curTime)
						{
							activeChar.sendMessage("Hero chat is allowed once per 10 seconds.");
							return;
						}
						activeChar.HeroChatLaunched = curTime;
					}

					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!player.isInBlockList(activeChar) && !player.isBlockAll())
							player.sendPacket(cs);
				}
				break;
			case PETITION_PLAYER:
			case PETITION_GM:
				//for(L2Player gm : GmListTable.getAllGMs())
				//	if(!gm.getMessageRefusal())
				//		gm.sendPacket(cs);
				break;
			case BATTLEFIELD:
				if(activeChar.getTerritorySiege() > -1 && TerritorySiege.isTerritoryChatAccessible())
				{
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!player.isInBlockList(activeChar) && !player.isBlockAll() && activeChar.getTerritorySiege() == player.getTerritorySiege())
							player.sendPacket(cs);
				}
				break;
			default:
				_log.warning("Character " + activeChar.getName() + " used unknown chat type: " + _type + ". Cheater?");
		}

		if(!shift_item)
			activeChar.getListeners().onSay(_type, _target, _text);
	}

	private void telnet_output(String _text, int type)
	{
		GameStatusThread tinstance = Status.telnetlist;

		while(tinstance != null)
		{
			if(type == TELL && tinstance.LogTell)
				tinstance.write(_text);
			else if(tinstance.LogChat)
				tinstance.write(_text);
			tinstance = tinstance.next;
		}
	}

	public class UnbanTask extends l2open.common.RunnableImpl
	{
		private String _name;

		public UnbanTask(String Name)
		{
			_name = Name;
		}

		public void runImpl()
		{
			L2Player plyr = L2World.getPlayer(_name);
			if(plyr != null)
			{
				plyr.setAccessLevel(0);
				plyr.sendMessage("Nochannel deactivated");
				Log.add("" + plyr + ": unbanchat online", "abuse");
			}
			else
			{
				PlayerData.getInstance().setCharacterAccessLevel(_name, 0);
				Log.add("Player " + _name + ": unbanchat offline", "abuse");
			}

			_banned.remove(_name);
		}
	}
}