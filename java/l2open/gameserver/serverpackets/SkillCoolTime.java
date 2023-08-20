package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.SkillTimeStamp;

import java.util.*;

/**
 * @author: Diagod
 */
public class SkillCoolTime extends L2GameServerPacket
{
	private List<CoolTime> _list = Collections.emptyList();

	public SkillCoolTime(L2Player player)
	{
		Collection<SkillTimeStamp> list = player.getSkillReuseTimeStamps().values();
		_list = new ArrayList<CoolTime>(list.size());

		for(SkillTimeStamp stamp : list)
		{
			if(stamp.hasNotPassed())
			{
				int reuseCurrent = Math.round(stamp.getReuseCurrent() / 1000);
				
				if(reuseCurrent == 0)
					continue;
				else if(ConfigValue.SkillReuseType == 0)
				{
					L2Skill skill = player.getKnownSkill(stamp.getSkill());
					if(reuseCurrent == 0 || skill == null || skill.getLevel() != stamp.getLevel())
						continue;
				}
				CoolTime sk = new CoolTime();
				sk.skill_id = stamp.getSkill();
				sk.skill_lvl = stamp.getLevel();
				sk.skill_r_base = Math.round(stamp.getReuseBasic() / 1000);
				sk.skill_r_current = reuseCurrent;
				_list.add(sk);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xc7);
		writeD(_list.size());
		for(int i = 0;i < _list.size();i++)
		{
			CoolTime sk = _list.get(i);
			writeD(sk.skill_id);
			writeD(sk.skill_lvl);
			writeD(sk.skill_r_base);
			writeD(sk.skill_r_current);
		}
	}

	private static class CoolTime
	{
		public int skill_id;
		public int skill_lvl;
		public int skill_r_base;
		public int skill_r_current;
	}
}