package l2open.gameserver.tables;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.L2Augmentation;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.triggers.*;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.OptionDataTemplate;
import l2open.gameserver.xml.loader.XmlOptionDataLoader;
import l2open.util.GArray;
import l2open.util.Rnd;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import gnu.trove.TIntObjectHashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Соответствует r3616 версии SF, с некоторыми отличиями.
 */
public class AugmentationData
{
	private static final Logger _log = Logger.getLogger(AugmentationData.class.getName());

	private static AugmentationData _Instance;

	public static AugmentationData getInstance()
	{
		if(_Instance == null)
			_Instance = new AugmentationData();
		return _Instance;
	}

	// stats
	private static final int STAT_BLOCKSIZE = 3640;
	private static final int STAT_SUBBLOCKSIZE = 91;

	// skills
	private static final int BLUE_START = 14561;
	private static final int SKILLS_BLOCKSIZE = 178;

	// basestats
	private static final int BASESTAT_STR = 16341;
	private static final int BASESTAT_CON = 16342;
	private static final int BASESTAT_INT = 16343;
	private static final int BASESTAT_MEN = 16344;

	// accessory
	private static final int ACC_START = 16669;
	private static final int ACC_BLOCKS_NUM = 10;
	private static final int ACC_STAT_SUBBLOCKSIZE = 21;

	private static final int ACC_RING_START = ACC_START;
	private static final int ACC_RING_SKILLS = 18;
	private static final int ACC_RING_BLOCKSIZE = ACC_RING_SKILLS + 4 * ACC_STAT_SUBBLOCKSIZE;
	private static final int ACC_RING_END = ACC_RING_START + ACC_BLOCKS_NUM * ACC_RING_BLOCKSIZE - 1;

	private static final int ACC_EAR_START = ACC_RING_END + 1;
	private static final int ACC_EAR_SKILLS = 18;
	private static final int ACC_EAR_BLOCKSIZE = ACC_EAR_SKILLS + 4 * ACC_STAT_SUBBLOCKSIZE;
	private static final int ACC_EAR_END = ACC_EAR_START + ACC_BLOCKS_NUM * ACC_EAR_BLOCKSIZE - 1;

	private static final int ACC_NECK_START = ACC_EAR_END + 1;
	private static final int ACC_NECK_SKILLS = 24;
	private static final int ACC_NECK_BLOCKSIZE = ACC_NECK_SKILLS + 4 * ACC_STAT_SUBBLOCKSIZE;

	private List<?>[] _blueSkills = new ArrayList[10];
	private List<?>[] _purpleSkills = new ArrayList[10];
	private List<?>[] _redSkills = new ArrayList[10];

	public AugmentationData()
	{
		_log.info("Initializing AugmentationData.");

		for(int i = 0; i < 10; i++)
		{
			_blueSkills[i] = new ArrayList<Integer>();
			_purpleSkills[i] = new ArrayList<Integer>();
			_redSkills[i] = new ArrayList<Integer>();
		}

		load();

		// Use size*4: since theres 4 blocks of stat-data with equivalent size
		/*for(int i = 0; i < 10; i++)
			_log.info("AugmentationData: Loaded: " + _blueSkills[i].size() + " blue, " + _purpleSkills[i].size() + " purple and " + _redSkills[i].size() + " red skills for lifeStoneLevel " + i);*/
	}

	@SuppressWarnings("unchecked")
	private final void load()
	{
		// Load the skillmap
		// Note: the skillmap data is only used when generating new augmentations
		// the client expects a different id in order to display the skill in the
		// items description...
		try
		{
			int badAugmantData = 0;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(ConfigValue.DatapackRoot + "/data/stats/augmentation/augmentation_skillmap.xml");

			Document doc = factory.newDocumentBuilder().parse(file);
			for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				if("list".equalsIgnoreCase(n.getNodeName()))
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						if("augmentation".equalsIgnoreCase(d.getNodeName()))
						{
							NamedNodeMap attrs = d.getAttributes();
							int skillId = 0, augmentationId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							int skillLvL = 0;
							String type = "blue";

							TriggerType t = null;
							double chance = 0;
							for(Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								attrs = cd.getAttributes();
								if("skillId".equalsIgnoreCase(cd.getNodeName()))
								{
									skillId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								}
								else if("skillLevel".equalsIgnoreCase(cd.getNodeName()))
								{
									skillLvL = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
								}
								else if("type".equalsIgnoreCase(cd.getNodeName()))
								{
									type = attrs.getNamedItem("val").getNodeValue();
								}
								else if("trigger_type".equalsIgnoreCase(cd.getNodeName()))
								{
									t = TriggerType.valueOf(attrs.getNamedItem("val").getNodeValue());
								}
								else if("trigger_chance".equalsIgnoreCase(cd.getNodeName()))
								{
									chance = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
								}
							}

							if(skillId == 0)
							{
								badAugmantData++;
								continue;
							}
							else if(skillLvL == 0)
							{
								badAugmantData++;
								continue;
							}

							int k = (augmentationId - BLUE_START) / SKILLS_BLOCKSIZE;
							if(type.equalsIgnoreCase("blue"))
								((List<Integer>) _blueSkills[k]).add(augmentationId);
							else if(type.equalsIgnoreCase("purple"))
								((List<Integer>) _purpleSkills[k]).add(augmentationId);
							else if(type.equalsIgnoreCase("red"))
								((List<Integer>) _redSkills[k]).add(augmentationId);
						}

			if(badAugmantData != 0)
				_log.info("AugmentationData: " + badAugmantData + " bad skill(s) were skipped.");
		}
		catch(Exception e)
		{
			_log.log(Level.SEVERE, "Error parsing augmentation_skillmap.xml.", e);
			return;
		}
	}

	public L2Augmentation generateRandomAugmentation(int lifeStoneLevel, int lifeStoneGrade, int bodyPart)
	{
		switch(bodyPart)
		{
			case L2Item.SLOT_L_FINGER:
			case L2Item.SLOT_R_FINGER:
			case L2Item.SLOT_L_FINGER | L2Item.SLOT_R_FINGER:
			case L2Item.SLOT_L_EAR:
			case L2Item.SLOT_R_EAR:
			case L2Item.SLOT_L_EAR | L2Item.SLOT_R_EAR:
			case L2Item.SLOT_NECK:
				return generateRandomAccessoryAugmentation(lifeStoneLevel, bodyPart);
			default:
				return generateRandomWeaponAugmentation(lifeStoneLevel, lifeStoneGrade);
		}
	}

	private L2Augmentation generateRandomWeaponAugmentation(int lifeStoneLevel, int lifeStoneGrade)
	{
		// Note that stat12 stands for stat 1 AND 2 (same for stat34 ;p )
		// this is because a value can contain up to 2 stat modifications
		// (there are two short values packed in one integer value, meaning 4 stat modifications at max)
		// for more info take a look at getAugStatsById(...)

		// Note: lifeStoneGrade: (0 means low grade, 3 top grade)
		// First: determine whether we will add a skill/baseStatModifier or not
		// because this determine which color could be the result 
		int stat12 = 0;
		int stat34 = 0;
		boolean generateSkill = false;
		boolean generateGlow = false;

		//lifestonelevel is used for stat Id and skill level, but here the max level is 9
		lifeStoneLevel = Math.min(lifeStoneLevel, 9);

		switch(lifeStoneGrade)
		{
			case 0:
				generateSkill = Rnd.chance(ConfigValue.AugmentationNGSkillChance);
				generateGlow = Rnd.chance(ConfigValue.AugmentationNGGlowChance);
				break;
			case 1:
				generateSkill = Rnd.chance(ConfigValue.AugmentationMidSkillChance);
				generateGlow = Rnd.chance(ConfigValue.AugmentationMidGlowChance);
				break;
			case 2:
				generateSkill = Rnd.chance(ConfigValue.AugmentationHighSkillChance);
				generateGlow = Rnd.chance(ConfigValue.AugmentationHighGlowChance);
				break;
			case 3:
				generateSkill = Rnd.chance(ConfigValue.AugmentationTopSkillChance);
				generateGlow = Rnd.chance(ConfigValue.AugmentationTopGlowChance);
				break;
		}

		if(!generateSkill && Rnd.get(1, 100) <= ConfigValue.AugmentationBaseStatChance)
			stat34 = Rnd.get(BASESTAT_STR, BASESTAT_MEN);

		// Second: decide which grade the augmentation result is going to have:
		// 0:yellow, 1:blue, 2:purple, 3:red
		// The chances used here are most likely custom,
		// whats known is: you cant have yellow with skill(or baseStatModifier)
		// noGrade stone can not have glow, mid only with skill, high has a chance(custom), top allways glow
		int resultColor = Rnd.get(0, 100);
		if(stat34 == 0 && !generateSkill)
			if(resultColor <= 15 * lifeStoneGrade + 40)
				resultColor = 1;
			else
				resultColor = 0;
		else if(resultColor <= 10 * lifeStoneGrade + 5 || stat34 != 0)
			resultColor = 3;
		else if(resultColor <= 10 * lifeStoneGrade + 10)
			resultColor = 1;
		else
			resultColor = 2;

		// generate a skill if neccessary
		L2Skill skill = null;
		if(generateSkill)
		{
			switch(resultColor)
			{
				case 1: // blue skill
					stat34 = ((Integer) _blueSkills[lifeStoneLevel].get(Rnd.get(0, _blueSkills[lifeStoneLevel].size() - 1)));
					break;
				case 2: // purple skill
					stat34 = ((Integer) _purpleSkills[lifeStoneLevel].get(Rnd.get(0, _purpleSkills[lifeStoneLevel].size() - 1)));
					break;
				case 3: // red skill
					stat34 = ((Integer) _redSkills[lifeStoneLevel].get(Rnd.get(0, _redSkills[lifeStoneLevel].size() - 1)));
					break;
			}
			OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(stat34);
			if(template != null)
			{
				if(template.getSkills().size() > 0)
					skill = template.getSkills().get(0);
				if(skill == null && template.getTriggerList().size() > 0)
					skill = template.getTriggerList().get(0).getSkill();
			}
		}

		// Third: Calculate the subblock offset for the choosen color,
		// and the level of the lifeStone
		// from large number of retail augmentations:
		// no skill part
		// Id for stat12:
		// A:1-910 B:911-1820 C:1821-2730 D:2731-3640 E:3641-4550 F:4551-5460 G:5461-6370 H:6371-7280
		// Id for stat34(this defines the color):
		// I:7281-8190(yellow) K:8191-9100(blue) L:10921-11830(yellow) M:11831-12740(blue)
		// you can combine I-K with A-D and L-M with E-H
		// using C-D or G-H Id you will get a glow effect
		// there seems no correlation in which grade use which Id except for the glowing restriction
		// skill part
		// Id for stat12:
		// same for no skill part
		// A same as E, B same as F, C same as G, D same as H
		// A - no glow, no grade LS
		// B - weak glow, mid grade LS?
		// C - glow, high grade LS?
		// D - strong glow, top grade LS?

		// is neither a skill nor basestat used for stat34? then generate a normal stat
		int offset;
		if(stat34 == 0)
		{
			int temp = Rnd.get(2, 3);
			int colorOffset = resultColor * 10 * STAT_SUBBLOCKSIZE + temp * STAT_BLOCKSIZE + 1;
			offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + colorOffset;

			stat34 = Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);
			if(generateGlow && lifeStoneGrade >= 2)
				offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + (temp - 2) * STAT_BLOCKSIZE + lifeStoneGrade * 10 * STAT_SUBBLOCKSIZE + 1;
			else
				offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + (temp - 2) * STAT_BLOCKSIZE + Rnd.get(0, 1) * 10 * STAT_SUBBLOCKSIZE + 1;
		}
		else if(!generateGlow)
			offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + Rnd.get(0, 1) * STAT_BLOCKSIZE + 1;
		else
			offset = lifeStoneLevel * STAT_SUBBLOCKSIZE + Rnd.get(0, 1) * STAT_BLOCKSIZE + (lifeStoneGrade + resultColor) / 2 * 10 * STAT_SUBBLOCKSIZE + 1;

		// 16341-16380
		// 21423-21426
		// 23061-23064
		// 24903-24948
		// 24977-24980
		if(ConfigValue.AugmentationStatChance > 0 && Rnd.get(1, 100) <= ConfigValue.AugmentationStatChance)
		{
			int ch1=16341;
			int ch2=16380;
			switch(Rnd.get(5))
			{
				case 0:
					ch1=16341;
					ch2=16380;
					break;
				case 1:
					ch1=21423;
					ch2=21426;
					break;
				case 2:
					ch1=23061;
					ch2=23064;
					break;
				default:
					ch1=16341;
					ch2=16380;
					break;
				/*case 3:
					ch1=24903;
					ch2=24948;
					break;
				case 4:
					ch1=24977;
					ch2=24980;
					break;*/
			}
			stat12 = Rnd.get(ch1, ch2);
		}
		else
			stat12 = Rnd.get(offset, offset + STAT_SUBBLOCKSIZE - 1);

		return new L2Augmentation(((stat34 << 16) + stat12), skill);
	}

	private L2Augmentation generateRandomAccessoryAugmentation(int lifeStoneLevel, int bodyPart)
	{
		int stat12 = 0;
		int stat34 = 0;
		int base = 0;
		int skillsLength = 0;

		lifeStoneLevel = Math.min(lifeStoneLevel, 9);

		switch(bodyPart)
		{
			case L2Item.SLOT_L_FINGER:
			case L2Item.SLOT_R_FINGER:
			case L2Item.SLOT_L_FINGER | L2Item.SLOT_R_FINGER:
				base = ACC_RING_START + ACC_RING_BLOCKSIZE * lifeStoneLevel;
				skillsLength = ACC_RING_SKILLS;
				break;
			case L2Item.SLOT_L_EAR:
			case L2Item.SLOT_R_EAR:
			case L2Item.SLOT_L_EAR | L2Item.SLOT_R_EAR:
				base = ACC_EAR_START + ACC_EAR_BLOCKSIZE * lifeStoneLevel;
				skillsLength = ACC_EAR_SKILLS;
				break;
			case L2Item.SLOT_NECK:
				base = ACC_NECK_START + ACC_NECK_BLOCKSIZE * lifeStoneLevel;
				skillsLength = ACC_NECK_SKILLS;
				break;
			default:
				return null;
		}

		int resultColor = Rnd.get(0, 3);
		L2Skill skill = null;

		// first augmentation (stats only)
		stat12 = Rnd.get(ACC_STAT_SUBBLOCKSIZE);

		if(Rnd.get(1, 100) <= ConfigValue.AugmentationAccSkillChance)
		{
			// second augmentation (skill)
			stat34 = base + Rnd.get(skillsLength);
			
			
			OptionDataTemplate template = XmlOptionDataLoader.getInstance().getTemplate(stat34);
			if(template != null)
			{
				if(template.getSkills().size() > 0)
					skill = template.getSkills().get(0);
				if(skill == null && template.getTriggerList().size() > 0)
					skill = template.getTriggerList().get(0).getSkill();
			}
		}

		if(skill == null)
		{
			// second augmentation (stats)
			// calculating any different from stat12 value inside sub-block
			// starting from next and wrapping over using remainder
			stat34 = (stat12 + 1 + Rnd.get(ACC_STAT_SUBBLOCKSIZE - 1)) % ACC_STAT_SUBBLOCKSIZE;
			// this is a stats - skipping skills
			stat34 = base + skillsLength + ACC_STAT_SUBBLOCKSIZE * resultColor + stat34;
		}

		// stat12 has stats only
		stat12 = base + skillsLength + ACC_STAT_SUBBLOCKSIZE * resultColor + stat12;

		return new L2Augmentation(((stat34 << 16) + stat12), skill);
	}
}