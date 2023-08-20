package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.List;

/**
 * Используется на Primeval Isle NPC Vervato (id: 32104)
 *
 * @Author: SYS
 * @Date: 27/6/2007
 */
public class SummonCorpse extends Functions implements ScriptFile
{
	private static int SUMMON_PRICE = 200000;

	public void onLoad()
	{
		_log.info("Loaded Service: Summon a corpse");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	/**
	 * Телепортирует все труппы, находящиеся в группе в данный момент
	 * @return
	 */
	public void doSummon()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		String fail = Files.read("data/html/default/32104-fail.htm", player);
		String success = Files.read("data/html/default/32104-success.htm", player);

		if(!player.isInParty())
		{
			show(fail, player, npc);
			return;
		}

		int counter = 0;
		List<L2Player> partyMembers = player.getParty().getPartyMembers();
		for(L2Player partyMember : partyMembers)
			if(partyMember != null && partyMember.isDead())
			{
				counter++;
				if(player.getAdena() < SUMMON_PRICE)
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
				player.reduceAdena(SUMMON_PRICE, true);
				Location coords = new Location(11255 + Rnd.get(-20, 20), -23370 + Rnd.get(-20, 20), -3649);
				partyMember.summonCharacterRequest(player, coords, 0);
			}

		if(counter == 0)
			show(fail, player, npc);
		else
			show(success, player, npc);
	}
}