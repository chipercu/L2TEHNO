package l2open.gameserver.model.entity.market;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2EtcItem;
import l2open.util.Files;
import l2open.util.GArray;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javolution.util.FastMap;
import javolution.util.FastList;

/**
 * @author Diagod
 */
public class AuctionRegistration
{
	protected static Logger _log = Logger.getLogger(AuctionRegistration.class.getName());

	private static AuctionRegistration _instance = null;

	/* Отображение */
	public static final int MAX_LOTS = 50; // Макс. кол-во лотов аукциона
	public static final int MAX_CHAR_LOTS = 12; // Макс. кол-во слотов для чара
	public static final int LOTS_PER_PAGE = 10; // Кол-во лотов на страницу (указывайтеся четное число)

	/* Продажа */
	public static final int[] DISALLOWED_ITEMS_FOR_BID = { 5588, 7694  }; // Список запрещенных предметов для выставления на продажу
	public static final double MARKET_TAX = 0.1; // Налог аукциона (берется с заявленой цены предмета), в процентах

	/* Другие настройки */
	public static final boolean SEND_MESSAGE_AFTER_TRADE = true; // Посылать сообщения продавцу и покупателю при покупке / продаже предмета
	public static final boolean ALLOW_AUGMENTATED_ITEMS = true; // Разрешить выставлять на продажу аугментированные предметы
	public static final boolean ALLOW_ETC_ITEMS_FOR_SELL = false; // Разрешить выставлять на продажу etc. айетмы (свитки и пр.)
	public static final boolean ALLOW_ENCHATED_ITEMS = true; // Разрешить выставлять на продажу заточенные предметы
	public static final String TRADE_MESSAGE_FORSELLER = "Ваш товар %item% был успешно продан."; // Сообщение продавцу
	public static final String TRADE_MESSAGE_FORBUYER = "Вы успешно купили товар %item%."; // Сообщение покупателю

	public static AuctionRegistration getInstance()
	{
		if(_instance == null)
			_instance = new AuctionRegistration();
		return _instance;
	}
		
	private static FastMap<Integer, GArray<String>> _regPlayer = new FastMap<Integer, GArray<String>>().setShared(true);

	// Товары рынка...
	private static FastMap<Integer, FastMap<Integer, LotInfo>> _tovarInfoMarket = new FastMap<Integer, FastMap<Integer, LotInfo>>().setShared(true);

	// Товары Аукциона...
	private static FastMap<Integer, FastMap<Integer, LotInfo>> _tovarInfoAuction = new FastMap<Integer, FastMap<Integer, LotInfo>>().setShared(true);

	// Проданые товары рынка...
	private static FastMap<Integer, FastMap<Integer, LotInfo>> _tovarInfoSellMarket = new FastMap<Integer, FastMap<Integer, LotInfo>>().setShared(true);

	// Проданые товары Аукциона...
	private static FastMap<Integer, FastMap<Integer, LotInfo>> _tovarInfoSellAuction = new FastMap<Integer, FastMap<Integer, LotInfo>>().setShared(true);

	private static Map<String, Integer> prices = new FastMap<String, Integer>();

	public AuctionRegistration()
	{
		loadDBRegistration();
		loadDBInfoBid();
		prices.put("Adena", 57);
		prices.put("CoL", 4037);
	}

	public synchronized void setRegistrPlayer(L2Player player, String name, String firstname, String cash, String data)
	{
		GArray<String> _info = new GArray<String>(6);
		_info.add(String.valueOf(player.getObjectId())); // index(0) Первым делом мы ставим ИД обьекта.
		_info.add(player.getName()); // index(1) Потом ставим Ник перса.
		_info.add(name); // index(2) Далее записываем Реальное Имя игрока.
		_info.add(firstname); // index(3) Потом Фамилию.
		_info.add(cash); // index(4) Кошелек
		_info.add(data); // index(5) Дата регистрации...

		_regPlayer.put(player.getObjectId(), _info);
		saveDBRegistration(_info, true);
	}

	private synchronized void saveDBRegistration(GArray<String> info, boolean update)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			if(update)
				statement = con.prepareStatement("INSERT INTO auction_char_info(objectId,player_name,name,firstname,cash,data) VALUES (?,?,?,?,?,?)");
			else
				statement = con.prepareStatement("UPDATE auction_char_info SET name = ?, firstname = ?, cash = ? WHERE objectId = ?");
			if(update)
			{
				statement.setString(1, info.get(0));
				statement.setString(2, info.get(1));
				statement.setString(3, info.get(2));
				statement.setString(4, info.get(3));
				statement.setString(5, info.get(4));
				statement.setString(6, info.get(5));
			}
			else
			{
				statement.setString(1, info.get(2));
				statement.setString(2, info.get(3));
				statement.setString(3, info.get(4));
				statement.setString(4, info.get(0));
			}
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public synchronized void loadDBRegistration()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		GArray<String> _info = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM auction_char_info");
			rset = statement.executeQuery();

			int objectId;
			String player_name;
			String name;
			String firstname;
			String cash;
			String data;

			while(rset.next())
			{
				objectId = rset.getInt("objectId");
				player_name = rset.getString("player_name");
				name = rset.getString("name");
				firstname = rset.getString("firstname");
				cash = rset.getString("cash");
				data = rset.getString("data");

				_info = new GArray<String>(6);
				_info.add(String.valueOf(objectId)); // index(0) Первым делом мы ставим ИД обьекта.
				_info.add(player_name); // index(1) Потом ставим Ник перса.
				_info.add(name); // index(2) Далее записываем Реальное Имя игрока.
				_info.add(firstname); // index(3) Потом Фамилию.
				_info.add(cash); // index(4) Кошелек
				_info.add(data); // index(5) Дата регистрации...

				_regPlayer.put(objectId, _info);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	// 
	public synchronized void loadDBInfoBid()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		FastMap<Integer, LotInfo> _info = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM auction_item");
			rset = statement.executeQuery();

			int objectId; // ID продавца...
			int itemObjectId; // ID обьекта итема...
			int itemId; // ID итема...
			long itemCount; // Количество продаваемого итема...
			String itemName; // Имя итема...
			int cashValue; // за, что продаем итем...
			long cashCount; // цена итема...
			long data; // дата выставления на продажу в секундах...
			long dataAdd; // дополнительное время для продажи...
			int status; // 0 - продается, 1 - продано.
			int type; // 0 - рынок, 1 - аукцион.
			long lastBetValue = -1;
			int lastBetObjectId = -1;

			while(rset.next())
			{
				objectId = rset.getInt("objectId");
				itemObjectId = rset.getInt("itemObjectId");
				itemId = rset.getInt("itemId");
				itemCount = rset.getLong("itemCount");
				itemName = rset.getString("itemName");
				cashValue = rset.getInt("cashValue");
				cashCount = rset.getLong("cashCount");
				data = rset.getLong("data");
				dataAdd = rset.getLong("dataAdd");
				status = rset.getInt("status");
				type = rset.getInt("type");
				lastBetValue = rset.getLong("lastBetValue");
				lastBetObjectId = rset.getInt("lastBetObjectId");

				LotInfo lot = new LotInfo();
				lot.objectId = objectId;
				lot.itemObjectId = itemObjectId;
				lot.itemId = itemId;
				lot.itemCount = itemCount;
				lot.itemName = itemName;
				lot.cashValue = cashValue;
				lot.cashCount = cashCount;
				lot.data = data;
				lot.dataAdd = dataAdd;
				lot.status = status;
				lot.type = type;
				lot.lastBetValue = lastBetValue;
				lot.lastBetObjectId = lastBetObjectId;

				switch(status)
				{
					case 0:
						if(type == 0 || type == 1)
						{
							if(_tovarInfoMarket.containsKey(objectId))
							{
								_info = _tovarInfoMarket.get(objectId);
								_info.put(lot.itemObjectId, lot);
								break;
							}
							_info = new FastMap<Integer, LotInfo>();
							_info.put(lot.itemObjectId, lot);
							_tovarInfoMarket.put(objectId, _info);
							break;
						}
						else if(type == 2 || type == 3)
						{
							if(_tovarInfoAuction.containsKey(objectId))
							{
								_info = _tovarInfoAuction.get(objectId);
								_info.put(lot.itemObjectId, lot);
								break;
							}
							_info = new FastMap<Integer, LotInfo>();
							_info.put(lot.itemObjectId, lot);
							_tovarInfoAuction.put(objectId, _info);
							break;
						}
						else
							System.out.println("AuctionRegistration Error-272"); // TODO: System Error Msg.
						break;
					case 1:
						if(type == 0 || type == 1)
						{
							if(_tovarInfoSellMarket.containsKey(objectId))
							{
								_info = _tovarInfoSellMarket.get(objectId);
								_info.put(lot.itemObjectId, lot);
								break;
							}
							_info = new FastMap<Integer, LotInfo>();
							_info.put(lot.itemObjectId, lot);
							_tovarInfoSellMarket.put(objectId, _info);
							break;
						}
						else if(type == 2 || type == 3)
						{
							if(_tovarInfoSellAuction.containsKey(objectId))
							{
								_info = _tovarInfoSellAuction.get(objectId);
								_info.put(lot.itemObjectId, lot);
								break;
							}
							_info = new FastMap<Integer, LotInfo>();
							_info.put(lot.itemObjectId, lot);
							_tovarInfoSellAuction.put(objectId, _info);
							break;
						}
						else
							System.out.println("AuctionRegistration Error-302"); // TODO: System Error Msg.
						break;
					default:
						System.out.println("AuctionRegistration Error-305"); // TODO: System Error Msg.
						break;
				}
			}
			rset.close();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	/**
	 * (3) itemCount - количество продаваемых итемов...
	 * (5) // cashValue - тип валюты за которую продают...
	 * (6) // cashCount - стоимость...
	 * (9) // status - состояние, 0 - продается, 1 - продано.
	 * (10) // type - тип продажи, 0 - рынок, налог плотит продавец, 1 - рынок, налог плотит покупатель, 2 - Аукцион, налог плотит продавец, 3 - Аукцион, налог плотит покупатель...
	 * (0) // objectId - ID продавца...
	 * (1) // itemObjectId - ID обьекта итема...
	 * (2) // itemId - ID итема...
	 **/
	private synchronized void saveBidToDB(LotInfo info, boolean update)
	{
		saveBidToDB(info, update, false);
	}

	private synchronized void saveBidToDB(LotInfo info, boolean update, boolean deleted)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			if(!deleted)
			{
				if(!update)
					statement = con.prepareStatement("INSERT INTO auction_item(objectId,itemObjectId,itemId,itemCount,itemName,cashValue,cashCount,data,dataAdd,status,type,lastBetValue,lastBetObjectId) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
				else
					statement = con.prepareStatement("UPDATE auction_item SET itemCount = ?, cashValue = ?, cashCount = ?, status = ?, type = ?, lastBetValue = ?, lastBetObjectId = ? WHERE objectId = ? AND itemObjectId = ? AND itemId = ?");
				if(!update)
				{
					statement.setInt(1, info.objectId); // objectId - ID продавца...
					statement.setInt(2, info.itemObjectId); // itemObjectId - ID обьекта итема...
					statement.setInt(3, info.itemId); // itemId - ID итема...
					statement.setLong(4, info.itemCount); // itemCount - количество продаваемых итемов...
					statement.setString(5, info.itemName); // itemName - имя итема...
					statement.setInt(6, info.cashValue); // cashValue - тип валюты за которую продают...
					statement.setLong(7, info.cashCount); // cashCount - стоимость...
					statement.setLong(8, info.data); // data - дата подачи итема на продажу...
					statement.setLong(9, info.dataAdd); // dataAdd - дополнительное время на продажу итема...
					statement.setInt(10, info.status); // status - состояние, 0 - продается, 1 - продано.
					statement.setInt(11, info.type); // type - тип продажи, 0 - рынок, налог плотит продавец, 1 - рынок, налог плотит покупатель, 2 - Аукцион, налог плотит продавец, 3 - Аукцион, налог плотит покупатель...
					statement.setLong(12, info.lastBetValue); // lastBetValue - последняя ставка на аукционе.
					statement.setInt(13, info.lastBetObjectId); // lastBetObjectId - ID чара который сделал последнюю ставку на аукционе...
				}
				else
				{
					statement.setLong(1, info.itemCount); // itemCount - количество продаваемых итемов...
					statement.setInt(2, info.cashValue); // cashValue - тип валюты за которую продают...
					statement.setLong(3, info.cashCount); // cashCount - стоимость...
					statement.setInt(4, info.status); // status - состояние, 0 - продается, 1 - продано.
					statement.setInt(5, info.type); // type - тип продажи, 0 - рынок, налог плотит продавец, 1 - рынок, налог плотит покупатель, 2 - Аукцион, налог плотит продавец, 3 - Аукцион, налог плотит покупатель...
					statement.setLong(6, info.lastBetValue); // lastBetValue - последняя ставка на аукционе.
					statement.setInt(7, info.lastBetObjectId); // lastBetObjectId - ID чара который сделал последнюю ставку на аукционе...
					statement.setInt(8, info.objectId); // objectId - ID продавца...
					statement.setInt(9, info.itemObjectId); // itemObjectId - ID обьекта итема...
					statement.setInt(10, info.itemId); // itemId - ID итема...
				}
			}
			else
			{
				statement = con.prepareStatement("DELETE FROM auction_item WHERE itemObjectId=? AND objectId=?");
				statement.setInt(1, info.itemObjectId);
				statement.setInt(2, info.objectId);
			}
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public GArray<String> getRegInfo(int playerId)
	{
		return _regPlayer.get(playerId);
	}

	/**
	 * Возвращает список товаров.
	 * @playerId - ID продавца.
	 * @type - тип возвращаемых результатов:
	 *			0 - возвращает товары рынка.
	 *			1 - возвращает товары аукциона.
	 *			2 - возвращает проданые товары рынка.
	 *			3 - возвращает проданые товары аукциона.
	 **/
	public FastMap<Integer, LotInfo> getBidInfo(int playerId, int type)
	{
		switch(type)
		{
			case 0:
				return _tovarInfoMarket.get(playerId);
			case 1:
				return _tovarInfoAuction.get(playerId);
			case 2:
				return _tovarInfoSellMarket.get(playerId);
			case 3:
				return _tovarInfoSellAuction.get(playerId);
			default:
				System.out.println("AuctionRegistration Error-426");// TODO: System Error Msg.
				return null;
		}
	}

	public boolean isPlayerRegistr(int playerId)
	{
		return !ConfigValue.EnableMarcketRegAcc || _regPlayer.containsKey(playerId);
	}

	/**
	 * Сохраняет товар.
	 * @playerId - ID продавца.
	 * @type - тип возвращаемых результатов:
	 *			0 - Сохраняет товар рынка.
	 *			1 - Сохраняет товар аукциона.
	 *			2 - Сохраняет проданый товар рынка.
	 *			3 - Сохраняет проданый товар аукциона.
	 **/
	public void setBidInfo(LotInfo lotInfo, int playerId, boolean saveDB, int type)
	{
		FastMap<Integer, LotInfo> _lotInfoList;
		switch(type)
		{
			case 0:
				if(_tovarInfoMarket.containsKey(playerId))
				{
					_lotInfoList = _tovarInfoMarket.get(playerId);
					_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
					break;
				}
				_lotInfoList = new FastMap<Integer, LotInfo>();
				_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
				_tovarInfoMarket.put(playerId, _lotInfoList);
				break;
			case 1:
				if(_tovarInfoAuction.containsKey(playerId))
				{
					_lotInfoList = _tovarInfoAuction.get(playerId);
					_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
					break;
				}
				_lotInfoList = new FastMap<Integer, LotInfo>();
				_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
				_tovarInfoAuction.put(playerId, _lotInfoList);
				break;
			case 2:
				if(_tovarInfoSellMarket.containsKey(playerId))
				{
					_lotInfoList = _tovarInfoSellMarket.get(playerId);
					_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
					break;
				}
				_lotInfoList = new FastMap<Integer, LotInfo>();
				_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
				_tovarInfoSellMarket.put(playerId, _lotInfoList);
				break;
			case 3:
				if(_tovarInfoSellAuction.containsKey(playerId))
				{
					_lotInfoList = _tovarInfoSellAuction.get(playerId);
					_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
					break;
				}
				_lotInfoList = new FastMap<Integer, LotInfo>();
				_lotInfoList.put(lotInfo.itemObjectId, lotInfo);
				_tovarInfoSellAuction.put(playerId, _lotInfoList);
				break;
			default:
				System.out.println("AuctionRegistration Error-495");// TODO: System Error Msg.
				break;
		}
		if(saveDB)
			saveBidToDB(lotInfo, false);
	}

	public int getBidsCount(int type)
	{
		int count = 0;
		switch(type)
		{
			case 0:
				for(FastMap<Integer, LotInfo> it : _tovarInfoMarket.values())
					count += it.size();
				break;
			case 1:
				for(FastMap<Integer, LotInfo> it : _tovarInfoAuction.values())
					count += it.size();
				break;
			case 2:
				for(FastMap<Integer, LotInfo> it : _tovarInfoSellMarket.values())
					count += it.size();
				break;
			case 3:
				for(FastMap<Integer, LotInfo> it : _tovarInfoSellAuction.values())
					count += it.size();
				break;
			default:
				System.out.println("AuctionRegistration Error-524");// TODO: System Error Msg.
				break;
		}
		return count;
	}

	public int getPlayerBidsCount(int playerId, int type)
	{
		try
		{
			switch(type)
			{
				case 0:
					return _tovarInfoMarket.get(playerId).size();
				case 1:
					return _tovarInfoAuction.get(playerId).size();
				case 2:
					return _tovarInfoSellMarket.get(playerId).size();
				case 3:
					return _tovarInfoSellAuction.get(playerId).size();
				default:
					System.out.println("AuctionRegistration Error-545");// TODO: System Error Msg.
					return 0;
			}
		}
		catch(NullPointerException e)
		{
			return 0;
		}
	}

	public void deleteLot(int charObjId, int bidId)
	{
		L2Player player = L2ObjectsStorage.getPlayer(charObjId);
		LotInfo bid = getLotById(charObjId, bidId);
		if(bid.objectId != charObjId)
			return;
		if(getAllPlayerBids(charObjId, 0).contains(bid))
		{
			PlayerData.getInstance().updateInDb(charObjId, "INVENTORY", bidId);
			player.getInventory().addItem(PlayerData.getInstance().restoreFromDb(bidId));
			saveBidToDB(bid, true, true);
			_tovarInfoMarket.get(charObjId).remove(bidId);
			sendResultHtml(player, "Ваш предмет успешно удален с рынка.");
		}
		else if(getAllPlayerBids(charObjId, 1).contains(bid))
		{
			PlayerData.getInstance().updateInDb(charObjId, "INVENTORY", bidId);
			player.getInventory().addItem(PlayerData.getInstance().restoreFromDb(bidId));
			saveBidToDB(bid, true, true);
			_tovarInfoAuction.get(charObjId).remove(bidId);
			sendResultHtml(player, "Ваш предмет успешно удален с аукциона.");
		}
	}

	public void addLot(int playerid, int itemObjId, int costItemId, int costItemCount, String tax, int type)
	{
		long addData = 0;
		L2Player player = L2ObjectsStorage.getPlayer(playerid);
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/906.htm", player);
		L2ItemInstance item = player.getInventory().getItemByObjectId(itemObjId);
		int taxType = -1;
		if(tax.equalsIgnoreCase("Seller"))
		{
			if(type == 0)
				taxType = 0; // 0 для Рынка
			else
				taxType = 2; // 2 для Аукциона
		}
		else if(tax.equalsIgnoreCase("Buyer"))
		{
			if(type == 0)
				taxType = 1; // 1 для Рынка
			else
				taxType = 3; // 3 для Аукциона
		}
		if(!checkItemForMarket(item))
		{
			content = content.replace("%text%", "Извините, этот предмет нельзя выставить на рынок.");
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			ShowBoard.separateAndSend(content, player);
			return;
		}
		if(!prices.containsValue(costItemId))
		{
			content = content.replace("%text%", "Извините, эта валюта не поддерживается рынком.");
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			ShowBoard.separateAndSend(content, player);
			return;
		}
		if((getBidsCount(type) + 1) > MAX_LOTS)
		{
			content = content.replace("%text%", "Извините, аукцион переполнен.");
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			ShowBoard.separateAndSend(content, player);
			return;
		}
		if(getPlayerBidsCount(player.getObjectId(), type) + 1 > MAX_CHAR_LOTS)
		{
			content = content.replace("%text%", "Извините, вы превысили макс. количество товаров.");
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			ShowBoard.separateAndSend(content, player);
			return;
		}
		if((taxType == 0 || taxType == 2) && (player.getInventory().getItemByItemId(costItemId) != null && player.getInventory().getItemByItemId(costItemId).getCount() < (costItemCount * MARKET_TAX)))
		{
			content = content.replace("%text%", "Извините, у Вас не достаточно средств для оплаты налога рынка.");
			content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
			ShowBoard.separateAndSend(content, player);
			return;
		}
		if(item.isEquipped())
			player.getInventory().unEquipItem(item);
		L2ItemInstance newItem = player.getInventory().dropItem(item,costItemCount, false);
		newItem.setOwnerId(player.getObjectId());
		newItem.setLocation(ItemLocation.SELL);
		newItem.updateDatabase(true, false);

		player.sendPacket(new InventoryUpdate().addModifiedItem(item));
		player.updateStats();

		LotInfo lot = new LotInfo(playerid, itemObjId, item.getItemId(), 1, item.getName(), costItemId, costItemCount, System.currentTimeMillis(), addData, 0, type, -1, -1);

		if(taxType == 0 || taxType == 2)
			player.getInventory().destroyItemByItemId(costItemId, (long)(costItemCount * MARKET_TAX), true);

		setBidInfo(lot, playerid, true, type);

		content = content.replace("%text%", "Товар успешно добавлен на рынок.");
		content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
		ShowBoard.separateAndSend(content, player);
	}

	public FastList<LotInfo> getAllBids(int type)
	{
		FastList<LotInfo> result = new FastList<LotInfo>();
		Collection<FastMap<Integer, LotInfo>> collect = null;
		switch(type)
		{
			case 0:
				collect = _tovarInfoMarket.values();
				break;
			case 1:
				collect = _tovarInfoAuction.values();
				break;
			case 2:
				collect = _tovarInfoSellMarket.values();
				break;
			case 3:
				collect = _tovarInfoSellAuction.values();
				break;
		}

		for(FastMap<Integer, LotInfo> list: collect)
			for(LotInfo bid: list.values())
				result.add(bid);
		return result;
	}

	public void buyLot(int buyerId, int bidId, int type)
	{
		LotInfo bid = null;

		for(LotInfo li : getAllBids(type))
			if(li.status == 0)
				if(li.itemObjectId == bidId)
				{
					if(bid == null)
						bid = li;
					else
						System.out.println("buyLot DUBLICATE!!!");
				}

		L2Player seller = null;
		L2Player buyer = null;
		try
		{
			seller = L2ObjectsStorage.getPlayer(bid.objectId);
		}
		catch(NullPointerException e)
		{}
		try
		{
			buyer = L2ObjectsStorage.getPlayer(buyerId);
		}
		catch(NullPointerException e)
		{}

		if(buyer == null)
			return;
		if(buyer.getObjectId() == bid.objectId)
		{
			System.out.println("buyLot LoX Detected: " + buyer.getName());
			return;
		}
		if(buyer.getInventory().getItemByItemId(bid.cashValue) == null || ((bid.type == 1 || bid.type == 3) && (buyer.getInventory().getItemByItemId(bid.cashValue).getCount() < (bid.cashCount + (bid.cashCount * MARKET_TAX)))) || ((bid.type == 0 || bid.type == 2) && (buyer.getInventory().getItemByItemId(bid.cashValue).getCount() < bid.cashCount)))
		{
			sendResultHtml(buyer, "Извините, у Вас не хватает денег на оплату товара.");
			return;
		}

		L2ItemInstance item = PlayerData.getInstance().restoreFromDb(bid.itemObjectId);
		if(item != null && item.getLocation() == ItemLocation.SELL)
		{
			double itemcount = ((bid.type == 1 || bid.type == 3) ? (bid.cashCount + (bid.cashCount * MARKET_TAX)) : bid.cashCount);
			if(buyer.getInventory().destroyItemByItemId(bid.cashValue, (long)itemcount, true) != null)
			{
				//buyer.getInventory().addItem(seller.getInventory().dropItem(item.getObjectId(), bid.itemCount, false));
				
				buyer.sendPacket(SystemMessage.obtainItems(item));
				buyer.getInventory().addItem(item).updateDatabase(true, false, true);

				if(seller != null)
				{
					seller.getInventory().addItem(bid.cashValue, bid.cashCount);
					if(SEND_MESSAGE_AFTER_TRADE)
						seller.sendMessage((TRADE_MESSAGE_FORSELLER.replace("%item%", bid.itemName + " +" + getItemById(bid.itemObjectId).getRealEnchantLevel())));
				}
				else
					givePayPrice(bid.objectId, bid.cashValue, bid.cashCount);

				if(SEND_MESSAGE_AFTER_TRADE)
					buyer.sendMessage((TRADE_MESSAGE_FORBUYER.replace("%item%", bid.itemName + " +" + getItemById(bid.itemObjectId).getRealEnchantLevel())));
			}
		}

		bid.status = 1;
		bid.buyrName = buyer.getName();
		bid.buyrId = buyer.getObjectId();

		saveBidToDB(bid, true);

		if(getAllPlayerBids(bid.objectId, 0).contains(bid))
			_tovarInfoMarket.get(bid.objectId).remove(bidId);
		else if(getAllPlayerBids(bid.objectId, 1).contains(bid))
			_tovarInfoAuction.get(bid.objectId).remove(bidId);

		sendResultHtml(buyer, "Товар успешно приобретен.");
	}

	public FastList<LotInfo> getAllPlayerBids(int playerId, int type)
	{
		FastList<LotInfo> result = new FastList<LotInfo>();
		try
		{
			for(LotInfo bid: getBidInfo(playerId, type).values())
				result.add(bid);
		}
		catch(NullPointerException e)
		{
			
		}
		return result;
	}

	public FastList<LotInfo> getAllSellPlayerBids(int playerId)
	{
		FastList<LotInfo> result = new FastList<LotInfo>();
		try
		{
			for(LotInfo bid : getBidInfo(playerId, 0).values())
				result.add(bid);
			for(LotInfo bid : getBidInfo(playerId, 1).values())
				result.add(bid);
		}
		catch(NullPointerException e)
		{
			
		}
		return result;
	}

	public LotInfo getLotById(int playerId, int itemObjectId)
	{
		for(LotInfo bid: getBidInfo(playerId, 0).values())
			if(itemObjectId == bid.itemObjectId)
				return bid;
		for(LotInfo bid: getBidInfo(playerId, 1).values())
			if(itemObjectId == bid.itemObjectId)
				return bid;
		return null;
	}

	public LotInfo getLotById(int itemObjectId)
	{
		for(LotInfo bid: getAllBids(0))
			if(itemObjectId == bid.itemObjectId)
				return bid;
		for(LotInfo bid: getAllBids(1))
			if(itemObjectId == bid.itemObjectId)
				return bid;
		return null;
	}

	public L2ItemInstance getItemById(int itemObjectId)
	{
		return PlayerData.getInstance().restoreFromDb(itemObjectId);
	}

	// ------------------------------- Разный хлам -------------------------------
	public boolean isInArray(int[] arr, int item)
	{
		for(int i: arr)
		{
			if(i == item)
				return true;
		}
		return false;
	}

	public boolean checkItemForMarket(L2ItemInstance item)
	{
		if(isInArray(DISALLOWED_ITEMS_FOR_BID, item.getItemId()) || (item.isAugmented() && !ALLOW_AUGMENTATED_ITEMS) || item.isStackable() || ((item.getItem() instanceof L2EtcItem) && !ALLOW_ETC_ITEMS_FOR_SELL) || (item.getRealEnchantLevel() > 0 && !ALLOW_ENCHATED_ITEMS))
			return false;
		return true;
	}

	// ------------------------------- Итемы за которые можно толкать вещи -------------------------------
	public String getShortItemName(int id)
	{
		for(Map.Entry<String, Integer> entry: prices.entrySet())
		{
			if(entry.getValue() == id)
				return entry.getKey();
		}
		return "";
	}
	
	public int getShortItemId(String name)
	{
		for(Map.Entry<String, Integer> entry: prices.entrySet())
		{
			if(entry.getKey().equalsIgnoreCase(name))
				return entry.getValue();
		}
		return 0;
	}

	public String getPriceList()
	{
		String res = "";
		Object[] str = prices.keySet().toArray();
		for(int i = 0;i < str.length;i++)
		{
			res += (String)str[i];
			if(!(i == str.length-1))
			{
				res += ";";
			}
		}
		return res;
	}

	private void sendResultHtml(L2Player player, String text)
	{
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "marcket/906.htm", player);
		content = content.replace("%text%", text);
		content = content.replace("%backlink%", "<a action=\"bypass -h _marketpage;index\">Назад</a>");
		ShowBoard.separateAndSend(content, player);
	}

	public static void givePayPrice(int playerId, int item, long count)
	{
        L2Player player = L2ObjectsStorage.getPlayer(playerId);
        if(player != null) // цель в игре? отлично
            Functions.addItem(player, item, count);
		else 
		{
            ThreadConnection con = null;
            FiltredPreparedStatement statement = null;
            ResultSet rs = null;
            try
			{
                con = L2DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("SELECT object_id FROM items WHERE owner_id = ? AND item_id = ? AND loc = 'INVENTORY' LIMIT 1"); // сперва пробуем найти в базе его адену в инвентаре
                statement.setInt(1, playerId);
                statement.setInt(2, item);
                rs = statement.executeQuery();
                if(rs.next())
				{
                    int id = rs.getInt("object_id");
                    DatabaseUtils.closeStatement(statement);
                    statement = con.prepareStatement("UPDATE items SET count=count+? WHERE object_id = ? LIMIT 1"); // если нашли увеличиваем ее количество
                    statement.setLong(1, count);
                    statement.setInt(2, id);
                    statement.executeUpdate();
                }
				else
				{
                    DatabaseUtils.closeStatement(statement);
                    statement = con.prepareStatement("INSERT INTO items_delayed (owner_id,item_id,`count`,description) VALUES (?,?,?,'mail')"); // иначе используем items_delayed
                    statement.setLong(1, playerId);
                    statement.setLong(2, item);
                    statement.setLong(3, count);
                    statement.executeUpdate();
                }
            }
			catch(SQLException e)
			{
                e.printStackTrace();
            }
			finally
			{
                DatabaseUtils.closeDatabaseCSR(con, statement, rs);
            }
        }
    }

	public String getSellerName(int sellerId)
	{
		return (String)mysql.get("SELECT `char_name` FROM `characters` WHERE `obj_Id`=" + sellerId);
	}
}
