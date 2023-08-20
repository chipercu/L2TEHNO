package l2open.gameserver.skills;

import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.skills.conditions.Condition;
import l2open.gameserver.tables.EnchantTable;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public final class DocumentSkill extends DocumentBase
{
	public class Skill
	{
		public int id;
		public String name;
		public StatsSet[] sets;
		public int currentLevel;
		public GArray<L2Skill> skills = new GArray<L2Skill>();
		public ArrayList<L2Skill> currentSkills = new ArrayList<L2Skill>();
	}

	private Skill currentSkill;
	private HashSet<String> usedTables = new HashSet<String>();
	private List<L2Skill> skillsInFile = new LinkedList<L2Skill>();

	DocumentSkill(File file)
	{
		super(file);
	}

	@Override
	protected void resetTable()
	{
		/*if(!usedTables.isEmpty())
			for(String table : tables.keySet())
				if(!usedTables.contains(table))
					_log.warning("WARNING: Unused table " + table + " for skill " + currentSkill.id);*/
		usedTables.clear();
		super.resetTable();
	}

	private void setCurrentSkill(Skill skill)
	{
		currentSkill = skill;
	}

	protected List<L2Skill> getSkills()
	{
		return skillsInFile;
	}

	@Override
	protected Object getTableValue(String name)
	{
		try
		{
			usedTables.add(name);
			Object[] a = tables.get(name);
			if(a.length - 1 >= currentSkill.currentLevel)
				return a[currentSkill.currentLevel];
			return a[a.length - 1];
		}
		catch(RuntimeException e)
		{
			_log.log(Level.SEVERE, "error in table " + name + " of skill Id " + currentSkill.id, e);
			return 0;
		}
	}

	@Override
	protected Object getTableValue(String name, int idx)
	{
		idx--;
		try
		{
			usedTables.add(name);
			Object[] a = tables.get(name);
			if(a.length - 1 >= idx)
				return a[idx];
			return a[a.length - 1];
		}
		catch(RuntimeException e)
		{
			_log.log(Level.SEVERE, "wrong level count in skill Id " + currentSkill.id + " table " + name + " level " + idx, e);
			return 0;
		}
	}

	@Override
	protected void parseDocument(Document doc)
	{
		for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			if("list".equalsIgnoreCase(n.getNodeName()))
			{
				loop : for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					if("skill".equalsIgnoreCase(d.getNodeName()))
					{
						if(SkillsEngine.getInstance().getCustomSkills().contains(Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue())))
							continue loop;
						setCurrentSkill(new Skill());
						parseSkill(d);
						skillsInFile.addAll(currentSkill.skills);
						resetTable();
					}
			}
			else if("skill".equalsIgnoreCase(n.getNodeName()))
			{
				setCurrentSkill(new Skill());
				parseSkill(n);
				skillsInFile.addAll(currentSkill.skills);
			}
	}

	public static final byte[] elevels30 = { 0, 76, 76, 76, 77, 77, 77, 78, 78, 78, 79, 79, 79, 80, 80, 80, 81, 81, 81,
			82, 82, 82, 83, 83, 83, 84, 84, 84, 85, 85, 85 };

	public static final byte[] elevels15 = { 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5 };

	protected void parseSkill(Node n)
	{
		NamedNodeMap attrs = n.getAttributes();
		int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
		String skillName = attrs.getNamedItem("name").getNodeValue();
		String levels = attrs.getNamedItem("levels").getNodeValue();
		int lastLvl = Integer.parseInt(levels);

		try
		{
			HashMap<Short, Short> displayLevels = new HashMap<Short, Short>();

			// перебираем энчанты
			Node enchant = null;
			HashMap<String, Object[]> etables = new HashMap<String, Object[]>();
			int count = 0, eLevels = 0;
			Node d = n.cloneNode(true);
			for(int k = 0; k < d.getChildNodes().getLength(); k++)
			{
				enchant = d.getChildNodes().item(k);
				if(!enchant.getNodeName().startsWith("enchant"))
					continue;
				if(eLevels == 0)
					if(enchant.getAttributes().getNamedItem("levels") != null)
						eLevels = Integer.parseInt(enchant.getAttributes().getNamedItem("levels").getNodeValue());
					else
						eLevels = 30;
				String ename = enchant.getAttributes().getNamedItem("name").getNodeValue();
				for(int r = 1; r <= eLevels; r++)
				{
					short level = (short) (lastLvl + eLevels * count + r);
					L2EnchantSkillLearn e = new L2EnchantSkillLearn(skillId, 100 * (count + 1) + r, skillName, "+" + r + " " + ename, (r == 1 ? lastLvl : 100 * (count + 1) + r - 1), lastLvl, eLevels);

					GArray<L2EnchantSkillLearn> t = EnchantTable._enchant.get(skillId);
					if(t == null)
						t = new GArray<L2EnchantSkillLearn>();
					t.add(e);
					EnchantTable._enchant.put(skillId, t);
					displayLevels.put(level, (short) ((count + 1) * 100 + r));
				}
				count++;
				Node first = enchant.getFirstChild();
				Node curr = null;
				for(curr = first; curr != null; curr = curr.getNextSibling())
					if("table".equalsIgnoreCase(curr.getNodeName()))
					{
						NamedNodeMap a = curr.getAttributes();
						String name = a.getNamedItem("name").getNodeValue();
						Object[] table = parseTable(curr);
						table = fillTableToSize(table, eLevels);
						Object[] fulltable = etables.get(name);
						if(fulltable == null)
							fulltable = new Object[lastLvl + eLevels * 8 + 1];
						System.arraycopy(table, 0, fulltable, lastLvl + (count - 1) * eLevels, eLevels);
						etables.put(name, fulltable);
					}
			}
			lastLvl += eLevels * count;

			currentSkill.id = skillId;
			currentSkill.name = skillName;
			currentSkill.sets = new StatsSet[lastLvl];

			for(int i = 0; i < lastLvl; i++)
			{
				currentSkill.sets[i] = new StatsSet();
				currentSkill.sets[i].set("skill_id", currentSkill.id);
				currentSkill.sets[i].set("level", i + 1);
				currentSkill.sets[i].set("name", currentSkill.name);
			}

			if(currentSkill.sets.length != lastLvl)
				throw new RuntimeException("Skill id=" + skillId + " number of levels missmatch, " + lastLvl + " levels expected");

			Node first = n.getFirstChild();
			for(n = first; n != null; n = n.getNextSibling())
				if("table".equalsIgnoreCase(n.getNodeName()))
					parseTable(n);

			// обрабатываем таблицы сливая их с энчантами
			for(String tn : tables.keySet())
			{
				Object[] et = etables.get(tn);
				if(et != null)
				{
					Object[] t = tables.get(tn);
					Object max = t[t.length - 1];
					System.arraycopy(t, 0, et, 0, t.length);
					for(int j = 0; j < et.length; j++)
						if(et[j] == null)
							et[j] = max;
					tables.put(tn, et);
				}
			}

			for(int i = 1; i <= lastLvl; i++)
				for(n = first; n != null; n = n.getNextSibling())
					if("set".equalsIgnoreCase(n.getNodeName()))
					{
						parseBeanSet(n, currentSkill.sets[i - 1], i);
						
						String name2 = n.getAttributes().getNamedItem("name").getNodeValue().trim();
						String value2 = n.getAttributes().getNamedItem("val").getNodeValue().trim();
						if(i==1)
						{
							if(name2.equals("skillType") && value2.equals("NOTDONE"))
								Log.add("type="+value2+"	skill_id="+skillId, "skills_not_done", "");
							else if(name2.equals("skillType") && value2.equals("NOTUSED"))
								Log.add("type="+value2+"	skill_id="+skillId, "skills_not_used", "");
						}
					}

			makeSkills();
			for(int i = 0; i < lastLvl; i++)
			{
				currentSkill.currentLevel = i;
				L2Skill current = currentSkill.currentSkills.get(i);
				if(displayLevels.get(current.getLevel()) != null)
					current.setDisplayLevel(displayLevels.get(current.getLevel()).shortValue());
				current.setEnchantLevelCount(eLevels);
				//System.out.println(current.getLevel() + ":" + displayLevels.get(current.getLevel()));
				for(n = first; n != null; n = n.getNextSibling())
				{
					if("cond".equalsIgnoreCase(n.getNodeName()))
					{
						Condition condition = parseCondition(n.getFirstChild());
						if(condition != null)
						{
							Node msgAttribute = n.getAttributes().getNamedItem("msgId");
							if(msgAttribute != null)
							{
								int msgId = parseNumber(msgAttribute.getNodeValue()).intValue();
								condition.setSystemMsg(msgId);
							}
							current.attach(condition);
						}
					}
					if("for".equalsIgnoreCase(n.getNodeName()))
						parseTemplate(n, current);
					if ("triggers".equalsIgnoreCase(n.getNodeName())) 
						parseTrigger(n, current); 
				}
			}
			currentSkill.skills.addAll(currentSkill.currentSkills);
		}
		catch(Exception e)
		{
			_log.severe("Error loading skill " + skillId);
			e.printStackTrace();
		}
	}

	private Object[] fillTableToSize(Object[] table, int size)
	{
		if(table.length < size)
		{
			Object[] ret = new Object[size];
			System.arraycopy(table, 0, ret, 0, table.length);
			table = ret;
		}
		for(int j = 1; j < size; j++)
			if(table[j] == null)
				table[j] = table[j - 1];
		return table;
	}

	private void makeSkills()
	{
		currentSkill.currentSkills = new ArrayList<L2Skill>(currentSkill.sets.length);
		//System.out.println(sets.length);
		for(int i = 0; i < currentSkill.sets.length; i++)
			currentSkill.currentSkills.add(i, currentSkill.sets[i].getEnum("skillType", SkillType.class).makeSkill(currentSkill.sets[i]));
	}
}