package l2open.gameserver.itemmall;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.gameserver.xml.XmlUtils;
import l2open.util.ValueSortMap;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author : Ragnarok
 * @date : 19.12.10    13:48
 */
public class ItemMall {
    private static Logger log = Logger.getLogger(ItemMall.class.getName());

    private static ItemMall _instance;

    public final int BR_BUY_SUCCESS = 1;
    public final int BR_BUY_LACK_OF_POINT = -1;
    public final int BR_BUY_INVALID_PRODUCT = -2;
    public final int BR_BUY_USER_CANCEL = -3;
    public final int BR_BUY_INVENTROY_OVERFLOW = -4;
    public final int BR_BUY_CLOSED_PRODUCT = -5;
    public final int BR_BUY_SERVER_ERROR = -6;
    public final int BR_BUY_BEFORE_SALE_DATE = -7;
    public final int BR_BUY_AFTER_SALE_DATE = -8;
    public final int BR_BUY_INVALID_USER = -9;
    public final int BR_BUY_INVALID_ITEM = -10;
    public final int BR_BUY_INVALID_USER_STATE = -11;
    public final int BR_BUY_NOT_DAY_OF_WEEK = -12;
    public final int BR_BUY_NOT_TIME_OF_DAY = -13;
    public final int BR_BUY_SOLD_OUT = -14;
    public final int MAX_BUY_COUNT = 99;
    private ConcurrentHashMap<Integer, ItemMallItemTemplate> brTemplates;
    private ConcurrentHashMap<Integer, ItemMallItem> shop;
    protected ExBR_ProductList list;
    private ConcurrentHashMap<Integer, List<ItemMallItem>> recentList;

    public static ItemMall getInstance() {
        if (_instance == null)
            _instance = new ItemMall();
        return _instance;
    }

    private ItemMall() {
        brTemplates = new ConcurrentHashMap<Integer, ItemMallItemTemplate>();
        shop = new ConcurrentHashMap<Integer, ItemMallItem>();
        list = null;
        recentList = new ConcurrentHashMap<Integer, List<ItemMallItem>>();
        load();
    }

    public void requestBuyItem(L2Player player, int brId, int count) {
        if (count > MAX_BUY_COUNT)
            count = MAX_BUY_COUNT;
        if (count < 1)
            count = 1;

        ItemMallItem item = shop.get(brId);
        if (item == null) {
            sendResult(player, BR_BUY_INVALID_PRODUCT);
            return;
        }

        if (player.getPoint(false) < item.price * count) {
            sendResult(player, BR_BUY_LACK_OF_POINT);
            return;
        }

        Calendar cal = Calendar.getInstance();
        if (item.iStartSale > 0 && (item.iStartSale > (int) (cal.getTimeInMillis() / 1000))) {
            sendResult(player, BR_BUY_BEFORE_SALE_DATE);
            return;
        }

        if (item.iEndSale > 0 && (item.iEndSale < (int) (cal.getTimeInMillis() / 1000))) {
            sendResult(player, BR_BUY_AFTER_SALE_DATE);
            return;
        }

        if (item.iStartHour != 0 || item.iStartMin != 0 || item.iEndHour != 0 || item.iEndMin != 0) {
            if ((item.iStartHour > cal.get(Calendar.HOUR_OF_DAY) && item.iStartMin > cal.get(Calendar.HOUR_OF_DAY)) ||
                    (item.iEndHour < cal.get(Calendar.HOUR_OF_DAY) && item.iEndMin < cal.get(Calendar.HOUR_OF_DAY))) {
                sendResult(player, BR_BUY_NOT_TIME_OF_DAY);
                return;
            }
        }

        if (item.isLimited() && (item.limit() || item.iMaxStock - item.iStock < count)) {
            sendResult(player, BR_BUY_SOLD_OUT);
            return;
        }

		int reduce = (player.getPoint(false) - (item.price * count));
		
		if(reduce < 0)
		{
			//System.out.println("if(reduce < 0)!!!!!");
            sendResult(player, BR_BUY_LACK_OF_POINT);
            return;
        }
        validateMyPoints(player, item.price * count, false);

        player.sendMessage("You have successfully used your " + (item.price * count) + " points.");

		L2Item dummy = ItemTemplates.getInstance().getTemplate(item.template.itemId);
        if (dummy.isStackable()) {
            if (!player.getInventory().validateWeight(dummy.getWeight() * item.count * count)) {
                sendResult(player, BR_BUY_INVENTROY_OVERFLOW);
                return;
            }

            if (player.getInventory().getItemByItemId(item.template.itemId) == null && !player.getInventory().validateCapacity(1)) {
                sendResult(player, BR_BUY_INVENTROY_OVERFLOW);
                return;
            }

            player.getInventory().addItem(item.template.itemId, item.count * count);
            player.sendPacket(new SystemMessage(53).addItemName(item.template.itemId).addNumber(count));
        } else {
            if (!player.getInventory().validateCapacity(item.count * count) || !player.getInventory().validateWeight(dummy.getWeight() * item.count * count)) {
                sendResult(player, BR_BUY_INVENTROY_OVERFLOW);
                return;
            }

            for (int i = 0; i < count * item.count; i++) {
                player.getInventory().addItem(item.template.itemId, 1);
                player.sendPacket(new SystemMessage(54).addItemName(item.template.itemId));
            }
        }

        if (item.isLimited()) {
            synchronized (item) {
                item.iStock += count;
            }
        }
        item.iSale += count;
        if (recentList.get(player.getObjectId()) == null) {
            List<ItemMallItem> charList = new ArrayList<ItemMallItem>();
            charList.add(item);
            recentList.put(player.getObjectId(), charList);
        } else {
            recentList.get(player.getObjectId()).add(item);
        }

        sendResult(player, BR_BUY_SUCCESS);
    }

    public void load() {
        loadTempaltes();
        loadShop();
    }

    public void loadTempaltes() {
        brTemplates = new ConcurrentHashMap<Integer, ItemMallItemTemplate>();
        try {
            File file = new File(ConfigValue.DatapackRoot + "/data/xml/item-mall.xml");
            Document document = XmlUtils.readFile(file);

            Element root = document.getRootElement();
            for (Iterator i = root.elementIterator("item"); i.hasNext();) {
                Element item = (Element) i.next();
                int brId = Integer.parseInt(item.attributeValue("brId"));
                int itemId;
                try {
                    itemId = Integer.parseInt(item.attributeValue("itemId"));
                } catch (NumberFormatException e) {
                    continue;
                }
                int cat = Integer.parseInt(item.attributeValue("category"));
                ItemMallItemTemplate imit = new ItemMallItemTemplate();
                imit.brId = brId;
                imit.itemId = itemId;
                imit.category = cat;
                brTemplates.put(imit.itemId, imit);
            }
        } catch (Exception e) {
            log.severe("ItemMall: Error parsing item-mall.xml file. ");
            e.printStackTrace();
        }

        log.info("ItemMall: loaded " + brTemplates.size() + " item templates.");
    }

    @SuppressWarnings("unchecked")
    public void loadShop() {
        shop = new ConcurrentHashMap<Integer, ItemMallItem>();
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM item_mall WHERE onSale=1 ORDER BY ord");
            result = statement.executeQuery();
            while (result.next()) {
                int vsm = result.getInt("itemId");

                ItemMallItemTemplate template = brTemplates.get(vsm);

                if (template == null) {
                    log.warning("Item Mall: item template for " + vsm + " was not found. skipping.");
                    continue;
                }

                ItemMallItem item = new ItemMallItem(template);
                item.count = result.getInt("count");
                item.price = result.getInt("price");
                item.order = result.getInt("ord");
                item.iCategory2 = result.getInt("iCategory2");
                item.iStartSale = result.getInt("iStartSale");
                item.iEndSale = result.getInt("iEndSale");
                item.iStartHour = result.getInt("iStartHour");
                item.iStartMin = result.getInt("iStartMin");
                item.iEndHour = result.getInt("iEndHour");
                item.iEndMin = result.getInt("iEndMin");
                item.iStock = result.getInt("iStock");
                item.iMaxStock = result.getInt("iMaxStock");

				L2Item dummy = ItemTemplates.getInstance().getTemplate(vsm);
                item.iWeight = dummy.getWeight();
                item.iDropable = dummy.isDropable();
                shop.put(item.template.brId, item);
            }
        } catch (final Exception e) {
            log.warning("ItemMall: error in loadShop() " + e);
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, result);
        }
        log.info("ItemMall: loaded " + shop.size() + " items available for trading.");
        list = new ExBR_ProductList();
        Map<ItemMallItem, Integer> data = new LinkedHashMap<ItemMallItem, Integer>();

        for (ItemMallItem imi : shop.values()) {
            data.put(imi, imi.order);
        }

        data = ValueSortMap.sortMapByValue(data, true);
        list.col = data.keySet();
    }

    public void saveData() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            for (ItemMallItem imi : shop.values()) {
                if (imi.isLimited()) {
                    statement = con.prepareStatement("UPDATE item_mall set iStock=? where ord=?");
                    statement.setInt(1, imi.iStock);
                    statement.setInt(2, imi.order);
                    statement.executeUpdate();
                    statement.close();
                }
            }
            System.out.println("ItemMall: Data saved.");
        } catch (final Exception e) {
            System.out.println("ItemMall: error in saveData() " + e);
        } finally {
            DatabaseUtils.closeDatabaseCS(con, statement);
        }
    }

    public void sendResult(L2Player player, int code){
        player.sendPacket(new ExBR_BuyProductResult(code));
    }

    public void validateMyPoints(L2Player player, int reduce, boolean set_game)
	{
		if(reduce < 0)
		{
			reduce = 0;
			System.out.println("Warning validateMyPoints player: " + player.getName() + " buy chet metod. This player need ban...");
		}
        try
		{
            mysql.setEx(set_game ? L2DatabaseFactory.getInstance() : L2DatabaseFactory.getInstanceLogin(), "UPDATE "+(set_game ? "market_point" : "`accounts`")+" SET `points`=points-? WHERE `login`=?", reduce, player.getAccountName());
        }
		catch (SQLException e)
		{
            e.printStackTrace();
        }
        player.sendPacket(new ExBR_GamePoint(player.getObjectId(), player.getPoint(false)));
    }

    public void showList(L2Player player) {
        player.sendPacket(list);
    }

    public void showItemInfo(L2Player player, int brId) {
        ItemMallItem item = shop.get(brId);
        if (item == null) {
            sendResult(player, BR_BUY_INVALID_ITEM);
            return;
        }

        player.sendPacket(new ExBR_ProductInfo(item));
    }

    public class ItemMallItem {
        public ItemMallItemTemplate template = null;
        public int count;
        public int price;
        public int order;
        public int iSale = 0;
        public int iDayWeek;
        public int iCategory2; // дополнительная категория(в пределах 0-3) никуда\выбор дня\эвент\и туда и туда
        public int iStartSale;
        public int iEndSale;
        public int iStartHour;
        public int iStartMin;
        public int iEndHour;
        public int iEndMin;
        public int iStock;
        public int iMaxStock;

        public int iWeight;
        public boolean iDropable;

        public ItemMallItem(ItemMallItemTemplate t) {
            template = t;
        }

        public boolean limit() {
            return iStock >= iMaxStock;
        }

        public boolean isLimited() {
            return iMaxStock > 0;
        }
    }

    public class ItemMallItemTemplate {
        public int brId;
        public int itemId;
        public int category;
    }

    public void recentProductList(L2Player player) {
        player.sendPacket(new ExBR_RecentProductListPacket(player.getObjectId()));
    }

    public List<ItemMallItem> getRecentListByOID(int objId) {
        return recentList.get(objId) == null ? new ArrayList<ItemMallItem>() : recentList.get(objId);
    }
}
