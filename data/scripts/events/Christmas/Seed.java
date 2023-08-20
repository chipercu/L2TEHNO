package events.Christmas;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;

public class Seed implements IItemHandler, ScriptFile
{
	public class DeSpawnScheduleTimerTask extends l2open.common.RunnableImpl
	{
		L2NpcInstance spawnedTree = null;

		public DeSpawnScheduleTimerTask(L2NpcInstance spawn)
		{
			spawnedTree = spawn;
		}

		public void runImpl()
		{
			try
			{
				spawnedTree.deleteMe();
			}
			catch(Throwable t)
			{}
		}
	}

	private static int[] _itemIds = { 5560, // Christmas Tree
			5561 // Special Christmas Tree
	};

	private static int[] _npcIds = { 13006, // Christmas Tree
			13007 // Special Christmas Tree
	};

	private static final int DESPAWN_TIME = 600000; //10 min

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		L2Player activeChar = (L2Player) playable;
		L2NpcTemplate template = null;

		int itemId = item.getItemId();
		for(int i = 0; i < _itemIds.length; i++)
			if(_itemIds[i] == itemId)
			{
				template = NpcTable.getTemplate(_npcIds[i]);
				break;
			}

		for(L2NpcInstance npc : L2World.getAroundNpc(activeChar, 300, 200))
			if(npc.getNpcId() == _npcIds[0] || npc.getNpcId() == _npcIds[1])
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addNpcName(npc.getNpcId()));
				return;
			}

		// Запрет на саммон елок слищком близко к другим НПЦ
		if(activeChar.isInCombatZone() || activeChar.isOnSiegeField() || L2World.getAroundNpc(activeChar, 100, 200).size() > 0 || activeChar.isInZone(L2Zone.ZoneType.epic))
		{
			activeChar.sendPacket(Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
			return;
		}
		else if(template == null)
			return;

		try
		{
			L2NpcInstance npc = template.getNewInstance();
            npc.setReflection(0);
            npc.setSpawnedLoc(activeChar.getLoc());
			npc.setTitle(activeChar.getName());
            npc.onSpawn();
            npc.spawnMe(npc.getSpawnedLoc());

			// АИ вешающие бафф регена устанавливается только для большой елки
			if(itemId == 5561)
			{
				npc.setAI(new ctreeAI(npc));
				npc.getAI().startAITask();
			}

			ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(npc), (activeChar.isInPeaceZone() ? DESPAWN_TIME / 3 : DESPAWN_TIME));
			activeChar.getInventory().destroyItem(item.getObjectId(), 1, false);
		}
		catch(Exception e)
		{
			activeChar.sendPacket(Msg.YOUR_TARGET_CANNOT_BE_FOUND);
		}
	}

	public int[] getItemIds()
	{
		return _itemIds;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}