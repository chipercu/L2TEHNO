/**
 * 
 */
package l2open.gameserver.skills;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.effects.*;
import l2open.gameserver.skills.funcs.*;
import l2open.gameserver.skills.pts_effects.*;
import l2open.gameserver.skills.pts_effects.i_dispel_by_category.Category;
import l2open.pts.templ.*;

import java.lang.reflect.Constructor;

public enum EffectType
{
	/**
		-CurseOfLifeFlow.java
		-DamOverTimeLethal.java
		DamOverTime.java
		Fear.java
		HealPercent.java
		Symbol.java
	**/
	// Основные эффекты
	AddSkills(EffectAddSkills.class, false),
	AgathionResurrect(EffectAgathionRes.class, true),
	Betray(EffectBetray.class, true),
	BlessNoblesse(EffectBlessNoblesse.class, true),
	Buff(EffectBuff.class, false),
	BuffHung(EffectBuffHung.class, false),
	CallSkills(EffectCallSkills.class, false),
	CharmOfCourage(EffectCharmOfCourage.class, true),
	CPDamPercent(EffectCPDamPercent.class, true),
	CPHeal(EffectCpHeal.class, true),
	DamOverTime(EffectDamOverTime.class, false),
	DamOverTimeLethal(EffectDamOverTimeLethal.class, false),
	Disarm(EffectDisarm.class, true),
	Discord(EffectDiscord.class, true),
	DispelEffects(EffectDispelEffects.class, true),
	Enervation(EffectEnervation.class, false),
	Fear(EffectFear.class, true),
	FreyaSkillIgnore(EffectFreyaSkillIgnore.class, false),
	Grow(EffectGrow.class, false),
	Hate(EffectHate.class, false),
	Heal(EffectHeal.class, false),
	HealCPPercent(EffectHealCPPercent.class, true),
	HealPercent(EffectHealPercent.class, false),
	HPDamPercent(EffectHPDamPercent.class, true),
	Interrupt(EffectInterrupt.class, true),
	ManaHeal(EffectManaHeal.class, false),
	ManaHealPercent(EffectManaHealPercent.class, false),
	Meditation(EffectMeditation.class, false),
	Mute(EffectMute.class, true),
	MuteAll(EffectMuteAll.class, true),
	MuteAttack(EffectMuteAttack.class, true),
	MutePhisycal(EffectMutePhisycal.class, true),
	Paralyze(EffectParalyze.class, true),
	Petrification(EffectPetrification.class, true),
	ResDebuff(EffectResDebuff.class, false),
	Salvation(EffectSalvation.class, true),
	Sleep(EffectSleep.class, true),
	Stun(EffectStun.class, true),
	Symbol(EffectSymbol.class, false),
	Transformation(EffectTransformation.class, true),
	Vitality(EffectBuff.class, true),
	Warp(EffectWarp.class, false),

	Debuff(EffectBuff.class, false),
	SoulRetain(EffectBuff.class, false),
	WatcherGaze(EffectBuff.class, false),
	TransferDam(EffectBuff.class, false),

	// ПТС эффекты...
	/** + **/c_chameleon_rest(c_chameleon_rest.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_chameleon_rest.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/c_fake_death(c_fake_death.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_fake_death.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/c_hp(c_hp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_hp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/c_item(c_item.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_item.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/c_mp(c_mp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_mp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/c_mp_by_level(c_mp_by_level.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_mp_by_level.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/c_rest(c_rest.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return c_rest.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	cub_attack_speed(cub_attack_speed.class),
	cub_block_act(cub_block_act.class),
	cub_heal(cub_heal.class),
	cub_hp(cub_hp.class),
	cub_hp_drain(cub_hp_drain.class),
	cub_m_attack(cub_m_attack.class),
	cub_physical_attack(cub_physical_attack.class),
	cub_physical_defence(cub_physical_defence.class),
	i_enchant_armor(i_enchant_armor.class),
	i_enchant_armor_rate(i_enchant_armor_rate.class),
	i_enchant_attribute(i_enchant_attribute.class),
	i_enchant_weapon(i_enchant_weapon.class),
	i_enchant_weapon_rate(i_enchant_weapon_rate.class),
	i_abnormal_time_change(i_abnormal_time_change.class),
	/** + **/i_add_hate(i_add_hate.class),
	i_add_max_entrance_inzone(i_add_max_entrance_inzone.class),
	/** + **/i_align_direction(i_align_direction.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_align_direction.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_backstab(i_backstab.class),
	i_betray(i_betray.class),
	i_blink(i_blink.class),
	i_bookmark_add_slot(i_bookmark_add_slot.class),
	i_bookmark_teleport(i_bookmark_teleport.class),
	i_call_party(i_call_party.class),
	i_call_pc(i_call_pc.class),
	i_call_skill(i_call_skill.class),
	i_capture_flag(i_capture_flag.class),
	i_capture_flag_start(i_capture_flag_start.class),
	i_capture_ownthing(i_capture_ownthing.class),
	i_capture_ownthing_start(i_capture_ownthing_start.class),
	i_change_face(i_change_face.class),
	i_change_hair_color(i_change_hair_color.class),
	i_change_hair_style(i_change_hair_style.class),
	i_change_skill_level(i_change_skill_level.class),
	i_collecting(i_collecting.class),
	i_compulsion_social_action(i_compulsion_social_action.class),
	i_confuse(i_confuse.class),
	/** + **/i_consume_body(i_consume_body.class),
	i_convert_item(i_convert_item.class),
	/** + **/i_cp(i_cp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_cp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, FuncPTS.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), FuncPTS.valueOf(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_death(i_death.class),
	i_death_link(i_death_link.class),
	i_defuse_trap(i_defuse_trap.class),
	/** + **/i_delete_hate(i_delete_hate.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_delete_hate.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_delete_hate_of_me(i_delete_hate_of_me.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_delete_hate_of_me.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_despawn(i_despawn.class),
	i_detect_object(i_detect_object.class),
	i_detect_trap(i_detect_trap.class),
	i_dismount_for_event(i_dismount_for_event.class),
	i_dispel_all(i_dispel_all.class),
	/** + **/i_dispel_by_category(i_dispel_by_category.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_dispel_by_category.class.getConstructor(Env.class, EffectTemplate.class, Category.class, Integer.class, Integer.class).newInstance(env, template, Category.valueOf(template._effect_param[0]), Integer.parseInt(template._effect_param[1]), Integer.parseInt(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_dispel_by_name(i_dispel_by_name.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_dispel_by_name.class.getConstructor(Env.class, EffectTemplate.class, String.class).newInstance(env, template, template._effect_param[0].substring(2, template._effect_param[0].length()-2));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_dispel_by_slot(i_dispel_by_slot.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_dispel_by_slot.class.getConstructor(Env.class, EffectTemplate.class, SkillAbnormalType.class, Integer.class).newInstance(env, template, SkillAbnormalType.valueOf(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_dispel_by_slot_myself(i_dispel_by_slot_myself.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_dispel_by_slot_myself.class.getConstructor(Env.class, EffectTemplate.class, SkillAbnormalType.class).newInstance(env, template, SkillAbnormalType.valueOf(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_dispel_by_slot_probability(i_dispel_by_slot_probability.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_dispel_by_slot_probability.class.getConstructor(Env.class, EffectTemplate.class, SkillAbnormalType.class, Integer.class).newInstance(env, template, SkillAbnormalType.valueOf(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_distrust(i_distrust.class),
	i_energy_attack(i_energy_attack.class),
	i_escape(i_escape.class),
	i_event_agathion_reuse_delay(i_event_agathion_reuse_delay.class),
	i_fatal_blow(i_fatal_blow.class),
	i_fishing_cast(i_fishing_cast.class),
	i_fishing_pumping(i_fishing_pumping.class),
	i_fishing_reeling(i_fishing_reeling.class),
	i_fishing_shot(i_fishing_shot.class),
	i_fly_away(i_fly_away.class),
	i_fly_self(i_fly_self.class),
	/** + **/i_focus_energy(i_focus_energy.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_focus_energy.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_focus_max_energy(i_focus_max_energy.class),
	i_focus_soul(i_focus_soul.class),
	i_food_for_pet(i_food_for_pet.class),
	i_force_sitdown(i_force_sitdown.class),
	/** + **/i_get_agro(i_get_agro.class),
	i_give_contribution(i_give_contribution.class),
	i_harvesting(i_harvesting.class),
	i_heal(i_heal.class),
	i_heal_link(i_heal_link.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_heal_link.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, FuncPTS.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), FuncPTS.valueOf(template._effect_param[1]), Integer.parseInt(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_holything_possess(i_holything_possess.class),
	/** + **/i_hp(i_hp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_hp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, FuncPTS.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), FuncPTS.valueOf(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_hp_by_level_self(i_hp_by_level_self.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_hp_by_level_self.class.getConstructor(Env.class, EffectTemplate.class, Double.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_hp_drain(i_hp_drain.class),
	/** + **/i_hp_per_max(i_hp_per_max.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_hp_per_max.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_hp_self(i_hp_self.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_hp_self.class.getConstructor(Env.class, EffectTemplate.class, Double.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_inform(i_inform.class),
	i_install_advance_base(i_install_advance_base.class),
	i_install_camp(i_install_camp.class),
	i_install_camp_ex(i_install_camp_ex.class),
	i_m_attack(i_m_attack.class),
	i_m_attack_by_abnormal(i_m_attack_by_abnormal.class),
	i_m_attack_by_dist(i_m_attack_by_dist.class),
	i_m_attack_by_hp(i_m_attack_by_hp.class),
	i_m_attack_by_range(i_m_attack_by_range.class),
	/** + **/i_m_attack_mp(i_m_attack_mp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_m_attack_mp.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Integer.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Integer.parseInt(template._effect_param[1]), Integer.parseInt(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_m_attack_over_hit(i_m_attack_over_hit.class),
	i_m_attack_over_hit_range(i_m_attack_over_hit_range.class),
	i_m_attack_range(i_m_attack_range.class),
	i_m_soul_attack(i_m_soul_attack.class),
	i_mount_for_event(i_mount_for_event.class),
	/** + **/i_mp(i_mp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_mp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, FuncPTS.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), FuncPTS.valueOf(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_mp_by_level(i_mp_by_level.class),
	i_mp_by_level_self(i_mp_by_level_self.class),
	/** + **/i_mp_per_max(i_mp_per_max.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_mp_per_max.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_my_summon_kill(i_my_summon_kill.class),
	/** + **/i_npc_kill(i_npc_kill.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_npc_kill.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_open_common_recipebook(i_open_common_recipebook.class),
	i_open_dwarf_recipebook(i_open_dwarf_recipebook.class),
	i_p_attack(i_p_attack.class),
	i_p_attack_by_dist(i_p_attack_by_dist.class),
	i_p_soul_attack(i_p_soul_attack.class),
	i_pcbang_point_up(i_pcbang_point_up.class),
	i_physical_attack_hp_link(i_physical_attack_hp_link.class),
	i_pledge_send_system_message(i_pledge_send_system_message.class),
	/** + **/i_randomize_hate(i_randomize_hate.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_randomize_hate.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_real_damage(i_real_damage.class),
	i_rebalance_hp(i_rebalance_hp.class),
	i_rebalance_mp(i_rebalance_mp.class),
	i_refuel_airship(i_refuel_airship.class),
	i_register_siege_golem(i_register_siege_golem.class),
	i_remove_energy(i_remove_energy.class),
	i_remove_soul(i_remove_soul.class),
	/** + **/i_restoration(i_restoration.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_restoration.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Long.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Long.parseLong(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_restoration_random(i_restoration_random.class),
	i_resurrection(i_resurrection.class),
	i_run_away(i_run_away.class),
	i_set_skill(i_set_skill.class),
	i_set_visible(i_set_visible.class),
	/** + **/i_skill_turning(i_skill_turning.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_skill_turning.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_soul_blow(i_soul_blow.class),
	i_soul_shot(i_soul_shot.class),
	i_sowing(i_sowing.class),
	i_sp(i_sp.class),
	i_spirit_shot(i_spirit_shot.class),
	i_spoil(i_spoil.class),
	i_steal_abnormal(i_steal_abnormal.class),
	i_summon(i_summon.class),
	i_summon_agathion(i_summon_agathion.class),
	i_summon_cubic(i_summon_cubic.class),
	i_summon_npc(i_summon_npc.class),
	i_summon_pet(i_summon_pet.class),
	i_summon_soul_shot(i_summon_soul_shot.class),
	i_summon_spirit_shot(i_summon_spirit_shot.class),
	i_summon_trap(i_summon_trap.class),
	i_sweeper(i_sweeper.class),
	/** + **/i_take_adena(i_take_adena.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_take_adena.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Integer.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Integer.parseInt(template._effect_param[1]), Integer.parseInt(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_target_cancel(i_target_cancel.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_target_cancel.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/i_target_me(i_target_me.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return i_target_me.class.getConstructor(Env.class, EffectTemplate.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	i_teleport(i_teleport.class),
	i_teleport_to_target(i_teleport_to_target.class),
	i_transfer_hate(i_transfer_hate.class),
	i_uninstall_advance_base(i_uninstall_advance_base.class),
	i_unlock(i_unlock.class),
	i_unsummon_agathion(i_unsummon_agathion.class),
	i_vp_up(i_vp_up.class),
	op_blink(op_blink.class),
	op_combat(op_combat.class),
	op_skill(op_skill.class),
	op_target_pc(op_target_pc.class),
	p_2h_blunt_bonus(p_2h_blunt_bonus.class),
	p_2h_sword_bonus(p_2h_sword_bonus.class),
	p_abnormal_rate_limit(p_abnormal_rate_limit.class),
	p_abnormal_remove_by_dmg(p_abnormal_remove_by_dmg.class),
	p_abnormal_remove_by_hit(p_abnormal_remove_by_hit.class),
	p_anti_cubic(p_anti_cubic.class),
	p_area_damage(p_area_damage.class),
	p_attack_attribute(p_attack_attribute.class),
	p_attack_range(p_attack_range.class),
	p_attack_speed(p_attack_speed.class),
	p_attack_speed_by_hp1(p_attack_speed_by_hp1.class),
	p_attack_speed_by_hp2(p_attack_speed_by_hp2.class),
	p_attack_speed_by_weapon(p_attack_speed_by_weapon.class),
	/** + **/p_attack_trait(p_attack_trait.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_attack_trait.class.getConstructor(Env.class, EffectTemplate.class, SkillTrait.class, Double.class).newInstance(env, template, SkillTrait.valueOf(template._effect_param[0]), Double.parseDouble(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	p_avoid(p_avoid.class),
	/** + **/p_avoid_agro(p_avoid_agro.class),
	p_avoid_by_move_mode(p_avoid_by_move_mode.class),
	p_avoid_rate_by_hp1(p_avoid_rate_by_hp1.class),
	p_avoid_rate_by_hp2(p_avoid_rate_by_hp2.class),
	p_avoid_skill(p_avoid_skill.class),
	p_betray(p_betray.class),
	/** + **/p_block_act(p_block_act.class),
	/** + **/p_party_buff(p_party_buff.class),
	p_block_attack(p_block_attack.class),
	/** + **/p_block_buff(p_block_buff.class),
	/** + **/p_block_buff_slot(p_block_buff_slot.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_block_buff_slot.class.getConstructor(Env.class, EffectTemplate.class, SkillAbnormalType[].class).newInstance(env, template, SkillAbnormalType.getValue(template._effect_param));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	p_block_chat(p_block_chat.class),
	p_block_controll(p_block_controll.class),
	/** + **/p_block_debuff(p_block_debuff.class),
	/** + **/p_block_getdamage(p_block_getdamage.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_block_getdamage.class.getConstructor(Env.class, EffectTemplate.class, Boolean.class).newInstance(env, template, template._effect_param[0].equals("block_hp"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/p_block_move(p_block_move.class),
	p_block_party(p_block_party.class),
	p_block_resurrection(p_block_resurrection.class),
	p_block_skill_physical(p_block_skill_physical.class),
	p_block_skill_special(p_block_skill_special.class),
	p_block_spell(p_block_spell.class),
	p_breath(p_breath.class),
	/** + **/p_change_fishing_mastery(p_change_fishing_mastery.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_change_fishing_mastery.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Double.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Double.parseDouble(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	p_channel_clan(p_channel_clan.class),
	p_cheapshot(p_cheapshot.class),
	p_counter_skill(p_counter_skill.class),
	p_cp_regen(p_cp_regen.class),
	p_cp_regen_by_move_mode(p_cp_regen_by_move_mode.class),
	p_create_common_item(p_create_common_item.class),
	p_create_item(p_create_item.class),
	p_critical_damage(p_critical_damage.class),
	p_critical_damage_position(p_critical_damage_position.class),
	p_critical_rate(p_critical_rate.class),
	p_critical_rate_by_hp1(p_critical_rate_by_hp1.class),
	p_critical_rate_by_hp2(p_critical_rate_by_hp2.class),
	p_critical_rate_position_bonus(p_critical_rate_position_bonus.class),
	p_crystal_grade_modify(p_crystal_grade_modify.class),
	p_crystallize(p_crystallize.class),
	p_cubic_mastery(p_cubic_mastery.class),
	p_damage_shield(p_damage_shield.class),
	p_defence_attribute(p_defence_attribute.class),
	p_defence_critical_damage(p_defence_critical_damage.class),
	p_defence_critical_rate(p_defence_critical_rate.class),
	/** + **/p_defence_trait(p_defence_trait.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_defence_trait.class.getConstructor(Env.class, EffectTemplate.class, SkillTrait.class, Double.class).newInstance(env, template, SkillTrait.valueOf(template._effect_param[0]), Double.parseDouble(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	p_disarm(p_disarm.class),
	p_dominion_transform(p_dominion_transform.class),
	p_enlarge_abnormal_slot(p_enlarge_abnormal_slot.class),
	p_enlarge_storage(p_enlarge_storage.class),
	p_exp_modify(p_exp_modify.class),
	p_expand_deco_slot(p_expand_deco_slot.class),
	p_fatal_blow_rate(p_fatal_blow_rate.class),
	p_fear(p_fear.class),
	p_fishing_mastery(p_fishing_mastery.class),
	p_hate_attack(p_hate_attack.class),
	p_heal_effect(p_heal_effect.class),
	p_heal_effect_add(p_heal_effect_add.class),
	/** + **/p_hide(p_hide.class),
	p_hit(p_hit.class),
	p_hit_at_night(p_hit_at_night.class),
	p_hit_number(p_hit_number.class),
	p_hp_regen(p_hp_regen.class),
	p_hp_regen_by_move_mode(p_hp_regen_by_move_mode.class),
	p_limit(p_limit.class),
	p_limit_cp(p_limit_cp.class),
	p_limit_hp(p_limit_hp.class),
	p_limit_mp(p_limit_mp.class),
	p_luck(p_luck.class),
	p_magic_critical_dmg(p_magic_critical_dmg.class),
	p_magic_critical_rate(p_magic_critical_rate.class),
	p_magic_defence_critical_dmg(p_magic_defence_critical_dmg.class),
	p_magic_defence_critical_dmg_add(p_magic_defence_critical_dmg_add.class),
	p_magic_defence_critical_rate(p_magic_defence_critical_rate.class),
	p_magic_defence_critical_rate_add(p_magic_defence_critical_rate_add.class),
	p_magic_mp_cost(p_magic_mp_cost.class),
	p_magic_speed(p_magic_speed.class),
	p_magic_speed_by_weapon(p_magic_speed_by_weapon.class),
	/** + **/p_magical_attack(p_magical_attack.class),
	p_magical_attack_add(p_magical_attack_add.class),
	/** + **/p_magical_defence(p_magical_defence.class),
	p_magical_defence_add(p_magical_defence_add.class),
	p_mana_charge(p_mana_charge.class),
	p_max_cp(p_max_cp.class),
	p_max_hp(p_max_hp.class),
	p_max_mp(p_max_mp.class),
	p_max_mp_add(p_max_mp_add.class),
	p_mp_regen(p_mp_regen.class),
	p_mp_regen_add(p_mp_regen_add.class),
	p_mp_regen_by_move_mode(p_mp_regen_by_move_mode.class),
	p_mp_vampiric_attack(p_mp_vampiric_attack.class),
	/** + **/p_passive(p_passive.class),
	p_physical_armor_hit(p_physical_armor_hit.class),
	p_physical_attack(p_physical_attack.class),
	p_physical_attack_by_hp1(p_physical_attack_by_hp1.class),
	p_physical_attack_by_hp2(p_physical_attack_by_hp2.class),
	p_physical_attack_by_material(p_physical_attack_by_material.class),
	p_physical_defence(p_physical_defence.class),
	p_physical_defence_by_hp1(p_physical_defence_by_hp1.class),
	p_physical_defence_by_hp2(p_physical_defence_by_hp2.class),
	p_physical_defence_by_material(p_physical_defence_by_material.class),
	p_physical_polarm_target_single(p_physical_polarm_target_single.class),
	p_physical_shield_defence(p_physical_shield_defence.class),
	p_physical_shield_defence_angle_all(p_physical_shield_defence_angle_all.class),
	p_pk_protect(p_pk_protect.class),
	p_preserve_abnormal(p_preserve_abnormal.class),
	p_pvp_bonus(p_pvp_bonus.class),
	p_pvp_magical_skill_defence_bonus(p_pvp_magical_skill_defence_bonus.class),
	p_pvp_magical_skill_dmg_bonus(p_pvp_magical_skill_dmg_bonus.class),
	p_pvp_physical_attack_defence_bonus(p_pvp_physical_attack_defence_bonus.class),
	p_pvp_physical_attack_dmg_bonus(p_pvp_physical_attack_dmg_bonus.class),
	p_pvp_physical_skill_defence_bonus(p_pvp_physical_skill_defence_bonus.class),
	p_pvp_physical_skill_dmg_bonus(p_pvp_physical_skill_dmg_bonus.class),
	p_recovery_vp(p_recovery_vp.class),
	p_reduce_cancel(p_reduce_cancel.class),
	p_reduce_drop_penalty(p_reduce_drop_penalty.class),
	p_reflect_dd(p_reflect_dd.class),
	p_reflect_skill(p_reflect_skill.class),
	p_remove_equip_penalty(p_remove_equip_penalty.class),
	p_resist_abnormal_by_category(p_resist_abnormal_by_category.class),
	p_resist_dd_magic(p_resist_dd_magic.class),
	p_resist_dispel_by_category(p_resist_dispel_by_category.class),
	/** + **/p_resurrection_special(p_resurrection_special.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_resurrection_special.class.getConstructor(Env.class, EffectTemplate.class, Integer.class, Integer.class).newInstance(env, template, Integer.parseInt(template._effect_param[0]), Integer.parseInt(template._effect_param[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	p_reuse_delay(p_reuse_delay.class),
	p_safe_fall_height(p_safe_fall_height.class),
	p_set_cloak_slot(p_set_cloak_slot.class),
	p_set_collected(p_set_collected.class),
	p_shield_defence_rate(p_shield_defence_rate.class),
	p_skill_critical(p_skill_critical.class),
	p_skill_critical_probability(p_skill_critical_probability.class),
	p_skill_power(p_skill_power.class),
	p_soul_eating(p_soul_eating.class),
	p_sp_modify(p_sp_modify.class),
	p_speed(p_speed.class),
	p_stat_up(p_stat_up.class),
	/** + **/p_target_me(p_target_me.class),
	p_trade(p_trade.class),
	p_transfer_damage_pc(p_transfer_damage_pc.class),
	p_transfer_damage_summon(p_transfer_damage_summon.class),
	/** + **/p_transfer_stats(p_transfer_stats.class),
	p_transform(p_transform.class),
	p_transform_hangover(p_transform_hangover.class),
	p_trigger_skill_by_attack(p_trigger_skill_by_attack.class),
	p_trigger_skill_by_avoid(p_trigger_skill_by_avoid.class),
	p_trigger_skill_by_dmg(p_trigger_skill_by_dmg.class),
	p_trigger_skill_by_skill(p_trigger_skill_by_skill.class),
	p_vampiric_attack(p_vampiric_attack.class),
	p_violet_boy(p_violet_boy.class),
	p_weight_limit(p_weight_limit.class),
	p_weight_penalty(p_weight_penalty.class),
	/** + **/t_hp(t_hp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return t_hp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class, FuncPTS.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]), FuncPTS.valueOf(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/t_hp_fatal(t_hp_fatal.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return t_hp_fatal.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class, FuncPTS.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]), FuncPTS.valueOf(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/t_mp(t_mp.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return t_mp.class.getConstructor(Env.class, EffectTemplate.class, Double.class, Integer.class, FuncPTS.class).newInstance(env, template, Double.parseDouble(template._effect_param[0]), Integer.parseInt(template._effect_param[1]), FuncPTS.valueOf(template._effect_param[2]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	/** + **/p_ignore_skill(p_ignore_skill.class)
	{
		@Override
		public final L2Effect makeEffect(Env env, EffectTemplate template)
		{
			try
			{
				return p_ignore_skill.class.getConstructor(Env.class, EffectTemplate.class).newInstance(env, template);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	},
	// --------------------------------------------------------------
	i_summon_npc_near(i_summon_npc_near.class),
	i_bonuscount_up(i_bonuscount_up.class),
	p_bonustimelimit_up(p_bonustimelimit_up.class),
	i_show_reuse_delay(i_show_reuse_delay.class),
	p_item_chance(p_item_chance.class),
	p_stat_up_limited(p_stat_up_limited.class),
	p_bonustim1elimit_up(p_bonustim1elimit_up.class),
	i_agathion_energy(i_agathion_energy.class),
	c_agathion_energy(c_agathion_energy.class),
	i_reset_quest(i_reset_quest.class),
	p_play_music(p_play_music.class),
	// ---- IT ----
	i_p_attack_over_hit(i_p_attack_over_hit.class),
	i_target_me_chance(i_target_me_chance.class),
	i_target_cancel_chance(i_target_cancel_chance.class),
	i_mana_burn(i_mana_burn.class),
	
	;

	private final Class<? extends L2Effect> clazz;
	private final boolean isRaidImmune;
	private final boolean isPTS;

	private EffectType(Class<? extends L2Effect> clazz, boolean isRaidImmune)
	{
		this.clazz = clazz;
		this.isRaidImmune = isRaidImmune;
		isPTS = false;
	}

	private EffectType(Class<? extends L2Effect> clazz)
	{
		this.clazz = clazz;
		isRaidImmune = false;
		isPTS = true;
	}

	public boolean isRaidImmune()
	{
		return isRaidImmune;
	}

	public L2Effect makeEffect(Env env, EffectTemplate template)
	{
		try
		{
			Constructor<? extends L2Effect> c = isPTS ? clazz.getConstructor(Env.class, EffectTemplate.class) : clazz.getConstructor(Env.class, EffectTemplate.class);
			return c.newInstance(env, template);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}