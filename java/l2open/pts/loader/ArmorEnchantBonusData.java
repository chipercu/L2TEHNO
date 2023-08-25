package l2open.pts.loader;

import java.util.*;

/**
 * Запиздовал Diagod...
 * open-team.ru
 **/
public class ArmorEnchantBonusData extends LoadData
{
	public ArmorEnchantBonusData()
	{
		loadData();
	}
	public static int[][] bonus_grade;
	public static float onepiece_factor;

	// Загружает имена и ид категорий...
	public static void loadData()
	{
		long _time1 = System.currentTimeMillis();
		List<String> _pch = loadFile("./data/pts/armorenchantbonusdata.txt");
		if(_pch == null)
		{
			_log.warning("Not found: './data/pts/armorenchantbonusdata.txt'");
			return;
		}
		StringTokenizer st;

		for(String line : _pch)
		{
			st = new StringTokenizer(line, "=");
			String name = st.nextToken().trim();
			String value = st.nextToken().trim();
			if(name.equals("onepiece_factor"))
				onepiece_factor = Integer.parseInt(value)/100f;
			else if(name.equals("bonus_grade_none"))
			{
				String[] arg = getTextWith(value).split(";");
				if(bonus_grade == null)
					bonus_grade = new int[6][arg.length];
				for(int i=0;i<arg.length;i++)
					bonus_grade[0][i] = Integer.parseInt(arg[i]);
			}
			else if(name.equals("bonus_grade_d"))
			{
				String[] arg = getTextWith(value).split(";");
				if(bonus_grade == null)
					bonus_grade = new int[6][arg.length];
				for(int i=0;i<arg.length;i++)
					bonus_grade[1][i] = Integer.parseInt(arg[i]);
			}
			else if(name.equals("bonus_grade_c"))
			{
				String[] arg = getTextWith(value).split(";");
				if(bonus_grade == null)
					bonus_grade = new int[6][arg.length];
				for(int i=0;i<arg.length;i++)
					bonus_grade[2][i] = Integer.parseInt(arg[i]);
			}
			else if(name.equals("bonus_grade_b"))
			{
				String[] arg = getTextWith(value).split(";");
				if(bonus_grade == null)
					bonus_grade = new int[6][arg.length];
				for(int i=0;i<arg.length;i++)
					bonus_grade[3][i] = Integer.parseInt(arg[i]);
			}
			else if(name.equals("bonus_grade_a"))
			{
				String[] arg = getTextWith(value).split(";");
				if(bonus_grade == null)
					bonus_grade = new int[6][arg.length];
				for(int i=0;i<arg.length;i++)
					bonus_grade[4][i] = Integer.parseInt(arg[i]);
			}
			else if(name.equals("bonus_grade_s"))
			{
				String[] arg = getTextWith(value).split(";");
				if(bonus_grade == null)
					bonus_grade = new int[6][arg.length];
				for(int i=0;i<arg.length;i++)
					bonus_grade[5][i] = Integer.parseInt(arg[i]);
			}
		}
		st = null;
		_pch.clear();
		_pch = null;
		long _time2 = System.currentTimeMillis() - _time1;
		_log.info("PTS ArmorEnchantBonusData: Finish loading data fo "+_time2+" ms...");
	}
}
