package l2open.gameserver.xml.loader;

import l2open.config.*;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.conditions.*;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.skills.triggers.*;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.OptionDataTemplate;
import l2open.gameserver.xml.XmlUtils;
import org.dom4j.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author : Diagod
 * @date : 08.01.12
 */
public class XmlOptionDataLoader
{
	private Logger _log = Logger.getLogger(XmlOptionDataLoader.class.getName());

    private static XmlOptionDataLoader ourInstance = new XmlOptionDataLoader();
    private ConcurrentHashMap<Integer, OptionDataTemplate> _templates = new ConcurrentHashMap<Integer, OptionDataTemplate>();

    public static XmlOptionDataLoader getInstance()
	{
        return ourInstance;
    }

    private XmlOptionDataLoader()
	{
        _templates.clear();
        load();
    }

    private void load()
	{
		File file;
		if (ConfigValue.develop){
			file = new File("data/stats/option");
		}else {
			file = new File(ConfigValue.DatapackRoot + "/data/stats/option");
		}

        try
		{
            for(File f : file.listFiles())
			{
                if(f.getName().endsWith(".xml"))
				{
                    Document doc = XmlUtils.readFile(f);
                    Element rootElement = doc.getRootElement();
					
					for(Iterator<Element> itemIterator = rootElement.elementIterator(); itemIterator.hasNext();)
					{
						Element optionDataElement = itemIterator.next();
						OptionDataTemplate template = new OptionDataTemplate(Integer.parseInt(optionDataElement.attributeValue("id")));
						for(Iterator<Element> subIterator = optionDataElement.elementIterator(); subIterator.hasNext();)
						{
							Element subElement = subIterator.next();
							String subName = subElement.getName();
							if(subName.equalsIgnoreCase("for"))
								parseFor(subElement, template);
							else if(subName.equalsIgnoreCase("triggers"))
								parseTriggers(subElement, template);
							else if(subName.equalsIgnoreCase("skills"))
							{
								for(Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
								{
									Element nextElement =  nextIterator.next();
									int id = Integer.parseInt(nextElement.attributeValue("id"));
									int level = Integer.parseInt(nextElement.attributeValue("level"));

									L2Skill skill = SkillTable.getInstance().getInfo(id, level);

									if(skill != null)
										template.addSkill(skill);
									else
										_log.info("L2Skill not found(" + id + "," + level + ") for option data:" + template.getId() + "; file:" + f.getName());
								}
							}
						}
						_templates.put(template.getId(), template);
					}
                }
            }
            _log.info("XmlOptionDataLoader: Loaded " + _templates.size() + " templates.");
        }
		catch (DocumentException e)
		{
            e.printStackTrace();
        }
    }

	protected void parseFor(Element forElement, StatTemplate template)
	{
		for(Iterator iterator = forElement.elementIterator(); iterator.hasNext();)
		{
			Element element = (Element) iterator.next();
			final String elementName = element.getName();
			if(elementName.equalsIgnoreCase("add"))
			{
				Stats stat = Stats.valueOfXml(element.attributeValue("stat"));
				String order = element.attributeValue("order");
				int ord = parseNumber(order).intValue();
				double val = 0;
				if(element.attributeValue("value") != null)
					val = parseNumber(element.attributeValue("value")).doubleValue();

				template.attachFunc(new FuncTemplate(null, "Add", stat, ord, val));
			}
			else if(elementName.equalsIgnoreCase("mul"))
			{
				Stats stat = Stats.valueOfXml(element.attributeValue("stat"));
				String order = element.attributeValue("order");
				int ord = parseNumber(order).intValue();
				double val = 0;
				if(element.attributeValue("value") != null)
					val = parseNumber(element.attributeValue("value")).doubleValue();

				template.attachFunc(new FuncTemplate(null, "Mul", stat, ord, val));
			}
		}
	}

	protected void parseTriggers(Element f, StatTemplate triggerable)
	{
		for(Iterator iterator = f.elementIterator(); iterator.hasNext();)
		{
			Element element = (Element) iterator.next();
			int id = parseNumber(element.attributeValue("id")).intValue();
			int level = parseNumber(element.attributeValue("level")).intValue();
			TriggerType t = TriggerType.valueOf(element.attributeValue("type"));
			double chance = parseNumber(element.attributeValue("chance")).doubleValue();

			TriggerInfo trigger = new TriggerInfo(id, level, t, chance, (byte)0);

			triggerable.addTrigger(trigger);
		}
	}

	protected Number parseNumber(String value)
	{
		try
		{
			if(value.indexOf('.') == -1)
			{
				int radix = 10;
				if(value.length() > 2 && value.substring(0, 2).equalsIgnoreCase("0x"))
				{
					value = value.substring(2);
					radix = 16;
				}
				return Integer.valueOf(value, radix);
			}
			return Double.valueOf(value);
		}
		catch(NumberFormatException e)
		{
			return null;
		}
	}

	public OptionDataTemplate getTemplate(int id)
	{
		return _templates.get(id);
	}

	public ConcurrentHashMap<Integer, OptionDataTemplate> getTemplates()
	{
		return _templates;
	}
}
