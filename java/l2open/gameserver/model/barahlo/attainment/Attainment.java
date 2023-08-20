package l2open.gameserver.model.barahlo.attainment;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.MagicSkillLaunched;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.Rnd;

import java.util.logging.Logger;

public class Attainment
{
	protected static final Logger _log = Logger.getLogger(Attainment.class.getName());

	public final static int[] _arr_int = new int[0];
	protected L2Player _owner;
	public int _pve_count;
	public int _rb_kill_count;
	public int _time_tick;
	public int _pvp_count;
	public int _pk_count;
	public int _pk_kill_count;
	public int _enchant_weapon;
	public int _enchant_armor;
	public int _quest_complet;
	public int _craft_count;
	public int _reflection_count;
	public int _oly_win;
	public int _oly_loos;
	public float my_damage=0;

	public Attainment(L2Player owner)
	{
		_owner = owner;
		_pve_count = _owner.getVarInt("AttainmentPvE", 0);
		_rb_kill_count = _owner.getVarInt("AttainmentRbKill", 0);
		_pvp_count = _owner.getVarInt("AttainmentPvP", 0);
		_pk_count = _owner.getVarInt("AttainmentPk", 0);
		_pk_kill_count = _owner.getVarInt("AttainmentPkKill", 0);
		_enchant_weapon = _owner.getVarInt("AttainmentEnchSucessWeapon", 0);
		_enchant_armor = _owner.getVarInt("AttainmentEnchSucessArmor", 0);
		_quest_complet = _owner.getVarInt("AttainmentQuestComplet", 0);
		_craft_count = _owner.getVarInt("AttainmentCraftCount", 0);
		_reflection_count = _owner.getVarInt("AttainmentReflectionCount", 0);
		_time_tick = _owner.getVarInt("Attainment4_time", 0);
		_oly_win = _owner.getVarInt("AttainmentOlympiadWin", 0);
		_oly_loos = _owner.getVarInt("AttainmentOlympiadLoos", 0);
	}

	public void incPve(final L2Character died, boolean last_hit)
	{}

	public void incLevel()
	{}

	public void incPvp(final L2Player died)
	{}

	public void incPk(final L2Player died)
	{}

	public void createCouple(L2Player player1, L2Player player2)
	{}

	public void incCraft(boolean mw)
	{}

	public void incReflection(int id)
	{}

	public void setNoble()
	{}

	public void kill_char(L2Character target)
	{}

	public void oly_battle_end(boolean isWin)
	{}

	public void setClan()
	{}

	public void incDonatte(int item_id, long item_count)
	{}

	public void enchant_fail(int level, int safe_level, int ench_scrol)
	{}

	public void enchant_sucess(int level, int safe_level, int ench_scrol, boolean is_weapon)
	{}

	public void char_resurection(L2Character target)
	{}

	public void event_battle_end(int type, boolean isWin)
	{}

	public void reduceCurrentHp(double damage, L2Player attacker)
	{}

	public void doDie(L2Player attacker)
	{}

	public void setKillZaken()
	{}

	public void setKillFrinteza()
	{}

	public void setKillRaid(L2Character raid)
	{}

	public void confirm_quest()
	{}

	public void rang_up(int rang_level)
	{}

	public int getAttainmentState(int id)
	{
		return 0;
	}

	public int[] getAttainmentStats(int id)
	{
		return _arr_int;
	}

	public String getIcon(int id)
	{
		return "";
	}

	public void Attainment10()
	{}

	public boolean checkAttainment10()
	{
		return false;
	}

	public void incTime()
	{}

	public void enter_world(boolean first)
	{}

	public void questComplet(QuestState qs)
	{}

	public void setSkill(int[] value, String name, int level)
	{
		_owner.addSkill(SkillTable.getInstance().getInfo(value[0], value[1]), true);
		_owner.sendMessage("Вы получили *"+name+"* - ур "+level+"!");
	}

	public void setRewardMsg(long[] item)
	{
		for(int i=0;i<item.length;i+=2)
		{
			if(item[i] == -100)
			{
				_owner.addFractionPoint((int)item[i+1]);
				_owner.sendMessage("Вы получили "+item[i+1]+" очков фракции.");
			}
			else
			{
				_owner.getInventory().addItem((int)item[i], item[i+1]);
				_owner.sendPacket(SystemMessage.obtainItems((int)item[i], item[i+1], 0));
			}
		}
		_owner.broadcastSkill(new MagicSkillUse(_owner, _owner, ConfigValue.AttainmentAnimation[0], ConfigValue.AttainmentAnimation[1], ConfigValue.AttainmentAnimation[2], 0), true);
		_owner.broadcastSkill(new MagicSkillLaunched(_owner.getObjectId(), ConfigValue.AttainmentAnimation[0], ConfigValue.AttainmentAnimation[1], _owner, false), true);
		_owner.sendMessage("Вы получили награду за достижение.");
	}

	public void setReward(long[] item)
	{
		for(int i=0;i<item.length;i+=2)
		{
			if(item[i] == -100)
			{
				_owner.addFractionPoint((int)item[i+1]);
				_owner.sendMessage("Вы получили "+item[i+1]+" очков фракции.");
			}
			else
			{
				_owner.getInventory().addItem((int)item[i], item[i+1]);
				_owner.sendPacket(SystemMessage.obtainItems((int)item[i], item[i+1], 0));
			}
		}
	}

	public void setRndReward(long[] item)
	{
		int index = Rnd.get(item.length/2)*2;

		if(item[index] == -100)
		{
			_owner.addFractionPoint((int)item[index+1]);
			_owner.sendMessage("Вы получили "+item[index+1]+" очков фракции.");
		}
		else
		{
			_owner.getInventory().addItem((int)item[index], item[index+1]);
			_owner.sendPacket(SystemMessage.obtainItems((int)item[index], item[index+1], 0));
		}
		//_owner.sendMessage("Благодарим Вас за ваше игровое время. Вы получили подарок.");
		//_owner.broadcastSkill(new MagicSkillUse(_owner, _owner, ConfigValue.AttainmentAnimation[0], ConfigValue.AttainmentAnimation[1], ConfigValue.AttainmentAnimation[2], 0), true);
		//_owner.broadcastSkill(new MagicSkillLaunched(_owner.getObjectId(), ConfigValue.AttainmentAnimation[0], ConfigValue.AttainmentAnimation[1], _owner, false), true);
		//_owner.sendMessage("Вы получили награду за достижение.");
	}
}