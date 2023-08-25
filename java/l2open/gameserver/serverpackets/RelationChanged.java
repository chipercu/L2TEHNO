package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelationChanged extends L2GameServerPacket
{
	public static final int RELATION_PARTY1 = 0x00001; // party member
	public static final int RELATION_PARTY2 = 0x00002; // party member
	public static final int RELATION_PARTY3 = 0x00004; // party member
	public static final int RELATION_PARTY4 = 0x00008; // party member (for information, see L2PcInstance.getRelation())
	public static final int RELATION_PARTYLEADER = 0x00010; // true if is party leader
	public static final int RELATION_HAS_PARTY = 0x00020; // true if is in party
	public static final int RELATION_CLAN_MEMBER = 0x00040; // true if is in clan
	public static final int RELATION_LEADER = 0x00080; // true if is clan leader
	public static final int RELATION_CLAN_MATE = 0x00100; // true if is in same clan
	public static final int RELATION_INSIEGE = 0x00200; // true if in siege
	public static final int RELATION_ATTACKER = 0x00400; // true when attacker
	public static final int RELATION_ALLY = 0x00800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 0x01000; // true when red icon, doesn't matter with blue
	public static final int RELATION_MUTUAL_WAR = 0x04000; // double fist
	public static final int RELATION_1SIDED_WAR = 0x08000; // single fist
	public static final int RELATION_ALLY_MEMBER = 0x10000; // clan is in alliance
	public static final int RELATION_TERRITORY_WAR = 0x80000; // Territory Wars

	protected final List<RelationChangedData> _data;

	protected RelationChanged(int s)
	{
		_data = new ArrayList<RelationChangedData>(s);
	}

	protected void add(RelationChangedData data)
	{
		_data.add(data);
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xCE);
		writeD(_data.size());
		for(RelationChangedData data : _data)
		{
			writeD(data.charObjId);
			writeD(data.relation);
			writeD(data.isAutoAttackable ? 1 : 0);
			writeD(data.karma);
			writeD(data.pvpFlag);
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xCE);
		writeD(_data.size());
		for(RelationChangedData data : _data)
		{
			writeD(data.charObjId);
			writeD(data.relation);
			writeD(data.isAutoAttackable ? 1 : 0);
			writeD(-data.karma);
			writeD(data.pvpFlag);
		}
		return true;
	}

	static class RelationChangedData
	{
		public final L2Playable cha_i;
		//public final L2Player activChar;
		public final int charObjId;
		public final boolean isAutoAttackable;
		public final int relation, karma, pvpFlag;

		public RelationChangedData(L2Playable cha, boolean _isAutoAttackable, int _relation, L2Player activChar_)
		{
			isAutoAttackable = _isAutoAttackable;
			relation = _relation;
			charObjId = cha.getObjectId();
			karma = cha.getKarma();
			pvpFlag = cha.getPvpFlag();
			cha_i = cha;
			//activChar = activChar_;
		}

		/*public String toString()
		{
			return "RelationChangedData: [char='"+cha_i.getName()+"', relation="+relation+", isAutoAttackable="+isAutoAttackable+", karma="+karma+", pvpFlag="+pvpFlag+"] to [char='"+activChar.getName()+"']";
		}*/
	}

	/**
	 * @param targetChar игрок, отношение к которому изменилось
	 * @param activeChar игрок, которому будет отослан пакет с результатом
	 */
	public static Collection<L2GameServerPacket> update(L2Player sendTo, L2Player targetChar, L2Player activeChar)
	{
		if(targetChar == null || activeChar == null || targetChar.isInOfflineMode())
			return null;

		Collection<L2GameServerPacket> ret = new GArray<L2GameServerPacket>(2);
		L2Summon pet = targetChar.getPet();
		int relation = targetChar.getRelation(activeChar);

		if(targetChar.getTerritorySiege() > -1 || activeChar.getTerritorySiege() > -1)
		{
			targetChar.sendPacket(new ExDominionWarStart(activeChar));
			activeChar.sendPacket(new ExDominionWarStart(targetChar));
		}

	//	Util.test();
		RelationChanged pkt = new RelationChanged(1);
		if(pet != null)
			pkt.add(new RelationChangedData(pet, targetChar.isAutoAttackable(activeChar), 0, activeChar));
		if(sendTo != null)
			pkt.add(new RelationChangedData(targetChar, targetChar.isAutoAttackable(activeChar), relation, activeChar));
		ret.add(pkt);
		
		return ret;
	}
}