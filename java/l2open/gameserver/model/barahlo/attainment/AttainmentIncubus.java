package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Files;

/**
Система достижений giran.online:
Разработать уникальную поуровневую систему достижений, выведенную в Community Board. 
Каждое достижение имеет по несколько уровней, получая которые в автоматическом режиме игроку выдается определенная награда.
При получении определенного достижения игроку выходит сообщение: Вы получили *achievename* - ур n! Поздравляем, Ваш подарок: *reward*!
Внешний вид в кб - https://ibb.co/d8amT6
Список достижений и наград за них:

EnableAttainment=true
AttainmentType=1376
Attainment13_Minute=1

# Проверка по железу в пк/пвп
AttainmentHwidProtect = true

AttainmentIn_PvE = 1000,2000,5000,7000,10000,14000,18000,22000,26000,30000
AttainmentIn_PvE_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1

AttainmentIn_PvP = 100,200,400,600,900,1200,1600,2000,2500,3000
AttainmentIn_PvP_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1

AttainmentIn_Pk = 50,100,200,300,450,600,800,1000,1250,1500
AttainmentIn_Pk_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1

AttainmentIn_Time = 24,48,96,144,243,288,384,480,600,720
AttainmentIn_Time_Reward = 57,1;57,1;57,1;57,1;57,1;57,1;57,1;57,1;57,1;57,1

AttainmentIn_RbKill = 5,10,15,20,25,30,40,50,60,70
AttainmentIn_RbKill_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1

AttainmentIn_Instance = 10,20,40,60,80,100,130,160,200,250

AttainmentIn_EnchantWeapon = 30,90,120,150,180,210,240,270,300,330
AttainmentIn_EnchantWeapon_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1

AttainmentIn_EnchantArmor = 30,90,120,150,180,210,240,270,300,330
AttainmentIn_EnchantArmor_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1;7029,1

AttainmentIn_Quest = 30,80,130,200,300
AttainmentIn_Quest_Reward = 7029,1;7029,1;7029,1;7029,1;7029,1
AttainmentIn_QuestOnlyDisposable = true

AttainmentIn_Craft = 50,200,500
AttainmentIn_Craft_Reward = 7029,1;7029,1;7029,1

**/
public class AttainmentIncubus extends Attainment
{
	public AttainmentIncubus(L2Player owner)
	{
		super(owner);
	}

	/**
	Добавить помощника по прокачке.
	То есть, при достижении определенных уровней вылетает окошко, которое предлагает воспользоваться бесплатным телепортом в следующую локацию для прокачки с картинкой.
	Также при достижении 20 и 40 уровня открывается окошко с выбором профессии за Адену. (помощник работает только до 75 уровня)
	**/
	public void incLevel()
	{
		int level = _owner.getLevel();
		int state = -1;

		for(int i=0;i < ConfigValue.AttainmentIn_Helper.length/2;i++)
		{
			//System.out.println("incLevel: level="+level+"["+ConfigValue.AttainmentIn_Helper[i*2]+"]["+ConfigValue.AttainmentIn_Helper[i*2+1]+"]");
			if(level >= ConfigValue.AttainmentIn_Helper[i*2] && level <= ConfigValue.AttainmentIn_Helper[i*2+1] && _owner.getVarInt("AttainmentHelper", -1) < i)
			{
				state=i;
				break;
			}
		}

		if(state > -1)
		{
			_owner.setVar("AttainmentHelper", String.valueOf(state));
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot+"helper/state"+state+".htm", _owner);
			NpcHtmlMessage html = new NpcHtmlMessage(_owner, null);

			content = content.replace("<?value?>", String.valueOf(state));

			html.setHtml(content);
			_owner.sendPacket(html);
		}
	}

	/**
	AttainmentIn_PvE = 1000,2000,5000,7000,10000,14000,18000,22000,26000,30000
	Великий охотник (10 уровней) - нужно убить n количество монстров, зависимо от уровня достижения:
	1 уровень - убить 1.000 монстров, при достижении игрок получает +1% к урону в ПВЕ;
	2 уровень - убить 2.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 2%);
	3 уровень - убить 5.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 3%);
	4 уровень - убить 7.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 4%);
	5 уровень - убить 10.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 5%);
	6 уровень - убить 14.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 6%);
	7 уровень - убить 18.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 7%);
	8 уровень - убить 22.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 8%);
	9 уровень - убить 26.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 9%);
	10 уровень - убить 30.000 монстров, при достижении игрок получает +1% к урону в ПВЕ (в общем 10%).
	**/
	public void incPve(final L2Character died, boolean last_hit)
	{
		int a_level = _owner.getVarInt("Attainment1_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_PvE.length)
		{
			_owner.setVar("AttainmentPvE", String.valueOf(++_pve_count));
			int a_count = ConfigValue.AttainmentIn_PvE[a_level];
			if(a_count <= _pve_count)
			{
				setSkill(ConfigValue.AttainmentIn_PvE_Reward[a_level], "Великий охотник", a_level+1);
				_owner.setVar("Attainment1_Level", String.valueOf(++a_level));
			}
		}
	}

	/**
	AttainmentIn_PvP = 100,200,400,600,900,1200,1600,2000,2500,3000
	Великий боец (10 уровней) - нужно победить в ПВП n количество игроков, зависимо от уровня достижения:
	1 уровень - победить 100 игроков, при достижении игрок получает +0.2% к урону в ПВП (в общем 0.2%);
	2 уровень - победить 200 игроков, при достижении игрок получает +0.4% к урону в ПВП (в общем 0.4%);
	3 уровень - победить 400 игроков, при достижении игрок получает +0.6% к урону в ПВП (в общем 0.6%);
	4 уровень - победить 600 игроков, при достижении игрок получает +0.8% к урону в ПВП (в общем 0.8%);
	5 уровень - победить 900 игроков, при достижении игрок получает +1.0% к урону в ПВП (в общем 1.0%);
	6 уровень - победить 1200 игроков, при достижении игрок получает +1.2% к урону в ПВП (в общем 1.2%);
	7 уровень - победить 1600 игроков, при достижении игрок получает +1.4% к урону в ПВП (в общем 1.4%);
	8 уровень - победить 2000 игроков, при достижении игрок получает +1.6% к урону в ПВП (в общем 1.6%);
	9 уровень - победить 2500 игроков, при достижении игрок получает +1.8% к урону в ПВП (в общем 1.8%);
	10 уровень - победить 3000 игроков, при достижении игрок получает +2.0% к урону в ПВП (в общем 2.0%);
	**/
	public void incPvp(final L2Player died)
	{
		if(ConfigValue.AttainmentHwidProtect && died.getHWIDs().equals(_owner.getHWIDs()) || ConfigValue.AttainmentKillProtect && died.no_kill_time())
			return;
		int a_level = _owner.getVarInt("Attainment2_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_PvP.length)
		{
			_owner.setVar("AttainmentPvP", String.valueOf(++_pvp_count));
			int a_count = ConfigValue.AttainmentIn_PvP[a_level];
			if(a_count <= _pvp_count)
			{
				setSkill(ConfigValue.AttainmentIn_PvP_Reward[a_level], "Великий боец", a_level+1);
				_owner.setVar("Attainment2_Level", String.valueOf(++a_level));
			}
		}
	}

	/**
	AttainmentIn_Pk = 50,100,200,300,450,600,800,1000,1250,1500
	Великий убийца (10 уровней) - нужно убить n количество игроков, зависимо от уровня достижения: //ПК
	1 уровень - убить 50 игроков, при достижении игрок получает +0.2% к защите в ПВП (в общем 0.2%);
	2 уровень - убить 100 игроков, при достижении игрок получает +0.4% к защите в ПВП (в общем 0.4%);
	3 уровень - убить 200 игроков, при достижении игрок получает +0.6% к защите в ПВП (в общем 0.6%);
	4 уровень - убить 300 игроков, при достижении игрок получает +0.8% к защите в ПВП (в общем 0.8%);
	5 уровень - убить 450 игроков, при достижении игрок получает +1.0% к защите в ПВП (в общем 1.0%);
	6 уровень - убить 600 игроков, при достижении игрок получает +1.2% к защите в ПВП (в общем 1.2%);
	7 уровень - убить 800 игроков, при достижении игрок получает +1.4% к защите в ПВП (в общем 1.4%);
	8 уровень - убить 1000 игроков, при достижении игрок получает +1.6% к защите в ПВП (в общем 1.6%);
	9 уровень - убить 1250 игроков, при достижении игрок получает +1.8% к защите в ПВП (в общем 1.8%);
	10 уровень - убить 1500 игроков, при достижении игрок получает +2.0% к защите в ПВП (в общем 2.0%).
	**/
	public void incPk(final L2Player died)
	{
		if(ConfigValue.AttainmentHwidProtect && died.getHWIDs().equals(_owner.getHWIDs()) || ConfigValue.AttainmentKillProtect && died.no_kill_time())
			return;
		int a_level = _owner.getVarInt("Attainment3_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_Pk.length)
		{
			_owner.setVar("AttainmentPk", String.valueOf(++_pk_count));
			int a_count = ConfigValue.AttainmentIn_Pk[a_level];
			if(a_count <= _pk_count)
			{
				setSkill(ConfigValue.AttainmentIn_Pk_Reward[a_level], "Великий убийца", a_level+1);
				_owner.setVar("Attainment3_Level", String.valueOf(++a_level));
			}
		}
	}

	/**
	Attainment13_Minute=1
	AttainmentIn_Time = 24,48,96,144,243,288,384,480,600,720
	Боец со стажем (10 уровней) - нужно провести в игре n количество часов, зависимо от уровня достижения:
	1 уровень - провести в игре 1 сутки (24 часа), при достижении игрок получает 10 ивент-монет;
	2 уровень - провести в игре 2 суток, при достижении игрок получает 15 ивент-монет;
	3 уровень - провести в игре 4 суток, при достижении игрок получает 25 ивент-монет;
	4 уровень - провести в игре 6 суток, при достижении игрок получает 40 ивент-монет;
	5 уровень - провести в игре 9 суток, при достижении игрок получает 60 ивент-монет;
	6 уровень - провести в игре 12 суток, при достижении игрок получает 80 ивент-монет;
	7 уровень - провести в игре 16 суток, при достижении игрок получает 100 ивент-монет;
	8 уровень - провести в игре 20 суток, при достижении игрок получает 120 ивент-монет;
	9 уровень - провести в игре 25 суток, при достижении игрок получает 140 ивент-монет;
	10 уровень - провести в игре 30 суток, при достижении игрок получает 150 ивент-монет.
	**/
	public void incTime()
	{
		int a_level = _owner.getVarInt("Attainment4_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_Time.length)
		{
			int a_count = ConfigValue.AttainmentIn_Time[a_level]*60;

			_owner.setVar("Attainment4_time", String.valueOf(++_time_tick));
			if(a_count <= _time_tick)
			{
				_owner.sendMessage("Вы получили *Боец со стажем* - ур "+(a_level+1)+"!");
				setReward(ConfigValue.AttainmentIn_Time_Reward[a_level]);
				_owner.setVar("Attainment4_Level", String.valueOf(++a_level));
			}
		}
		else
		{
			_owner.setVar("Attainment13", String.valueOf(true)); // так и должно быть
			_owner.stopAttainmentTask();
		}
	}

	/**
	AttainmentIn_RbKill = 5,10,15,20,25,30,40,50,60,70
	Элитный охотник (10 уровней) - нужно убить n количество боссов, зависимо от уровня достижения: //урон в пве и урон по боссам стакается
	1 уровень - убить 5 боссов, при достижении игрок получает +1% к урону по боссам (в общем 1%);
	1 уровень - убить 10 боссов, при достижении игрок получает +1% к урону по боссам (в общем 2%);
	1 уровень - убить 15 боссов, при достижении игрок получает +1% к урону по боссам (в общем 3%);
	1 уровень - убить 20 боссов, при достижении игрок получает +1% к урону по боссам (в общем 4%);
	1 уровень - убить 25 боссов, при достижении игрок получает +1% к урону по боссам (в общем 5%);
	1 уровень - убить 30 боссов, при достижении игрок получает +1% к урону по боссам (в общем 6%);
	1 уровень - убить 40 боссов, при достижении игрок получает +1% к урону по боссам (в общем 7%);
	1 уровень - убить 50 боссов, при достижении игрок получает +1% к урону по боссам (в общем 8%);
	1 уровень - убить 60 боссов, при достижении игрок получает +1% к урону по боссам (в общем 9%);
	1 уровень - убить 70 боссов, при достижении игрок получает +1% к урону по боссам (в общем 10%).
	**/
	public void setKillRaid(L2Character raid)
	{
		int a_level = _owner.getVarInt("Attainment5_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_RbKill.length)
		{
			_owner.setVar("AttainmentRbKill", String.valueOf(++_rb_kill_count));
			int a_count = ConfigValue.AttainmentIn_RbKill[a_level];
			if(a_count <= _rb_kill_count)
			{
				setSkill(ConfigValue.AttainmentIn_RbKill_Reward[a_level], "Элитный охотник", a_level+1);
				_owner.setVar("Attainment5_Level", String.valueOf(++a_level));
			}
		}
	}

	/**
	AttainmentIn_Instance = 10,20,40,60,80,100,130,160,200,250
	Владыка подземелий (10 уровней) - нужно пройти n количество подземелий, зависимо от уровня достижения: //без камалок
	1 уровень - пройти 10 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	2 уровень - пройти 20 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	3 уровень - пройти 40 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	4 уровень - пройти 60 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	5 уровень - пройти 80 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	6 уровень - пройти 100 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	7 уровень - пройти 130 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	8 уровень - пройти 160 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	9 уровень - пройти 200 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов;
	10 уровень - пройти 250 подземелий, при достижении игрок получает 1 билет обнуления времени инстансов.
	**/
	// TODO:
	public void incReflection(int id)
	{
		int a_level = _owner.getVarInt("Attainment6_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_Instance.length)
		{
			_owner.setVar("AttainmentReflectionCount", String.valueOf(++_reflection_count));
			int a_count = ConfigValue.AttainmentIn_Instance[a_level];
			if(a_count <= _reflection_count)
			{
				_owner.sendMessage("Вы получили *Владыка подземелий* - ур "+(a_level+1)+"!");
				setReward(ConfigValue.AttainmentIn_Instance_Reward[a_level]);
				_owner.setVar("Attainment6_Level", String.valueOf(++a_level));
			}
		}
	}

	/**
	AttainmentIn_EnchantWeapon = 30,90,120,150,180,210,240,270,300,330
	Счастливчик по оружию (10 уровней) - нужно удачно модифицировать n количество раз оружие:
	1 уровень - удачно модифицировать оружие 30 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.1%);
	2 уровень - удачно модифицировать оружие 90 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.2%);
	3 уровень - удачно модифицировать оружие 120 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.3%);
	4 уровень - удачно модифицировать оружие 150 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.4%);
	5 уровень - удачно модифицировать оружие 180 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.5%);
	6 уровень - удачно модифицировать оружие 210 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.6%);
	7 уровень - удачно модифицировать оружие 240 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.7%);
	8 уровень - удачно модифицировать оружие 270 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.8%);
	9 уровень - удачно модифицировать оружие 300 раз, при достижении игрок получает +0.1% к уровню CP (в общем 0.9%);
	10 уровень - удачно модифицировать оружие 330 раз, при достижении игрок получает +0.1% к уровню CP (в общем 1.0%).

	AttainmentIn_EnchantArmor = 30,90,120,150,180,210,240,270,300,330
	Счастливчик по доспехам (10 уровней) - нужно удачно модифицировать n количество раз броню: //бижа+броня
	1 уровень - удачно модифицировать доспехи 30 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.1%);
	2 уровень - удачно модифицировать доспехи 90 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.2%);
	3 уровень - удачно модифицировать доспехи 120 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.3%);
	4 уровень - удачно модифицировать доспехи 150 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.4%);
	5 уровень - удачно модифицировать доспехи 180 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.5%);
	6 уровень - удачно модифицировать доспехи 210 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.6%);
	7 уровень - удачно модифицировать доспехи 240 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.7%);
	8 уровень - удачно модифицировать доспехи 270 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.8%);
	9 уровень - удачно модифицировать доспехи 300 раз, при достижении игрок получает +0.1% к уровню HP (в общем 0.9%);
	10 уровень - удачно модифицировать доспехи 330 раз, при достижении игрок получает +0.1% к уровню HP (в общем 1.0%).
	**/
	public void enchant_sucess(int level, int safe_level, int ench_scrol, boolean is_weapon)
	{
		if(safe_level >= level)
			return;

		if(is_weapon)
		{
			int a_level = _owner.getVarInt("Attainment7_Level", 0);
			if(a_level < ConfigValue.AttainmentIn_EnchantWeapon.length)
			{
				_owner.setVar("AttainmentEnchSucessWeapon", String.valueOf(++_enchant_weapon));
				int a_count = ConfigValue.AttainmentIn_EnchantWeapon[a_level];
				if(a_count <= _enchant_weapon)
				{
					setSkill(ConfigValue.AttainmentIn_EnchantWeapon_Reward[a_level], "Счастливчик по оружию", a_level+1);
					_owner.setVar("Attainment7_Level", String.valueOf(++a_level));
				}
			}
		}
		else
		{
			int a_level = _owner.getVarInt("Attainment8_Level", 0);
			if(a_level < ConfigValue.AttainmentIn_EnchantArmor.length)
			{
				_owner.setVar("AttainmentEnchSucessArmor", String.valueOf(++_enchant_armor));
				int a_count = ConfigValue.AttainmentIn_EnchantArmor[a_level];
				if(a_count <= _enchant_armor)
				{
					setSkill(ConfigValue.AttainmentIn_EnchantArmor_Reward[a_level], "Счастливчик по доспехам", a_level+1);
					_owner.setVar("Attainment8_Level", String.valueOf(++a_level));
				}
			}
		}
	}
	/**
	AttainmentIn_Quest = 30,80,130,200,300
	Специалист по заданиям (5 уровней) - нужно пройти n количество заданий: //квесты
	1 уровень - пройти 30 зайданий, при достижении игрок получает +1 к скорости бега (в общем 1);
	2 уровень - пройти 80 зайданий, при достижении игрок получает +1 к скорости бега (в общем 2);
	3 уровень - пройти 130 зайданий, при достижении игрок получает +1 к скорости бега (в общем 3);
	4 уровень - пройти 200 зайданий, при достижении игрок получает +1 к скорости бега (в общем 4);
	5 уровень - пройти 300 зайданий, при достижении игрок получает +1 к скорости бега (в общем 5).
	**/
	public void questComplet(QuestState qs)
	{
		int a_level = _owner.getVarInt("Attainment9_Level", 0);
		if(a_level < ConfigValue.AttainmentIn_Quest.length)
		{
			_owner.setVar("AttainmentQuestComplet", String.valueOf(++_quest_complet));
			int a_count = ConfigValue.AttainmentIn_Quest[a_level];
			if(a_count <= _quest_complet)
			{
				setSkill(ConfigValue.AttainmentIn_Quest_Reward[a_level], "Специалист по заданиям", a_level+1);
				_owner.setVar("Attainment9_Level", String.valueOf(++a_level));
			}
		}
	}

	/**
	AttainmentIn_Craft = 50,200,500
	Великий созадтель (3 уровня) - нужно создать n количество master work предметов: 
	1 уровень - создать 50 уникальных предметов, при достижении игрок получает +1% к шансу крафта МВ, даблкрафта (в общем +1%);
	2 уровень - создать 200 уникальных предметов, при достижении игрок получает +1% к шансу крафта МВ, даблкрафта (в общем +2%);
	3 уровень - создать 500 уникальных предметов, при достижении игрок получает +1% к шансу крафта МВ, даблкрафта (в общем +3%).
	**/
	public void incCraft(boolean mw)
	{
		if(mw)
		{
			int a_level = _owner.getVarInt("Attainment10_Level", 0);
			if(a_level < ConfigValue.AttainmentIn_Craft.length)
			{
				_owner.setVar("AttainmentCraftCount", String.valueOf(++_craft_count));
				int a_count = ConfigValue.AttainmentIn_Craft[a_level];
				if(a_count <= _craft_count)
				{
					setSkill(ConfigValue.AttainmentIn_Craft_Reward[a_level], "Великий созадтель", a_level+1);
					_owner.setVar("Attainment10_Level", String.valueOf(++a_level));
				}
			}
		}
	}

	// Для отображения иконок в комунке.
	public int getAttainmentState(int id)
	{
		return _owner.getVarInt("Attainment"+id+"_Level", 0);
	}

	public String getIcon(int id)
	{
		int level = _owner.getVarInt("Attainment"+id+"_Level", 0);
		switch(id)
		{
			case 1:
				switch(level)
				{
					case 0:
						return "GO.ach_hunter.hunting_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_hunter.hunting_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_hunter.hunting_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_hunter.hunting_gold_64_10";
				}
				break;
			case 2:
				switch(level)
				{
					case 0:
						return "GO.ach_fighter.pvp_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_fighter.pvp_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_fighter.pvp_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_fighter.pvp_gold_64_10";
				}
				break;
			case 3:
				switch(level)
				{
					case 0:
						return "GO.ach_great_killer.pk_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_great_killer.pk_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_great_killer.pk_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_great_killer.pk_gold_64_10";
				}
				break;
			case 4:
				switch(level)
				{
					case 0:
						return "GO.ach_figner_skill.fight_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_figner_skill.fight_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_figner_skill.fight_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_figner_skill.fight_gold_64_10";
				}
				break;
			case 5:
				switch(level)
				{
					case 0:
						return "GO.ach_great_hunter.rb_kill_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_great_hunter.rb_kill_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_great_hunter.rb_kill_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_great_hunter.rb_kill_gold_64_10";
				}
				break;
			case 6:
				switch(level)
				{
					case 0:
						return "GO.ach_dungeon.dungeon_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_dungeon.dungeon_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_dungeon.dungeon_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_dungeon.dungeon_gold_64_10";
				}
				break;
			case 7:
				switch(level)
				{
					case 0:
						return "GO.ach_lucky_weap.weapon_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_lucky_weap.weapon_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_lucky_weap.weapon_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_lucky_weap.weapon_gold_64_10";
				}
				break;
			case 8:
				switch(level)
				{
					case 0:
						return "GO.ach_lucky_armor.armor_bronze_64_tex";
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						return "GO.ach_lucky_armor.armor_bronze_64_"+level;
					case 6:
					case 7:
					case 8:
					case 9:
						return "GO.ach_lucky_armor.armor_silver_64_"+level;
					case 10:
					case 11:
						return "GO.ach_lucky_armor.armor_gold_64_10";
				}
				break;
			case 9:
				switch(level)
				{
					case 0:
						return "GO.ach_quest.Quest_bronze_64_tex";
					case 1:
					case 2:
					case 3:
						return "GO.ach_quest.Quest_bronze_64_"+level;
					case 4:
						return "GO.ach_quest.Quest_silver_64_"+level;
					case 5:
					case 6:
						return "GO.ach_quest.Quest_gold_64_5";
				}
				break;
			case 10:
				switch(level)
				{
					case 0:
						return "GO.ach_item.item_bonze_64_tex";
					case 1:
						return "GO.ach_item.item_bonze_64_1";
					case 2:
						return " GO.ach_item.item_silver_64_2";
					case 3:
					case 4:
						return "GO.ach_item.item_gold_64_3";
				}
				break;
		}
		return "";
	}
	/**
	* return: level, cur_count, need_count
	**/
	public int[] getAttainmentStats(int id)
	{
		int a_level = _owner.getVarInt("Attainment"+id+"_Level", 0);
		switch(id)
		{
			case 1:
				return new int[]{a_level, _pve_count, ConfigValue.AttainmentIn_PvE[Math.min(a_level, ConfigValue.AttainmentIn_PvE.length-1)]};
			case 2:
				return new int[]{a_level, _pvp_count, ConfigValue.AttainmentIn_PvP[Math.min(a_level, ConfigValue.AttainmentIn_PvP.length-1)]};
			case 3:
				return new int[]{a_level, _pk_count, ConfigValue.AttainmentIn_Pk[Math.min(a_level, ConfigValue.AttainmentIn_Pk.length-1)]};
			case 4:
				return new int[]{a_level, _time_tick/60, ConfigValue.AttainmentIn_Time[Math.min(a_level, ConfigValue.AttainmentIn_Time.length-1)]};
			case 5:
				return new int[]{a_level, _rb_kill_count, ConfigValue.AttainmentIn_RbKill[Math.min(a_level, ConfigValue.AttainmentIn_RbKill.length-1)]};
			case 6:
				return new int[]{a_level, _reflection_count, ConfigValue.AttainmentIn_Instance[Math.min(a_level, ConfigValue.AttainmentIn_Instance.length-1)]};
			case 7:
				return new int[]{a_level, _enchant_weapon, ConfigValue.AttainmentIn_EnchantWeapon[Math.min(a_level, ConfigValue.AttainmentIn_EnchantWeapon.length-1)]};
			case 8:
				return new int[]{a_level, _enchant_armor, ConfigValue.AttainmentIn_EnchantArmor[Math.min(a_level, ConfigValue.AttainmentIn_EnchantArmor.length-1)]};
			case 9:
				return new int[]{a_level, _quest_complet, ConfigValue.AttainmentIn_Quest[Math.min(a_level, ConfigValue.AttainmentIn_Quest.length-1)]};
			case 10:
				return new int[]{a_level, _craft_count, ConfigValue.AttainmentIn_Craft[Math.min(a_level, ConfigValue.AttainmentIn_Craft.length-1)]};
		}
		return _arr_int;
	}
}