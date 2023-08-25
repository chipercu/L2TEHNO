package services.NPCBuffer;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.Util;

public class Buffer extends Functions implements ScriptFile
{
	/** Количество бафов в группах */
	private static int grpCount1, grpCount2, grpCount3, grpCount4, grpCount5;

	// Количество бафов в 1 и второй группах должно быть одинаковое
	private static int buffs[][] = { // id, lvl, group
	// Chants
			{ 1251, 2, 5 }, // Chant of Fury
			{ 1252, 3, 5 }, // Chant of Evasion
			{ 1253, 3, 5 }, // Chant of Rage
			{ 1284, 3, 5 }, // Chant of Revenge
			{ 1308, 3, 5 }, // Chant of Predator
			{ 1309, 3, 5 }, // Chant of Eagle
			{ 1310, 4, 5 }, // Chant of Vampire
			{ 1362, 1, 5 }, // Chant of Spirit
			{ 1363, 1, 5 }, // Chant of Victory
			{ 1390, 3, 5 }, // War Chant
			{ 1391, 3, 5 }, // Earth Chant
			// Songs
			{ 264, 1, 4 }, // Song of Earth
			{ 265, 1, 4 }, // Song of Life
			{ 266, 1, 4 }, // Song of Water
			{ 267, 1, 4 }, // Song of Warding
			{ 268, 1, 4 }, // Song of Wind
			{ 269, 1, 4 }, // Song of Hunter
			{ 270, 1, 4 }, // Song of Invocation
			{ 304, 1, 4 }, // Song of Vitality
			{ 305, 1, 4 }, // Song of Vengeance
			{ 306, 1, 4 }, // Song of Flame Guard
			{ 308, 1, 4 }, // Song of Storm Guard
			{ 349, 1, 4 }, // Song of Renewal
			{ 363, 1, 4 }, // Song of Meditation
			{ 364, 1, 4 }, // Song of Champion
			// Dances
			{ 271, 1, 3 }, // Dance of Warrior
			{ 272, 1, 3 }, // Dance of Inspiration
			{ 273, 1, 3 }, // Dance of Mystic
			{ 274, 1, 3 }, // Dance of Fire
			{ 275, 1, 3 }, // Dance of Fury
			{ 276, 1, 3 }, // Dance of Concentration
			{ 277, 1, 3 }, // Dance of Light
			{ 307, 1, 3 }, // Dance of Aqua Guard
			{ 309, 1, 3 }, // Dance of Earth Guard
			{ 310, 1, 3 }, // Dance of Vampire
			{ 311, 1, 3 }, // Dance of Protection
			{ 365, 1, 3 }, // Dance of Siren
			// Группа для магов 2
			{ 7059, 1, 2 }, // Wild Magic
			{ 4356, 3, 2 }, // Empower
			{ 4355, 3, 2 }, // Acumen
			{ 4352, 1, 2 }, // Berserker Spirit
			{ 4346, 4, 2 }, // Mental Shield
			{ 4351, 6, 2 }, // Concentration
			{ 4342, 2, 2 }, // Wind Walk
			{ 4347, 6, 2 }, // Bless the Body
			{ 4348, 6, 2 }, // Bless the Soul
			{ 4344, 3, 2 }, // Shield
			{ 7060, 1, 2 }, // Clarity
			{ 4350, 4, 2 }, // Resist Shock
			// Группа для воинов 1
			{ 7057, 1, 1 }, // Greater Might
			{ 4345, 3, 1 }, // Might
			{ 4344, 3, 1 }, // Shield
			{ 4349, 2, 1 }, // Magic Barrier
			{ 4342, 2, 1 }, // Wind Walk
			{ 4347, 6, 1 }, // Bless the Body
			{ 4357, 2, 1 }, // Haste
			{ 4359, 3, 1 }, // Focus
			{ 4358, 3, 1 }, // Guidance
			{ 4360, 3, 1 }, // Death Whisper
			{ 4354, 4, 1 }, // Vampiric Rage
			{ 4346, 4, 1 } // Mental Shield
	};

	public void onLoad()
	{
		if(ConfigValue.BufferEnabled)
			_log.info("Loaded Service: NPCBuffer [state: activated]");
		else
			_log.info("Loaded Service: NPCBuffer [state: deactivated]");

		for(int buff[] : buffs)
			switch(buff[2])
			{
				case 1:
					grpCount1++;
					break;
				case 2:
					grpCount2++;
					break;
				case 3:
					grpCount3++;
					break;
				case 4:
					grpCount4++;
					break;
				case 5:
					grpCount5++;
					break;
			}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	/**
	 * Бафает группу баффов, снимает плату за бафф, отображает диалог с кнопкой возврата к списку бафов
	 * @param args массив строк, где элемент 0 - id группы бафов
	 */
	public void doBuffGroup(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();
		L2NpcInstance npc = getNpc();

		if(!checkCondition(player, npc))
			return;

		if(player.getAdena() < ConfigValue.BufferPrice * (Integer.valueOf(args[1]) + 2))
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		player.reduceAdena(ConfigValue.BufferPrice * (Integer.valueOf(args[1]) + 2), true);

		int time = 0;
		int id_groups = Integer.valueOf(args[0]);
		int select_id = Integer.valueOf(args[1]);
		L2Skill skill;
		for(int buff[] : buffs)
			if(buff[2] == id_groups)
			{
				if(select_id == 0)
				{
					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					time += skill.getHitTime();
					ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), time);
					time += 200;
				}
				if(select_id == 1)
				{
					if(pet == null)
						return;

					skill = SkillTable.getInstance().getInfo(buff[0], buff[1]);
					time += skill.getHitTime();
					ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), time);
					time += 200;
				}
			}
	}

	/**
	 * Бафает один бафф, снимает плату за бафф, отображает диалог с кнопкой возврата к списку бафов
	 * @param args массив строк: элемент 0 - id скида, элемент 1 - уровень скила
	 */
	public void doBuff(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2Summon pet = player.getPet();
		L2NpcInstance npc = getNpc();

		if(!checkCondition(player, npc))
			return;

		if(player.getAdena() < ConfigValue.BufferPrice)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		try
		{
			int skill_id = Integer.valueOf(args[0]);
			int skill_lvl = Integer.valueOf(args[1]);
			int select_id = Integer.valueOf(args[2]);
			L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_lvl);
			if(select_id == 0)
				ThreadPoolManager.getInstance().schedule(new BeginBuff(getNpc(), skill, player), skill.getHitTime());
			if(select_id == 1)
			{
				if(pet == null)
					return;

				ThreadPoolManager.getInstance().schedule(new BeginPetBuff(getNpc(), skill, pet), skill.getHitTime());
			}
			player.reduceAdena(ConfigValue.BufferPrice, true);
		}
		catch(Exception e)
		{
			player.sendMessage("Invalid skill!");
		}

		show(Files.read("data/scripts/services/NPCBuffer/buffs-tolist.htm", player), player, npc);
	}

	/**
	 * Проверяет возможность бафа персонажа.<BR>
	 * В случае невозможности бафа показывает игроку html с ошибкой и возвращает false.
	 * @param player персонаж
	 * @return true, если можно бафать персонажа
	 */
	public boolean checkCondition(L2Player player, L2NpcInstance npc)
	{
		if(!ConfigValue.BufferEnabled || player == null || npc == null)
			return false;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return false;

		String html;

		// Проверяем по уровню
		if(player.getLevel() > ConfigValue.BufferMaxLvl || player.getLevel() < ConfigValue.BufferMinLvl)
		{
			html = Files.read("data/scripts/services/NPCBuffer/no-lvl.htm", player);
			html = html.replace("%min_lvl%", Integer.toString(ConfigValue.BufferMinLvl));
			html = html.replace("%max_lvl%", Integer.toString(ConfigValue.BufferMaxLvl));
			show(html, player, npc);
			return false;
		}

		//Можно ли юзать бафера во время осады?
		//if(!Config.SERVICES_BUFFER_SIEGE)
		//{
		//	Residence castle = TownManager.getInstance().getClosestTown(getSelf()).getCastle();
		//	Siege siege = castle.getSiege();
		//	if(siege == null)
		//	{
		//		show(Files.read("data/scripts/services/NPCBuffer/no-siege.htm", player), player, npc);
		//		return false;
		//	}
		//}
		return true;
	}

	/* Выбор меню */
	public void SelectMenu(String[] args)
	{
		int select_menu = Integer.valueOf(args[0]);
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		String html = null;

		if(select_menu == 0)
			html = Files.read("data/scripts/services/NPCBuffer/buffschar.htm", player);

		if(select_menu == 1)
		{
			if(ConfigValue.BufferPetEnabled != true)
				return;
			if(player.getPet() == null)
				return;
			html = Files.read("data/scripts/services/NPCBuffer/buffspet.htm", player);
		}

		assert html != null;
		html = html.replace("%grp_price1%", Util.formatAdena(ConfigValue.BufferPrice * (grpCount1 + 2)));
		html = html.replace("%grp_price2%", Util.formatAdena(ConfigValue.BufferPrice * (grpCount2 + 2)));
		html = html.replace("%grp_price3%", Util.formatAdena(ConfigValue.BufferPrice * (grpCount3 + 2)));
		html = html.replace("%grp_price4%", Util.formatAdena(ConfigValue.BufferPrice * (grpCount4 + 2)));
		html = html.replace("%grp_price5%", Util.formatAdena(ConfigValue.BufferPrice * (grpCount5 + 2)));
		html = html.replace("%buffs_in_grp1%", Integer.toString(grpCount1));
		html = html.replace("%buffs_in_grp2%", Integer.toString(grpCount2));
		html = html.replace("%buffs_in_grp3%", Integer.toString(grpCount3));
		html = html.replace("%buffs_in_grp4%", Integer.toString(grpCount4));
		html = html.replace("%buffs_in_grp5%", Integer.toString(grpCount5));
		html = html.replace("%price%", Util.formatAdena(ConfigValue.BufferPrice));
		show(html, player, npc);
	}

	/* Показывает страницу с выбором кого бафать. */
	public void SelectBuffs()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance lastNpc = player.getLastNpc();

		if(!checkCondition(player, lastNpc))
			return;

		show(Files.read("data/scripts/services/NPCBuffer/buffs.htm", player), player, lastNpc);
	}

	/**
	 * Генерит ссылку, которая в дальнейшем аппендится эвент менеждерам
	 * @return html код ссылки
	 */
	public String OutDia()
	{
		if(!ConfigValue.BufferEnabled)
			return "";
		String append = "<br><a action=\"bypass -h scripts_services.NPCBuffer.Buffer:SelectBuffs\">";
		append += new CustomMessage("scripts.services.NPCBuffer.Buffer.selectBuffs", getSelf());
		append += "</a>";
		return append;
	}

	// Далее идут аппенды диалогов эвент гейткиперам
	public String DialogAppend_31212(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31213(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31214(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31215(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31216(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31217(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31218(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31219(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31220(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31221(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31222(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31223(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31224(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31767(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_32048(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
	}

	public String DialogAppend_31768(Integer val)
	{
		if(val != 0)
			return "";
		return OutDia();
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
			if(_target.isInOlympiadMode())
				return;
			_buffer.broadcastSkill(new MagicSkillUse(_buffer, _target, _skill.getDisplayId(), _skill.getLevel(), _skill.getHitTime(), 0));
			ThreadPoolManager.getInstance().schedule(new EndBuff(_buffer, _skill, _target), _skill.getHitTime());
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
			_buffer.broadcastSkill(new MagicSkillLaunched(_buffer.getObjectId(), _skill.getId(), _skill.getLevel(), _target, _skill.isOffensive()));
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
			_buffer.broadcastSkill(new MagicSkillUse(_buffer, _target, _skill.getDisplayId(), _skill.getLevel(), _skill.getHitTime(), 0));
			ThreadPoolManager.getInstance().schedule(new EndPetBuff(_buffer, _skill, _target), _skill.getHitTime());
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
			_buffer.broadcastSkill(new MagicSkillLaunched(_buffer.getObjectId(), _skill.getId(), _skill.getLevel(), _target, _skill.isOffensive()));
		}
	}
}