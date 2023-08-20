package communityboard;

import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Files;

import javolution.util.FastMap;

public class CommunityBoardReflection extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static enum Commands
	{
		_bbs_refenter
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(bypass.startsWith("_bbs_refenter:"))
		{
			int id = Integer.parseInt(bypass.split(":")[1]);
			refCreate(id, player);
		}
	}

	private void refCreate(int instancedZoneId, L2Player player)
	{
		if(player == null || player.isDead())
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(instancedZoneId);
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone iz = izs.get(0);
		assert iz != null;

		String name = iz.getName();
		int min_level = iz.getMinLevel();
		int max_level = iz.getMaxLevel();
		int timelimit = iz.getTimelimit();

		if(player.getLevel() < min_level || player.getLevel() > max_level || player.isInFlyingTransform())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		if(player.isInParty())
		{
			player.sendPacket(Msg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
			return;
		}

		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(player));
			return;
		}

		if(InstancedZoneManager.getInstance().getTimeToNextEnterInstance(name, player) > 0)
		{
			player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
			return;
		}

		Reflection r = new Reflection(iz.getName());
		r.setInstancedZoneId(instancedZoneId);

		for(InstancedZone i : izs.values())
		{
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
			r.FillDoors(i.getDoors());
		}

		r.setCoreLoc(r.getReturnLoc());
		r.setReturnLoc(player.getLoc());
		player.setVar("backCoords", r.getReturnLoc().toXYZString());
		player.teleToLocation(r.getTeleportLoc(), r.getId());

		//player.setVarInst(name, String.valueOf(System.currentTimeMillis()));

		if(timelimit > 0)
		{
			r.startCollapseTimer(timelimit * 60 * 1000L);
			player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timelimit));
		}
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}