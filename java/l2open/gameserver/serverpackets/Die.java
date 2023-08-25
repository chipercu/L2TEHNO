package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2MonsterInstance;

/**
 * Пример:
 * 00
 * 8b 22 90 48 objectId
 * 01 00 00 00
 * 00 00 00 00
 * 00 00 00 00
 * 00 00 00 00
 * 00 00 00 00
 * 00 00 00 00
 * 00 00 00 00
 * format  dddddddd   rev 828
 */
public class Die extends L2GameServerPacket
{
	private int _chaId;
	private boolean _fake;
	private boolean _sweepable;
	private int _access;
	private L2Clan _clan;
	private L2Character _cha;
	private int to_hideaway, to_castle, to_siege_HQ, to_fortress;
	private int to_agathion = 0;
	private int to_village = 1;

	/**
	 * @param _characters
	 */
	public Die(L2Character cha)
	{
		_cha = cha;
		if(cha.isPlayer())
		{
			L2Player player = (L2Player) cha;
			_access = player.getPlayerAccess().ResurectFixed || ((player.getInventory().getCountOf(10649) > 0 || player.getInventory().getCountOf(13300) > 0) && !player.isOnSiegeField()) ? 0x01 : 0x00;
			to_agathion = player.isAgathionResAvailable() ? 0x01 : 0x00;
			_clan = player.getClan();
		}
		_chaId = cha.getObjectId();
		_fake = !cha.isDead();
		if(cha.isMonster())
			_sweepable = ((L2MonsterInstance) cha).isSweepActive();

		if(_clan != null)
		{
			SiegeClan siegeClan = null;
			
			if(_cha.getEventMaster() == null || _cha.getEventMaster()._ref == null || _cha.getEventMaster()._ref.getId() != _cha.getReflectionId())
			{
				Siege siege = SiegeManager.getSiege(_cha, true);
				if(siege != null)
					siegeClan = siege.getAttackerClan(_clan);

				if(TerritorySiege.checkIfInZone(_cha))
					siegeClan = TerritorySiege.getSiegeClan(_clan);
			}
			else if(_cha.getEventMaster().siege_event && (_cha.getEventMaster()._defender_clan == null || _cha.getEventMaster()._defender_clan.getClanId() != _cha.getClanId()))
				siegeClan = _cha.getEventMaster().getSiegeClan(_cha.getPlayer());

			to_hideaway = _clan.getHasHideout() > 0 ? 0x01 : 0x00;
			to_castle = _clan.getHasCastle() > 0 ? 0x01 : 0x00;
			to_siege_HQ = siegeClan != null && siegeClan.getHeadquarter() != null ? 0x01 : 0x00;
			to_fortress = _clan.getHasFortress() > 0 ? 0x01 : 0x00;
		}
		else
		{
			to_hideaway = 0;
			to_castle = 0;
			to_siege_HQ = 0;
			to_fortress = 0;
		}

		if(cha.isPlayer() && cha.getPlayer().isRestartPoint() > 0)
		{
			to_village = 0;
			to_hideaway = 0;
			to_castle = 0;
			to_siege_HQ = 0;
			to_fortress = 0;
			_access = 0;
			to_agathion = 0;
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(_fake)
			return;

		writeC(0x00);
		writeD(_chaId);
		writeD(to_village); // to nearest village
		writeD(to_hideaway); // to hide away
		writeD(to_castle); // to castle
		writeD(to_siege_HQ); // to siege HQ
		writeD(_sweepable ? 0x01 : 0x00); // sweepable  (blue glow)
		writeD(_access); // FIXED
		writeD(to_fortress); // fortress
		writeC(0x00); //show die animation
		writeD(to_agathion);//agathion ress button
	    writeD(0x00); //additional free space
	}
}