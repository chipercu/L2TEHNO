package l2open.gameserver.templates;

import javolution.util.FastMap;
import l2open.extensions.scripts.Script;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Drop;
import l2open.gameserver.model.L2DropData;
import l2open.gameserver.model.L2MinionData;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.base.*;
import l2open.gameserver.model.instances.L2BossInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemLocation;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.skills.*;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.ArrayUtils;
import l2open.util.GArray;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This cl contains all generic data of a L2Spawn object.<BR><BR>
 *
 * <B><U> Data</U> :</B><BR><BR>
 * <li>npcId, type, name, sex</li>
 * <li>revardExp, revardSp</li>
 * <li>aggroRange, factionId, factionRange</li>
 * <li>rhand, lhand, armor</li>
 * <li>_drops</li>
 * <li>_minions</li>
 * <li>_teachInfo</li>
 * <li>_skills</li>
 * <li>_questsStart</li><BR><BR>
 */
public final class L2NpcTemplate extends L2CharTemplate
{
	private static Logger _log = Logger.getLogger(L2NpcTemplate.class.getName());

	public static enum ShotsType
	{
		NONE,
		SOUL,
		SPIRIT,
		BSPIRIT,
		SOUL_SPIRIT,
		SOUL_BSPIRIT
	}

	private final static HashMap<Integer, L2Skill> _emptySkills = new HashMap<Integer, L2Skill>(0);
	private final static L2Skill[] _emptySkillArray = new L2Skill[0];

	public int npcId;
	public String type;
	public String ai_type;
	public String name;
	public String title;
	// не используется - public final String sex;
	public byte level;
	public int revardExp;
	public int revardSp;
	public double expRate;
	public short aggroRange;
	public int rhand;
	public int lhand;
	// не используется - public final int armor;
	public String factionId;
	public short factionRange;
	public String jClass;
	public int displayId = 0;
	public boolean isDropHerbs = false;
	public ShotsType shots;
	public boolean isRaid;
	public boolean isBoss;
	public boolean isEpicRaid;
	public boolean isRefRaid;
	public int atkElement;
	public int elemAtkPower;
	public int baseFireRes;
	public int baseWindRes;
	public int baseWaterRes;
	public int baseEarthRes;
	public int baseHolyRes;
	public int baseDarkRes;
	
	public int agro_range;
	public int event_flag;
	
	public int undying;
	public int can_be_attacked;
	public int corpse_time;

	public Race _race;

	/** fixed skills*/
	private int race = 0;
	public double rateHp = 1;

	/** The object containing all Item that can be dropped by L2NpcInstance using this L2NpcTemplate*/
	private L2Drop _drop = null;
	public int killscount = 0;

	/** The table containing all Minions that must be spawn with the L2NpcInstance using this L2NpcTemplate*/
	private final GArray<L2MinionData> _minions = new GArray<L2MinionData>(0);

	private GArray<ClassId> _teachInfo = null;
	private Map<QuestEventType, Quest[]> _questEvents;
	private Class<L2NpcInstance> this_class;

	private HashMap<Integer, L2Skill> _skills;
	private HashMap<SkillType, L2Skill[]> _skillsByType;
	private L2Skill[] _dam_skills, _dot_skills, _debuff_skills, _buff_skills, _stun_skills, _heal_skills;

	private GArray<L2ItemInstance> _inventory;

	public WeaponType base_attack_type = WeaponType.FIST;

	public boolean isFlying = false;
	public boolean chatWindowDisabled = false;
	public boolean searchingMaster = false;
	public boolean spawnAnimationDisabled = false;
	public boolean randomAnimationDisabled = false;

	/**
	 * Constructor<?> of L2Character.<BR><BR>
	 * @param set The StatsSet object to transfer data to the method
	 */
	public L2NpcTemplate(StatsSet set)
	{
		super(set);
		npcId = set.getInteger("npcId");
		displayId = set.getInteger("displayId");
		type = set.getString("type");
		ai_type = set.getString("ai_type");
		name = set.getString("name");
		title = set.getString("title");
		// sex = set.getString("sex");
		level = set.getByte("level");
		revardExp = set.getInteger("revardExp");
		revardSp = set.getInteger("revardSp");
		expRate = revardExp / (((double) level) * level);
		aggroRange = set.getShort("aggroRange");
		rhand = set.getInteger("rhand");
		lhand = set.getInteger("lhand");
		// armor = set.getInteger("armor");
		_race = Race.valueOf(set.getString("race"));
		jClass = set.getString("jClass", null);
		String f = set.getString("factionId", null);
		factionId = f == null ? "" : f.intern();
		factionRange = set.getShort("factionRange");
		isDropHerbs = set.getBool("isDropHerbs");
		shots = set.getEnum("shots", ShotsType.class, ShotsType.NONE);
		atkElement = set.getInteger("AtkElement");
		elemAtkPower = set.getInteger("elemAtkPower");
		baseFireRes = set.getInteger("FireRes");
		baseWindRes = set.getInteger("WindRes");
		baseWaterRes = set.getInteger("WaterRes");
		baseEarthRes = set.getInteger("EarthRes");
		baseHolyRes = set.getInteger("HolyRes");
		baseDarkRes = set.getInteger("DarkRes");
		agro_range = set.getInteger("agro_range");
		event_flag = set.getInteger("event_flag");
		undying = set.getInteger("undying");
		can_be_attacked = set.getInteger("can_be_attacked");
		corpse_time = set.getInteger("corpse_time");
		base_attack_type = set.getEnum("base_attack_type", WeaponType.class, WeaponType.FIST);
		setInstance(type);
	}

	public void setSet(StatsSet set)
	{
		npcId = set.getInteger("npcId");
		displayId = set.getInteger("displayId");
		type = set.getString("type");
		ai_type = set.getString("ai_type");
		name = set.getString("name");
		title = set.getString("title");
		level = set.getByte("level");
		revardExp = set.getInteger("revardExp");
		revardSp = set.getInteger("revardSp");
		expRate = revardExp / (((double) level) * level);
		aggroRange = set.getShort("aggroRange");
		rhand = set.getInteger("rhand");
		lhand = set.getInteger("lhand");
		jClass = set.getString("jClass", null);
		_race = Race.valueOf(set.getString("race"));
		String f = set.getString("factionId", null);
		factionId = f == null ? "" : f.intern();
		factionRange = set.getShort("factionRange");
		isDropHerbs = set.getBool("isDropHerbs");
		shots = set.getEnum("shots", ShotsType.class, ShotsType.NONE);
		atkElement = set.getInteger("AtkElement");
		elemAtkPower = set.getInteger("elemAtkPower");
		baseFireRes = set.getInteger("FireRes");
		baseWindRes = set.getInteger("WindRes");
		baseWaterRes = set.getInteger("WaterRes");
		baseEarthRes = set.getInteger("EarthRes");
		baseHolyRes = set.getInteger("HolyRes");
		baseDarkRes = set.getInteger("DarkRes");
		agro_range = set.getInteger("agro_range");
		event_flag = set.getInteger("event_flag");
		undying = set.getInteger("undying");
		can_be_attacked = set.getInteger("can_be_attacked");
		corpse_time = set.getInteger("corpse_time");
		base_attack_type = set.getEnum("base_attack_type", WeaponType.class, WeaponType.FIST);
		setSets(set);
	}

	public Class<L2NpcInstance> getInstanceClass()
	{
		return this_class;
	}

	public Constructor<?> getInstanceConstructor()
	{
		return this_class == null ? null : this_class.getConstructors()[0];
	}

	public Constructor<?> getInstanceConstructor(int index)
	{
		return this_class == null ? null : this_class.getConstructors()[index];
	}

	public boolean isInstanceOf(Class<?> _class)
	{
		return this_class != null && _class.isAssignableFrom(this_class);
	}

	/**
	 * Создает новый инстанс NPC. Для него следует вызывать (именно в этом порядке):
	 * <br> setSpawnedLoc (обязательно)
	 * <br> setReflection (если reflection не базовый)
	 * <br> onSpawn (обязательно)
	 * <br> setChampion (опционально)
	 * <br> setCurrentHpMp (если вызывался setChampion)
	 * <br> spawnMe (в качестве параметра брать getSpawnedLoc)
	 */
	public L2NpcInstance getNewInstance()
	{
		try
		{
			return (L2NpcInstance) getInstanceConstructor().newInstance(IdFactory.getInstance().getNextId(), this);
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "Unable to create instance of NPC " + npcId);
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setInstance(String type)
	{
		Class<L2NpcInstance> _this_class = null;
		try
		{
			_this_class = (Class<L2NpcInstance>) Class.forName("l2open.gameserver.model.instances." + type + "Instance");
		}
		catch(ClassNotFoundException e)
		{
			Script sc = Scripts.getInstance().getClasses().get("npc.model." + type + "Instance");
			if(sc != null)
				_this_class = (Class<L2NpcInstance>) sc.getRawClass();
			if(_this_class == null)
			{
				sc = Scripts.getInstance().getClasses().get("npc.pts." + type);
				if(sc != null)
					_this_class = (Class<L2NpcInstance>) sc.getRawClass();
			}
		}
		if(_this_class == null)
			_log.warning("Not found type: " + type);
		this_class = _this_class;
		isRaid = isInstanceOf(L2RaidBossInstance.class) && !isInstanceOf(L2ReflectionBossInstance.class);
		isEpicRaid = getNpcId() == 29181 || getNpcId() == 29179 || getNpcId() == 29180 || getNpcId() == 29047 || getNpcId() == 29068 || getNpcId() == 29020 || getNpcId() == 29028 || getNpcId() == 29062 || getNpcId() == 29065 || getNpcId() == 29186 || getNpcId() == 25700 || getNpcId() == 25699 || getNpcId() == 29178 || getNpcId() == 29177;
		isBoss = isInstanceOf(L2BossInstance.class);
		isRefRaid = isInstanceOf(L2ReflectionBossInstance.class);
	}

	public void addTeachInfo(ClassId classId)
	{
		if(_teachInfo == null)
			_teachInfo = new GArray<ClassId>();
		_teachInfo.add(classId);
	}

	public ClassId[] getTeachInfo()
	{
		if(_teachInfo == null)
			return null;
		return _teachInfo.toArray(new ClassId[_teachInfo.size()]);
	}

	public boolean canTeach(ClassId classId)
	{
		if(_teachInfo == null)
			return false;
		return _teachInfo.contains(classId);
	}

	public void addDropData(L2DropData drop)
	{
		if(_drop == null)
			_drop = new L2Drop();
		_drop.addData(drop);
	}

	public void addRaidData(L2MinionData minion)
	{
		_minions.add(minion);
	}

	public void addSkill(L2Skill skill)
	{
		if(_skills == null)
			_skills = new HashMap<Integer, L2Skill>();
		if(_skillsByType == null)
			_skillsByType = new HashMap<SkillType, L2Skill[]>();

		_skills.put(skill.getId(), skill);

		if(skill.isNotUsedByAI() || skill.getTargetType() == L2Skill.SkillTargetType.TARGET_NONE || skill.getSkillType() == L2Skill.SkillType.NOTDONE || !skill.isActive())
			return;

		L2Skill[] skilllist;
		if(_skillsByType.get(skill.getSkillType()) != null)
		{
			skilllist = new L2Skill[_skillsByType.get(skill.getSkillType()).length + 1];
			System.arraycopy(_skillsByType.get(skill.getSkillType()), 0, skilllist, 0, _skillsByType.get(skill.getSkillType()).length);
		}
		else
			skilllist = new L2Skill[1];

		skilllist[skilllist.length - 1] = skill;

		_skillsByType.put(skill.getSkillType(), skilllist);
	}

	public L2Skill[] getSkillsByType(SkillType type)
	{
		if(_skillsByType == null)
			return _emptySkillArray;
		return _skillsByType.containsKey(type) ? _skillsByType.get(type) : _emptySkillArray;
	}

	public synchronized L2Skill[] getDamageSkills()
	{
		if(_dam_skills == null)
		{
			List<L2Skill> _list = new ArrayList<L2Skill>();
			for(L2Skill s : summ(getSkillsByType(SkillType.PDAM), getSkillsByType(SkillType.MANADAM), getSkillsByType(SkillType.MDAM), getSkillsByType(SkillType.DRAIN), getSkillsByType(SkillType.DRAIN_SOUL)))
				if(s != null && !ArrayUtils.contains(getStunSkills(), s) && !ArrayUtils.contains(getHealSkills(), s) && !s.isSuicideAttack())
					_list.add(s);
			_dam_skills = _list.toArray(new L2Skill[_list.size()]);
		}
		return _dam_skills;
	}

	public synchronized L2Skill[] getDotSkills()
	{
		if(_dot_skills == null)
		{
			List<L2Skill> _list = new ArrayList<L2Skill>();
			for(L2Skill s : summ(getSkillsByType(SkillType.DOT), getSkillsByType(SkillType.MDOT), getSkillsByType(SkillType.POISON), getSkillsByType(SkillType.BLEED)))
				if(s != null && !ArrayUtils.contains(getStunSkills(), s) && !ArrayUtils.contains(getHealSkills(), s))
					_list.add(s);
			_dot_skills = _list.toArray(new L2Skill[_list.size()]);
		}
		return _dot_skills;
	}

	public synchronized L2Skill[] getDebuffSkills()
	{
		if(_debuff_skills == null)
		{
			List<L2Skill> _list = new ArrayList<L2Skill>();
			for(L2Skill s : summ(getSkillsByType(SkillType.DEBUFF), getSkillsByType(SkillType.ROOT), getSkillsByType(SkillType.MUTE), getSkillsByType(SkillType.TELEPORT_NPC)))
				if(s != null && !ArrayUtils.contains(getStunSkills(), s) && !ArrayUtils.contains(getHealSkills(), s))
					_list.add(s);
			_debuff_skills = _list.toArray(new L2Skill[_list.size()]);
		}
		return _debuff_skills;
	}

	public synchronized L2Skill[] getBuffSkills()
	{
		if(_buff_skills == null)
		{
			List<L2Skill> _list = new ArrayList<L2Skill>();
			for(L2Skill s : getSkillsByType(SkillType.BUFF))
				if(s != null && !ArrayUtils.contains(getStunSkills(), s) && !ArrayUtils.contains(getHealSkills(), s))
					_list.add(s);
			_buff_skills = _list.toArray(new L2Skill[_list.size()]);
		}
		return _buff_skills;
	}

	public synchronized L2Skill[] getStunSkills()
	{
		if(_stun_skills == null)
		{
			List<L2Skill> _list = new ArrayList<L2Skill>();
			for(L2Skill sk : getSkills().values())
				if(sk.getTraitType() == SkillTrait.trait_paralyze || sk.getAbnormalType() == SkillAbnormalType.paralyze || sk.getTraitType() == SkillTrait.trait_shock || sk.getAbnormalType() == SkillAbnormalType.stun || sk.getTraitType() == SkillTrait.trait_sleep || sk.getAbnormalType() == SkillAbnormalType.sleep || sk.getTraitType() == SkillTrait.trait_shock)
					_list.add(sk);
			if(_skillsByType != null)
			{
				if(_skillsByType.containsKey(SkillType.STUN))
					for(L2Skill s : _skillsByType.get(SkillType.STUN))
						if(s != null)
							_list.add(s);
				if(_skillsByType.containsKey(SkillType.SLEEP))
					for(L2Skill s : _skillsByType.get(SkillType.SLEEP))
						if(s != null)
							_list.add(s);
				if(_skillsByType.containsKey(SkillType.PARALYZE))
					for(L2Skill s : _skillsByType.get(SkillType.PARALYZE))
						if(s != null)
							_list.add(s);
			}
			_stun_skills = _list.toArray(new L2Skill[_list.size()]);
		}
		return _stun_skills;
	}

	public synchronized L2Skill[] getHealSkills()
	{
		if(_heal_skills == null)
		{
			List<L2Skill> _list = new ArrayList<L2Skill>();
			for(L2Skill sk : getSkills().values())
				if(sk.isHealSkill())
					_list.add(sk);
			if(_skillsByType != null)
			{
				if(_skillsByType.containsKey(SkillType.HEAL))
					for(L2Skill s : _skillsByType.get(SkillType.HEAL))
						if(s != null)
							_list.add(s);
				if(_skillsByType.containsKey(SkillType.HEAL_PERCENT))
					for(L2Skill s : _skillsByType.get(SkillType.HEAL_PERCENT))
						if(s != null)
							_list.add(s);
				if(_skillsByType.containsKey(SkillType.HOT))
					for(L2Skill s : _skillsByType.get(SkillType.HOT))
						if(s != null)
							_list.add(s);
			}
			_heal_skills = _list.toArray(new L2Skill[_list.size()]);
		}
		return _heal_skills;
	}

	private static final L2Skill[] summ(L2Skill[]... skills2d)
	{
		int i = 0;
		for(L2Skill[] skills : skills2d)
			i += skills.length;
		if(i == 0)
			return _emptySkillArray;
		L2Skill[] result = new L2Skill[i];
		i = 0;
		for(L2Skill[] skills : skills2d)
		{
			System.arraycopy(skills, 0, result, i, skills.length);
			i += skills.length;
		}
		return result;
	}

	/**
	 * Return the list of all possible drops of this L2NpcTemplate.<BR><BR>
	 */
	public L2Drop getDropData()
	{
		return _drop;
	}

	/**
	 * Обнуляет дроплист моба
	 */
	public void clearDropData()
	{
		_drop = null;
	}

	/**
	 * Return the list of all Minions that must be spawn with the L2NpcInstance using this L2NpcTemplate.<BR><BR>
	 */
	public GArray<L2MinionData> getMinionData()
	{
		return _minions;
	}

	public HashMap<Integer, L2Skill> getSkills()
	{
		return _skills == null ? _emptySkills : _skills;
	}

	public void addQuestEvent(QuestEventType EventType, Quest q)
	{
		if(_questEvents == null)
			_questEvents = new FastMap<QuestEventType, Quest[]>().setShared(true);

		if(_questEvents.get(EventType) == null)
			_questEvents.put(EventType, new Quest[] { q });
		else
		{
			Quest[] _quests = _questEvents.get(EventType);
			int len = _quests.length;

			Quest[] tmp = new Quest[len + 1];
			for(int i = 0; i < len; i++)
			{
				if(_quests[i].getName().equals(q.getName()))
				{
					_quests[i] = q;
					return;
				}
				tmp[i] = _quests[i];
			}
			tmp[len] = q;

			_questEvents.put(EventType, tmp);
		}
	}

	public Quest[] getEventQuests(QuestEventType EventType)
	{
		if(_questEvents == null)
			return null;
		return _questEvents.get(EventType);
	}

	public boolean hasQuestEvents()
	{
		return _questEvents != null && !_questEvents.isEmpty();
	}

	public boolean canTalkThisQuest(Quest quest)
	{
		/*if(hasQuestEvents())
		{
			Quest[] quests = getEventQuests(QuestEventType.QUEST_START);
			if(quests != null)
				for(Quest q : quests)
					if(q.getQuestIntId() == quest.getQuestIntId())
						return true;

			quests = getEventQuests(QuestEventType.QUEST_TALK);
			if(quests != null)
				for(Quest q : quests)
					if(q.getQuestIntId() == quest.getQuestIntId())
						return true;

			quests = getEventQuests(QuestEventType.NPC_FIRST_TALK);
			if(quests != null)
				for(Quest q : quests)
					if(q.getQuestIntId() == quest.getQuestIntId())
						return true;
		}
		return false;*/
		return true;
	}

	public int getRace()
	{
		return race;
	}

	public void setRace(int newrace)
	{
		race = newrace;
	}

	public boolean isUndead()
	{
		return race == 1;
	}

	public void setRateHp(double newrate)
	{
		rateHp = newrate;
	}

	@Override
	public String toString()
	{
		return "Npc template " + name + "[" + npcId + "]";
	}

	@Override
	public int getNpcId()
	{
		return npcId;
	}

	public final String getJClass()
	{
		return jClass;
	}

	public synchronized void giveItem(L2ItemInstance item, boolean store)
	{
		if(_inventory == null)
			_inventory = new GArray<L2ItemInstance>();

		synchronized (_inventory)
		{
			if(item.isStackable())
				for(L2ItemInstance i : _inventory)
					if(i.getItemId() == item.getItemId())
					{
						i.setCount(item.getCount() + i.getCount());
						if(store)
							i.updateDatabase(true, false);
						return;
					}

			_inventory.add(item);

			if(store)
			{
				item.setOwnerId(getNpcId());
				item.setLocation(ItemLocation.MONSTER);
				item.updateDatabase();
			}
		}
	}

	/**
	 * Возвращает все вещи в инвентаре и удаляет инвентарь. Может вернуть null если инвентарь пуст.
	 */
	public GArray<L2ItemInstance> takeInventory()
	{
		if(_inventory != null)
			synchronized (_inventory)
			{
				GArray<L2ItemInstance> ret = _inventory;
				_inventory = null;
				return ret;
			}
		return null;
	}

	public static StatsSet getEmptyStatsSet()
	{
		StatsSet npcDat = L2CharTemplate.getEmptyStatsSet();

		npcDat.set("npcId", 0);
		npcDat.set("displayId", 0);
		npcDat.set("level", 0);
		npcDat.set("name", "");
		npcDat.set("title", "");
		npcDat.set("sex", "male");
		npcDat.set("type", "");
		npcDat.set("ai_type", "npc");
		npcDat.set("revardExp", 0);
		npcDat.set("revardSp", 0);
		npcDat.set("aggroRange", 0);
		npcDat.set("rhand", 0);
		npcDat.set("lhand", 0);
		npcDat.set("armor", 0);
		npcDat.set("factionId", "");
		npcDat.set("factionRange", 0);
		npcDat.set("isDropHerbs", false);
		npcDat.set("AtkElement", -1);
		npcDat.set("elemAtkPower", 0);
		npcDat.set("FireRes", 0);
		npcDat.set("WindRes", 0);
		npcDat.set("WaterRes", 0);
		npcDat.set("EarthRes", 0);
		npcDat.set("HolyRes", 0);
		npcDat.set("DarkRes", 0);
		npcDat.set("agro_range", 0);
		npcDat.set("event_flag", 0);
		
		npcDat.set("undying", 0);
		npcDat.set("can_be_attacked", 1);
		npcDat.set("corpse_time", 7);
		npcDat.set("race", "none");
		return npcDat;
	}
}