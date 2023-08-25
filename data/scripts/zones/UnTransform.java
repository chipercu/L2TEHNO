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

public class UnTransform extends Functions implements ScriptFile
{
	private ZoneListener _zoneListener;

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(player == null)
				return;
			player.can_transform=false;
			player.setTransformation(0);
			for(L2Effect eff : player.getEffectList().getAllEffects())
				if(eff.getSkill().getName().startsWith("Stone of "))
					eff.exit(true, false);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			L2Player player = object.getPlayer();
			if(player == null)
				return;
			player.can_transform=true;
		}
	}

	public void onLoad()
	{
		_zoneListener = new ZoneListener();
		for(int s : ConfigValue.ZoneToBlockTransform)
		{
			try
			{
				L2Zone zone = ZoneManager.getInstance().getZoneById(s);
				zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
			}
			catch(Exception e)
			{
				_log.info("UnTransform: Not find zone: "+s);
				e.printStackTrace();
			}
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}		
}