package zones;

import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.player.PlayerData;

import java.util.ArrayList;

public class RemoveSkill extends Functions implements ScriptFile
{
	private ZoneListener _zoneListener;
	private L2Zone zone;

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(player == null)
				return;
			L2Skill skill;
			if(player._gavnocod != null)
				player._gavnocod.clear();
			boolean update = false;
			for(int skill_id : ConfigValue.DellSkillIds)
				if((skill = player.getKnownSkill(skill_id)) != null)
				{
					if(player._gavnocod == null)
						player._gavnocod = new ArrayList<L2Skill>();
					player._gavnocod.add(skill);
					player.removeSkill(skill, false, false);
					update = true;
				}
			if(update)
			{
				//player.sendPacket(new SkillList(player));
				player.updateStats();
			}
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(player == null)
				return;
			if(player._gavnocod != null)
			{
				for(L2Skill skill : player._gavnocod)
					player.addSkill(skill, false);
				player._gavnocod.clear();
				//player.sendPacket(new SkillList(player));
				player.updateStats();
			}
		}
	}

	public void onLoad()
	{
		_zoneListener = new ZoneListener();
		for(int s : ConfigValue.DellSkillZoneIds)
		{
			zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.other, s);
			zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}		
}