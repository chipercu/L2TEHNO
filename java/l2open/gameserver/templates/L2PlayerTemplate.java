package l2open.gameserver.templates;

import l2open.config.ConfigValue;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;

public class L2PlayerTemplate extends L2CharTemplate
{
	/** The Class<?> object of the L2Player */
	public final ClassId classId;

	public final Race race;
	public final String className;

	public final Location spawnLoc = new Location();

	public final boolean isMale;

	public final int classBaseLevel;
	public final float lvlHpAdd;
	public final float lvlHpMod;
	public final float lvlCpAdd;
	public final float lvlCpMod;
	public final float lvlMpAdd;
	public final float lvlMpMod;

	public final float p_atk_mod;
	public final float m_atk_mod;
	public final float p_def_mod;
	public final float m_def_mod;

	public final float hp_mod;
	public final float mp_mod;
	public final float p_critical_rate_mod;

	public final float m_atk_spd_mod;
	public final float p_atk_spd_mod;
	public final float m_atk_crit_chance_mod;
	public final float p_atk_crit_chance_mod;
	public final float p_critical_damage_per_mod;
	public final int p_critical_damage_diff_mod;

	private GArray<L2Item> _items = new GArray<L2Item>();

	public L2PlayerTemplate(StatsSet set)
	{
		super(set);
		classId = ClassId.values()[set.getInteger("classId")];
		race = Race.values()[set.getInteger("raceId")];
		className = set.getString("className");

		if(ConfigValue.CharacterCreateLoc.length == 3)
			spawnLoc.set(new Location(ConfigValue.CharacterCreateLoc[0], ConfigValue.CharacterCreateLoc[1], ConfigValue.CharacterCreateLoc[2]));
		else
			spawnLoc.set(new Location(set.getInteger("spawnX"), set.getInteger("spawnY"), set.getInteger("spawnZ")));

		isMale = set.getBool("isMale", true);

		classBaseLevel = set.getInteger("classBaseLevel");
		lvlHpAdd = set.getFloat("lvlHpAdd");
		lvlHpMod = set.getFloat("lvlHpMod");
		lvlCpAdd = set.getFloat("lvlCpAdd");
		lvlCpMod = set.getFloat("lvlCpMod");
		lvlMpAdd = set.getFloat("lvlMpAdd");
		lvlMpMod = set.getFloat("lvlMpMod");

		p_atk_mod = set.getFloat("p_atk_mod");
		m_atk_mod = set.getFloat("m_atk_mod");
		p_def_mod = set.getFloat("p_def_mod");
		m_def_mod = set.getFloat("m_def_mod");

		hp_mod = set.getFloat("hp_mod");
		mp_mod = set.getFloat("mp_mod");
		p_critical_rate_mod = set.getFloat("p_critical_rate_mod");

		m_atk_spd_mod = set.getFloat("m_atk_spd_mod");
		p_atk_spd_mod = set.getFloat("p_atk_spd_mod");
		m_atk_crit_chance_mod = set.getFloat("m_atk_crit_chance_mod");
		p_atk_crit_chance_mod = set.getFloat("p_atk_crit_chance_mod");
		p_critical_damage_per_mod = set.getFloat("p_critical_damage_per_mod");
		p_critical_damage_diff_mod = set.getInteger("p_critical_damage_diff_mod");
	}

	/**
	 * add starter equipment
	 * @param i
	 */
	public void addItem(int itemId)
	{
		L2Item item = ItemTemplates.getInstance().getTemplate(itemId);
		if(item != null)
			_items.add(item);
	}

	/**
	 *
	 * @return itemIds of all the starter equipment
	 */
	public L2Item[] getItems()
	{
		return _items.toArray(new L2Item[_items.size()]);
	}
}