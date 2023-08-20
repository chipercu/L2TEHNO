package l2open.gameserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class L2SkillLearn
{
	public final short id;
	public final short skillLevel;
	public final int _spCost;
	public final int _repCost;
	public final byte minLevel;
	public final short itemId;
	public final int itemCount;
	public final int type;
	public final boolean common;
	public final boolean clan;
	public final boolean clansquad;
	public final boolean transformation;
	public final boolean certification;
    private final List<Integer> delete_skills;
    private final List<Integer> incompatible_skills;
    private final List<Integer> delete_vailability_skills;

	// not needed, just for easier debug
	public final String name;

	public L2SkillLearn(short _id, short lvl, byte minLvl, String _name, int cost, short _itemId, int _itemCount, int class_id, List<Integer> _delete_skills, List<Integer> _incompatible_skills, List<Integer> _delete_vailability_skills)
	{
		id = _id;
		skillLevel = lvl;
		minLevel = minLvl;
		name = _name.intern();
		if(class_id == -2 || class_id == -3)
		{
			_spCost = 0;
			_repCost = cost;
		}
		else if(class_id == -4)
		{
			_repCost = 0;
			_spCost = 0;
		}
		else
		{
			_repCost = 0;
			_spCost = cost;
		}
		itemId = _itemId;
		itemCount = _itemCount;
		common = class_id == -1;
		clan = class_id == -2;
		clansquad = class_id == -3;
		transformation = class_id == -4;
		certification = class_id == -10;
		type = class_id;
		delete_skills = _delete_skills;
		incompatible_skills = _incompatible_skills;
		delete_vailability_skills = _delete_vailability_skills;
	}

	public short getId()
	{
		return id;
	}

	public short getLevel()
	{
		return skillLevel;
	}

	public byte getMinLevel()
	{
		return minLevel;
	}

	public String getName()
	{
		return name;
	}

	public int getSpCost()
	{
		return _spCost;
	}

	public short getItemId()
	{
		return itemId;
	}

	public int getItemCount() //TODO: long
	{
		return itemCount;
	}

	public int getRepCost()
	{
		return _repCost;
	}

	public List<Integer> getDeleteSkills()
	{
		return Collections.unmodifiableList(delete_skills);
	}

	public List<Integer> getIncompatibleSkills()
	{
		return Collections.unmodifiableList(incompatible_skills);
	}

	public List<Integer> getVailabilitySkills()
	{
		return Collections.unmodifiableList(delete_vailability_skills);
	}

	public boolean canLearnSkill(L2Skill skill)
	{
		return !incompatible_skills.contains(skill.getId());
	}

	public boolean canLearnSkill(L2Player player)
	{
		for(L2Skill skill : player.getAllSkills())
			if(incompatible_skills.contains(skill.getId()))
				return false;
		if(getVailabilitySkills().size() > 0)
			for(int skill_id : getVailabilitySkills())
				if(player.getKnownSkill(skill_id) == null)
					return false;
		return true;
	}

	public List<L2Skill> getRemovedSkillsForPlayer(L2Player player)
	{
        List<L2Skill> skills = new ArrayList<L2Skill>();
        for(int skill_id : getDeleteSkills())
            if(player.getKnownSkill(skill_id) != null)
                skills.add(player.getKnownSkill(skill_id));

		/*for(int skill_id : getVailabilitySkills())
			if(player.getKnownSkill(skill_id) != null)
				skills.add(player.getKnownSkill(skill_id));*/
        return skills;
    }

	public boolean deleteSkills(L2Player player)
	{
		List<L2Skill> delete_skills = getRemovedSkillsForPlayer(player);
		if(!delete_skills.isEmpty())
			for(L2Skill del_skill : delete_skills)
				player.removeSkill(del_skill, true, true);
		return true;
	}

	@Override
	public String toString()
	{
		return "SkillLearn for " + name + " id " + id + " level " + skillLevel;
	}
}