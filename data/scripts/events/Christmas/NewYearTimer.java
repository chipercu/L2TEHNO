package events.Christmas;

import java.util.Calendar;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;

/**
 * User: Death
 * Date: 20/12/2007
 * Time: 23:25:41
 */
public class NewYearTimer implements ScriptFile
{
	private static NewYearTimer instance;

	public static NewYearTimer getInstance()
	{
		if(instance == null)
			new NewYearTimer();
		return instance;
	}

	public NewYearTimer()
	{
		if(instance != null)
			return;

		instance = this;

		if(!isActive())
			return;

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		while(getDelay(c) < 0)
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);

		ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("С Новым, " + c.get(Calendar.YEAR) + ", Годом!!!"), getDelay(c));
		c.add(Calendar.SECOND, -1);
		ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("1"), getDelay(c));
		c.add(Calendar.SECOND, -1);
		ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("2"), getDelay(c));
		c.add(Calendar.SECOND, -1);
		ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("3"), getDelay(c));
		c.add(Calendar.SECOND, -1);
		ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("4"), getDelay(c));
		c.add(Calendar.SECOND, -1);
		ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("5"), getDelay(c));
	}

	private long getDelay(Calendar c)
	{
		return c.getTime().getTime() - System.currentTimeMillis();
	}

	/**
	 * Вызывается при загрузке классов скриптов
	 */
	public void onLoad()
	{}

	/**
	 * Вызывается при перезагрузке
	 * После перезагрузки onLoad() вызывается автоматически
	 */
	public void onReload()
	{}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return ServerVariables.getString("Christmas", "off").equalsIgnoreCase("on");
	}

	/**
	 * Вызывается при выключении сервера
	 */
	public void onShutdown()
	{}

	private class NewYearAnnouncer extends l2open.common.RunnableImpl
	{
		private final String message;

		private NewYearAnnouncer(String message)
		{
			this.message = message;
		}

		public void runImpl()
		{
			Announcements.getInstance().announceToAll(message);

			// Через жопу сделано, но не суть важно :)
			if(message.length() == 1)
				return;

			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				L2Skill skill = SkillTable.getInstance().getInfo(3266, 1);
				MagicSkillUse msu = new MagicSkillUse(player, player, 3266, 1, skill.getHitTime(), 0);
				player.broadcastPacket(msu);
			}

			instance = null;
			new NewYearTimer();
		}
	}
}