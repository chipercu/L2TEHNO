package l2open.gameserver.model;

import l2open.gameserver.geodata.GeoBlock;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.geodata.GeoCollision;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExEventMatchFenceInfo;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.*;

public class L2Fence extends L2Object
{
	public GeoBlock[] geo_collision = new GeoBlock[4];
	public int fence_state=1;
	public int ordinatus;
	public int abscissa;

	public L2Fence(int obj_id, int or, int ab)
	{
		super(obj_id, false);
		ordinatus = or;
		abscissa = ab;
	}

	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}

	public void updateAbnormalEffect()
	{
		updateState();
	}

	public void updateState()
	{
		for(L2Player _cha : L2World.getAroundPlayers(this))
			_cha.sendPacket(new ExEventMatchFenceInfo(getObjectId(), fence_state, getX(), getY(), getZ(), ordinatus, abscissa));
	}

	public L2GameServerPacket newCharInfo()
	{
		return new ExEventMatchFenceInfo(getObjectId(), fence_state, getX(), getY(), getZ(), ordinatus, abscissa);
	}

	@Override
	public boolean isFence()
	{
		return true;
	}

	public void createGeoCollision()
	{
		geo_collision[0] = new GeoBlock();
		geo_collision[1] = new GeoBlock();
		geo_collision[2] = new GeoBlock();
		geo_collision[3] = new GeoBlock();
		// -----------------------------------------------------
		int loc_x1 = getX()+(abscissa+20)/2;
		int loc_x2 = getX()-(abscissa+20)/2;
		int loc_y1 = getY()+(ordinatus+20)/2;
		int loc_y2 = getY()-(ordinatus+20)/2;

		int zmin = getZ()-50;
		int zmax = getZ()+50;
		// -----------------------1------------------------------
		L2Territory pos1 = new L2Territory(getObjectId()*2);
		pos1.add(loc_x1, loc_y1+2, zmin, zmax);
		pos1.add(loc_x1, loc_y1-2, zmin, zmax);
		pos1.add(loc_x2, loc_y1-2, zmin, zmax);
		pos1.add(loc_x2, loc_y1+2, zmin, zmax);
		geo_collision[0].setGeoPos(pos1);
		// -----------------------2------------------------------
		L2Territory pos2 = new L2Territory(getObjectId()*2+1);
		pos2.add(loc_x2+2, loc_y1, zmin, zmax);
		pos2.add(loc_x2-2, loc_y1, zmin, zmax);
		pos2.add(loc_x2-2, loc_y2, zmin, zmax);
		pos2.add(loc_x2+2, loc_y2, zmin, zmax);
		geo_collision[1].setGeoPos(pos2);
		// -----------------------3------------------------------
		L2Territory pos3 = new L2Territory(getObjectId()*2+2);
		pos3.add(loc_x2, loc_y2+2, zmin, zmax);
		pos3.add(loc_x2, loc_y2-2, zmin, zmax);
		pos3.add(loc_x1, loc_y2-2, zmin, zmax);
		pos3.add(loc_x1, loc_y2+2, zmin, zmax);
		geo_collision[2].setGeoPos(pos3);
		// -----------------------4------------------------------
		L2Territory pos4 = new L2Territory(getObjectId()*2+3);
		pos4.add(loc_x1+2, loc_y2, zmin, zmax);
		pos4.add(loc_x1-2, loc_y2, zmin, zmax);
		pos4.add(loc_x1-2, loc_y1, zmin, zmax);
		pos4.add(loc_x1+2, loc_y1, zmin, zmax);
		geo_collision[3].setGeoPos(pos4);
	}

	public void applyGeoCollision()
	{
		GeoEngine.applyGeoCollision(geo_collision[0], getReflection().getGeoIndex());
		GeoEngine.applyGeoCollision(geo_collision[1], getReflection().getGeoIndex());
		GeoEngine.applyGeoCollision(geo_collision[2], getReflection().getGeoIndex());
		GeoEngine.applyGeoCollision(geo_collision[3], getReflection().getGeoIndex());
	}

	public void removeGeoCollision()
	{
		if(geo_collision[0].getGeoAround() != null)
			GeoEngine.removeGeoCollision(geo_collision[0], getReflection().getGeoIndex());
		if(geo_collision[1].getGeoAround() != null)
			GeoEngine.removeGeoCollision(geo_collision[1], getReflection().getGeoIndex());
		if(geo_collision[2].getGeoAround() != null)
			GeoEngine.removeGeoCollision(geo_collision[2], getReflection().getGeoIndex());
		if(geo_collision[3].getGeoAround() != null)
			GeoEngine.removeGeoCollision(geo_collision[3], getReflection().getGeoIndex());
	}
}
