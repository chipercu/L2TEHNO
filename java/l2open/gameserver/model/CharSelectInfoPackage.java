package l2open.gameserver.model;

import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.PcInventory;

/**
 * Used to Store data sent to Client for Character
 * Selection screen.
 *
 * @version $Revision: 1.2.2.2.2.4 $ $Date: 2005/03/27 15:29:33 $
 */
public class CharSelectInfoPackage
{
	private String _name;
	private int _objectId = 0;
	private int _charId = 0x00030b7a;
	private long _exp = 0;
	private int _sp = 0;
	private int _clanId = 0;
	private int _race = 0;
	private int _classId = 0;
	private int _baseClassId = 0;
	private int _deleteTimer = 0;
	private long _lastAccess = 0L;
	private int _face = 0;
	private int _hairStyle = 0;
	private int _hairColor = 0;
	private int _sex = 0;
	private int _level = 1;
	private int _karma = 0, _pk = 0, _pvp = 0;
	private int _maxHp = 0;
	private double _currentHp = 0;
	private int _maxMp = 0;
	private double _currentMp = 0;
	private int[][] _paperdoll;
	private int _accesslevel = 0;
	private int _x = 0, _y = 0, _z = 0;
	private int _vitalityPoints = 0; 

	/**
	 * @param int1
	 */
	public CharSelectInfoPackage(int objectId, String name)
	{
		setObjectId(objectId);
		_name = name;
		_paperdoll = PcInventory.restoreVisibleInventory(objectId);
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public void setObjectId(int objectId)
	{
		_objectId = objectId;
	}

	public int getCharId()
	{
		return _charId;
	}

	public void setCharId(int charId)
	{
		_charId = charId;
	}

	public int getClanId()
	{
		return _clanId;
	}

	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}

	public int getClassId()
	{
		return _classId;
	}

	public int getBaseClassId()
	{
		return _baseClassId;
	}

	public void setBaseClassId(int baseClassId)
	{
		_baseClassId = baseClassId;
	}

	public void setClassId(int classId)
	{
		_classId = classId;
	}

	public double getCurrentHp()
	{
		return _currentHp;
	}

	public void setCurrentHp(double currentHp)
	{
		_currentHp = currentHp;
	}

	public double getCurrentMp()
	{
		return _currentMp;
	}

	public void setCurrentMp(double currentMp)
	{
		_currentMp = currentMp;
	}

	public int getDeleteTimer()
	{
		return _deleteTimer;
	}

	public void setDeleteTimer(int deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}

	public long getLastAccess()
	{
		return _lastAccess;
	}

	public void setLastAccess(long lastAccess)
	{
		_lastAccess = lastAccess;
	}

	public long getExp()
	{
		return _exp;
	}

	public void setExp(long exp)
	{
		_exp = exp;
	}

	public int getFace()
	{
		return _face;
	}

	public void setFace(int face)
	{
		_face = face;
	}

	public int getHairColor()
	{
		return _hairColor;
	}

	public void setHairColor(int hairColor)
	{
		_hairColor = hairColor;
	}

	public int getHairStyle()
	{
		return _hairStyle;
	}

	public void setHairStyle(int hairStyle)
	{
		_hairStyle = hairStyle;
	}

	public int getPaperdollObjectId(int slot)
	{
		return _paperdoll[slot][0];
	}

	public int getPaperdollItemId(int slot)
	{
		return _paperdoll[slot][1];
	}

	public int getLevel()
	{
		return _level;
	}

	public void setLevel(int level)
	{
		_level = level;
	}

	public int getMaxHp()
	{
		return _maxHp;
	}

	public void setMaxHp(int maxHp)
	{
		_maxHp = maxHp;
	}

	public int getMaxMp()
	{
		return _maxMp;
	}

	public void setMaxMp(int maxMp)
	{
		_maxMp = maxMp;
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public int getRace()
	{
		return _race;
	}

	public void setRace(int race)
	{
		_race = race;
	}

	public int getSex()
	{
		return _sex;
	}

	public void setSex(int sex)
	{
		_sex = sex;
	}

	public int getSp()
	{
		return _sp;
	}

	public void setSp(int sp)
	{
		_sp = sp;
	}

	public int getEnchantEffect()
	{
		return _paperdoll[Inventory.PAPERDOLL_RHAND][2];
	}

	public int getKarma()
	{
		return _karma;
	}

	public void setKarma(int karma)
	{
		_karma = karma;
	}

	public int getAccessLevel()
	{
		return _accesslevel;
	}

	public void setAccessLevel(int accesslevel)
	{
		_accesslevel = accesslevel;
	}

	public int getX()
	{
		return _x;
	}

	public void setX(int x)
	{
		_x = x;
	}

	public int getY()
	{
		return _y;
	}

	public void setY(int y)
	{
		_y = y;
	}

	public int getZ()
	{
		return _z;
	}

	public void setZ(int z)
	{
		_z = z;
	}

	public int getPk()
	{
		return _pk;
	}

	public void setPk(int pk)
	{
		_pk = pk;
	}

	public int getPvP()
	{
		return _pvp;
	}

	public void setPvP(int pvp)
	{
		_pvp = pvp;
	}

	public void setVitalityPoints(int points) 
	{ 
		_vitalityPoints = points; 
	} 
       
	public int getVitalityPoints() 
    { 
		return _vitalityPoints; 
    } 
}