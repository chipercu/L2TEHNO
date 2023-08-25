package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;

// EnableAttainment=true
// AttainmentType=256
public class AttainmentVidak extends Attainment
{
	public AttainmentVidak(L2Player owner)
	{
		super(owner);
	}

	public void incPvp(final L2Player died)
	{
		if(/*died.getHWIDs().equals(_owner.getHWIDs()) || */ConfigValue.AttainmentKillProtect && died.no_kill_time())
			return;
		_pvp_count++;
		_owner.setVar("AttainmentPvP", String.valueOf(_pvp_count));
		if(_pvp_count >= ConfigValue.Attainment1_count && !_owner.getVarB("Attainment1"))
		{
			_owner.setVar("Attainment1", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment1_reward);
			setSkill();
		}
		if(_pvp_count >= ConfigValue.Attainment2_count && !_owner.getVarB("Attainment2"))
		{
			_owner.setVar("Attainment2", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment2_reward);
			setSkill();
		}
	}

	public void incPk(final L2Player died)
	{
		if(died.getHWIDs().equals(_owner.getHWIDs()) || ConfigValue.AttainmentKillProtect && died.no_kill_time())
			return;
		_pk_count++;
		_owner.setVar("AttainmentPk", String.valueOf(_pk_count));
		if(_pk_count >= ConfigValue.Attainment3_count && !_owner.getVarB("Attainment3"))
		{
			_owner.setVar("Attainment3", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment3_reward);
			setSkill();
		}
		if(_pk_count >= ConfigValue.Attainment4_count && !_owner.getVarB("Attainment4"))
		{
			_owner.setVar("Attainment4", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment4_reward);
			setSkill();
		}
	}

	public void setNoble()
	{
		if(_owner.isNoble() && !_owner.getVarB("Attainment5"))
		{
			_owner.setVar("Attainment5", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment5_reward);
			setSkill();
		}
	}

	public void kill_char(L2Character target)
	{
		if(target.isPlayer() && (target.getPlayer().getActiveClassId() == 90 || target.getPlayer().getActiveClassId() == 91 || target.getPlayer().getActiveClassId() == 99 || target.getPlayer().getActiveClassId() == 106) && !_owner.getVarB("Attainment6") && !target.getPlayer().getHWIDs().equals(_owner.getHWIDs()) && (!ConfigValue.AttainmentKillProtect || !target.getPlayer().no_kill_time()))
		{
			if(_owner.getVarInt("Attainment6_kill", 0) >= ConfigValue.Attainment6_count)
			{
				_owner.setVar("Attainment6", String.valueOf(true));
				setRewardMsg(ConfigValue.Attainment6_reward);
				setSkill();
			}
			else
				_owner.setVar("Attainment6_kill", String.valueOf(_owner.getVarInt("Attainment6_kill", 0)+1));
		}
	}

	public void oly_battle_end(boolean isWin)
	{
		if(isWin && !_owner.getVarB("Attainment7"))
		{
			if(_owner.getVarInt("Attainment7_win", 0) >= ConfigValue.Attainment7_count)
			{
				_owner.setVar("Attainment7", String.valueOf(true));
				setRewardMsg(ConfigValue.Attainment7_reward);
			}
			else
				_owner.setVar("Attainment7_win", String.valueOf(_owner.getVarInt("Attainment7_win", 0)+1));
		}
	}

	public void setClan()
	{
		if(_owner.getLevel() >= ConfigValue.Attainment8_level && _owner.getPledgeType() != L2Clan.SUBUNIT_ACADEMY && _owner.getClan() != null && _owner.getClan().getLevel() >= ConfigValue.Attainment8_count && !_owner.getVarB("Attainment8") && _owner.getVarLong("join_clan", Long.MAX_VALUE) <= (System.currentTimeMillis()-ConfigValue.Attainment8_Time*60*1000L) && PlayerData.getInstance().countCharForClan(_owner) <= 1)
		{
			// SELECT COUNT(*) FROM characters WHERE last_hwid='' AND clanid='';
			_owner.setVar("Attainment8", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment8_reward);
		}
	}

	public void incDonatte(int item_id, long item_count)
	{
		if(item_id == ConfigValue.Attainment9_DonateItem && item_count >= 5 && !_owner.getVarB("Attainment9"))
		{
			_owner.setVar("Attainment9", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment9_reward);
		}
	}

	public void char_resurection(L2Character target)
	{
		if(target.isPlayer() && !_owner.getVarB("Attainment11") && !target.getPlayer().getHWIDs().equals(_owner.getHWIDs()))
		{
			if(_owner.getVarInt("Attainment11_res", 0) >= ConfigValue.Attainment11_count)
			{
				_owner.setVar("Attainment11", String.valueOf(true));
				setRewardMsg(ConfigValue.Attainment11_reward);
				setSkill();
			}
			else
				_owner.setVar("Attainment11_res", String.valueOf(_owner.getVarInt("Attainment11_res", 0)+1));
		}
	}

	public void event_battle_end(int type, boolean isWin) // 0 - CTF, 1 - LastHero, 2 - TvT
	{
		if(isWin)
		{
			if(_owner.getVarInt("Attainment12_win", 0) >= ConfigValue.Attainment12_count && !_owner.getVarB("Attainment12"))
			{
				_owner.setVar("Attainment12", String.valueOf(true));
				setRewardMsg(ConfigValue.Attainment12_reward);
			}
			else
				_owner.setVar("Attainment12_win", String.valueOf(_owner.getVarInt("Attainment12_win", 0)+1));
		}
	}

	public void incTime()
	{
		int time = _owner.getVarInt("Attainment13_time", 0);
		if(time >= ConfigValue.Attainment13_count-1 && !_owner.getVarB("Attainment13"))
		{
			_owner.setVar("Attainment13", String.valueOf(true));
			_owner.stopAttainmentTask();
			setRewardMsg(ConfigValue.Attainment13_reward);
			setSkill();
		}
		else if(!_owner.getVar("Attainment13_loc", "").equals(String.valueOf(_owner.getLoc())))
		{
			_owner.setVar("Attainment13_loc", String.valueOf(_owner.getLoc()));
			_owner.setVar("Attainment13_time", String.valueOf(time+1));
		}
	}

	/**
		12. Достижение
		http://joxi.ru/LYDWU4wyTJC8LrQEGfw
		Умереть 10 раз в pvp при этом получить 3000 урона. При этом меня должны дубасить 3 чела.
		Время обнуления урона 4 минуты. Если не убили то начинают дубасить заново.
		Если нанесли меньше 3000 урона то не делать зачет.
		Если били 1 или 2 перса то не делать зачет.

		Пример: залетаю фуловый в pvp зону меня начинают дубасить 3е. Наносят минимум 3000 урона по мне я дохну и сразу в зачет очко.
		Сделать проверку по hwid.

		# Смерть
		Nagrada= 4356
		Skolko=120
		yron=3000
	**/
	public float my_damage=0;
	public void reduceCurrentHp(double damage, L2Player attacker)
	{
		if(!attacker.getHWIDs().equals(_owner.getHWIDs()))
		{
			my_damage+=damage;
		}
	}

	public boolean valid_die(L2Player attacker)
	{
		if(my_damage >= ConfigValue.Attainment14_damage)
			return true;
		return false;
	}

	public void doDie(L2Player attacker)
	{
		if(getAttainmentState(0) == 1 && !_owner.getVarB("Attainment14") && valid_die(attacker))
		{
			my_damage=0;
			int count = _owner.getVarInt("Attainment14_die", 0);
			_owner.setVar("Attainment14_die", String.valueOf(count+1));
			if(count >= ConfigValue.Attainment14_count)
				Attainment14();
		}
	}

	public void Attainment14()
	{
		if(!_owner.getVarB("Attainment14"))
		{
			_owner.setVar("Attainment14", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment14_reward);
			setSkill();
		}
	}

	public void Attainment10()
	{
		if(!_owner.getVarB("Attainment10"))
		{
			_owner.setVar("Attainment10", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment10_reward);
			setSkill();
		}
	}

	public boolean checkAttainment10()
	{
		if(_owner.isMaried() && _owner.getParty() != null)
		{
			L2Player partner = L2ObjectsStorage.getPlayer(_owner.getPartnerId());
			if(partner != null && partner.getParty() != null && _owner.getParty() == partner.getParty() && _owner.getDistance(partner) <= ConfigValue.Attainment10_radius)
				return true;
		}
		return false;
	}

	public void setSkill()
	{
		if(_owner.getVarB("Attainment1") && _owner.getVarB("Attainment2") && _owner.getVarB("Attainment3") && _owner.getVarB("Attainment4") && _owner.getVarB("Attainment5") && _owner.getVarB("Attainment6") && _owner.getVarB("Attainment11") && _owner.getVarB("Attainment13") && _owner.getVarB("Attainment14"))
			_owner.addSkill(SkillTable.getInstance().getInfo(ConfigValue.AttainmentSkillReward[0], ConfigValue.AttainmentSkillReward[1]), true);
	}

	public void setKillZaken()
	{
		if(_owner.isNoble() && !_owner.getVarB("Attainment15"))
		{
			_owner.setVar("Attainment15", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment15_reward);
			setSkill();
		}
	}

	public void setKillFrinteza()
	{
		if(_owner.isNoble() && !_owner.getVarB("Attainment16"))
		{
			_owner.setVar("Attainment16", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment16_reward);
			setSkill();
		}
	}

	public void setKillRaid(L2Character raid)
	{
		if(raid.getNpcId() == 29020)
			setKillBaium();
		else if(_owner.isNoble() && !_owner.getVarB("Attainment17"))
		{
			_owner.setVar("Attainment17", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment17_reward);
			setSkill();
		}
	}

	public void setKillBaium()
	{
		if(_owner.isNoble() && !_owner.getVarB("Attainment18"))
		{
			_owner.setVar("Attainment18", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment18_reward);
			setSkill();
		}
	}

	// 1 2 3 4 9 11 12
	// Для отображения иконок в комунке.
	public int getAttainmentState(int id)
	{
		switch(id)
		{
			case 1:
				if(_pvp_count >= ConfigValue.Attainment1_count)
					return 1;
				break;
			case 2:
				if(_pvp_count >= ConfigValue.Attainment2_count)
					return 1;
				break;
			case 3:
				if(_pk_count >= ConfigValue.Attainment3_count)
					return 1;
				break;
			case 4:
				if(_pk_count >= ConfigValue.Attainment4_count)
					return 1;
				break;
			case 5:
				if(_owner.isNoble())
					return 1;
				break;
			case 6:
				if(_owner.getVarB("Attainment6")) // +
					return 1;
				break;
			case 7:
				if(_owner.getVarB("Attainment7")) // +
					return 1;
				break;
			case 8:
				if(_owner.getVarB("Attainment8")) // +
					return 1;
				break;
			case 9:
				if(_owner.getVarB("Attainment9")) // +
					return 1;
				break;
			case 10:
				if(_owner.getVarB("Attainment10")) // +
					return 1;
				break;
			case 11:
				if(_owner.getVarB("Attainment11")) // +
					return 1;
				break;
			case 12:
				if(_owner.getVarB("Attainment12")) // +
					return 1;
				break;
			case 13:
				if(_owner.getVarB("Attainment13")) // +
					return 1;
				break;
			case 14:
				if(_owner.getVarB("Attainment14")) // -
					return 1;
				break;
			//--------------------------------------------
			case 15:
				if(_owner.getVarB("Attainment15")) // -
					return 1;
				break;
			case 16:
				if(_owner.getVarB("Attainment16")) // -
					return 1;
				break;
			case 17:
				if(_owner.getVarB("Attainment17")) // -
					return 1;
				break;
			case 18:
				if(_owner.getVarB("Attainment18")) // -
					return 1;
				break;
			default:
				return 0;
		}
		return 0;
	}
}
/**
Фича достижения.

Механика иконок:
http://joxi.ru/zWfWU4wyTJDCLhpauKU
Все иконки имеют 2 статуса. Если достижение изучено то иконки меняются.
Каждая иконка является своеобразной кнопкой. Нажал появилось описание.
1 и 2 иконки имеют фишку меняться если счетчик стал больше. типа уровень 1 и уровень 2.



1. Достижение.
http://cdn.joxi.ru/uploads/prod/2014/07/28/5a2/d3e/fac70d91bb72e02f491900ae54e5bcd94ee516c0.jpg
Набить 50 PvP получить награду.
Конфиг:
# Задание v1 на 50 pvp
Zadacha= 50
Nagrada= 4356
Skolko=20

# Задание v2 на 150 pvp
Zadacha= 150
Nagrada= 4356
Skolko=40

Данное достижение делится на 2 уровня.
т.е первый уровень надо набить 50 pvp и получить награду. 2 уровень набить 150 pvp. В итоге надо набить 200 pvp
Сделать вывод счетчика набитых PvP он независим от игрового.
http://joxi.ru/MnjWU4wyTJBROA7_6bg
т.е в первом случаи мы набиваем 50 pvp счетчик останавливается на 50.
а уже 51 pvp идет во 2 счетчик под цифрой 1 и так набиваем 150 pvp и счетчик останавливается.



Защита от накрутки.

Защита от накрутки по Hwid.
Если в течении 30 минут одного и того же персонажа убивают N раз в подряд то сразу давать блок по учету PvP на N времени ( конфиг) и списать со счетчика  все PvP которые он набил за это время.

Проверка PvP на время. Если персонаж набил больше 8 PvP(конфиг) за N время( конфиг) То давать блок на (конфиг) N время.

п.с Пример: обычно сразу ресают и начинают бить. Поэтому набив больше 8 PvP за 2 минуты получают блок


Если за 7 часов набито 100 PvP чистить счетчик полностью под 0. (чистить счетчик для достижения)

Если персонаж выполнил достижение то правила по защите от накрутке pvp все снимаются. Кроме hwid.

Блок на PvP
Если персонаж попал в блок то в течении определенного времени его счетчик на достижение не пополняется.
т.е останавливается. У нас в данных персонажа находится 45 PvP, а на счетчике достижения будет только 28 PvP.

2.Достижение PK
Набить 10 PK получить награду.

Конфиг:
# Задание v1 на 50 pk
ZadachaPK= 10
Nagrada= 4356
Skolko=20

# Задание v2 на 50 PK
ZadachaPK = 50
Nagrada= 4356
Skolko=40

В итоге надо набить 60 pk
Сделать проверку по hwid
Аналогичная защита от накрутки.
И такая же система счетчиков как в pvp.

3. Достижение
Получить статус дворянина. Стали дворянином сразу награду выдают.

# Дворянство
Nagrada= 4356
Skolko=120

4. Достижение
# Убийство
Nagrada= 4356
Skolko=120

Устройте охоту на Phoenix Knight, Hell Knight, Eva's Templar, Shillien Templar.
http://joxi.ru/4XrWU4wyTJC7LpInJR4
Нужно свой счетчик пополнить 10 убийствами т.е можно убить 1 раз Phoenix Knight 6 раз Hell Knight и 3 Shillien Templar.
Не имеет значение кого ты убиваешь главное, что бы он соответствовал по профе. В зачет не идут убийство в PK данных проф.

Сделать проверку по hwid
Аналогичная защита от накрутки.
И такая же система счетчиков как в pvp.

5. Достижение
Провести 10 победных боев на олимпе. Очередность побед не имеет значение. Т.е  1 бой у нас победа потом у нас 4 слива потом 3 победы. Итого у нас 4 победы осталось 7.
# Олимп
Nagrada= 4356
Skolko=120


6. Достижение
Вступить в клан, если клан 5 лвл то выдавать награду сразу. Если клан допустим 3 лвл то награду не выдавать ждать пока не станет 5 лвлом. Если клан прокачали до 5 лвл награду выдавать.
# Клан
Nagrada= 4356
Skolko=120

7. Достижение
Сделать свае первое пожертвование на сервер. Сумма не имеет значение. т.е тупо должны быть зачислены донки на аккаунт.
# Награда за донат
Nagrada= 4356
Skolko=120

8.Достижение
http://joxi.ru/2nzWU4wyTJAMNec6iC4
Достижение связано со свадьбой.
Сделать проверку по hwid.

Давать 20% к рейтам если 2 персонажа(молодожены) в пати и в радиусе не более 1000 друг от друга.

# Свадебка
Pribavlat_k_reitam= 20%
Radius=1000

9.Достижение
http://joxi.ru/r33WU4wyTJB6Y13mxfs
Произвести 10 воскрешений с помощью свитка или скиллом. В зачет не идут питомцы.
Сделать проверку по hwid.

# ресалка
Nagrada= 4356
Skolko=120


10.Достижение
Достижение связано с ивентами. 
Одержи 3 победы в ивентах CtF TvT LH и получи награду.
Просто тупо сделать учет побед. Как мы получаем 3 победы сразу выдавать награду.

# ивенты
Nagrada= 4356
Skolko=120

11. Достижение
Ивент связан со временем проведенным в игре
http://joxi.ru/oH7WU_3JTJBsDhuu7Eg

Нужно провести в игре 24 часа при этом быть активным. В учет не идет оффлайн трейд.
Сделать проверку по координатам на которых расположен персонаж.  Если он тупо стоит в течении часа на 1 точке то списывать этот час с учтенного времени для бонуса.

# Время
Nagrada= 4356
Skolko=120
Vremi=24

12. Достижение
http://joxi.ru/LYDWU4wyTJC8LrQEGfw
Умереть 10 раз в pvp при этом получить 3000 урона. При этом меня должны дубасить 3 чела.
Время обнуления урона 4 минуты. Если не убили то начинают дубасить заново.
Если нанесли меньше 3000 урона то не делать зачет.
Если били 1 или 2 перса то не делать зачет.

Пример: залетаю фуловый в pvp зону меня начинают дубасить 3е. Наносят минимум 3000 урона по мне я дохну и сразу в зачет очко.
Сделать проверку по hwid.

# Смерть
Nagrada= 4356
Skolko=120
yron=3000




Когда мы выполнили 1 2 3 4 9 11 12(все должны быть по максимуму)  достижения у нас появляется пасивный скилл.
пасивку я придумаю сам. Сделать просто конфиг на id скилла который выдавать.
После выполнения этих достижений выводить диалоговое окно в котором будет сообщатся о добавлении пасивки.

Пасивка не работает на олимпе

#Скилл который давать после выполнение достижений 1 2 3 4 9 11 12
Skill_za_dostigenia=40500






Фича с учетом смертей PvP и PK
Если нас убили делать учет. Не делать учет если мы под пером и прочей шляпой которая нас восстанавливает.
http://joxi.ru/zI7WU_3JTJAxGjAIYW4

Фича с выводом данных по олимпу.
http://joxi.ru/Eo_WU4wyTJC4Lnb5Rrs
По картинке думаю ясно, что сделать.

Фича со сменой картинки под ту или иную профу тот или иной пол.
http://joxi.ru/ZI_WU4wyTJAbY7SYWjc

1. Нужно сделать,  что бы в алт+б когда заходит игрок под его перса выводилась картинка.
т.е Орк М Воин выводилась картинка орка мужика воина. Вид картинки img_1_1_1
2. Так же сделать, что бы в html через диалоги выводилось.

Конфиг откуда брать киртинки (название файла)

Krtinki=icons


Фича
Сделать конфиг на вывод анонса о бане, только для админа и того кого забанили
остальные его не видят.
Т.е я забанил жулика и у него сразу появляется анонс. Но его видит только он и админ.
Как обычная система вывода только ограничить ее для 2х человек.
Анонсы видят все админы.




Фича.
Есть такой конфи,г как отключение олимпиады и тогда нобл не работает, надо что бы нобл работал но олимп был отключен.


Фича 
Вывод  .repair что бы работало через кнопку если нажать в алт+б

Фича
  Запретить антиботу проверку на бота в городах, эпиках, инстантах.
  Вывести конфиг на время проверки на бота.
  Вывести конфиг для игроков которые купили премиум, проверять ли их, если проверять то через какой промежуток времени.




Фича ивент(пока думаю)
Штурм и защита. Команды делятся автоматически на 60 и 40% атакующая сторона 60%. Определяется защищающая и атакующая сторона.
Замок для проведения Дион.

Атакующая сторона.
Если в штурме участвует свыше 90 человек то делить команды по спауну.

Место для спауна 1/3 команды (30 человек и менее)
http://joxi.ru/V5zWU_3JTJBUNkapskw

Место для спауна 1/3 команды (30 человек и менее)
http://joxi.ru/0ZzWU_3JTJDzRyBcQfA

Остатки команды спаунятся по центру (30 человек) но не менее 30.
http://joxi.ru/DJ3WU_3JTJBYNj2UvF4

У атакующей стороны есть скилл для каста захвата
Задача закостовать http://joxi.ru/s53WU4wyTJA7NdRpLEQ может любой участник атаки.



Защищающая сторона:
50% сил и более располагается в замке
http://joxi.ru/FJ7WU4wyTJAcNUlidLY

15%
http://joxi.ru/Zp7WU_3JTJBmNE3IVj4

10%
http://joxi.ru/r57WU4wyTJB4Y_EVTjE

10%
http://joxi.ru/9Z7WU4wyTJBSOAEvpAw

http://joxi.ru/OqDWU4wyTJDnNJKLWTE
место спауна 2% и 2%

процентная система работает если защитников более либо равно 90 человек

если их менее то спаунить всех внутри замка.

двери могут открывать все участники кроме центральных ворот.
Алт+б баф разрешен.

разрешены пати.
Запрещены ресы.

Конфиг на награду победившей стороне.



Фича озвучки.
Озвучка слышна у всех. В месте с озвучкой выводится сообщение на экран кто кого убил.
Первое убийство звук First Blood. 


Дабл килл если кто то убил двоих в подряд. 
В месте с озвучкой выводится сообщение на экран кто кого убил.

Трипл килл если убил 3х в подряд.
Мега килл если убил 4 врага и сам не здох не разу.

Монстер килл если убил 5рых

Ультра килл если убил 6рых

(Для твт) Когда команда лидирует по очкам на 80%  Слышна озвучка Овнейдж и пишется сообщение что команда допустим синих лидирует на 80%

Если убил 8рых Анстоппабл



Фича c олимпом(думаю)

Ходя на олимп и побеждая, с вероятностью 10% выскакивает диалог в котором персонаж может выбрать 1 их предметов который он хочет получить. Кароч надо сделать тупо кнопки. Я сам сделаю html и описание предметов. Нажав на кнопку выбранного предмета страница тупо закрывается. Предмет падает нам в инвентарь.
Всего 15 страниц.
Если мы попали в этот шанс то выскакивает сраница номер 1.
Опять бои, опять победы и шанс выдает нам страницу 2 и так по возрастанию.
Чем больше боев отходил тем больше вероятность, что страничка появится.

После 10 боев Есть шанс выпасть странице 1
После 20 боев страницы 2
После 30 боев страницы 3
После 40 боев страницы 5
После 50 боев страницы 6


**/