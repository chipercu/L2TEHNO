import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.DelusionChamber;
import l2open.gameserver.model.entity.KamalokaNightmare;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Kamaloka extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Kamaloka Gate Loaded");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void Gatekeeper(String[] param)
	{
		if(param.length < 1)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		int instancedZoneId = Integer.parseInt(param[0]);
		boolean hwid_protect = instancedZoneId == 134 && ConfigValue.LabaHwidProtect;
		InstancedZoneManager izm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> izs = izm.getById(instancedZoneId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone iz = izs.get(0);
		assert iz != null;

		String name = iz.getName();
		int timelimit = iz.getTimelimit();
		boolean dispellBuffs = iz.isDispellBuffs();
		int min_level = iz.getMinLevel();
		int max_level = iz.getMaxLevel();
		int minParty = iz.getMinParty();
		int maxParty = iz.getMaxParty();

		if(!player.isInParty())
		{
			player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
			return;
		}

		if(player.getParty().isInReflection())
		{
			if(player.getLevel() < min_level || player.getLevel() > max_level)
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
				return;
			}
			if(player.isCursedWeaponEquipped())
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
				return;
			}
			Reflection old_ref = player.getParty().getReflection();
			if(old_ref != null && player.getActiveReflection() == old_ref)
			{
				if(!iz.equals(old_ref.getInstancedZone()))
				{
					player.sendMessage("Your party is in instanced zone already.");
					return;
				}
				if(!ConfigValue.KamalokaLimit.equalsIgnoreCase("Leader") && izm.getTimeToNextEnterInstance(name, player) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
					return;
				}
				if(player.getLevel() < min_level || player.getLevel() > max_level)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
					return;
				}
				else if(hwid_protect)
					for(L2Player pl : old_ref.getPlayers())
						if(pl != null && pl.getHWIDs().equals(player.getHWIDs()))
						{
							player.sendMessage("В инстанс можно попасть только с одного ПК.");
							return;
						}
				player.teleToLocation(old_ref.getTeleportLoc(), old_ref.getId());
				if(dispellBuffs)
				{
					for(L2Effect e : player.getEffectList().getAllEffects())
						if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
							e.exit(false, false);
					if(player.getPet() != null)
					{
						for(L2Effect e : player.getPet().getEffectList().getAllEffects())
							if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
								e.exit(false, false);
						player.getPet().updateEffectIcons();
					}
					player.updateEffectIcons();
				}
				return;
			}
		}

		if(!player.getParty().isLeader(player))
		{
			player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
			return;
		}

		if(player.getParty().getMemberCount() > maxParty)
		{
			player.sendPacket(Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
			return;
		}
		if(player.getParty().getMemberCount() < minParty)
		{
			player.sendPacket(new SystemMessage("The party must contains at least " + minParty + " members."));
			return;
		}

		List<String> _hwid = new ArrayList<String>(player.getParty() == null ? 0 : player.getParty().getMemberCount());

		int count = 0;
		for(L2Player member : player.getParty().getPartyMembers())
		{
			if(member.getLevel() < min_level || member.getLevel() > max_level)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
				member.sendPacket(sm);
				player.sendPacket(sm);
				return;
			}
			if(!player.isInRange(member, 500))
			{
				member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
				player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
				return;
			}
			if(hwid_protect)
			{
				if(_hwid.contains(member.getHWIDs()))
				{
					player.sendMessage("В инстанс можно попасть только с одного ПК. Игрок '"+member.getName()+"' пытается завести более одного окна.");
					continue;
				}
				_hwid.add(member.getHWIDs());
			}
			count++;
		}

		if(hwid_protect && count < 2)
			return;

		if(ConfigValue.KamalokaLimit.equalsIgnoreCase("Leader"))
		{
			if(izm.getTimeToNextEnterInstance(name, player) > 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
				return;
			}
		}
		else
			for(L2Player member : player.getParty().getPartyMembers())
				if(izm.getTimeToNextEnterInstance(name, member) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
					return;
				}
		
		if(instancedZoneId == 3)
		{
			if(player.getInventory().getCountOf(15515) > 0)
			{
				Functions.removeItem(player, 15515, 1);
			}
			else
			{
				player.sendMessage(player.getLang().equals("ru") ? "У вас недостаточно предметов чтобы зайти." : "You don't have item for enter.");
				return;
			}
		}

		if(instancedZoneId == 4)
		{
			if(player.getInventory().getCountOf(15516) > 0)
			{
				Functions.removeItem(player, 15516, 1);
			}
			else
			{
				player.sendMessage(player.getLang().equals("ru") ? "У вас недостаточно предметов чтобы зайти." : "You don't have item for enter.");
				return;
			}
		}
		

		Reflection r = new Reflection(iz);
		r.setInstancedZoneId(instancedZoneId);

		for(InstancedZone i : izs.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
			r.FillDoors(i.getDoors());
		}

		_hwid.clear();
		if(minParty <= 1) // для соло инстансов без босса флаг ставится при входе
			player.setVarInst(name, String.valueOf(System.currentTimeMillis()));
		for(L2Player member : player.getParty().getPartyMembers())
		{
			if(hwid_protect)
			{
				if(_hwid.contains(member.getHWIDs()))
					continue;
				_hwid.add(member.getHWIDs());
			}
			if(dispellBuffs)
			{
				for(L2Effect e : member.getEffectList().getAllEffects())
					if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
						e.exit(false, false);
				if(member.getPet() != null)
				{
					for(L2Effect e : member.getPet().getEffectList().getAllEffects())
						if(!e.getSkill().isOffensive() && !e.getSkill().isNewbie() && e.getSkill().isCancelable())
							e.exit(false, false);
					member.getPet().updateEffectIcons();
				}
				member.updateEffectIcons();
			}

			member.setVar("backCoords", r.getReturnLoc().toXYZString());
			member.teleToLocation(iz.getTeleportCoords(), r.getId());
		}

		player.getParty().setReflection(r);
		r.setParty(player.getParty());
		if(timelimit > 0)
		{
			r.startCollapseTimer(timelimit * 60 * 1000L);
			player.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
		}
	}

	public void SoloGatekeeper(String[] param)
	{
		int _instanceId = 0;
		if(param.length < 1)
			throw new IllegalArgumentException();

		
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		if(player.isInParty())
		{
			player.sendPacket(Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
			return;
		}

		if(param[0].equals("-1"))
		{
			KamalokaNightmare r = ReflectionTable.getInstance().findSoloKamaloka(player.getObjectId());
			if(r != null)
			{
				player.setVar("backCoords", r.getReturnLoc().toXYZString());
				player.teleToLocation(r.getTeleportLoc(), r.getId());
				return;
			}
			else
			{
				player.sendPacket(Msg.SYSTEM_ERROR);
				return;
			}
		}
		
		switch(Integer.parseInt(param[0]))
		{
			case 25:
				_instanceId = 46;
				break;
			case 30:
				_instanceId = 47;
				break;
			case 35:
				_instanceId = 48;
				break;
			case 40:
				_instanceId = 49;
				break;
			case 45:
				_instanceId = 50;
				break;
			case 50:
				_instanceId = 51;
				break;
			case 55:
				_instanceId = 52;
				break;
			case 60:
				_instanceId = 53;
				break;
			case 65:
				_instanceId = 54;
				break;
			case 70:
				_instanceId = 55;
				break;
			case 75:
			case 80:
			case 85:
				_instanceId = 56;
				break;
		}

		if(ConfigValue.KamalokaNightmaresPremiumOnly && player.getBonus().RATE_XP <= 1)
		{
			player.sendMessage(new CustomMessage("common.PremiumOnly", player));
			return;
		}

		InstancedZoneManager izm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(_instanceId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone iz = izs.get(0);
		assert iz != null;

		String name = iz.getName();
		int timelimit = iz.getTimelimit();
		int min_level = iz.getMinLevel();
		int max_level = iz.getMaxLevel();

		if(player.getLevel() < min_level || player.getLevel() > max_level)
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}
		
		KamalokaNightmare r = ReflectionTable.getInstance().findSoloKamaloka(player.getObjectId());
		if(r != null)
		{
			player.setVar("backCoords", r.getReturnLoc().toXYZString());
			player.teleToLocation(r.getTeleportLoc(), r.getId());
			return;
		}

		if(izm.getTimeToNextEnterInstance(name, player) > 0)
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
			return;
		}

		r = new KamalokaNightmare(player);
		r.setInstancedZoneId(_instanceId);

		for(InstancedZone i : izs.values())
		{
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
			r.FillDoors(i.getDoors());
		}

		player.setVarInst(name, String.valueOf(System.currentTimeMillis()));
		r.setReturnLoc(player.getLoc());
		player.setVar("backCoords", r.getReturnLoc().toXYZString());
		player.teleToLocation(r.getTeleportLoc(), r.getId());
		ReflectionTable.getInstance().addSoloKamaloka(player.getObjectId(), r);
		if(timelimit > 0)
		{
			r.startCollapseTimer(timelimit * 60 * 1000L);
			player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
		}
	}

	public void StaticSoloInstance(String[] param)
	{
		if(param.length < 1)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null || player.isDead())
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		int instancedZoneId = Integer.parseInt(param[0]);

		// SoD
		if(instancedZoneId == 400 && ServerVariables.getLong("SoD_opened", 0) * 1000L + ConfigValue.SeedofDestructionOpenTime * 60 * 60 * 1000L < System.currentTimeMillis())
		{
			TiatEnter();
			return;
		}

		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(instancedZoneId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone iz = izs.get(0);
		assert iz != null;

		if(player.getLevel() < iz.getMinLevel() || player.getLevel() > iz.getMaxLevel() || player.isInFlyingTransform())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		Reflection r = ReflectionTable.SOD_REFLECTION_ID == 0 ? null : ReflectionTable.getInstance().get(ReflectionTable.SOD_REFLECTION_ID);
		if(ReflectionTable.SOD_REFLECTION_ID > 0 && r != null)
		{
			player.setVar("backCoords", r.getReturnLoc().toXYZString());
			player.teleToLocation(r.getTeleportLoc(), r.getId());
			return;
		}
		else
		{
			r = new Reflection(iz.getName());
			r.setInstancedZoneId(instancedZoneId);
			ReflectionTable.SOD_REFLECTION_ID = r.getId();
		}

		long timelimit = 0;
		if(instancedZoneId == 400)
			timelimit = ServerVariables.getLong("SoD_opened", 0) * 1000L + ConfigValue.SeedofDestructionOpenTime * 60 * 60 * 1000L - System.currentTimeMillis();

		for(InstancedZone i : izs.values())
		{
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
			r.FillDoors(i.getDoors());
		}

		r.setCoreLoc(r.getReturnLoc());
		r.setReturnLoc(player.getLoc());
		player.setVar("backCoords", r.getReturnLoc().toXYZString());
		player.teleToLocation(r.getTeleportLoc(), r.getId());
		if(timelimit > 0)
			r.startCollapseTimer(timelimit);
	}

	public void TiatEnter()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		int instancedZoneId = 110;

		if((player.getParty() == null || !player.getParty().isInCommandChannel() && !ConfigValue.TiatDisableCommandChanel) && !player.isGM())
		{
			player.sendPacket(Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL);
			return;
		}
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		InstancedZoneManager izm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(instancedZoneId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone iz = izs.get(0);
		assert iz != null;

		String name = iz.getName();
		int timelimit = iz.getTimelimit();
        boolean dispellBuffs = iz.isDispellBuffs();
		int minMembers = iz.getMinParty();
		int maxMembers = iz.getMaxParty();


        if(player.getParty() != null && player.getParty().isInReflection())
		{
            Reflection old_ref = player.getParty().getReflection();
            if (old_ref.getInstancedZoneId() != instancedZoneId || player.getActiveReflection() != old_ref)
			{
                player.sendMessage("Неправильно выбран инстанс");
                return;
            }

            if(player.getLevel() < iz.getMinLevel() || player.getLevel() > iz.getMaxLevel())
			{
                player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
                return;
            }
            if (player.isCursedWeaponEquipped() || player.isInFlyingTransform() || player.isDead())
			{
                player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
                return;
            }
            if (izm.getTimeToNextEnterInstance(name, player) > 0)
			{
                player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
                return;
            }
            if(dispellBuffs)
			{
				for(L2Effect e : player.getEffectList().getAllEffects())
					if(!e.getSkill().isOffensive() && !e.getSkill().getName().startsWith("Adventurer's "))
						e.exit(false, false);
				if(player.getPet() != null)
				{
					for(L2Effect e : player.getPet().getEffectList().getAllEffects())
						if(!e.getSkill().isOffensive() && !e.getSkill().getName().startsWith("Adventurer's "))
							e.exit(false, false);
					player.getPet().updateEffectIcons();
				}
				player.updateEffectIcons();
			}
            player.setReflection(old_ref);
            player.teleToLocation(iz.getTeleportCoords(), old_ref.getId());
            return;
        }
		L2CommandChannel cc = player.getParty() == null ? null : player.getParty().getCommandChannel();
		if(cc != null)
		{
			if(cc.getChannelLeader() != player)
			{
				player.sendMessage("You must be leader of the command channel.");
				return;
			}
			else if(cc.getMemberCount() < minMembers)
			{
				player.sendMessage("The command channel must contains at least " + minMembers + " members.");
				return;
			}
			else if(cc.getMemberCount() > maxMembers)
			{
				player.sendMessage("The command channel must contains not more than " + maxMembers + " members.");
				return;
			}

			for(L2Player member : cc.getMembers())
			{
				if(member.getLevel() < iz.getMinLevel() || member.getLevel() > iz.getMaxLevel())
				{
					cc.broadcastToChannelMembers(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if(member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if(!player.isInRange(member, 500))
				{
					member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					return;
				}
				if(izm.getTimeToNextEnterInstance(name, member) > 0)
				{
					cc.broadcastToChannelMembers(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
					return;
				}
			}
		}
		else
		{
			if(!player.getParty().isLeader(player))
			{
				player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
				return;
			}
			else if(player.getParty().getMemberCount() < minMembers)
			{
				player.sendMessage("The party must contains at least " + minMembers + " members.");
				return;
			}
			else if(player.getParty().getMemberCount() > maxMembers)
			{
				player.sendMessage("The party must contains not more than " + maxMembers + " members.");
				return;
			}

			for(L2Player member : player.getParty().getPartyMembers())
			{
				if(member.getLevel() < iz.getMinLevel() || member.getLevel() > iz.getMaxLevel())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if(member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if(!player.isInRange(member, 500))
				{
					member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
					return;
				}
				if(izm.getTimeToNextEnterInstance(name, member) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
					return;
				}
			}
		}

		Reflection r = new Reflection(name);
		r.setInstancedZoneId(instancedZoneId);

		for(InstancedZone i : izs.values())
		{
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
			r.FillDoors(i.getDoors());
		}

		r.setCoreLoc(r.getReturnLoc());
		r.setReturnLoc(player.getLoc());

		if(cc != null)
		{
			for(L2Player member : cc.getMembers())
			{
				//member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
				member.setVar("backCoords", r.getReturnLoc().toXYZString());
				member.teleToLocation(iz.getTeleportCoords(), r.getId());
			}

			cc.setReflection(r);
			r.setCommandChannel(cc);
		}
		else if(player.getParty() != null)
		{
			for(L2Player member : player.getParty().getPartyMembers())
			{
				//member.setVarInst(name, String.valueOf(System.currentTimeMillis()));
				member.setVar("backCoords", r.getReturnLoc().toXYZString());
				member.teleToLocation(iz.getTeleportCoords(), r.getId());
			}
			
			player.getParty().setReflection(r);
			r.setParty(player.getParty());
		}
		else
		{
			//player.setVarInst(name, String.valueOf(System.currentTimeMillis()));
			player.setVar("backCoords", r.getReturnLoc().toXYZString());
			player.teleToLocation(iz.getTeleportCoords(), r.getId());
		}

		if(timelimit > 0)
			r.startCollapseTimer(timelimit * 60 * 1000L);
	}

	public void LeaveKamaloka()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(player.getParty() == null || !player.getParty().isLeader(player))
		{
			show("You are not a party leader.", player, npc);
			return;
		}

		player.getReflection().collapse();
	}

	public String DialogAppend_32484(Integer val)
	{
		L2Player player = (L2Player) getSelf();
		String ret = "";
		if(player == null || player.getLevel() < 20)
			return ret;
		if(ConfigValue.SellReenterNightmaresTicket || ConfigValue.SellReenterAbyssTicket || ConfigValue.SellReenterLabyrinthTicket)
		{
			ret += "<br>Ticket price: " + Util.formatAdena(player.getLevel() * 5000) + " adena.";
			if(ConfigValue.SellReenterNightmaresTicket)
				ret += "<br>[scripts_Kamaloka:buyTicket 1|Buy ticket to Hall of Nightmates]";
			if(ConfigValue.SellReenterAbyssTicket)
				ret += "<br>[scripts_Kamaloka:buyTicket 2|Buy ticket to Hall of Abyss]";
			if(ConfigValue.SellReenterLabyrinthTicket)
				ret += "<br>[scripts_Kamaloka:buyTicket 3|Buy ticket to Labirinth of Abyss]";
		}
		return ret;
	}

	public void buyTicket(String[] id)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || player.getLevel() < 20)
			return;
		int price = player.getLevel() * 5000;
		if(Functions.getItemCount(player, 57) < price)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		Functions.removeItem(player, 57, price);
		if(id[0].equals("1"))
			Functions.addItem(player, 13011, 1);
		else if(id[0].equals("2"))
			Functions.addItem(player, 13010, 1);
		else if(id[0].equals("3"))
			Functions.addItem(player, 13012, 1);
		else
		{
			_log.warning("!!!!!!!!!!!!LOH DETECTED!!!!!!!!!!!! LOH NAME: "+player.getName());
			// TODO: вывод байпаса.
		}
	}

	public void toDC()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.setVar("DCBackCoords", player.getLoc().toXYZString());
		player.teleToLocation(-114582, -152635, -6742);
	}

	public void fromDC()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		String var = player.getVar("DCBackCoords");
		if(var == null || var.equals(""))
			return;
		player.teleToLocation(new Location(var), 0);
		player.unsetVar("DCBackCoords");
	}

	public void enterDC(String[] param)
	{
		if(param.length < 1)
			throw new IllegalArgumentException();

		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		int izId = Integer.parseInt(param[0]);
		int type = izId - 120;
		
		FastMap<Integer, DimensionalRiftRoom> rooms = DimensionalRiftManager.getInstance().getRooms(type);

		if(rooms == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}
		int min_level = 80;

		if(!player.isInParty())
		{
			player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
			return;
		}

		if(player.getParty().isInReflection())
		{
			if(player.getLevel() < min_level)
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
				return;
			}
			if(player.isCursedWeaponEquipped())
			{
				player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
				return;
			}
			Reflection old_ref = player.getParty().getReflection();
			if(old_ref != null && old_ref instanceof DelusionChamber && player.getActiveReflection() == old_ref)
			{
				if((type == 11 || type == 12) && InstancedZoneManager.getInstance().getTimeToNextEnterInstance(izId, player) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
					return;
				}
				player.teleToLocation(old_ref.getTeleportLoc(), old_ref.getId());
				return;
			}
		}

		if(!player.getParty().isLeader(player))
		{
			player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
			return;
		}

		if(type == 11 || type == 12)
			for(L2Player member : player.getParty().getPartyMembers())
				if(InstancedZoneManager.getInstance().getTimeToNextEnterInstance(izId, member) > 0)
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
					return;
				}

		for(L2Player member : player.getParty().getPartyMembers())
		{
			if(member.getLevel() < min_level)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member);
				member.sendPacket(sm);
				player.sendPacket(sm);
				return;
			}
			if(!player.isInRange(member, 500))
			{
				member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
				player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
				return;
			}
		}

		new DelusionChamber(player.getParty(), type, Rnd.get(1, rooms.size() - 1));
	}
}