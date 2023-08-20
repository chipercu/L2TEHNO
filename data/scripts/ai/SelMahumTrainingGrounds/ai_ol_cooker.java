package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для для повара в селмахум координаты ходьбы брал с РПГ =)
 **/

public class ai_ol_cooker extends Fighter
{
	private L2Character myself = null;
	public ai_ol_cooker(L2Character self)
	{
		super(self);
		myself = self;
		AI_TASK_ACTIVE_DELAY = 5100;
		AI_TASK_DELAY = 1000;

	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	public String SuperPointName = "";
	public int Skill01_ID = 6330;
	public Location[] points = null;
	private int _lastPoint = 0;
	private boolean lastPoint = false;

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		myself.i_ai0 = 0;
		myself.i_ai1 = 0;
		CreateOnePrivateEx(22779,"SelMahumTrainingGrounds.ai_ol_cooker_guard",0,1,myself.getX(),myself.getY(),(myself.getZ() + 100),0,1,0,0);
		CreateOnePrivateEx(22779,"SelMahumTrainingGrounds.ai_ol_cooker_guard",0,1,myself.getX(),myself.getY(),(myself.getZ() + 100),0,0,0,0);
		if(SuperPointName.equals("cooker_01")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(85780,54313,-3656),new Location(86697,53999,-3704),new Location(87220,54529,-3664),new Location(88378,54153,-3608),new Location(88992,53970,-3712),new Location(89908,54160,-3760),new Location(90616,54180,-3776),new Location(91292,54959,-3760)};
			//points = new Location[] {new Location(84463,63007,-3576),new Location(85669,63026,-3624),new Location(86420,62611,-3680),new Location(86529,61661,-3616),new Location(87300,61489,-3656)};
		}
		else if(SuperPointName.equals("cooker_02")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(94083,55399,-3488),new Location(94341,54729,-3576),new Location(94018,54385,-3616),new Location(92768,54569,-3680),new Location(91724,54862,-3744),new Location(91800,55673,-3640),new Location(91859,56697,-3560),new Location(91268,57690,-3696)};
			//points = new Location[] {new Location(87967,60334,-3568),new Location(88379,60689,-3632),new Location(89299,60265,-3656),new Location(90084,60663,-3632),new Location(91007,59913,-3632),new Location(93435,59901,-3304),new Location(92321,60291,-3384),new Location(90966,60218,-3600),new Location(90051,59869,-3664),new Location(88796,59984,-3584)};
		}
		else if(SuperPointName.equals("cooker_03")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(87975,60373,-3568),new Location(88388,60715,-3632),new Location(89349,60249,-3664),new Location(90099,60673,-3648),new Location(91028,59924,-3624),new Location(92092,59894,-3488),new Location(93394,59891,-3304),new Location(92256,60288,-3416),new Location(90905,60193,-3600),new Location(90020,59855,-3664),new Location(89198,59936,-3608),new Location(88758,59979,-3584),new Location(88030,59590,-3544)};
			//points = new Location[] {new Location(88405,60253,-3584),new Location(87379,61104,-3624),new Location(86659,60527,-3512),new Location(87016,59568,-3560),new Location(87242,59282,-3560),new Location(88830,58516,-3584),new Location(89981,58624,-3680),new Location(90736,58534,-3776),new Location(90848,58141,-3792),new Location(91119,57845,-3744)};
		}
		else if(SuperPointName.equals("cooker_04")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(84466,63043,-3576),new Location(85683,63034,-3624),new Location(86438,62659,-3680),new Location(86482,62023,-3656),new Location(86525,61658,-3616),new Location(87316,61473,-3656)};
			//points = new Location[] {new Location(85774,54317,-3656),new Location(86676,54005,-3704),new Location(87246,54542,-3664),new Location(88953,53970,-3696),new Location(89922,54159,-3760),new Location(90631,54175,-3776),new Location(91321,54979,-3760)};
		}
		else if(SuperPointName.equals("cooker_05")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(87363,61092,-3624),new Location(86658,60514,-3512),new Location(87033,59548,-3560),new Location(87390,59175,-3568),new Location(88933,58482,-3600),new Location(90003,58629,-3696),new Location(90750,58537,-3776),new Location(90860,58113,-3792),new Location(91154,57837,-3728)};
			//points = new Location[] {new Location(94292,54810,-3568),new Location(94064,54348,-3616),new Location(91685,54871,-3744),new Location(91806,55607,-3640),new Location(91865,56637,-3560),new Location(91225,57716,-3712),new Location(91862,56677,-3560),new Location(91677,54851,-3744),new Location(92716,54590,-3680),new Location(94053,54324,-3616),new Location(94353,54689,-3576)};
		}
		else if(SuperPointName.equals("cooker_06")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(93149,61655,-3240),new Location(93398,62163,-3264),new Location(93488,62956,-3288),new Location(93220,63977,-3344),new Location(92016,64640,-3552),new Location(91510,65206,-3608),new Location(89707,65225,-3696),new Location(88463,65224,-3720),new Location(88066,65216,-3720),new Location(87878,64685,-3720),new Location(87259,64441,-3688),new Location(86635,64930,-3624)};
			//points = new Location[] {new Location(93149,61631,-3240),new Location(93403,62180,-3264),new Location(93484,62942,-3288),new Location(93229,63952,-3344),new Location(92005,64642,-3552),new Location(91555,65165,-3592),new Location(88043,65236,-3720),new Location(87888,64708,-3720),new Location(87292,64467,-3688),new Location(86612,64943,-3524)};
		}
		else if(SuperPointName.equals("cooker_07")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(78940,63729,-3608),new Location(79264,64129,-3496),new Location(79117,65244,-3312),new Location(80398,65096,-3312),new Location(81548,64977,-3296),new Location(81690,63849,-3576),new Location(82395,63805,-3584),new Location(83283,63961,-3568),new Location(83900,63299,-3584),new Location(85284,63527,-3688),new Location(85935,64597,-3624)};
			//points = new Location[] {new Location(78943,63731,-3608),new Location(79261,64127,-3496),new Location(79108,65257,-3304),new Location(81538,64977,-3396),new Location(81710,63835,-3576),new Location(82370,63802,-3584),new Location(83313,63943,-3568),new Location(83886,63293,-3584),new Location(85272,63502,-3688),new Location(85943,64604,-3624)};
		}
		else if(SuperPointName.equals("cooker_08")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(77938,69555,-3096),new Location(78542,69884,-3168),new Location(79191,69425,-3304),new Location(79626,68966,-3328),new Location(80088,69350,-3384),new Location(80579,68684,-3448),new Location(80732,67870,-3432),new Location(80816,67025,-3336),new Location(80972,65925,-3384)};
			//points = new Location[] {new Location(77947,69568,-3096),new Location(78532,69881,-3168),new Location(79217,69404,-3304),new Location(79598,68964,-3320),new Location(80082,69330,-3384),new Location(80591,68680,-3448),new Location(80723,67896,-3432),new Location(80960,66017,-3384)};
		}
		else if(SuperPointName.equals("cooker_09")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(82840,69486,-3136),new Location(82513,69970,-3248),new Location(81683,69844,-3344),new Location(81095,69356,-3416),new Location(80712,68690,-3456),new Location(80839,67829,-3432),new Location(80924,67156,-3344),new Location(81036,65933,-3368)};
			//points = new Location[] {new Location(83620,66128,-3040),new Location(84046,65817,-3112),new Location(84697,65768,-3192),new Location(85285,65780,-3232),new Location(85825,66115,-3320),new Location(86709,65765,-3576),new Location(88189,65712,-3720)};
		}
		else if(SuperPointName.equals("cooker_10")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(83632,66143,-3040),new Location(84058,65822,-3120),new Location(84707,65773,-3192),new Location(85269,65784,-3232),new Location(85836,66121,-3320),new Location(86708,65773,-3576),new Location(87353,65748,-3696),new Location(88200,65720,-3720)};
			//points = new Location[] {new Location(90259,67364,-3696),new Location(90652,67132,-3664),new Location(91697,67336,-3648),new Location(91870,67871,-3592),new Location(91382,68349,-3536),new Location(90880,68217,-3560),new Location(89976,68368,-3656),new Location(89652,69429,-3424),new Location(88961,69714,-3344),new Location(88311,68961,-3176),new Location(87851,68766,-3128),new Location(87426,69389,-3064),new Location(86066,69477,-3064),new Location(85776,69137,-3064),new Location(85717,67833,-3064),new Location(86025,67590,-3064),new Location(86486,67765,-3064),new Location(86984,67651,-3080)};
		}
		else if(SuperPointName.equals("cooker_11")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(95962,68593,-3312),new Location(95052,67989,-3368),new Location(94209,67887,-3584),new Location(93767,67806,-3664),new Location(92623,68472,-3592),new Location(92466,69681,-3544),new Location(91038,69719,-3464),new Location(90098,69653,-3432),new Location(89946,69624,-3432),new Location(88463,69971,-3280),new Location(88692,70361,-3360),new Location(90520,70608,-3536),new Location(91441,70373,-3488),new Location(92651,70756,-3568),new Location(93795,71126,-3616),new Location(93946,70378,-3664),new Location(93239,69041,-3608),new Location(94124,68257,-3640),new Location(94934,68302,-3432)};
			//points = new Location[] {new Location(95956,68593,-3312),new Location(95050,68002,-3368),new Location(93787,67812,-3664),new Location(92648,68459,-3592),new Location(92474,69689,-3544),new Location(91158,69736,-3472),new Location(89903,69628,-3432),new Location(88393,69991,-3280),new Location(88697,70361,-3360),new Location(90512,70604,-3536),new Location(91422,70372,-3488),new Location(93749,71098,-3616),new Location(93950,70395,-3664),new Location(93227,69030,-3608),new Location(94106,68265,-3640),new Location(94973,68298,-3424)};
		}
		else if(SuperPointName.equals("cooker_12")) //done PTS
		{
			points = new Location[] {new Location(getActor().getSpawnedLoc().x, getActor().getSpawnedLoc().y, getActor().getSpawnedLoc().z),new Location(91181,67639,-3600),new Location(90867,67539,-3632),new Location(90280,67369,-3696),new Location(90720,67109,-3664),new Location(91680,67347,-3648),new Location(91878,67883,-3592),new Location(91369,68349,-3536),new Location(90829,68210,-3576),new Location(89931,68369,-3656),new Location(89724,69089,-3544),new Location(89652,69408,-3424),new Location(88995,69691,-3344),new Location(88319,68937,-3176),new Location(87836,68752,-3128),new Location(87401,69394,-3064),new Location(86668,69428,-3064),new Location(86013,69458,-3040),new Location(85747,69110,-3064),new Location(85750,67817,-3064),new Location(86060,67606,-3064),new Location(86552,67761,-3064),new Location(86981,67679,-3080)};
			//points = new Location[] {new Location(82557,69945,-3248),new Location(81715,69852,-3344),new Location(81114,69390,-3400),new Location(80736,68741,-3448),new Location(81024,65948,-3368)};
		}
	}

	private synchronized void startMoveTask()
	{
		if(!lastPoint)
			_lastPoint++;

		if(_lastPoint >= points.length)
			lastPoint = true;

		if(lastPoint)
			_lastPoint--;

		if(_lastPoint < 0)
			_lastPoint = 0;

		if(_lastPoint == 0 && lastPoint)
			lastPoint = false;

		getActor().setWalking();
		addTaskMove(Location.findPointToStay(points[_lastPoint], 30, getActor().getReflection().getGeoIndex()), true);
		doTask();
		clearTasks();
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}

	@Override
	public void returnHome(boolean clearAggro)
	{
		super.returnHome(clearAggro);
		clearTasks();
		startMoveTask();
	}

	@Override
	public void NO_DESIRE()
	{
		if(!getActor().isMoving && getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
			startMoveTask();

		super.NO_DESIRE();
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		L2Character c0;
		if(script_event_arg1 == 2119019)
		{
			myself.i_ai1 = 1;
			c0 = GetCreatureFromIndex(script_event_arg2);
			if(IsNullCreature(c0) == 0)
			{
				if(!myself.isAttackingNow())
				{
				}
			}
		}
	}

	@Override
	protected void onEvtArrived()
	{
		BroadcastScriptEvent(2219017, 0, 300);
		myself.AddTimerEx(2219002,2000);
		super.onEvtArrived();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 2219001)
		{
			ChangeMoveType(0);
			if(!myself.isAttackingNow())
			{
			}
		}
		if(timer_id == 2219002)
		{
			if(myself.i_ai1 == 1)
			{
				if(Rnd.get(2) < 1)
				{
					Say(MakeFString(1801116, "", "", "", "", ""));
				}
				else
				{
					Say(MakeFString(1801117, "", "", "", "", ""));
				}
				myself.i_ai0 = 2;
				myself.AddTimerEx(2219001,6000);
				myself.i_ai1 = 0;
			}
			else
			{
				myself.i_ai0 = 1;
				myself.AddTimerEx(2219001,100);
			}
		}
		if(timer_id == 2019003)
		{
			if((myself.c_ai0.alive() > 0 && (myself.isInCombat())) && myself.i_ai5 == 1)
			{
				AddUseSkillDesire(myself.c_ai0, SkillTable.getInstance().getInfo(Skill01_ID, 1),10000000);
			}
			else
			{
				myself.i_ai0 = 1;
				myself.i_ai2 = 0;
				ChangeMoveType(0);
			}
		}
		if(timer_id == 2019004)
		{
			myself.i_ai5 = 0;
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(6646,1), 999999); //6646
			if(IsNullCreature(myself.c_ai0) != 1)
			{
				AddAttackDesire(myself.c_ai0, 1, 5000);
			}
		}
		if(timer_id == 2019005)
		{
			myself.i_ai6 = 1;
		}
		if(timer_id == 2019006)
		{
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(6645,1), 900000);  //6645
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		if(skill.getId() == Skill01_ID)
		{
			if(myself.c_ai0.alive() > 0 && myself.isInCombat() && myself.i_ai5 == 1)
			{
				AddUseSkillDesire(myself.c_ai0, SkillTable.getInstance().getInfo(Skill01_ID, 1), 10000000);
			}
		}
		if(target.alive() == 0 && target.is_pc() == 0)
		{
			//RemoveAttackDesire(getActor().getMostHated().getObjectId());
			if(getActor().getMostHated() != null)
				getActor().getMostHated().removeFromHatelist(getActor(), false);
			getActor().abortAttack(true, false);
			myself.c_ai0 = null;
			myself.i_ai0 = 1;
			myself.i_ai2 = 0;
			myself.i_ai5 = 0;
		}
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		getActor().stopMove();
		if(myself.i_ai2 == 0)
		{
			if(myself.i_ai5 == 0)
			{
				myself.AddTimerEx(2019004,((3 * 60) * 1000));
				myself.AddTimerEx(2019005,(60 * 1000));
				myself.i_ai5 = 1;
				myself.i_ai0 = 4;
			}
			myself.c_ai0 = attacker;
			myself.AddTimerEx(2019003,1000);
			myself.AddTimerEx(2019006,(60 * 1000));
			myself.i_ai2 = 1;
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{
		if(myself.i_ai2 == 0)
		{
			if(myself.i_ai5 == 0)
			{
				myself.AddTimerEx(2019004,((3 * 60) * 1000));
				myself.AddTimerEx(2019005,(60 * 1000));
				myself.i_ai5 = 1;
				myself.i_ai0 = 4;
			}
			myself.c_ai0 = attacker;
			myself.AddTimerEx(2019003,1000);
			myself.AddTimerEx(2019006,(60 * 1000));
			myself.i_ai2 = 1;
		}
		super.PARTY_ATTACKED(attacker, party_member_attacked, damage);
	}

	@Override
	public void MY_DYING(L2Character killer)
	{
		if(myself.i_ai6 != 1)
		{
			if(Rnd.get(10) < 2)
			{
				DropItem1(getActor(),15492,1);
			}
		}
		super.MY_DYING(killer);
	}
}
