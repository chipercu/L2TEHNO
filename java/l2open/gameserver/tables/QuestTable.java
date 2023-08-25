package l2open.gameserver.tables;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import l2open.config.ConfigValue;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestType;
import l2open.gameserver.templates.QuestTemplate;
import l2open.gameserver.tables.comp.DTDEntityResolver;

/**
 * @author L2CCCP
 * @site http:l2cccp.com/
 */
public class QuestTable
{
	private static QuestTable _instance;

	private final Logger _log = Logger.getLogger("QuestTable");
	private Map<Integer, QuestTemplate> _quests;
	private final SAXReader reader;

	public static QuestTable getInstance()
	{
		if(_instance == null)
			_instance = new QuestTable();

		return _instance;
	}

	private QuestTable()
	{
		reader = new SAXReader();
		_quests = new HashMap<Integer, QuestTemplate>();

		final String directory = ConfigValue.DatapackRoot + "/data/xml/quests/";
		File dir = new File(directory);
		if(!dir.exists())
		{
			_log.warning("Dir " + dir.getAbsolutePath() + " not exists");
			return;
		}

		File dtd = new File(directory, "quests.dtd");
		if(!dtd.exists())
		{
			_log.warning("DTD file: " + dtd.getName() + " not exists.");
			return;
		}

		reader.setValidation(true);
		reader.setEntityResolver(new DTDEntityResolver(dtd));

		try
		{
			Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());

			for(File f : files)
			{
				if(!f.isHidden())
				{
					try
					{
						Document doc = parse(new FileInputStream(f), f.getName());
						readData(doc.getRootElement());
					}
					catch(Exception e)
					{
						_log.warning("Exception: " + e + " in file: " + f.getName());
					}
				}
			}
		}
		catch(Exception e)
		{
			_log.warning("Exception: " + e);
		}

		_log.info("QuestTable: Load " + _quests.size() + " quest(s)");
	}

	private Document parse(InputStream f, String name) throws DocumentException
	{
		return reader.read(f);
	}

	private void readData(Element element) throws Exception
	{
		for(Iterator<Element> iterator = element.elementIterator("quest"); iterator.hasNext();)
		{
			Element quest = iterator.next();
			final int id = Integer.parseInt(quest.attributeValue("id"));
			final QuestType type = QuestType.valueOf(quest.attributeValue("type"));

			QuestTemplate template = new QuestTemplate(id, type);
			parseData(quest, template);
		}
	}

	private void parseData(Element quest, final QuestTemplate template)
	{
		Map<String, String> name = new HashMap<String, String>();
		for(Iterator<Element> it = quest.elementIterator(); it.hasNext();)
		{
			Element element = it.next();

			final String link = element.getName();
			if("name".equalsIgnoreCase(link))
			{
				final String lang = element.attributeValue("lang");
				final String val = element.attributeValue("val");
				name.put(lang, val);
			}
			else if("setting".equalsIgnoreCase(link))
				parseSetting(element, template);
			else
				_log.warning("Unknown element in quest data -> " + link);
		}

		template.setNames(name);
		add(template);
	}

	private void parseSetting(Element quest, final QuestTemplate template)
	{
		Map<QuestEventType, List<Integer>> events = new HashMap<QuestEventType, List<Integer>>();
		List<Integer> items = new ArrayList<Integer>();
		for(Iterator<Element> it = quest.elementIterator(); it.hasNext();)
		{
			Element element = it.next();

			final String link = element.getName();
			if("rate".equalsIgnoreCase(link))
			{
				final double[] rates = new double[4];
				final double drop = Double.parseDouble(element.attributeValue("drop", "1.0"));
				final double reward = Double.parseDouble(element.attributeValue("reward", "1.0"));
				final double exp = Double.parseDouble(element.attributeValue("exp", "1.0"));
				final double sp = Double.parseDouble(element.attributeValue("sp", "1.0"));

				rates[QuestTemplate.DROP] = drop;
				rates[QuestTemplate.REWARD] = reward;
				rates[QuestTemplate.EXP] = exp;
				rates[QuestTemplate.SP] = sp;

				template.setRates(rates);
			}
			else if("event".equalsIgnoreCase(link))
			{
				final QuestEventType type = QuestEventType.valueOf(element.attributeValue("type"));
				final int id = Integer.parseInt(element.attributeValue("val"));

				if(events.containsKey(type))
				{
					List<Integer> list = events.get(type);
					list.add(id);
				}
				else
				{
					List<Integer> list = new ArrayList<Integer>();
					list.add(id);
					events.put(type, list);
				}
			}
			else if("item".equalsIgnoreCase(link))
			{
				final int id = Integer.parseInt(element.attributeValue("id"));
				items.add(id);
			}
			else
				_log.warning("Unknown element in quest setting -> " + link);
		}

		template.setEvents(events);
		template.setItems(items);
	}

	public void add(final QuestTemplate quest)
	{
		_quests.put(quest.getId(), quest);
	}

	public QuestTemplate get(final int quest)
	{
		return _quests.get(quest);
	}
}