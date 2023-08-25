package l2open.gameserver.serverpackets;

import l2open.gameserver.model.*;
import l2open.util.GArray;

public class ExEventMatchSpelledInfo extends L2GameServerPacket
{
	// chdd(dhd)
	private int game_id;
	private int char_obj_id;
	private GArray<Effect> _effects;

	class Effect
	{
		int skillId;
		int level;
		int duration;

		public Effect(int skillId, int level, int duration)
		{
			this.skillId = skillId;
			this.level = level;
			this.duration = duration;
		}
	}

	public ExEventMatchSpelledInfo(L2Player player, int _game_id)
	{
		_effects = new GArray<Effect>();

		game_id = _game_id;
		char_obj_id = player.getObjectId();
		L2Effect[] effects = player.getEffectList().getAllFirstEffects();
		for(L2Effect effect : effects)
			if(effect != null && effect.isInUse() && effect.getState() == L2Effect.ACTING && effect.getDisplayId() >= 0 && !effect.getSkill().isToggle())
			{
				int duration = (int) (effect.getTimeLeft() / 1000);
				_effects.add(new Effect(effect.getDisplayId(), effect.getDisplayLevel(), duration));
			}
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x04);

		writeD(game_id);
		writeD(char_obj_id);
		writeD(_effects.size());
		for(Effect temp : _effects)
		{
			writeD(temp.skillId);
			writeH(temp.level);
			writeD(temp.duration);
		}
	}
}