package ai;

import bosses.LastImperialTombManager;
import javolution.util.FastMap;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * @author Diamond
 */
public class Scarlet extends DefaultAI
{
	private static final int _strongScarletId = 29047;
	private static final int _frintezzasSwordId = 7903;

	private final L2Skill Swing = SkillTable.getInstance().getInfo(5014, 3); // s_frintessa_daemon_attack3 - 5014-3
	private final L2Skill Dash = SkillTable.getInstance().getInfo(5015, 3); // s_frintessa_daemon_charge3 - 5015-3
	private final L2Skill DashAll = SkillTable.getInstance().getInfo(5015, 6); // s_frintessa_daemon_charge_slow3 - 5015-6
	private final L2Skill AntiGravity = SkillTable.getInstance().getInfo(5016, 1); // s_frintessa_daemon_trance1 - 5016-1
	private final L2Skill MagicCircle = SkillTable.getInstance().getInfo(5018, 2); // s_frintessa_daemon_field2 - 5018-2
	private final L2Skill Vampiric = SkillTable.getInstance().getInfo(5019, 1); // s_frintessa_daemon_drain_hp1 - 5019-1

	private final L2Skill SwingVer1 = SkillTable.getInstance().getInfo(5013, 1); // s_frintessa_daemon_attack1 - 5013-1
	private final L2Skill SwingVer2 = SkillTable.getInstance().getInfo(5014, 1); // s_frintessa_daemon_attack2 - 5014-1
	private final L2Skill DashVer2 = SkillTable.getInstance().getInfo(5015, 2); // s_frintessa_daemon_charge2 - 5015-2
	private final L2Skill DashAllVer1 = SkillTable.getInstance().getInfo(5015, 4); // s_frintessa_daemon_charge_slow1 - 5015-4
	private final L2Skill DashAllVer2 = SkillTable.getInstance().getInfo(5015, 5); // s_frintessa_daemon_charge_slow2 - 5015-5

	private L2NpcInstance myself = null;

	private FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();

	public Scarlet(L2Character actor)
	{
		super(actor);
		myself = (L2NpcInstance)actor;
	}

	@Override
	protected boolean createNewTask()
	{
		return createNewTask(0);
	}

	protected boolean createNewTask(int skill_name_id)
	{
		clearTasks();
		L2Character target;
		if((target = prepareTarget()) == null)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return false;
		}

		if(myself == null || myself.isDead())
			return false;

		LastImperialTombManager.World world = LastImperialTombManager.getWorld(myself.getReflectionId());
		if(world != null && !world._zonefrintezza.checkIfInZone(myself))
		{
			teleportHome(true);
			return false;
		}

		int stage = 0;
		if(myself.getNpcId() == _strongScarletId)
			stage = 2;
		else if(myself.getRightHandItem() == _frintezzasSwordId)
			stage = 1;

		if(stage == 2)
			follower_of_frintessa_tr(target, skill_name_id);
		else
			follower_of_frintessa(target);
		
		d_skill.clear();

		AddAttackDesire(target, 0, 1000);
		//return chooseTaskAndTargets(r_skill, target, distance);
		return true;
	}
	@Override
	protected boolean maybeMoveToHome()
	{
		LastImperialTombManager.World world = LastImperialTombManager.getWorld(myself.getReflectionId());
		if(world != null && world._zonefrintezza != null && !world._zonefrintezza.checkIfInZone(myself))
			teleportHome(true);
		return false;
	}

	public boolean follower_of_frintessa(L2Character target)
	{
		if(myself.getCurrentHp() < (myself.getMaxHp() * 0.500000) && myself.i_ai3 != 30010)
			myself.i_ai3 = 30010;
		else if(myself.getCurrentHp() < (myself.getMaxHp() * 0.200000) && myself.i_ai4 == 0)
			myself.i_ai3 = 30011;
		if(myself.i_ai4 == 0)
		{
			if(IsNullCreature(target) == 0)
			{
				if(myself.i_ai3 < 30010)
				{
					if(Rnd.get(10000) < 2000)
					{
						//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
						//{
							if(myself.getAggroList().size() >= 5)
							{
								for(AggroInfo ai : myself.getAggroList())
								{
									if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
									{
										AddUseSkillDesire(target,DashAllVer1,10000);
										return true;
									}
								}
							}
						//}
					}
					else if(Rnd.get(10000) < 500)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,AntiGravity,10000);
						}
					}
					else if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,SwingVer1,10000);
					}
				}
				if(myself.i_ai3 >= 30010)
				{
					if(myself.i_ai2 > 0)
					{
						if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
						{
							if(IsNullCreature(target) == 0)
							{
								AddUseSkillDesire(target,DashVer2,10000);
							}
							myself.i_ai2 = (myself.i_ai2 + 1);
						}
						else if(myself.i_ai2 == 3)
						{
							if(IsNullCreature(target) == 0)
							{
								AddUseSkillDesire(target,DashAllVer2,10000);
							}
							myself.i_ai2 = 0;
						}
						else
						{
							myself.i_ai2 = 0;
						}
					}
					else if(myself.getCurrentHp() > (myself.getMaxHp() * 0.500000))
					{
						if(Rnd.get(10000) < 2000)
						{
							//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
						//{
							if(myself.getAggroList().size() >= 5)
							{
								for(AggroInfo ai : myself.getAggroList())
								{
									if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
									{
										AddUseSkillDesire(target,DashAllVer2,10000);
										return true;
									}
								}
							}
						//}
						}
						else if(Rnd.get(10000) < 500)
						{
							myself.i_ai2 = 1;
							if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
							{
								if(IsNullCreature(target) == 0)
								{
									AddUseSkillDesire(target,DashVer2,10000);
								}
								myself.i_ai2 = (myself.i_ai2 + 1);
							}
							else if(myself.i_ai2 == 3)
							{
								if(IsNullCreature(target) == 0)
								{
									AddUseSkillDesire(target,DashAllVer2,10000);
								}
								myself.i_ai2 = 0;
							}
							else
							{
								myself.i_ai2 = 0;
							}
						}
						else if(Rnd.get(10000) < 500)
						{
							if(IsNullCreature(target) == 0)
							{
								AddUseSkillDesire(target,AntiGravity,10000);
							}
						}
						else if(Rnd.get(10000) < 500)
						{
							if(IsNullCreature(target) == 0)
							{
								AddUseSkillDesire(target,SkillTable.getInstance().getInfo(5018, 1),10000);
							}
						}
						else if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,SwingVer2,10000);
						}
					}
					else if(Rnd.get(10000) < 2000)
					{
						//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
						//{
							if(myself.getAggroList().size() >= 5)
							{
								for(AggroInfo ai : myself.getAggroList())
								{
									if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
									{
										AddUseSkillDesire(target,DashAllVer2,10000);
										return true;
									}
								}
							}
						//}
					}
					else if(Rnd.get(10000) < 1500)
					{
						myself.i_ai2 = 1;
						if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
						{
							if(IsNullCreature(target) == 0)
							{
								AddUseSkillDesire(target,DashVer2,10000);
							}
							myself.i_ai2 = (myself.i_ai2 + 1);
						}
						else if(myself.i_ai2 == 3)
						{
							if(IsNullCreature(target) == 0)
							{
								AddUseSkillDesire(target,DashAllVer2,10000);
							}
							myself.i_ai2 = 0;
						}
						else
						{
							myself.i_ai2 = 0;
						}
					}
					else if(Rnd.get(10000) < 1500)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,AntiGravity,10000);
						}
					}
					else if(Rnd.get(10000) < 1000)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,SkillTable.getInstance().getInfo(5018, 1),10000);
						}
					}
					else if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,SwingVer2,10000);
					}
				}
			}
		}
		return true;
	}

	public boolean follower_of_frintessa_tr(L2Character target, int skill_name_id)
	{
		double distance = myself.getDistance(target);

		if(skill_name_id == 2) // 328204290
		{
			myself.i_ai2 = 1;
			if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
			{
				if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,Dash,10000);
				}
				myself.i_ai2 = (myself.i_ai2 + 1);
			}
			else if(myself.i_ai2 == 3)
			{
				if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,DashAll,10000);
				}
				myself.i_ai2 = 0;
			}
			else
			{
				myself.i_ai2 = 0;
			}
		}

		if(IsNullCreature(target) == 0)
		{
			if(myself.i_ai2 > 0)
			{
				if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,Dash,10000);
					}
					myself.i_ai2 = (myself.i_ai2 + 1);
				}
				else if(myself.i_ai2 == 3)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,DashAll,10000);
					}
					myself.i_ai2 = 0;
				}
				else
				{
					myself.i_ai2 = 0;
				}
			}
			else if(myself.getCurrentHp() > (myself.getMaxHp() * 0.750000))
			{
				if(Rnd.get(10000) < 2000)
				{
					//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
					//{
						if(myself.getAggroList().size() >= 5)
						{
							for(AggroInfo ai : myself.getAggroList())
							{
								if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
								{
									AddUseSkillDesire(target,DashAll,10000);
									return true;
								}
							}
						}
					//}
				}
				else if(Rnd.get(10000) < 1000)
				{
					myself.i_ai2 = 1;
					if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,Dash,10000);
						}
						myself.i_ai2 = (myself.i_ai2 + 1);
					}
					else if(myself.i_ai2 == 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,DashAll,10000);
						}
						myself.i_ai2 = 0;
					}
					else
					{
						myself.i_ai2 = 0;
					}
				}
				else if(Rnd.get(10000) < 1000)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,AntiGravity,10000);
					}
				}
				else if(Rnd.get(10000) < 0)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,MagicCircle,10000);
					}
				}
				else if(Rnd.get(10000) < 0)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,Vampiric,10000);
					}
				}
				else if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,Swing,10000);
				}
			}
			else if(myself.getCurrentHp() > (myself.getMaxHp() * 0.500000))
			{
				if(Rnd.get(10000) < 2000)
				{
					//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
					//{
						if(myself.getAggroList().size() >= 5)
						{
							for(AggroInfo ai : myself.getAggroList())
							{
								if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
								{
									AddUseSkillDesire(target,DashAll,10000);
									return true;
								}
							}
						}
					//}
				}
				else if(Rnd.get(10000) < 1000)
				{
					myself.i_ai2 = 1;
					if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,Dash,10000);
						}
						myself.i_ai2 = (myself.i_ai2 + 1);
					}
					else if(myself.i_ai2 == 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,DashAll,10000);
						}
						myself.i_ai2 = 0;
					}
					else
					{
						myself.i_ai2 = 0;
					}
				}
				else if(Rnd.get(10000) < 750)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,AntiGravity,10000);
					}
				}
				else if(Rnd.get(10000) < 500)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,MagicCircle,10000);
					}
				}
				else if(Rnd.get(10000) < 500)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,Vampiric,10000);
					}
				}
				else if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,Swing,10000);
				}
			}
			else if(myself.getCurrentHp() > (myself.getMaxHp() * 0.250000))
			{
				if(Rnd.get(10000) < 2000)
				{
					//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
					//{
						if(myself.getAggroList().size() >= 5)
						{
							for(AggroInfo ai : myself.getAggroList())
							{
								if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
								{
									AddUseSkillDesire(target,DashAll,10000);
									return true;
								}
							}
						}
					//}
				}
				else if(Rnd.get(10000) < 1000)
				{
					myself.i_ai2 = 1;
					if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,Dash,10000);
						}
						myself.i_ai2 = (myself.i_ai2 + 1);
					}
					else if(myself.i_ai2 == 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,DashAll,10000);
						}
						myself.i_ai2 = 0;
					}
					else
					{
						myself.i_ai2 = 0;
					}
				}
				else if(Rnd.get(10000) < 750)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,AntiGravity,10000);
					}
				}
				else if(Rnd.get(10000) < 1000)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,MagicCircle,10000);
					}
				}
				else if(Rnd.get(10000) < 500)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,Vampiric,10000);
					}
				}
				else if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,Swing,10000);
				}
			}
			else if(myself.getCurrentHp() > (myself.getMaxHp() * 0.100000))
			{
				if(Rnd.get(10000) < 2000)
				{
					//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
					//{
						if(myself.getAggroList().size() >= 5)
						{
							for(AggroInfo ai : myself.getAggroList())
							{
								if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
								{
									AddUseSkillDesire(target,DashAll,10000);
									return true;
								}
							}
						}
					//}
				}
				else if(Rnd.get(10000) < 1000)
				{
					myself.i_ai2 = 1;
					if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,Dash,10000);
						}
						myself.i_ai2 = (myself.i_ai2 + 1);
					}
					else if(myself.i_ai2 == 3)
					{
						if(IsNullCreature(target) == 0)
						{
							AddUseSkillDesire(target,DashAll,10000);
						}
						myself.i_ai2 = 0;
					}
					else
					{
						myself.i_ai2 = 0;
					}
				}
				else if(Rnd.get(10000) < 1000)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,AntiGravity,10000);
					}
				}
				else if(Rnd.get(10000) < 1000)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,MagicCircle,10000);
					}
				}
				else if(Rnd.get(10000) < 1000)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,Vampiric,10000);
					}
				}
				else if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,Swing,10000);
				}
			}
			else if(Rnd.get(10000) < 2000)
			{
				//if(IsInThisTerritory("25_15_frintessa_NoCharge01") == 1)
				//{
					if(myself.getAggroList().size() >= 5)
					{
						for(AggroInfo ai : myself.getAggroList())
						{
							if(IsNullCreature(ai.attacker) == 0 && DistFromMe(ai.attacker) >= 500 && DistFromMe(ai.attacker) <= 1000)
							{
								AddUseSkillDesire(target,DashAll,10000);
								return true;
							}
						}
					}
				//}
			}
			else if(Rnd.get(10000) < 1000)
			{
				myself.i_ai2 = 1;
				if(myself.i_ai2 > 0 && myself.i_ai2 < 3)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,Dash,10000);
					}
					myself.i_ai2 = (myself.i_ai2 + 1);
				}
				else if(myself.i_ai2 == 3)
				{
					if(IsNullCreature(target) == 0)
					{
						AddUseSkillDesire(target,DashAll,10000);
					}
					myself.i_ai2 = 0;
				}
				else
				{
					myself.i_ai2 = 0;
				}
			}
			else if(Rnd.get(10000) < 500)
			{
				if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,AntiGravity,10000);
				}
			}
			else if(Rnd.get(10000) < 500)
			{
				if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,MagicCircle,10000);
				}
			}
			else if(Rnd.get(10000) < 0)
			{
				if(IsNullCreature(target) == 0)
				{
					AddUseSkillDesire(target,Vampiric,10000);
				}
			}
			else if(IsNullCreature(target) == 0)
			{
				AddUseSkillDesire(target,Swing,10000);
			}
		}
		return true;
	}

	public boolean callDash(int level)
	{
		return createNewTask(level);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}