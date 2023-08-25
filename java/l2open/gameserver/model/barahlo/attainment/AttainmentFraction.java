package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;

/**
+1). Уверенность через край – 50 раз получить успешное усиление (Заточка)
+2). Грустная душа – 50 раз неудачно усилить ((Заточка)25 фейлов)
+3). С рвением к знаниям – пройти квест для вступления во фракцию.
+4). В трех шагах от всезнания – получить 8 ранг фракции.
+5). Мастер по всем вопросам – получить 10 ранг фракции.
+6). Признанный мудрец – получить 12 ранг фракции.
+7). Показное богатство – сделать пожертвование в размере 100 донок.
+8). Важный гость – сделать пожертвование в размере 1000 донок.
+9). Глава карателей – убить 50 пк.
+10). Палач – убить 50 людей в ПК.
+11). С огоньком в крови – убить любого эпик босса.(Названия: Валакас, Антарас, Ант квин, белеф, фрея, закен, фринтеза, баюм)
+12). Влюбленная душа – сыграть свадьбу 
**/
// ??? _owner.sendPacket(new ExShowScreenMessage("Получено достижение: \"Киллер\" ур.1!", 5000, ScreenMessageAlign.TOP_CENTER, true));
// EnableAttainment=true
// AttainmentType = 512
// AttainmentAnimation = 6791,1,2000
public class AttainmentFraction extends Attainment
{
	public AttainmentFraction(L2Player owner)
	{
		super(owner);
	}

	// 1). Уверенность через край – 100 раз получить успешное усиление (Заточка)
	public void enchant_sucess(int level, int safe_level, int ench_scrol, boolean is_weapon)
	{
		if(safe_level >= level || ench_scrol != 6577)
			return;
		int count = _owner.getVarInt("AttainmentEnchSucess", 0)+1;
		_owner.setVar("AttainmentEnchSucess", String.valueOf(count));
		if(count >= 100 && !_owner.getVarB("Attainment1"))
		{
			_owner.setVar("Attainment1", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment1_reward);
		}
	}

	// 2). Грустная душа – 100 раз неудачно усилить ((Заточка)25 фейлов)
	public void enchant_fail(int level, int safe_level, int ench_scrol)
	{
		if(ench_scrol == 6578)
		{
			int count = _owner.getVarInt("AttainmentEnchFail", 0)+1;
			_owner.setVar("AttainmentEnchFail", String.valueOf(count));
			if(count >= 100 && !_owner.getVarB("Attainment2"))
			{
				_owner.setVar("Attainment2", String.valueOf(true));
				setRewardMsg(ConfigValue.Attainment2_reward);
			}
		}
	}

	// 3). С рвением к знаниям – пройти квест для вступления во фракцию.
	public void confirm_quest()
	{
		if(!_owner.getVarB("Attainment3"))
		{
			_owner.setVar("Attainment3", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment3_reward);
		}
	}

	// 4). В трех шагах от всезнания – получить 8 ранг фракции.
	// 5). Мастер по всем вопросам – получить 10 ранг фракции.
	// 6). Признанный мудрец – получить 12 ранг фракции.
	public void rang_up(int rang_level)
	{
		if(rang_level >= 8 && !_owner.getVarB("Attainment4"))
		{
			_owner.setVar("Attainment4", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment4_reward);
		}
		if(rang_level >= 10 && !_owner.getVarB("Attainment5"))
		{
			_owner.setVar("Attainment5", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment5_reward);
		}
		if(rang_level >= 12 && !_owner.getVarB("Attainment6"))
		{
			_owner.setVar("Attainment6", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment6_reward);
		}
	}

	// 7). Показное богатство – сделать пожертвование в размере 100 донок.
	// 8). Важный гость – сделать пожертвование в размере 1000 донок.
	public void incDonatte(int item_id, long item_count)
	{
		if(item_id == 4037 && item_count >= 100 && !_owner.getVarB("Attainment7"))
		{
			_owner.setVar("Attainment7", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment7_reward);
		}
		if(item_id == 4037 && item_count >= 1000 && !_owner.getVarB("Attainment8"))
		{
			_owner.setVar("Attainment8", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment8_reward);
		}
	}

	// +9). Глава карателей – убить 50 пк.
	public void incPvp(final L2Player died)
	{
		if(died.getHWIDs().equals(_owner.getHWIDs()) || ConfigValue.AttainmentKillProtect && died.no_kill_time() || died.getKarma() <= 0)
			return;
		_pk_kill_count++;
		_owner.setVar("AttainmentPkKill", String.valueOf(_pk_kill_count));
		if(_pk_kill_count >= 50 && !_owner.getVarB("Attainment9"))
		{
			_owner.setVar("Attainment9", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment9_reward);
		}
	}

	// +10). Палач – убить 50 людей в ПК.
	public void incPk(final L2Player died)
	{
		if(died.getHWIDs().equals(_owner.getHWIDs()) || ConfigValue.AttainmentKillProtect && died.no_kill_time())
			return;
		_pk_count++;
		_owner.setVar("AttainmentPk", String.valueOf(_pk_count));
		if(_pk_count >= 50 && !_owner.getVarB("Attainment10"))
		{
			_owner.setVar("Attainment10", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment10_reward);
		}
	}

	public void setKillZaken()
	{
		if(!_owner.getVarB("Attainment11"))
		{
			_owner.setVar("Attainment11", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment11_reward);
		}
	}

	public void setKillFrinteza()
	{
		if(!_owner.getVarB("Attainment11"))
		{
			_owner.setVar("Attainment11", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment11_reward);
		}
	}

	// Валакас(29028), Антарас(29068), Ант квин(29001), белеф(29118), фрея(29180 : 29179)
	// 11). С огоньком в крови – убить любого эпик босса.(Названия: Валакас, Антарас, Ант квин, белеф, фрея, закен, фринтеза, баюм)
	public void setKillRaid(L2Character raid)
	{
		if((raid.getNpcId() == 29028 || raid.getNpcId() == 29068 || raid.getNpcId() == 29001 || raid.getNpcId() == 29118 || raid.getNpcId() == 29180 || raid.getNpcId() == 29179) && !_owner.getVarB("Attainment11"))
		{
			_owner.setVar("Attainment11", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment11_reward);
		}
	}

	public int getAttainmentState(int id)
	{
		return 0;
	}

	// 12). Влюбленная душа – сыграть свадьбу 
	public void createCouple(L2Player player1, L2Player player2)
	{
		if(!_owner.getVarB("Attainment12"))
		{
			_owner.setVar("Attainment12", String.valueOf(true));
			setRewardMsg(ConfigValue.Attainment12_reward);
		}
	}
}