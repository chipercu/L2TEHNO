package l2open.gameserver.model;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.serverpackets.ShortBuffStatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.Func;
import l2open.util.GArray;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class EffectList
{
	public static final int NONE_SLOT_TYPE = -1;
	public static final int BUFF_SLOT_TYPE = 0;
	public static final int MUSIC_SLOT_TYPE = 1;
	public static final int TRIGGER_SLOT_TYPE = 2;
	public static final int DEBUFF_SLOT_TYPE = 3;
	private L2Character _owner;
	private ConcurrentLinkedQueue<L2Skill> _skills;
	private ConcurrentLinkedQueue<L2Effect> _effects;
	private ConcurrentLinkedQueue<L2Effect> _effectsPassive;
	private Lock lock = new ReentrantLock();

	public EffectList(L2Character owner)
	{
		_owner = owner;
	}

	/**
	 * Возвращает число эффектов соответствующее данному скиллу
	 */
	public int getEffectsCountForSkill(int skill_id)
	{
		if(isEmpty())
			return 0;
		int count = 0;
		for(L2Effect e : _effects)
			if(e.getSkill().getId() == skill_id)
				count++;
		return count;
	}

	public int getActiveMusicCount(int skill_id)
	{
		if(isEmpty())
			return 0;

		List<Integer> ret = new ArrayList<Integer>();
		for(L2Effect e : _effects)
			if(e.getSkill().isMusic() && e.getSkill().getId() != skill_id && e.getTimeLeft() > 1000 && !ret.contains(e.getSkill().getId()))
				ret.add(e.getSkill().getId());

		return ret.size();
	}

	public ConcurrentLinkedQueue<L2Effect> getAllCancelableEffects(int offensive)
	{
		ConcurrentLinkedQueue<L2Effect> ret = new ConcurrentLinkedQueue<L2Effect>();
		for(L2Effect e : getAllEffects())
			if(e.getSkill().isCancelable())
				if(offensive == 1 || e.getSkill().isOffensive() == (offensive > 0))
					ret.add(e);
		return ret;
	}

	public L2Effect getEffectByType(EffectType et)
	{
		if(_effects != null)
			for(L2Effect e : _effects)
				if(e.getEffectType() == et)
					return e;
		return null;
	}

	public GArray<L2Effect> getEffectsBySkill(L2Skill skill)
	{
		if(skill == null)
			return null;
		return getEffectsBySkillId(skill.getId());
	}

	public GArray<L2Effect> getEffectsBySkillId(int skillId)
	{
		if(_effects == null && _effectsPassive == null)
			return null;
		GArray<L2Effect> temp = new GArray<L2Effect>();
		if(_effects != null)
			for(L2Effect e : _effects)
				if(e.getSkill().getId() == skillId)
					temp.add(e);
		if(_effectsPassive != null)
			for(L2Effect e : _effectsPassive)
				if(e.getSkill().getId() == skillId)
					temp.add(e);

		return temp.isEmpty() ? null : temp;
	}
	
	/**
	 * Возвращает только первый эффект у скилла! Не использовать на скилле у которых два и более эффекта (просто вернёт первый из них).
	 */
	public L2Effect getEffectBySkillId(int skillId)
	{
		if(_effects == null)
			return null;
		for(L2Effect e : _effects)
			if(e.getSkill().getId() == skillId)
				return e;

		return null;
	}

	public L2Effect getEffectByStackType(SkillAbnormalType type)
	{
		if(_effects == null)
			return null;
		for(L2Effect e : _effects)
			if(e.getAbnormalType() == type)
				return e;
		return null;
	}

	public int GetAbnormalLevel(SkillAbnormalType type)
	{
		if(_effects == null)
			return -1;
		for(L2Effect e : _effects)
			if(e != null && e.getAbnormalType() == type)
				return e.getAbnormalLv();
		return -1;
	}

	/*public int GetAbnormalLevel(SkillAbnormalType type)
	{
		if(_abnormal_list.size() == 0 || !_abnormal_list.containsKey(type))
			return -1;
		return _abnormal_list.get(type);
	}*/

	public boolean containEffectFromSkills(int[] skillIds)
	{
		if(_effects == null)
			return false;
		for(L2Effect e : _effects)
		{
			int id1 = e.getSkill().getId();
			for(int id2 : skillIds)
				if(id1 == id2)
					return true;
		}

		return false;
	}

	public ConcurrentLinkedQueue<L2Effect> getAllEffects()
	{
		if(_effects == null)
			return new ConcurrentLinkedQueue<L2Effect>();
		return _effects;
	}

	public ConcurrentLinkedQueue<L2Skill> getAllSkills(boolean no_toggle)
	{
		if(_effects == null)
			return new ConcurrentLinkedQueue<L2Skill>();
		ConcurrentLinkedQueue<L2Skill> ret = new ConcurrentLinkedQueue<L2Skill>();
		for(L2Effect e : getAllEffects())
			if((!no_toggle || !e.getSkill().isToggle()) && !ret.contains(e.getSkill()))
				ret.add(e.getSkill());
		return ret;
	}

	public boolean isEmpty()
	{
		return _effects == null || _effects.isEmpty();
	}

	/**
	 * Возвращает первые эффекты для всех скиллов. Нужно для отображения не
	 * более чем 1 иконки для каждого скилла.
	 */
	public L2Effect[] getAllFirstEffects()
	{
		if(_effects == null)
			return new L2Effect[0];

		FastMap<Integer, L2Effect> temp = new FastMap<Integer, L2Effect>();

		if(_effects != null)
			for(L2Effect ef : _effects)
				if(ef != null)
					temp.put(ef.getSkill().getId(), ef);

		Collection<L2Effect> temp2 = temp.values();
		return temp2.toArray(new L2Effect[temp2.size()]);
	}

	/**
	 * Ограничение на количество бафов
	 */
	private void checkBuffSlots(L2Effect newEffect)
	{
		L2Character actor = getOwner();
		if(actor == null)
			return;

		if(_effects == null)
			return;
		int slotType = getSlotType(newEffect);
		if(slotType == -1)
			return;

		int size = 0;
		ArrayList<Integer> skillIds = new ArrayList<Integer>();
		for(L2Effect e : _effects)
		{
			if(e.isInUse())
			{
				if(e.getSkill().equals(newEffect.getSkill()))
					return;
				if(!skillIds.contains(e.getSkill().getId()))
				{
					int subType = getSlotType(e);
					if(subType == slotType)
					{
						size++;
						skillIds.add(e.getSkill().getId());
					}
				}
			}
		}
		int limit = 0;
		switch(slotType)
		{
			case BUFF_SLOT_TYPE:
				limit = actor.getBuffLimit();
				break;
			case MUSIC_SLOT_TYPE:
				limit = actor.getSongLimit();
				break;
			case DEBUFF_SLOT_TYPE:
				limit = ConfigValue.DebuffLimit;
				break;
			case TRIGGER_SLOT_TYPE:
				limit = ConfigValue.TriggerLimit;
				break;
		}

		if(size < limit)
			return;
		int skillId = 0;
		for(L2Effect e : _effects)
			if(e.isInUse() && getSlotType(e) == slotType)
			{
				skillId = e.getSkill().getId();
				break;
			}

		if(skillId != 0)
			stopEffect(skillId);
	}

	public static int getSlotType(L2Effect e)
	{
		if(e.getSkill().isPassive() || e.getSkill().isToggle() || e.getSkill().isTransformation() || e.getAbnormalType() == SkillAbnormalType.life_force_kamael || e.getAbnormalType() == SkillAbnormalType.hp_recover || e.getAbnormalType() == SkillAbnormalType.mp_recover || e.getAbnormalType() == SkillAbnormalType.life_force_others || e.getAbnormalType() == SkillAbnormalType.vote)
			return NONE_SLOT_TYPE;
		if(e.isOffensive())
			return DEBUFF_SLOT_TYPE;
		if(e.getSkill().isMusic())
			return MUSIC_SLOT_TYPE;
		if(e.getSkill().isTrigger())
			return TRIGGER_SLOT_TYPE;
		return BUFF_SLOT_TYPE;
	}

	public static boolean checkStackType(SkillAbnormalType ef1, SkillAbnormalType ef2)
	{
		if(ef1 != SkillAbnormalType.none && ef1 == ef2)
			return true;
		return false;
	}

	public void addEffectPassive(L2Effect newEffect)
	{
		/*lock.lock();
		try
		{*/
			if(_effectsPassive == null)
				_effectsPassive = new ConcurrentLinkedQueue<L2Effect>();
			_effectsPassive.add(newEffect);
			// Запускаем эффект
			newEffect.setInUse(true);
			newEffect.setActive(true);
		/*}
		finally
		{
			lock.unlock();
		}*/
	}

	public int addEffect(L2Effect newEffect)
	{
		L2Character owner = getOwner();
		if(owner == null || newEffect == null)
			return 0;
		else if(newEffect.getSkill().isPassive())
		{
			addEffectPassive(newEffect);
			return 0;
		}
		// Проверка на имунность к бафам/дебафам
		else if(owner.p_block_debuff.get() && newEffect.getSkill().canBeImmune() && newEffect.isOffensive())
			return 0;
		int skid = newEffect.getSkill().getId();
		// Хербы при вызванном саммоне делятся с саммоном пополам
		if((owner.isSummon() || owner.getPet() != null && !owner.getPet().isDead() && owner.getPet().isSummon()) && newEffect.getSkill().getAbnormalInstant() == 1)
			newEffect.setPeriod(newEffect.getPeriod() / 2);

		lock.lock();
		try
		{
			if(_effects == null)
				_effects = new ConcurrentLinkedQueue<L2Effect>();

			//System.out.println(owner + " " + Arrays.toString(_effects.toArray()));

			// Проверяем на лимиты бафов/дебафов
			checkBuffSlots(newEffect);

			if(!_effects.contains(newEffect))
				// Применяем эффект к параметрам персонажа
				owner.addStatFuncs(newEffect.getStatFuncs());

			// Добавляем новый эффект
			_effects.add(newEffect);
			//_abnormal_list.put(newEffect.getAbnormalType(), newEffect.getAbnormalLv());

			// Запускаем эффект
			newEffect.setInUse(true);
			newEffect.setActive(true);
		}
		finally
		{
			lock.unlock();
		}

		// затычка на баффы повышающие хп/мп
		int result = 1;
		boolean can_update_npc = false;
		for(Func f : newEffect.getStatFuncs())
		{
			if(f._stat == Stats.p_max_hp)
			{
				//can_update_npc = true;
				result |= 2;
			}
			else if(f._stat == Stats.p_max_mp)
				result |= 4;
			else if(f._stat == Stats.p_max_cp)
				result |= 8;
			else if(f._stat == Stats.p_speed/* || f._stat == Stats.p_attack_speed || f._stat == Stats.p_magic_speed*/)
				can_update_npc = true;
		}
		if(owner.isNpc() && can_update_npc)
			owner.updateAbnormalEffect();
		return result;
	}

	/**
	 * Вызывающий метод синхронизирован, дополнительная синхронизация не нужна.
	 * @see l2open.gameserver.model.L2Effect#stopEffectTask()
	 * @param effect эффект для удаления
	 */
	public void removeEffect(L2Effect effect, boolean update, boolean remove, boolean replace)
	{
		L2Character owner = getOwner();
		if(owner == null)
			return;
		else if(effect != null && (effect.getSkill().isPassive()/* || effect.getSkill().isToggle()*/) && _effectsPassive != null && _effectsPassive.contains(effect))
		{
			/*lock.lock();
			try
			{*/
				effect.setInUse(false);
				_effectsPassive.remove(effect);
			/*}
			finally
			{
				lock.unlock();
			}*/
			return;
		}
		else if(effect == null || _effects == null || !_effects.contains(effect))
			return;
		// У туглов свой месседж.
		else if(remove && !effect.getSkill().isToggle() && effect.getTemplate()._is_first == 0)
			owner.sendPacket(new SystemMessage(SystemMessage.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effect.getSkill().getId(), effect.getSkill().getDisplayLevel()));

		owner.removeStatsOwner(effect);
		effect.setInUse(false);
		/*lock.lock();
		try
		{*/
			_effects.remove(effect);
		/*}
		finally
		{
			lock.unlock();
		}*/
		removeNextRun(effect.getAbnormalType(), !replace);
		//if(_abnormal_list.containsKey(effect.getAbnormalType()))
		//	_abnormal_list.remove(effect.getAbnormalType());

		if(owner.isPlayer() && (effect.getAbnormalType() == SkillAbnormalType.life_force_kamael || effect.getAbnormalType() == SkillAbnormalType.hp_recover || effect.getAbnormalType() == SkillAbnormalType.life_force_others))
			owner.sendPacket(new ShortBuffStatusUpdate());
		if(update)
			owner.updateEffectIcons();
		boolean can_update_npc = false;
		for(Func f : effect.getStatFuncs())
			if(f._stat == Stats.p_speed/* || f._stat == Stats.p_attack_speed || f._stat == Stats.p_magic_speed || f._stat == Stats.p_max_hp*/)
				can_update_npc = true;
		if(owner.isNpc() && can_update_npc)
			owner.updateAbnormalEffect();
	}

	/**
	 * Низкоуровневый метод, не использовать
	 */
	public void removeFromList(L2Effect effect)
	{
		_effects.remove(effect);
		//if(_abnormal_list.containsKey(effect.getAbnormalType()))
		//	_abnormal_list.remove(effect.getAbnormalType());
	}

	public void stopAllEffects()
	{
		stopAllEffects(true);
	}

	public void stopAllEffects(boolean update_icon)
	{
		L2Character owner = getOwner();
		if(owner == null)
			return;

		if(_effects != null)
		{
			/*lock.lock();
			try
			{*/
				owner.setMassUpdating(true);
				for(L2Effect e : _effects)
					if(e != null)
						e.exit(false, false);
				owner.setMassUpdating(false);
			/*}
			finally
			{
				lock.unlock();
			}*/
			owner.sendChanges();
			if(update_icon)
				owner.updateEffectIcons();
		}
	}

	public void stopEffect(int skillId)
	{
		stopEffect(skillId, true, true);
	}

	public void stopEffect(int skillId, boolean can_delay, boolean can_update)
	{
		if(_effects != null)
		{
			boolean update=false;
			for(L2Effect e : _effects)
				if(e != null && e.getSkill().getId() == skillId)
				{
					e.setCanDelay(can_delay);
					e.exit(false, false);
					update = can_update;
				}
			if(update)
				_owner.updateEffectIcons();
		}
	}

	public void stopEffect(SkillAbnormalType sat)
	{
		if(_effects != null)
		{
			boolean update=false;
			for(L2Effect e : _effects)
				if(e != null && e.getAbnormalType() == sat)
				{
					e.exit(false, false);
					update = true;
				}
			if(update)
				_owner.updateEffectIcons();
		}
	}

	public void stopEffect(L2Skill skill)
	{
		if(skill != null)
			stopEffect(skill.getId(), true, true);
	}

	public void stopEffectByDisplayId(int skillId)
	{
		if(_effects != null)
		{
			boolean update=false;
			for(L2Effect e : _effects)
				if(e != null && e.getSkill().getDisplayId() == skillId)
				{
					e.exit(false, false);
					update = true;
				}
			if(update)
				_owner.updateEffectIcons();
		}
	}

	/*
	 * Находит скиллы с указанным эффектом, и останавливает у этих скиллов все эффекты (не только указанный).
	 */
	public void stopAllSkillEffects(EffectType type)
	{
		if(_effects != null)
		{
			GArray<Integer> temp = new GArray<Integer>();
			for(L2Effect e : _effects)
				if(e.getEffectType() == type)
					temp.add(e.getSkill().getId());

			for(Integer id : temp)
				stopEffect(id);
		}
	}

	public void setOwner(L2Character owner)
	{
		_owner = owner;
	}

	private L2Character getOwner()
	{
		return _owner;
	}

	// TODO: Потом переделать!
	private HashMap<SkillAbnormalType, GArray<L2Effect>> _next_run = new HashMap<SkillAbnormalType, GArray<L2Effect>>();

	public void addNextRun(SkillAbnormalType sat, Integer id)
	{
		// if(ef != null && ef.isInUse() && checkStackType(ef.getAbnormalType(), next.getAbnormalType()))
		GArray<L2Effect> e_list = getEffectsBySkillId(id);
		if(e_list != null)
		{
			for(L2Effect e : e_list)
				if(e != null && !e._instantly)
				{
					e.setActive(false);
					getOwner().removeStatsOwner(e);
					removeFromList(e);
				}
			_next_run.put(sat, e_list);
		}
	}

	public void removeNextRun(SkillAbnormalType sat, boolean run)
	{
		if(_next_run.containsKey(sat))
		{
			GArray<L2Effect> e_list = _next_run.remove(sat);
			if(e_list != null)
			{
				if(run)
				{
					for(L2Effect e : e_list)
						if(e != null && !e._instantly && !e.isFinished())
						{
							e.setActive(true);
							_effects.add(e);
							getOwner().addStatFuncs(e.getStatFuncs());
						}
					getOwner().updateStats();
				}
				else
				{
					for(L2Effect e : e_list)
						if(e != null && !e._instantly)
							e.exit(false, false);
				}
			}
		}
	}
}