package services.villagemasters;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2VillageMasterInstance;

public class Occupation extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Loaded Service: Villagemasters [Changing occupations]");
	}

	public void onTalk30026()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		//fighter
		if(classId == ClassId.fighter)
			htmltext = "bitz003h.htm";

		//warrior, knight, rogue
		else if(classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue)
			htmltext = "bitz004.htm";
		//warlord, paladin, treasureHunter
		else if(classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
			htmltext = "bitz005.htm";
		//gladiator, darkAvenger, hawkeye
		else if(classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
			htmltext = "bitz005.htm";
		else
			htmltext = "bitz002.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30026/" + htmltext);
	}

	public void onTalk30031()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.wizard || classId == ClassId.cleric)
			htmltext = "06.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.warlock || classId == ClassId.bishop || classId == ClassId.prophet)
			htmltext = "07.htm";
		else if(classId == ClassId.mage)
			htmltext = "01.htm";
		else
			// All other Races must be out
			htmltext = "08.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30031/" + htmltext);
	}

	public void onTalk30037()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenMage)
			htmltext = "01.htm";
		else if(classId == ClassId.mage)
			htmltext = "08.htm";
		else if(classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.oracle)
			htmltext = "31.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet)
			htmltext = "32.htm";
		else if(classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elementalSummoner)
			htmltext = "32.htm";
		else
			htmltext = "33.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30037/" + htmltext);
	}

	public void onChange30037(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_FAITH_ID = 1201;
		short ETERNITY_DIAMOND_ID = 1230;
		short LEAF_OF_ORACLE_ID = 1235;
		short BEAD_OF_SEASON_ID = 1292;
		short classid = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		String htmltext = "33.htm";

		if(classid == 26 && pl.getClassId() == ClassId.elvenMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
				htmltext = "15.htm";
			else if(Level <= 19 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null)
				htmltext = "16.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
				htmltext = "17.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(ETERNITY_DIAMOND_ID, 1, false);
				pl.setClassId(classid, false);
				htmltext = "18.htm";
			}
		}
		else if(classid == 29 && pl.getClassId() == ClassId.elvenMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
				htmltext = "19.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null)
				htmltext = "20.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
				htmltext = "21.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(LEAF_OF_ORACLE_ID, 1, false);
				pl.setClassId(classid, false);
				htmltext = "22.htm";
			}
		}
		else if(classid == 11 && pl.getClassId() == ClassId.mage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
				htmltext = "23.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null)
				htmltext = "24.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
				htmltext = "25.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(BEAD_OF_SEASON_ID, 1, false);
				pl.setClassId(classid, false);
				htmltext = "26.htm";
			}
		}
		else if(classid == 15 && pl.getClassId() == ClassId.mage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
				htmltext = "27.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null)
				htmltext = "28.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
				htmltext = "29.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_FAITH_ID, 1, false);
				pl.setClassId(classid, false);
				htmltext = "30.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30037/" + htmltext);
	}

	public void onTalk30066()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.fighter)
			htmltext = "08.htm";
		else if(classId == ClassId.elvenKnight || classId == ClassId.elvenScout || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue)
			htmltext = "38.htm";
		else if(classId == ClassId.templeKnight || classId == ClassId.plainsWalker || classId == ClassId.swordSinger || classId == ClassId.silverRanger)
			htmltext = "39.htm";
		else if(classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
			htmltext = "39.htm";
		else if(classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
			htmltext = "39.htm";
		else
			htmltext = "40.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30066/" + htmltext);
	}

	public void onChange30066(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MEDALLION_OF_WARRIOR_ID = 1145;
		short SWORD_OF_RITUAL_ID = 1161;
		short BEZIQUES_RECOMMENDATION_ID = 1190;
		short ELVEN_KNIGHT_BROOCH_ID = 1204;
		short REORIA_RECOMMENDATION_ID = 1217;
		short newclass = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(newclass == 19 && classId == ClassId.elvenFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(ELVEN_KNIGHT_BROOCH_ID) == null)
				htmltext = "18.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(ELVEN_KNIGHT_BROOCH_ID) != null)
				htmltext = "19.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ELVEN_KNIGHT_BROOCH_ID) == null)
				htmltext = "20.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ELVEN_KNIGHT_BROOCH_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(ELVEN_KNIGHT_BROOCH_ID, 1, false);
				pl.setClassId(newclass, false);
				htmltext = "21.htm";
			}
		}

		if(newclass == 22 && classId == ClassId.elvenFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) == null)
				htmltext = "22.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) != null)
				htmltext = "23.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) == null)
				htmltext = "24.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(REORIA_RECOMMENDATION_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(REORIA_RECOMMENDATION_ID, 1, false);
				pl.setClassId(newclass, false);
				htmltext = "25.htm";
			}
		}

		if(newclass == 1 && classId == ClassId.fighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) == null)
				htmltext = "26.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) != null)
				htmltext = "27.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) == null)
				htmltext = "28.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MEDALLION_OF_WARRIOR_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(MEDALLION_OF_WARRIOR_ID, 1, false);
				pl.setClassId(newclass, false);
				htmltext = "29.htm";
			}
		}

		if(newclass == 4 && classId == ClassId.fighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) == null)
				htmltext = "30.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) != null)
				htmltext = "31.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) == null)
				htmltext = "32.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(SWORD_OF_RITUAL_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(SWORD_OF_RITUAL_ID, 1, false);
				pl.setClassId(newclass, false);
				htmltext = "33.htm";
			}
		}

		if(newclass == 7 && classId == ClassId.fighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) == null)
				htmltext = "34.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) != null)
				htmltext = "35.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) == null)
				htmltext = "36.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(BEZIQUES_RECOMMENDATION_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(BEZIQUES_RECOMMENDATION_ID, 1, false);
				pl.setClassId(newclass, false);
				htmltext = "37.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30066/" + htmltext);
	}

	public void onTalk30511()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.scavenger)
			htmltext = "01.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "09.htm";
		else if(classId == ClassId.bountyHunter || classId == ClassId.warsmith)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30511/" + htmltext);
	}

	public void onChange30511(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_SEARCHER_ID = 2809;
		short MARK_OF_GUILDSMAN_ID = 3119;
		short MARK_OF_PROSPERITY_ID = 3238;
		short newclass = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(newclass == 55 && classId == ClassId.scavenger)
			if(Level <= 39)
			{
				if(pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
					htmltext = "05.htm";
				else
					htmltext = "06.htm";
			}
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
				htmltext = "07.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_GUILDSMAN_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_PROSPERITY_ID, 1, false);
				pl.setClassId(newclass, false);
				htmltext = "08.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30511/" + htmltext);
	}

	public void onTalk30070()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenMage)
			htmltext = "01.htm";
		else if(classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elvenWizard || classId == ClassId.oracle)
			htmltext = "31.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet || classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elementalSummoner)
			htmltext = "32.htm";
		else if(classId == ClassId.mage)
			htmltext = "08.htm";
		else
			htmltext = "33.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30070/" + htmltext);
	}

	public void onChange30070(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_FAITH_ID = 1201;
		short ETERNITY_DIAMOND_ID = 1230;
		short LEAF_OF_ORACLE_ID = 1235;
		short BEAD_OF_SEASON_ID = 1292;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 26 && classId == ClassId.elvenMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
				htmltext = "15.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null)
				htmltext = "16.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) == null)
				htmltext = "17.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ETERNITY_DIAMOND_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(ETERNITY_DIAMOND_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "18.htm";
			}
		}
		else if(event == 29 && classId == ClassId.elvenMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
				htmltext = "19.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null)
				htmltext = "20.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) == null)
				htmltext = "21.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(LEAF_OF_ORACLE_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(LEAF_OF_ORACLE_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "22.htm";
			}
		}
		else if(event == 11 && classId == ClassId.mage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
				htmltext = "23.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null)
				htmltext = "24.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) == null)
				htmltext = "25.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(BEAD_OF_SEASON_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(BEAD_OF_SEASON_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "26.htm";
			}
		}
		else if(event == 15 && classId == ClassId.mage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
				htmltext = "27.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null)
				htmltext = "28.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) == null)
				htmltext = "29.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MARK_OF_FAITH_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_FAITH_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "30.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30070/" + htmltext);
	}

	public void onTalk30154()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.elvenMage)
			htmltext = "02.htm";
		else if(classId == ClassId.elvenWizard || classId == ClassId.oracle || classId == ClassId.elvenKnight || classId == ClassId.elvenScout)
			htmltext = "12.htm";
		else if(pl.getRace() == Race.elf)
			htmltext = "13.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30154/" + htmltext);
	}

	public void onTalk30358()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.darkFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.darkMage)
			htmltext = "02.htm";
		else if(classId == ClassId.darkWizard || classId == ClassId.shillienOracle || classId == ClassId.palusKnight || classId == ClassId.assassin)
			htmltext = "12.htm";
		else if(pl.getRace() == Race.darkelf)
			htmltext = "13.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30358/" + htmltext);
	}

	public void onTalk30498()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.scavenger || classId == ClassId.artisan)
			htmltext = "09.htm";
		else if(pl.getRace() == Race.dwarf)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30498/" + htmltext);
	}

	public void onChange30498(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short RING_OF_RAVEN_ID = 1642;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 54 && classId == ClassId.dwarvenFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(RING_OF_RAVEN_ID) == null)
				htmltext = "05.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(RING_OF_RAVEN_ID) != null)
				htmltext = "06.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(RING_OF_RAVEN_ID) == null)
				htmltext = "07.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(RING_OF_RAVEN_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(RING_OF_RAVEN_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "08.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30498/" + htmltext);
	}

	public void onTalk30499()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.scavenger || classId == ClassId.artisan)
			htmltext = "09.htm";
		else if(pl.getRace() == Race.dwarf)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30499/" + htmltext);
	}

	public void onChange30499(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short PASS_FINAL_ID = 1635;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 56 && classId == ClassId.dwarvenFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(PASS_FINAL_ID) == null)
				htmltext = "05.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(PASS_FINAL_ID) != null)
				htmltext = "06.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(PASS_FINAL_ID) == null)
				htmltext = "07.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(PASS_FINAL_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(PASS_FINAL_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "08.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30499/" + htmltext);
	}

	public void onTalk30525()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.artisan)
			htmltext = "05.htm";
		else if(classId == ClassId.warsmith)
			htmltext = "06.htm";
		else
			htmltext = "07.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30525/" + htmltext);
	}

	public void onTalk30520()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.dwarvenFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.artisan || classId == ClassId.scavenger)
			htmltext = "05.htm";
		else if(classId == ClassId.warsmith || classId == ClassId.bountyHunter)
			htmltext = "06.htm";
		else
			htmltext = "07.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30520/" + htmltext);
	}

	public void onTalk30512()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.artisan)
			htmltext = "01.htm";
		else if(classId == ClassId.dwarvenFighter)
			htmltext = "09.htm";
		else if(classId == ClassId.warsmith || classId == ClassId.bountyHunter)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30512/" + htmltext);
	}

	public void onChange30512(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_MAESTRO_ID = 2867;
		short MARK_OF_GUILDSMAN_ID = 3119;
		short MARK_OF_PROSPERITY_ID = 3238;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 57 && classId == ClassId.artisan)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_MAESTRO_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
					htmltext = "05.htm";
				else
					htmltext = "06.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_MAESTRO_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GUILDSMAN_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_PROSPERITY_ID) == null)
				htmltext = "07.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_GUILDSMAN_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_MAESTRO_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_PROSPERITY_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "08.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30512/" + htmltext);
	}

	public void onTalk30565()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.orcFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.orcRaider || classId == ClassId.orcMonk || classId == ClassId.orcShaman)
			htmltext = "09.htm";
		else if(classId == ClassId.orcMage)
			htmltext = "16.htm";
		else if(pl.getRace() == Race.orc)
			htmltext = "10.htm";
		else
			htmltext = "11.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30565/" + htmltext);
	}

	public void onTalk30109()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenKnight)
			htmltext = "01.htm";
		else if(classId == ClassId.knight)
			htmltext = "08.htm";
		else if(classId == ClassId.rogue)
			htmltext = "15.htm";
		else if(classId == ClassId.elvenScout)
			htmltext = "22.htm";
		else if(classId == ClassId.warrior)
			htmltext = "29.htm";
		else if(classId == ClassId.elvenFighter || classId == ClassId.fighter)
			htmltext = "76.htm";
		else if(classId == ClassId.templeKnight || classId == ClassId.plainsWalker || classId == ClassId.swordSinger || classId == ClassId.silverRanger)
			htmltext = "77.htm";
		else if(classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasureHunter)
			htmltext = "77.htm";
		else if(classId == ClassId.gladiator || classId == ClassId.darkAvenger || classId == ClassId.hawkeye)
			htmltext = "77.htm";
		else
			htmltext = "78.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30109/" + htmltext);
	}

	public void onChange30109(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_CHALLENGER_ID = 2627;
		short MARK_OF_DUTY_ID = 2633;
		short MARK_OF_SEEKER_ID = 2673;
		short MARK_OF_TRUST_ID = 2734;
		short MARK_OF_DUELIST_ID = 2762;
		short MARK_OF_SEARCHER_ID = 2809;
		short MARK_OF_HEALER_ID = 2820;
		short MARK_OF_LIFE_ID = 3140;
		short MARK_OF_CHAMPION_ID = 3276;
		short MARK_OF_SAGITTARIUS_ID = 3293;
		short MARK_OF_WITCHCRAFT_ID = 3307;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 20 && classId == ClassId.elvenKnight)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
					htmltext = "36.htm";
				else
					htmltext = "37.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
				htmltext = "38.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "39.htm";
			}

		else if(event == 21 && classId == ClassId.elvenKnight)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
					htmltext = "40.htm";
				else
					htmltext = "41.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
				htmltext = "42.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "43.htm";
			}

		else if(event == 5 && classId == ClassId.knight)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
					htmltext = "44.htm";
				else
					htmltext = "45.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
				htmltext = "46.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "47.htm";
			}

		else if(event == 6 && classId == ClassId.knight)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
					htmltext = "48.htm";
				else
					htmltext = "49.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
				htmltext = "50.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_WITCHCRAFT_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "51.htm";
			}

		else if(event == 8 && classId == ClassId.rogue)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
					htmltext = "52.htm";
				else
					htmltext = "53.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
				htmltext = "54.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "55.htm";
			}

		else if(event == 9 && classId == ClassId.rogue)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
					htmltext = "56.htm";
				else
					htmltext = "57.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
				htmltext = "58.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SAGITTARIUS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "59.htm";
			}

		else if(event == 23 && classId == ClassId.elvenScout)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
					htmltext = "60.htm";
				else
					htmltext = "61.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
				htmltext = "62.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "63.htm";
			}

		else if(event == 24 && classId == ClassId.elvenScout)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
					htmltext = "64.htm";
				else
					htmltext = "65.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
				htmltext = "66.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SAGITTARIUS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "67.htm";
			}

		else if(event == 2 && classId == ClassId.warrior)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
					htmltext = "68.htm";
				else
					htmltext = "69.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
				htmltext = "70.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "71.htm";
			}

		else if(event == 3 && classId == ClassId.warrior)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
					htmltext = "72.htm";
				else
					htmltext = "73.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
				htmltext = "74.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_CHAMPION_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "75.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30109/" + htmltext);
	}

	public void onTalk30115()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.elvenWizard)
			htmltext = "01.htm";
		else if(classId == ClassId.wizard)
			htmltext = "08.htm";
		else if(classId == ClassId.sorceror || classId == ClassId.necromancer || classId == ClassId.warlock)
			htmltext = "39.htm";
		else if(classId == ClassId.spellsinger || classId == ClassId.elementalSummoner)
			htmltext = "39.htm";
		else if((pl.getRace() == Race.elf || pl.getRace() == Race.human) && classId.isMage())
			htmltext = "38.htm";
		else
			htmltext = "40.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30115/" + htmltext);
	}

	public void onChange30115(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_SCHOLAR_ID = 2674;
		short MARK_OF_TRUST_ID = 2734;
		short MARK_OF_MAGUS_ID = 2840;
		short MARK_OF_LIFE_ID = 3140;
		short MARK_OF_WITCHCRFAT_ID = 3307;
		short MARK_OF_SUMMONER_ID = 3336;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 27 && classId == ClassId.elvenWizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
					htmltext = "18.htm";
				else
					htmltext = "19.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
				htmltext = "20.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_MAGUS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "21.htm";
			}

		else if(event == 28 && classId == ClassId.elvenWizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
					htmltext = "22.htm";
				else
					htmltext = "23.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
				htmltext = "24.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SUMMONER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "25.htm";
			}

		else if(event == 12 && classId == ClassId.wizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
					htmltext = "26.htm";
				else
					htmltext = "27.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
				htmltext = "28.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_MAGUS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "29.htm";
			}

		else if(event == 13 && classId == ClassId.wizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WITCHCRFAT_ID) == null)
					htmltext = "30.htm";
				else
					htmltext = "31.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WITCHCRFAT_ID) == null)
				htmltext = "32.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_WITCHCRFAT_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "33.htm";
			}

		else if(event == 14 && classId == ClassId.wizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
					htmltext = "34.htm";
				else
					htmltext = "35.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
				htmltext = "36.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SUMMONER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "37.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30115/" + htmltext);
	}

	public void onTalk30120()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.oracle)
			htmltext = "01.htm";
		else if(classId == ClassId.cleric)
			htmltext = "05.htm";
		else if(classId == ClassId.elder || classId == ClassId.bishop || classId == ClassId.prophet)
			htmltext = "25.htm";
		else if((pl.getRace() == Race.human || pl.getRace() == Race.elf) && classId.isMage())
			htmltext = "24.htm";
		else
			htmltext = "26.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30120/" + htmltext);
	}

	public void onChange30120(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_PILGRIM_ID = 2721;
		short MARK_OF_TRUST_ID = 2734;
		short MARK_OF_HEALER_ID = 2820;
		short MARK_OF_REFORMER_ID = 2821;
		short MARK_OF_LIFE_ID = 3140;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 30 || classId == ClassId.oracle)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
					htmltext = "12.htm";
				else
					htmltext = "13.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LIFE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
				htmltext = "14.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LIFE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "15.htm";
			}

		else if(event == 16 && classId == ClassId.cleric)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
					htmltext = "16.htm";
				else
					htmltext = "17.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_HEALER_ID) == null)
				htmltext = "18.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_HEALER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "19.htm";
			}

		else if(event == 17 && classId == ClassId.cleric)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
					htmltext = "20.htm";
				else
					htmltext = "21.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_TRUST_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
				htmltext = "22.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_TRUST_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_REFORMER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "23.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30120/" + htmltext);
	}

	public void onTalk30500()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.orcFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.orcMage)
			htmltext = "06.htm";
		else if(classId == ClassId.orcRaider || classId == ClassId.orcMonk || classId == ClassId.orcShaman)
			htmltext = "21.htm";
		else if(classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer)
			htmltext = "22.htm";
		else
			htmltext = "23.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30500/" + htmltext);
	}

	public void onChange30500(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_RAIDER_ID = 1592;
		short KHAVATARI_TOTEM_ID = 1615;
		short MASK_OF_MEDIUM_ID = 1631;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 45 && classId == ClassId.orcFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) == null)
				htmltext = "09.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) != null)
				htmltext = "10.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) == null)
				htmltext = "11.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MARK_OF_RAIDER_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_RAIDER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "12.htm";
			}
		}

		else if(event == 47 && classId == ClassId.orcFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) == null)
				htmltext = "13.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) != null)
				htmltext = "14.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) == null)
				htmltext = "15.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(KHAVATARI_TOTEM_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(KHAVATARI_TOTEM_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "16.htm";
			}
		}

		else if(event == 50 && classId == ClassId.orcMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) == null)
				htmltext = "17.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) != null)
				htmltext = "18.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) == null)
				htmltext = "19.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(MASK_OF_MEDIUM_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(MASK_OF_MEDIUM_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "20.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30500/" + htmltext);
	}

	public void onTalk30290()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.darkFighter)
			htmltext = "01.htm";
		else if(classId == ClassId.darkMage)
			htmltext = "08.htm";
		else if(classId == ClassId.palusKnight || classId == ClassId.assassin || classId == ClassId.darkWizard || classId == ClassId.shillienOracle)
			htmltext = "31.htm";
		else if(pl.getRace() == Race.darkelf)
			htmltext = "32.htm";
		else
			htmltext = "33.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30290/" + htmltext);
	}

	public void onChange30290(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short GAZE_OF_ABYSS_ID = 1244;
		short IRON_HEART_ID = 1252;
		short JEWEL_OF_DARKNESS_ID = 1261;
		short ORB_OF_ABYSS_ID = 1270;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 32 && classId == ClassId.darkFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) == null)
				htmltext = "15.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) != null)
				htmltext = "16.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) == null)
				htmltext = "17.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(GAZE_OF_ABYSS_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(GAZE_OF_ABYSS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "18.htm";
			}
		}

		else if(event == 35 && classId == ClassId.darkFighter)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(IRON_HEART_ID) == null)
				htmltext = "19.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(IRON_HEART_ID) != null)
				htmltext = "20.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(IRON_HEART_ID) == null)
				htmltext = "21.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(IRON_HEART_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(IRON_HEART_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "22.htm";
			}
		}

		else if(event == 39 && classId == ClassId.darkMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) == null)
				htmltext = "23.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) != null)
				htmltext = "24.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) == null)
				htmltext = "25.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(JEWEL_OF_DARKNESS_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(JEWEL_OF_DARKNESS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "26.htm";
			}
		}

		else if(event == 42 && classId == ClassId.darkMage)
		{
			if(Level <= 19 && pl.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) == null)
				htmltext = "27.htm";
			if(Level <= 19 && pl.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) != null)
				htmltext = "28.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) == null)
				htmltext = "29.htm";
			if(Level >= 20 && pl.getInventory().getItemByItemId(ORB_OF_ABYSS_ID) != null)
			{
				pl.getInventory().destroyItemByItemId(ORB_OF_ABYSS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "30.htm";
			}
		}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30290/" + htmltext);
	}

	public void onTalk30513()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.orcMonk)
			htmltext = "01.htm";
		else if(classId == ClassId.orcRaider)
			htmltext = "05.htm";
		else if(classId == ClassId.orcShaman)
			htmltext = "09.htm";
		else if(classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer)
			htmltext = "32.htm";
		else if(classId == ClassId.orcFighter || classId == ClassId.orcMage)
			htmltext = "33.htm";
		else
			htmltext = "34.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30513/" + htmltext);
	}

	public void onChange30513(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_CHALLENGER_ID = 2627;
		short MARK_OF_PILGRIM_ID = 2721;
		short MARK_OF_DUELIST_ID = 2762;
		short MARK_OF_WARSPIRIT_ID = 2879;
		short MARK_OF_GLORY_ID = 3203;
		short MARK_OF_CHAMPION_ID = 3276;
		short MARK_OF_LORD_ID = 3390;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 48 && classId == ClassId.orcMonk)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
					htmltext = "16.htm";
				else
					htmltext = "17.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
				htmltext = "18.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "19.htm";
			}

		else if(event == 46 && classId == ClassId.orcRaider)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
					htmltext = "20.htm";
				else
					htmltext = "21.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_CHAMPION_ID) == null)
				htmltext = "22.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_CHAMPION_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "23.htm";
			}

		else if(event == 51 && classId == ClassId.orcShaman)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LORD_ID) == null)
					htmltext = "24.htm";
				else
					htmltext = "25.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_LORD_ID) == null)
				htmltext = "26.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_LORD_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "27.htm";
			}

		else if(event == 52 && classId == ClassId.orcShaman)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WARSPIRIT_ID) == null)
					htmltext = "28.htm";
				else
					htmltext = "29.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_GLORY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WARSPIRIT_ID) == null)
				htmltext = "30.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_GLORY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_WARSPIRIT_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "31.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30513/" + htmltext);
	}

	public void onTalk30474()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(npc.getNpcId() == 30175)
		{
			if(classId == ClassId.shillienOracle)
				htmltext = "08.htm";
			else if(classId == ClassId.darkWizard)
				htmltext = "19.htm";
			else if(classId == ClassId.spellhowler || classId == ClassId.shillienElder || classId == ClassId.phantomSummoner)
				htmltext = "54.htm";
			else if(classId == ClassId.darkMage)
				htmltext = "55.htm";
			else
				htmltext = "56.htm";
		}
		else if(classId == ClassId.palusKnight)
			htmltext = "01.htm";
		else if(classId == ClassId.shillienOracle)
			htmltext = "08.htm";
		else if(classId == ClassId.assassin)
			htmltext = "12.htm";
		else if(classId == ClassId.darkWizard)
			htmltext = "19.htm";
		else if(classId == ClassId.shillienKnight || classId == ClassId.abyssWalker || classId == ClassId.bladedancer || classId == ClassId.phantomRanger)
			htmltext = "54.htm";
		else if(classId == ClassId.spellhowler || classId == ClassId.shillienElder || classId == ClassId.phantomSummoner)
			htmltext = "54.htm";
		else if(classId == ClassId.darkFighter || classId == ClassId.darkMage)
			htmltext = "55.htm";
		else
			htmltext = "56.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30474/" + htmltext);
	}

	public void onChange30474(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short MARK_OF_CHALLENGER_ID = 2627;
		short MARK_OF_DUTY_ID = 2633;
		short MARK_OF_SEEKER_ID = 2673;
		short MARK_OF_SCHOLAR_ID = 2674;
		short MARK_OF_PILGRIM_ID = 2721;
		short MARK_OF_DUELIST_ID = 2762;
		short MARK_OF_SEARCHER_ID = 2809;
		short MARK_OF_REFORMER_ID = 2821;
		short MARK_OF_MAGUS_ID = 2840;
		short MARK_OF_FATE_ID = 3172;
		short MARK_OF_SAGITTARIUS_ID = 3293;
		short MARK_OF_WITCHCRAFT_ID = 3307;
		short MARK_OF_SUMMONER_ID = 3336;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "No Quest";

		if(event == 33 && classId == ClassId.palusKnight)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
					htmltext = "26.htm";
				else
					htmltext = "27.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_DUTY_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_WITCHCRAFT_ID) == null)
				htmltext = "28.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_DUTY_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_WITCHCRAFT_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "29.htm";
			}

		else if(event == 34 && classId == ClassId.palusKnight)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
					htmltext = "30.htm";
				else
					htmltext = "31.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_CHALLENGER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_DUELIST_ID) == null)
				htmltext = "32.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_CHALLENGER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_DUELIST_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "33.htm";
			}

		else if(event == 43 && classId == ClassId.shillienOracle)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
					htmltext = "34.htm";
				else
					htmltext = "35.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_PILGRIM_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_REFORMER_ID) == null)
				htmltext = "36.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_PILGRIM_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_REFORMER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "37.htm";
			}

		else if(event == 36 && classId == ClassId.assassin)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
					htmltext = "38.htm";
				else
					htmltext = "39.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SEARCHER_ID) == null)
				htmltext = "40.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SEARCHER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "41.htm";
			}

		else if(event == 37 && classId == ClassId.assassin)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
					htmltext = "42.htm";
				else
					htmltext = "43.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SEEKER_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SAGITTARIUS_ID) == null)
				htmltext = "44.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SEEKER_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SAGITTARIUS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "45.htm";
			}

		else if(event == 40 && classId == ClassId.darkWizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
					htmltext = "46.htm";
				else
					htmltext = "47.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_MAGUS_ID) == null)
				htmltext = "48.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_MAGUS_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "49.htm";
			}

		else if(event == 41 && classId == ClassId.darkWizard)
			if(Level <= 39)
				if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
					htmltext = "50.htm";
				else
					htmltext = "51.htm";
			else if(pl.getInventory().getItemByItemId(MARK_OF_SCHOLAR_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_FATE_ID) == null || pl.getInventory().getItemByItemId(MARK_OF_SUMMONER_ID) == null)
				htmltext = "52.htm";
			else
			{
				pl.getInventory().destroyItemByItemId(MARK_OF_SCHOLAR_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_FATE_ID, 1, false);
				pl.getInventory().destroyItemByItemId(MARK_OF_SUMMONER_ID, 1, false);
				pl.setClassId(event, false);
				htmltext = "53.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/30474/" + htmltext);
	}

	public void onChange32145(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short SteelrazorEvaluation = 9772;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "04.htm";

		if(event == 126 && classId == ClassId.femaleSoldier)
			if(Level >= 20 && pl.getInventory().getItemByItemId(SteelrazorEvaluation) != null)
			{
				pl.getInventory().destroyItemByItemId(SteelrazorEvaluation, 1, false);
				pl.setClassId(event, false);
				htmltext = "03.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/32145/" + htmltext);
	}

	public void onTalk32145()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.femaleSoldier)
			htmltext = "01.htm";
		else
			htmltext = "02.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/32145/" + htmltext);
	}

	public void onChange32146(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short GwainsRecommendation = 9753;
		short event = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		ClassId classId = pl.getClassId();
		String htmltext = "04.htm";

		if(event == 125 && classId == ClassId.maleSoldier)
			if(Level >= 20 && pl.getInventory().getItemByItemId(GwainsRecommendation) != null)
			{
				pl.getInventory().destroyItemByItemId(GwainsRecommendation, 1, false);
				pl.setClassId(event, false);
				htmltext = "03.htm";
			}

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/32146/" + htmltext);
	}

	public void onTalk32146()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.maleSoldier)
			htmltext = "01.htm";
		else
			htmltext = "02.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/32146/" + htmltext);
	}

	public void onTalk32199()
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		String htmltext;
		ClassId classId = pl.getClassId();

		if(classId == ClassId.warder)
			htmltext = "01.htm";
		else if(classId == ClassId.trooper)
			htmltext = "11.htm";
		else
			htmltext = "02.htm";

		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/32199/" + htmltext);
	}

	public void onChange32199(String[] args)
	{
		L2Player pl = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(pl == null || npc == null)
			return;
		if(!(npc instanceof L2VillageMasterInstance))
		{
			show("I have nothing to say you", pl, npc);
			return;
		}

		short KamaelInquisitorMark = 9782;
		short SB_Certificate = 9806;
		short OrkurusRecommendation = 9760;
		short classid = Short.parseShort(args[0]);

		int Level = pl.getLevel();
		String htmltext = "02.htm";

		if(classid == 130 && pl.getClassId() == ClassId.warder)
		{
			if(Level <= 39 && pl.getInventory().getItemByItemId(KamaelInquisitorMark) == null)
				htmltext = "03.htm";
			else if(Level <= 39 && pl.getInventory().getItemByItemId(KamaelInquisitorMark) != null)
				htmltext = "04.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(KamaelInquisitorMark) == null)
				htmltext = "05.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(KamaelInquisitorMark) != null)
			{
				pl.getInventory().destroyItemByItemId(KamaelInquisitorMark, 1, false);
				pl.setClassId(classid, false);
				htmltext = "06.htm";
			}
		}
		else if(classid == 129 && pl.getClassId() == ClassId.warder)
		{
			if(Level <= 39 && pl.getInventory().getItemByItemId(SB_Certificate) == null)
				htmltext = "07.htm";
			else if(Level <= 39 && pl.getInventory().getItemByItemId(SB_Certificate) != null)
				htmltext = "08.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(SB_Certificate) == null)
				htmltext = "09.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(SB_Certificate) != null)
			{
				pl.getInventory().destroyItemByItemId(SB_Certificate, 1, false);
				pl.setClassId(classid, false);
				htmltext = "10.htm";
			}
		}
		else if(classid == 127 && pl.getClassId() == ClassId.trooper)
		{
			if(Level <= 39 && pl.getInventory().getItemByItemId(OrkurusRecommendation) == null)
				htmltext = "12.htm";
			else if(Level <= 39 && pl.getInventory().getItemByItemId(OrkurusRecommendation) != null)
				htmltext = "13.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(OrkurusRecommendation) == null)
				htmltext = "14.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(OrkurusRecommendation) != null)
			{
				pl.getInventory().destroyItemByItemId(OrkurusRecommendation, 1, false);
				pl.setClassId(classid, false);
				htmltext = "15.htm";
			}
		}
		else if(classid == 128 && pl.getClassId() == ClassId.trooper)
		{
			if(Level <= 39 && pl.getInventory().getItemByItemId(SB_Certificate) == null)
				htmltext = "16.htm";
			else if(Level <= 39 && pl.getInventory().getItemByItemId(SB_Certificate) != null)
				htmltext = "17.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(SB_Certificate) == null)
				htmltext = "18.htm";
			if(Level >= 40 && pl.getInventory().getItemByItemId(SB_Certificate) != null)
			{
				pl.getInventory().destroyItemByItemId(SB_Certificate, 1, false);
				pl.setClassId(classid, false);
				htmltext = "19.htm";
			}
		}
		((L2VillageMasterInstance) npc).showChatWindow(pl, "data/html/villagemaster/32199/" + htmltext);
	}

	public void onTalk32213()
	{
		onTalk32199();
	}

	public void onChange32213(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32214()
	{
		onTalk32199();
	}

	public void onChange32214(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32217()
	{
		onTalk32199();
	}

	public void onChange32217(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32218()
	{
		onTalk32199();
	}

	public void onChange32218(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32220()
	{
		onTalk32199();
	}

	public void onChange32220(String[] args)
	{
		onChange32199(args);
	}
	
	public void onTalk32221()
	{
		onTalk32199();
	}

	public void onChange32221(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32222()
	{
		onTalk32199();
	}

	public void onChange32222(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32205()
	{
		onTalk32199();
	}

	public void onChange32205(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32206()
	{
		onTalk32199();
	}

	public void onChange32206(String[] args)
	{
		onChange32199(args);
	}

	public void onTalk32147()
	{
		onTalk30037();
	}

	public void onTalk32150()
	{
		onTalk30565();
	}

	public void onTalk32153()
	{
		onTalk30037();
	}

	public void onTalk32154()
	{
		onTalk30066();
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}