package l2open.gameserver.model;

import l2open.config.*;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.loader.XmlPcParameterLoader;
import l2open.util.GArray;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
/**
 * Character Sub-Class<?> Definition
 * <BR>
 * Used to store key information about a character's sub-class.
 *
 * @author Tempy
 */
public class L2SubClass
{
	static final Logger _log = Logger.getLogger(L2SubClass.class.getName());

	public static final int CERTIFICATION_65 = 1 << 0;
	public static final int CERTIFICATION_70 = 1 << 1;
	public static final int CERTIFICATION_75 = 1 << 2;
	public static final int CERTIFICATION_80 = 1 << 3;
	
	private int _class;
	private long _exp;
	private long maxExp = Experience.LEVEL[Experience.LEVEL.length - 1];
	private int _sp;
	private int _certification;
	private static final byte minLevel = 1;
	private byte _level;
	private byte _maxLevel = 80;
	private double _Hp = 1, _Mp = 1, _Cp = 1;
	private boolean _active, _isBase, _isBase2;
	private L2Player _player;
	private DeathPenalty _dp;
	private double _max_hp;
	private double _max_mp;
	private double _max_cp;

	private void updateStat()
	{
		_max_hp = XmlPcParameterLoader.getInstance().getMaxHp(_class, _level);
		_max_mp = XmlPcParameterLoader.getInstance().getMaxMp(_class, _level);
		_max_cp = XmlPcParameterLoader.getInstance().getMaxCp(_class, _level);
	}
	public L2SubClass(byte level, long exp)
	{
		_level = level;
		_exp = exp;
		updateStat();
	}

	public int getClassId()
	{
		return _class;
	}

	public long getExp()
	{
		return _exp;
	}

	public long getMaxExp()
	{
		return maxExp;
	}

	public void addExp(long val)
	{
		setExp(_exp + val);
	}

	public long getSp()
	{
		return Math.min(_sp, Integer.MAX_VALUE);
	}

	public void addSp(long val)
	{
		setSp(_sp + val);
	}

	public byte getLevel()
	{
		return _level;
	}

	public void setClassId(int classId)
	{
		_class = classId;
		updateStat();
	}

	public void setExp(long val)
	{
		if(val < 0)
			_exp = 0;
		else if(val <= maxExp)
			_exp = val;
	}

	public void setSp(long spValue)
	{
		spValue = Math.max(spValue, 0);
		spValue = Math.min(spValue, Integer.MAX_VALUE);
		_sp = (int) spValue;
	}

	public void setHp(double hpValue)
	{
		_Hp = hpValue;
	}

	public double getHp()
	{
		return _Hp;
	}

	public void setMp(final double mpValue)
	{
		_Mp = mpValue;
	}

	public double getMp()
	{
		return _Mp;
	}

	public void setCp(final double cpValue)
	{
		_Cp = cpValue;
	}

	public double getCp()
	{
		return _Cp;
	}

	public boolean setLevel(byte val)
	{
		if(val > _maxLevel)
		{
			_level = _maxLevel;
			_exp = maxExp;
		}
		else if(val < minLevel)
			_level = minLevel;
		else
			_level = val;
		updateStat();
		return _level == val;
	}

	public boolean incLevel()
	{
		return setLevel((byte) (_level + 1));
	}

	public boolean decLevel()
	{
		return setLevel((byte) (_level - 1));
	}

	public void setActive(final boolean active)
	{
		_active = active;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setBase(final boolean base)
	{
		_isBase = base;
		_maxLevel = (_isBase || ConfigValue.Multi_Enable || _isBase2) ? (byte) Experience.getMaxLevel() : (byte) Experience.getMaxSubLevel();
		maxExp = Experience.LEVEL[_maxLevel + 1] - 1;
	}

	public void setBase2(final boolean base2)
	{
		_isBase2 = base2;
		_maxLevel = (_isBase || ConfigValue.Multi_Enable || _isBase2) ? (byte) Experience.getMaxLevel() : (byte) Experience.getMaxSubLevel();
		maxExp = Experience.LEVEL[_maxLevel + 1] - 1;
	}

	public boolean isBase()
	{
		return _isBase;
	}

	public boolean isBase2()
	{
		return _isBase2;
	}

	public DeathPenalty getDeathPenalty()
	{
		if(_dp == null)
			_dp = new DeathPenalty(_player, (byte) 0);
		return _dp;
	}

	public void setDeathPenalty(DeathPenalty dp)
	{
		_dp = dp;
	}
	
	public int getCertification()
	{
		return _certification;
	}

	public void setCertification(int certification)
	{
		_certification = certification;
	}

	public void addCertification(int c)
	{
		_certification |= c;
	}

	public boolean isCertificationGet(int v)
	{
		return (_certification & v) == v;
	}

	public void setPlayer(L2Player player)
	{
		_player = player;
	}

	public double getMaxHp()
	{
		return _max_hp;
	}

	public double getMaxMp()
	{
		return _max_mp;
	}

	public double getMaxCp()
	{
		return _max_cp;
	}

	@Override
	public String toString()
	{
		return ClassId.values()[_class].toString() + " " + _level;
	}
}