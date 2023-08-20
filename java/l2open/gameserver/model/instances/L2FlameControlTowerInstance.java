package l2open.gameserver.model.instances;

import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.castle.CastleSiege;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @Author: Death
 * @Date: 17/9/2007
 * @Time: 17:31:36
 *
 * TODO: обработка трэпов :)
 */
public class L2FlameControlTowerInstance extends L2ControlTowerInstance
{
	private L2Zone zone;
	private L2Skill skill;

	/**
	 * Создаеь новый инстанс вышки отвечающий за ловушки.
	 * @param objectId ObjectID с IDFactory
	 * @param template Темплейт
	 */
	public L2FlameControlTowerInstance(int objectId, L2NpcTemplate template, CastleSiege siege, int maxHp)
	{
		super(objectId, template, siege, maxHp);
	}

	/**
	 * Получение зоны с ловушкой, зо которую отвечает этот инстанс
	 * @return зона с ловушкой
	 */
	public L2Zone getZone()
	{
		return zone;
	}

	/**
	 * Установка зоны с ловушкой подконтрольной этому инстансу
	 * @param zone зона
	 */
	public void setZone(L2Zone zone)
	{
		this.zone = zone;
	}

	/**
	 * Возвращает скилл которым на даный момент пользуется зона
	 * @return скилл
	 */
	public L2Skill getSkill()
	{
		return skill;
	}

	/**
	 * Устанавливает скилл для подконтролькной зоны
	 * @param skill скилл
	 */
	public void setSkill(L2Skill skill)
	{
		this.skill = skill;
	}

	/**
	 * Обработка смерти вышки:
	 * 1). Отключение подконтрольной зоны
	 * 2). Отсылка всем кто на поле боя пакета о отключении подконтрольной зоны
	 */
	@Override
	public void onDeath()
	{
		zone.setActive(false);
		sendTrapStatus();
	}

	/**
	 * Спавн вышки, установка зоны в активную
	 * 1). Включение подконтролькной зоны
	 * 2). Отсылка всем кто на поле боя пакета о включении подконтрольной зоны
	 */
	@Override
	public void onSpawn()
	{
		zone.setActive(true);
		sendTrapStatus();
		super.onSpawn();
	}

	/**
	 * Отсылает пакеты о состоянии ловушек всем игрокам которые находятся на поле боя
	 */
	private void sendTrapStatus()
	{
		Siege s = SiegeManager.getSiege(this, true);
		if(s == null)
		{
			System.err.println("Errow while getting siege zone for L2FlameControlTowerInstance");
			return;
		}

		for(L2Player p : s.getPlayersInZone())
			s.sendTrapStatus(p, true);
	}
}