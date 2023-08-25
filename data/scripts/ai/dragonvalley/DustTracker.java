package ai.dragonvalley;

import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

public class DustTracker extends Patrollers
{
	public String SuperPointName = "";
	private boolean lastPoint = false;

	static final Location[] _2221_47 = new Location[]
	{
		new Location(88040,106648,-3168),
		new Location(88526,107853,-3056),
		new Location(89602,108262,-3032),
		new Location(90440,107955,-3056),
		new Location(91697,108141,-3040)
	};
	static final Location[] _2221_50 = new Location[]
	{
		new Location(94092,107564,-3024),
		new Location(91246,107748,-3056),
		new Location(89123,106549,-3200)
	};
	static final Location[] _2321_26 = new Location[]
	{
		new Location(114348,110080,-3000),
		new Location(113987,110854,-3080),
		new Location(114423,112148,-3096),
		new Location(113658,113149,-3016),
		new Location(112942,113113,-2824),
		new Location(111814,112708,-2776),
		new Location(111894,111702,-2760)
	};
	static final Location[] _2321_27 = new Location[]
	{
		new Location(111720,110228,-3024),
		new Location(114292,109846,-2992),
		new Location(115327,109775,-3032),
		new Location(116721,110033,-3024),
		new Location(118567,109784,-2952),
		new Location(120260,109097,-2936)
	};
	static final Location[] _2321_36 = new Location[]
	{
		new Location(108508,117164,-3056),
		new Location(109126,116701,-3056),
		new Location(109329,115546,-3112),
		new Location(109588,113845,-3040),
		new Location(109474,113328,-3064)
	};

	public DustTracker(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		if(SuperPointName.equals("2221_47"))
			_points = _2221_47;
		else if(SuperPointName.equals("2221_50"))
			_points = _2221_50;
		else if(SuperPointName.equals("2321_26"))
			_points = _2321_26;
		else if(SuperPointName.equals("2321_27"))
			_points = _2321_27;
		else if(SuperPointName.equals("2321_36"))
			_points = _2321_36;
		else
			System.out.println("DustTracker: !!!!!!!!!!!! 40 !!!!!!!!!!!!");
		super.onEvtSpawn();
	}

	@Override
	protected void startMoveTask()
	{
		L2NpcInstance npc = getActor();
		if(_points != null)
		{
			if(_firstThought)
			{
				_lastPoint = getIndex(Location.findNearest(npc, _points));
				_firstThought = false;
			}
			else
			{
				if(!lastPoint)
					_lastPoint++;

				if(_lastPoint >= _points.length)
					lastPoint = true;

				if(lastPoint)
					_lastPoint--;

				if(_lastPoint < 0)
					_lastPoint = 0;
			}

			if(_lastPoint == 0 && lastPoint)
				lastPoint = false;

			npc.setRunning();
			addTaskMove(Location.findPointToStay(_points[_lastPoint], 30, npc.getReflection().getGeoIndex()), true);
			doTask();
			clearTasks();
		}
	}
}