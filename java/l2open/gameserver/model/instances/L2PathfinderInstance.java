package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.KamalokaNightmare;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.templates.L2NpcTemplate;

public class L2PathfinderInstance extends L2NpcInstance
{
	private Grade _rank = null;
	private boolean _rewarded = false;

	public static final int[][] boxes = {
	//    NG     D      C      B      A      S
			{ 00000, 00000, 00000, 00000, 00000, 00000 }, // 0-19 (can it be?)
			{ 12824, 10836, 12825, 10837, 10838, 10839 }, // 20-39
			{ 10840, 10841, 12826, 12827, 10842, 10843 }, // 40-51
			{ 10844, 10845, 10846, 12828, 12829, 10847 }, // 52-60
			{ 10848, 10849, 10850, 10851, 12830, 12831 }, // 61-75
			{ 10852, 10853, 10854, 10855, 10856, 12832 }, // 76+
	// { 12833, 10857, 10858, 10859, 10860, 10861 }, 
	// 12834,10862,10863,10864,10865 - unused
	};

	public L2PathfinderInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("ExitSoloKama"))
		{
			Reflection r = getReflection();
			if(r.getReturnLoc() != null)
				player.teleToLocation(r.getReturnLoc(), 0);
			else
				player.setReflection(0);
			player.unsetVar("backCoords");
			r.startCollapseTimer(1000);
		}
		else if(command.startsWith("ListPossible"))
		{
			StringBuffer sb = new StringBuffer("<font color=\"LEVEL\">Pathfinder Worker:</font><br>");

			if(ReflectionTable.getInstance().findSoloKamaloka(player.getObjectId()) != null)
				sb.append("Hey, what are you doing? Your work isn't done yet!<br><a action=\"bypass -h scripts_Kamaloka:SoloGatekeeper ").append(-1).append("\">Return to the Hall of Nightmares").append("</a><br>");
			else if(player.getLevel() < ConfigValue.NightmaresMinLevel || player.getLevel() > ConfigValue.NightmaresMaxLevel)
				sb.append("There are no offerings for your level.");
			else
			{
				InstancedZoneManager ilm = InstancedZoneManager.getInstance();
				if(ilm.getTimeToNextEnterInstance("Kamaloka, Hall of the Nightmares", player) > 0)
					sb.append("You can not enter hall of nightmares now, you must get some rest. Or... Maybe you have extra entrace pass?<br>");

				for(int i = 25; i <= ConfigValue.NightmaresMaxLevel-5; i += 5)
					if(player.getLevel() >= i - 5 && player.getLevel() <= i + 5)
						sb.append("<a action=\"bypass -h scripts_Kamaloka:SoloGatekeeper ").append(i).append("\">Enter Hall of the Nightmares, level ").append(i).append("</a><br>");
			}

			player.sendPacket(new NpcHtmlMessage(player, this).setHtml(sb.toString()));
		}
		else if(command.startsWith("SoloKamaReward"))
		{
			final Reflection r = getReflection();
			if(!_rewarded)
			{
				int base = ((KamalokaNightmare) r).getCounterBase().size();
				int doppler = ((KamalokaNightmare) r).getCounterDoppler().size();
				int v0id = ((KamalokaNightmare) r).getCounterV0id().size();
				int level = ((KamalokaNightmare) r).getLevel(r.getInstancedZoneId());
				final int count = ((level / 25) * (doppler + v0id + base / 5)) / 50;
				if(count > 0)
					Functions.addItem(player, KamalokaNightmare.KAMALOKA_ESSENCE, count);
				Functions.addItem(player, boxes[getGradeByLevel(player.getLevel()).externalOrdinal][calcRank().externalOrdinal], 1);
				_rewarded = true;
			}
			showChatWindow(player, 2);
		}
		else if(command.startsWith("Chat"))
			try
			{
				int val = Integer.parseInt(command.substring(5));
				showChatWindow(player, val);
			}
			catch(NumberFormatException nfe)
			{
				String filename = command.substring(5).trim();
				if(filename.length() == 0)
					showChatWindow(player, "/data/html/npcdefault.htm");
				else
					showChatWindow(player, filename);
			}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		NpcHtmlMessage res = new NpcHtmlMessage(player, this, getHtmlPath(getTemplate().npcId, val), val);
		if(getReflection().getId() > 0 && val == 1)
			res.replace("%rank%", calcRank().name());
		player.sendPacket(res);
	}

	private Grade calcRank()
	{
		if(_rank != null)
		{
			return _rank;
		}
		final Reflection r = getReflection();
		int base = ((KamalokaNightmare) r).getCounterBase().size();
		int doppler = ((KamalokaNightmare) r).getCounterDoppler().size();
		int v0id = ((KamalokaNightmare) r).getCounterV0id().size();
		int total = (int) ((base / 5) + doppler + v0id * 1.2);
		if(total >= 200)
			_rank = Grade.S;
		else if(total >= 166)
			_rank = Grade.A;
		else if(total >= 133)
			_rank = Grade.B;
		else if(total >= 100)
			_rank = Grade.C;
		else if(total >= 66)
			_rank = Grade.D;
		else
			_rank = Grade.NONE;
		return _rank;
	}

	private Grade getGradeByLevel(int level)
	{
		if(level < 20)
			return Grade.NONE;
		else if(level < 40)
			return Grade.D;
		else if(level < 52)
			return Grade.C;
		else if(level < 61)
			return Grade.B;
		else if(level < 76)
			return Grade.A;
		return Grade.S;
	}
}