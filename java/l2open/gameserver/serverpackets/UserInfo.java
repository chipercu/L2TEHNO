package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Cubic;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.Experience; 
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;

public class UserInfo extends L2GameServerPacket {
    private boolean can_writeImpl = false, partyRoom, hasFame;
    private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd, _relation;
    private float move_speed, attack_speed, col_radius, col_height;
    private PcInventory _inv;
    private Location _loc, _fishLoc;
    private int obj_id, vehicle_obj_id, _race, sex, base_class, level, curCp, maxCp, _enchant, _weaponFlag;
    private long _exp;
    private int curHp, maxHp, curMp, maxMp, curLoad, maxLoad, rec_left, rec_have;
    private int _str, _con, _dex, _int, _wit, _men, _sp, ClanPrivs, InventoryLimit;
    private int _patk, _patkspd, _pdef, evasion, accuracy, crit, _matk, _matkspd, mCritRate;
    private int _mdef, pvp_flag, karma, hair_style, hair_color, face, gm_commands, fame, vitality;
    private int clan_id, clan_crest_id, ally_id, ally_crest_id, large_clan_crest_id;
    private int private_store, can_crystalize, pk_kills, pvp_kills, class_id, agathion;
    private int team=0;
	private int noble, hero, fishing, mount_id, cw_level;
    private int name_color, running, pledge_class, pledge_type, title_color, transformation;
    private int DefenceFire, DefenceWater, DefenceWind, DefenceEarth, DefenceHoly, DefenceUnholy;
    private byte mount_type;
    private String _name, title;
    private L2Cubic[] cubics;
    private int[] attackElement;
    private boolean isFlying = false;
    private int _territoryId;
    private int[] abnormal_list;
	private AbnormalVisualEffect[] _abnormalEffects;

    public UserInfo(L2Player _cha) 
	{
		clan_id = _cha.getClanId();
        if (_cha.getTransformationName() != null) 
		{
            _name = _cha.getTransformationName();
            title = "";
            clan_crest_id = 0;
            ally_crest_id = 0;
            large_clan_crest_id = 0;
            cw_level = CursedWeaponsManager.getInstance().getLevel(_cha.getCursedWeaponEquippedId());
			title_color = _cha.getTitleColor();
			name_color = _cha.getNameColor();
        }
		else if(_cha.getEventMaster() != null && _cha.getEventMaster().sendUserModInfo(_cha))
		{
			_name =  _cha.getEventMaster().getCharName(_cha);
			title = _cha.getEventMaster().getCharTitle(_cha);
			name_color =  _cha.getEventMaster().getCharNameColor(_cha);
			title_color =  _cha.getEventMaster().getCharTitleColor(_cha);

			clan_id = _cha.getEventMaster().getClanId(_cha);
            clan_crest_id = _cha.getClanCrestId();
            ally_crest_id = _cha.getAllyCrestId();
            large_clan_crest_id = _cha.getClanCrestLargeId();
            cw_level = 0;
		}
		else 
		{
            _name = _cha.getName();
            clan_crest_id = _cha.getClanCrestId();
            ally_crest_id = _cha.getAllyCrestId();
            large_clan_crest_id = _cha.getClanCrestLargeId();
            cw_level = 0;

			if(_cha.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUFF)
			{
				title = _cha._buf_title;
				title_color = ConfigValue.OfflineBuffTitleColor;
				name_color = ConfigValue.OfflineBuffNameColor;
			}
			else
			{
				title = _cha.getTitle();
				title_color = _cha.getTitleColor();
				name_color = _cha.getNameColor();
			}
        }

        if(_cha.getPlayerAccess().GodMode && _cha.isInvisible())
            title += "[I]";
        if(_cha.isPolymorphed())
		{
            if(NpcTable.getTemplate(_cha.getPolyid()) != null)
                title += " - " + NpcTable.getTemplate(_cha.getPolyid()).name;
            else
                title += " - Polymorphed";
		}

        if (_cha.isMounted()) 
		{
            _enchant = 0;
            mount_id = _cha.getMountNpcId() + 1000000;
            mount_type = (byte) _cha.getMountType();
        } 
		else 
		{
            _enchant = (byte) _cha.getEnchantEffect();
            mount_id = 0;
            mount_type = 0;
        }

        _weaponFlag = _cha.getActiveWeaponInstance() == null ? 0x14 : 0x28;

		move_speed = _cha.getMovementSpeedMultiplier();
		_runSpd = _cha.getTemplate().baseRunSpd;//(int) (_cha.getRunSpeed() / move_speed);
		_walkSpd = _cha.getTemplate().baseWalkSpd;//(int) (_cha.getWalkSpeed() / move_speed);

        _flRunSpd = 0; // TODO
        _flWalkSpd = 0; // TODO

        if (_cha.isFlying()) 
		{
            _flyRunSpd = _runSpd;
            _flyWalkSpd = _walkSpd;
        } 
		else 
		{
            _flyRunSpd = 0;
            _flyWalkSpd = 0;
        }

        _swimRunSpd = _cha.getSwimSpeed();
        _swimWalkSpd = _cha.getSwimSpeed();
        _inv = _cha.getInventory();
        _relation = _cha.isClanLeader()? 0x40 : 0;
        if (_cha.getSiegeState() == 1)
            _relation |= 0x180;
        else if (_cha.getSiegeState() == 2)
            _relation |= 0x80;
		if(_cha.getTerritorySiege() > -1)
			_relation |= 0x1000;

        _relation = ConfigValue.relation > 0 && _cha.isGM() ? ConfigValue.relation : _relation;

        _loc = _cha.getLoc();
        obj_id = _cha.getObjectId();
        vehicle_obj_id = _cha.isInVehicle() ? _cha.getVehicle().getObjectId() : 0x00;
		_race = _cha.getBaseTemplate().race.ordinal();
        sex = _cha.getSex();
        base_class = _cha.getClassRace();
        level = _cha.getLevel();
        _exp = _cha.getExp();
        _str = _cha.getSTR();
        _dex = _cha.getDEX();
        _con = _cha.getCON();
        _int = _cha.getINT();
        _wit = _cha.getWIT();
        _men = _cha.getMEN();
        curHp = (int) _cha.getCurrentHp();
        maxHp = _cha.getMaxHp();
        curMp = (int) _cha.getCurrentMp();
        maxMp = _cha.getMaxMp();
        curLoad = _cha.getCurrentLoad();
        maxLoad = _cha.getMaxLoad();
        _sp = _cha.getIntSp();
        _patk = _cha.getPAtk(null);
        _patkspd = (int)_cha.getPAtkSpd();
        _pdef = _cha.getPDef(null);
        evasion = _cha.getEvasionRate(null);
        accuracy = _cha.getAccuracy();
		mCritRate = (int)_cha.getMagicCriticalRate(null, null);
        crit = _cha.getCriticalHit(null, null);
        _matk = _cha.getMAtk(null, null);
        _matkspd = (int)_cha.getMAtkSpd();
        _mdef = _cha.getMDef(null, null);
        pvp_flag = _cha.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
        karma = _cha.getKarma();
        attack_speed = _cha.getAttackSpeedMultiplier();
        col_radius = _cha.getColRadius();
        col_height = _cha.getColHeight();
        hair_style = _cha.getHairStyle();
        hair_color = _cha.getHairColor();
        face = _cha.getFace();
        gm_commands = _cha.isGM() || _cha.getPlayerAccess().CanUseGMCommand || ConfigValue.AllowSpecialCommands ? 1 : 0;
        // builder level активирует в клиенте админские команды
        ally_id = _cha.getAllyId();
        private_store = _cha.getPrivateStoreType();
        can_crystalize = _cha.getSkillLevel(L2Skill.SKILL_CRYSTALLIZE) > 0 ? 1 : 0;
        pk_kills = _cha.getPkKills();
        pvp_kills = _cha.getPvpKills();
        cubics = _cha.getCubics().toArray(new L2Cubic[_cha.getCubics().size()]);
        abnormal_list = _cha.getAbnormalEffectList();
		_abnormalEffects = _cha.getAbnormalEffectsArray();

        ClanPrivs = _cha.getClanPrivileges();
		rec_left = _cha.getRecommendation().getRecomLeft(); //c2 recommendations remaining
		rec_have = _cha.isGM() ? 0 : _cha.getRecommendation().getRecomHave(); //c2 recommendations received
        InventoryLimit = _cha.getInventoryLimit();
        class_id = _cha.getClassId().getId();
        maxCp = _cha.getMaxCp();
        curCp = (int) _cha.getCurrentCp();

		if(_cha.getEventMaster() == null || _cha.getEventMaster().sendVisualTeam(_cha))
		{
			if (_cha.getTeam() < 3)
				team = _cha.getTeam(); //team circle around feet 1= Blue, 2 = red
			else
				team = 1;
		}

        noble = _cha.isNoble() || _cha.isGM() && ConfigValue.GMHeroAura ? 1 : 0; //0x01: symbol on char menu ctrl+I
        hero = _cha.isHero() || _cha.isGM() && ConfigValue.GMHeroAura ? 1 : 0; //0x01: Hero Aura and symbol
        fishing = _cha.isFishing() ? 1 : 0; // Fishing Mode
        _fishLoc = _cha.getFishLoc();
        running = _cha.isRunning() ? 0x01 : 0x00; //changes the Speed display on Status Window
        pledge_class = _cha.getPledgeClass();
        pledge_type = _cha.getPledgeType();
        transformation = _cha.getTransformation();
        attackElement = _cha.getAttackElement();
        DefenceFire = _cha.getDefenceFire();
        DefenceWater = _cha.getDefenceWater();
        DefenceWind = _cha.getDefenceWind();
        DefenceEarth = _cha.getDefenceEarth();
        DefenceHoly = _cha.getDefenceHoly();
        DefenceUnholy = _cha.getDefenceUnholy();
        agathion = _cha.getAgathion() != null ? _cha.getAgathion().getId() : 0; //агнишен
        fame = _cha.getFame();
        vitality = (int) _cha.getVitality() * 2;
        partyRoom = PartyRoomManager.getInstance().isLeader(_cha);
		hasFame = _cha.hasSetFame();
        isFlying = _cha.isInFlyingTransform();
        _territoryId = _cha.getTerritorySiege();

        _cha.refreshSavedStats();
        can_writeImpl = true;
    }

    @Override
    protected final void writeImpl() 
	{
        if (!can_writeImpl)
            return;

        writeC(0x32);

        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z + ConfigValue.ClientZShift);
        writeD(vehicle_obj_id);
        writeD(obj_id);
        writeS(_name);
        writeD(_race);
        writeD(sex);
        writeD(base_class);
        writeD(level);
        writeQ(_exp);
		writeF((float)(_exp - Experience.LEVEL[level]) / (Experience.LEVEL[level + 1] - Experience.LEVEL[level])); // High Five exp % 
        writeD(_str);
        writeD(_dex);
        writeD(_con);
        writeD(_int);
        writeD(_wit);
        writeD(_men);
        writeD(maxHp);
        writeD(curHp);
        writeD(maxMp);
        writeD(curMp);
        writeD(_sp);
        writeD(curLoad);
        writeD(maxLoad);
        writeD(_weaponFlag);

        for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(_inv.getPaperdollObjectId(PAPERDOLL_ID));

		writePaperdollInfo(_inv, true, true, false);

        for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(_inv.getPaperdollAugmentationId(PAPERDOLL_ID));

        writeD(0x00); // TODO getMaxTalismanCount (SF)
        writeD(0x01); // TODO getCloakStatus (SF)

        writeD(_patk);
        writeD(_patkspd);
        writeD(_pdef);
        writeD(evasion);
        writeD(accuracy);
        writeD(crit);
        writeD(_matk);
        writeD(_matkspd);
        writeD(_patkspd);
        writeD(_mdef);

        writeD(pvp_flag);
        writeD(karma);
        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_swimRunSpd); // swimspeed
        writeD(_swimWalkSpd); // swimspeed
        writeD(_flRunSpd);
        writeD(_flWalkSpd);
        writeD(_flyRunSpd);
        writeD(_flyWalkSpd);
        writeF(move_speed);
        writeF(attack_speed);
        writeF(col_radius);
        writeF(col_height);
        writeD(hair_style);
        writeD(hair_color);
        writeD(face);
        writeD(gm_commands);
        writeS(title);
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);
        // 0x40 leader rights
        // siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
        writeD(_relation);
        writeC(mount_type); // mount type
        writeC(private_store);
        writeC(can_crystalize);
        writeD(pk_kills);
        writeD(pvp_kills);
        writeH(cubics.length);
        for (L2Cubic cubic : cubics) 
            writeH(cubic == null ? 0 : cubic.getId());
        writeC(partyRoom ? 0x01 : 0x00); //1-find party members
		writeD(abnormal_list[0]);
        writeC(isFlying ? 0x02 : 0x00);
        writeD(ClanPrivs);
        writeH(rec_left);
        writeH(rec_have);
        writeD(mount_id);
        writeH(InventoryLimit);
        writeD(class_id);
        writeD(0x00); // special effects? circles around player...
        writeD(maxCp);
        writeD(curCp);
        writeC(_enchant);
        writeC(team);
        writeD(large_clan_crest_id);
        writeC(noble);
        writeC(hero);
        writeC(fishing);
        writeD(_fishLoc.x);
        writeD(_fishLoc.y);
        writeD(_fishLoc.z);
        writeD(name_color);
        writeC(running);
        writeD(pledge_class);
        writeD(pledge_type);
        writeD(title_color);
        writeD(cw_level);
        writeD(transformation); // Transformation id

        // AttackElement (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
        writeH(attackElement == null ? -2 : attackElement[0]);
        writeH(attackElement == null ? 0 : attackElement[1]); // AttackElementValue
        writeH(DefenceFire); // DefAttrFire
        writeH(DefenceWater); // DefAttrWater
        writeH(DefenceWind); // DefAttrWind
        writeH(DefenceEarth); // DefAttrEarth
        writeH(DefenceHoly); // DefAttrHoly
        writeH(DefenceUnholy); // DefAttrUnholy

        writeD(agathion);

        // T2 Starts
        writeD(fame); // Fame
        writeD(hasFame ? 0x01 : 0x00);
        writeD(vitality); // Vitality Points

		writeD(abnormal_list[1]);

		writeD(_territoryId > 0 ? 0x50 + _territoryId : 0);
		writeD(0x00); // TODO _isDisguised (SF)
		writeD(_territoryId > 0 ? 0x50 + _territoryId : 0);
    }

	@Override
	protected boolean writeImplLindvior()
	{
		if(!can_writeImpl)
			return true;

		        writeC(0x32);

        writeD(_loc.x);
        writeD(_loc.y);
		writeD(_loc.z + ConfigValue.ClientZShift);
        writeD(vehicle_obj_id);
        writeD(obj_id);
        writeS(_name);
        writeD(_race);
        writeD(sex);
        writeD(base_class);
        writeD(level);
        writeQ(_exp);
		writeF((float)(_exp - Experience.LEVEL[level]) / (Experience.LEVEL[level + 1] - Experience.LEVEL[level])); // High Five exp % 
        writeD(_str);
        writeD(_dex);
        writeD(_con);
        writeD(_int);
        writeD(_wit);
        writeD(_men);
        writeD(maxHp);
        writeD(curHp);
        writeD(maxMp);
        writeD(curMp);
        writeD(_sp);
        writeD(curLoad);
        writeD(maxLoad);
        writeD(_weaponFlag);

		for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(_inv.getPaperdollObjectId(PAPERDOLL_ID));

		writePaperdollInfo(_inv, true, true, false);

        for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(_inv.getPaperdollAugmentationId(PAPERDOLL_ID));

        /*for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
            writeD(_inv[PAPERDOLL_ID][0]);

        for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
            writeD(_inv[PAPERDOLL_ID][1]);

        for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
            writeD(_inv[PAPERDOLL_ID][2]);*/

        writeD(0x00); // TODO getMaxTalismanCount (SF)
        writeD(0x01); // TODO getCloakStatus (SF)

		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_RHAND)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LHAND)); // Tauti
		writeD(0); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_GLOVES)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_CHEST)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LEGS)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_FEET)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_HAIR)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_DHAIR)); // Tauti


        writeD(_patk);
        writeD(_patkspd);
        writeD(_pdef);
        writeD(evasion);
        writeD(accuracy);
        writeD(crit);
        writeD(_matk);
        writeD(_matkspd);
        writeD(_patkspd);
        writeD(_mdef);
		writeD(0); //  writeD(mevasion); // mEvasion //GOD
		writeD(0); //  writeD(maccuracy); // mAccuracy //GOD
		writeD(mCritRate); // mCritRate //GOD
        writeD(pvp_flag);
        writeD(-karma);
        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_swimRunSpd); // swimspeed
        writeD(_swimWalkSpd); // swimspeed
        writeD(_flRunSpd);
        writeD(_flWalkSpd);
        writeD(_flyRunSpd);
        writeD(_flyWalkSpd);
        writeF(move_speed);
        writeF(attack_speed);
        writeF(col_radius);
        writeF(col_height);
        writeD(hair_style);
        writeD(hair_color);
        writeD(face);
        writeD(gm_commands);
        writeS(title);
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);
        // 0x40 leader rights
        // siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
        writeD(_relation);
        writeC(mount_type); // mount type
        writeC(private_store);
        writeC(can_crystalize);
        writeD(pk_kills);
        writeD(pvp_kills);
        writeH(cubics.length);
        for(L2Cubic cubic : cubics)
            writeH(cubic == null ? 0 : cubic.getId());
        writeC(partyRoom ? 0x01 : 0x00); // 1-find party members
        writeC(isFlying ? 0x02 : 0x00);
        writeD(ClanPrivs);
        writeH(rec_left);
        writeH(rec_have);
        writeD(mount_id);
        writeH(InventoryLimit);
        writeD(class_id);
        writeD(0x00);
        writeD(maxCp);
        writeD(curCp);
        writeC(_enchant);
        writeC(team);
        writeD(large_clan_crest_id);
        writeC(noble);
        writeC(hero);
        writeC(fishing);
        writeD(_fishLoc.x);
        writeD(_fishLoc.y);
        writeD(_fishLoc.z);
        writeD(name_color);
        writeC(running);
        writeD(pledge_class);
        writeD(pledge_type);
        writeD(title_color);
        writeD(cw_level);
        writeD(transformation); // Transformation id

        // AttackElement (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
        writeH(attackElement == null ? -2 : attackElement[0]);
        writeH(attackElement == null ? 0 : attackElement[1]); // AttackElementValue
        writeH(DefenceFire); // DefAttrFire
        writeH(DefenceWater); // DefAttrWater
        writeH(DefenceWind); // DefAttrWind
        writeH(DefenceEarth); // DefAttrEarth
        writeH(DefenceHoly); // DefAttrHoly
        writeH(DefenceUnholy); // DefAttrUnholy

        writeD(agathion);

        // T2 Starts
        writeD(fame); // Fame
        writeD(hasFame ? 1 : 0); // Minimap on Hellbound
        writeD(vitality); // Vitality Points

        /*
           * START: Структура написана от балды, чтобы соответствовать размеру пакета:
           */
        writeD(0x00);
        writeD(0x00);
        writeC(0x00); // UI кнопка refresh
        writeD(0x00);
        /*
           * END: Структура написана от балды, чтобы соответствовать размеру пакета:
           */

		writeD(_abnormalEffects.length);
		for(AbnormalVisualEffect abnormal : _abnormalEffects)
			writeD(abnormal.ordinal());

        writeC(0x00); // TAUTI

        /*writeC(0x32);

        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z + ConfigValue.ClientZShift);
        writeD(vehicle_obj_id);
        writeD(obj_id);
        writeS(_name);
        writeD(_race);
        writeD(sex);
        writeD(base_class);
        writeD(level);
        writeQ(_exp);
		writeF((float)(_exp - Experience.LEVEL[level]) / (Experience.LEVEL[level + 1] - Experience.LEVEL[level])); // High Five exp % 
        writeD(_str);
        writeD(_dex);
        writeD(_con);
        writeD(_int);
        writeD(_wit);
        writeD(_men);
        writeD(maxHp);
        writeD(curHp);
        writeD(maxMp);
        writeD(curMp);
        writeD(_sp);
        writeD(curLoad);
        writeD(maxLoad);
        writeD(_weaponFlag);

        for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(_inv.getPaperdollObjectId(PAPERDOLL_ID));

		writePaperdollInfo(_inv, true, true, false);

        for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(_inv.getPaperdollAugmentationId(PAPERDOLL_ID));

        writeD(0x00); // TODO getMaxTalismanCount (SF)
        writeD(0x01); // TODO getCloakStatus (SF)

		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_RHAND)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LHAND)); // Tauti
		writeD(0); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_GLOVES)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_CHEST)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LEGS)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_FEET)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_HAIR)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_DHAIR)); // Tauti

        writeD(_patk);
        writeD(_patkspd);
        writeD(_pdef);
        writeD(evasion);
        writeD(accuracy);
        writeD(crit);
        writeD(_matk);
        writeD(_matkspd);
        writeD(_patkspd);
        writeD(_mdef);

		writeD(0x00); // writeD(_mevasion);
		writeD(0x00); // writeD(_maccuracy);
		writeD(0x00); // writeD(_mCritRate);

        writeD(pvp_flag);
        writeD(karma);
        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_swimRunSpd); // swimspeed
        writeD(_swimWalkSpd); // swimspeed
        writeD(_flRunSpd);
        writeD(_flWalkSpd);
        writeD(_flyRunSpd);
        writeD(_flyWalkSpd);
        writeF(move_speed);
        writeF(attack_speed);
        writeF(col_radius);
        writeF(col_height);
        writeD(hair_style);
        writeD(hair_color);
        writeD(face);
        writeD(gm_commands);
        writeS(title);
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);
        // 0x40 leader rights
        // siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
        writeD(_relation);
        writeC(mount_type); // mount type
        writeC(private_store);
        writeC(can_crystalize);
        writeD(pk_kills);
        writeD(pvp_kills);
        writeH(cubics.length);
        for(L2Cubic cubic : cubics) 
            writeH(cubic == null ? 0 : cubic.getId());
        writeC(partyRoom ? 0x01 : 0x00); //1-find party members
        writeC(isFlying ? 0x02 : 0x00);
        writeD(ClanPrivs);
        writeH(rec_left);
        writeH(rec_have);
        writeD(mount_id);
        writeH(InventoryLimit);
        writeD(class_id);
        writeD(0x00); // special effects? circles around player...
        writeD(maxCp);
        writeD(curCp);
        writeC(_enchant);
        writeC(team);
        writeD(large_clan_crest_id);
        writeC(noble);
        writeC(hero);
        writeC(fishing);
        writeD(_fishLoc.x);
        writeD(_fishLoc.y);
        writeD(_fishLoc.z);
        writeD(name_color);
        writeC(running);
        writeD(pledge_class);
        writeD(pledge_type);
        writeD(title_color);
        writeD(cw_level);
        writeD(transformation); // Transformation id

        // AttackElement (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
        writeH(attackElement == null ? -2 : attackElement[0]);
        writeH(attackElement == null ? 0 : attackElement[1]); // AttackElementValue
        writeH(DefenceFire); // DefAttrFire
        writeH(DefenceWater); // DefAttrWater
        writeH(DefenceWind); // DefAttrWind
        writeH(DefenceEarth); // DefAttrEarth
        writeH(DefenceHoly); // DefAttrHoly
        writeH(DefenceUnholy); // DefAttrUnholy

        writeD(agathion);

        // T2 Starts
        writeD(fame); // Fame
        writeD(hasFame ? 0x01 : 0x00);
        writeD(vitality); // Vitality Points

		writeD(0x00);// Unknown GOD
		writeD(0x00);// Unknown GOD (1 - Party searching?)
		writeC(0x00); // writeC(_partySubstitute);
		writeD(0x00);// Unknown GOD

		writeD(0x00);*/
		/*if(_abnormalEffect == 0 && _abnormalEffect2 == 0)
			writeD(0x00);
		else if(_abnormalEffect > 0 && _abnormalEffect2 > 0)
		{
			writeD(2);
			writeD(_abnormalEffect);
			writeD(_abnormalEffect2);
		}
		else if(_abnormalEffect > 0)
		{
			writeD(1);
			writeD(_abnormalEffect);
		}
		else if(_abnormalEffect2 > 0)
		{
			writeD(1);
			writeD(_abnormalEffect2);
		}*/
		//writeC(0x00);
		return true;
	}

	public String getType()
	{
        return "[S] UserInfo["+_name+"]";
    }

    public static final byte[] PAPERDOLL_ORDER =
	{
			Inventory.PAPERDOLL_UNDER,
			Inventory.PAPERDOLL_REAR,
			Inventory.PAPERDOLL_LEAR,
			Inventory.PAPERDOLL_NECK,
			Inventory.PAPERDOLL_RFINGER,
			Inventory.PAPERDOLL_LFINGER,
			Inventory.PAPERDOLL_HEAD,
			Inventory.PAPERDOLL_RHAND,
			Inventory.PAPERDOLL_LHAND,
			Inventory.PAPERDOLL_GLOVES,
			Inventory.PAPERDOLL_CHEST,
			Inventory.PAPERDOLL_LEGS,
			Inventory.PAPERDOLL_FEET,
			Inventory.PAPERDOLL_BACK,
			Inventory.PAPERDOLL_LRHAND,
			Inventory.PAPERDOLL_HAIR,
			Inventory.PAPERDOLL_DHAIR,
			Inventory.PAPERDOLL_RBRACELET,
			Inventory.PAPERDOLL_LBRACELET,
			Inventory.PAPERDOLL_DECO1,
			Inventory.PAPERDOLL_DECO2,
			Inventory.PAPERDOLL_DECO3,
			Inventory.PAPERDOLL_DECO4,
			Inventory.PAPERDOLL_DECO5,
			Inventory.PAPERDOLL_DECO6,
			Inventory.PAPERDOLL_BELT // Пояс
    };
}











