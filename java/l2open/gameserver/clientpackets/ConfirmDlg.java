package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.VoteManager;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;

import org.apache.commons.lang3.tuple.Pair;
import l2open.gameserver.listener.actor.player.OnAnswerListener;

public class ConfirmDlg extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _messageId, _answer, _requestId;

	@Override
	public void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requestId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		switch(_requestId)
		{
			case 1:
				activeChar.summonCharacterAnswer(_answer);
				break;
			case 2:
				activeChar.reviveAnswer(_answer);
				break;
			case 3:
				activeChar.scriptAnswer(_answer);
				break;
			case 4:
				if(ConfigValue.AllowWedding && activeChar.isEngageRequest())
					activeChar.engageAnswer(_answer);
				break;
			case 5:
				VoteManager.getInstance().addVote(activeChar, _answer);
				break;
			case 6:
				if(activeChar.i_ai6 < System.currentTimeMillis() || _answer != 1)
					return;
				if(activeChar.isCombatFlagEquipped() || activeChar.isTerritoryFlagEquipped())
				{
					activeChar.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
					return;
				}
				if(activeChar.isFestivalParticipant())
				{
					activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Recall.Festival", activeChar));
					return;
				}
				if(activeChar.isInOlympiadMode())
				{
					activeChar.sendPacket(Msg.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
					return;
				}
				if(activeChar.getDuel() != null || activeChar.getTeam() != 0)
				{
					activeChar.sendMessage(new CustomMessage("common.RecallInDuel", activeChar));
					return;
				}
				if(RainbowSpringSiege.getInstance().isPlayerInArena(activeChar) || activeChar.getVar("jailed") != null)
					return;

				activeChar.abortAttack(true, true);
				activeChar.abortCast(true);
				activeChar.stopMove();
				if(activeChar.isInZone(ZoneType.battle_zone) && activeChar.getZone(ZoneType.battle_zone).getRestartPoints() != null)
				{
					activeChar.teleToLocation(activeChar.getZone(ZoneType.battle_zone).getSpawn());
					return;
				}
				if(activeChar.isInZone(ZoneType.peace_zone) && activeChar.getZone(ZoneType.peace_zone).getRestartPoints() != null)
				{
					activeChar.teleToLocation(activeChar.getZone(ZoneType.peace_zone).getSpawn());
					return;
				}
				activeChar.teleToClosestTown();
				break;
			default:
				Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(true);
				if (entry == null || entry.getKey() != _requestId)
					return;

				OnAnswerListener listener = entry.getValue();
				if (_answer == 1)
					listener.sayYes(activeChar);
				else
					listener.sayNo(activeChar);
				break;

		}
	}
}