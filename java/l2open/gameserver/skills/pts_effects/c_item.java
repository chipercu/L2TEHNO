package l2open.gameserver.skills.pts_effects;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {c_item;-63;5}
 * @c_item
 * @-63 - Количество ХП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_item extends L2Effect
{
	private int _item_id;
	private int _item_count;

	public c_item(Env env, EffectTemplate template, Integer item_id, Integer item_count)
	{
		super(env, template);

		add_action_timer = true;
		_item_id = item_id;
		_item_count = item_count;
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead())
			return false;
		else if(!_effected.consumeItem(_item_id, _item_count))
		{
			_effected.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			_effected.getEffectList().stopEffect(getSkill().getId());
			_effected.sendPacket(new SystemMessage(SystemMessage.S1_IS_ABORTED).addSkillName(getSkill().getId(), getSkill().getLevel()));
			return false;
		}
		return true;
	}
}