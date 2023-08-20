package commands.voiced;

import java.util.*;
import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;
import l2open.extensions.multilang.CustomMessage;
/**
 * @author: Diagod
 * open-team.ru
 **/
public class BuffStore extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "buff_store" };

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.startsWith("buff_store"))
		{
			if(!ConfigValue.BuffStoreEnable && !activeChar.isGM())
			{
				activeChar.sendMessage("Сервис выключен.");
				return false;
			}
			else if(activeChar.getOlympiadObserveId() != -1 || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.getKarma() > 0)
			{
				activeChar.sendActionFailed();
				return false;
			}
			else if(activeChar.getLevel() < ConfigValue.BuffStoreMinLevel)
			{
				show(new CustomMessage("scripts.commands.user.offline_b.LowLevel", activeChar).addNumber(ConfigValue.BuffStoreMinLevel), activeChar);
				return false;
			}
			else if(ConfigValue.BuffStoreOnlyPice && !activeChar.isInPeaceZone())
			{
				//activeChar.sendPacket(Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_IN_THIS_AREA);
				return false;
			}
			else if(activeChar.isCastingNow())
			{
				//activeChar.sendPacket(Msg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
				return false;
			}
			else if(activeChar.isInCombat())
			{
				//activeChar.sendPacket(Msg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
				return false;
			}
			else if(activeChar.isOutOfControl() || activeChar.isActionsDisabled() || activeChar.isMounted() || activeChar.isInOlympiadMode() || activeChar.getDuel() != null)
				return false;
			else if(activeChar.getNoChannelRemained() > 0)
			{
				show(new CustomMessage("scripts.commands.user.offline.BanChat", activeChar), activeChar);
				return false;
			}
			else if(activeChar.isActionBlocked(L2Zone.BLOCKED_ACTION_PRIVATE_STORE))
			{
				activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZone", activeChar));
				return false;
			}
			else if(activeChar.isInZone(L2Zone.ZoneType.Siege) || activeChar.isCursedWeaponEquipped() || activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped())
			{
				activeChar.sendActionFailed();
				return false;
			}
			else if(activeChar.getVar("jailed") != null)
				return false;
			else if(ConfigValue.BuffStoreOnlyFar)
			{
				boolean tradenear = false;
				for(L2Player player : L2World.getAroundPlayers(activeChar, ConfigValue.BuffStoreRadius, 200))
					if(player.isInStoreMode())
					{
						tradenear = true;
						break;
					}

				if(L2World.getAroundNpc(activeChar, ConfigValue.BuffStoreRadius + 100, 200).size() > 0)
					tradenear = true;

				if(tradenear)
				{
					activeChar.sendMessage(new CustomMessage("trade.OtherTradersNear", activeChar));
					return false;
				}
			}
			String[] arg = args.trim().split(":");
			if(arg.length != 2)
			{
				activeChar.sendMessage(new CustomMessage("scripts.services.Activation.InvalidArguments", activeChar));
				show(Files.read("data/scripts/services/buff_store.htm", activeChar), activeChar);
				return true;
			}
			
			List<L2Skill> skill_list = new ArrayList<L2Skill>();
			for(L2Skill skill : activeChar.getAllSkills())
				if(skill.getSkillType() == SkillType.BUFF && skill.getTargetType() != SkillTargetType.TARGET_SELF && skill.hasEffects() && !Util.contains(ConfigValue.BuffStoreNoSkill, skill.getId()) && (!ConfigValue.BuffStoreCheckCondSkill || skill.checkCondition(activeChar, activeChar, true, true, false)))
					skill_list.add(skill);
			if(skill_list.size() == 0)
			{
				activeChar.sendMessage("Отсутствуют умения которые можно продать.");
				activeChar.sendActionFailed();
				return false;
			}
			
			
			String price = arg[0].trim();
			String title = arg[1].trim();

			activeChar.setVar("buf_price", price);
			activeChar.setVar("buf_title", title);
			activeChar._buf_title = title;
			activeChar.setPrivateStoreType(L2Player.STORE_PRIVATE_BUFF);
			activeChar.broadcastUserInfo(true);
			activeChar.sitDown(false);
		}
		return false;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}