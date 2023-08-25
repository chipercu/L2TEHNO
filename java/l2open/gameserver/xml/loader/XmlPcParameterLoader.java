package l2open.gameserver.xml.loader;

import l2open.config.ConfigValue;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2EtcItem.EtcItemType;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.XmlUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class XmlPcParameterLoader 
{
    private static XmlPcParameterLoader ourInstance = new XmlPcParameterLoader();
    private Logger _log = Logger.getLogger(XmlPcParameterLoader.class.getName());

    private ConcurrentHashMap<Integer, StatParam> class_param = new ConcurrentHashMap<Integer, StatParam>();

    public static XmlPcParameterLoader getInstance() 
	{
        return ourInstance;
    }

    private XmlPcParameterLoader() 
	{
		load();
    }

    private void load() 
	{
		class_param.clear();

		if(ConfigValue.EnablePtsPlayerStat)
		{
			for(File f : new File(ConfigValue.DatapackRoot + "/data/stats/pc_parameter").listFiles())
				parseFile(f);

			_log.info("XmlPcParameterLoader: Loaded " + class_param.size() + " pc_parameters");
		}
    }

	public void parseFile(File f)
	{
		try 
		{
			if(f.getName().endsWith(".xml")) 
			{
				Document doc = XmlUtils.readFile(f);
				Element list = doc.getRootElement();
				for(Element class_data : list.elements("class_data")) 
				{
					int id = XmlUtils.getSafeInt(class_data, "id", 0);
					String name = class_data.attributeValue("name");

					StatParam sp = new StatParam();

					for(Element level_data : class_data.elements("level_data"))
					{
						int lvl = XmlUtils.getSafeInt(level_data, "lvl", 0);

						sp.base_hp[lvl-1] = XmlUtils.getSafeDouble(level_data, "hp", 1);
						sp.base_mp[lvl-1] = XmlUtils.getSafeDouble(level_data, "mp", 1);
						sp.base_cp[lvl-1] = XmlUtils.getSafeDouble(level_data, "cp", 1);
					}

					class_param.put(id, sp);
				}
			}
		} 
		catch (DocumentException e) 
		{
			e.printStackTrace();
		}
	}

	public double getMaxHp(int class_id, int level) 
	{
		if(class_param.containsKey(class_id))
			return class_param.get(class_id).base_hp[level-1];
		if(ConfigValue.EnablePtsPlayerStat)
			_log.warning("XmlPcParameterLoader: Error select HP param for class: "+class_id+" and level: "+level);
        return 1;
    }

	public double getMaxMp(int class_id, int level) 
	{
		if(class_param.containsKey(class_id))
			return class_param.get(class_id).base_mp[level-1];
		if(ConfigValue.EnablePtsPlayerStat)
			_log.warning("XmlPcParameterLoader: Error select MP param for class: "+class_id+" and level: "+level);
        return 1;
    }

	public double getMaxCp(int class_id, int level) 
	{
		if(class_param.containsKey(class_id))
			return class_param.get(class_id).base_cp[level-1];
		if(ConfigValue.EnablePtsPlayerStat)
			_log.warning("XmlPcParameterLoader: Error select CP param for class: "+class_id+" and level: "+level);
        return 1;
    }

	public class StatParam
	{
		public double[] base_hp = new double[ConfigValue.AltMaxLevel];
		public double[] base_mp = new double[ConfigValue.AltMaxLevel];
		public double[] base_cp = new double[ConfigValue.AltMaxLevel];

		public double[] org_hp_regen = new double[ConfigValue.AltMaxLevel];
		public double[] org_mp_regen = new double[ConfigValue.AltMaxLevel];
		public double[] org_cp_regen = new double[ConfigValue.AltMaxLevel];
	}
}
