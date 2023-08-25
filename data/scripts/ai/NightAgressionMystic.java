package ai;

import l2open.extensions.listeners.DayNightChangeListener;
import l2open.extensions.listeners.PropertyCollection;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * АИ для мобов, меняющих агресивность в ночное время.<BR>
 * Наследуется на прямую от Mystic.
 *
 * @Author: Death
 * @Date: 23/11/2007
 * @Time: 8:40:10
 */
public class NightAgressionMystic extends Mystic implements PropertyCollection
{
	public NightAgressionMystic(L2Character actor)
	{
		super(actor);
		GameTimeController.getInstance().getListenerEngine().addPropertyChangeListener(GameTimeControllerDayNightChange, new NightAgressionDayNightListener());
	}

	private class NightAgressionDayNightListener extends DayNightChangeListener
	{
		private NightAgressionDayNightListener()
		{
			if(GameTimeController.getInstance().isNowNight())
				switchToNight();
			else
				switchToDay();
		}

		/**
		 * Вызывается, когда на сервере наступает ночь
		 */
		@Override
		public void switchToNight()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.setAggroRange(-1);
		}

		/**
		 * Вызывается, когда на сервере наступает день
		 */
		@Override
		public void switchToDay()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.setAggroRange(0);
		}
	}
}