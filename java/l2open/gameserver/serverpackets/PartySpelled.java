package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.util.EffectsComparator;
import l2open.util.GArray;

import java.util.Arrays;

public class PartySpelled extends L2GameServerPacket
{
	private int char_type;
	private int char_obj_id = 0;
	private GArray<Effect> _effects;

	public PartySpelled(L2Character activeChar, boolean full)
	{
		if(activeChar == null)
			return;

		char_obj_id = activeChar.getObjectId();
		char_type = activeChar.isPet() ? 1 : activeChar.isSummon() ? 2 : 0;
		// 0 - L2Player // 1 - петы // 2 - саммоны
		_effects = new GArray<Effect>();
		if(full)
		{
			L2Effect[] effects = activeChar.getEffectList().getAllFirstEffects();
			Arrays.sort(effects, EffectsComparator.getInstance());
			for(L2Effect effect : effects)
				if(effect != null && effect.isInUse())
					effect.addPartySpelledIcon(this);
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(char_obj_id == 0)
			return;

		writeC(0xf4);
		writeD(char_type);
		writeD(char_obj_id);

		writeD(_effects.size());
		for(Effect temp : _effects)
		{
			writeD(temp._skillId);
			writeH(temp._level);
			writeD(temp._duration);
		}
	}

	public void addPartySpelledEffect(int skillId, int level, int duration)
	{
		_effects.add(new Effect(skillId, level, duration));
	}

	class Effect
	{
		final int _skillId;
		final int _level;
		final int _duration;

		public Effect(int skillId, int level, int duration)
		{
			_skillId = skillId;
			_level = level;
			_duration = duration;
		}
	}
}