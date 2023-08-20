package events.EventBuffer;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.GmListTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.GArray;

public class EventBuffer extends Functions implements ScriptFile
{

	private static int EVENT_MANAGER_ID = 39912;
	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();
	@SuppressWarnings("unused")
	private static boolean _active = false;
	@SuppressWarnings("unused")
	private static int grpCount;
	private static int buffs[][] = {
	// id, lvl, group
		// Mage Dance of Songs
		{273, 1, 8}, // Dance of Mystic
		{276, 1, 8}, // Dance of Concentration
		{264, 1, 8}, // Song of Earth
		{265, 1, 8}, // Song of Life
		{266, 1, 8}, // Song of Water
		{267, 1, 8}, // Song of Warding
		{268, 1, 8}, // Song of Wind
		{270, 1, 8}, // Song of Invocation
		{304, 1, 8}, // Song of Vitality
		{305, 1, 8}, // Song of Vengeance
		{306, 1, 8}, // Song of Flame Guard
		{308, 1, 8}, // Song of Storm Guard
		// Warrior Dance of Songs
		{271, 1, 7}, // Dance of Warrior
		{272, 1, 7}, // Dance of Inspiration
		{274, 1, 7}, // Dance of Fire
		{275, 1, 7}, // Dance of Fury
		{264, 1, 7}, // Song of Earth
		{265, 1, 7}, // Song of Life
		{266, 1, 7}, // Song of Water
		{267, 1, 7}, // Song of Warding
		{268, 1, 7}, // Song of Wind
		{269, 1, 7}, // Song of Hunter
		{270, 1, 7}, // Song of Invocation
		{304, 1, 7}, // Song of Vitality
		{305, 1, 7}, // Song of Vengeance
		{306, 1, 7}, // Song of Flame Guard
		{308, 1, 7}, // Song of Storm Guard
		// All Dance of Songs
		{271, 1, 6}, // Dance of Warrior
		{272, 1, 6}, // Dance of Inspiration
		{273, 1, 6}, // Dance of Mystic
		{274, 1, 6}, // Dance of Fire
		{275, 1, 6}, // Dance of Fury
		{276, 1, 6}, // Dance of Concentration
		{277, 1, 6}, // Dance of Light
		{307, 1, 6}, // Dance of Aqua Guard
		{309, 1, 6}, // Dance of Earth Guard
		{311, 1, 6}, // Dance of Protection
		{264, 1, 6}, // Song of Earth
		{265, 1, 6}, // Song of Life
		{266, 1, 6}, // Song of Water
		{267, 1, 6}, // Song of Warding
		{268, 1, 6}, // Song of Wind
		{269, 1, 6}, // Song of Hunter
		{270, 1, 6}, // Song of Invocation
		{304, 1, 6}, // Song of Vitality
		{305, 1, 6}, // Song of Vengeance
		{306, 1, 6}, // Song of Flame Guard
		{308, 1, 6}, // Song of Storm Guard
		// Chant
		{1251, 2, 5}, // Chant of Fury
		{1252, 3, 5}, // Chant of Evasion
		{1253, 3, 5}, // Chant of Rage
		{1284, 3, 5}, // Chant of Revenge
		{1308, 3, 5}, // Chant of Predator
		{1309, 3, 5}, // Chant of Eagle
		{1310, 4, 5}, // Chant of Vampire
		{1362, 1, 5}, // Chant of Spirit
		{1363, 1, 5}, // Chant of Victory
		{1390, 3, 5}, // War Chant
		{1391, 3, 5}, // Earth Chant
		{1461, 1, 5}, // Chant of Protection

		// Songs
		{264, 1, 4}, // Song of Earth
		{265, 1, 4}, // Song of Life
		{266, 1, 4}, // Song of Water
		{267, 1, 4}, // Song of Warding
		{268, 1, 4}, // Song of Wind
		{269, 1, 4}, // Song of Hunter
		{270, 1, 4}, // Song of Invocation
		{304, 1, 4}, // Song of Vitality
		{305, 1, 4}, // Song of Vengeance
		{306, 1, 4}, // Song of Flame Guard
		{308, 1, 4}, // Song of Storm Guard
		{349, 1, 4}, // Song of Renewal
		{363, 1, 4}, // Song of Meditation
		{364, 1, 4}, // Song of Champion

		// Dances
		{271, 1, 3}, // Dance of Warrior
		{272, 1, 3}, // Dance of Inspiration
		{273, 1, 3}, // Dance of Mystic
		{274, 1, 3}, // Dance of Fire
		{275, 1, 3}, // Dance of Fury
		{276, 1, 3}, // Dance of Concentration
		{277, 1, 3}, // Dance of Light
		{307, 1, 3}, // Dance of Aqua Guard
		{309, 1, 3}, // Dance of Earth Guard
		{310, 1, 3}, // Dance of Vampire
		{311, 1, 3}, // Dance of Protection
		{365, 1, 3}, // Dance of Siren

		// Группа для магов 2
				{4352,2, 2},
				{4342,2, 2},
				{4351,6, 2},
				{4355,3, 2},
				{4353,6, 2},
				{4356,3, 2},
				{4349,2, 2},
				{4347,6, 2},
				{4348,6, 2},
				{4344,3, 2},
				{1303,2, 2},
				{1391, 3, 2}, // Earth Chant
				{1461, 1, 2},
				{4703, 13, 2},
				{4699, 13, 2},
				{365,1, 2},
				{349,1, 2},
				{364,1, 2},
				{304,1, 2},
				{276,1, 2},
				{273,1, 2},
				{267,1, 2},
				{268,1, 2},
				{264,1, 2},
				{1413,1, 2},

		// Группа для воинов 1
				{4360,3, 1},
				{4342,2, 1},
				{4359,3, 1},
				{4358,3, 1},
				{4357,2, 1},
				{4354,4, 1},
				{4347,6, 1},
				{4348,6, 1},
				{4346,4, 1},
				{4344,3, 1},
				{4345,3, 1},
				{4352,2, 1},
				{4353,6, 1},
				{4349,2, 1},
				{1390, 3, 1}, // War Chant
				{1461, 1, 1},
				{4703, 13, 1},
				{4699, 13, 1},
				{274,1, 1},
				{275,1, 1},
				{271,1, 1},
				{310,1, 1},
				{269,1, 1},
				{268,1, 1},
				{267,1, 1},
				{264,1, 1},
				{304,1, 1},
				{349,1, 1},
				{364,1, 1},
				{1363,1, 1}
			};

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Event Buffer [state: activated]");
		}
		else
			_log.info("Loaded Event: Event Buffer [state: deactivated]");
	}

	private static boolean isActive()
	{
		return IsActive("EventBuffer");
	}

	public void startEvent()
	{
		final L2Player player = (L2Player) getSelf();
		if( !player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("EventBuffer", true))
		{
			if(ConfigValue.EVENTBUFFER_SPAWN_EVENT_NPC)
			{
				spawnEventManagers();
				_log.info("Event 'Event Buffer' started.");
				Announcements.getInstance().announceByCustomMessage("scripts.events.EventBuffer.AnnounceEventStarted", null);
				GmListTable.broadcastMessageToGMs("Эвент запущен, Npc Расставлены.");
			}
			else
				_log.info("Event 'Event Buffer' started.");
		}
		else
			player.sendMessage("Event 'Event Buffer' already started.");

		_active = true;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if( !player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive("EventBuffer", false))
		{
			unSpawnEventManagers();
			_log.info("Event 'Event Buffer' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.EventBuffer.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Event Buffer' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	private void spawnEventManagers()
	{
		int EVENT_MANAGERS[][] = {
		/* { x, y, z, heading } */
		{46856, 51496, -3003, 49648}, /* Elven Village */
		{9640, 15624, -4600, 63648}, /* Dark Elven Village */
		{ -84184, 244680, -3755, 41000}, /* Talking Island Village */
		{115080, -178232, -911, 0}, /* Dwarven Village */
		{ -45256, -112600, -265, 61743}, /* Orc Village */
		{ -116840, 46552, 341, 40352}, /* Kamael Village */
		{146744, 25896, -2039, 2872}, /* Aden */
		{83273, 147912, -3413, 16384}, /* Giran */
		{83000, 53572, -1522, 31000}, /* Oren */
		{15752, 142888, -2732, 16000}, /* Dion */
		{111448, 219416, -3572, 48000}, /* Heine */
		{ -12792, 122808, -3143, 49152}, /* Gludio */
		{147848, -55208, -2760, 48000}, /* Goddard */
		{87128, -143464, -1318, 16000}, /* Schuttgart */
		{ -80696, 149832, -3070, 20480}, /* Gludin */
		{117080, 77016, -2722, 34000}, /* Hunter */
		{43880, -47672, -822, 50000},/* Rune */
        { -184392, 243480, 1576, 52000} /* Gracia */
		};
		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	public void onReload()
	{
		unSpawnEventManagers();
	}

	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	/*************************************************************************************************************************/
	/** Для персонажа */
	/*************************************************************************************************************************/
	public void charGroup(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_PACK_ITEM_ID) < ConfigValue.EVENTBUFFER_PACK_ITEM_COUNT)
        {
			if(ConfigValue.EVENTBUFFER_PACK_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		removeItem(player, ConfigValue.EVENTBUFFER_PACK_ITEM_ID, ConfigValue.EVENTBUFFER_PACK_ITEM_COUNT);

		int id_groups = Integer.valueOf(args[0]);
		L2Skill skill;
		for(int buff[] : buffs)
			if(buff[2] == id_groups)
			{
				{
					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					if(!skill.checkSkillAbnormal(player))
						for (EffectTemplate et : skill.getEffectTemplates())
						{
							Env env = new Env(getNpc(), player, skill);
							L2Effect effect = et.getEffect(env);
							effect.setPeriod(ConfigValue.BuffTime);
							player.getEffectList().addEffect(effect);
						}
					//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), 0);
				}

			}
		show(Files.read("data/scripts/events/EventBuffer/char/menu.htm", player), player);
	}

	public void AllDanceofSongs(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_ALLDANCEOFSONGS_ITEM_ID) < ConfigValue.EVENTBUFFER_ALLDANCEOFSONGS_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_ALLDANCEOFSONGS_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		removeItem(player, ConfigValue.EVENTBUFFER_ALLDANCEOFSONGS_ITEM_ID, ConfigValue.EVENTBUFFER_ALLDANCEOFSONGS_ITEM_COUNT);

		int id_groups = Integer.valueOf(args[0]);
		L2Skill skill;
		for(int buff[] : buffs)
			if(buff[2] == id_groups)
			{
				{
					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					if(!skill.checkSkillAbnormal(player))
						for (EffectTemplate et : skill.getEffectTemplates())
						{
							Env env = new Env(getNpc(), player, skill);
							L2Effect effect = et.getEffect(env);
							effect.setPeriod(ConfigValue.BuffTime);
							player.getEffectList().addEffect(effect);
						}
					//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
				}

			}
		show(Files.read("data/scripts/events/EventBuffer/char/menu.htm", player), player);
	}

	public void WarriorDanceofSongs(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_ID) < ConfigValue.EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		removeItem(player, ConfigValue.EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_ID, ConfigValue.EVENTBUFFER_WARRIORDANCEOFSONGS_ITEM_COUNT);

		int id_groups = Integer.valueOf(args[0]);
		L2Skill skill;
		for(int buff[] : buffs)
			if(buff[2] == id_groups)
			{
				{
					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					if(!skill.checkSkillAbnormal(player))
						for (EffectTemplate et : skill.getEffectTemplates())
						{
							Env env = new Env(getNpc(), player, skill);
							L2Effect effect = et.getEffect(env);
							effect.setPeriod(ConfigValue.BuffTime);
							player.getEffectList().addEffect(effect);
						}
					//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
				}

			}
		show(Files.read("data/scripts/events/EventBuffer/char/menu.htm", player), player);
	}

	public void MageDanceofSongs(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_ID) < ConfigValue.EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		removeItem(player, ConfigValue.EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_ID, ConfigValue.EVENTBUFFER_MAGEDANCEOFSONGS_ITEM_COUNT);

		int id_groups = Integer.valueOf(args[0]);
		L2Skill skill;
		for(int buff[] : buffs)
			if(buff[2] == id_groups)
			{
				{
					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					if(!skill.checkSkillAbnormal(player))
						for (EffectTemplate et : skill.getEffectTemplates())
						{
							Env env = new Env(getNpc(), player, skill);
							L2Effect effect = et.getEffect(env);
							effect.setPeriod(ConfigValue.BuffTime);
							player.getEffectList().addEffect(effect);
						}
					//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
				}

			}
		show(Files.read("data/scripts/events/EventBuffer/char/menu.htm", player), player);
	}

	public void charOther(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_OTHER_ITEM_ID) < ConfigValue.EVENTBUFFER_OTHER_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_OTHER_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(!skill.checkSkillAbnormal(player))
				for (EffectTemplate et : skill.getEffectTemplates())
				{
					Env env = new Env(getNpc(), player, skill);
					L2Effect effect = et.getEffect(env);
					effect.setPeriod(ConfigValue.BuffTime);
					player.getEffectList().addEffect(effect);
				}
			//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_OTHER_ITEM_ID, ConfigValue.EVENTBUFFER_OTHER_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/char/buffs.htm", player), player);
	}

	public void charDance(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_DANCE_ITEM_ID) < ConfigValue.EVENTBUFFER_DANCE_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_DANCE_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(!skill.checkSkillAbnormal(player))
				for (EffectTemplate et : skill.getEffectTemplates())
				{
					Env env = new Env(getNpc(), player, skill);
					L2Effect effect = et.getEffect(env);
					effect.setPeriod(ConfigValue.BuffTime);
					player.getEffectList().addEffect(effect);
				}
			//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_DANCE_ITEM_ID, ConfigValue.EVENTBUFFER_DANCE_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/char/dances.htm", player), player);
	}

	public void charSong(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_SONG_ITEM_ID) < ConfigValue.EVENTBUFFER_SONG_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_SONG_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(!skill.checkSkillAbnormal(player))
				for (EffectTemplate et : skill.getEffectTemplates())
				{
					Env env = new Env(getNpc(), player, skill);
					L2Effect effect = et.getEffect(env);
					effect.setPeriod(ConfigValue.BuffTime);
					player.getEffectList().addEffect(effect);
				}
			//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_SONG_ITEM_ID, ConfigValue.EVENTBUFFER_SONG_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/char/songs.htm", player), player);
	}

	public void charChant(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_CHANT_ITEM_ID) < ConfigValue.EVENTBUFFER_CHANT_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_CHANT_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(!skill.checkSkillAbnormal(player))
				for (EffectTemplate et : skill.getEffectTemplates())
				{
					Env env = new Env(getNpc(), player, skill);
					L2Effect effect = et.getEffect(env);
					effect.setPeriod(ConfigValue.BuffTime);
					player.getEffectList().addEffect(effect);
				}
			//ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_CHANT_ITEM_ID, ConfigValue.EVENTBUFFER_CHANT_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/char/chants.htm", player), player);
	}

	public void cancel_buff_char()
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;
		{
			player.getEffectList().stopAllEffects();
		}
		show(Files.read("data/scripts/events/EventBuffer/char/menu.htm", player), player);
	}

	public void heal_player()
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

        if(getItemCount(player, ConfigValue.EVENTBUFFER_HEAL_ITEM_ID) < ConfigValue.EVENTBUFFER_HEAL_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_HEAL_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		removeItem(player, ConfigValue.EVENTBUFFER_HEAL_ITEM_ID, ConfigValue.EVENTBUFFER_HEAL_ITEM_COUNT);
        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		if(player.isPlayer())
			player.setCurrentCp(player.getMaxCp());
	}

	/*************************************************************************************************************************/
	/** Для пета/суммона */
	/*************************************************************************************************************************/
	public void petGroup(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();

		if( !checkCondition(player))
			return;

		if(getItemCount(player, ConfigValue.EVENTBUFFER_PACK_ITEM_ID) < ConfigValue.EVENTBUFFER_PACK_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_PACK_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
		removeItem(player, ConfigValue.EVENTBUFFER_PACK_ITEM_ID, ConfigValue.EVENTBUFFER_PACK_ITEM_COUNT);

		int id_groups = Integer.valueOf(args[0]); // group4 
		L2Skill skill;
		for(int buff[] : buffs) 
			if(buff[2] == id_groups)
			{
				{
					if(pet == null)
						return;

					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), +0);
				}
			}
		show(Files.read("data/scripts/events/EventBuffer/pet/menu.htm", player), player);
	}

	public void petOther(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();

		if(getItemCount(player, ConfigValue.EVENTBUFFER_OTHER_ITEM_ID) < ConfigValue.EVENTBUFFER_OTHER_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_OTHER_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(pet == null)
				return;
			ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_OTHER_ITEM_ID, ConfigValue.EVENTBUFFER_OTHER_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/pet/buffs.htm", player), player);
	}

	public void petDance(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();

		if(getItemCount(player, ConfigValue.EVENTBUFFER_DANCE_ITEM_ID) < ConfigValue.EVENTBUFFER_DANCE_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_DANCE_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(pet == null)
				return;
			ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_DANCE_ITEM_ID, ConfigValue.EVENTBUFFER_DANCE_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/pet/dances.htm", player), player);
	}

	public void petSong(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();

		if(getItemCount(player, ConfigValue.EVENTBUFFER_SONG_ITEM_ID) < ConfigValue.EVENTBUFFER_SONG_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_SONG_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(pet == null)
				return;
			ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_SONG_ITEM_ID, ConfigValue.EVENTBUFFER_SONG_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/pet/songs.htm", player), player);
	}

	public void petChant(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();

		if(getItemCount(player, ConfigValue.EVENTBUFFER_CHANT_ITEM_ID) < ConfigValue.EVENTBUFFER_CHANT_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_CHANT_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(pet == null)
				return;
			ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), +0);
			removeItem(player, ConfigValue.EVENTBUFFER_CHANT_ITEM_ID, ConfigValue.EVENTBUFFER_CHANT_ITEM_COUNT);
		}
		show(Files.read("data/scripts/events/EventBuffer/pet/chants.htm", player), player);
	}

	public void cancel_buff_pet()
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;
		L2Summon pet = player.getPet();
		{
			pet.getEffectList().stopAllEffects();
		}
		show(Files.read("data/scripts/events/EventBuffer/pet/menu.htm", player), player);
	}

    public void heal_pet()
	{
		L2Player player = (L2Player) getSelf();
        L2Summon pet = player.getPet();

		if( !checkCondition(player))
			return;

        if(getItemCount(player, ConfigValue.EVENTBUFFER_HEAL_ITEM_ID) < ConfigValue.EVENTBUFFER_HEAL_ITEM_COUNT)
		{
			if(ConfigValue.EVENTBUFFER_HEAL_ITEM_ID == 57)
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			else
				player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}
        if(pet == null)
			return;
        removeItem(player, ConfigValue.EVENTBUFFER_HEAL_ITEM_ID, ConfigValue.EVENTBUFFER_HEAL_ITEM_COUNT);
		pet.setCurrentHpMp(pet.getMaxHp(), pet.getMaxMp());
	}

	/*************************************************************************************************************************/

	public boolean checkCondition(L2Player player)
	{
		if(player.isInCombat() || player.isInDuel() || player.isActionsDisabled() || player.isSitting() || player.getLastNpc().getNpcId() != EVENT_MANAGER_ID || player.getLastNpc().getDistance(player) > 300)
			return false;

		if(player.getOlympiadGame() != null || Olympiad.isRegisteredInComp(player))
		{
			show("Buff sell are closed for Oly time.", player);
			return false;
		}

		String html;

		if(player.getLevel() > ConfigValue.EVENTBUFFER_MAX_LVL || player.getLevel() < ConfigValue.EVENTBUFFER_MIN_LVL)
		{
			html = Files.read("data/scripts/events/EventBuffer/no-lvl.htm", player);
			html = html.replace("%min_lvl%", Integer.toString(ConfigValue.EVENTBUFFER_MIN_LVL));
			html = html.replace("%max_lvl%", Integer.toString(ConfigValue.EVENTBUFFER_MAX_LVL));
			show(html, player);
			return false;
		}
		return true;
	}

	public void SelectMenu(String[] args)
	{
		int select_menu = Integer.valueOf(args[0]);
		L2Player player = (L2Player) getSelf();
		String html = null;

		if(select_menu == 0)
			html = Files.read("data/scripts/events/EventBuffer/char/menu.htm", player);

		if(select_menu == 1)
		{
			if(ConfigValue.EVENTBUFFER_ENABLE_PET_BUFF != true)
				return;
			if(player.getPet() == null)
				return;
			html = Files.read("data/scripts/events/EventBuffer/pet/menu.htm", player);
		}
		show(html, player);
	}

	public void open(String[] args)
	{
		L2Player player = (L2Player) getSelf();

		String html;
		html = Files.read("data/scripts/events/EventBuffer/" + args[0], player);
		show(html, player);
	}

	public void SelectBuffs()
	{
		L2Player player = (L2Player) getSelf();

		if( !checkCondition(player))
			return;

		show(Files.read("data/scripts/events/EventBuffer/buffs.htm", player), player);
	}

	public class BeginBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Player _target;

		public BeginBuff(L2Character buffer, L2Skill skill, L2Player target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			if(ConfigValue.EVENTBUFFER_ENABLE_BUFF_ANIMATION)
			{
				_buffer.broadcastPacket(new MagicSkillUse(_buffer, _target, _skill.getDisplayId(), _skill.getLevel(), _skill.getHitTime(), 0));
				if(ConfigValue.EVENTBUFFER_ENABLE_SKILL_GETHITTIME)
				{
					ThreadPoolManager.getInstance().schedule(new EndBuff(_buffer, _skill, _target), _skill.getHitTime());
				}
				else
					ThreadPoolManager.getInstance().schedule(new EndBuff(_buffer, _skill, _target), 0);
			}
			else if(ConfigValue.EVENTBUFFER_ENABLE_SKILL_GETHITTIME)
			{
				ThreadPoolManager.getInstance().schedule(new EndBuff(_buffer, _skill, _target), _skill.getHitTime());
			}
			else
				ThreadPoolManager.getInstance().schedule(new EndBuff(_buffer, _skill, _target), 0);
		}
	}

	public class EndBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Player _target;

		public EndBuff(L2Character buffer, L2Skill skill, L2Player target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			_skill.getEffects(_buffer, _target, false, false);
		}
	}

	public class BeginPetBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Summon _target;

		public BeginPetBuff(L2Character buffer, L2Skill skill, L2Summon target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			if(ConfigValue.EVENTBUFFER_ENABLE_BUFF_ANIMATION)
			{
				_buffer.broadcastPacket(new MagicSkillUse(_buffer, _target, _skill.getDisplayId(), _skill.getLevel(), _skill.getHitTime(), 0));
				if(ConfigValue.EVENTBUFFER_ENABLE_SKILL_GETHITTIME)
				{
					ThreadPoolManager.getInstance().schedule(new EndPetBuff(_buffer, _skill, _target), _skill.getHitTime());
				}
				else
					ThreadPoolManager.getInstance().schedule(new EndPetBuff(_buffer, _skill, _target), +0);
			}
			else if(ConfigValue.EVENTBUFFER_ENABLE_SKILL_GETHITTIME)
			{
				ThreadPoolManager.getInstance().schedule(new EndPetBuff(_buffer, _skill, _target), _skill.getHitTime());
			}
			else
				ThreadPoolManager.getInstance().schedule(new EndPetBuff(_buffer, _skill, _target), +0);
		}
	}

	public class EndPetBuff extends l2open.common.RunnableImpl
	{
		L2Character _buffer;
		L2Skill _skill;
		L2Summon _target;

		public EndPetBuff(L2Character buffer, L2Skill skill, L2Summon target)
		{
			_buffer = buffer;
			_skill = skill;
			_target = target;
		}

		public void runImpl()
		{
			_skill.getEffects(_buffer, _target, false, false);
		}
	}
}