package l2open.gameserver.skills;

/**
 * Simple class containing all neccessary information to maintain
 * valid timestamps and reuse for skills upon relog. Filter this
 * carefully as it becomes redundant to store reuse for small delays.
 * @author Yesod
 */
public class SkillTimeStamp
{
	private int skill;
	private int skill_lvl;
	private long reuse;
	private long endTime;
	public int class_id;

	public SkillTimeStamp(int _skill, int _skill_lvl, long _endTime, long _reuse, int _class_id)
	{
		skill = _skill;
		skill_lvl = _skill_lvl;
		reuse = _reuse;
		endTime = _endTime;
		class_id = _class_id;
	}

	public int getSkill()
	{
		return skill;
	}

	public int getLevel()
	{
		return skill_lvl;
	}

	public long getReuseBasic()
	{
		if(reuse == 0)
			return getReuseCurrent();
		return reuse;
	}

	/**
	 * Возвращает оставшееся время реюза в миллисекундах.
	 */
	public long getReuseCurrent()
	{
		return Math.max(endTime - System.currentTimeMillis(), 0);
	}

	public long getEndTime()
	{
		return endTime;
	}

	/* Check if the reuse delay has passed and
	 * if it has not then update the stored reuse time
	 * according to what is currently remaining on
	 * the delay. */
	public boolean hasNotPassed()
	{
		return System.currentTimeMillis() < endTime;
	}
}