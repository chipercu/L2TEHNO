package ai.SeedOfAnnihilation;

import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.util.concurrent.ScheduledFuture;

/**
 * @author: Drizzy
 * @date: 13.05.2012
 * @time: 17:01
 * АИ для магвенов. Сделан по офф скриптам.
 */
public class Maguen extends Fighter
{
	private ScheduledFuture<?> FIRST_TIMER = null;
	private ScheduledFuture<?> SECOND_TIMER = null;
	private ScheduledFuture<?> THIRD_TIMER = null;
	private ScheduledFuture<?> FORTH_TIMER = null;
	private ScheduledFuture<?> END_TIMER = null;
	private ScheduledFuture<?> FOLLOW_TASK = null;
	private boolean clearTask = true;
	private L2Character cha;
	private L2Character player;
	public String SPAWN = ""; 

	public Maguen(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for(L2Character ch : L2World.getAroundCharacters(getActor(), 1000, 300))
			if(ch.getName().equals(SPAWN))
				cha = ch;
		if(cha != null)
		{
			getActor().setTitle(cha.getName());
			getActor().setRunning();

			if(cha.isPlayer())
			{
				ExShowScreenMessage sm = new ExShowScreenMessage(1801149, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true);
				cha.sendPacket(sm);
			}
			FOLLOW_TASK = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowTask(), 1000, 1000);
		}
		ThreadPoolManager.getInstance().schedule(new distcheckTimer(), 1000);
	}

	public class FollowTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			if(clearTask)
				actor.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, cha, 100);
		}
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		final L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(skill.getId() == 9060 && cha != null && caster == cha)
		{
			clearTask = false;
			if(FIRST_TIMER != null)
				FIRST_TIMER.cancel(true);
			if(SECOND_TIMER != null)
				SECOND_TIMER.cancel(true);
			if(THIRD_TIMER != null)
				THIRD_TIMER.cancel(true);
			if(FORTH_TIMER != null)
				FORTH_TIMER.cancel(true);
			if(FOLLOW_TASK != null)
				FOLLOW_TASK.cancel(true);

			if(caster.getPlayer() != null)
				player = caster.getPlayer();

			if(actor.getNpcState() == 1)
			{
				AddEffect(caster, 6367);
			}
			else if(actor.getNpcState() == 2)
			{
				AddEffect(caster, 6368);
			}
			else if(actor.getNpcState() == 3)
			{
				AddEffect(caster, 6369);
			}
		}
		super.onEvtSeeSpell(skill, caster);
	}

	//Добавляем эффект кастеру.
	private void AddEffect(L2Character caster, int skillId)
	{
		GArray<L2Effect> effect = caster.getEffectList().getEffectsBySkillId(skillId);
		if(effect != null)
		{
			int level = effect.get(0).getSkill().getLevel();
			if(level < 3)
			{
				getActor().doCast(SkillTable.getInstance().getInfo(skillId, level + 1), caster, true);
			}
		}
		else
		{
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
			if(skill != null)
			{
				getActor().doCast(SkillTable.getInstance().getInfo(skillId, 1), caster, true);
			}
		}
	}

	//Добавляем эффект парти кастующего.
	private void AddPartyEffect(L2Character caster, int skillId, int lvl)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, lvl);
		if(caster.getPlayer().getParty() != null)
		{
			for(L2Player member : caster.getPlayer().getParty().getPartyMembers())
			{
				skill.getEffects(getActor(), member, false, false);
			}
		}
		else
			skill.getEffects(getActor(), caster, false, false);
		getActor().broadcastSkill(new MagicSkillUse(getActor(), caster, skill.getId(), lvl, skill.getHitTime(), 0));
	}

	//Удаляем эффект с кастующего.
	private void DeleteEffect(L2Character caster, int skillId)
	{
		GArray<L2Effect> effect = caster.getEffectList().getEffectsBySkillId(skillId);
		if(effect != null)
		{
			effect.get(0).exit(true, false);
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		int i1;
		int i2;
		int i3;
		if(player != null)
		{
			if(player.getEffectList().getEffectByStackType(SkillAbnormalType.soa_buff1) == null)
				i1 = 0;
			else
				i1 = player.getEffectList().getEffectByStackType(SkillAbnormalType.soa_buff1).getAbnormalLv();

			if(player.getEffectList().getEffectByStackType(SkillAbnormalType.soa_buff2) == null)
				i2 = 0;
			else
				i2 = player.getEffectList().getEffectByStackType(SkillAbnormalType.soa_buff2).getAbnormalLv();

			if(player.getEffectList().getEffectByStackType(SkillAbnormalType.soa_buff3) == null)
				i3 = 0;
			else
				i3 = player.getEffectList().getEffectByStackType(SkillAbnormalType.soa_buff3).getAbnormalLv();

			if(i1 == 3 && i2 == 0 && i3 == 0)
			{
				player.sendMessage(new CustomMessage("ai.Maguen.Bistakon", player));
				DeleteEffect(player, 6367);
				if(Rnd.chance(70))
					AddPartyEffect(player, 6343, 1);
				else
					AddPartyEffect(player, 6343, 2);

				//Ревард магвена.
				int i4 = Rnd.get(10000);
				int i5 = Rnd.get(20);
				if(i4 == 0 && i5 != 0)
				{
					if(player.isPlayer())
					{
						player.getPlayer().getInventory().addItem(15488, 1);
					}
				}
				else if(i4 == 0 && i5 == 0)
				{
					if(player.isPlayer())
					{
						player.getPlayer().getInventory().addItem(15489, 1);
					}
				}
				if(END_TIMER != null)
				{
					END_TIMER.cancel(true);
					END_TIMER = null;
				}
				END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(getActor()), 3000);
			}
			else if(i1 == 0 && i2 == 3 && i3 == 0)
			{
				player.sendMessage(new CustomMessage("ai.Maguen.Cocrakon", player));
				DeleteEffect(player, 6368);
				if(Rnd.chance(70))
					AddPartyEffect(player, 6365, 1);
				else
					AddPartyEffect(player, 6365, 2);

				//Ревард магвена.
				int i4 = Rnd.get(10000);
				int i5 = Rnd.get(20);
				if(i4 == 0 && i5 != 0)
				{
					if(player.isPlayer())
					{
						player.getPlayer().getInventory().addItem(15488, 1);
					}
				}
				else if(i4 == 0 && i5 == 0)
				{
					if(player.isPlayer())
					{
						player.getPlayer().getInventory().addItem(15489, 1);
					}
				}
				if(END_TIMER != null)
				{
					END_TIMER.cancel(true);
					END_TIMER = null;
				}
				END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(getActor()), 3000);
			}
			else if(i1 == 0 && i2 == 0 && i3 == 3)
			{
				player.sendMessage(new CustomMessage("ai.Maguen.Reptilikon", player));
				DeleteEffect(player, 6369);
				if(Rnd.chance(70))
					AddPartyEffect(player, 6366, 1);
				else
					AddPartyEffect(player, 6366, 2);

				//Ревард магвена.
				int i4 = Rnd.get(10000);
				int i5 = Rnd.get(20);
				if(i4 == 0 && i5 != 0)
				{
					if(player.isPlayer())
					{
						player.getPlayer().getInventory().addItem(15488, 1);
					}
				}
				else if(i4 == 0 && i5 == 0)
				{
					if(player.isPlayer())
					{
						player.getPlayer().getInventory().addItem(15489, 1);
					}
				}
				if(END_TIMER != null)
				{
					END_TIMER.cancel(true);
					END_TIMER = null;
				}
				END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(getActor()), 3000);
			}
			else if(i1 + i2 + i3 == 3)
			{
				if(i1 == 1 && i2 == 1 && i3 == 1)
				{
					DeleteEffect(player, 6367);
					DeleteEffect(player, 6368);
					DeleteEffect(player, 6369);
					player.sendMessage(new CustomMessage("ai.Maguen.Succes", player));
					switch(Rnd.get(3))
					{
						case 0:
							if(Rnd.chance(70))
								AddPartyEffect(player, 6343, 1);
							else
								AddPartyEffect(player, 6343, 2);
							break;
						case 1:
							if(Rnd.chance(70))
								AddPartyEffect(player, 6365, 1);
							else
								AddPartyEffect(player, 6365, 2);
							break;
						case 2:
							if(Rnd.chance(70))
								AddPartyEffect(player, 6366, 1);
							else
								AddPartyEffect(player, 6366, 2);
							break;
						default:
							break;
					}
					//Ревард магвена.
					int i4 = Rnd.get(10000);
					int i5 = Rnd.get(20);
					if(i4 == 0 && i5 != 0)
					{
						if(player.isPlayer())
						{
							player.getPlayer().getInventory().addItem(15488, 1);
						}
					}
					else if(i4 == 0 && i5 == 0)
					{
						if(player.isPlayer())
						{
							player.getPlayer().getInventory().addItem(15489, 1);
						}
					}
					if(END_TIMER != null)
					{
						END_TIMER.cancel(true);
						END_TIMER = null;
					}
					END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(getActor()), 3000);
				}
				else
				{
					player.sendMessage(new CustomMessage("ai.Maguen.Boom", player));
					DeleteEffect(player, 6367);
					DeleteEffect(player, 6368);
					DeleteEffect(player, 6369);
					if(END_TIMER != null)
					{
						END_TIMER.cancel(true);
						END_TIMER = null;
					}
					END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(getActor()), 1000);
				}
			}
			if(END_TIMER != null)
			{
				END_TIMER.cancel(true);
				END_TIMER = null;
			}
			END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(getActor()), 1000);
			getActor().setNpcState(4);
		}
		super.onEvtFinishCasting(skill, caster,target);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(attacker == null)
			return;

		if(attacker.isPlayable())
			return;

		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void checkAggression(L2Character target)
	{
		if(target.isPlayable())
			return;

		super.checkAggression(target);
	}

	private class distcheckTimer extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			if(FIRST_TIMER == null)
			{
				if(cha == null)
				{
					if(END_TIMER != null)
					{
						END_TIMER.cancel(true);
						END_TIMER = null;
					}
					END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(actor), 2000);
				}
				else if(actor.getRealDistance3D(cha) < 100)
				{
					if(FIRST_TIMER == null)
						FIRST_TIMER = ThreadPoolManager.getInstance().schedule(new firstTimer(), 4000);
				}
				else
					ThreadPoolManager.getInstance().schedule(new distcheckTimer(), 100);
			}
		}
	}

	private class firstTimer extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			if(clearTask)
			{
				actor.setNpcState(Rnd.get(1, 3));
				SECOND_TIMER = ThreadPoolManager.getInstance().schedule(new secondTimer(), 5000 + Rnd.get(300));
			}
			else
				actor.setNpcState(4);
		}
	}

	private class secondTimer extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			if(clearTask)
			{
				actor.setNpcState(Rnd.get(1, 3));
				THIRD_TIMER = ThreadPoolManager.getInstance().schedule(new thirdTimer(), 4600 + Rnd.get(600));
			}
			else
				actor.setNpcState(4);
		}
	}

	private class thirdTimer extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			if(clearTask)
			{
				actor.setNpcState(Rnd.get(1, 3));
				FORTH_TIMER = ThreadPoolManager.getInstance().schedule(new forthTimer(), 4200 + Rnd.get(900));
			}
			else
				actor.setNpcState(4);
		}
	}

	private class forthTimer extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			if(clearTask)
			{
				actor.setNpcState(4);
				if(END_TIMER != null)
				{
					END_TIMER.cancel(true);
					END_TIMER = null;
				}
				END_TIMER = ThreadPoolManager.getInstance().schedule(new endTimer(actor), 500);
			}
			else
				actor.setNpcState(4);
		}
	}

	private class endTimer extends l2open.common.RunnableImpl
	{
		private L2NpcInstance actor;
		public endTimer(L2NpcInstance _actor)
		{
			actor = _actor;
		}
		@Override
		public void runImpl()
		{
			actor.setNpcState(4);
			actor.doDie(null);
		}
	}

	@Override
	public void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character target = prepareTarget();
		if(target != null && target.isPlayable())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return;
		}
		super.thinkAttack();
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		actor.setNpcState(4);
		clearTask = false;
		if(FIRST_TIMER != null)
			FIRST_TIMER.cancel(true);
		if(SECOND_TIMER != null)
			SECOND_TIMER.cancel(true);
		if(THIRD_TIMER != null)
			THIRD_TIMER.cancel(true);
		if(FORTH_TIMER != null)
			FORTH_TIMER.cancel(true);
		if(FOLLOW_TASK != null)
			FOLLOW_TASK.cancel(true);
		super.MY_DYING(killer);
	}
}
