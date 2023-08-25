package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Summon;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.tables.PetDataTable;
import l2open.util.Location;

/**
 * Дамп пакета с оффа, 828 протокол:
 * 0000: b2 01 00 00 00 f9 a1 50 4c 3f 79 0f 00 00 00 00    .......PL?y.....
 * 0010: 00 a7 4e 02 00 71 62 00 00 a8 f7 ff ff 01 a0 00    ..N..qb.........
 * 0020: 00 00 00 00 00 4d 01 00 00 16 01 00 00 a0 00 00    .....M..........
 * 0030: 00 50 00 00 00 a0 00 00 00 50 00 00 00 a0 00 00    .P.......P......
 * 0040: 00 50 00 00 00 a0 00 00 00 50 00 00 00 9a 99 99    .P.......P......
 * 0050: 99 99 99 f1 3f 81 43 a8 52 b3 07 f0 3f 00 00 00    ....?.C.R...?...
 * 0060: 00 00 00 34 40 00 00 00 00 00 00 45 40 00 00 00    ...4@......E@...
 * 0070: 00 00 00 00 00 00 00 00 00 01 01 00 00 01 00 00    ................
 * 0080: 43 00 79 00 63 00 00 00 01 00 00 00 00 00 00 00    C.y.c...........
 * 0090: 00 00 00 00 00 00 00 00 00 00 00 00 a8 13 00 00    ................
 * 00a0: a8 13 00 00 60 07 00 00 60 07 00 00 00 00 00 00    ....`...`.......
 * 00b0: 4a 00 00 00 b9 ef 81 29 00 00 00 00 00 00 00 00    J......)........
 * 00c0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00    ................
 * 00d0: ee d4 00 00 da 07 00 00 ff 01 00 00 98 03 00 00    ................
 * 00e0: df 01 00 00 74 00 00 00 6f 00 00 00 50 00 00 00    ....t...o...P...
 * 00f0: b0 00 00 00 16 01 00 00 4d 01 00 00 00 00 00 00    ........M.......
 * 0100: 00 00 00 00 00 00 05 00 00 00 02 00 00 00 00 00    ................
 * 0110: 00 00                                              ..
 *
 * rev 828	dddddddddddddddddddffffdddcccccSSdddddddddddQQQddddddddddddd
 */
public class PetInfo extends L2GameServerPacket
{
	private int _runSpd, _walkSpd, MAtkSpd, PAtkSpd, pvp_flag, karma, rideable, _ownerId;
	private int _type, obj_id, npc_id, runing, incombat, dead, _sp, level, _abnormalEffect, _abnormalEffect2;
	private int curFed, maxFed, curHp, maxHp, curMp, maxMp, curLoad, maxLoad;
	private int PAtk, PDef, MAtk, MDef, Accuracy, Evasion, Crit, team, sps, ss, type, _showSpawnAnimation = 1;
	private Location _loc;
	private float col_redius, col_height;
	private long exp, exp_this_lvl, exp_next_lvl;
	private String _name, title;

	private AbnormalVisualEffect[] _abnormalEffects;

	public PetInfo(L2Summon summon)
	{
		if(summon.getPlayer() == null)
			return;
		_ownerId = summon.getPlayer().getObjectId();
		_type = summon.getSummonType();
		obj_id = summon.getObjectId();
		npc_id = summon.getTemplate().npcId;
		_loc = summon.getLoc();
		MAtkSpd = (int)summon.getMAtkSpd();
		PAtkSpd = (int)summon.getPAtkSpd();
		_runSpd = summon.getRunSpeed();
		_walkSpd = summon.getWalkSpeed();
		col_redius = summon.getColRadius();
		col_height = summon.getColHeight();
		runing = summon.isRunning() ? 1 : 0;
		incombat = summon.isInCombat() ? 1 : 0;
		dead = summon.isAlikeDead() ? 1 : 0;

		if(summon.getPlayer() != null && summon.getPlayer().getEventMaster() != null)
		{
			_name =  summon.getPlayer().getEventMaster().getCharName(summon.getPlayer());
			title = summon.getPlayer().getEventMaster().getCharTitle(summon.getPlayer());
		}
		else
		{
			_name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();
			title = summon.getTitle();
		}

		pvp_flag = summon.getPvpFlag();
		karma = summon.getKarma();
		curFed = summon.getCurrentFed();
		maxFed = summon.getMaxFed();
		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		_sp = summon.getSp();
		level = summon.getLevel();
		exp = summon.getExp();
		exp_this_lvl = summon.getExpForThisLevel();
		exp_next_lvl = summon.getExpForNextLevel();
		curLoad = summon.isPet() ? summon.getInventory().getTotalWeight() : 0;
		maxLoad = summon.getMaxLoad();
		PAtk = summon.getPAtk(null);
		PDef = summon.getPDef(null);
		MAtk = summon.getMAtk(null, null);
		MDef = summon.getMDef(null, null);
		Accuracy = summon.getAccuracy();
		Evasion = summon.getEvasionRate(null);
		Crit = summon.getCriticalHit(null, null);
		_abnormalEffect = summon.getAbnormalEffect();
		_abnormalEffect2 = summon.getAbnormalEffect2();
		_abnormalEffects = summon.getAbnormalEffectsArray();
		// В режиме трансформации значек mount/dismount не отображается
		if(summon.getPlayer() != null && summon.getPlayer().getTransformation() != 0)
			rideable = 0; //not rideable
		else
			rideable = (PetDataTable.getInstance().getInfo(npc_id, level)== null || !PetDataTable.getInstance().getInfo(npc_id, level).isMountable()) ? 0 : 1;

		if(summon.getPlayer() == null || summon.getPlayer().getEventMaster() == null || summon.getPlayer().getEventMaster().sendVisualTeam(summon.getPlayer()))
			team = summon.getTeam();
		else
			team = 0;

		ss = summon.getSoulshotConsumeCount();
		sps = summon.getSpiritshotConsumeCount();

		type = summon.getFormId();
	}

	public PetInfo(L2Summon summon, int showSpawnAnimation)
	{
		this(summon);
		_showSpawnAnimation = showSpawnAnimation;
	}

	@Override
	protected final void writeImpl()
	{
		if(obj_id == 0)
			return;
		writeC(0xb2);
		writeD(_type);
		writeD(obj_id);
		writeD(npc_id + 1000000);
		writeD(0); // 1=attackable
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
		writeD(0);
		writeD(MAtkSpd);
		writeD(PAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_runSpd/*_swimRunSpd*/);
		writeD(_walkSpd/*_swimWalkSpd*/);
		writeD(_runSpd/*_flRunSpd*/);
		writeD(_walkSpd/*_flWalkSpd*/);
		writeD(_runSpd/*_flyRunSpd*/);
		writeD(_walkSpd/*_flyWalkSpd*/);
		writeF(1/*_cha.getProperMultiplier()*/);
		writeF(1/*_cha.getAttackSpeedMultiplier()*/);
		writeF(col_redius);
		writeF(col_height);
		writeD(0); // right hand weapon
		writeD(0);
		writeD(0); // left hand weapon
		writeC(1); // name above char 1=true ... ??
		writeC(runing); // running=1
		writeC(incombat); // attacking 1=true
		writeC(dead); // dead 1=true
		writeC(_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeD(-1); // High Five NPCString ID 
		writeS(_name);
		writeD(-1); // High Five NPCString ID 
		writeS(title);
		writeD(1);
		writeD(pvp_flag); //0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
		writeD(karma); // hmm karma ??
		writeD(curFed); // how fed it is
		writeD(maxFed); //max fed it can be
		writeD(curHp); //current hp
		writeD(maxHp); // max hp
		writeD(curMp); //current mp
		writeD(maxMp); //max mp
		writeD(_sp); //sp
		writeD(level);// lvl
		writeQ(exp);
		writeQ(exp_this_lvl); // 0%  absolute value
		writeQ(exp_next_lvl); // 100% absoulte value
		writeD(curLoad); //weight
		writeD(maxLoad); //max weight it can carry
		writeD(PAtk);//patk
		writeD(PDef);//pdef
		writeD(MAtk);//matk
		writeD(MDef);//mdef
		writeD(Accuracy);//accuracy
		writeD(Evasion);//evasion
		writeD(Crit);//critical
		writeD(_runSpd);//speed
		writeD(PAtkSpd);//atkspeed
		writeD(MAtkSpd);//casting speed
		writeD(_abnormalEffect); //c2  abnormal visual effect... bleed=1; poison=2; bleed?=4;
		writeH(rideable);
		writeC(0); // c2
		writeH(0); // ??
		writeC(team); // team aura (1 = blue, 2 = red)
		writeD(ss);
		writeD(sps);
		writeD(type);
		writeD(_abnormalEffect2);
	}

	@Override
	protected boolean writeImplLindvior()
	{
		if(obj_id == 0)
			return true;
		writeC(0xb2);
		writeD(_type);
		writeD(obj_id);
		writeD(npc_id + 1000000);
		writeD(0); // 1=attackable
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
		writeD(0);
		writeD(MAtkSpd);
		writeD(PAtkSpd);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_runSpd/*_swimRunSpd*/);
		writeD(_walkSpd/*_swimWalkSpd*/);
		writeD(_runSpd/*_flRunSpd*/);
		writeD(_walkSpd/*_flWalkSpd*/);
		writeD(_runSpd/*_flyRunSpd*/);
		writeD(_walkSpd/*_flyWalkSpd*/);
		writeF(1/*_cha.getProperMultiplier()*/);
		writeF(1/*_cha.getAttackSpeedMultiplier()*/);
		writeF(col_redius);
		writeF(col_height);
		writeD(0); // right hand weapon
		writeD(0);
		writeD(0); // left hand weapon
		writeC(_ownerId); // name above char 1=true ... ??
		writeC(runing); // running=1
		writeC(incombat); // attacking 1=true
		writeC(dead); // dead 1=true
		writeC(_showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
		writeD(-1); // High Five NPCString ID 
		writeS(_name);
		writeD(-1); // High Five NPCString ID 
		writeS(title);
		writeD(1);
		writeD(pvp_flag); //0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
		writeD(karma); // hmm karma ??
		writeD(curFed); // how fed it is
		writeD(maxFed); //max fed it can be
		writeD(curHp); //current hp
		writeD(maxHp); // max hp
		writeD(curMp); //current mp
		writeD(maxMp); //max mp
		writeD(_sp); //sp
		writeD(level);// lvl
		writeQ(exp);
		writeQ(exp_this_lvl); // 0%  absolute value
		writeQ(exp_next_lvl); // 100% absoulte value
		writeD(curLoad); //weight
		writeD(maxLoad); //max weight it can carry
		writeD(PAtk);//patk
		writeD(PDef);//pdef
		writeD(Accuracy);//accuracy
		writeD(Evasion);//evasion
		writeD(Crit);//critical
		writeD(MAtk);//matk
		writeD(MDef);//mdef
		writeD(0x00);//evasion
		writeD(0x00);//accuracy
		writeD(0x00);//critical
		writeD(_runSpd);//speed
		writeD(PAtkSpd);//atkspeed
		writeD(MAtkSpd);//casting speed
		writeD(rideable);
		writeC(0); // c2
		writeC(team); // team aura (1 = blue, 2 = red)
		writeD(ss);
		writeD(sps);
		writeD(type);
		writeD(0x00); // transform id
		writeD(0x00); // sum points
		writeD(0x00); // max sum points

		writeD(_abnormalEffects.length);
		for(AbnormalVisualEffect abnormal : _abnormalEffects)
			writeD(abnormal.ordinal());
 
		writeC(0x00);
		return true;
	}
}