package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.base.MultiSellIngredient;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MultiSellList;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.gameserver.xml.loader.XmlWeaponLoader;
import l2open.util.GArray;
import l2open.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multisell list manager
 */
public class L2Multisell {
    private static Logger _log = Logger.getLogger(L2Multisell.class.getName());
    private FastMap<Integer, MultiSellListContainer> entries = new FastMap<Integer, MultiSellListContainer>();
    private static L2Multisell _instance = new L2Multisell();

    public static final String NODE_PRODUCTION = "production";
    public static final String NODE_INGRIDIENT = "ingredient";

    public MultiSellListContainer getList(int id) {
        return entries.get(id);
    }

    public FastMap<Integer, MultiSellListContainer>  getLists(){
        return entries;
    }

    public L2Multisell() {
        parseData();
    }

    public void reload() {
        parseData();
    }

    public static L2Multisell getInstance() {
        return _instance;
    }

    private synchronized void parseData() {
        entries.clear();
        parse();
    }

    public static class MultiSellListContainer {
        private int _listId;
        private boolean _showall = true;
        private boolean keep_enchanted = false;
        private boolean ignore_price = false;
        private boolean is_dutyfree = false;
        private boolean nokey = false;
        private boolean _isnew = false;
        GArray<MultiSellEntry> entries = new GArray<MultiSellEntry>();

        public void setListId(int listId) {
            _listId = listId;
        }

        public int getListId() {
            return _listId;
        }

        public void setShowAll(boolean bool) {
            _showall = bool;
        }

        public void setIsNew(boolean bool) {
            _isnew = bool;
        }

        public boolean isShowAll() {
            return _showall;
        }

        public void setNoTax(boolean bool) {
            is_dutyfree = bool;
        }

        public boolean isNoTax() {
            return is_dutyfree;
        }

        public void setNoKey(boolean bool) {
            nokey = bool;
        }

        public boolean isNoKey() {
            return nokey;
        }

        public void setKeepEnchant(boolean bool) {
            keep_enchanted = bool;
        }

        public boolean isKeepEnchant() {
            return keep_enchanted;
        }

        public void setIgnorePrice(boolean bool) {
            ignore_price = bool;
        }

        public boolean isIgnorePrice() {
            return ignore_price;
        }

        public void addEntry(MultiSellEntry e) {
            entries.add(e);
        }

        public GArray<MultiSellEntry> getEntries() {
            return entries;
        }

        public boolean isEmpty() {
            return entries.isEmpty();
        }

        public boolean isNew() {
            return _isnew;
        }
    }

    private void hashFiles(String dirname, GArray<File> hash) {
        File dir;
        if (ConfigValue.develop) {
            dir = new File("data/" + dirname);
        } else {
            dir = new File(ConfigValue.DatapackRoot, "data/" + dirname);
        }


        if (!dir.exists()) {
            _log.info("Dir " + dir.getAbsolutePath() + " not exists");
            return;
        }
        File[] files = dir.listFiles();
        for (File f : files)
            if (f.getName().endsWith(".xml"))
                hash.add(f);
            else if (f.isDirectory() && !f.getName().equals(".svn"))
                hashFiles(dirname + "/" + f.getName(), hash);
    }

    public void addMultiSellListContainer(int id, MultiSellListContainer list) {
        if (entries.containsKey(id))
            _log.warning("MultiSell redefined: " + id);

        list.setListId(id);
        entries.put(id, list);
    }

    public MultiSellListContainer remove(String s) {
        return remove(new File(s));
    }

    public MultiSellListContainer remove(File f) {
        return remove(Integer.parseInt(f.getName().replaceAll(".xml", "")));
    }

    public MultiSellListContainer remove(int id) {
        return entries.remove(id);
    }

    public void parseFile(File f) {
        int id = 0;
        try {
            id = Integer.parseInt(f.getName().replaceAll(".xml", ""));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Error loading file " + f, e);
            return;
        }
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            doc = factory.newDocumentBuilder().parse(f);
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Error loading file " + f, e);
            return;
        }
        try {
            addMultiSellListContainer(id, parseDocument(doc, id));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Error in file " + f, e);
        }
    }

    private void parse() {
        new File("log/game/multiselldebug.txt").delete();
        GArray<File> files = new GArray<File>();
        hashFiles("multisell", files);
        for (File f : files)
            parseFile(f);
    }

    protected MultiSellListContainer parseDocument(Document doc, int id) {
        MultiSellListContainer list = new MultiSellListContainer();
        int entId = 1;

        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
            if ("list".equalsIgnoreCase(n.getNodeName()))
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                    if ("item".equalsIgnoreCase(d.getNodeName())) {
                        MultiSellEntry e = parseEntry(d, id, list.isIgnorePrice());
                        if (e != null) {
                            e.setEntryId(entId++);
                            list.addEntry(e);
                        }
                    } else if ("config".equalsIgnoreCase(d.getNodeName())) {
                        list.setShowAll(XMLUtil.getAttributeBooleanValue(d, "showall", true));
                        list.setNoTax(XMLUtil.getAttributeBooleanValue(d, "notax", false));
                        list.setIsNew(XMLUtil.getAttributeBooleanValue(d, "is_new", false));
                        list.setKeepEnchant(XMLUtil.getAttributeBooleanValue(d, "keepenchanted", false));
                        list.setNoKey(XMLUtil.getAttributeBooleanValue(d, "nokey", false));
                        list.setIgnorePrice(XMLUtil.getAttributeBooleanValue(d, "ignoreprice", false));
                    }

        return list;
    }

    protected MultiSellEntry parseEntry(Node n, int MultiSellId, boolean ignoreprice) {
        MultiSellEntry entry = new MultiSellEntry();

        entry._chance = XMLUtil.getAttributeIntValue(n, "chance", 100);
        entry._return_chance = XMLUtil.getAttributeIntValue(n, "return_chance", 0);

        //_log.info("parseEntry: "+entry._chance+" "+entry._return_chance);

        //l2open.util.Log.add(MultiSellId + " loading new entry", "multiselldebug");
        GArray<String> debuglist = new GArray<String>();

        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            //l2open.util.Log.add(MultiSellId + " processing node " + d.getNodeName(), "multiselldebug");
            debuglist.add(d.getNodeName() + " " + d.getAttributes() + " " + d.getNodeName().hashCode() + " " + d.getNodeName().length());
            if (NODE_INGRIDIENT.equalsIgnoreCase(d.getNodeName())) {
                boolean canRetrun = false;
                int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
                long count = Long.parseLong(d.getAttributes().getNamedItem("count").getNodeValue());
                int enchant = 0, element = L2Item.ATTRIBUTE_NONE, elementValue = 0;
                if (d.getAttributes().getNamedItem("enchant") != null)
                    enchant = Integer.parseInt(d.getAttributes().getNamedItem("enchant").getNodeValue());
                if (d.getAttributes().getNamedItem("element") != null)
                    element = Integer.parseInt(d.getAttributes().getNamedItem("element").getNodeValue());
                if (d.getAttributes().getNamedItem("elementValue") != null)
                    elementValue = Integer.parseInt(d.getAttributes().getNamedItem("elementValue").getNodeValue());
                if (d.getAttributes().getNamedItem("canRetrun") != null)
                    canRetrun = Boolean.parseBoolean(d.getAttributes().getNamedItem("canRetrun").getNodeValue());

                //l2open.util.Log.add(MultiSellId + " loaded ingredient " + id + " count " + count, "multiselldebug");
                entry.addIngredient(new MultiSellIngredient(id, count, enchant, element, elementValue, 0, false, canRetrun));
            } else if (NODE_PRODUCTION.equalsIgnoreCase(d.getNodeName())) {
                int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
                long count = Long.parseLong(d.getAttributes().getNamedItem("count").getNodeValue());
                int enchant = 0, element = L2Item.ATTRIBUTE_NONE, elementValue = 0;
                int temporal = 0;


                boolean equip = false;
                if (d.getAttributes().getNamedItem("enchant") != null)
                    enchant = Integer.parseInt(d.getAttributes().getNamedItem("enchant").getNodeValue());
                if (d.getAttributes().getNamedItem("element") != null)
                    element = Integer.parseInt(d.getAttributes().getNamedItem("element").getNodeValue());
                if (d.getAttributes().getNamedItem("elementValue") != null)
                    elementValue = Integer.parseInt(d.getAttributes().getNamedItem("elementValue").getNodeValue());
                if (d.getAttributes().getNamedItem("temporal") != null)
                    temporal = Integer.parseInt(d.getAttributes().getNamedItem("temporal").getNodeValue());
                if (d.getAttributes().getNamedItem("equip") != null)
                    equip = Boolean.parseBoolean(d.getAttributes().getNamedItem("equip").getNodeValue());

                if (!ConfigValue.AllowShadowWeapons && id > 0) {
                    L2Item item = ItemTemplates.getInstance().getTemplate(id);
                    if (item != null && item.isShadowItem() && item.isWeapon() && !ConfigValue.AllowShadowWeapons)
                        return null;
                }

                //l2open.util.Log.add(MultiSellId + " loaded product " + id + " count " + count, "multiselldebug");
                entry.addProduct(new MultiSellIngredient(id, count, enchant, element, elementValue, temporal, equip, false));
            }
            //else
            //	l2open.util.Log.add(MultiSellId + " skipping node " + d.getNodeName(), "multiselldebug");
        }

        if (entry.getIngredients().isEmpty() || entry.getProduction().isEmpty()) {
            l2open.util.Log.add(MultiSellId + " wrong node", "multiselldebug");
            l2open.util.Log.add(MultiSellId + " LIST: " + debuglist.toString(), "multiselldebug");
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                l2open.util.Log.add(d.getNodeName() + " " + d.getAttributes() + " " + d.getNodeName().hashCode() + " " + d.getNodeName().length() + " IsProduction: " + NODE_PRODUCTION.equalsIgnoreCase(d.getNodeName()), "multiselldebug");
            return null;
        }

        if (entry.getIngredients().size() == 1 && entry.getProduction().size() == 1 && entry.getIngredients().get(0).getItemId() == 57) {
            L2Item item = ItemTemplates.getInstance().getTemplate(entry.getProduction().get(0).getItemId());
            if (item == null) {
                _log.warning("WARNING!!! MultiSell [" + MultiSellId + "] Production [" + entry.getProduction().get(0).getItemId() + "] is null");
                return null;
            }
            if ((MultiSellId < 70000 || MultiSellId > 70010) && !ignoreprice) // Все кроме GM Shop
                if (item.getReferencePrice() > entry.getIngredients().get(0).getItemCount())
                    _log.warning("WARNING!!! MultiSell [" + MultiSellId + "] Production '" + item.getName() + "' [" + entry.getProduction().get(0).getItemId() + "] price is lower than referenced | " + item.getReferencePrice() + " > " + entry.getIngredients().get(0).getItemCount());
            //return null;
        }

        return entry;
    }

    private static long[] parseItemIdAndCount(String s) {
        if (s == null || s.isEmpty())
            return null;
        String[] a = s.split(":");
        try {
            long id = Integer.parseInt(a[0]);
            long count = a.length > 1 ? Long.parseLong(a[1]) : 1;
            return new long[]{id, count};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MultiSellEntry parseEntryFromStr(String s) {
        if (s == null || s.isEmpty())
            return null;

        String[] a = s.split("->");
        if (a.length != 2)
            return null;

        long[] ingredient, production;
        if ((ingredient = parseItemIdAndCount(a[0])) == null || (production = parseItemIdAndCount(a[1])) == null)
            return null;

        MultiSellEntry entry = new MultiSellEntry();
        entry.addIngredient(new MultiSellIngredient((int) ingredient[0], ingredient[1]));
        entry.addProduct(new MultiSellIngredient((int) production[0], production[1]));
        return entry;
    }

    public void SeparateAndSend(int listId, L2Player player, double taxRate) {
        for (int i : ConfigValue.DisabledMultisells)
            if (i == listId) {
                player.sendMessage(new CustomMessage("common.Disabled", player));
                return;
            }

        MultiSellListContainer list = generateMultiSell(listId, player, taxRate);
        if (list == null)
            return;
        MultiSellListContainer temp = new MultiSellListContainer();
        int page = 1;

        temp.setListId(list.getListId());

        // Запоминаем отсылаемый лист, чтобы не подменили
        player.setMultisell(list);

        for (MultiSellEntry e : list.getEntries()) {
            if (temp.getEntries().size() == ConfigValue.MultisellPageSize) {
                player.sendPacket(new MultiSellList(temp, page, 0));
                page++;
                temp = new MultiSellListContainer();
                temp.setListId(list.getListId());
            }
            temp.addEntry(e);
        }

        player.sendPacket(new MultiSellList(temp, page, 1));
        if (player.isGM()) {
            if (list.getListId() > 500000000)
                player.sendMessage("PTS Multisell: " + (list.getListId() - 500000000));
            else
                player.sendMessage("Multisell: " + list.getListId());
        }
    }

    private MultiSellListContainer generateMultiSell(int listId, L2Player player, double taxRate) {
        MultiSellListContainer list = new MultiSellListContainer();
        list._listId = listId;

        // Hardcoded  - обмен вещей на равноценные
        GArray<L2ItemInstance> _items;
        if (listId == 9999) {
            list.setShowAll(false);
            list.setKeepEnchant(true);
            list.setNoTax(true);
            final Inventory inv = player.getInventory();
            _items = new GArray<L2ItemInstance>();
            for (final L2ItemInstance itm : inv.getItems())
                if (itm.getItem().getAdditionalName().isEmpty() // Менять можно только обычные предметы
                        && !itm.getItem().isSa() // SA менять нельзя
                        && !itm.getItem().isRare() // Rare менять нельзя
                        && !itm.getItem().isCommonItem() // Common менять нельзя
                        && !itm.getItem().isPvP() // PvP менять нельзя
                        && itm.canBeTraded(player) // универсальная проверка
                        && !itm.isStackable() //
                        && itm.getItem().getItemId() != 21973 // Затычка
                        && itm.getItem().getType2() == L2Item.TYPE2_WEAPON //
                        && itm.getItem().getCrystalType() != Grade.NONE //
                        && itm.getReferencePrice() <= ConfigValue.MammonExchange //
                        && itm.getItem().getCrystalCount() > 0 //
                        && itm.getItem().isTradeable() //
                        && (itm.getCustomFlags() & L2ItemInstance.FLAG_NO_TRADE) != L2ItemInstance.FLAG_NO_TRADE //
                )
                    _items.add(itm);

            for (final L2ItemInstance itm : _items)
                for (L2Weapon i : XmlWeaponLoader.getInstance().getWeapons().values())
                    if (i.getAdditionalName().isEmpty() // Менять можно только обычные предметы
                            && !i.isSa() // На SA менять нельзя
                            && !i.isRare() // На Rare менять нельзя
                            && !i.isCommonItem() // На Common менять нельзя
                            && !i.isPvP() // На PvP менять нельзя
                            && !i.isShadowItem() // На Shadow менять нельзя
                            && i.isTradeable() // можно использовать чтобы запретить менять специальные вещи
                            && i.getItemId() != 21973 //Затычка
                            && i.getItemId() != itm.getItemId() //
                            && i.getType2() == L2Item.TYPE2_WEAPON //
                            && itm.getItem().getCrystalType() != Grade.NONE //
                            && i.getItemType() == WeaponType.DUAL == (itm.getItem().getItemType() == WeaponType.DUAL) //
                            && itm.getItem().getCrystalType() == i.getCrystalType() //
                            && itm.getItem().getCrystalCount() == i.getCrystalCount() //
                    ) {
                        final int entry = new int[]{itm.getItemId(), i.getItemId(), itm.getRealEnchantLevel()}.hashCode();
                        MultiSellEntry possibleEntry = new MultiSellEntry(entry, i.getItemId(), 1, itm.getRealEnchantLevel());
                        possibleEntry.addIngredient(new MultiSellIngredient(itm.getItemId(), 1, itm.getRealEnchantLevel(), false));
                        list.entries.add(possibleEntry);
                    }
        }

        // Hardcoded  - обмен вещей с доплатой за AA
        else if (listId == 9998) {
            list.setShowAll(false);
            list.setKeepEnchant(false);
            list.setNoTax(true);
            final Inventory inv = player.getInventory();
            _items = new GArray<L2ItemInstance>();
            for (final L2ItemInstance itm : inv.getItems())
                if (itm.getItem().getAdditionalName().isEmpty() // Менять можно только обычные предметы
                        && !itm.getItem().isSa() // SA менять нельзя
                        && !itm.getItem().isRare() // Rare менять нельзя
                        && !itm.getItem().isCommonItem() // Common менять нельзя
                        && !itm.getItem().isPvP() // PvP менять нельзя
                        && !itm.getItem().isShadowItem() // Shadow менять нельзя
                        && !itm.isTemporalItem() // Temporal менять нельзя
                        && !itm.isStackable() //
                        && itm.getItem().getType2() == L2Item.TYPE2_WEAPON //
                        && itm.getItem().getCrystalType() != Grade.NONE //
                        && itm.getReferencePrice() <= ConfigValue.MammonUpgrade //
                        && itm.getItem().getCrystalCount() > 0 //
                        && !itm.isEquipped() //
                        && itm.getItem().isTradeable() //
                        && (itm.getCustomFlags() & L2ItemInstance.FLAG_NO_TRADE) != L2ItemInstance.FLAG_NO_TRADE //
                )
                    _items.add(itm);

            for (final L2ItemInstance itemtosell : _items)
                for (final L2Weapon itemtobuy : XmlWeaponLoader.getInstance().getWeapons().values())
                    if (itemtobuy.getAdditionalName().isEmpty() // Менять можно только обычные предметы
                            && !itemtobuy.isSa() // На SA менять нельзя
                            && !itemtobuy.isRare() // На Rare менять нельзя
                            && !itemtobuy.isCommonItem() // На Common менять нельзя
                            && !itemtobuy.isPvP() // На PvP менять нельзя
                            && !itemtobuy.isShadowItem() // На Shadow менять нельзя
                            && itemtobuy.isTradeable() //
                            && itemtobuy.getType2() == L2Item.TYPE2_WEAPON //
                            && itemtobuy.getItemType() == WeaponType.DUAL == (itemtosell.getItem().getItemType() == WeaponType.DUAL) //
                            && itemtobuy.getCrystalType().ordinal() >= itemtosell.getItem().getCrystalType().ordinal() //
                            && itemtobuy.getReferencePrice() <= ConfigValue.MammonUpgrade //
                            && itemtosell.getItem().getReferencePrice() < itemtobuy.getReferencePrice() //
                            && itemtosell.getReferencePrice() * 1.7 > itemtobuy.getReferencePrice() //
                    ) {
                        final int entry = new int[]{itemtosell.getItemId(), itemtobuy.getItemId(), itemtosell.getRealEnchantLevel()}.hashCode();
                        MultiSellEntry possibleEntry = new MultiSellEntry(entry, itemtobuy.getItemId(), 1, 0);
                        possibleEntry.addIngredient(new MultiSellIngredient(itemtosell.getItemId(), 1, itemtosell.getRealEnchantLevel(), false));
                        possibleEntry.addIngredient(new MultiSellIngredient((short) 5575, (int) ((itemtobuy.getReferencePrice() - itemtosell.getReferencePrice()) * 1.2), 0, false));
                        list.entries.add(possibleEntry);
                    }
        }

        // Hardcoded  - обмен вещей на кристаллы
        else if (listId == 9997) {
            list.setShowAll(false);
            list.setKeepEnchant(true);
            list.setNoTax(false);
            final Inventory inv = player.getInventory();
            for (final L2ItemInstance itm : inv.getItems())
                if (!itm.isStackable() && itm.getItem().isCrystallizable() && itm.getItem().getCrystalType() != Grade.NONE && itm.getItem().getCrystalCount() > 0 && !itm.isShadowItem() && !itm.isTemporalItem() && !itm.isEquipped() && (itm.getCustomFlags() & L2ItemInstance.FLAG_NO_CRYSTALLIZE) != L2ItemInstance.FLAG_NO_CRYSTALLIZE) {
                    final L2Item crystal = ItemTemplates.getInstance().getTemplate(itm.getItem().getCrystalType().cry);
                    final int entry = new int[]{itm.getItemId(), itm.getRealEnchantLevel()}.hashCode();
                    MultiSellEntry possibleEntry = new MultiSellEntry(entry, crystal.getItemId(), itm.getItem().getCrystalCount(), itm.getRealEnchantLevel());
                    possibleEntry.addIngredient(new MultiSellIngredient(itm.getItemId(), 1, itm.getRealEnchantLevel(), false));
                    possibleEntry.addIngredient(new MultiSellIngredient((short) 57, (int) (itm.getItem().getCrystalCount() * crystal.getReferencePrice() * 0.05), 0, false));
                    list.entries.add(possibleEntry);
                }
        }

        // Все мультиселлы из датапака
        else {
            MultiSellListContainer container = L2Multisell.getInstance().getList(listId);
            if (container == null) {
                _log.warning("Not found multisell " + listId);
                return null;
            } else if (container.isEmpty()) {
                player.sendMessage(new CustomMessage("common.Disabled", player));
                return null;
            }

            boolean enchant = container.isKeepEnchant();
            boolean notax = container.isNoTax();
            boolean showall = container.isShowAll();
            boolean nokey = container.isNoKey();


            list.setShowAll(showall);
            list.setKeepEnchant(enchant);
            list.setNoTax(notax);
            list.setNoKey(nokey);

            final Inventory inv = player.getInventory();
            for (MultiSellEntry origEntry : container.getEntries()) {
                MultiSellEntry ent = origEntry.clone();

                // Обработка налога, если лист не безналоговый
                // Адены добавляются в лист если отсутствуют или прибавляются к существующим
                GArray<MultiSellIngredient> ingridients;
                if (!notax && taxRate > 0.) {
                    double tax = 0, adena = 0;
                    ingridients = new GArray<MultiSellIngredient>(ent.getIngredients().size() + 1);
                    for (MultiSellIngredient i : ent.getIngredients()) {
                        if (i.getItemId() == 57) {
                            adena += i.getItemCount();
                            tax += i.getItemCount() * (taxRate);
                            continue;
                        }
                        ingridients.add(i);
                        if (i.getItemId() == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE)
                            // hardcoded. Налог на клановую репутацию. Формула проверена на с6 и соответсвует на 100%.
                            //TODO: Проверить на корейском(?) оффе налог на банг поинты и fame
                            tax += i.getItemCount() / 120 * 1000 * taxRate * 100;
                        if (i.getItemId() < 1)
                            continue;

                        final L2Item item = ItemTemplates.getInstance().getTemplate(i.getItemId());
                        if (item == null)
                            System.out.println("Not found template for itemId: " + i.getItemId());
                        else if (item.isStackable())
                            tax += item.getReferencePrice() * i.getItemCount() * taxRate;
                    }

                    adena = Math.round(adena + tax);
                    if (adena >= 1)
                        ingridients.add(new MultiSellIngredient(57, (long) adena));

                    tax = Math.round(tax);
                    if (tax >= 1)
                        ent.setTax((long) tax);

                    ent.getIngredients().clear();
                    ent.getIngredients().addAll(ingridients);
                } else
                    ingridients = ent.getIngredients();

                // Если стоит флаг "показывать все" не проверять наличие ингридиентов
                if (showall)
                    list.entries.add(ent);
                else {
                    GArray<Integer> _itm = new GArray<Integer>();
                    // Проверка наличия у игрока ингридиентов
                    for (MultiSellIngredient i : ingridients) {
                        L2Item template = i.getItemId() <= 0 ? null : ItemTemplates.getInstance().getTemplate(i.getItemId());
                        if (i.getItemId() <= 0 || template.getType2() <= L2Item.TYPE2_ACCESSORY || template.getType2() >= (nokey ? L2Item.TYPE2_OTHER : L2Item.TYPE2_PET_WOLF)) // Экипировка
                        {
                            if (i.getItemId() == 12374) // Mammon's Varnish Enhancer
                                continue;

                            //TODO: а мы должны тут сверять count?
                            if (i.getItemId() == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE) {
                                if (!_itm.contains(i.getItemId()) && player.getClan() != null && player.getClan().getReputationScore() >= i.getItemCount())
                                    _itm.add(i.getItemId());
                                continue;
                            } else if (i.getItemId() == L2Item.ITEM_ID_PC_BANG_POINTS) {
                                if (!_itm.contains(i.getItemId()) && player.getPcBangPoints() >= i.getItemCount())
                                    _itm.add(i.getItemId());
                                continue;
                            } else if (i.getItemId() == L2Item.PVP_COIN) {
                                if (!_itm.contains(i.getItemId()) && player.getPvpKills() >= i.getItemCount())
                                    _itm.add(i.getItemId());
                                continue;
                            } else if (i.getItemId() == L2Item.ITEM_ID_FAME) {
                                if (!_itm.contains(i.getItemId()) && player.getFame() >= i.getItemCount())
                                    _itm.add(i.getItemId());
                                continue;
                            } else if (i.getItemId() == L2Item.ITEM_ID_OWER_POINT) {
                                if (!_itm.contains(i.getItemId()) && player.getRangPoint() >= i.getItemCount())
                                    _itm.add(i.getItemId());
                                continue;
                            } else if (i.getItemId() == L2Item.ITEM_ID_RAID_POINT) {
                                if (!_itm.contains(i.getItemId()) && player.getRaidPoints() >= i.getItemCount())
                                    _itm.add(i.getItemId());
                                continue;
                            }

                            for (final L2ItemInstance item : inv.getItems())
                                if (item.getItemId() == i.getItemId() && !item.isEquipped() && (item.getCustomFlags() & L2ItemInstance.FLAG_NO_TRADE) != L2ItemInstance.FLAG_NO_TRADE) {
                                    if (_itm.contains(enchant ? i.getItemId() + item.getRealEnchantLevel() * 100000L : i.getItemId())) // Не проверять одинаковые вещи
                                        continue;

                                    if (item.getRealEnchantLevel() < i.getItemEnchant()) // Некоторые мультиселлы требуют заточки
                                        continue;

                                    if (item.isStackable() && item.getCount() < i.getItemCount())
                                        break;

                                    _itm.add(enchant ? i.getItemId() + item.getRealEnchantLevel() * 100000 : i.getItemId());
                                    MultiSellEntry possibleEntry = new MultiSellEntry(enchant ? ent.getEntryId() + item.getRealEnchantLevel() * 100000 : ent.getEntryId());

                                    for (MultiSellIngredient p : ent.getProduction()) {
                                        p.setItemEnchant(item.getRealEnchantLevel());
                                        p.setElement(item.getAttackAttributeElement(), item.getAttackElementValue());
                                        possibleEntry.addProduct(p);
                                    }

                                    for (MultiSellIngredient ig : ingridients) {
                                        if (template != null && template.getType2() <= L2Item.TYPE2_ACCESSORY) {
                                            ig.setItemEnchant(item.getRealEnchantLevel());
                                            ig.setElement(item.getAttackAttributeElement(), item.getAttackElementValue());
                                        }
                                        possibleEntry.addIngredient(ig);
                                    }
                                    list.entries.add(possibleEntry);
                                    break;
                                }
                        }
                    }
                }
            }
        }

        return list;
    }

    public static void unload() {
        if (_instance != null) {
            _instance.entries.clear();
            _instance = null;
        }
    }
}