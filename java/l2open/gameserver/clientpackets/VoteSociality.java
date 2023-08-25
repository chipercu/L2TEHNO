package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;

public class VoteSociality extends L2GameClientPacket
{
	// format: cd
	@SuppressWarnings("unused")
	private int _targetid;

	@Override
	public void readImpl()
	{
		_targetid = readD();
	}

	@Override
	public void runImpl()
	{
		SystemMessage sm;

		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(!activeChar.getPlayerAccess().CanEvaluate)
			return;

		if(activeChar.getTarget() == null)
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return;
		}

		L2Player target = activeChar.getTarget().getPlayer();

		if(target == null)
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return;
		}

		if(activeChar.getLevel() < 10)
		{
			sm = Msg.ONLY_LEVEL_SUP_10_CAN_RECOMMEND;
			activeChar.sendPacket(sm);
			return;
		}

		if(target == activeChar)
		{
			sm = Msg.YOU_CANNOT_RECOMMEND_YOURSELF;

			activeChar.sendPacket(sm);
			return;
		}

		if(activeChar.getRecommendation().getRecomLeft() <= 0)
		{
			sm = Msg.NO_MORE_RECOMMENDATIONS_TO_HAVE;
			activeChar.sendPacket(sm);
			return;
		}

		if(target.getRecommendation().getRecomHave() >= 255)
		{
			sm = Msg.YOU_NO_LONGER_RECIVE_A_RECOMMENDATION;
			activeChar.sendPacket(sm);
			return;
		}

		activeChar.getRecommendation().giveRecom(target);
		sm = new SystemMessage(SystemMessage.YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT);
		sm.addString(target.getName());
		sm.addNumber(activeChar.getRecommendation().getRecomLeft());
		activeChar.sendPacket(sm);

		sm = new SystemMessage(SystemMessage.YOU_HAVE_BEEN_RECOMMENDED_BY_C1);
		sm.addString(activeChar.getName());
		target.sendPacket(sm);

		activeChar.sendUserInfo(false);
		target.broadcastUserInfo(true);
	}
}