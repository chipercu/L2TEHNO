package npc.model.birthday;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;

public class BirthDayCakeInstance extends L2NpcInstance
{
	private static final int  BIRTHDAY_CAKE_24 = 106;
	private static final int  BIRTHDAY_CAKE = 139;

	private static final L2Skill SKILL = SkillTable.getInstance().getInfo(22035, 1);
	private static final L2Skill SKILL_24 = SkillTable.getInstance().getInfo(22250, 1);

	private class CastTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(getNpcId() == BIRTHDAY_CAKE)
			{
				for(L2Player player : L2World.getAroundPlayers(BirthDayCakeInstance.this, SKILL.getAffectRange(), 100))
				{
						if(player.getEffectList().getEffectsBySkill(SKILL) != null)
							continue;

						SKILL.getEffects(BirthDayCakeInstance.this, player, false, false);
				}
			}
			else if(getNpcId() == BIRTHDAY_CAKE_24)
			{
				final L2Player player = (L2Player) getSummoner();
				if (player == null)
				{
					ThreadPoolManager.getInstance().schedule(this, 1000);
					return;
				}

				if (!player.isInParty())
				{
					if (player.isInRange(BirthDayCakeInstance.this, SKILL_24.getCastRange()))
					{
						SKILL_24.getEffects(BirthDayCakeInstance.this, player, false, false);
					}
				}
				else
				{
					for (L2Player member : player.getParty().getPartyMembers())
					{
						if ((member != null) && member.isInRange(BirthDayCakeInstance.this, SKILL_24.getCastRange()))
						{
							SKILL_24.getEffects(BirthDayCakeInstance.this, member, false, false);
						}
					}
				}
			}
		}
	}

	public BirthDayCakeInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new CastTask(), 1000L, 1000L);
	}
}
