package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Multisell.MultiSellListContainer;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.L2Augmentation;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.base.MultiSellIngredient;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.ExPCCafePointInfo;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.SafeMath;
import l2open.util.Rnd;
import l2open.util.Util;

import java.nio.BufferUnderflowException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.logging.Logger;

public class RequestMultiSellChoose extends L2GameClientPacket {
    // format: cdddhdddddddddd
    private static Logger _log = Logger.getLogger(RequestMultiSellChoose.class.getName());
    private int _listId;
    private int _entryId;
    private long _amount;
    private byte attackElement = L2Item.ATTRIBUTE_NONE;
    private int attackElementValue = 0;
    private int[] deffAttr = null;
    private boolean _keepenchant = false;
    private boolean _notax = false;
    private boolean hasAttr = false;
    private MultiSellListContainer _list = null;
    private GArray<ItemData> _items = new GArray<ItemData>();
    private String query = "INSERT INTO `multisell_log` VALUES (?,?,?,?,?,?,?);";

    private class ItemData {
        private final int _id;
        private final long _count;
        private final L2ItemInstance _item;
        private final boolean canReturn;

        public ItemData(int id, long count, L2ItemInstance item, boolean retrn) {
            _id = id;
            _count = count;
            _item = item;
            canReturn = retrn;
        }

        public int getId() {
            return _id;
        }

        public long getCount() {
            return _count;
        }

        public L2ItemInstance getItem() {
            return _item;
        }

        public boolean canReturn() {
            return canReturn;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ItemData))
                return false;

            ItemData i = (ItemData) obj;

            return _id == i._id && _count == i._count && _item == i._item;
        }
    }

    @Override
    public void readImpl() {
        try {
            _listId = readD();
            _entryId = readD();
            _amount = readQ();
        } catch (BufferUnderflowException e) {
            _log.warning(getClient().getLoginName() + " maybe packet cheater!");
        }
    }

    @Override
    public void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.is_block)
            return;

        if (activeChar.getEventMaster() != null && activeChar.getEventMaster().blockNpcBypass())
            return;
        if (!ConfigValue.AltKarmaPlayerCanShop && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        _list = activeChar.getMultisell();

        // На всякий случай...
        if (_list == null) {
            activeChar.sendActionFailed();
            return;
        }

        // Проверяем, не подменили ли id
        if (activeChar.getMultisell().getListId() != _listId) {
            Util.handleIllegalPlayerAction(activeChar, "RequestMultiSellChoose[134]", "Tried to buy from multisell: " + _listId, 1);
            return;
        }

        if (_amount < 1) {
            activeChar.sendActionFailed();
            return;
        }

        _keepenchant = _list.isKeepEnchant();
        _notax = _list.isNoTax();

        List<L2ItemInstance> _list_ui = new ArrayList<>();
        for (MultiSellEntry entry : _list.getEntries())
            if (entry.getEntryId() == _entryId) {
                doExchange(activeChar, entry, _list_ui);
                break;
            }
        activeChar.sendPacket(new InventoryUpdate(_list_ui));
    }

    private void doExchange(L2Player activeChar, MultiSellEntry entry, List<L2ItemInstance> _list_ui) {
        PcInventory inv = activeChar.getInventory();

        long totalAdenaCost = 0;
        long tax;
        try {
            tax = SafeMath.safeMulLong(entry.getTax(), _amount);
        } catch (ArithmeticException e) {
            return;
        }
        L2NpcInstance merchant = activeChar.getLastNpc();
        Castle castle = merchant != null ? merchant.getCastle(activeChar) : null;

        GArray<MultiSellIngredient> productId = entry.getProduction();
        boolean logExchange = Util.contains_int(ConfigValue.LogMultisellId, _listId);

        String itemIds = "";
        String counts = "";
        String dItemId = "";
        String dCount = "";

        synchronized (inv) {
            int slots = inv.slotsLeft();
            if (slots == 0) {
                activeChar.sendPacket(Msg.THE_WEIGHT_AND_VOLUME_LIMIT_OF_INVENTORY_MUST_NOT_BE_EXCEEDED);
                return;
            }

            int req = 0;
            long totalLoad = 0;
            for (MultiSellIngredient i : productId) {
                if (i.getItemId() <= 0)
                    continue;
                totalLoad += ItemTemplates.getInstance().getTemplate(i.getItemId()).getWeight() * _amount;
                if (!ItemTemplates.getInstance().getTemplate(i.getItemId()).isStackable())
                    req += _amount;
                else
                    req++;
            }
            if (req > slots || !inv.validateWeight(totalLoad)) {
                activeChar.sendPacket(Msg.THE_WEIGHT_AND_VOLUME_LIMIT_OF_INVENTORY_MUST_NOT_BE_EXCEEDED);
                return;
            }

            if (entry.getIngredients().size() == 0) {
                _log.info("WARNING Ingredients list = 0 multisell id=:" + _listId + " player:" + activeChar.getName());
                activeChar.sendActionFailed();
                return;
            }

            L2Augmentation augmentation = null;

            // Перебор всех ингридиентов, проверка наличия и создание списка забираемого
            for (MultiSellIngredient ingridient : entry.getIngredients()) {
                int ingridientEnchant = ingridient.getItemEnchant();
                int ingridientItemId = ingridient.getItemId();
                long ingridientItemCount = ingridient.getItemCount();
                long total_amount;
                try {
                    total_amount = SafeMath.safeMulLong(ingridientItemCount, _amount);
                } catch (ArithmeticException e) {
                    activeChar.sendActionFailed();
                    return;
                }

                if (ingridientItemId > 0 && !ItemTemplates.getInstance().getTemplate(ingridientItemId).isStackable())
                    for (int i = 0; i < ingridientItemCount * _amount; i++) {
                        L2ItemInstance[] list = inv.getAllItemsById(ingridientItemId);
                        // Если энчант имеет значение - то ищем вещи с точно таким энчантом
                        if (_keepenchant) {
                            L2ItemInstance itemToTake = null;
                            for (L2ItemInstance itm : list)
                                if ((itm.getRealEnchantLevel() == ingridientEnchant || itm.getItem().getType2() > 2) && !_items.contains(new ItemData(itm.getItemId(), itm.getCount(), itm, ingridient.canReturn())) && !itm.isShadowItem() && !itm.isTemporalItem() && (itm.getCustomFlags() & L2ItemInstance.FLAG_NO_TRADE) != L2ItemInstance.FLAG_NO_TRADE) {
                                    hasAttr = itm.hasAttribute();
                                    if (hasAttr) {
                                        attackElement = itm.getAttackAttributeElement();
                                        attackElementValue = itm.getAttackElementValue();
                                        deffAttr = itm.getDeffAttr();
                                    }
                                    itemToTake = itm;
                                    break;
                                }
                            if (itemToTake == null) {
                                activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                //_log.info("YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS 1");
                                return;
                            }

                            if (!checkItem(itemToTake, activeChar)) {
                                activeChar.sendActionFailed();
                                return;
                            }

                            if (itemToTake.getAugmentation() != null)
                                augmentation = itemToTake.getAugmentation();
                            _items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake, ingridient.canReturn()));
                        }
                        // Если энчант не обрабатывается берется вещь с наименьшим энчантом
                        else {
                            L2ItemInstance itemToTake = null;
                            for (L2ItemInstance itm : list)
                                if (!_items.contains(new ItemData(itm.getItemId(), itm.getCount(), itm, ingridient.canReturn())) && (itemToTake == null || itm.getRealEnchantLevel() < itemToTake.getRealEnchantLevel()) && !itm.isShadowItem() && !itm.isTemporalItem() && (itm.getCustomFlags() & L2ItemInstance.FLAG_NO_TRADE) != L2ItemInstance.FLAG_NO_TRADE && checkItem(itm, activeChar)) {
                                    itemToTake = itm;
                                    if (itemToTake.getRealEnchantLevel() == 0)
                                        break;
                                }

                            if (itemToTake == null) {
                                activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                //_log.info("YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS 2");
                                return;
                            }
                            if (itemToTake.getAugmentation() != null)
                                augmentation = itemToTake.getAugmentation();
                            _items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake, ingridient.canReturn()));
                        }
                    }
                else if (ingridientItemId == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE) {
                    if (activeChar.getClan() == null) {
                        activeChar.sendPacket(Msg.YOU_ARE_NOT_A_CLAN_MEMBER);
                        return;
                    }

                    if (activeChar.getClan().getReputationScore() < total_amount) {
                        activeChar.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                        return;
                    }

                    if (activeChar.getClan().getLeaderId() != activeChar.getObjectId()) {
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addString(activeChar.getName()));
                        return;
                    }
                    _items.add(new ItemData(ingridientItemId, total_amount, null, ingridient.canReturn()));
                } else if (ingridientItemId == L2Item.ITEM_ID_PC_BANG_POINTS) {
                    if (activeChar.getPcBangPoints() < total_amount) {
                        activeChar.sendPacket(Msg.YOU_ARE_SHORT_OF_ACCUMULATED_POINTS);
                        return;
                    }
                    _items.add(new ItemData(ingridientItemId, total_amount, null, ingridient.canReturn()));
                } else if (ingridientItemId == L2Item.ITEM_ID_FAME) {
                    if (activeChar.getFame() < total_amount) {
                        activeChar.sendPacket(Msg.NOT_ENOUGH_FAME_POINTS);
                        return;
                    }
                    _items.add(new ItemData(ingridientItemId, total_amount, null, ingridient.canReturn()));
                } else if (ingridientItemId == L2Item.ITEM_ID_OWER_POINT) {
                    if (activeChar.getRangPoint() < total_amount) {
                        activeChar.sendMessage("У вас не достаточно Очков Воина.");
                        return;
                    }
                    _items.add(new ItemData(ingridientItemId, total_amount, null, ingridient.canReturn()));
                } else if (ingridientItemId == L2Item.PVP_COIN) {
                    if (activeChar.getPvpKills() < total_amount) {
                        activeChar.sendMessage("У вас не достаточно PvP.");
                        return;
                    }
                    _items.add(new ItemData(ingridientItemId, total_amount, null, ingridient.canReturn()));
                } else if (ingridientItemId == L2Item.ITEM_ID_RAID_POINT) {
                    if (activeChar.getRaidPoints() < total_amount) {
                        activeChar.sendMessage("У вас не достаточно Raid Point.");
                        return;
                    }
                    _items.add(new ItemData(ingridientItemId, total_amount, null, ingridient.canReturn()));
                } else {
                    if (ingridientItemId == 57)
                        totalAdenaCost += total_amount;
                    L2ItemInstance item = inv.getItemByItemId(ingridientItemId);

                    if (item == null || item.getCount() < total_amount) {
                        activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                        //_log.info("YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS 3");
                        return;
                    }

                    _items.add(new ItemData(item.getItemId(), total_amount, item, ingridient.canReturn()));
                }

                if (activeChar.getAdena() < totalAdenaCost) {
                    activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    return;
                }
            }

            int _visual_item_id = -1;
            int _visual_enchant_level = -1;

            boolean succ = Rnd.chance(entry.getChance());
            boolean succ2 = Rnd.chance(entry.getReturnChance());
            for (ItemData id : _items) {
                long count = id.getCount();
                if (count > 0) {
                    L2ItemInstance item = id.getItem();

                    if (item != null) {
                        if (succ || !succ2 || !id.canReturn()) {
                            if (item._visual_item_id > 0 && (item.getBodyPart() == L2Item.SLOT_FULL_ARMOR || item.getBodyPart() == L2Item.SLOT_CHEST || item.getBodyPart() == L2Item.SLOT_FORMAL_WEAR || item.isWeapon()))
                                _visual_item_id = item._visual_item_id;
                            if (item._visual_enchant_level > -1 && item.isWeapon())
                                _visual_enchant_level = item._visual_enchant_level;
                            activeChar.sendPacket(SystemMessage.removeItems(item.getItemId(), count));
                            if (item.isEquipped())
                                inv.unEquipItemInSlot(item.getEquipSlot());
                            _list_ui.add(inv.destroyItem(item, count, true, false));
                        } else if (!succ && succ2 && id.canReturn()) {

                        }
                    } else if (id.getId() == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE) {
                        activeChar.getClan().incReputation((int) -count, false, "MultiSell" + _listId);
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_REPUTATION_SCORE).addNumber(count));
                    } else if (id.getId() == L2Item.ITEM_ID_PC_BANG_POINTS)
                        activeChar.reducePcBangPoints((int) count);
                    else if (id.getId() == L2Item.PVP_COIN) {
                        activeChar.setPvpKills(activeChar.getPvpKills() - (int) count);
                        activeChar.sendUserInfo(false);
                    } else if (id.getId() == L2Item.ITEM_ID_FAME) {
                        activeChar.setFame(activeChar.getFame() - (int) count, "MultiSell" + _listId);
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S2_S1_HAS_DISAPPEARED).addNumber(count).addString("Fame"));
                    } else if (id.getId() == L2Item.ITEM_ID_OWER_POINT) {
                        activeChar.setRangPoint(activeChar.getRangPoint() - (int) count);
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S2_S1_HAS_DISAPPEARED).addNumber(count).addString("Очки Воина"));
                    } else if (id.getId() == L2Item.ITEM_ID_RAID_POINT) {
                        activeChar.addRaidPoints((int) (-count));
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S2_S1_HAS_DISAPPEARED).addNumber(count).addString("Raid Point"));
                    }
                    dItemId += id.getId() + ";";
                    dCount += id.getCount() + ";";
                }
            }

            if (tax > 0 && !_notax)
                if (castle != null) {
                    activeChar.sendMessage("Tax: " + tax);
                    if (merchant != null && merchant.getReflection().getId() == 0) {
                        castle.addToTreasury(tax, true, false);
                        Log.add(castle.getName() + "|" + tax + "|Multisell", "treasury");
                    }
                }

            if (!succ) {
                activeChar.sendActionFailed();
                activeChar.sendMessage("Неудачное улучшение предмета.");
                return;
            }

            for (MultiSellIngredient in : productId) {
                if (in.getItemId() <= 0) {
                    if (in.getItemId() == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE) {
                        activeChar.getClan().incReputation((int) (in.getItemCount() * _amount), false, "MultiSell" + _listId);
                        activeChar.sendPacket(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(in.getItemCount() * _amount));
                    } else if (in.getItemId() == L2Item.PVP_COIN) {
                        activeChar.setPvpKills(activeChar.getPvpKills() + (int) (in.getItemCount() * _amount));
                        activeChar.sendUserInfo(false);
                    } else if (in.getItemId() == L2Item.ITEM_ID_PC_BANG_POINTS)
                        activeChar.addPcBangPoints((int) (in.getItemCount() * _amount), false, 1);
                    else if (in.getItemId() == L2Item.ITEM_ID_FAME)
                        activeChar.setFame(activeChar.getFame() + (int) (in.getItemCount() * _amount), "MultiSell" + _listId);
                    else if (in.getItemId() == L2Item.ITEM_ID_OWER_POINT)
                        activeChar.setRangPoint(activeChar.getRangPoint() + (int) (in.getItemCount() * _amount));
                    else if (in.getItemId() == L2Item.ITEM_ID_RAID_POINT)
                        activeChar.addRaidPoints((int) (in.getItemCount() * _amount));
                } else if (ItemTemplates.getInstance().getTemplate(in.getItemId()).isStackable()) {
                    L2ItemInstance product = ItemTemplates.getInstance().createItem(in.getItemId());
                    double total = in.getItemCount() * _amount;

                    if (total <= 0 || total > Long.MAX_VALUE) {
                        activeChar.sendActionFailed();
                        continue;
                    }

                    product.setCount((long) total);
                    activeChar.sendPacket(SystemMessage.obtainItems(product));
                    _list_ui.add(inv.addItem(product, true, true, false, false));
                } else {
                    for (int i = 0; i < in.getItemCount() * _amount; i++) {
                        L2ItemInstance product = new L2ItemInstance(IdFactory.getInstance().getNextId(), ItemTemplates.getInstance().getTemplate(in.getItemId()), in._temporal > 0);
                        if (in._temporal > 0) {
                            product._temporal = true;
                            product._lifeTimeRemaining = (int) (System.currentTimeMillis() / 1000) + in._temporal;
                        }
                        if (_keepenchant) {
                            product.setEnchantLevel(in.getItemEnchant());
                            if (deffAttr != null)
                                product.setAttributeElement(attackElement, attackElementValue, deffAttr, true);
                        }
                        if (ConfigValue.RequestMultiSellChooseAugmentSet && augmentation != null && product.isEquipable() && product.canBeEnchanted() && !product.isRaidAccessory())
                            product.setAugmentation(augmentation);

                        if (_visual_item_id > 0 && (product.getBodyPart() == L2Item.SLOT_FULL_ARMOR || product.getBodyPart() == L2Item.SLOT_CHEST || product.getBodyPart() == L2Item.SLOT_FORMAL_WEAR || product.isWeapon()))
                            //product.setVisualItemId(_visual_item_id);
                            product._visual_item_id = _visual_item_id;
                        if (_visual_enchant_level > -1 && product.isWeapon())
                            product._visual_enchant_level = _visual_enchant_level;

                        _list_ui.add(inv.addItem(product, true, true, false, false));
                        activeChar.sendPacket(SystemMessage.obtainItems(product));
                        if (in._setEquip)
                            activeChar.tryEqupUneqipItem(product);
                    }
                }
                itemIds += in.getItemId() + ";";
                counts += (in.getItemCount() * _amount) + ";";
            }
        }

        activeChar.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
        if (logExchange) {
            Log.add("MultisellBuy[" + _listId + "][" + activeChar.getAccountName() + "]: items_buy[" + itemIds + "] items_buy_count[" + counts + "] price_items[" + dItemId + "] price_items_count[" + dCount + "]", "multisell_debug");
            if (ConfigValue.LogMultisellToSql) {
                ThreadConnection con = null;
                FiltredPreparedStatement statement = null;
                try {
                    con = L2DatabaseFactory.getInstance().getConnection();
                    statement = con.prepareStatement(query);
                    statement.setString(1, String.valueOf(_listId));
                    statement.setString(2, new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    statement.setString(3, itemIds);
                    statement.setString(4, counts);
                    statement.setString(5, dItemId);
                    statement.setString(6, dCount);
                    statement.setString(7, activeChar.getName());
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    DatabaseUtils.closeDatabaseCS(con, statement);
                }
            }
        }

        if (_list == null || !_list.isShowAll()) // Если показывается только то, на что хватает материалов обновить окно у игрока
            L2Multisell.getInstance().SeparateAndSend(_listId, activeChar, castle == null ? 0 : castle.getTaxRate());
    }

    private boolean checkItem(L2ItemInstance temp, L2Player activeChar) {
        if (temp == null)
            return false;

        if (temp.isHeroWeapon())
            return false;

        if (temp.isShadowItem())
            return false;

        if (temp.isTemporalItem())
            return false;

        if (PetDataTable.isPetControlItem(temp) && activeChar.isMounted())
            return false;

        if (activeChar.getPet() != null && temp.getObjectId() == activeChar.getPet().getControlItemObjId())
            return false;

        if (temp.isEquipped())
            return false;

        if (temp.isWear())
            return false;

        if (activeChar.getEnchantScroll() == temp)
            return false;

        return true;
    }
}