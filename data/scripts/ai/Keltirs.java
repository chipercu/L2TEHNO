package ai;

import l2open.config.ConfigValue;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * User: darkevil, thx VX, Artful за текст, airman за инициативу.
 * Date: 06.04.2008 Time: 22:02:29
 * Info: Crazy Keltirs - по желанию можно добавить и другие
 */
public class Keltirs extends Fighter
{
	// Радиус на который будут отбегать келтиры.
	private static final int range = 600;
	// Время в мс. через которое будет повторяться Rnd фраза.
	private static final int voicetime = 8000;
	private long _lastAction;
	private static final String[] _retreatText = { "Не трогай меня, я боюсь!", "Ты страшный! Братья, убегаем!",
			"Полундра! Сезон охоты открыт!!!", "Если еще раз меня ударишь - у тебя будут неприятности!",
			"Браконьер, я тебя сдам правоохранительным органам!", "Делаем ноги, за 60 сек ^-_-^",
			"Нас не догонят, нас не догонят..." };

	private static final String[] _fightText = { "Всех убью, один останусь!", "Рррррррр!", "Бей гада!",
			"Хочешь, за жопу укушу", "Щас КУСЬ всем сделаю..." };

	public Keltirs(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected boolean createNewTask()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;

		if(Rnd.chance(60))
		{
			// clearTasks();
			L2Character target;
			if((target = prepareTarget()) == null)
				return false;

			// Добавить новое задание
			addTaskAttack(target);

			if(System.currentTimeMillis() - _lastAction > voicetime)
			{
				Functions.npcSay(actor, _fightText[Rnd.get(_fightText.length)]);
				_lastAction = System.currentTimeMillis();
			}
			return true;
		}

		Location sloc = actor.getSpawnedLoc();
		int spawnX = sloc.x;
		int spawnY = sloc.y;
		int spawnZ = sloc.z;

		int x = spawnX + Rnd.get(2 * range) - range;
		int y = spawnY + Rnd.get(2 * range) - range;
		int z = GeoEngine.getHeight(x, y, spawnZ, actor.getReflection().getGeoIndex());

		actor.setRunning();

		actor.moveToLocation(x, y, z, 0, true);

		addTaskMove(spawnX, spawnY, spawnZ, false);
		if(System.currentTimeMillis() - _lastAction > voicetime)
		{
			Functions.npcSay(actor, _retreatText[Rnd.get(_retreatText.length)]);
			_lastAction = System.currentTimeMillis();
		}
		return true;
	}

	private static final int[] _list = { 20481, 20529, 20530, 20531, 20532, 20533, 20534, 20535, 20536, 20537, 20538,
			20539, 20544, 20545, 22229, 22230, 22231, 18003 };

	public static void onLoad()
	{
		if(ConfigValue.AltAiKeltirs)
			for(int id : _list)
			{
				for(L2NpcInstance i : L2ObjectsStorage.getAllByNpcId(id, false))
					i.setAI(new Keltirs(i));
				NpcTable.getTemplate(id).ai_type = "Keltirs";
			}
	}
}