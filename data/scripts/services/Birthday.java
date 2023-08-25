package services;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.util.Location;
import l2open.util.NpcUtils;
import l2open.util.Util;

import java.util.Calendar;

/**
 * @author
 *
 * High Five: Exchanges Explorer Hat for Birthday Hat
 */
public class Birthday extends Functions
{
	private static final int EXPLORERHAT = 10250;
	private static final int HAT = 21594; // Birthday Hat
	private static final int NPC_ALEGRIA = 32600; // Alegria


	private static final String msgSpawned = "data/scripts/services/Birthday-spawned.htm";

	/**
	 * Вызывается у гейткиперов
	 */
	public void summonAlegria()
	{
		L2Player player = (L2Player)getSelf();
		L2NpcInstance npc = getNpc();

		if(player == null || npc == null || !L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		//TODO: На оффе можно вызвать до 3х нпсов. Но зачем? о.0
		for(L2NpcInstance n : L2World.getAroundNpc(npc))
			if(n.getNpcId() == NPC_ALEGRIA)
			{
				show(msgSpawned, player, npc);
				return;
			}

		player.sendPacket(new PlaySound(1, "HB01", 0, 0, new Location()));

		try
		{
			//Спаним Аллегрию где-то спереди от ГК
			int x = (int) (npc.getX() + 40 * Math.cos(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));
			int y = (int) (npc.getY() + 40 * Math.sin(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));

			L2NpcInstance alegria = NpcUtils.spawnSingle(NPC_ALEGRIA, x, y, npc.getZ(), 180000);
			alegria.setHeading(Util.getHeadingTo(alegria.getLoc(), player.getLoc()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Вызывается у NPC Alegria
	 */
	public void exchangeHat()
	{
		L2Player player = (L2Player)getSelf();
		final L2NpcInstance npc = getNpc();

		if(player == null || npc == null || !L2NpcInstance.canBypassCheck(player, player.getLastNpc()) || npc.isBusy())
			return;

		if(Functions.getItemCount(player, EXPLORERHAT) < 1)
		{
			show("data/html/default/32600-nohat.htm", player, npc);
			return;
		}
		Functions.removeItem(player, EXPLORERHAT, 1);
		Functions.addItem(player, HAT, 1);
		show("data/html/default/32600-successful.htm", player, npc);

		long now = System.currentTimeMillis() / 1000;
		player.setVar("Birthday", String.valueOf(now), -1);

		npc.setBusy(true);

		DecayTaskManager.getInstance().addDecayTask(npc);
	}

	/**
	 * Вернет true если у чара сегодня день рождения
	 */
	private boolean isBirthdayToday(L2Player player)
	{
		if(player.getCreateTime() == 0)
			return false;

		Calendar create = Calendar.getInstance();
		create.setTimeInMillis(player.getCreateTime());
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());

		return create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) && create.get(Calendar.YEAR) != now.get(Calendar.YEAR);
	}
}