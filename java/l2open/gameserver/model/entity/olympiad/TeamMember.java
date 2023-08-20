package l2open.gameserver.model.entity.olympiad;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.network.MMOConnection;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.SkillTimeStamp;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Files;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.reference.*;

import java.util.concurrent.ConcurrentSkipListSet;

public class TeamMember
{
    private OlympiadGame _game;
	private HardReference<L2Player> _player;
    private int _objId;
    private String _name = "";
	private int _classId;
    private CompType _type;
    private int _side;
    private Location _returnLoc;
    private int _returnRef;
    private boolean _isDead;

	private boolean _isPortToArena = false;

    public boolean isDead() {
        return _isDead;
    }

    public void doDie() {
        _isDead = true;
    }

    public TeamMember(int obj_id, String name, OlympiadGame game, int side)
	{
        _objId = obj_id;
        _name = name;
        _game = game;
        _type = game.getType();
        _side = side;

        L2Player player = L2ObjectsStorage.getPlayer(obj_id);
        if (player == null)
            return;

        _player = player.getRef();
		_classId = player.getActiveClassId();

        try
		{
            if(player.inObserverMode())
				player.leaveObserverMode(Olympiad.getGameBySpectator(player));
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }

        player.setOlympiadSide(side);
        player.setOlympiadGame(game);
    }

    public StatsSet getStat() {
        return Olympiad._nobles.get(_objId);
    }

    public void takePointsForCrash()
	{
        //if(!checkPlayer())
		if(!Olympiad.isFakeOly())
            try
			{
                StatsSet stat = getStat();
				if(stat != null)
				{
					int points = stat.getInteger(Olympiad.POINTS);
					int diff = Math.min(OlympiadGame.MAX_POINTS_LOOSE, points / _type.getLooseMult());
					if(ConfigValue.OlympiadTakePointForCrash)
						stat.set(Olympiad.POINTS, points - diff);
					l2open.util.Util.test("Olympiad Result: " + _name + " lost " + diff + " points for crash: ", "olympiad", "olympiad_crash");
					Log.add("Olympiad Result: " + _name + " lost " + diff + " points for crash", "olympiad_crash");
				}
                // TODO: Снести подробный лог после исправления беспричинного отъёма очков.
                L2Player player = _player.get();
                if(player == null)
                    Log.add("Olympiad info: " + _name + " crashed coz player == null", "olympiad");
                else 
				{
					if(player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
						l2open.util.Util.test("Olympiad info: " + _name + " crashed coz isCombatFlagEquipped() || isTerritoryFlagEquipped: ", "olympiad", "olympiad_crash");
                        //Log.add("Olympiad info: " + _name + " crashed coz player.isLogoutStarted()", "olympiad");
                    if(player.isLogoutStarted() || player.isLogout())
						l2open.util.Util.test("Olympiad info: " + _name + " crashed coz player.isLogoutStarted(): ", "olympiad", "olympiad_crash");
                        //Log.add("Olympiad info: " + _name + " crashed coz player.isLogoutStarted()", "olympiad");
                    if(!player.isOnline())
						l2open.util.Util.test("Olympiad info: " + _name + " crashed coz !player.isOnline(): ", "olympiad", "olympiad_crash");
                       // Log.add("Olympiad info: " + _name + " crashed coz !player.isOnline()", "olympiad");
                    if(player.getOlympiadGame() == null)
						l2open.util.Util.test("Olympiad info: " + _name + " crashed coz player.getOlympiadGame() == null: ", "olympiad", "olympiad_crash");
                       // Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadGame() == null", "olympiad");
                    if(player.getOlympiadObserveId() > 0)
						l2open.util.Util.test("Olympiad info: " + _name + " crashed coz player.getOlympiadObserveId() > 0: ", "olympiad", "olympiad_crash");
                       // Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadObserveId() > 0", "olympiad");
                    L2GameClient client = player.getNetConnection();
                    if (client == null)
						l2open.util.Util.test("Olympiad info: " + _name + " crashed: client == null: ", "olympiad", "olympiad_crash");
                       // Log.add("Olympiad info: " + _name + " crashed: client == null", "olympiad");
                    else {
                        MMOConnection conn = client.getConnection();
                        if (conn == null)
							l2open.util.Util.test("Olympiad info: " + _name + " crashed coz conn == null: ", "olympiad", "olympiad_crash");
                           // Log.add("Olympiad info: " + _name + " crashed coz conn == null", "olympiad");
                        else if (conn.isClosed())
							l2open.util.Util.test("Olympiad info: " + _name + " crashed coz conn.isClosed(): ", "olympiad", "olympiad_crash");
                           // Log.add("Olympiad info: " + _name + " crashed coz conn.isClosed()", "olympiad");
                    }
                }
            }
			catch (Exception e)
			{
                e.printStackTrace();
            }
    }

    public boolean checkPlayer()
	{
        L2Player player = _player.get();
		if(player == null || player.isLogout() || player.isLogoutStarted() || !player.isOnline() || player.getOlympiadGame() == null || player.getOlympiadObserveId() > 0 || player.isDead())
            return false;
        L2GameClient client = player.getNetConnection();
        if(client == null)
            return false;
        MMOConnection conn = client.getConnection();
        if(conn == null || conn.isClosed())
            return false;
		if(player.getVar("jailed") != null || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
			return false;
		if(player.isCursedWeaponEquipped())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.Cursed", player));
			return false;
		}
        return true;
    }

    public void stopEffect() 
	{
        L2Player player = _player.get();
        if(player == null || !checkPlayer())
		{
            _player = HardReferences.emptyRef();
            return;
        }
        try
		{
			for(L2Effect eff : player.getEffectList().getAllEffects())
				if(!eff.getSkill().isToggle())
				{
					eff.setCanDelay(false);
					eff.exit(false, false);
				}
			player.updateEffectIcons();
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
    }

    public void portPlayerToArena()
	{
		L2Player player = _player.get();
        if(!checkPlayer() || player == null || player.isTeleporting())
		{
            _player = HardReferences.emptyRef();
            return;
        }

        try
		{
            _returnLoc = player.getLoc();
            _returnRef = player.getReflection().getId();

			player.setTransformation(0);

            if (player.isDead())
                player.doRevive();
            if (player.isSitting())
                player.standUp();

            player.setTarget(null);
            player.setIsInOlympiadMode(true);

            if (player.getParty() != null) {
                L2Party party = player.getParty();
                party.oustPartyMember(player, false);
            }

			if(ConfigValue.OlympiadAltRewardChance > -1)
			{
				int count = player.getVarInt("CompetitionCount", 0);
				player.setVar("CompetitionCount", String.valueOf(count+1),Olympiad._compEnd);
			}
			if(!Olympiad.isFakeOly())
				Olympiad.incCompetitionCount(player.getObjectId(), _game.getType());

			L2Zone zone = Olympiad.STADIUMS[_game.getOllyId()].getZone();
			int[] tele = zone.getSpawns().get(_side - 1);

			_isPortToArena = true;
			
			player.setReflection(_game.getReflect());
			player.setVar("backCoordsOly", _returnLoc.toXYZString());
            player.teleToLocation(tele[0], tele[1], tele[2], _game.getReflect().getId());
			if(ConfigValue.OlympiadStatEnable)
				OlympiadStat.updateInfo(player, 2, 0); // количество входов

            if(_type == CompType.TEAM_RANDOM || _type == CompType.TEAM)
                player.setTeam(_side, true);

            player.sendPacket(new ExOlympiadMode(_side));

			// Снимаем вещи, которые нельзя использовать на оли.
			for(L2ItemInstance item : player.getInventory().getItemsList())
				if(!item.getOlympiadUse())
				{
					if(item.isEquipped())
						player.getInventory().unEquipItem(item);
					else
						player.getInventory().refreshListenersUnequipped(-1, item);
				}
				else if(item._visual_item_id > 0 && item.isEquipped())
					player.getInventory().refreshListeners(item, -1);
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
    }

	public void sendRelation()
	{
		L2Player player = _player.get();
        if(player == null)
            return;
		player.broadcastRelationChanged();
	}

    public void portPlayerBack() 
	{
		L2Player player = _player.get();
        if (player == null)
            return;

        try
		{
            player.setIsInOlympiadMode(false);
            player.setOlympiadSide(-1);
            player.setOlympiadGame(null);

			for(L2Effect eff : player.getEffectList().getAllEffects())
				if(!eff.getSkill().isToggle())
					eff.exit(false, false);
			player.updateEffectIcons();

            player.setCurrentCp(player.getMaxCp());
            player.setCurrentMp(player.getMaxMp());
			player.setReflection(0);

            if(player.isDead())
			{
                player.setCurrentHp(player.getMaxHp(), true);
                player.broadcastPacket(new Revive(player));
            }
			else
                player.setCurrentHp(player.getMaxHp(), false);

            // Add clan skill
            if(player.getClan() != null)
                for(L2Skill skill : player.getClan().getAllSkills())
                    if(skill.getMinPledgeClass() <= player.getPledgeClass())
                        player.addSkill(skill, false);

            //Add Squad skill
            if(player.getClan() != null)
                if(player.getClan().getSquadSkills() != null && !player.getClan().getSquadSkills().isEmpty())
                    for(int pledgeId : player.getClan().getSquadSkills().keySet())
					{
                        FastMap<Integer, L2Skill> skills = player.getClan().getSquadSkills().get(pledgeId);
                        for(L2Skill s : skills.values())
						{
                            player.sendPacket(new ExSubPledgetSkillAdd(s.getId(), s.getLevel(), pledgeId));
                            if(pledgeId == player.getPledgeType())
                                player.addSkill(s, false);
                        }
                    }

            // Add Hero Skills
            if (player.isHero())
                Hero.addSkills(player);

            // Обновляем скилл лист, после добавления скилов
            player.sendPacket(new SkillList(player));
			if(player.getTeam() > 0)
				player.sendPacket(new ExOlympiadMode(0));

			// Сбрасываем статистику дамага.
			player.setTeam(0, true);

			Olympiad.removeRegistration(player.getObjectId());

			for(L2ItemInstance item : player.getInventory().getItemsList())
				if(!item.getOlympiadUse() && item.getItem() instanceof L2EtcItem && !item.isStackable() && (item.getStatFuncs(false) != null || item.getItem().getAttachedSkills() != null))
					player.getInventory().refreshListenersEquipped(-1, item);
				else if(item._visual_item_id > 0 && item.isEquipped())
					player.getInventory().refreshListeners(item, -1);

        }
		catch (Exception e)
		{
            e.printStackTrace();
        }

        try
		{
			if(_isPortToArena)
			{
				if(ConfigValue.OlympiadStatEnable)
					OlympiadStat.updateInfo(player, 3, 0); // время пребывания в зоне
				if(_returnLoc != null)
				{
					player.setReflection(_returnRef);
					player.teleToLocation(_returnLoc);
				}
				else
				{
					player.setReflection(0);
					player.teleToClosestTown();
				}
			}
        }
		catch(Exception e)
		{
            e.printStackTrace();
        }
		player.unsetVar("backCoords");
		player.unsetVar("backCoordsOly");

		if(ConfigValue.OlympiadAltRewardChance > -1)
		{
			int chance = ConfigValue.OlympiadAltRewardChance;
			int count = player.getVarInt("CompetitionCount", 0);
			if(count >= 25)
				chance = 50;
			else if(count >= 15)
				chance = 20;
			else if(count >= 10)
				chance = 10;
			if(Rnd.chance(chance))
			{
				int rnd = Rnd.get(100);
				int item_id = 0;
				if(rnd < 60)
					item_id = 40097;
				else if(rnd < 95)
					item_id = 40098;
				else
					item_id = 40099;
				L2ItemInstance item = player.getInventory().addItem(item_id, 1);
				player.sendPacket(SystemMessage.obtainItems(item.getItemId(), 1, 0));

				NpcHtmlMessage msg = new NpcHtmlMessage(5);
				String txt = Files.read("./data/scripts/vidak/oly_reward.htm", player);
				txt = txt.replace("<?item_name?>", item.getName());
				txt = txt.replace("<?item_icon?>", item.getItem().getIcon());
				msg.setHtml(txt);
				player.sendPacket(msg);
			}
		}
    }

	public void regenPlayer()
	{
        L2Player player = _player.get();
        if (player == null)
            return;
		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		if(player.getPet() != null)
			player.getPet().setCurrentHpMp(player.getPet().getMaxHp(), player.getPet().getMaxMp());	
		player.setCurrentCp(player.getMaxCp());
		player.broadcastUserInfo(true);
	}

    public void preparePlayer()
	{
		L2Player player = _player.get();
        if (player == null)
            return;

        try
		{
            // Remove Buffs
			for(L2Effect eff : player.getEffectList().getAllEffects())
				if(!eff.getSkill().isToggle())
					eff.exit(false, false);
			player.updateEffectIcons();

            // Сброс кулдауна скилов с базовым реюзом 15 мин и менее
            boolean reseted = false;
            for(SkillTimeStamp sts : player.getSkillReuseTimeStamps().values())
			{
				L2Skill sk = player.getKnownSkill(sts.getSkill());
                if(sk != null && sk.getReuseDelay() <= 15 * 60 * 1000)
				{
                    player.enableSkill(ConfigValue.SkillReuseType == 0 ? sts.getSkill()*65536L+sts.getLevel() : sts.getSkill());
                    reseted = true;
                }
			}
            if (reseted)
                player.sendPacket(new SkillCoolTime(player));

            // Remove clan skill
            if(player.getClan() != null)
			{
                for(L2Skill skill : player.getClan().getAllSkills())
                    player.removeSkill(skill, false, false);
			}

            // Remove squad skill
            if(player.getClan() != null)
			{
                if(player.getClan().getSquadSkills() != null && !player.getClan().getSquadSkills().isEmpty())
				{
                    for(int pledgeId : player.getClan().getSquadSkills().keySet())
					{
                        FastMap<Integer, L2Skill> skills = player.getClan().getSquadSkills().get(pledgeId);
                        for(L2Skill s : skills.values())
						{
                            player.sendPacket(new ExSubPledgetSkillAdd(s.getId(), s.getLevel(), pledgeId));
                            if(pledgeId == player.getPledgeType())
                                player.removeSkill(s, false, false);
                        }
                    }
                }
				player.updateEffectIcons();
            }

            // Remove Hero Skills
            if (player.isHero())
                Hero.removeSkills(player);

            // Abort casting if player casting
            if (player.isCastingNow())
                player.abortCast(true);

            // Удаляем чужие кубики
            for(L2Cubic cubic : player.getCubics())
                if(!cubic.isMyCubic())
                    player.deleteCubic(cubic.getSlot());

            // Remove Summon's Buffs
            if (player.getPet() != null) {
                L2Summon summon = player.getPet();
                if (summon.isPet())
                    summon.unSummon();
                else
                    summon.getEffectList().stopAllEffects(true);
            }

            // unsummon agathion
            if (player.getAgathion() != null)
                player.setAgathion(0);

            //Если игрок в дуэли, останавливаем дуэль.
            if (player.getDuel() != null) {
                player.getDuel().setDuelState(player, Duel.DuelState.Interrupted);
            }

            // Обновляем скилл лист, после удаления скилов
            player.sendPacket(new SkillList(player));

            // Remove Hero weapons
            L2ItemInstance wpn = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
            if(wpn != null && wpn.isHeroWeapon() && !wpn.getItem()._is_hero)
			{
                player.getInventory().unEquipItem(wpn);
                player.abortAttack(true, true);
                player.validateItemExpertisePenalties(false, false, true);
            }

			if(ConfigValue.OlympiadRemoveAutoShot)
			{
				// remove bsps/sps/ss automation
				ConcurrentSkipListSet<Integer> activeSoulShots = player.getAutoSoulShot();
				for(int itemId : activeSoulShots)
				{
					player.removeAutoSoulShot(itemId);
					player.sendPacket(new ExAutoSoulShot(itemId, false));
				}

				// Разряжаем заряженные соул и спирит шоты
				L2ItemInstance weapon = player.getActiveWeaponInstance();
				if (weapon != null)
				{
					weapon.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
					weapon.setChargedSoulshot(L2ItemInstance.CHARGED_NONE);
				}
			}

            player.broadcastUserInfo(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveNobleData() {
        OlympiadDatabase.saveNobleData(_objId);
    }

    public void logout() 
	{
		if(ConfigValue.OlympiadStatEnable)
			OlympiadStat.updateInfo(getPlayer(), 3, 0); // время пребывания в зоне
        _player = HardReferences.emptyRef();
		_game = null;
		_objId = 0;
    }

    public L2Player getPlayer() {
        return _player.get();
    }

    public int getObjId() {
        return _objId;
    }

    public String getName() {
        return _name;
    }

	public int getClassId() {
		return _classId;
	}
}