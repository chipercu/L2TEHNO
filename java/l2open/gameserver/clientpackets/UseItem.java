package l2open.gameserver.clientpackets;

import java.nio.BufferUnderflowException;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.L2PlayableAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2Vehicle;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ShowCalc;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Util;

public class UseItem extends L2GameClientPacket {
    private int _objectId;
    private boolean ctrl_pressed;

    /**
     * packet type id 0x19
     * format:		cdd
     */
    @Override
    public void readImpl() {
        try {
            _objectId = readD();
            ctrl_pressed = readD() == 1;
        } catch (BufferUnderflowException e) {
            e.printStackTrace();
            _log.info("Attention! Possible cheater found! Login:" + getClient().getLoginName());
        }
    }

    @Override
    public void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.setActive();

        if (activeChar.isOutOfControl() || activeChar.isBlockUseItem()) {
            activeChar.sendActionFailed();
            return;
        }
        //Нельзя использовать любые предметы в тюрьме.
        else if (activeChar.getVar("jailed") != null) {
            activeChar.sendMessage("You cannot use items in Jail.");
            return;
        }

        synchronized (activeChar.getInventory()) {
            L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
//            for (L2Skill attachedSkill : item.getItem().getAttachedSkills()) {
//
//                switch (attachedSkill.getSkillType()){
//                    case HEAL: {
//                        activeChar.getCurrentHp()
//                    }
//                }
//
//
//            }

            if (item != null && activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE && !Util.contains_int(ConfigValue.OfftradeItem, item.getItemId())) {
                activeChar.sendPacket(Msg.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP, Msg.ActionFail);
                return;
            } else if (item != null && activeChar.getEventMaster() != null && !activeChar.getEventMaster().canUseItem(activeChar, item)) {
                //activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
                return;
            } else if (item == null) {
                if (activeChar.getPet() == null)
                    return;
                if (true && activeChar.getPet().isPet()) {
                    L2PetInstance pet = (L2PetInstance) activeChar.getPet();

                    item = pet.getInventory().getItemByObjectId(_objectId);

                    if (item == null || item.getCount() <= 0)
                        return;
                    else if (activeChar.isAlikeDead() || pet.isDead()) {
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
                        return;
                    } else if (activeChar.getInventory().isLockedItem(item))
                        return;
                    else if (pet.tryEquipItem(item, true))
                        return;
                        // manual pet feeding
                    else if (pet.tryFeedItem(item, true))
                        return;

                    int item_disable_id;
                    int grp_id = item.getItem().delay_share_group;

                    if (grp_id > 0)
                        item_disable_id = (grp_id * 65536 + item.getItemId()) * -1;
                    else {
                        item_disable_id = item.getItemId() * -1;
                        grp_id = item.getItemId();
                    }

                    L2Skill[] skills = item.getItem().getAttachedSkills();
                    if (item.getItem().immediate_effect == 1 && skills != null && skills.length > 0) {
                        if (!pet.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? item_disable_id * 65536L + 1 : item_disable_id)) {
                            boolean _consume = false;
                            for (L2Skill skill : skills) {
                                L2Character aimingTarget = skill.getAimingTarget(pet, pet.getTarget());
                                int condResult = skill.condition(pet, aimingTarget, true);
                                if (condResult == -1)
                                    return;
                                if (item.getItem().isConsume() && !_consume) {
                                    if (pet.getInventory().destroyItem(item, 1, false) == null)
                                        return;
                                    _consume = true;
                                }
                                pet.altUseSkill(skill, aimingTarget, false);
                                pet.disableItem(item_disable_id, 1/*skill.getLevel()*/, item.getItemId(), grp_id, item.getReuseDelay(), item.getReuseDelay());
                            }
                        }
                    } else
                        activeChar.sendPacket(Msg.ITEM_NOT_AVAILABLE_FOR_PETS);
                }
                activeChar.sendActionFailed();
                return;
            }

            int itemId = item.getItemId();
            if (itemId == 57) {
                activeChar.sendActionFailed();
                return;
            } else if (activeChar.isFishing() && (itemId < 6535 || itemId > 6540)) {
                // You cannot do anything else while fishing
                activeChar.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
                return;
            } else if (activeChar.isDead()) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                return;
            } else if (item.getItem().isForPet()) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_EQUIP_A_PET_ITEM).addItemName(itemId));
                return;
            }
            // Маги не могут вызывать Baby Buffalo Improved
            else if (ConfigValue.ImprovedPetsLimitedUse && activeChar.isMageClass() && item.getItemId() == 10311) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                return;
            }
            // Войны не могут вызывать Improved Baby Kookaburra
            else if (ConfigValue.ImprovedPetsLimitedUse && !activeChar.isMageClass() && item.getItemId() == 10313) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                return;
            } else if (!item.getOlympiadUse() && activeChar.isInOlympiadMode()) {
                activeChar.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
                return;
            } else if (item.getItem().isQuest()) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_USE_QUEST_ITEMS));
                return;
            }
            // Нельзя использовать зелья и тп.
            else if (activeChar.isPotionsDisabled()) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                return;
            } else if (/*!item.getOlympiadUse() && activeChar.isInEvent() == 1 || */item.getItemId() == 10411 && !activeChar.isInZone(ZoneType.Siege)) {
                activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
                return;
            } else if (activeChar.getInventory().isLockedItem(item))
                return;
            else if (item.isEquipable()) {
                if (activeChar.getEffectList().getEffectByType(EffectType.Disarm) != null && (item.isWeapon())) {
                    activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                    return;
                }
                // Нельзя снимать/одевать любое снаряжение при этих условиях
                else if (activeChar.isStunned() || activeChar.isActionBlock() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead()) {
                    activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                    return;
                }

                int bodyPart = item.getBodyPart();

                if (bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND) {
                    // Нельзя снимать/одевать оружие, сидя на пете
                    if (activeChar.isMounted()) {
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                        return;
                    }
                    // Нельзя снимать/одевать проклятое оружие и флаги
                    else if (activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped()) {
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                        return;
                    }

                    // Нельзя одевать/снимать оружие/щит/сигил, управляя кораблем
                    L2Vehicle vehicle = activeChar.getVehicle();
                    if (vehicle != null && vehicle.isAirShip()) {
                        L2AirShip airship = (L2AirShip) vehicle;
                        if (airship.getDriver() == activeChar) {
                            activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                            return;
                        }
                    }
                }

                // Нельзя снимать/одевать проклятое оружие
                if (item.isCursed()) {
                    activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                    return;
                } else if ((item.getCustomFlags() & L2ItemInstance.FLAG_NO_UNEQUIP) == L2ItemInstance.FLAG_NO_UNEQUIP) {
                    activeChar.sendActionFailed();
                    return;
                }
                // Don't allow weapon/shield hero equipment during Olympiads
                else if (activeChar.isInOlympiadMode() && item.isHeroWeapon() && !item.getItem()._is_hero) {
                    activeChar.sendActionFailed();
                    return;
                } else if (activeChar.isCastingNow()) {
                    activeChar.getAI().setNextAction(L2PlayableAI.nextAction.EQIP, item, null, false, false);
                    return;
                }

                activeChar.tryEqupUneqipItem(item);
                return;
            } else if (itemId == 4393) {
                activeChar.sendPacket(new ShowCalc(itemId));
                return;
            } else if (ItemTemplates.useHandler(activeChar, item, ctrl_pressed) >= 0)
                return;

            if (activeChar.getEventMaster() == null || !activeChar.getEventMaster().useItem(activeChar, item, ctrl_pressed)) {
                IItemHandler handler = ItemHandler.getInstance().getItemHandler(itemId);
                if (handler != null)
                    handler.useItem(activeChar, item, ctrl_pressed);
            }
        }
    }

    public String getType() {
        return "[C] UseItem[" + _objectId + "][" + ctrl_pressed + "]";
    }
}