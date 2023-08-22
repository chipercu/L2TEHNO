package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.castle.CastleSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Log;

public final class L2CastleTeleporterInstance extends L2NpcInstance
{
	//private static Logger _log = Logger.getLogger(L2CastleTeleporterInstance.class.getName());

	private static int Cond_All_False = 0;
	private static int Cond_Castle_Attacker = 1;
	private static int Cond_Castle_Owner = 2;
	private static int Cond_Castle_Defender = 3;

	public L2CastleTeleporterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		int condition = validateCondition(player);
		if(condition <= Cond_All_False)
			return;

		super.onBypassFeedback(player, command);

		if(command.startsWith("CastleMassGK"))
		{
			command = command.substring(13); //срезаем ненужное
			String args[] = command.split("_");

			long delay;
			Siege activeSiege = SiegeManager.getSiege(this, true);
			if(TerritorySiege.isInProgress())
				delay = TerritorySiege.getDefenderRespawnTotal(getCastle().getId());
			else if(activeSiege != null)
				delay = ((CastleSiege) activeSiege).isAllTowersDead() ? 480000 : 30000; // Если убиты все кристалы, респ 8 минут, в противном случае 30с.
			else if(getEventMaster() != null)
				delay = getEventMaster().state; // Если убиты все кристалы, респ 8 минут, в противном случае 30с.
			else
				delay = Long.parseLong(args[0]); // аргумент 0 = время телепорта
			Log.add("MASS_TELEPORT_TALK["+delay+"]["+(player.getClan() != null ? player.getClan().getName() : "null")+"]["+getCastle()+"]: "+player, "siege_info");
			//_log.info("CastleMassGK(): "+delay);

			int x = Integer.parseInt(args[1]); // аргумент 1 = точка телепорта х
			int y = Integer.parseInt(args[2]); // аргумент 2 = точка телепорта y
			int z = Integer.parseInt(args[3]); // аргумент 3 = точка телепорта z
			int rnd = Integer.parseInt(args[4]) + 1; // аргумент 4 = дистанция случайного расброса игроков
			int radius = Integer.parseInt(args[5]); // аргумент 5 = радиус для сбора персонажей. Возможно правильнее будет переделать на зоны
			String text = args[6]; // аргумент 6 = то что орет гк при телепорте

			if(_massGkTask == null) // если не существует таск, то создать новый. Если существует - игнорить.
			{
				_massGkTask = new MassGKTask(this, x, y, z, rnd, radius, text);
				ThreadPoolManager.getInstance().schedule(_massGkTask, delay);
			}
			showChatWindow(player, ConfigValue.DatapackRoot + "/data/html/teleporter/massGK-Teleported.htm"); // выдать html-ку с ответом
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		return ConfigValue.DatapackRoot + "/data/html/teleporter/" + pom + ".htm";
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String filename;
		int cond = validateCondition(player);

		if(_massGkTask != null)
			filename = ConfigValue.DatapackRoot + "/data/html/teleporter/massGK-Teleported.htm";
		else if(cond == Cond_Castle_Owner || cond == Cond_Castle_Defender)
		{
			/*if(getNpcId() == 35502 || getNpcId() == 35547 || getNpcId() == 35095 || getNpcId() == 35137 || getNpcId() == 35179 || getNpcId() == 35221 || getNpcId() == 35266 || getNpcId() == 35311 || getNpcId() == 35355)
			{
				filename = ConfigValue.DatapackRoot + "/data/html/teleporter/" + getNpcId() + ".htm"; // Teleport message window
			}
			else*/
				filename = ConfigValue.DatapackRoot + "/data/html/teleporter/" + getNpcId() + ".htm"; // Teleport message window
		}
		else
			filename = ConfigValue.DatapackRoot + "/data/html/teleporter/castleteleporter-no.htm"; // "Go out!"

		player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
	}

	private int validateCondition(L2Player player)
	{
		if(player.isGM() || player.getEventMaster() != null && player.getEventMaster()._ref != null && player.getEventMaster()._ref.getId() == player.getReflectionId() && player.getEventMaster().siege_event)
			return Cond_Castle_Owner;

		Castle castle;
		if(player.getClan() != null && (castle = getCastle()) != null)
			if(castle.getOwnerId() == player.getClanId()) // Clan owns castle
				return Cond_Castle_Owner; // Owner
			else if(castle.getSiege().isInProgress() && castle.getSiege().checkIsAttacker(player.getClan()))
				return Cond_Castle_Attacker; // Attacker
			else if(castle.getSiege().isInProgress() && castle.getSiege().checkIsDefender(player.getClan()))
				return Cond_Castle_Defender; // Defender

		return Cond_All_False;
	}

	protected MassGKTask _massGkTask;

	public class MassGKTask extends l2open.common.RunnableImpl
	{
		L2NpcInstance _npc;
		int _x, _y, _z, _rnd, _radius;
		String _text;

		public MassGKTask(L2NpcInstance npc, int x, int y, int z, int rnd, int radius, String text)
		{
			_npc = npc;
			_x = x;
			_y = y;
			_z = z;
			_rnd = rnd;
			_radius = radius;
			_text = text;
		}

		public void runImpl()
		{
			//Functions.npcShout(_npc, _text);
			try
			{
				_npc.Shout(_npc.MakeFString(1000443,_npc.MakeFString((1001000 + _npc.getCastle().getId()),"","","","",""),"","","",""));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			for(L2Player p : L2World.getAroundPlayers(_npc, _radius, 50))
			{
				int cond = validateCondition(p);
				if(cond == Cond_Castle_Owner || cond == Cond_Castle_Defender)
				{
					p.teleToLocation(GeoEngine.findPointToStay(_x, _y, _z, 10, _rnd, getReflection().getGeoIndex()));
					Log.add("MASS_TELEPORT_PLAYER["+(p.getClan() != null ? p.getClan().getName() : "null")+"]["+_npc.getCastle()+"]: "+p, "siege_info");
				}
			}

			_massGkTask = null; //освободить для дальнейшего использования масс гк
		}
	}
}