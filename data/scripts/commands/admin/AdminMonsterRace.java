package commands.admin;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.MonsterRace;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.DeleteObject;
import l2open.gameserver.serverpackets.MonRaceInfo;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.util.Location;

@SuppressWarnings("unused")
public class AdminMonsterRace implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_mons
	}

	protected static int state = -1;

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(fullString.equalsIgnoreCase("admin_mons"))
		{
			if(!activeChar.getPlayerAccess().MonsterRace)
				return false;
			handleSendPacket(activeChar);
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleSendPacket(L2Player activeChar)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race
		 * -1 0 to end the race
		 *
		 * 8003 to 8027
		 */

		int[][] codes = { { -1, 0 }, { 0, 15322 }, { 13765, -1 }, { -1, 0 } };
		MonsterRace race = MonsterRace.getInstance();

		if(state == -1)
		{
			state++;
			race.newRace();
			race.newSpeeds();
			activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));
		}
		else if(state == 0)
		{
			state++;
			activeChar.sendPacket(Msg.THEYRE_OFF);
			activeChar.broadcastPacket(new PlaySound("S_Race"));
			activeChar.broadcastPacket(new PlaySound(0, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559)));
			activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));

			ThreadPoolManager.getInstance().schedule(new RunRace(codes, activeChar), 5000);
		}
	}

	class RunRace extends l2open.common.RunnableImpl
	{
		private int[][] codes;
		private L2Player activeChar;

		public RunRace(int[][] codes, L2Player activeChar)
		{
			this.codes = codes;
			this.activeChar = activeChar;
		}

		public void runImpl()
		{
			// int[][] speeds1 = MonsterRace.getInstance().getSpeeds();
			// MonsterRace.getInstance().newSpeeds();
			// int[][] speeds2 = MonsterRace.getInstance().getSpeeds();
			/*
			 * int[] speed = new int[8]; for(int i=0; i<8; i++) { for(int j=0; j<20;
			 * j++) { //System.out.println("Adding "+speeds1[i][j] +" and "+
			 * speeds2[i][j]); speed[i] += (speeds1[i][j]*1);// + (speeds2[i][j]*1); }
			 * System.out.println("Total speed for "+(i+1)+" = "+speed[i]); }
			 */

			activeChar.broadcastPacket(new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds()));
			ThreadPoolManager.getInstance().schedule(new RunEnd(activeChar), 30000);
		}
	}

	class RunEnd extends l2open.common.RunnableImpl
	{
		private L2Player activeChar;

		public RunEnd(L2Player activeChar)
		{
			this.activeChar = activeChar;
		}

		public void runImpl()
		{
			L2NpcInstance obj;

			for(int i = 0; i < 8; i++)
			{
				obj = MonsterRace.getInstance().getMonsters()[i];
				// FIXME i don't know, if it's needed (Styx)
				// L2World.removeObject(obj);
				activeChar.broadcastPacket(new DeleteObject(obj));

			}
			state = -1;
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}