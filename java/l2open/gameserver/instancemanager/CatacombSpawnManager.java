package l2open.gameserver.instancemanager;

import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.util.GArray;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CatacombSpawnManager
{
	private static Logger _log = Logger.getLogger(CatacombSpawnManager.class.getName());

	private static CatacombSpawnManager _instance;
	private static GArray<L2Spawn> _dawnMobs = new GArray<L2Spawn>();
	private static GArray<L2Spawn> _duskMobs = new GArray<L2Spawn>();

	private static int _currentState = 0; // 0 = Undefined, 1 = Dawn, 2 = Dusk

	// вообще все мобы в катах, не только управляемые менеджером
	public static final List<Integer> _monsters = Arrays.asList(21139, 21140, 21141, 21142, 21143, 21144, 21145, 21146, 21147, 21148, 21149, 21150, 21151, 21152, 21153, 21154, 21155, 21156, 21157, 21158, 21159, 21160, 21161, 21162, 21163, 21164, 21165, 21166, 21167, 21168, 21169, 21170, 21171, 21172, 21173, 21174, 21175, 21176, 21177, 21178, 21179, 21180, 21181, 21182, 21183, 21184, 21185, 21186, 21187, 21188, 21189, 21190, 21191, 21192, 21193, 21194, 21195, 21196, 21197, 21198, 21199, 21200, 21201, 21202, 21203, 21204, 21205, 21206, 21207, 21208, 21209, 21210, 21211, 21213, 21214, 21215, 21217, 21218, 21219, 21221, 21222, 21223, 21224, 21225, 21226, 21227, 21228, 21229, 21230, 21231, 21236, 21237, 21238, 21239, 21240, 21241, 21242, 21243, 21244, 21245, 21246, 21247, 21248, 21249, 21250, 21251, 21252, 21253, 21254, 21255);

	public static CatacombSpawnManager getInstance()
	{
		if(_instance == null)
			_instance = new CatacombSpawnManager();

		return _instance;
	}

	public void addDawnMob(L2Spawn spawnDat)
	{
		_dawnMobs.add(spawnDat);
	}

	public void addDuskMob(L2Spawn spawnDat)
	{
		_duskMobs.add(spawnDat);
	}

	public void changeMode(int mode)
	{
		if(_currentState == mode)
			return;

		_currentState = mode;

		switch(mode)
		{
			case 0: // all spawns
				deleteMobs(_dawnMobs);
				deleteMobs(_duskMobs);
				spawnMobs(_duskMobs);
				spawnMobs(_dawnMobs);
				break;
			case 1: // dusk spawns
				deleteMobs(_dawnMobs);
				deleteMobs(_duskMobs);
				spawnMobs(_duskMobs);
				break;
			case 2: // dawn spawns
				deleteMobs(_dawnMobs);
				deleteMobs(_duskMobs);
				spawnMobs(_dawnMobs);
				break;
			default:
				_log.warning("DayNightSpawnManager: Wrong mode sent");
				break;
		}
	}

	public void notifyChangeMode()
	{
		if(SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION)
			changeMode(SevenSigns.getInstance().getCabalHighestScore());
		else
			changeMode(0);
	}

	public void cleanUp()
	{
		deleteMobs(_duskMobs);
		deleteMobs(_dawnMobs);

		_duskMobs.clear();
		_dawnMobs.clear();
	}

	public void spawnMobs(GArray<L2Spawn> mobsSpawnsList)
	{
		for(L2Spawn spawnDat : mobsSpawnsList)
		{
			if(_currentState == 0)
				spawnDat.restoreAmount();
			else
				spawnDat.setAmount(spawnDat.getAmount() * 2);

			spawnDat.init();
		}
	}

	public static void deleteMobs(GArray<L2Spawn> mobsSpawnsList)
	{
		for(L2Spawn spawnDat : mobsSpawnsList)
			spawnDat.despawnAll();
	}
}