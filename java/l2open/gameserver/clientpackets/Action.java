package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2WorldRegion;
import l2open.util.*;

public class Action extends L2GameClientPacket
{
	// cddddc
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX;
	@SuppressWarnings("unused")
	private int _originY;
	@SuppressWarnings("unused")
	private int _originZ;
	private int _actionId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		_actionId = readC();// 0 for simple click  1 for shift click
	}

	@Override
	public void runImpl()
	{
		try
		{
			L2Player activeChar = getClient().getActiveChar();
			if(activeChar == null)
				return;

			if(activeChar.isOutOfControl())
			{
				activeChar.sendActionFailed();
				return;
			}

			if(activeChar.inObserverMode() && activeChar.getObservNeighbor() != null)
				for(L2WorldRegion region : activeChar.getObservNeighbor().getNeighbors())
					for(L2Object obj : region.getObjectsList(new GArray<L2Object>(region.getObjectsSize()), activeChar.getObjectId(), activeChar.getReflection()))
						if(obj != null && obj.getObjectId() == _objectId && activeChar.getTarget() != obj)
						{
							obj.onAction(activeChar, false, 0);
							return;
						}

			if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
			{
				activeChar.sendActionFailed();
				return;
			}

			L2Object obj = activeChar.getVisibleObject(_objectId);

			if(obj == null)
			{
				// Для провалившихся предметов, чтобы можно было все равно поднять
				activeChar.sendActionFailed();
				return;
			}
			
			activeChar.setActive();

			if(activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != obj)
			{
				activeChar.sendActionFailed();
				return;
			}

			obj.onAction(activeChar, _actionId == 1, 0);
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
}