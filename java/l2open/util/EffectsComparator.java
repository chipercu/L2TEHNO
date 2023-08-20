package l2open.util;

import l2open.gameserver.model.L2Effect;

import java.util.Comparator;

public class EffectsComparator implements Comparator<L2Effect>
{
	private static final EffectsComparator instance = new EffectsComparator();

	public static final EffectsComparator getInstance()
	{
		return instance;
	}

	/**
	 * 1. Положительные эффекты - Показываются обычные положительные эффекты. Персонаж может получить до 20-ти положительных умений, но если выучить Божественное Вдохновение, то можно увеличить количество до 24-х.
	 * 2. Включаемые умения - Показываются включаемые положительные эффекты. Количество таких эффектов не ограниченно, и показывается после обычных эффектов.
	 * 3. Песни / Танцы - Показываются Песни / Танцы. Одновременно может действовать до 12-ти шт.
	 * 4. Срабатывающие Умения - Показываются эффекты, срабатывающие при определенных условиях (при нанесение или полученнии урона и т.д). Одновременно может действовать до 24-х шт.
	 * 5. Отрицательные эффекты - Показываются отрицательные эффекты. Можно получить максимум до 12-ти шт.
	 * 6. Штрафы - Показываются такие вещи, как штраф смерти, штраф по весу или рангу и т.д. А также показывается энергия или уровень духа некоторых классов.
	**/
	@Override
	public synchronized int compare(L2Effect e1, L2Effect e2)
	{
		if(e1 == null || e2 == null)
			return 0;
		boolean toggle1 = e1.getSkill().isToggle();
		boolean toggle2 = e2.getSkill().isToggle();

		if(toggle1 && toggle2)
			return compareStartTime(e1, e2);

		if(toggle1 || toggle2)
			if(toggle1)
				return 1;
			else
				return -1;

		boolean music1 = e1.getSkill().isMusic();
		boolean music2 = e2.getSkill().isMusic();

		if(music1 && music2)
			return compareStartTime(e1, e2);

		if(music1 || music2)
			if(music1)
				return 1;
			else
				return -1;

		boolean trigger1 = e1.getSkill().isTrigger();
		boolean trigger2 = e2.getSkill().isTrigger();

		if(trigger1 && trigger2)
			return compareStartTime(e1, e2);

		if(trigger1 || trigger2)
			if(trigger1)
				return 1;
			else
				return -1;

		boolean offensive1 = e1.isOffensive() && !e1.getSkill().isPenalty();
		boolean offensive2 = e2.isOffensive() && !e2.getSkill().isPenalty();

		if(offensive1 && offensive2)
			return compareStartTime(e1, e2);

		if(offensive1 || offensive2)
			if (!offensive1)
				return 1;
			else
				return -1;

		boolean penalty1 = e1.getSkill().isPenalty();
		boolean penalty2 = e2.getSkill().isPenalty();

		if(penalty1 && penalty2)
			return compareStartTime(e1, e2);

		if(penalty1 || penalty2)
			if (!penalty1)
				return 1;
			else
				return -1;

		return compareStartTime(e1, e2);
	}

	private int compareStartTime(L2Effect o1, L2Effect o2)
	{
		/*if(o1.getDisplayId() > o2.getDisplayId())
			return 1;

		if(o1.getDisplayId() < o2.getDisplayId())
			return -1;*/
		if(o1.getPeriodStartTime() > o2.getPeriodStartTime())
			return 1;

		if(o1.getPeriodStartTime() < o2.getPeriodStartTime())
			return -1;

		return 0;
	}
}