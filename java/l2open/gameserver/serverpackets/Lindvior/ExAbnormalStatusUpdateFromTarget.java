package l2open.gameserver.serverpackets;

import java.util.Arrays;

import l2open.gameserver.model.L2Character;
/*import lineage2.gameserver.model.Effect;
import lineage2.gameserver.model.Skill;
import lineage2.gameserver.utils.EffectsComparator;

import org.apache.commons.lang3.ArrayUtils;*/

public class ExAbnormalStatusUpdateFromTarget extends L2GameServerPacket
{
	//private Effect[] _effectArray = Effect.EMPTY_L2EFFECT_ARRAY;
	private final int _objId;
	
	public ExAbnormalStatusUpdateFromTarget(final L2Character target)
	{
		_objId = target.getObjectId();
		/*final Effect[] el = target.getEffectList().getAllFirstEffects();
		if (el.length > 0)
		{
			for (final Effect eo : el)
			{
				if (eo.isInUse())
				{
					_effectArray = ArrayUtils.add(_effectArray, eo);
				}
			}
			Arrays.sort(_effectArray, EffectsComparator.getInstance());
		}*/
	}
	
	@Override
	protected final void writeImpl()
	{
		writeEx(0xE6);
		writeD(_objId);
		writeH(0x00);
		/*writeH(_effectArray.length);
		
		for (final Effect temp : _effectArray)
		{
			final Skill sk = temp.getSkill();
			
			writeD(sk.getDisplayId());
			writeH(sk.getDisplayLevel());
			writeD(getDisplayTypeOfEffect(sk));
			writeD(temp.getTimeLeft());
			writeD((temp.getEffector() != null) ? temp.getEffector().getObjectId() : 0);
		}*/
	}
	
	/*private static int getDisplayTypeOfEffect(final L2Skill s)
	{
		// 1 ?
		if (s.isOffensive())
		{
			return 2;
		}
		
		if (s.isToggle())
		{
			return 3;
		}
		
		if (s.isMusic())
		{
			return 4;
		}
		
		if (s.isItemSkill())
		{
			return 5;
		}
		
		if (s.isHeroic())
		{
			return 6;
		}
		
		if (s.isTrigger())
		{
			return 7;
		}
		
		return 0;
	}*/
}
