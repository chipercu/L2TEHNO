package l2open.gameserver.skills.enums;

import l2open.gameserver.model.*;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.*;

/**
 * @author : Diagod
 **/
/**
 * Проверка конкретной цели, подходит ли она для наложения эффекта...
 **/
public enum AffectObject
{
	none, // не указано, значит все попавшиеся подходят под таргет...
	all, // попадают все, даже пати/клан/али
	clan // Только на сокланов...Используется только мобами...но пускай будет и для игроков...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(caster.isNpc() && ob.isNpc())
				return !((L2Character)ob).isDead() && ((L2NpcInstance)caster).getFactionId().equals(((L2NpcInstance)ob).getFactionId());
			else if(caster.isPlayer() && ob.isPlayer() && !((L2Character)ob).isDead() && caster.getPlayer().getClan() != null && ob.getPlayer().getClan() != null && ob.getPlayer().getClan() == caster.getPlayer().getClan())
				return true;
			return false;
		}
	},
	friend // игроки не авто атакебле...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(ob.isAutoAttackable(caster) /*|| !ob.isPlayable()*/) // Тут блин уточнить какой стороной их вертеть)
				return false;
			return true;
		}
	},
	hidden_place // Скрытые обьекты...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(ob.isVisible())
				return false;
			return true;
		}
	},
	invisible // Ловушки...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(ob.isTrap())
				return true;
			return false;
		}
	},
	noe // TODO: Без понятия, что такое...Используется только в 1 скиле 707 - от трансформации...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			return true;
		}
	},
	not_friend // Все авто атакейбл...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(ob.isAutoAttackable(caster)) // Тут блин уточнить какой стороной их вертеть)
				return true;
			return false;
		}
	},
	object_dead_npc_body // Труп мобов...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(ob.isNpc() && ((L2Character)ob).isDead())
				return true;
			return false;
		}
	},
	undead_real_enemy // Нежить...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			if(ob.isNpc() && ((L2Character)ob).getRace() == Race.undead)
				return true;
			return false;
		}
	},
	wyvern_object // TODO: вот здесь я хз че такое, нужно уточнять...
	{
		@Override
		public boolean validate(L2Character caster, L2Object ob)
		{
			return true;
		}
	};

	public boolean validate(L2Character caster, L2Object ob)
	{
		return true;
	}
}