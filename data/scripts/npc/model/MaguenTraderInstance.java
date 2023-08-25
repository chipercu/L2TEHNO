package npc.model;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Location;

public final class MaguenTraderInstance extends L2NpcInstance
{
	L2NpcInstance maguen;
	public MaguenTraderInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_collector"))
		{
			if(Functions.getItemCount(player, 15487) > 0)
				showHtmlFile(player, "32735-2.htm");
			else
				Functions.addItem(player, 15487, 1);
		}
		else if(command.equalsIgnoreCase("request_maguen"))
		{
			if(maguen != null && !maguen.isDead())
			{
				showHtmlFile(player, "32735-4.htm");
			}
			else
			{
				spawnSingle(18839, this); // wild maguen
				showHtmlFile(player, "32735-3.htm");
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}

	private void spawnSingle(int npcId, L2NpcInstance actor)
	{
		L2Spawn spawn = null;
		StatsSet npcDat = null;
		L2NpcTemplate template = null;
		try
		{
			template = NpcTable.getTemplate(npcId);
			npcDat = template.getSet();
			npcDat.set("displayId", npcId);
			template.setSet(npcDat);

			spawn = new L2Spawn(template);
			spawn.setAmount(1);
			spawn.setRespawnDelay(0, 0);
			spawn.setAIParam("SPAWN="+getName());
			Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
			spawn.setLoc(pos);
			maguen = spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
