package l2open.gameserver.model.instances;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Rnd;

public class L2FestivalMonsterInstance extends L2MonsterInstance
{
	protected int _bonusMultiplier = 1;

	/**
	 * Constructor<?> of L2FestivalMonsterInstance (use L2Character and L2NpcInstance constructor).<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Call the L2Character constructor to set the _template of the L2FestivalMonsterInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR) </li>
	 * <li>Set the name of the L2MonsterInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it </li><BR><BR>
	 *
	 * @param objectId Identifier of the object to initialized
	 * @param template Template to apply to the NPC
	 */
	public L2FestivalMonsterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void setOfferingBonus(int bonusMultiplier)
	{
		_bonusMultiplier = bonusMultiplier;
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();
	}

	@Override
	public void spawnMe()
	{
		super.spawnMe();

		GArray<L2Player> pl = L2World.getAroundPlayers(this);
		if(pl.isEmpty())
			return;
		GArray<L2Player> alive = new GArray<L2Player>(9);
		for(L2Player p : pl)
			if(!p.isDead())
				alive.add(p);
		if(alive.isEmpty())
			return;

		L2Player target = alive.get(Rnd.get(alive.size()));
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
	}

	/**
	 * Actions:
	 * <li>Check if the killing object is a player, and then find the party they belong to.</li>
	 * <li>Add a blood offering item to the leader of the party.</li>
	 * <li>Update the party leader's inventory to show the new item addition.</li>
	 */
	@Override
	public void doItemDrop(L2Character topDamager)
	{
		super.doItemDrop(topDamager);

		if(!topDamager.isPlayable())
			return;

		L2Player topDamagerPlayer = topDamager.getPlayer();
		L2Party associatedParty = topDamagerPlayer.getParty();

		if(associatedParty == null)
			return;

		L2Player partyLeader = associatedParty.getPartyLeader();
		if(partyLeader == null)
			return;

		L2ItemInstance bloodOfferings = ItemTemplates.getInstance().createItem(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);

		int mult = 1;
		if(getChampion() == 1)
			mult = 12;
		else if(getChampion() == 2)
			mult = 75;
		bloodOfferings.setCount(_bonusMultiplier * mult);
		partyLeader.getInventory().addItem(bloodOfferings);
		partyLeader.sendPacket(SystemMessage.obtainItems(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING, _bonusMultiplier * mult, 0));
	}

	@Override
	public boolean isAggressive()
	{
		return true;
	}

	@Override
	public int getAggroRange()
	{
		return 1000;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}