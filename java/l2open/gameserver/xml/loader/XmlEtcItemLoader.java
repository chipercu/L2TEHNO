package l2open.gameserver.xml.loader;

import l2open.config.*;
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


/**
 * @author : Ragnarok
 * @date : 10.01.11    16:20
 */
public class XmlEtcItemLoader 
{
    private static XmlEtcItemLoader ourInstance = new XmlEtcItemLoader();
	private List<Integer> customItem = new ArrayList<Integer>(0);
    private ConcurrentHashMap<Integer, L2EtcItem> etcItems = new ConcurrentHashMap<Integer, L2EtcItem>();
    private Logger log = Logger.getLogger(XmlEtcItemLoader.class.getName());

    public static XmlEtcItemLoader getInstance() 
	{
        return ourInstance;
    }

    private XmlEtcItemLoader() 
	{
		load();
    }

    private void load() 
	{
		etcItems.clear();

		for(File f : new File(ConfigValue.DatapackRoot + "/data/stats/custom_items").listFiles())
			parseFile(f, true);
		for(File f : new File(ConfigValue.DatapackRoot + "/data/stats/items/etcitem").listFiles())
			parseFile(f, false);

		log.info("XmlEtcItemLoader: Loaded " + customItem.size() + " Custom EtcItems");
		log.info("XmlEtcItemLoader: Loaded " + etcItems.size() + " EtcItems");
		customItem.clear();
    }

	public void parseFile(File f, boolean custom)
	{
		try 
		{
			if(f.getName().endsWith(".xml")) 
			{
				Document doc = XmlUtils.readFile(f);
				Element list = doc.getRootElement();
				for(Element etcitem : list.elements("etcitem")) 
				{
					StatsSet set = new StatsSet();
					EtcItemType type;
					set.set("type1", L2Item.TYPE1_ITEM_QUESTITEM_ADENA);
					set.set("type2", L2Item.TYPE2_OTHER);
					type = EtcItemType.valueOf(etcitem.attributeValue("type"));
					int id = XmlUtils.getSafeInt(etcitem, "id", 0);
					if(id == 0 || !custom && customItem.contains(id))
						continue;
					else if(custom)
						customItem.add(id);
					set.set("item_id", id);
					set.set("name", etcitem.attributeValue("name"));
					for(Iterator<Element> i = etcitem.elementIterator("set"); i.hasNext(); ) 
					{
						Element e = i.next();
						set.set(e.attributeValue("name"), e.attributeValue("val"));
					}
					switch(type) 
					{
						case QUEST:
							set.set("type2", L2Item.TYPE2_QUEST);
							break;
						case MONEY:
							set.set("type2", L2Item.TYPE2_MONEY);
							break;
					}
					Element skills = etcitem.element("skills");
					if(skills != null) 
					{
						String sk = "";
						String lvl = "";
						for(Iterator<Element> i = skills.elementIterator("skill"); i.hasNext(); ) 
						{
							Element skill = i.next();
							sk += skill.attributeValue("id") + ";";
							lvl += skill.attributeValue("lvl") + ";";
						}
						set.set("skill_id", sk);
						set.set("skill_level", lvl);
					}
					L2EtcItem eItem = new L2EtcItem(type, set);
					etcItems.put(id, eItem);
				}
			}
		} 
		catch (DocumentException e) 
		{
			e.printStackTrace();
		}
	}

    public ConcurrentHashMap<Integer, L2EtcItem> getEtcItems() 
	{
        return etcItems;
    }
}
