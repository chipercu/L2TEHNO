package l2open.pts.templ;

import l2open.gameserver.model.L2Skill.Element;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.ArrayUtils;

import java.util.*;

/**
 * Запиздовал Diagod...
 * open-team.ru
 **/
public class PlayerTempl
{
	public int _classId;
	public byte _raceId;
	public byte _sex;
	public byte _isMage;

	public byte min_int;
	public byte min_str;
	public byte min_con;
	public byte min_men;
	public byte min_dex;
	public byte min_wit;

	public byte base_int;
	public byte base_str;
	public byte base_con;
	public byte base_men;
	public byte base_dex;
	public byte base_wit;

	public byte max_int;
	public byte max_str;
	public byte max_con;
	public byte max_men;
	public byte max_dex;
	public byte max_wit;

	public float[] base_cp = new float[85];
	public float[] base_hp = new float[85];
	public float[] base_mp = new float[85];

	public float[] org_hp_regen = new float[99];
	public float[] org_mp_regen = new float[99];
	public float[] org_cp_regen = new float[99];

	public float[] org_hp_regen_weight = new float[6];
	public float[] org_mp_regen_weight = new float[6];
	public float[] org_cp_regen_weight = new float[6];

	public int base_physical_attack;
	public int base_critical;
	public String base_attack_type; // Нужно ли оно нам? :)
	public int base_attack_speed;

	public int[] base_defend = new int[7]; // Верхняя часть тела, нижняя часть тела, шлем, сапоги, перчатках, белье, мантии...
	public int base_defend_sum = 0; // Что бы лишний раз не считать...

	public int[] base_magic_defend = new int[5]; // Ожерелье, Правая Серьга, Левая Серьга, Правое Кольцо, Левое Кольцо.
	public int base_magic_defend_sum = 0;

	public int base_magic_attack;
	public int base_can_penetrate;
	public int base_attack_range;
	public int[] base_damage_range = new int[4]; // хз че за параметры у него...
	public int base_rand_dam;
	public int[] moving_speed = new int[8]; // Ходьба, Бег, медлено под водой, быстро под водой, медленый полет, быстрый полет, медленно верхом, быстро верхом
	public int org_jump;
	public int pc_breath_bonus_table;
	public int pc_safe_fall_height_table;
	public float collision_radius;
	public float collision_height;

	public int regen_move_mode_bonus; // Не используется :)
	public float pc_karma_increase_table; // Не используется :)
	public float pc_karma_increase_constant; // Не используется :)

	public PlayerTempl(int id, byte sex, byte isMage)
	{
		_classId = id;
		_sex = sex;
		_isMage = isMage;
	}
}