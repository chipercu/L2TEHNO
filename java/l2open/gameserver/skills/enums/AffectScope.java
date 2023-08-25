package l2open.gameserver.skills.enums;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.util.Location;
import l2open.util.Util;
import l2open.util.Rnd;

import java.util.*;

// TODO: сделать лимит таргета 10, для игроков если affect_limit_s == 1
public enum AffectScope
{
	range_sort_by_hp //+
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			List<L2Character> healTargets = new ArrayList<L2Character>();
			for(L2Character act : L2World.getAroundCharacters(target, skill.getAffectRange(), 256))
			{
				if(act == null || act.getObjectId() == caster.getObjectId() && target.getObjectId() != caster.getObjectId() || act.isInvisible() || act.isHealBlocked(true, false) && !(target instanceof SeducedInvestigatorInstance) || act.isDead() || act.isCursedWeaponEquipped()/* || act.getPvpFlag() != 0 || act.getKarma() > 0*/ || act.isAutoAttackable(caster) || caster.getPlayer() != null && act.getPlayer() != null && (caster.getPlayer().atWarWith(act.getPlayer()) || act.getPlayer().atWarWith(caster.getPlayer())) || act.block_hp.get())
					continue;
				healTargets.add(act);
			}
			if(healTargets.isEmpty())
			{
				if(!target.isAutoAttackable(caster) && !caster.isHealBlocked(true, false) && !caster.isCursedWeaponEquipped() && !caster.block_hp.get())
					healTargets.add(target);
				return healTargets;
			}

			Collections.sort(healTargets, comparator);

			List<L2Character> _result = new ArrayList<L2Character>();

			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			int npc_count = 0;
			boolean add_target = false;

			if(!target.isHealBlocked(true, false) && !target.isDead() && !target.isCursedWeaponEquipped() && !target.block_hp.get() && !target.isAutoAttackable(caster))
			{
				add_target = true;
				npc_count++;
			}

			for(L2Character act : healTargets)
			{
				_result.add(act);
				npc_count++;
				if(npc_count >= target_count)
					break;
			}

			if(add_target)
				_result.add(target);

			return _result;
		}
	},
	dead_union //+ Возвращаем всех дохлых членов СС(возможно али+клан в том числе) в радиусе @getAffectRange()
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = null;
			for(L2Player act : L2World.getAroundPlayers(target, skill.getAffectRange(), 256))
				if(act != null && act.isDead() && caster.getParty() != null && target.getParty() != null && caster.getParty().getCommandChannel() != null && caster.getParty().getCommandChannel() == target.getParty().getCommandChannel())
				//if(act != null && act.isDead() && act.getClanId() != 0 && act.getAllyId() == caster.getAllyId())
				{
					if(_result == null)
						_result = new ArrayList<L2Character>(2);
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	balakas_scope //+ вроди бы тоже самое, что и range...
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			for(L2Character act : L2World.getAroundCharacters(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && skill.affect_object.validate(caster, act))
				{
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	dead_pledge //+ Возвращаем всех дохлых сокланов в радиусе @getAffectRange()
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = null;
			for(L2Player act : L2World.getAroundPlayers(target, skill.getAffectRange(), 256))
				if(act != null && act.isDead() && act.getClanId() != 0 && act.getClanId() == caster.getClanId())
				{
					if(_result == null)
						_result = new ArrayList<L2Character>(2);
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	fan //+ fan_range = {0;0;600;60} 1 и 2 - не известно, 3 - радиус действия, 4 - угол действия, 180 - все что перед чаром, 360 - вокруг чара.
	// affect_scope = fan	affect_object = not_friend	fan_range = {0;0;300;160}
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			// 
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			if(target != null && !target.isDead() && skill.affect_object.validate(caster, target))
				_result.add(target);
			for(L2Character act : L2World.getAroundCharacters(target, skill.fan_range_h, 256))
				if(act != null && !act.isDead() && Util.isFacing(caster, target, skill.fan_range_l) && skill.affect_object.validate(caster, act))
				{	
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	none //? выдает цель, на которую прошел крит...
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			List<L2Character> _result = null;
			if(!target.isDead())
			{
				_result = new ArrayList<L2Character>(1);
				_result.add(target);
			}
			return _result;
		}
	},
	party //+ Берет в таргет членов пати в радиусе @getAffectRange()
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			List<L2Character> _result = null;
			if(caster.getPlayer() != null && caster.getPlayer().getParty() != null)
			{
				_result = new ArrayList<L2Character>(2);
				for(L2Player act : caster.getPlayer().getParty().getPartyMembers())
					if(act != null && !act.isDead() && target.getDistance(act) <= skill.getAffectRange()) // Проверить на ПТСке, влияет ли координата Z(getDistance3D)...
						_result.add(act);
			}
			else if(target != null && !target.isDead())
			{
				if(_result == null)
					_result = new ArrayList<L2Character>(1);
				_result.add(target);
			}
			return _result;
		}
	},
	party_pledge //+ Берет в таргет членов пати/клана в радиусе @getAffectRange()
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			List<L2Character> _result = new ArrayList<L2Character>(2);
			if(target != null && !target.isDead())
				_result.add(target);
			for(L2Player act : L2World.getAroundPlayers(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && (act.getClanId() != 0 && act.getClanId() == caster.getClanId() || caster.getParty() != null && act.getParty() == caster.getParty()))	
					_result.add(act);
			return _result;
		}
	},
	pledge //+ TODO: проверить на 7007, берёт клан того кто в таргете или того кто кастует...
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			List<L2Character> _result = new ArrayList<L2Character>(2);
			if(target != null && !target.isDead())
				_result.add(target);
			for(L2Player act : L2World.getAroundPlayers(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && act.getClanId() != 0 && act.getClanId() == caster.getClanId())
					_result.add(act);
			return _result;
		}
	},
	point_blank //+ Возвращает список целей в радиусе @getAffectRange() таргета, за его исключением
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			for(L2Character act : L2World.getAroundCharacters(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && skill.affect_object.validate(caster, act))
				{	
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	range //+ Возвращает список целей в радиусе @getAffectRange() таргета
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			if(target != null && !target.isDead() && skill.affect_object.validate(caster, target))
				_result.add(target);
			for(L2Character act : L2World.getAroundCharacters(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && skill.affect_object.validate(caster, act))
				{	
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	ring_range //+
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			if(target != null && !target.isDead() && skill.affect_object.validate(caster, target))
				_result.add(target);
			for(L2Character act : L2World.getAroundCharacters(target, skill.fan_range_h, 256))
				if(act != null && !act.isDead() && skill.affect_object.validate(caster, act))
				{	
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	single //+ Возвращаем только таргет.
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			if(target != null && !target.isDead() && skill.affect_object.validate(caster, target))
			{
				List<L2Character> _result = new ArrayList<L2Character>(1);
				_result.add(target);
				return _result;
			}
			return null;
		}
	},
	square //+
	{
		/**
		* target_type = enemy	affect_scope = square	affect_object = not_friend	fan_range = {0;0;300;100}
		* square - прямоуголник, шириной 100 и длиной ДО цели в 300.
		**/
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			L2Territory terr = new L2Territory(0);

			int radius = Math.max(skill.fan_range_h, skill.fan_range_l)+50;

			int zmin1 = caster.getPrevZ() - 50;
			int zmax1 = caster.getPrevZ() + 50;
			int zmin2 = target.getZ() - 50;
			int zmax2 = target.getZ() + 50;

			double angle = Location.calculateAngleFrom(caster.getPrevX(), caster.getPrevY(), target.getX(), target.getY());

			double radian1 = Math.toRadians(angle-90);
			double radian2 = Math.toRadians(angle+90);

			int dx = target.getX() - caster.getPrevX();
			int dy = target.getY() - caster.getPrevY();

			int c_x = (int) (caster.getX() - Math.sin(radian1) * skill.fan_range_h);
			int c_y = (int) (caster.getY() + Math.cos(radian1) * skill.fan_range_h);

			int width = skill.fan_range_l/2; // _radius

			// fan_range_l - ширина
			// fan_range_h - дистанция до цели
			terr.add(c_x + (int) (Math.cos(radian1) * width), c_y + (int) (Math.sin(radian1) * width), zmin1, zmax1);
			terr.add(c_x + (int) (Math.cos(radian2) * width), c_y + (int) (Math.sin(radian2) * width), zmin1, zmax1);

			terr.add(caster.getX() + (int) (Math.cos(radian2) * width), caster.getY() + (int) (Math.sin(radian2) * width), zmin2, zmax2);
			terr.add(caster.getX() + (int) (Math.cos(radian1) * width), caster.getY() + (int) (Math.sin(radian1) * width), zmin2, zmax2);

			if(caster.isPlayer() && ((L2Player) caster).isGM())
			{
				caster.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, false));
				caster.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, true));
			}

			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			int npc_count=unlim ? Integer.MIN_VALUE : 0;
			int player_count = skill.affect_limit_s == 0 ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			for(L2Character act : L2World.getAroundCharacters(target, radius, 256))
			{
				if(act != null && skill.affect_object.validate(caster, act) && terr.isInside(act))
				{
					if(!act.isPlayable())
					{
						if(target_count <= npc_count)
							continue;
						npc_count++;
					}
					else
					{
						if(10 <= player_count)
							continue;
						player_count++;
					}
					_result.add(act);
				}
			}
			return null;
		}
	},
	square_pb //+ прямоугольная область, работает от кастера, до дистанции N
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			L2Territory terr = new L2Territory(0);
			int radius = Math.max(skill.fan_range_h, skill.fan_range_l)+50;
			int width = skill.fan_range_l/2; // _radius

			int zmin1 = caster.getZ() - 50;
			int zmax1 = caster.getZ() + 50;

			double angle = Util.convertHeadingToDegree(caster.getHeading());
				
			angle += skill.fan_range_s;
			if(angle >= 360)
				angle -= 360;
				
			double radian1 = Math.toRadians(angle-90);
			double radian2 = Math.toRadians(angle+90);

			int c_x = (int) (caster.getX() - Math.sin(radian1) * skill.fan_range_h);
			int c_y = (int) (caster.getY() + Math.cos(radian1) * skill.fan_range_h);

			// fan_range_l - ширина
			// fan_range_h - дистанция до цели
			terr.add(c_x + (int) (Math.cos(radian1) * width), c_y + (int) (Math.sin(radian1) * width), zmin1, zmax1); // getPointInRadius(c_x_c_y, width, angle-90)
			terr.add(c_x + (int) (Math.cos(radian2) * width), c_y + (int) (Math.sin(radian2) * width), zmin1, zmax1); // getPointInRadius(c_x_c_y, width, angle+90)

			terr.add(caster.getX() + (int) (Math.cos(radian2) * width), caster.getY() + (int) (Math.sin(radian2) * width), zmin1, zmax1); // getPointInRadius(caster.getLoc(), width, angle+90)
			terr.add(caster.getX() + (int) (Math.cos(radian1) * width), caster.getY() + (int) (Math.sin(radian1) * width), zmin1, zmax1); // getPointInRadius(caster.getLoc(), width, angle-90)

			if(caster.isPlayer() && ((L2Player) caster).isGM())
			{
				caster.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, false));
				caster.sendPacket(Functions.Points2Trace(terr.getCoords(), 50, true, true));
			}

			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			int npc_count=unlim ? Integer.MIN_VALUE : 0;
			int player_count = skill.affect_limit_s == 0 ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			for(L2Character act : L2World.getAroundCharacters(target, radius, 256))
			{
				if(act != null && skill.affect_object.validate(caster, act) && terr.isInside(act))
				{
					if(!act.isPlayable())
					{
						if(target_count <= npc_count)
							continue;
						npc_count++;
					}
					else
					{
						if(10 <= player_count)
							continue;
						player_count++;
					}
					_result.add(act);
				}
			}
			return null;
		}
	},
	static_object_scope //+ 2353/7022 - для обнаруженяи скрытых объектов
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			for(L2Character act : L2World.getAroundCharacters(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && act.isDoor() && skill.affect_object.validate(caster, act))
				{	
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	},
	wyvern_scope // +
	{
		@Override
		public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
		{
			int target_count = Rnd.get(skill.affect_limit_p, skill.affect_limit_p+skill.affect_limit_n+1);
			boolean unlim = skill.affect_limit_p == 0 && (skill.affect_limit_n == 0 || Rnd.chance(50));
			int npc_count=unlim ? Integer.MIN_VALUE : 0;

			List<L2Character> _result = new ArrayList<L2Character>(2);
			if(target != null && !target.isDead() && skill.affect_object.validate(caster, target))
				_result.add(target);
			for(L2Character act : L2World.getAroundCharacters(target, skill.getAffectRange(), 256))
				if(act != null && !act.isDead() && skill.affect_object.validate(caster, act))
				{	
					if(!act.isPlayer())
						npc_count++;
					_result.add(act);
					if(target_count <= npc_count) // Достигли лимита таргетов...
						break;
				}
			return _result;
		}
	};

	public List<L2Character> getTargetList(L2Character caster, L2Character target, L2Skill skill)
	{
		return null;
	}

	private static HealTargetComparator comparator = new HealTargetComparator();
	private static class HealTargetComparator implements Comparator<L2Character>
	{
		@Override
		public int compare(L2Character o1, L2Character o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			if(getHpPer(o1) < getHpPer(o2))
				return -1;
			if(getHpPer(o1) > getHpPer(o2))
				return 1;
			return 0;
		}

		private double getHpPer(L2Character target)
		{
			return target.getCurrentHp() / target.getMaxHp();
		}
	}
}