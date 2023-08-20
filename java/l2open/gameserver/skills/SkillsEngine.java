package l2open.gameserver.skills;

import l2open.gameserver.model.L2Skill;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SkillsEngine
{

	protected static Logger _log = Logger.getLogger(SkillsEngine.class.getName());

	private static SkillsEngine _instance;

	private List<File> _skillFiles;
	private List<File> _skillCustomFiles;
	private List<Integer> customSkill;

	public static SkillsEngine getInstance()
	{
		return _instance;
	}

	public static void reload()
	{
		_instance = new SkillsEngine();
	}

	private SkillsEngine()
	{
		_skillFiles = new LinkedList<File>();
		_skillCustomFiles = new LinkedList<File>();
		customSkill = new ArrayList<Integer>(0);

		if (Boolean.parseBoolean(System.getenv("DEVELOP"))){
			hashFiles("data/stats/skills", _skillFiles);
			hashFiles("data/stats/custom_skills", _skillCustomFiles);
		}else {
			hashFiles("data/stats/skills", _skillFiles);
			hashFiles("data/stats/custom_skills", _skillCustomFiles);
		}
		new File("log/game/skills_not_done.txt").delete();
		new File("log/game/skills_not_used.txt").delete();
	}

	private void hashFiles(String dirname, List<File> hash)
	{
		File dir = new File(dirname);
		if(!dir.exists())
		{
			_log.info("Dir " + dir.getAbsolutePath() + " not exists");
			return;
		}
		File[] files = dir.listFiles();
		for(File f : files)
			if(f.getName().endsWith(".xml"))
				hash.add(f);
	}

	public List<L2Skill> loadSkills(File file)
	{
		if(file == null)
		{
			_log.info("SkillsEngine: File not found!");
			return null;
		}
		DocumentSkill doc = new DocumentSkill(file);
		doc.parse();
		return doc.getSkills();
	}

	public L2Skill[][] loadAllSkills(int _maxid, int _maxLevel)
	{
		int skillIdIdx, skillLvlIdx, total = 0, total_custom = 0, maxLevel = 0;
		L2Skill[][] result = new L2Skill[_maxid][];

		// Сначала грузим кастомные скилы, потом только остальные...
		for(File fileCustom : _skillCustomFiles)
		{
			List<L2Skill> s = loadSkills(fileCustom);
			if(s != null)
			{
				for(L2Skill skill : s)
				{
					customSkill.add(skill.getId());
					skillIdIdx = skill.getId() - 1;
					skillLvlIdx = skill.getLevel() - 1;

					if(result[skillIdIdx] == null)
						result[skillIdIdx] = new L2Skill[_maxLevel];
					if(result[skillIdIdx][skillLvlIdx] != null)
						new KeyAlreadyExistsException("Unable to store skill " + skill).printStackTrace();
					else
						result[skillIdIdx][skillLvlIdx] = skill;
					maxLevel = Math.max(maxLevel, skill.getLevel());
					total_custom++;
				}
			}
		}

		// загружаем скилы
		for(File file : _skillFiles)
		{
			List<L2Skill> s = loadSkills(file);
			if(s == null)
				continue;
			for(L2Skill skill : s)
			{
				skillIdIdx = skill.getId() - 1;
				skillLvlIdx = skill.getLevel() - 1;

				if(result[skillIdIdx] == null)
					result[skillIdIdx] = new L2Skill[_maxLevel];
				if(result[skillIdIdx][skillLvlIdx] != null)
					new KeyAlreadyExistsException("Unable to store skill " + skill).printStackTrace();
				else
					result[skillIdIdx][skillLvlIdx] = skill;
				maxLevel = Math.max(maxLevel, skill.getLevel());
				total++;
			}
		}

		int topindex = result.length - 1;
		// если надо ресайзим саму таблицу result
		if(result[topindex] == null)
		{
			do
				topindex--;
			while(topindex > 0 && result[topindex] == null);

			L2Skill[][] tmp = result;
			result = new L2Skill[topindex + 1][];
			System.arraycopy(tmp, 0, result, 0, result.length);
			tmp = null;
		}

		// если надо ресайзим отдельные субтаблицы result[]
		for(int i = 0; i < result.length; i++)
		{
			if(result[i] == null)
				continue;
			topindex = result[i].length - 1;
			if(result[i][topindex] == null)
			{
				do
					topindex--;
				while(topindex > 0 && result[i][topindex] == null);
				L2Skill[] tmp = result[i];
				result[i] = new L2Skill[topindex + 1];
				System.arraycopy(tmp, 0, result[i], 0, result[i].length);
				tmp = null;
			}
		}

		_skillFiles.clear();
		_skillCustomFiles.clear();
		customSkill.clear();
		_log.warning("SkillsEngine: Loaded " + total + " skill templates from XML files. Max id: " + result.length + ", max level: " + maxLevel);
		_log.warning("SkillsEngine: Loaded " + total_custom + " custom skill templates from XML files.");
		return result;
	}

	public List<Integer> getCustomSkills()
	{
		return customSkill;
	}
}