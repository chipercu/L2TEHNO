package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Cubic;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.util.Location;
import l2open.util.Util;

import java.util.logging.Logger;

public class CharInfo extends L2GameServerPacket
{
	private static final Logger _log = Logger.getLogger(CharInfo.class.getName());

	private L2Player _cha;
	private Inventory _inv;
	private int _mAtkSpd, _pAtkSpd;
	private int _runSpd, _walkSpd, _swimSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd;
	private Location _loc, _fishLoc;
	private String _name, _title;
	private int _objId, _race, _sex, base_class, pvp_flag, karma, rec_have;
	private float speed_move, speed_atack, col_radius, col_height;
	private int hair_style, hair_color, face, _abnormalEffect, _abnormalEffect2;
	private int clan_id, clan_crest_id, large_clan_crest_id, ally_id, ally_crest_id, class_id;
	private byte _sit, _run, _combat, _dead, private_store;
	private byte _noble, _hero, _fishing, mount_type;
	private int plg_class, pledge_type, clan_rep_score, cw_level, mount_id;
	private int _nameColor, _title_color, _transform, _agathion;
	private L2Cubic[] cubics;
	private boolean can_writeImpl = false;
	private boolean partyRoom = false;
	private boolean isFlying = false;
	private int _territoryId;
	private int curHP, maxHP, curMP, maxMP, curCP;

	private AbnormalVisualEffect[] _abnormalEffects;

	protected boolean logHandled()
	{
		return true;
	}

	public CharInfo(L2Player cha)
	{
		if((_cha = cha) == null || _cha.isInvisible() || _cha.isDeleting())
			return;

		_territoryId = _cha.getTerritorySiege();
		partyRoom = PartyRoomManager.getInstance().isLeader(_cha);
		rec_have = _cha.isGM() ? 0 : _cha.getRecommendation().getRecomHave();
		_hero = _cha.isHero() || _cha.isGM() && ConfigValue.GMHeroAura ? (byte) 1 : (byte) 0; // 0x01: Hero Aura
		_nameColor = _cha.getNameColor(); // New C5
		// Проклятое оружие и трансформации для ТВ скрывают имя и все остальные опознавательные знаки
		if(_cha.getTransformationName() != null || _cha.getReflection().getId() < 0 && _cha.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			_name = _cha.getTransformationName() != null ? _cha.getTransformationName() : _cha.getName();
			_title = "";
			clan_id = 0;
			clan_crest_id = 0;
			ally_id = 0;
			ally_crest_id = 0;
			large_clan_crest_id = 0;
			if(_cha.isCursedWeaponEquipped())
				cw_level = CursedWeaponsManager.getInstance().getLevel(_cha.getCursedWeaponEquippedId());
		}
		else if(ConfigValue.LastHeroChangeName && cha.isInEvent() == 2)
		{
			_name = ConfigValue.LastHeroName;
			_title = ConfigValue.LastHeroTitle;
			_title_color = 0xFFFFFF;
			_nameColor = 255;
			_hero = 1;
			clan_id = 0;
			clan_crest_id = 0;
			ally_id = 0;
			ally_crest_id = 0;
			large_clan_crest_id = 0;
			cw_level = 0;
		}
		else if(_cha.getEventMaster() != null)
		{
			_name =  _cha.getEventMaster().getCharName(_cha);
			_title = _cha.getEventMaster().getCharTitle(_cha);
			_nameColor =  _cha.getEventMaster().getCharNameColor(_cha);
			_title_color =  _cha.getEventMaster().getCharTitleColor(_cha);
			_hero =  _cha.getEventMaster().getHeroAura(_cha);

			rec_have = 0;
			_territoryId = 0;
			partyRoom = false;

			if(cha.isInEvent() == 11)
			{
				_hero = 0;
				clan_id = 0;
				clan_crest_id = 0;
				ally_id = 0;
				ally_crest_id = 0;
				large_clan_crest_id = 0;
				cw_level = 0;
			}
			else
			{
				clan_id = _cha.getEventMaster().getClanId(_cha);
				clan_crest_id = _cha.getClanCrestId();
				ally_id = _cha.getAllyId();
				ally_crest_id = _cha.getAllyCrestId();
				large_clan_crest_id = _cha.getClanCrestLargeId();
				cw_level = 0;
			}
		}
		else if(ConfigValue.BotNoName && _cha.isBot())
		{
			_name="";
			_title="";
		}
		else
		{
			_name = _cha.getName();
			clan_id = _cha.getClanId();
			clan_crest_id = _cha.getClanCrestId();
			ally_id = _cha.getAllyId();
			ally_crest_id = _cha.getAllyCrestId();
			large_clan_crest_id = _cha.getClanCrestLargeId();
			cw_level = 0;
			if(_cha.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
			{
				if(ConfigValue.NotShowCrestOnTrade)
				{
					clan_id=0;
					ally_id=0;
					clan_crest_id=0;
					ally_crest_id=0;
					large_clan_crest_id=0;
				}
				if(_cha.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUFF)
				{
					_title = _cha._buf_title;
					_title_color = ConfigValue.OfflineBuffTitleColor;
					_nameColor = ConfigValue.OfflineBuffNameColor;
				}
				else
					_title = "";
			}
			else if(!_cha.isConnected())
			{
				_title = "Разрыв";
				_title_color = 255;
			}
			else
			{
				_title = _cha.getTitle();
				_title_color = _cha.getTitleColor();
			}
		}

		if(_cha.isMounted())
		{
			mount_id = _cha.getMountNpcId() + 1000000;
			mount_type = (byte) _cha.getMountType();
		}
		else
		{
			mount_id = 0;
			mount_type = 0;
		}

		curCP = (int) _cha.getCurrentCp();
		curHP = (int) _cha.getCurrentHp();
		maxHP = _cha.getMaxHp();
		curMP = (int) _cha.getCurrentMp();
		maxMP = _cha.getMaxMp();
		
		_inv = _cha.getInventory();
		_mAtkSpd = (int)_cha.getMAtkSpd();
		_pAtkSpd = (int)_cha.getPAtkSpd();
		speed_move = _cha.getMovementSpeedMultiplier();
		speed_atack = _cha.getAttackSpeedMultiplier();

		_runSpd = _cha.getTemplate().baseRunSpd;//(int) (_cha.getRunSpeed() / speed_move);
		_walkSpd = _cha.getTemplate().baseWalkSpd;//(int) (_cha.getWalkSpeed() / speed_move);

		_flRunSpd = 0; // TODO
		_flWalkSpd = 0; // TODO

		if(_cha.isFlying())
		{
			_flyRunSpd = _runSpd;
			_flyWalkSpd = _walkSpd;
		}
		else
		{
			_flyRunSpd = 0;
			_flyWalkSpd = 0;
		}

		_swimSpd = ConfigValue.SwimingSpeedTemplate; //_cha.getSwimSpeed();
		_loc = _cha.getLoc();
		_objId = _cha.getObjectId();
		_race = _cha.getBaseTemplate().race.ordinal();
		_sex = _cha.getSex();
		base_class = _cha.getClassRace();
		pvp_flag = _cha.getPvpFlag();
		karma = _cha.getKarma();
		col_radius = _cha.getColRadius();
		col_height = _cha.getColHeight();
		hair_style = _cha.getHairStyle();
		hair_color = _cha.getHairColor();
		face = _cha.getFace();
		if(clan_id > 0 && _cha.getClan() != null)
			clan_rep_score = _cha.getClan().getReputationScore();
		else
			clan_rep_score = 0;
		_sit = _cha.isSitting() ? (byte) 0 : (byte) 1; // standing = 1 sitting = 0
		_run = _cha.isRunning() ? (byte) 1 : (byte) 0; // running = 1 walking = 0
		_combat = _cha.isInCombat() ? (byte) 1 : (byte) 0;
		_dead = _cha.isAlikeDead() ? (byte) 1 : (byte) 0;
		private_store = (byte) _cha.getPrivateStoreType(); // 1 - sellshop
		cubics = _cha.getCubics().toArray(new L2Cubic[_cha.getCubics().size()]);
		_abnormalEffect = _cha.getAbnormalEffect();
		_abnormalEffect2 = _cha.getAbnormalEffect2();
		_abnormalEffects = _cha.getAbnormalEffectsArray();
		class_id = _cha.getClassId().getId();

		_noble = _cha.isNoble() ? (byte) 1 : (byte) 0; // 0x01: symbol on char menu ctrl+I
		_fishing = _cha.isFishing() ? (byte) 1 : (byte) 0;
		_fishLoc = _cha.getFishLoc();
		plg_class = _cha.getPledgeClass();
		pledge_type = _cha.getPledgeType();
		_transform = _cha.getTransformation();
		_agathion = _cha.getAgathion() != null ? _cha.getAgathion().getId() : 0;
		isFlying = _cha.isInFlyingTransform();

		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getObjectId() == _cha.getObjectId())
		{
			_log.severe("You cant send CharInfo about his character to active user!!!");
			Thread.dumpStack();
			return;
		}

		writeC(0x31);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + ConfigValue.ClientZShift);
		writeD(0x00/*_loc.h*/); // _clanBoatObjectId
		writeD(_objId);
		writeS(_name);
		writeD(_race);
		writeD(_sex);
		writeD(base_class);

		writePaperdollInfo(_inv, false, activeChar.send_visual_id, activeChar.disable_cloak);

		for(byte PAPERDOLL_ID : PAPERDOLL_ORDER)
			writeD(_inv.getPaperdollAugmentationId(PAPERDOLL_ID));

		writeD(0x01); // talismans
		writeD(0x00); // openCloak

		writeD(pvp_flag);
		writeD(karma);

		writeD(_mAtkSpd);
		writeD(_pAtkSpd);

		writeD(0x00);

		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimSpd/* 0x32 */); // swimspeed
		writeD(_swimSpd/* 0x32 */); // swimspeed
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(speed_move); // _cha.getProperMultiplier()
		writeF(speed_atack); // _cha.getAttackSpeedMultiplier()
		writeF(col_radius);
		writeF(col_height);
		writeD(hair_style);
		writeD(hair_color);
		writeD(face);
		writeS(_title);
		writeD(clan_id);
		writeD(clan_crest_id);
		writeD(ally_id);
		writeD(ally_crest_id);
		writeC(_sit);
		writeC(_run);
		writeC(_combat);
		writeC(_dead);
		writeC(0x00); // is invisible
		writeC(mount_type); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		writeC(private_store);
		writeH(cubics.length);
		for(L2Cubic cubic : cubics)
			writeH(cubic == null ? 0 : cubic.getId());
		writeC(partyRoom ? 0x01 : 0x00); // find party members
		writeD(_abnormalEffect);
		writeC(isFlying ? 0x02 : 0x00);
		writeH(rec_have);
		writeD(mount_id);
		writeD(class_id);
		writeD(0x00);

		writeEnchant(_cha, activeChar.send_visual_enchant);

		if(_cha.getEventMaster() == null || _cha.getEventMaster().sendVisualTeam(_cha))
		{
			if(_cha.getTeam() < 3)
				writeC((byte) _cha.getTeam()); // team circle around feet 1 = Blue, 2 = red
			else if(activeChar.getTeam() == 0)
				writeC(0);
			else
				writeC(activeChar.getTeam() == _cha.getTeam() ? 1 : 2);
		}
		else
			writeC(0);

		writeD(large_clan_crest_id);
		writeC(_noble);
		writeC(_hero);

		writeC(_fishing);
		writeD(_fishLoc.x);
		writeD(_fishLoc.y);
		writeD(_fishLoc.z);

		writeD(_nameColor);
		writeD(_loc.h);
		writeD(plg_class);
		writeD(pledge_type);
		writeD(_title_color);
		writeD(cw_level);
		writeD(clan_rep_score);
		writeD(_transform);
		writeD(_agathion);

		writeD(_cha.hasSetFame() ? 0x01 : 0x00);

		writeD(_abnormalEffect2);
		writeD(_territoryId > 0 ? 0x50 + _territoryId : 0);
		writeD(0x00); // ?
		writeD(0x00); // ?
	}

	@Override
	protected boolean writeImplLindvior()
	{
		L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return true;

        if (_objId == 0)
            return true;

        if (activeChar.getObjectId() == _objId)
		{
            _log.severe("You cant send CharInfo about his character to active user!!!");
            return true;
        }

        writeC(0x31);
        writeD(_loc.x);
        writeD(_loc.y);
		writeD(_loc.z + ConfigValue.ClientZShift);
		writeD(0x00); // writeD(_clanBoatObjectId);
        writeD(_objId);
        writeS(_name);
        writeD(_race);
        writeD(_sex);
        writeD(base_class);

		writePaperdollInfo(_inv, false, true, false);

		for(byte PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			writeH(_inv.getPaperdollAugmentationId(PAPERDOLL_ID));
			writeH(0x00);
		}

       /* for (int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
            writeD(_inv[PAPERDOLL_ID][0]);
        }
        for (int PAPERDOLL_ID : PAPERDOLL_ORDER) {
            writeH(_inv[PAPERDOLL_ID][1]);
            writeH(0x00);
        }*/

        writeD(0x01); // TODO talisman count(VISTALL)
        writeD(0x00); // TODO cloak status(VISTALL)

        writeD(pvp_flag);
        writeD(-karma);

		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_RHAND)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LHAND)); // Tauti
		writeD(0); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_GLOVES)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_CHEST)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LEGS)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_FEET)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_HAIR)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_DHAIR)); // Tauti

        writeD(_mAtkSpd);
        writeD(_pAtkSpd);

        writeD(0x00);

        writeD(_runSpd);
        writeD(_walkSpd);
        writeD(_swimSpd);
        writeD(_swimSpd);
        writeD(_flRunSpd);
        writeD(_flWalkSpd);
        writeD(_flyRunSpd);
        writeD(_flyWalkSpd);

        writeF(speed_move); // _cha.getProperMultiplier()
        writeF(speed_atack); // _cha.getAttackSpeedMultiplier()
        writeF(col_radius);
        writeF(col_height);
        writeD(hair_style);
        writeD(hair_color);
        writeD(face);
        writeS(_title);
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);

        writeC(_sit);
        writeC(_run);
        writeC(_combat);
        writeC(_dead);
        writeC(0x00); // is invisible
        writeC(mount_type); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no
        // mount
        writeC(private_store);
        writeH(cubics.length);
       for(L2Cubic cubic : cubics)
			writeH(cubic == null ? 0 : cubic.getId());
		writeC(partyRoom ? 0x01 : 0x00); // find party members
		writeC(isFlying ? 0x02 : 0x00);
        writeH(rec_have);
        writeD(mount_id);
        writeD(class_id);
        writeD(0x00);

		writeEnchant(_cha, activeChar.send_visual_enchant);

       	if(_cha.getEventMaster() == null || _cha.getEventMaster().sendVisualTeam(_cha))
		{
			if(_cha.getTeam() < 3)
				writeC((byte) _cha.getTeam()); // team circle around feet 1 = Blue, 2 = red
			else if(activeChar.getTeam() == 0)
				writeC(0);
			else
				writeC(activeChar.getTeam() == _cha.getTeam() ? 1 : 2);
		}
		else
			writeC(0);


        writeD(large_clan_crest_id);
        writeC(_noble);
        writeC(_hero);

        writeC(_fishing);
        writeD(_fishLoc.x);
        writeD(_fishLoc.y);
        writeD(_fishLoc.z);

        writeD(_nameColor);
        writeD(_loc.h);
        writeD(plg_class);
        writeD(pledge_type);
        writeD(_title_color);
        writeD(cw_level);
        writeD(clan_rep_score);
        writeD(_transform);
        writeD(_agathion);

        writeD(0x01); // T2
        /*START: Структура написана от балды, чтобы соответствовать размеру пакета:*/
        writeD(0x00); // TAUTI
        writeD(0x00); // TAUTI
        writeD(0x00); // TAUTI
        writeD(curCP); // TAUTI
        writeD(curHP); // TAUTI
        writeD(maxHP); // TAUTI
        writeD(curMP); // TAUTI
        writeD(maxMP); // TAUTI
        writeD(0x00); // TAUTI
        writeD(0x00); // TAUTI
        writeC(0x00); // TAUTI
        /*END: Структура написана от балды, чтобы соответствовать размеру пакета:*/

		writeD(_abnormalEffects.length);
		for(AbnormalVisualEffect abnormal : _abnormalEffects)
			writeD(abnormal.ordinal());

        writeC(0x00); // TAUTI

		/*if(!can_writeImpl)
			return true;

		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return true;

		if(activeChar.equals(_cha))
		{
			_log.severe("You cant send CharInfo about his character to active user!!!");
			Thread.dumpStack();
			return true;
		}

		writeC(0x31);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + ConfigValue.ClientZShift);*/
		//writeD(0x00/*_loc.h*/); // _clanBoatObjectId
		/*writeD(_objId);
		writeS(_name);
		writeD(_race);
		writeD(_sex);
		writeD(base_class);

		writePaperdollInfo(_inv, false, true, false);

		for(byte PAPERDOLL_ID : PAPERDOLL_ORDER)
			writeD(_inv.getPaperdollAugmentationId(PAPERDOLL_ID));

		writeD(0x01); // talismans
		writeD(0x00); // openCloak

		writeD(pvp_flag);
		writeD(karma);

		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_RHAND)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LHAND)); // Tauti
		writeD(0); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_GLOVES)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_CHEST)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LEGS)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_FEET)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_HAIR)); // Tauti
		writeD(0); // writeD(inv.getVisualItemId(Inventory.PAPERDOLL_DHAIR)); // Tauti

		writeD(_mAtkSpd);
		writeD(_pAtkSpd);

		writeD(0x00);

		writeD(_runSpd);
		writeD(_walkSpd);*/
		//writeD(_swimSpd/* 0x32 */); // swimspeed
		//writeD(_swimSpd/* 0x32 */); // swimspeed
		/*writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(speed_move); // _cha.getProperMultiplier()
		writeF(speed_atack); // _cha.getAttackSpeedMultiplier()
		writeF(col_radius);
		writeF(col_height);
		writeD(hair_style);
		writeD(hair_color);
		writeD(face);
		writeS(_title);
		writeD(clan_id);
		writeD(clan_crest_id);
		writeD(ally_id);
		writeD(ally_crest_id);
		writeC(_sit);
		writeC(_run);
		writeC(_combat);
		writeC(_dead);
		writeC(0x00); // is invisible
		writeC(mount_type); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		writeC(private_store);
		writeH(cubics.length);
		for(L2Cubic cubic : cubics)
		{
			writeH(cubic == null ? 0 : cubic.getId());
		}
		writeC(partyRoom ? 0x01 : 0x00); // find party members
		writeC(isFlying ? 0x02 : 0x00);
		writeH(rec_have);
		writeD(mount_id);
		writeD(class_id);
		writeD(0x00);

		writeEnchant(_cha, activeChar.send_visual_enchant);

		if(_cha.getTeam() < 3)
			writeC((byte) _cha.getTeam()); // team circle around feet 1 = Blue, 2 = red
		else if(activeChar.getTeam() == 0)
			writeC(0);
		else
			writeC(activeChar.getTeam() == _cha.getTeam() ? 1 : 2);

		writeD(large_clan_crest_id);
		writeC(_noble);
		writeC(_hero);

		writeC(_fishing);
		writeD(_fishLoc.x);
		writeD(_fishLoc.y);
		writeD(_fishLoc.z);

		writeD(_nameColor);
		writeD(_loc.h);
		writeD(plg_class);
		writeD(pledge_type);
		writeD(_title_color);
		writeD(cw_level);
		writeD(clan_rep_score);
		writeD(_transform);
		writeD(_agathion);

		writeD(_cha.hasSetFame() ? 0x01 : 0x00);

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

		writeD(0x00);*/
		/*
		if(_abnormalEffect == 0 && _abnormalEffect2 == 0)
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

		//writeC(0x00); // writeD(_territoryId > 0 ? 0x50 + _territoryId : 0);
		return true;
	}

	public String getType()
	{
        return "[S] CharInfo["+_name+"]";
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
}