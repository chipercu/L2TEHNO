package ai;

import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * AI охраны в Pagan Temple.<br>
 * <li>Не умеют ходить
 * <li>Видит всех в режиме Silent Move
 * <li>Бьют физ атакой игроков, подошедших на расстояние удара
 * <li>Бьют магией, если были атакованы
 * <li>Социальны к собратьям, помогают атакуя магией
 * <li>В случае, если игрок вышел за пределы агро радиуса прекращают использовать дальнобойную магию
 *
 * @author SYS
 */
public class PaganGuard extends Mystic
{
	public PaganGuard(L2Character actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected boolean checkTarget(L2Character target, boolean canSelf, int range)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && target != null && !actor.isInRange(target, actor.getAggroRange()))
		{
			target.removeFromHatelist(actor, true);
			return false;
		}
		return super.checkTarget(target, canSelf, range);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}