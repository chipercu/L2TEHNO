package services.Talks;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.tables.DoorTable;
import l2open.util.Files;
import l2open.util.Location;

public class HellboundTraitor extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/Traitor/";
	private static String RuFilePatch = "data/html-ru/hellbound/Traitor/";

	private static final int Leodas = 22448;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void MarksOfBetrayal()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance LeodasRB = L2ObjectsStorage.getByNpcId(Leodas);
		L2NpcInstance npc = L2ObjectsStorage.getByNpcId(32364);
		int hellboundLevel = HellboundManager.getInstance().getLevel();
		if(LeodasRB != null)
		{
			show(Files.read(EnFilePatch + "32364-7.htm", player), player);
			return;
		}
		if(hellboundLevel >= 5)
		{
			if(player.getInventory().getItemByItemId(9676) != null)
			{
				long marksCount = player.getInventory().getItemByItemId(9676).getCount();
				if(marksCount >= 1 && marksCount < 10)
				{
					if(player.getVar("lang@").equalsIgnoreCase("ru"))
						show(Files.read(RuFilePatch + "32364-6.htm", player), player);
					else
						show(Files.read(EnFilePatch + "32364-6.htm", player), player);
				}
				else if(marksCount >= 10)
				{
					removeItem(player, 9676, 10); // Marks of Betrayal
					npc.broadcastPacket(new Say2(npc.getObjectId(), 1, npc.getName(), "Brothers! This stranger wants to kill our Commander!!!"));
					DoorTable.getInstance().getDoor(19250003).openMe();
					DoorTable.getInstance().getDoor(19250004).openMe();
					ThreadPoolManager.getInstance().schedule(new DoorClose(), 1800000);
					HellboundManager.getInstance().addPoints(-50);
					Functions.spawn(new Location( -27807, 252740, -3520, 0), Leodas);
				}
			}
			else
			{
				if(player.getVar("lang@").equalsIgnoreCase("ru"))
					show(Files.read(RuFilePatch + "32364-4.htm", player), player);
				else
					show(Files.read(EnFilePatch + "32364-4.htm", player), player);
			}
		}
	}

	public class DoorClose extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			DoorTable.getInstance().getDoor(19250003).closeMe();
			DoorTable.getInstance().getDoor(19250004).closeMe();
		}
	}
}