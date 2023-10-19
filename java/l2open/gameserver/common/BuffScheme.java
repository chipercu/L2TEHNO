package l2open.gameserver.common;

import l2open.config.ConfigValue;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class BuffScheme
{
	public static HashMap<Integer, BuffScheme> buffSchemes = new HashMap<Integer, BuffScheme>();

	private int id;
	private String name;
	private int priceId;
	private int priceCount;
	private ArrayList<Buff> buffIds = new ArrayList<Buff>();

	public BuffScheme(int id, String name, int priceId, int priceCount)
	{
		this.id = id;
		this.name = name;
		this.priceId = priceId;
		this.priceCount = priceCount;
	}

	public static void load()
	{
		try
		{
			File file;
			if (ConfigValue.develop) {
				file = new File("data/BuffScheme.xml");
			} else {
				file = new File(ConfigValue.DatapackRoot + "/data/BuffScheme.xml");
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setIgnoringComments(true);
			Document document = dbf.newDocumentBuilder().parse(file);

			for(Node list = document.getFirstChild(); list != null; list = list.getNextSibling())
			{
				if("list".equalsIgnoreCase(list.getNodeName()))
				{
					for(Node button = list.getFirstChild(); button != null; button = button.getNextSibling())
					{
						if("scheme".equalsIgnoreCase(button.getNodeName()))
						{
							NamedNodeMap nnm = button.getAttributes();
							int id = Integer.parseInt(nnm.getNamedItem("id").getNodeValue());
							String name = nnm.getNamedItem("name").getNodeValue();
							int priceId = Integer.parseInt(nnm.getNamedItem("priceId").getNodeValue());
							int priceCount = Integer.parseInt(nnm.getNamedItem("count").getNodeValue());
							BuffScheme dbs = new BuffScheme(id, name, priceId, priceCount);

							for(Node buff = button.getFirstChild(); buff != null; buff = buff.getNextSibling())
							{
								if("buff".equalsIgnoreCase(buff.getNodeName()))
								{
									NamedNodeMap nnm1 = buff.getAttributes();
									int buffId = Integer.parseInt(nnm1.getNamedItem("id").getNodeValue());
									int level = Integer.parseInt(nnm1.getNamedItem("level").getNodeValue());
									dbs.addBuff(buffId, level);
								}
							}
							buffSchemes.put(id, dbs);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public int getPriceId()
	{
		return priceId;
	}

	public int getPriceCount()
	{
		return priceCount;
	}

	public void addBuff(int id, int level)
	{
		Buff buffId = new Buff(id, level);
		buffIds.add(buffId);
	}

	public ArrayList<Buff> getBuffIds()
	{
		return buffIds;
	}
}