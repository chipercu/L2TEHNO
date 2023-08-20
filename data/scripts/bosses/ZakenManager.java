package bosses;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExSendUIEvent;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncMul;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : Drizzy and Ragnarok
 * @date : 15.01.11
 */

public class ZakenManager extends Functions implements ScriptFile 
{
    private static FastMap<Integer, List<Integer>> membersCC = new FastMap<Integer, List<Integer>>();
    private static FastMap<Integer, List<Integer>> membersParty = new FastMap<Integer, List<Integer>>();

    public static HashMap<Integer, ZakenInstanceInfo> instances;
    // Во избежание ошибок Z координаты закенов и Z координаты бочек на этаже НЕ должны отличаться
    public static final int[][] ROOM_CENTER_COORDS = {
            // комнаты 1-го этажа
            {54248, 220120, -3522, 0},
            {56280, 220120, -3522, 0},
            {55272, 219096, -3522, 0},
            {54232, 218072, -3522, 0},
            {56296, 218072, -3522, 0},
            // комнаты 2-го этажа
            {56280, 218072, -3250, 0},
            {54232, 218072, -3250, 0},
            {55256, 219112, -3250, 0},
            {56296, 220120, -3250, 0},
            {54232, 220136, -3250, 0},
            // комнаты 3-го этажа
            {56296, 218072, -2978, 0},
            {54232, 218072, -2978, 0},
            {55272, 219112, -2978, 0},
            {56280, 220120, -2978, 0},
            {54232, 220120, -2978, 0}
    };

    public final class ZakenInstanceInfo 
	{
        int zakenId;
        Location zakenLoc;
        List<Integer> blueKandles;
        List<Integer> redKandles;
		long timer;

        public ZakenInstanceInfo(int zakenId, Location zakenLoc, long timer)
		{
            this.zakenId = zakenId;
            this.zakenLoc = zakenLoc;
            blueKandles = new ArrayList<Integer>();
            redKandles = new ArrayList<Integer>();
			this.timer = timer;
        }

        public Location getZakenLoc() 
		{
            return zakenLoc;
        }

        public List<Integer> getBlueKandles() 
		{
            return blueKandles;
        }

        public List<Integer> getRedKandles() 
		{
            return redKandles;
        }

        public int getZakenId() 
		{
            return zakenId;
        }

		public long getTimer()
		{
			return timer;
		}
    }

	@SuppressWarnings( { "fallthrough" })
    public void enterInstance(String[] strings) 
	{
        int zakenId=-1;
        int instancedZoneId=-1;
        L2Player player = (L2Player) getSelf();
        L2Party party = player.getParty();

        if(strings[0].equalsIgnoreCase("Night")/* && GameTimeController.getInstance().isNowNight()*/) 
		{
            instancedZoneId = 114;
            zakenId = 29022;
        } 
		else if(strings[0].equalsIgnoreCase("Day")/* && !GameTimeController.getInstance().isNowNight()*/) 
		{
            instancedZoneId = 133;
            zakenId = 29176;
        } 
		else if(strings[0].equalsIgnoreCase("DayHigh")/* && !GameTimeController.getInstance().isNowNight()*/) 
		{
            instancedZoneId = 135;
            zakenId = 29181;
        } 

		if(ConfigValue.ZakenOneEnter && instances != null && instances.size() > 1)
		{
			player.sendMessage("Рейд Босс Закен уже занят.");
			return;
		}
        InstancedZoneManager izm = InstancedZoneManager.getInstance();
        FastMap<Integer, InstancedZoneManager.InstancedZone> izs = InstancedZoneManager.getInstance().getById(instancedZoneId);
        if (izs == null) 
		{
            player.sendPacket(Msg.SYSTEM_ERROR);
            return;
        }
        InstancedZoneManager.InstancedZone iz = izs.get(0);
        if (iz == null) 
		{
            player.sendPacket(Msg.SYSTEM_ERROR);
            return;
        }
        String name = iz.getName();
        int timelimit = iz.getTimelimit();
        int minMembers = iz.getMinParty();
        int maxMembers = iz.getMaxParty();
        int min_level = iz.getMinLevel();
        int max_level = iz.getMaxLevel();
		boolean cancel = iz.isDispellBuffs();

        //check party
        if (party == null) 
		{
            player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
            return;
        }

        // Если игрок тпанулся из инста(смерть, сое), возвращаем его в инстанс
        if(player.getParty().isInReflection()) 
		{
            Reflection old_ref = player.getParty().getReflection();
            if (old_ref.getInstancedZoneId() != instancedZoneId) 
			{
                player.sendMessage("Неправильно выбран инстанс");
                return;
            }
            else if (player.getLevel() < min_level || player.getLevel() > max_level) 
			{
                player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
                return;
            }
            else if (player.isCursedWeaponEquipped() || player.isInFlyingTransform() || player.isDead()) 
			{
                player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
                return;
            }
			else if (izm.getTimeToNextEnterInstance(name, player) > 0) 
			{
                player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
                return;
            }
			else if(ConfigValue.ZakenHwidProtect)
				for(L2Player pl : old_ref.getPlayers())
					if(pl != null && pl.getHWIDs().equals(player.getHWIDs()))
					{
						player.sendMessage("В инстанс можно попасть только с одного ПК.");
						return;
					}
			if(instancedZoneId == 133 || instancedZoneId == 135)
			{
				List<Integer> _member_list = membersParty.get(old_ref.getId());

				if(_member_list != null)
				{
					_member_list.add(player.getObjectId());
					membersParty.put(old_ref.getId(), _member_list);
				}
			}
			else
			{
				List<Integer> _member_list = membersCC.get(old_ref.getId());

				if(_member_list != null)
				{
					_member_list.add(player.getObjectId());
					membersCC.put(old_ref.getId(), _member_list);
				}
			}
            player.setReflection(old_ref);
            player.teleToLocation(iz.getTeleportCoords(), old_ref.getId());
            return;
        }

        //иначе заходит новая пати, проверяем условия, создаем инстанс
        //check party leader
        switch (instancedZoneId) 
		{
            case 133:
            case 135:
                if(party.getCommandChannel() == null)
				{
                    if(!party.isLeader(player)) 
					{
                        player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
                        return;
                    }
                    //check min count member for party
					else if(party.getMemberCount() < minMembers) 
					{
                        player.sendMessage("The party must contains at least " + minMembers + " members.");
                        return;
                    }

					List<Integer> p_list = new ArrayList<Integer>();

					for(List<Integer> player_list : getMembersParty().values())
						for(Integer m : player_list)
							if(m != null)
								p_list.add(m);

					List<String> _hwid = new ArrayList<String>(party.getMemberCount());
					int count = 0;
                    for(L2Player member : party.getPartyMembers())
					{
                        if(member.getLevel() < min_level || member.getLevel() > max_level) 
						{
                            player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                            return;
                        }
						else if(member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead()) 
						{
                            player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                            return;
                        }
						else if(!player.isInRange(member, 500))
						{
                            member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                            player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                            return;
                        }
						else if(izm.getTimeToNextEnterInstance(name, member) > 0 || p_list.contains(member.getObjectId())) 
						{
                            player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
                            return;
                        }
						if(ConfigValue.ZakenHwidProtect)
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
					//check min count member for party
					if(ConfigValue.ZakenHwidProtect && count < minMembers) 
					{
                        player.sendMessage("The party must contains at least " + minMembers + " members.");
                        return;
                    }
					p_list.clear();
					break;
                }
            case 114:
                if(!player.getParty().isInCommandChannel()) 
				{
                    player.sendPacket(Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL);
                    return;
                }
                L2CommandChannel cc = player.getParty().getCommandChannel();
                //check cc leader
				if(cc.getChannelLeader() != player) 
				{
                    player.sendMessage("You must be leader of the command channel.");
                    return;
                }
                //check min-max member count for CC
				else if(cc.getMemberCount() < minMembers) 
				{
                    player.sendMessage("The command channel must contains at least " + minMembers + " members.");
                    return;
                }

				List<String> _hwid = new ArrayList<String>(cc.getMemberCount());
				int count = 0;
				List<Integer> p_list = new ArrayList<Integer>();

				for(List<Integer> player_list : getMembersCC().values())
					for(Integer m : player_list)
						if(m != null)
							p_list.add(m);

                for (L2Player member : cc.getMembers()) 
				{
                    if(member.getLevel() < min_level || member.getLevel() > max_level) 
					{
                        player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                        return;
                    }
					else if(member.isCursedWeaponEquipped() || member.isInFlyingTransform() || member.isDead()) 
					{
                        player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
                        return;
                    }
					else if(!player.isInRange(member, 500)) 
					{
                        member.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                        player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK);
                        return;
                    }
					else if(izm.getTimeToNextEnterInstance(name, member) > 0 || p_list.contains(member.getObjectId())) 
					{
                        cc.broadcastToChannelMembers(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(member));
                        return;
                    }
					if(ConfigValue.ZakenHwidProtect)
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
				if(ConfigValue.ZakenHwidProtect && count < minMembers) 
				{
                    player.sendMessage("The command channel must contains at least " + minMembers + " members.");
                    return;
                }
				else if(count > maxMembers) 
				{
                    player.sendMessage("The command channel must contains not more than " + maxMembers + " members.");
                    return;
                }
				p_list.clear();
                break;
        }

		final Reflection r = new ZakenReflection(name);
        r.setInstancedZoneId(instancedZoneId);
        for(InstancedZoneManager.InstancedZone i : izs.values()) 
		{
            if(r.getTeleportLoc() == null) 
                r.setTeleportLoc(i.getTeleportCoords());
            r.FillSpawns(i.getSpawnsInfo());
            r.FillDoors(i.getDoors());
        }
        r.setCoreLoc(r.getReturnLoc());
        r.setReturnLoc(player.getLoc());
        if(player.getParty().isInCommandChannel()) 
		{
            L2CommandChannel cc = player.getParty().getCommandChannel();
			List<String> _hwid = new ArrayList<String>(cc.getMemberCount());
            for(L2Player member : cc.getMembers()) 
			{
				if(ConfigValue.ZakenHwidProtect)
				{
					if(_hwid.contains(member.getHWIDs()))
						continue;
					_hwid.add(member.getHWIDs());
				}
                member.setVar("backCoords", r.getReturnLoc().toXYZString());
				if(cancel)
				{
					dispellBuffs(member);
					if(member.getPet() != null)
						dispellBuffs(member.getPet());
				}
                member.teleToLocation(iz.getTeleportCoords(), r.getId());
                member.sendPacket(new ExSendUIEvent(member, false, true, 0, timelimit * 60 * 1000, ""));
            }
            cc.setReflection(r);
            r.setCommandChannel(cc);

			List<Integer> _member_list = new ArrayList<Integer>(cc.getMembers().size());
			for(L2Player pl : cc.getMembers())
				_member_list.add(pl.getObjectId());

			membersCC.put(cc.getReflection().getId(), _member_list);
        }
		else 
		{
			List<String> _hwid = new ArrayList<String>(player.getParty().getMemberCount());
            for(L2Player member : player.getParty().getPartyMembers()) 
			{
				if(ConfigValue.ZakenHwidProtect)
				{
					if(_hwid.contains(member.getHWIDs()))
						continue;
					_hwid.add(member.getHWIDs());
				}
                member.setVar("backCoords", r.getReturnLoc().toXYZString());
                member.teleToLocation(iz.getTeleportCoords(), r.getId());
                member.sendPacket(new ExSendUIEvent(member, false, true, 0, timelimit * 60 * 1000, ""));
            }
            player.getParty().setReflection(r);
            r.setParty(player.getParty());
			
			List<Integer> _member_list = new ArrayList<Integer>(player.getParty().getPartyMembers().size());
			for(L2Player pl : player.getParty().getPartyMembers())
				_member_list.add(pl.getObjectId());
			
			membersParty.put(player.getParty().getReflection().getId(), _member_list);
        }
        if(instances == null)
            instances = new HashMap<Integer, ZakenInstanceInfo>();
        int[] coords = ROOM_CENTER_COORDS[Rnd.get(ROOM_CENTER_COORDS.length)];
        instances.put(r.getId(), new ZakenInstanceInfo(zakenId, new Location(coords), System.currentTimeMillis()));
        if(timelimit > 0) 
		{
            r.startCollapseTimer(timelimit * 60 * 1000);
            if(player.getParty().isInCommandChannel())
                player.getParty().getCommandChannel().broadcastToChannelMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
            else
                player.getParty().broadcastToPartyMembers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
			ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					ZakenManager.instances.remove(r.getId());
				}
			}, timelimit * 60000+30000);
        }
    }

    /**
     * Подсчитывает статы в зависимости от горящих красных свечек
     * Увеличиваем m\pDef, m\pAtk закена
     */
    public static void calcZakenStat(L2NpcInstance zaken, int rId) 
	{
        ZakenInstanceInfo instanceInfo = instances.get(rId);
        double count = instanceInfo.getRedKandles().size();
        double stat = Math.max(0, 1 + instanceInfo.getRedKandles().size() / 40);
        //zaken.removeStatsOwner(zaken);
        if (count > 0) 
		{
            zaken.addStatFunc(new FuncMul(Stats.p_physical_defence, 0x30, zaken, stat));
            zaken.addStatFunc(new FuncMul(Stats.p_magical_defence, 0x30, zaken, stat));
            zaken.addStatFunc(new FuncMul(Stats.p_physical_attack, 0x30, zaken, stat));
            zaken.addStatFunc(new FuncMul(Stats.p_magical_attack, 0x30, zaken, stat));
        }
    }

	private void dispellBuffs(L2Playable playable)
	{
		if(playable != null)
		{
			for(L2Effect effect : playable.getEffectList().getAllEffects())
			{
				if(effect.getEffectType() == EffectType.Vitality)
					continue;
				if(!effect.getSkill().isOffensive() && !effect.getSkill().getName().startsWith("Adventurer's "))
					effect.exit(false, false);
				playable.updateEffectIcons();
			}
		}
	}

    public static FastMap<Integer, List<Integer>> getMembersParty()
    {
        return membersParty;
    }

    public static FastMap<Integer, List<Integer>> getMembersCC()
    {
        return membersCC;
    }

    public void onLoad() 
	{
        _log.info("ZakenManager: Init Zaken Manager.");
    }

    public void onReload() 
	{
    }

    public void onShutdown() 
	{
    }
}