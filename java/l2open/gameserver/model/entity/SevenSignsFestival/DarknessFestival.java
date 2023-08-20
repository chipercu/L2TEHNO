package l2open.gameserver.model.entity.SevenSignsFestival;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.instances.L2FestivalMonsterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

import java.util.Timer;
import java.util.TimerTask;

public class DarknessFestival extends Reflection
{
	public static final int FESTIVAL_LENGTH = 1080000; // 18 mins
	public static final int FESTIVAL_FIRST_SPAWN = 60000; // 1 min
	public static final int FESTIVAL_SECOND_SPAWN = 540000; // 9 mins
	public static final int FESTIVAL_CHEST_SPAWN = 900000; // 15 mins

	private FestivalSpawn _witchSpawn;
	private FestivalSpawn _startLocation;

	private int currentState = 0;
	private boolean _challengeIncreased = false;
	private final int _levelRange;
	private final int _cabal;

	private Timer _spawnTimer;
	private TimerTask _spawnTimerTask;

	public DarknessFestival(L2Party party, int cabal, int level)
	{
		super("Darkness Festival");
		setParty(party);
		_levelRange = level;
		_cabal = cabal;
		startCollapseTimer(FESTIVAL_LENGTH + FESTIVAL_FIRST_SPAWN);

		if(cabal == SevenSigns.CABAL_DAWN)
		{
			_witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_WITCH_SPAWNS[_levelRange]);
			_startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_PLAYER_SPAWNS[_levelRange]);
		}
		else
		{
			_witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_WITCH_SPAWNS[_levelRange]);
			_startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_PLAYER_SPAWNS[_levelRange]);
		}

		party.setReflection(this);
		setReturnLoc(party.getPartyLeader().getLoc());
		for(L2Player p : party.getPartyMembers())
		{
			p.setVar("backCoords", p.getLoc().toXYZString());
			p.getEffectList().stopAllEffects(true);
			p.teleToLocation(Rnd.coordsRandomize(_startLocation.loc, 20, 100), this.getId());
		}

		scheduleNext();
		L2NpcTemplate witchTemplate = NpcTable.getTemplate(_witchSpawn.npcId);
		// Spawn the festival witch for this arena
		try
		{
			L2Spawn npcSpawn = new L2Spawn(witchTemplate);
			npcSpawn.setLoc(_witchSpawn.loc);
			npcSpawn.setReflection(_id);
			addSpawn(npcSpawn);
			npcSpawn.doSpawn(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		sendMessageToParticipants("The festival will begin in 1 minute.");
	}

	private void scheduleNext()
	{
		switch(currentState)
		{
			case 0:
				currentState = FESTIVAL_FIRST_SPAWN;

				_spawnTimer = new Timer();
				_spawnTimerTask = new TimerTask(){
					@Override
					public void run()
					{
						spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 0);
						sendMessageToParticipants("Go!");
						scheduleNext();
					}
				};
				_spawnTimer.schedule(_spawnTimerTask, FESTIVAL_FIRST_SPAWN);
				break;
			case FESTIVAL_FIRST_SPAWN:
				currentState = FESTIVAL_SECOND_SPAWN;

				_spawnTimer = new Timer();
				_spawnTimerTask = new TimerTask(){
					@Override
					public void run()
					{
						spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 2);
						sendMessageToParticipants("Next wave arrived!");
						scheduleNext();
					}
				};
				_spawnTimer.schedule(_spawnTimerTask, FESTIVAL_SECOND_SPAWN - FESTIVAL_FIRST_SPAWN);
				break;
			case FESTIVAL_SECOND_SPAWN:
				currentState = FESTIVAL_CHEST_SPAWN;

				_spawnTimer = new Timer();
				_spawnTimerTask = new TimerTask(){
					@Override
					public void run()
					{
						spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 3);
						sendMessageToParticipants("The chests have spawned! Be quick, the festival will end soon.");
					}
				};
				_spawnTimer.schedule(_spawnTimerTask, FESTIVAL_CHEST_SPAWN - FESTIVAL_SECOND_SPAWN);
				break;
			default:
				System.out.println("WTF???");
		}
	}

	public void spawnFestivalMonsters(int respawnDelay, int spawnType)
	{
		int[][] _npcSpawns = null;
		switch(spawnType)
		{
			case 0:
			case 1:
				_npcSpawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_PRIMARY_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_PRIMARY_SPAWNS[_levelRange];
				break;
			case 2:
				_npcSpawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_SECONDARY_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_SECONDARY_SPAWNS[_levelRange];
				break;
			case 3:
				_npcSpawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_CHEST_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_CHEST_SPAWNS[_levelRange];
				break;
			default:
				return;
		}
		for(int[] element : _npcSpawns)
		{
			FestivalSpawn currSpawn = new FestivalSpawn(element);
			L2NpcTemplate npcTemplate = NpcTable.getTemplate(currSpawn.npcId);
			try
			{
				L2Spawn npcSpawn = new L2Spawn(npcTemplate);
				npcSpawn.setReflection(_id);
				npcSpawn.setLoc(currSpawn.loc);
				npcSpawn.setHeading(Rnd.get(65536));
				npcSpawn.setAmount(1);
				npcSpawn.setRespawnDelay(respawnDelay);
				npcSpawn.startRespawn();
				L2FestivalMonsterInstance festivalMob = (L2FestivalMonsterInstance) npcSpawn.doSpawn(true);
				// Set the offering bonus to 2x or 5x the amount per kill, if this spawn is part of an increased challenge or is a festival chest.
				if(spawnType == 1)
					festivalMob.setOfferingBonus(2);
				else if(spawnType == 3)
					festivalMob.setOfferingBonus(5);
				addSpawn(npcSpawn);
			}
			catch(Exception e)
			{
				System.out.println("SevenSignsFestival: Error while spawning NPC ID " + currSpawn.npcId + ": " + e);
			}
		}
	}

	public boolean increaseChallenge()
	{
		if(_challengeIncreased)
			return false;
		// Set this flag to true to make sure that this can only be done once.
		_challengeIncreased = true;
		// Spawn more festival monsters, but this time with a twist.
		spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 1);
		return true;
	}

	@Override
	public void collapse()
	{
		if(_spawnTimer != null)
		{
			_spawnTimer.cancel();
			_spawnTimer = null;
		}

		if(getParty() != null)
		{
			L2Player player = getParty().getPartyLeader();
			L2ItemInstance bloodOfferings = player.getInventory().getItemByItemId(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);

			// Check if the player collected any blood offerings during the festival.
			if(bloodOfferings != null)
			{
				long offeringCount = bloodOfferings.getCount();
				long offeringScore = offeringCount * SevenSignsFestival.FESTIVAL_OFFERING_VALUE;
				boolean isHighestScore = SevenSignsFestival.getInstance().setFinalScore(getParty(), _cabal, _levelRange, offeringScore);

				player.getInventory().destroyItem(bloodOfferings, offeringCount, true);

				// Send message that the contribution score has increased.
				player.sendPacket(new SystemMessage(SystemMessage.YOUR_CONTRIBUTION_SCORE_IS_INCREASED_BY_S1).addNumber(offeringScore));

				sendCustomMessageToParticipants("l2open.gameserver.model.entity.SevenSignsFestival.Ended");
				if(isHighestScore)
					sendMessageToParticipants("Your score is highest!");
			}
			else
				player.sendMessage(new CustomMessage("l2open.gameserver.model.instances.L2FestivalGuideInstance.BloodOfferings", player));
		}
		super.collapse();
	}

	private void sendMessageToParticipants(String s)
	{
		synchronized (_objects_lock)
		{
			for(L2Object p : _objects)
				if(p != null && p.isPlayer())
					p.getPlayer().sendMessage(s);
		}
	}

	private void sendCustomMessageToParticipants(String s)
	{
		synchronized (_objects_lock)
		{
			for(L2Object p : _objects)
				if(p != null && p.isPlayer())
					p.getPlayer().sendMessage(new CustomMessage(s, p));
		}
	}

	public void partyMemberExited()
	{
		if(getParty() == null || getParty().getMemberCount() <= 1)
			collapse();
	}

	@Override
	public boolean canChampions()
	{
		return true;
	}

	@Override
	public boolean isAutolootForced()
	{
		return true;
	}
}