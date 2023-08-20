package l2open.gameserver.listener.actor.player.impl;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.listener.actor.player.OnAnswerListener;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.skillclasses.Call;
import l2open.util.reference.HardReference;

/**
 * @author VISTALL
 * @date 11:28/15.04.2011
 */
public class SummonAnswerListener implements OnAnswerListener
{
	private final HardReference<L2Player> _summonerRef;
	private final int _itemId;
	private final long _count;
	private final long _timeStamp;

	public SummonAnswerListener(L2Player summoner, L2Player player, int itemConsumeId, long itemConsumeCount, int expiration)
	{
		_summonerRef = summoner.getRef();
		_itemId = itemConsumeId;
		_count = itemConsumeCount;
		_timeStamp = expiration > 0 ? System.currentTimeMillis() + expiration : Long.MAX_VALUE;
	}

	@Override
	public void sayYes(L2Player player)
	{
		if(System.currentTimeMillis() > _timeStamp)
			return;

		L2Player summoner = _summonerRef.get();
		if(summoner == null)
			return;

		if(Call.canSummonHere(summoner) != null)
			return;

		if(Call.canBeSummoned(player) != null)
			return;

		player.abortAttack(true, true);
		player.abortCast(true);
		player.stopMove();

		if(_itemId == 0 || _count == 0)
			player.teleToLocation(summoner.getLoc());
		else if(Functions.removeItem(player, _itemId, _count) != 0)
			player.teleToLocation(summoner.getLoc());
		else
			player.sendPacket(new SystemMessage(SystemMessage.INCORRECT_ITEM_COUNT));
	}

	@Override
	public void sayNo(L2Player player)
	{
		//
	}
}
