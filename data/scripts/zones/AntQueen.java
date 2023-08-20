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

public class AntQueen extends Functions implements ScriptFile
{
	private ZoneListener _zoneListener = new ZoneListener();

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
		if(ConfigValue.AntQueenUnTransform)
		{
			L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.epic, 702102);
			zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		}
		if(ConfigValue.BaiumUnTransform)
		{
			L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.epic, 702001);
			zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		}
		if(ConfigValue.AntharasUnTransform)
		{
			L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.epic, 702002);
			zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		}
		if(ConfigValue.ValakasUnTransform)
		{
			L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.epic, 702003);
			zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}		
}