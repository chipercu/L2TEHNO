package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;

import java.util.Collection;
import java.util.List;

/**
 * @author ALF
 * @date 17.07.2012
 */
public class ExAcquirableSkillListByClass extends L2GameServerPacket
{
	
	private Collection<L2SkillLearn> allskills;
	private L2Player _player;

	public ExAcquirableSkillListByClass(L2Player player)
	{
		_player = player;
		if(player != null)
			allskills = player.getAvailableSkillsLind(player.getClassId());
	}
	
	@Override
	protected final void writeImpl()
	{
		if(allskills == null)
			return;
		writeEx(0xFA);

		writeD(allskills.size());
		for(L2SkillLearn sk : allskills)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(sk.getId(), sk.getLevel());
			
			if(skill == null)
				continue;

			//_log.info("ExAcquirableSkillListByClass: ["+sk.getId()+"]["+sk.getLevel()+"]");
			writeD(sk.getId());
			writeD(sk.getLevel());
			writeD(sk.getSpCost());
			writeH(sk.getMinLevel());
			writeH(0x00); // writeH(sk.getDualClassMinLvl()); // Dual-class min level.
			boolean consumeItem = sk.getItemId() > 0;
			writeD(consumeItem ? 1 : 0);
			if (consumeItem)
			{
				writeD(sk.getItemId());
				writeQ(sk.getItemCount());
			}

			List<L2Skill> delete_skills = sk.getRemovedSkillsForPlayer(_player);
			writeD(delete_skills.size());
            for(L2Skill skill2 : delete_skills)
			{
                writeD(skill2.getId());// skillId
                writeD(skill2.getLevel());// skillLvl
			}
		}
	}
}
/**
public class ExAcquirableSkillListByClass extends L2GameServerPacket
{
    private Player player;
    private Collection<SkillLearn> skills;
    private List<Require> _reqs = Collections.emptyList();

    public ExAcquirableSkillListByClass(Player player)
	{
        this.player = player;
        skills = new ArrayList<SkillLearn>();
        for(SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL, true))
            if(skill.getSpCost() != 0)
                skills.add(skill);
    }

    @Override
    protected final void writeImpl()
	{
        writeEx(0xFA);

       writeD(skills.size());
       for (SkillLearn skillLearn : skills)
	   {
            writeD(skillLearn.getId());// skill id
            writeD(skillLearn.getLevel());// skill level
            writeD(skillLearn.getSpCost());// sp_cost
            writeH(skillLearn.getMinLevel());// Required Level
            writeH(0);//Glory Days      //479 протокол
            writeD(_reqs.size());
            for (Require temp : _reqs)
			{
                writeD(temp.itemId);
                writeQ(temp.count);
            }

            writeD(skillLearn.getRemovedSkillsForPlayer(player).size());// deletedSkillsSize
            for (Skill skill : skillLearn.getRemovedSkillsForPlayer(player))
			{
                writeD(skill.getId());// skillId
                writeD(skill.getLevel());// skillLvl
            }
        }
    }

    private static class Require
	{
        public int itemId;
        public long count;

        @SuppressWarnings("unused")
        public Require(int pItemId, long pCount)
		{
            itemId = pItemId;
            count = pCount;
        }
    }
}
**/