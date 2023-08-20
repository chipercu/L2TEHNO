package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2DecoyInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

public class NpcInfo extends L2GameServerPacket 
{
    //   ddddddddddddddddddffffdddcccccSSddd dddddccffddddccd
    private boolean can_writeImpl = false;
    private L2Character _cha;
	private L2NpcInstance _npc;
	private int _npcObjId, _npcId, running, incombat, dead, team, _showSpawnAnimation = 0;
    private int _runSpd, _walkSpd, _mAtkSpd, _pAtkSpd, _rhand, _lhand, _enchantEffect;
    private int karma, pvp_flag, _abnormalEffect, _abnormalEffect2, clan_crest_id, ally_crest_id, clanId, allyId;
    private double colHeight, colRadius, currentColHeight, currentColRadius, move_speed, attack_speed;
    private boolean _isAttackable;
    private Location _loc, decoy_fishLoc;
    private String _name = "";
    private String _title = "";
	private int _nps_string_name = -1;
	private int _nps_string_title = -1;
	private int _transformId;
	private int _HP, _maxHP, _MP, _maxMP;

    private Inventory decoy_inv;
    private int decoy_race, decoy_sex, decoy_base_class, decoy_clan_id, decoy_ally_id, _formId;
    private int decoy_noble, decoy_hair_style, decoy_hair_color, decoy_face, decoy_sitting;
    private int decoy_rec_have, decoy_rec_left, decoy_class_id, decoy_large_clan_crest_id;
    private int decoy_PledgeClass, decoy_pledge_type;
    private int decoy_NameColor, decoy_TitleColor, decoy_Transformation, decoy_Agathion;
    private int decoy_hero, decoy_mount_id, decoy_swimSpd, decoy_cw_level, decoy_clan_rep_score;
    private byte decoy_mount_type, decoy_private_store, decoy_fishing;
    private double decoy_attack_speed;
    private L2Cubic[] decoy_cubics;
    private L2Character _attacker;
    private boolean isFlying = false;
	private boolean _isNameAbove = false;
	private int curHP, maxHP, curMP, maxMP, curCP;

	private AbnormalVisualEffect[] _abnormalEffects;

	public NpcInfo(L2Player cha/*, L2Character attacker*/) 
	{
		if(cha == null || cha.isInvisible())
			return;

		_cha = cha;
		//_attacker = attacker;
		_npcId = cha.getPolyid();
		L2NpcTemplate _template = NpcTable.getTemplate(_npcId);
		if(_template == null)
			return;

        currentColHeight = colHeight = _template.collisionHeight;
        currentColRadius = colRadius = _template.collisionRadius;
		_rhand = _template.rhand;
		_lhand = _template.lhand;

		_isAttackable = _attacker == null ? true : cha.isAutoAttackable(_attacker);
		_HP = (int) cha.getCurrentHp();
		_MP = (int) cha.getCurrentMp();
		_maxHP = cha.getMaxHp();
		_maxMP = cha.getMaxMp();

        _showSpawnAnimation = 0;
		_nps_string_name = -1;
		_nps_string_title = -1;
        _enchantEffect = 0;

		/*if(ConfigValue.ServerSideNpcName || cha.getDisplayId() != 0 || cha.isShowName())
            _name = cha.getName();
        if(ConfigValue.ServerSideNpcTitle || cha.getDisplayId() != 0 || cha.isShowTitle())
            _title = _title + cha.getTitle();*/

        if(_cha.getEffectList().getEffectByType(EffectType.Grow) != null) 
		{
            currentColHeight = (int) (currentColHeight / 1.2);
            currentColRadius = (int) (currentColRadius / 1.2);
        }
        _npcObjId = _cha.getObjectId();
        _loc = _cha.getLoc();
        _mAtkSpd = (int)_cha.getMAtkSpd();

		attack_speed = _cha.getAttackSpeedMultiplier();
		move_speed = _cha.getMovementSpeedMultiplier();
		_runSpd = _cha.getTemplate().baseRunSpd;
		_walkSpd = _cha.getTemplate().baseWalkSpd;

		/*_runSpd = _cha.getRunSpeed();
		move_speed = _cha.getMoveMultiplier();
		_walkSpd = _cha.getWalkSpeed();*/
		karma = _cha.getKarma();
		pvp_flag = _cha.getPvpFlag();
		_pAtkSpd = (int)_cha.getPAtkSpd();
		running = _cha.isRunning() ? 1 : 0;
		incombat = _cha.isInCombat() ? 1 : 0;
		dead = _cha.isAlikeDead() ? 1 : 0;
		_abnormalEffect = _cha.getAbnormalEffect();
		_abnormalEffect2 = _cha.getAbnormalEffect2();
		_abnormalEffects = _cha.getAbnormalEffectsArray();
		isFlying = _cha.isFlying();
		_formId = _cha.getFormId();
		_isNameAbove = _cha.isNameAbove();

		if(cha.getEventMaster() == null || cha.getEventMaster().sendVisualTeam(cha))
			team = _cha.getTeam();
		else
			team = 0;

        can_writeImpl = true;
    }

	public NpcInfo(L2NpcInstance cha, L2Character attacker) 
	{
		if(cha == null)
			return;
		if(ConfigValue.DumpForNpc > 0 && cha.getNpcId() == ConfigValue.DumpForNpc)
		{
			_log.info("isRunning: "+cha.isRunning()+" _runSpd="+_cha.getRunSpeed());
			Util.test();
		}
		_npc = cha;
		_cha = cha;
		_attacker = attacker;
		_npcId = cha.getDisplayId() != 0 ? cha.getDisplayId() : cha.getTemplate().npcId;
		//_isAttackable = attacker == null ? false : cha.getTemplate().can_be_attacked == 1;
		_isAttackable = attacker == null ? false : cha.isAutoAttackable(attacker);
        _rhand = cha.getRightHandItem();
        _lhand = cha.getLeftHandItem();
        _enchantEffect = cha.getWeaponEnchant();
        if(ConfigValue.ServerSideNpcName || cha.getDisplayId() != 0 || cha.isShowName())
            _name = cha.getName();
        if(ConfigValue.ServerSideNpcTitle || cha.getDisplayId() != 0 || cha.isShowTitle())
            _title = _title + cha.getTitle();
        _showSpawnAnimation = cha.isShowSpawnAnimation();
		_nps_string_name = _npc._nps_string_name;
		_nps_string_title = _npc._nps_string_title;
		_HP = (int) cha.getCurrentHp();
		_MP = (int) cha.getCurrentMp();
		_maxHP = cha.getMaxHp();
		_maxMP = cha.getMaxMp();
        common();
        can_writeImpl = true;
    }

    public NpcInfo(L2Summon cha, L2Character attacker, int showSpawnAnimation) 
	{
        if(cha == null || cha.getPlayer() != null && cha.getPlayer().isInvisible())
            return;

        _showSpawnAnimation = showSpawnAnimation;
        _cha = cha;
        _attacker = attacker;
        _npcId = cha.getTemplate().npcId;
        _isAttackable = cha.isAutoAttackable(attacker); //(cha.getKarma() > 0);
        _rhand = 0;
        _lhand = 0;
        _enchantEffect = 0;
        if(ConfigValue.ServerSideNpcName || cha.isPet())
            _name = _cha.getName();
        _title = cha.getTitle();

        common();
        can_writeImpl = true;
    }

    private void common() 
	{
        currentColHeight = colHeight = _cha.getColHeight();
        currentColRadius = colRadius = _cha.getColRadius();
        if (_cha.getEffectList().getEffectByType(EffectType.Grow) != null) 
		{
            currentColHeight = (int) (currentColHeight / 1.2);
            currentColRadius = (int) (currentColRadius / 1.2);
        }
        _npcObjId = _cha.getObjectId();
        _loc = _cha.getLoc();
        _mAtkSpd = (int)_cha.getMAtkSpd();

		//
		if(_npc != null && _npc.isCrestEnable())
		{
			if(_npc.getCastle() != null && _npc.getCastle().getOwner() != null && _npc.getNpcId() != 36590 && _npc.getNpcId() != 35062)
				clanId = _npc.getCastle().getOwner().getClanId();
			clan_crest_id = _npc.getClanCrestId();
			if((_npc.getNpcId() == 36590 || _npc.getNpcId() == 35062) && clan_crest_id > 0)
				clanId = ((L2SiegeHeadquarterInstance)_npc).getClan().getClanId();
			//
			if(_npc.getCastle() != null && _npc.getCastle().getOwner() != null && _npc.getCastle().getOwner().getAlliance() != null && _npc.getNpcId() != 36590 && _npc.getNpcId() != 35062)
				allyId = _npc.getCastle().getOwner().getAllyId();
			ally_crest_id = _npc.getAllyCrestId();
			if((_npc.getNpcId() == 36590 || _npc.getNpcId() == 35062) && ally_crest_id > 0 && ((L2SiegeHeadquarterInstance)_npc).getClan().getAlliance() != null)
				allyId = ((L2SiegeHeadquarterInstance)_npc).getClan().getAlliance().getAllyCrestId();
		}

        if (_cha instanceof L2DecoyInstance)
            fillDecoy();
        else 
		{
			move_speed = _cha.getMovementSpeedMultiplier();
			_runSpd = _cha.getTemplate().baseRunSpd;
			_walkSpd = _cha.getTemplate().baseWalkSpd;

           /* _runSpd = _cha.getRunSpeed();
			move_speed = _cha.getMoveMultiplier();
            _walkSpd = _cha.getWalkSpeed();*/
            karma = _cha.getKarma();
            pvp_flag = _cha.getPvpFlag();
            _pAtkSpd = (int)_cha.getPAtkSpd();
            running = _cha.isRunning() ? 1 : 0;
            incombat = _cha.isInCombat() ? 1 : 0;
            dead = _cha.isAlikeDead() ? 1 : 0;
            _abnormalEffect = _cha.getAbnormalEffect();
            _abnormalEffect2 = _cha.getAbnormalEffect2();
			_abnormalEffects = _cha.getAbnormalEffectsArray();
            isFlying = _cha.isFlying();
			_formId = _cha.getFormId();
			_isNameAbove = _cha.isNameAbove();

            if (_cha instanceof L2Summon) 
			{
				if(_cha.getPlayer() == null || _cha.getPlayer().getEventMaster() == null || _cha.getPlayer().getEventMaster().sendVisualTeam(_cha.getPlayer()))
				{
					if (_cha.getTeam() < 3)
						team = _cha.getTeam();
					else if (_attacker == null || _attacker.getTeam() == 0)
						team = 0;
					else if (_attacker.getTeam() == _cha.getTeam())
						team = 1;
					else
						team = 2;
				}
				else
					team = 0;
            } 
			else
                team = _cha.getTeam();
        }
    }

    @Override
    protected final void writeImpl() 
	{
        if(!can_writeImpl)
            return;

        if(_cha instanceof L2DecoyInstance) 
		{
            writeImpl_Decoy();
            return;
        }

        writeC(0x0c);
        //ddddddddddddddddddffffdddcccccSSddddddddccffddddccd
        writeD(_npcObjId);
        writeD(_npcId + 1000000); // npctype id c4
        writeD(_isAttackable ? 1 : 0);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z + ConfigValue.ClientZShift);
        writeD(_loc.h);
        writeD(0x00);
        writeD(_mAtkSpd);
        writeD(_pAtkSpd);
        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_runSpd /*_swimRunSpd*//*0x32*/); // swimspeed
        writeD(_walkSpd/*_swimWalkSpd*//*0x32*/); // swimspeed
        writeD(_runSpd/*_flRunSpd*/);
        writeD(_walkSpd/*_flWalkSpd*/);
        writeD(_runSpd/*_flyRunSpd*/);
        writeD(_walkSpd/*_flyWalkSpd*/);
		writeF(move_speed); // Модификатор скорости моба)
		//writeF(1.100000023841858); // взято из клиента
		writeF(_pAtkSpd / 277.478340719);
		//writeF(speed_atack);

        writeF(colRadius);
        writeF(colHeight);
        writeD(_rhand); // right hand weapon
        writeD(0); //TODO chest
        writeD(_lhand); // left hand weapon
		writeC(_isNameAbove ? 1 : 0); // 2.2: name above char 1=true ... ??; 2.3: 1 - normal, 2 - dead
        writeC(running);
        writeC(incombat);
        writeC(dead);
        writeC(_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
        writeD(_nps_string_name); // High Five NPCString ID  
		writeS(_name);
		writeD(_nps_string_title); // High Five NPCString ID  
		writeS(_title);
        writeD(0/*_isPet*/); // 0- светло зеленый титул(моб), 1 - светло синий(пет)/отображение текущего МП
        writeD(pvp_flag);
        writeD(karma); // hmm karma ??
        writeD(_abnormalEffect); // C2
        writeD(clanId); // clan id (клиентом не используется, но требуется для показа значка)
        writeD(clan_crest_id); // clan crest id
        writeD(allyId); // ally id (клиентом не используется, но требуется для показа значка)
        writeD(ally_crest_id); // ally crest id
        writeC(isFlying ? 2 : 0); // C2
        writeC(team); // team aura 1-blue, 2-red
        writeF(currentColRadius); // тут что-то связанное с colRadius
        writeF(currentColHeight); // тут что-то связанное с colHeight
        writeD(Math.min(_enchantEffect, 127)); // C4
        writeD(0x00); // writeD(_npc.isFlying() ? 1 : 0); // C6
        writeD(0x00);
        writeD(_formId); // great wolf type
        writeC(_npc != null && _npc.isHideName() ? 0x00 : 0x01); // влияет на возможность примененя к цели /nexttarget и /assist
        writeC(_npc != null && _npc.isHideName() ? 0x00 : 0x01); // name above char 1=true ... ??
        writeD(_abnormalEffect2);
		writeD(_npc == null ? 0 : _npc.getNpcState());
    }

	@Override
	protected boolean writeImplLindvior()
	{
		if(!can_writeImpl)
			return true;

		if(_cha instanceof L2DecoyInstance) 
		{
			writeImpl_Decoy();
			return true;
		}

        writeC(0x0c);
        writeD(_npcObjId);
        writeD(_npcId + 1000000); // npctype id c4
        writeD(_isAttackable ? 1 : 0);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z + ConfigValue.ClientZShift);
        writeD(_loc.h);
        writeD(0x00);
        writeD(_mAtkSpd);
        writeD(_pAtkSpd);
        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_runSpd /*_swimRunSpd*//*0x32*/); // swimspeed
        writeD(_walkSpd/*_swimWalkSpd*//*0x32*/); // swimspeed
        writeD(_runSpd/*_flRunSpd*/);
        writeD(_walkSpd/*_flWalkSpd*/);
        writeD(_runSpd/*_flyRunSpd*/);
        writeD(_walkSpd/*_flyWalkSpd*/);
		writeF(move_speed); // Модификатор скорости моба)
		//writeF(1.100000023841858); // взято из клиента
        writeF(_pAtkSpd / 277.478340719);
		//writeF(speed_atack);

        writeF(colRadius);
        writeF(colHeight);
        writeD(_rhand); // right hand weapon
        writeD(0); //TODO chest
        writeD(_lhand); // left hand weapon
        writeC(_isNameAbove ? 1 : 0); // 2.2: name above char 1=true ... ??; 2.3: 1 - normal, 2 - dead

        writeC(running);
        writeC(incombat);
        writeC(dead);
        writeC(_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
        writeD(_nps_string_name); // High Five NPCString ID  
		writeS(_name);
		writeD(_nps_string_title); // High Five NPCString ID  
		writeS(_title);

        writeD(0); // как-то связано с тайтлом, если не 0 скрывать? Title color 0=client default?

        writeD(pvp_flag);
        writeD(karma); // hmm karma ??

        writeD(clanId); // clan id (клиентом не используется, но требуется для показа значка)
        writeD(clan_crest_id); // clan crest id
        writeD(allyId); // ally id (клиентом не используется, но требуется для показа значка)
        writeD(ally_crest_id); // ally crest id
		writeD(0x00);

        writeC(isFlying ? 2 : 0); // C2
        writeC(team); // team aura 1-blue, 2-red

        writeF(currentColRadius); // тут что-то связанное с colRadius
        writeF(currentColHeight); // тут что-то связанное с colHeight

        writeD(Math.min(_enchantEffect, 127)); // C4
		writeD(isFlying ? 1 : 0);
        writeD(0x00);
        writeD(_formId); // great wolf type

        writeC(_npc != null && _npc.isHideName() ? 0x00 : 0x01); // влияет на возможность примененя к цели /nexttarget и /assist
        writeC(_npc != null && _npc.isHideName() ? 0x00 : 0x01); // name above char 1=true ... ??
		writeD(_npc == null ? 0 : _npc.getNpcState());
		writeD(_transformId);
		
		writeD(_HP);
		writeD(_maxHP);
		writeD(_MP);
		writeD(_maxMP);
		
		writeC(0x00);
        writeD(0x00); //unk GOD
        writeD(0x00); //unk GOD
        writeD(0x00); //unk GOD
        writeF(0x00); // Lindvior модификатор размера

		writeD(_abnormalEffects.length);
		for(AbnormalVisualEffect abnormal : _abnormalEffects)
			writeD(abnormal.ordinal());
 
		return true;
	}

    private void fillDecoy() 
	{
        L2Player cha_owner = _cha.getPlayer();
		
		attack_speed = _cha.getAttackSpeedMultiplier();
		move_speed = cha_owner.getMovementSpeedMultiplier();
		_runSpd = cha_owner.getTemplate().baseRunSpd;
		_walkSpd = cha_owner.getTemplate().baseWalkSpd;

		//_runSpd = cha_owner.getRunSpeed();
		//_walkSpd = cha_owner.getWalkSpeed();

        karma = cha_owner.getKarma();
        pvp_flag = cha_owner.getPvpFlag();
        _pAtkSpd = (int)cha_owner.getPAtkSpd();
        running = cha_owner.isRunning() ? 1 : 0;
        incombat = cha_owner.isInCombat() ? 1 : 0;
        dead = cha_owner.isAlikeDead() ? 1 : 0;
        _abnormalEffect = cha_owner.getAbnormalEffect();
		_abnormalEffects = cha_owner.getAbnormalEffectsArray();

		if(cha_owner.getEventMaster() == null || cha_owner.getEventMaster().sendVisualTeam(cha_owner))
			team = cha_owner.getTeam();
		else
			team = 0;

        if (cha_owner.isCursedWeaponEquipped()) 
		{
            _name = cha_owner.getTransformationName();
            _title = "";
            clan_crest_id = 0;
            ally_crest_id = 0;
            decoy_clan_id = 0;
            decoy_ally_id = 0;
            decoy_large_clan_crest_id = 0;
            decoy_cw_level = CursedWeaponsManager.getInstance().getLevel(cha_owner.getCursedWeaponEquippedId());
        } 
		else 
		{
            _name = cha_owner.getName();
            _title = cha_owner.getTitle();
            clan_crest_id = cha_owner.getClanCrestId();
            ally_crest_id = cha_owner.getAllyCrestId();
            decoy_clan_id = cha_owner.getClanId();
            decoy_ally_id = cha_owner.getAllyId();
            decoy_large_clan_crest_id = cha_owner.getClanCrestLargeId();
            decoy_cw_level = 0;
        }

        if (cha_owner.isMounted()) 
		{
            decoy_mount_id = cha_owner.getMountNpcId() + 1000000;
            decoy_mount_type = (byte) cha_owner.getMountType();
        } 
		else 
		{
            decoy_mount_id = 0;
            decoy_mount_type = 0;
        }

        if (decoy_clan_id > 0 && cha_owner.getClan() != null)
            decoy_clan_rep_score = cha_owner.getClan().getReputationScore();
        else
            decoy_clan_rep_score = 0;

        decoy_fishing = cha_owner.isFishing() ? (byte) 1 : (byte) 0;
        decoy_fishLoc = cha_owner.getFishLoc();
        decoy_swimSpd = cha_owner.getSwimSpeed();
        decoy_private_store = (byte) cha_owner.getPrivateStoreType(); // 1 - sellshop
        decoy_inv = cha_owner.getInventory();
        decoy_race = cha_owner.getBaseTemplate().race.ordinal();
        decoy_sex = cha_owner.getSex();
        decoy_base_class = cha_owner.getBaseClassId();
        decoy_attack_speed = cha_owner.getAttackSpeedMultiplier();
        decoy_hair_style = cha_owner.getHairStyle();
        decoy_hair_color = cha_owner.getHairColor();
        decoy_face = cha_owner.getFace();
        decoy_sitting = cha_owner.isSitting() ? 0 : 1;
        decoy_cubics = cha_owner.getCubics().toArray(new L2Cubic[cha_owner.getCubics().size()]);
		decoy_rec_left = cha_owner.getRecommendation().getRecomLeft();
		decoy_rec_have = cha_owner.isGM() ? 0 : cha_owner.getRecommendation().getRecomHave();
        decoy_class_id = cha_owner.getClassId().getId();
        decoy_noble = cha_owner.isNoble() ? 1 : 0;
        decoy_hero = cha_owner.isHero() || cha_owner.isGM() && ConfigValue.GMHeroAura ? 1 : 0; // 0x01: Hero Aura
        decoy_NameColor = cha_owner.getNameColor();
        decoy_PledgeClass = cha_owner.getPledgeClass();
        decoy_pledge_type = cha_owner.getPledgeType();
        decoy_TitleColor = cha_owner.getTitleColor();
        decoy_Transformation = cha_owner.getTransformation();
        decoy_Agathion = cha_owner.getAgathion() != null ? cha_owner.getAgathion().getId() : 0;

		curCP = (int) cha_owner.getCurrentCp();
		curHP = (int) cha_owner.getCurrentHp();
		maxHP = cha_owner.getMaxHp();
		curMP = (int) cha_owner.getCurrentMp();
		maxMP = cha_owner.getMaxMp();

    }

    private void writeImpl_Decoy() 
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

        writeC(0x31);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z + ConfigValue.ClientZShift);
        writeD(_loc.h);
        writeD(_npcObjId);
        writeS(_name);
        writeD(decoy_race);
        writeD(decoy_sex);
        writeD(decoy_base_class);

		writePaperdollInfo(decoy_inv, false, false, false);

        for (byte PAPERDOLL_ID : PAPERDOLL_ORDER)
            writeD(decoy_inv.getPaperdollAugmentationId(PAPERDOLL_ID));

        writeD(0x00); // ?GraciaFinal
        writeD(0x00); // ?GraciaFinal

        writeD(pvp_flag);
        writeD(karma);

		if(getClient().isLindvior())
		{
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_RHAND)); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LHAND)); // Tauti
			writeD(0); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_GLOVES)); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_CHEST)); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LEGS)); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_FEET)); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_HAIR)); // Tauti
			writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_DHAIR)); // Tauti
		}

        writeD(_mAtkSpd);
        writeD(_pAtkSpd);

        writeD(0x00);

        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(decoy_swimSpd); // swimspeed
        writeD(decoy_swimSpd); // swimspeed
        writeD(_runSpd/*_flRunSpd*/);
        writeD(_walkSpd/*_flWalkSpd*/);
        writeD(_runSpd/*_flyRunSpd*/);
        writeD(_walkSpd/*_flyWalkSpd*/);
        writeF(move_speed);
        writeF(decoy_attack_speed);
        writeF(colRadius);
        writeF(colHeight);
        writeD(decoy_hair_style);
        writeD(decoy_hair_color);
        writeD(decoy_face);
        writeS(_title);
        writeD(decoy_clan_id);
        writeD(clan_crest_id);
        writeD(decoy_ally_id);
        writeD(ally_crest_id);

        //writeD(0);

        writeC(decoy_sitting);
        writeC(running);
        writeC(incombat);
        writeC(dead);
        writeC(0);
        writeC(decoy_mount_type);
        writeC(decoy_private_store);
        writeH(decoy_cubics.length);
        for (L2Cubic cubic : decoy_cubics)
            writeH(cubic == null ? 0 : cubic.getId());
        writeC(0x00); // find party members
		if(!getClient().isLindvior())
			writeD(_abnormalEffect);
        writeC(decoy_rec_left);
        writeH(decoy_rec_have);
        writeD(decoy_mount_id);
        writeD(decoy_class_id);
        writeD(0); // ?

		writeEnchant(_cha, activeChar.send_visual_enchant);

        writeC(team);
        writeD(decoy_large_clan_crest_id);
        writeC(decoy_noble);
        writeC(decoy_hero);

        writeC(decoy_fishing);
        writeD(decoy_fishLoc.x);
        writeD(decoy_fishLoc.y);
        writeD(decoy_fishLoc.z);

        writeD(decoy_NameColor);
        writeD(_loc.h);
        writeD(decoy_PledgeClass);
        writeD(decoy_pledge_type);
        writeD(decoy_TitleColor);
        writeD(decoy_cw_level);
        writeD(decoy_clan_rep_score);
        writeD(decoy_Transformation);
        writeD(decoy_Agathion);

        writeD(0x01); // T2

		if(getClient().isLindvior())
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(curCP);
			writeD(curHP);
			writeD(maxHP);
			writeD(curMP);
			writeD(maxMP);
			writeD(0x00);
			writeD(0x00);
			writeC(0x00);

			writeD(_abnormalEffects.length);
			for(AbnormalVisualEffect abnormal : _abnormalEffects)
				writeD(abnormal.ordinal());

			writeC(0x00); // writeD(_territoryId > 0 ? 0x50 + _territoryId : 0);
		}
		else
			writeD(_abnormalEffect2);
    }

    public static final byte[] PAPERDOLL_ORDER = 
	{
            Inventory.PAPERDOLL_UNDER,
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

    @Override
    public String getType() 
	{
        return super.getType() + (_cha != null ? " about " + _cha : "");
    }
}