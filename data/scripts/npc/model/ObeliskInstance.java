package npc.model;

import java.util.HashMap;

import l2open.gameserver.geodata.GeoCollision;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2RoundTerritory;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public class ObeliskInstance extends L2MonsterInstance implements GeoCollision
{
	public ObeliskInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		int id = IdFactory.getInstance().getNextId();
		L2Territory pos = new L2RoundTerritory(id, -245825, 217075, 230, -12208, -12000);
		setGeoPos(pos);
		GeoEngine.applyGeoCollision(this, getReflection().getGeoIndex());
	}

	private L2Territory geoPos;
	private byte[][] geoAround;

	public L2Territory getGeoPos()
	{
		return geoPos;
	}

	public void setGeoPos(L2Territory value)
	{
		geoPos = value;
	}

	public byte[][] getGeoAround()
	{
		return geoAround;
	}

	public void setGeoAround(byte[][] value)
	{
		geoAround = value;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public boolean isConcrete()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}