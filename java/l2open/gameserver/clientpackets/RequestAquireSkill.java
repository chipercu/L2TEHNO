package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2VillageMasterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SkillSpellbookTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.Util;

import java.util.List;

public class RequestAquireSkill extends L2GameClientPacket 
{
    // format: cddd(d)
    private int _id, _level, _skillType;
    private int _pLevel = -1;

    @Override
    public void readImpl() 
	{
        _id = readD();
        _level = readD();
        _skillType = readD();
        if (_skillType == 3)
            _pLevel = readD();
    }

    @Override
    public void runImpl() 
	{
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.getTransformation() != 0)
            return;
        L2NpcInstance trainer = activeChar.getLastNpc();
        if((trainer == null || activeChar.getDistance(trainer.getX(), trainer.getY()) > trainer.INTERACTION_DISTANCE+trainer.BYPASS_DISTANCE_ADD) && !activeChar.isGM() && _skillType > AcquireSkillList.OTHER && !activeChar.isLindvior() && !ConfigValue.Multi_Enable3 && !ConfigValue.EnableSkillLearnToBbs/* && _skillType != AcquireSkillList.USUAL*/)
            return;
		if(trainer != null && _skillType == 0 && (activeChar.isLindvior() || ConfigValue.Multi_Enable3 || ConfigValue.EnableSkillLearnToBbs))
			trainer=null;

        activeChar.setSkillLearningClassId(activeChar.getClassId());

        L2Skill skill = SkillTable.getInstance().getInfo(_id, _level);
        if (skill == null)
            return;

        if (activeChar.getSkillLevel(_id) >= _level && _skillType != AcquireSkillList.CLAN_ADDITIONAL)
            return; // already knows the skill with this level

        boolean isTransferSkill = _skillType == AcquireSkillList.TRANSFER;

        if (isTransferSkill && (activeChar.getLevel() < 76 || activeChar.getClassId().getLevel() < 4)) 
		{
            activeChar.sendMessage("You must have 3rd class change quest completed.");
            return;
        }

        if (_pLevel == -1 && !isTransferSkill && _level > 1 && activeChar.getSkillLevel(_id) != _level - 1) 
		{
			_log.info("RequestAquireSkill[61]: "+"tried to increase skill " + _id + " level to " + _level + " while having it's level " + activeChar.getSkillLevel(_id));
            Util.handleIllegalPlayerAction(activeChar, "RequestAquireSkill[61]", "tried to increase skill " + _id + " level to " + _level + " while having it's level " + activeChar.getSkillLevel(_id), 1);
            return;
        }

        // TODO обязательно добавить проверку на изучение при isTransferSkill
        if (!(skill.isCommon() || isTransferSkill || SkillTreeTable.getInstance().isSkillPossible(activeChar, _id, _level) || _skillType <= AcquireSkillList.OTHER || ConfigValue.Multi_Enable3)) 
		{
			_log.info("RequestAquireSkill[69]: "+"tried to increase skill " + _id + " level to " + _level + " while having it's level " + activeChar.getSkillLevel(_id)+" isCommon="+skill.isCommon()+" isTransferSkill="+isTransferSkill+" isSkillPossible="+SkillTreeTable.getInstance().isSkillPossible(activeChar, _id, _level)+" OTHER="+(_skillType <= AcquireSkillList.OTHER));
            Util.handleIllegalPlayerAction(activeChar, "RequestAquireSkill[69]", "tried to learn skill " + _id + " while on class " + activeChar.getActiveClass(), 1);
            return;
        }

        L2SkillLearn SkillLearn = SkillTreeTable.getSkillLearn(_id, _level, activeChar.getClassId(), _skillType == AcquireSkillList.CLAN || _skillType == AcquireSkillList.CLAN_ADDITIONAL ? activeChar.getClan() : null, isTransferSkill, _skillType == AcquireSkillList.CLAN_ADDITIONAL ? true : false, _skillType <= AcquireSkillList.OTHER);

		if(SkillLearn == null)
		{
            activeChar.sendActionFailed();
            return;
        }

        int itemCount = SkillLearn.getItemCount();
        if (itemCount == -1) 
		{
			if(ConfigValue.Multi_Enable3)
				itemCount = 1;
			else
			{
				activeChar.sendActionFailed();
				return;
			}
        }
		if(!SkillLearn.canLearnSkill(activeChar))
		{
			activeChar.sendActionFailed();
			return;
		}
        if(_skillType == AcquireSkillList.CLAN)
            learnClanSkill(skill, activeChar.getClan());
        else if (_skillType == AcquireSkillList.TRANSFER) 
		{
            if (isTransferSkill) 
			{
                int item_id = 0;
                int maxSkillCount = 0;
                switch (activeChar.getClassId())
				{
                    case cardinal:
                        item_id = 15307;
                        maxSkillCount = 1;
                        break;
                    case evaSaint:
                        item_id = 15308;
                        maxSkillCount = 1;
                        break;
                    case shillienSaint:
                        item_id = 15309;
                        maxSkillCount = 4;
                        break;
                    default:
                        activeChar.sendMessage("There is no skills for your class.");
                        return;
                }
                String var = activeChar.getVar("TransferSkills" + item_id);
                if (var != null && var.split(";").length >= maxSkillCount) 
				{
                    activeChar.sendMessage("There is no skills for your class.");
                    return;
                }
                L2ItemInstance spb = activeChar.getInventory().getItemByItemId(item_id);
                if (spb == null || spb.getCount() < 1) 
				{
                    activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_SKILLS);
                    return;
                }

                L2ItemInstance ri = activeChar.getInventory().destroyItem(spb, 1, true);
                activeChar.sendPacket(SystemMessage.removeItems(ri.getItemId(), 1));
                if (var == null)
                    var = "";
                if (!var.isEmpty())
                    var += ";";
                var += skill.getId();
                activeChar.setVar("TransferSkills" + item_id, var);
            }
			SkillLearn.deleteSkills(activeChar);
            activeChar.addSkill(skill, true);
            activeChar.updateStats();
            activeChar.sendUserInfo(true);
        }
		else if(_skillType == AcquireSkillList.CLAN_ADDITIONAL)
            learnPledgeSkill(skill, activeChar.getClan());
		else if(_skillType == AcquireSkillList.AUTO_UP)
			AutolearnSimple(activeChar, SkillLearn, skill);
        else
		{
            int _requiredSp = SkillTreeTable.getInstance().getSkillCost(activeChar, skill);
			if(_skillType <= AcquireSkillList.OTHER || ConfigValue.Multi_Enable3)
				_requiredSp = SkillLearn.getSpCost();
			if(activeChar.getSp() >= _requiredSp || SkillLearn.common || SkillLearn.transformation || SkillLearn.certification)
			{
				if(_skillType == AcquireSkillList.CERTIFICATION && activeChar.isSubClassActive())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUB_CLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE ));
					return;
				}
				Integer spb_id = SkillSpellbookTable.getSkillSpellbooks().get(SkillSpellbookTable.hashCode(new int[]{skill.getId(), skill.getLevel()}));
				if(spb_id != null) 
				{
					L2ItemInstance spb = activeChar.getInventory().getItemByItemId(spb_id);
					if(spb == null || spb.getCount() < itemCount) 
					{
						activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_SKILLS);
						return;
					}
					if(!ConfigValue.AutolearnSimple || _skillType == -100)
					{
						L2ItemInstance ri = activeChar.getInventory().destroyItem(spb, itemCount, true);
						activeChar.sendPacket(SystemMessage.removeItems(ri.getItemId(), itemCount));
					}
				}
				if(!SkillLearn.common && !SkillLearn.transformation && !SkillLearn.certification && (!ConfigValue.AutolearnSimple || _skillType == -100))
					activeChar.setSp(activeChar.getSp() - _requiredSp);
				if(ConfigValue.AutolearnSimple && _skillType != -100)
				{
					for(int i = skill.getLevel();i <= skill.getBaseLevel();i++)
					{
						SkillLearn = SkillTreeTable.getSkillLearn(skill.getId(), i, activeChar.getClassId(), null, false, false, _skillType <= AcquireSkillList.OTHER);
						if(SkillLearn == null || SkillLearn.minLevel > activeChar.getLevel())
							break;
						else if(activeChar.getSp() < (long)SkillLearn.getSpCost() && !SkillLearn.common && !SkillLearn.transformation && !SkillLearn.certification)
						{
							activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_SKILLS);
							break;
						}
						if(!SkillLearn.canLearnSkill(activeChar))
						{
							activeChar.sendActionFailed();
							return;
						}
						spb_id = SkillSpellbookTable.getSkillSpellbooks().get(SkillSpellbookTable.hashCode(new int[]{skill.getId(), i}));
						if(spb_id != null)
						{
							L2ItemInstance spb = activeChar.getInventory().getItemByItemId(spb_id);
							if(spb == null || spb.getCount() < itemCount)
								break;
							L2ItemInstance ri = activeChar.getInventory().destroyItem(spb, itemCount, true);
							activeChar.sendPacket(SystemMessage.removeItems(ri.getItemId(), itemCount));
						}
						skill = SkillTable.getInstance().getInfo(skill.getId(), i);
						if(!SkillLearn.common && !SkillLearn.transformation && !SkillLearn.certification)
							activeChar.setSp(activeChar.getSp() - (long)SkillLearn.getSpCost());

						SkillLearn.deleteSkills(activeChar);
					}
					activeChar.sendPacket((new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_2)).addSkillName(skill.getId(), skill.getLevel()));
				}
				else
					SkillLearn.deleteSkills(activeChar);
				activeChar.addSkill(skill, true);
                activeChar.updateStats();
                activeChar.sendUserInfo(true);

                //update all the shortcuts to this skill
                if(_level > 1)
                    for(L2ShortCut sc : activeChar.getAllShortCuts())
                        if(sc.id == _id && sc.type == L2ShortCut.TYPE_SKILL) 
						{
                            L2ShortCut newsc = new L2ShortCut(sc.slot, sc.page, sc.type, sc.id, _level);
                            activeChar.sendPacket(new ShortCutRegister(newsc));
                            activeChar.registerShortCut(newsc);
                        }
            }
			else 
			{
				activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_SKILLS);
                return;
            }
        }

        if(SkillLearn != null && SkillLearn.common)
            activeChar.sendPacket(new ExStorageMaxCount(activeChar));
        activeChar.sendPacket(new SkillList(activeChar));

        if(trainer != null)
		{
            if(_skillType == AcquireSkillList.USUAL)
                trainer.showSkillList(activeChar);
            else if (_skillType == AcquireSkillList.FISHING)
                trainer.showFishingSkillList(activeChar);
            else if (_skillType == AcquireSkillList.CLAN)
                trainer.showClanSkillList(activeChar);
            else if (_skillType == AcquireSkillList.TRANSFORMATION)
                trainer.showTransformationSkillList(activeChar);
            else if (_skillType == AcquireSkillList.TRANSFER)
                trainer.showTransferSkillList(activeChar);
			else if (_skillType == AcquireSkillList.COLLECTION)
				trainer.showCollectionSkillList(activeChar);
			else if (_skillType == AcquireSkillList.CERTIFICATION)
				trainer.showCertificationSkillList(activeChar);
			else if(_skillType <= AcquireSkillList.OTHER)
				trainer.showOtherSkillList(activeChar, _skillType);
		}
		else if(_skillType <= AcquireSkillList.OTHER)
			SkillTreeTable.getInstance().showOtherSkillList(activeChar, _skillType);
		else if(activeChar.getLastBbsOperaion() != null && activeChar.getLastBbsOperaion().contains("learn"))
			DifferentMethods.communityNextPage(activeChar, activeChar.getLastBbsOperaion());
    }

	private static void AutolearnSimple(L2Player player, L2SkillLearn skillLearn, L2Skill skill)
	{
		int skillLevel = player.getSkillLevel(Integer.valueOf(skillLearn.getId()));
		if(skillLevel != skillLearn.getLevel() - 1)
			return;
		for(int i = skill.getLevel(); i <= skill.getBaseLevel(); i++)
		{
			skill = SkillTable.getInstance().getInfo(skill.getId(), i);
			if(player.getSp() < (long)skillLearn.getSpCost())
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_SKILLS);
				return;
			}
			skillLearn = SkillTreeTable.getSkillLearn(skill.getId(), i, player.getClassId(), null, false, false, true);
			if(skillLearn == null || skillLearn.minLevel > player.getLevel())
				break;
			else if(skillLearn.getItemId() > 0 && !player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount()))
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_SKILLS);
                return;
			}
			player.sendPacket((new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_2)).addSkillName(skill.getId(), skill.getLevel()));
			player.setSp(player.getSp() - (long)skillLearn.getSpCost());

			skillLearn.deleteSkills(player);
			player.addSkill(skill, true);
		}
		player.sendUserInfo(true);
		player.updateStats();
		player.sendPacket(new SkillList(player));
		if(skill.getLevel() > 1)
			for(L2ShortCut sc : player.getAllShortCuts())
				if(sc.id == skill.getId() && sc.type == L2ShortCut.TYPE_SKILL) 
				{
					L2ShortCut newsc = new L2ShortCut(sc.slot, sc.page, sc.type, sc.id, skill.getLevel());
					player.sendPacket(new ShortCutRegister(newsc));
					player.registerShortCut(newsc);
				}
	}

    private void learnClanSkill(L2Skill skill, L2Clan clan) 
	{
        L2Player player = getClient().getActiveChar();
        if(player == null || skill == null || clan == null)
            return;
        L2NpcInstance trainer = player.getLastNpc();
        if (trainer == null)
            return;
        if(!(trainer instanceof L2VillageMasterInstance))
		{
            _log.info("RequestAquireSkill.learnClanSkill, trainer isn't L2VillageMasterInstance");
            _log.info(trainer.getName() + "[" + trainer.getNpcId() + "] Loc: " + trainer.getLoc());
            return;
        }
        if(!player.isClanLeader()) 
		{
            player.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }
        L2SkillLearn SkillLearn = SkillTreeTable.getSkillLearn(_id, _level, null, clan, false);
		if(clan == null || clan.getLeader() == null || clan.getLeader().getPlayer() == null)
			 return;
        int requiredRep = SkillTreeTable.getInstance().getSkillRepCost(clan, skill);
        int itemId = 0;
        if(!ConfigValue.AltDisableSpellbooks)
            itemId = SkillLearn.itemId;
        if (skill.getMinPledgeClass() <= clan.getLevel() && clan.getReputationScore() >= requiredRep) 
		{
            if (itemId > 0) 
			{
                L2ItemInstance spb = player.getInventory().getItemByItemId(itemId);
                if (spb == null || spb.getCount() < SkillLearn.itemCount) 
				{
                    // Haven't spellbook
                    player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_SKILLS);
                    return;
                }
                L2ItemInstance ri = player.getInventory().destroyItem(spb, SkillLearn.itemCount, true);
                player.sendPacket(SystemMessage.removeItems(ri.getItemId(), SkillLearn.itemCount));
            }
            clan.incReputation(-requiredRep, false, "AquireSkill: " + _id + ", lvl " + _level);
            clan.addNewSkill(skill, true);
            player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addSkillName(_id, _level));

            ((L2VillageMasterInstance) trainer).showClanSkillWindow(player); //Maybe we shoud add a check here...
        } 
		else 
		{
            player.sendMessage("Your clan doesn't have enough reputation points to learn this skill");
            return;
        }

        //update all the shortcuts to this skill
        if (_level > 1)
            for (L2ShortCut sc : player.getAllShortCuts())
                if (sc.id == _id && sc.type == L2ShortCut.TYPE_SKILL) 
				{
                    L2ShortCut newsc = new L2ShortCut(sc.slot, sc.page, sc.type, sc.id, _level);
                    player.sendPacket(new ShortCutRegister(newsc));
                    player.registerShortCut(newsc);
                }
        clan.addAndShowSkillsToPlayer(player);
    }

    private void learnPledgeSkill(L2Skill skill, L2Clan clan) 
	{
        L2Player player = getClient().getActiveChar();
        if (player == null || skill == null || clan == null)
            return;
        if (player.getLastNpc() == null)
            return;
        if (skill.getId() < 611 || skill.getId() > 616) 
		{
            _log.warning("Warning! Player " + player.getName() + " tried to add a non-squad skill to one of his squads!");
            return;
        }
        L2SkillLearn pSkill = SkillTreeTable.getInstance().getSquadSkill(skill.getId(), skill.getLevel());
        if (player.getClan().getReputationScore() < pSkill.getRepCost()) 
		{
            player.sendPacket(Msg.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION_SCORE);
            return;
        }
        if (player.getInventory().getCountOf(pSkill.getItemId()) >= pSkill.getItemCount() && player.getInventory().destroyItemByItemId(pSkill.getItemId(), pSkill.getItemCount(), true) != null) 
		{
            player.sendPacket(SystemMessage.removeItems(pSkill.getItemId(), pSkill.getItemCount()));
            player.getClan().incReputation(-pSkill.getRepCost(), false, "SquadSkills");
            player.getClan().addNewSkill(skill, true, _pLevel);
			player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addSkillName(_id, _level));
        } 
		else
            player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
        clan.addAndShowSkillsToPlayer(player);
    }
}