package commands.user;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IUserCommandHandler;
import l2open.gameserver.handler.UserCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * Support for /olympiadstat command
 */
public class OlympiadStat implements IUserCommandHandler, ScriptFile
{
	private static final int[] COMMAND_IDS = { 109 };

	public boolean useUserCommand(int id, L2Player activeChar)
	{
		if(id != COMMAND_IDS[0])
			return false;

		L2Player target = activeChar;
		if(ConfigValue.OlympiadStatTarget && activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
			target = activeChar.getTarget().getPlayer();

		if(!target.isNoble())
			activeChar.sendPacket(Msg.THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE);
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.THE_CURRENT_FOR_THIS_OLYMPIAD_IS_S1_WINS_S2_DEFEATS_S3_YOU_HAVE_EARNED_S4_OLYMPIAD_POINTS);
			sm.addNumber(Olympiad.getCompetitionDone(target.getObjectId()));
			sm.addNumber(Olympiad.getCompetitionWin(target.getObjectId()));
			sm.addNumber(Olympiad.getCompetitionLoose(target.getObjectId()));
			sm.addNumber(Olympiad.getNoblePoints(target.getObjectId()));
			activeChar.sendPacket(sm);

			sm = new SystemMessage(3261);
			sm.addNumber(ConfigValue.MaxCompForAll - Olympiad.getAllCompetitionCount(target.getObjectId()));
			sm.addNumber(Olympiad.getMaxForCompType(CompType.CLASSED) - Olympiad.getCompetitionCount(target.getObjectId(), CompType.CLASSED));
			sm.addNumber(Olympiad.getMaxForCompType(CompType.NON_CLASSED) - Olympiad.getCompetitionCount(target.getObjectId(), CompType.NON_CLASSED));
			sm.addNumber(Olympiad.getMaxForCompType(CompType.TEAM) - Olympiad.getCompetitionCount(target.getObjectId(), CompType.TEAM));
			activeChar.sendPacket(sm);
		}
		return true;
	}

	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}

	public void onLoad()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}