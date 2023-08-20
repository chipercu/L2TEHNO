package l2open.gameserver.model.instances;

import javolution.util.FastMap;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill.AddedSkill;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;

import java.util.Map;

public class L2FeedableBeastInstance extends L2MonsterInstance
{
	private static FastMap<Integer,GrowthCapableMob> _GrowthCapableMobs = new FastMap<Integer,GrowthCapableMob>().setShared(true);
	private static FastMap<String, AddedSkill[]> _TamedBeastsData = new FastMap<String, AddedSkill[]>().setShared(true);
	private static FastMap<Integer,Integer> _FeedInfo = new FastMap<Integer,Integer>().setShared(true);

	private static final int GOLDEN_SPICE = 15474;
	private static final int CRYSTAL_SPICE = 15475;
	private static final int SKILL_GOLDEN_SPICE = 9049;
	private static final int SKILL_CRYSTAL_SPICE = 9050;
	private static final int SKILL_BLESSED_GOLDEN_SPICE = 9051;
	private static final int SKILL_BLESSED_CRYSTAL_SPICE = 9052;
	private static final int SKILL_SGRADE_GOLDEN_SPICE = 9053;
	private static final int SKILL_SGRADE_CRYSTAL_SPICE = 9054;
	private static final int[] TAMED_BEASTS = { 18869, 18870, 18871, 18872, 16017, 16018, 16013, 16014, 16015, 16016 };
	private static final int TAME_CHANCE = 15;
	private static final int[] SPECIAL_SPICE_CHANCES = { 33, 75 };

	// all mobs that can eat...
	private static final int[] FEEDABLE_BEASTS = {
		18873, 18874, 18875, 18876, 18877, 18878, 18879, 18880, 18881, 18882,
		18883, 18884, 18885, 18886, 18887, 18888, 18889, 18890, 18891, 18892,
		18893, 18894, 18895, 18896, 18897, 18898, 18899, 18900, 21451, 21456,
		21452, 21457, 21453, 21459, 21458, 21454, 21455, 21466, 21464, 21462,
		21460, 21467, 21465, 21463, 21461, 21470, 21475, 21471, 21476, 21472,
		21478, 21477, 21473, 21474, 21485, 21483, 21481, 21479, 21486, 21484,
		21482, 21480, 21489, 21494, 21490, 21495, 21491, 21497, 21496, 21492,
		21493, 21504, 21502, 21500, 21498, 21505, 21503, 21501, 21499
	};

	// all mobs that grow by eating
	private static class GrowthCapableMob
	{
		private int _chance;
		private int _growthLevel;
		private int _tameNpcId;
		private Map<Integer,Integer> _skillSuccessNpcIdList = new FastMap<Integer,Integer>();

		public GrowthCapableMob(int chance, int growthLevel, int tameNpcId)
		{
			_chance = chance;
			_growthLevel = growthLevel;
			_tameNpcId = tameNpcId;
		}

		public void addNpcIdForSkillId(int skillId, int npcId)
		{
			_skillSuccessNpcIdList.put(skillId, npcId);
		}

		public int getGrowthLevel()
		{
			return _growthLevel;
		}

		public int getLeveledNpcId(int skillId)
		{
			if (!_skillSuccessNpcIdList.containsKey(skillId))
				return -1;
			else if (skillId == SKILL_BLESSED_GOLDEN_SPICE || skillId == SKILL_BLESSED_CRYSTAL_SPICE || skillId == SKILL_SGRADE_GOLDEN_SPICE || skillId == SKILL_SGRADE_CRYSTAL_SPICE)
			{
				if (Rnd.get(100) < SPECIAL_SPICE_CHANCES[0])
				{
					if (Rnd.get(100) < SPECIAL_SPICE_CHANCES[1])
						return _skillSuccessNpcIdList.get(skillId);
					else if (skillId == SKILL_BLESSED_GOLDEN_SPICE || skillId == SKILL_SGRADE_GOLDEN_SPICE)
						return _skillSuccessNpcIdList.get(SKILL_GOLDEN_SPICE);
					else
						return _skillSuccessNpcIdList.get(SKILL_CRYSTAL_SPICE);
				}
				else
					return -1;
			}
			else if (_growthLevel == 2 && Rnd.get(100) < TAME_CHANCE)
				return _tameNpcId;
			else if (Rnd.get(100) < _chance)
				return _skillSuccessNpcIdList.get(skillId);
			else
				return -1;
		}
	}

	public L2FeedableBeastInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);

		GrowthCapableMob temp;
		// Kookabura old #1
		temp = new GrowthCapableMob(100, 0, 16017);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21456);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21452);
		_GrowthCapableMobs.put(21451, temp);
		//1
		temp = new GrowthCapableMob(40, 0, 16017);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21457);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21457);
		_GrowthCapableMobs.put(21456, temp);
		temp = new GrowthCapableMob(40, 0, 16018);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21453);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21453);
		_GrowthCapableMobs.put(21452, temp);
		//2
		temp = new GrowthCapableMob(40, 0, 16017);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21459);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21458);
		_GrowthCapableMobs.put(21457, temp);
		temp = new GrowthCapableMob(40, 0, 16018);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21454);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21455);
		_GrowthCapableMobs.put(21453, temp);
		//3.1
		temp = new GrowthCapableMob(30, 0, 16017);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21466);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21467);
		_GrowthCapableMobs.put(21459, temp);
		temp = new GrowthCapableMob(30, 0, 16018);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21464);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21465);
		_GrowthCapableMobs.put(21458, temp);
		//3.2
		temp = new GrowthCapableMob(30, 0, 16017);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21462);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21463);
		_GrowthCapableMobs.put(21454, temp);
		temp = new GrowthCapableMob(30, 0, 16018);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21460);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21461);
		_GrowthCapableMobs.put(21455, temp);
		//4
		temp = new GrowthCapableMob(20, 0, 16017);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21824);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21825);
		_GrowthCapableMobs.put(21466, temp);
		_GrowthCapableMobs.put(21464, temp);
		_GrowthCapableMobs.put(21462, temp);
		_GrowthCapableMobs.put(21460, temp);
		temp = new GrowthCapableMob(20, 0, 16018);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21468);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21469);
		_GrowthCapableMobs.put(21467, temp);
		_GrowthCapableMobs.put(21465, temp);
		_GrowthCapableMobs.put(21463, temp);
		_GrowthCapableMobs.put(21461, temp);

		
		// Buffalo old #2
		temp = new GrowthCapableMob(100, 0, 16013);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21475);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21471);
		_GrowthCapableMobs.put(21470, temp);
		//1
		temp = new GrowthCapableMob(40, 0, 16013);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21476);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21476);
		_GrowthCapableMobs.put(21475, temp);
		temp = new GrowthCapableMob(40, 0, 16014);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21472);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21472);
		_GrowthCapableMobs.put(21471, temp);
		//2
		temp = new GrowthCapableMob(40, 0, 16013);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21478);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21477);
		_GrowthCapableMobs.put(21476, temp);
		temp = new GrowthCapableMob(40, 0, 16014);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21473);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21474);
		_GrowthCapableMobs.put(21472, temp);
		//3.1
		temp = new GrowthCapableMob(30, 0, 16013);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21485);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21486);
		_GrowthCapableMobs.put(21478, temp);
		temp = new GrowthCapableMob(30, 0, 16014);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21483);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21484);
		_GrowthCapableMobs.put(21477, temp);
		//3.2
		temp = new GrowthCapableMob(30, 0, 16013);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21481);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21482);
		_GrowthCapableMobs.put(21473, temp);
		temp = new GrowthCapableMob(30, 0, 16014);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21479);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21480);
		_GrowthCapableMobs.put(21474, temp);
		//4
		temp = new GrowthCapableMob(20, 0, 16013);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21826);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21827);
		_GrowthCapableMobs.put(21485, temp);
		_GrowthCapableMobs.put(21483, temp);
		_GrowthCapableMobs.put(21481, temp);
		_GrowthCapableMobs.put(21479, temp);
		temp = new GrowthCapableMob(20, 0, 16014);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21487);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21488);
		_GrowthCapableMobs.put(21486, temp);
		_GrowthCapableMobs.put(21484, temp);
		_GrowthCapableMobs.put(21482, temp);
		_GrowthCapableMobs.put(21480, temp);

		// Cougar old #3
		temp = new GrowthCapableMob(100, 0, 16015);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21494);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21490);
		_GrowthCapableMobs.put(21489, temp);
		//1
		temp = new GrowthCapableMob(40, 0, 16015);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21495);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21495);
		_GrowthCapableMobs.put(21494, temp);
		temp = new GrowthCapableMob(40, 0, 16016);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21491);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21491);
		_GrowthCapableMobs.put(21490, temp);
		//2
		temp = new GrowthCapableMob(40, 0, 16015);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21497);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21496);
		_GrowthCapableMobs.put(21495, temp);
		temp = new GrowthCapableMob(40, 0, 16016);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21492);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21493);
		_GrowthCapableMobs.put(21491, temp);
		//3.1
		temp = new GrowthCapableMob(30, 0, 16015);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21504);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21505);
		_GrowthCapableMobs.put(21497, temp);
		temp = new GrowthCapableMob(30, 0, 16016);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21502);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21503);
		_GrowthCapableMobs.put(21496, temp);
		//3.2
		temp = new GrowthCapableMob(30, 0, 16015);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21500);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21501);
		_GrowthCapableMobs.put(21492, temp);
		temp = new GrowthCapableMob(30, 0, 16016);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21498);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21499);
		_GrowthCapableMobs.put(21493, temp);
		//4
		temp = new GrowthCapableMob(20, 0, 16015);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21828);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21829);
		_GrowthCapableMobs.put(21504, temp);
		_GrowthCapableMobs.put(21502, temp);
		_GrowthCapableMobs.put(21500, temp);
		_GrowthCapableMobs.put(21498, temp);
		temp = new GrowthCapableMob(20, 0, 16016);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 21506);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 21507);
		_GrowthCapableMobs.put(21505, temp);
		_GrowthCapableMobs.put(21503, temp);
		_GrowthCapableMobs.put(21501, temp);
		_GrowthCapableMobs.put(21499, temp);

		
		
		
		
		// Kookabura
		temp = new GrowthCapableMob(100, 0, 18869);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18874);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18875);
		temp.addNpcIdForSkillId(SKILL_BLESSED_GOLDEN_SPICE, 18869);
		temp.addNpcIdForSkillId(SKILL_BLESSED_CRYSTAL_SPICE, 18869);
		temp.addNpcIdForSkillId(SKILL_SGRADE_GOLDEN_SPICE, 18878);
		temp.addNpcIdForSkillId(SKILL_SGRADE_CRYSTAL_SPICE, 18879);
		_GrowthCapableMobs.put(18873, temp);
		
		temp = new GrowthCapableMob(40, 1, 18869);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18876);
		_GrowthCapableMobs.put(18874, temp);
		
		temp = new GrowthCapableMob(40, 1, 18869);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18877);
		_GrowthCapableMobs.put(18875, temp);
		
		temp = new GrowthCapableMob(25, 2, 18869);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18878);
		_GrowthCapableMobs.put(18876, temp);
		
		temp = new GrowthCapableMob(25, 2, 18869);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18879);
		_GrowthCapableMobs.put(18877, temp);

		// Cougar
		temp = new GrowthCapableMob(100, 0, 18870);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18881);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18882);
		temp.addNpcIdForSkillId(SKILL_BLESSED_GOLDEN_SPICE, 18870);
		temp.addNpcIdForSkillId(SKILL_BLESSED_CRYSTAL_SPICE, 18870);
		temp.addNpcIdForSkillId(SKILL_SGRADE_GOLDEN_SPICE, 18885);
		temp.addNpcIdForSkillId(SKILL_SGRADE_CRYSTAL_SPICE, 18886);
		_GrowthCapableMobs.put(18880, temp);
		
		temp = new GrowthCapableMob(40, 1, 18870);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18883);
		_GrowthCapableMobs.put(18881, temp);
		
		temp = new GrowthCapableMob(40, 1, 18870);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18884);
		_GrowthCapableMobs.put(18882, temp);
		
		temp = new GrowthCapableMob(25, 2, 18870);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18885);
		_GrowthCapableMobs.put(18883, temp);
		
		temp = new GrowthCapableMob(25, 2, 18870);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18886);
		_GrowthCapableMobs.put(18884, temp);
		
		// Buffalo
		temp = new GrowthCapableMob(100, 0, 18871);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18888);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18889);
		temp.addNpcIdForSkillId(SKILL_BLESSED_GOLDEN_SPICE, 18871);
		temp.addNpcIdForSkillId(SKILL_BLESSED_CRYSTAL_SPICE, 18871);
		temp.addNpcIdForSkillId(SKILL_SGRADE_GOLDEN_SPICE, 18892);
		temp.addNpcIdForSkillId(SKILL_SGRADE_CRYSTAL_SPICE, 18893);
		_GrowthCapableMobs.put(18887, temp);
		
		temp = new GrowthCapableMob(40, 1, 18871);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18890);
		_GrowthCapableMobs.put(18888, temp);
		
		temp = new GrowthCapableMob(40, 1, 18871);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18891);
		_GrowthCapableMobs.put(18889, temp);
		
		temp = new GrowthCapableMob(25, 2, 18871);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18892);
		_GrowthCapableMobs.put(18890, temp);
		
		temp = new GrowthCapableMob(25, 2, 18871);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18893);
		_GrowthCapableMobs.put(18891, temp);
		
		// Grendel
		temp = new GrowthCapableMob(100, 0, 18872);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18895);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18896);
		temp.addNpcIdForSkillId(SKILL_BLESSED_GOLDEN_SPICE, 18872);
		temp.addNpcIdForSkillId(SKILL_BLESSED_CRYSTAL_SPICE, 18872);
		temp.addNpcIdForSkillId(SKILL_SGRADE_GOLDEN_SPICE, 18899);
		temp.addNpcIdForSkillId(SKILL_SGRADE_CRYSTAL_SPICE, 18900);
		_GrowthCapableMobs.put(18894, temp);
		
		temp = new GrowthCapableMob(40, 1, 18872);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18897);
		_GrowthCapableMobs.put(18895, temp);
		
		temp = new GrowthCapableMob(40, 1, 18872);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18898);
		_GrowthCapableMobs.put(18896, temp);
		
		temp = new GrowthCapableMob(25, 2, 18872);
		temp.addNpcIdForSkillId(SKILL_GOLDEN_SPICE, 18899);
		_GrowthCapableMobs.put(18897, temp);
		
		temp = new GrowthCapableMob(25, 2, 18872);
		temp.addNpcIdForSkillId(SKILL_CRYSTAL_SPICE, 18900);
		_GrowthCapableMobs.put(18898, temp);
		
		// Tamed beasts data
		AddedSkill[] stemp = new AddedSkill[2];
		stemp[0] = new AddedSkill(6432,1);
		stemp[1] = new AddedSkill(6668,1);
		_TamedBeastsData.put("%name% of Focus", stemp);
		
		stemp = new AddedSkill[2];
		stemp[0] = new AddedSkill(6433,1);
		stemp[1] = new AddedSkill(6670,1);
		_TamedBeastsData.put("%name% of Guiding", stemp);
		
		stemp = new AddedSkill[2];
		stemp[0] = new AddedSkill(6434,1);
		stemp[1] = new AddedSkill(6667,1);
		_TamedBeastsData.put("%name% of Swifth", stemp);
		
		stemp = new AddedSkill[1];
		stemp[0] = new AddedSkill(6671,1);
		_TamedBeastsData.put("Berserker %name%", stemp);
		
		stemp = new AddedSkill[2];
		stemp[0] = new AddedSkill(6669,1);
		stemp[1] = new AddedSkill(6672,1);
		_TamedBeastsData.put("%name% of Protect", stemp);
		
		stemp = new AddedSkill[2];
		stemp[0] = new AddedSkill(6431,1);
		stemp[1] = new AddedSkill(6666,1);
		_TamedBeastsData.put("%name% of Vigor", stemp);
	}


	public void spawnNext(L2NpcInstance npc, L2Player player, int nextNpcId, int food)
	{
		// remove the feedinfo of the mob that got despawned, if any
		if (_FeedInfo.containsKey(npc.getObjectId()))
		{
			if (_FeedInfo.get(npc.getObjectId()) == player.getObjectId())
				_FeedInfo.remove(npc.getObjectId());
		}
		npc.decayMe();
		npc.doDie(this); //Временная затычка.

		// if this is finally a trained mob, then despawn any other trained mobs that the
		// player might have and initialize the Tamed Beast.
		if (Util.contains_int(TAMED_BEASTS, nextNpcId))
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(nextNpcId);
			L2TamedBeastInstance nextNpc = new L2TamedBeastInstance(IdFactory.getInstance().getNextId(), template, player, food, npc.getLoc());

			String name = _TamedBeastsData.keySet().toArray(new String[_TamedBeastsData.keySet().size()])[Rnd.get(_TamedBeastsData.size())];
			AddedSkill[] skillList = _TamedBeastsData.get(name);
			switch(nextNpcId)
			{
				case 18869:
					name = name.replace("%name%", "Alpine Kookaburra");
					break;
				case 18870:
					name = name.replace("%name%", "Alpine Cougar");
					break;
				case 18871:
					name = name.replace("%name%", "Alpine Buffalo");
					break;
				case 18872:
					name = name.replace("%name%", "Alpine Grendel");
					break;
			}
			nextNpc.setName(name);
			nextNpc.broadcastPacket(new NpcInfo(nextNpc, player));
			for(AddedSkill sh : skillList)
				nextNpc.addBeastSkill(sh.getSkill());

			QuestState st = player.getQuestState("_020_BringUpWithLove");
			if (st != null && st.getInt("cond") == 1 && st.getQuestItemsCount(15533) == 0)
			{
				if(Rnd.get(10) == 1)
				{
					//if player has quest 20 going, give quest item
					//it's easier to hardcode it in here than to try and repeat this stuff in the quest
					st.giveItems(15533,1);
					st.set("cond","2");
				}
			}
		}
		else
		{
			// if not trained, the newly spawned mob will automatically be agro against its feeder
			// (what happened to "never bite the hand that feeds you" anyway?!)
			L2Character nextNpc = spawn(nextNpcId, npc.getX(), npc.getY(), npc.getZ());

			// register the player in the feedinfo for the mob that just spawned
			_FeedInfo.put(nextNpc.getObjectId(),player.getObjectId());
			nextNpc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 99999);
			nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);

			StatusUpdate su = new StatusUpdate(nextNpc.getObjectId());
			su.addAttribute(StatusUpdate.CUR_HP, (int) nextNpc.getCurrentHp());
			su.addAttribute(StatusUpdate.p_max_hp, nextNpc.getMaxHp());
			player.sendPacket(su);
			player.setTarget(nextNpc);
		}
	}

	@Override
	public void doDie(L2Character killer)
	{
		if(_FeedInfo != null)
		{
			if(_FeedInfo.containsKey(getObjectId()))
			{
				_FeedInfo.remove(getObjectId());
			}
		}
		super.doDie(killer);
	}

	public L2MonsterInstance spawn(int npcId, int x, int y, int z)
	{
		try
		{
			L2MonsterInstance monster = (L2MonsterInstance) NpcTable.getTemplate(npcId).getInstanceConstructor().newInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(npcId));
			monster.setSpawnedLoc(new Location(x, y, z));
			monster.onSpawn();
			monster.spawnMe(monster.getSpawnedLoc());
			return monster;
		}
		catch(Exception e)
		{
			System.out.println("Could not spawn Npc " + npcId);
			e.printStackTrace();
		}
		return null;
	}

	public void onSkillUse(L2NpcInstance npc, L2Player player, int skill_id)
	{
		// gather some values on local variables
		int npcId = getNpcId();
		// check if the npc and skills used are valid
		if(!Util.contains_int(FEEDABLE_BEASTS,npcId))
			return;
		if(skill_id !=SKILL_GOLDEN_SPICE && skill_id != SKILL_CRYSTAL_SPICE && skill_id !=SKILL_BLESSED_GOLDEN_SPICE && skill_id != SKILL_BLESSED_CRYSTAL_SPICE && skill_id !=SKILL_SGRADE_GOLDEN_SPICE && skill_id != SKILL_SGRADE_CRYSTAL_SPICE)
			return;

		int objectId = getObjectId();
		int growthLevel = 3;

		if (_GrowthCapableMobs.containsKey(npcId))
		{
			growthLevel = _GrowthCapableMobs.get(npcId).getGrowthLevel();
		}

		if (growthLevel == 0 && _FeedInfo.containsKey(objectId))
		{
			return;
		}
		else
		{
			_FeedInfo.put(objectId,player.getObjectId());
		}

		broadcastPacket2(new SocialAction(objectId, 2));

		int food = 0;
		if (skill_id == SKILL_GOLDEN_SPICE || skill_id == SKILL_BLESSED_GOLDEN_SPICE || skill_id == SKILL_SGRADE_GOLDEN_SPICE)
		{
			food = GOLDEN_SPICE;
		}
		else if (skill_id == SKILL_CRYSTAL_SPICE || skill_id == SKILL_BLESSED_CRYSTAL_SPICE || skill_id == SKILL_SGRADE_CRYSTAL_SPICE)
		{
			food = CRYSTAL_SPICE;
		}


		// if this pet can't grow, it's all done.
		if (_GrowthCapableMobs.containsKey(npcId))
		{
			// do nothing if this mob doesn't eat the specified food (food gets consumed but has no effect).
			int newNpcId = _GrowthCapableMobs.get(npcId).getLeveledNpcId(skill_id);
			if (newNpcId == -1)
			{
				if (growthLevel == 0)
				{
					_FeedInfo.remove(objectId);
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 99999);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
				return;
			}
			else if (growthLevel > 0 && _FeedInfo.get(objectId) != player.getObjectId())
			{
				return;
			}
			spawnNext(npc,player,newNpcId,food);
		}
		else
		{
			if(food != 0)
				npc.dropItem(player, food, 1);
		}
		return;
	}
}