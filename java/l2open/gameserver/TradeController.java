package l2open.gameserver;

import l2open.config.ConfigValue;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public class TradeController
{
	private static Logger _log = Logger.getLogger(TradeController.class.getName());
	private static TradeController _instance;

	private HashMap<Integer, NpcTradeList> _lists;

	public static TradeController getInstance()
	{
		if(_instance == null)
			_instance = new TradeController();
		return _instance;
	}

	public static void reload()
	{
		_instance = new TradeController();
	}

	private TradeController()
	{
		_lists = new HashMap<Integer, NpcTradeList>();

		try
		{
			File filelists = new File(ConfigValue.DatapackRoot + "/data/xml/merchant_filelists.xml");
			if (ConfigValue.develop){
				filelists = new File("data/xml/merchant_filelists.xml");
			}
			DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			factory1.setValidating(false);
			factory1.setIgnoringComments(true);
			Document doc1 = factory1.newDocumentBuilder().parse(filelists);

			int counterFiles = 0;
			int counterItems = 0;
			for(Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
				if("list".equalsIgnoreCase(n1.getNodeName()))
					for(Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
						if("file".equalsIgnoreCase(d1.getNodeName()))
						{
							final String filename = d1.getAttributes().getNamedItem("name").getNodeValue();

							File file = new File(ConfigValue.DatapackRoot + "/data/xml/" + filename);
							if (ConfigValue.develop){
								file = new File("data/xml/" + filename);
							}


							DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
							factory2.setValidating(false);
							factory2.setIgnoringComments(true);
							Document doc2 = factory2.newDocumentBuilder().parse(file);
							counterFiles++;

							for(Node n2 = doc2.getFirstChild(); n2 != null; n2 = n2.getNextSibling())
								if("list".equalsIgnoreCase(n2.getNodeName()))
									for(Node d2 = n2.getFirstChild(); d2 != null; d2 = d2.getNextSibling())
										if("tradelist".equalsIgnoreCase(d2.getNodeName()))
										{
											final int shop_id = Integer.parseInt(d2.getAttributes().getNamedItem("shop").getNodeValue());
											final int npc_id = Integer.parseInt(d2.getAttributes().getNamedItem("npc").getNodeValue());
											final float markup = npc_id > 0 ? 1 + Float.parseFloat(d2.getAttributes().getNamedItem("markup").getNodeValue()) / 100f : 0f;
											NpcTradeList tl = new NpcTradeList(shop_id);
											tl.setNpcId(npc_id);
											for(Node i = d2.getFirstChild(); i != null; i = i.getNextSibling())
												if("item".equalsIgnoreCase(i.getNodeName()))
												{
													final int itemId = Integer.parseInt(i.getAttributes().getNamedItem("id").getNodeValue());
													final L2Item template = ItemTemplates.getInstance().getTemplate(itemId);
													if(template == null)
													{
														_log.warning("Template not found for itemId: " + itemId + " for shop " + shop_id);
														continue;
													}
													if(!checkItem(template))
														continue;
													counterItems++;
													final int price = i.getAttributes().getNamedItem("price") != null ? Integer.parseInt(i.getAttributes().getNamedItem("price").getNodeValue()) : Math.round(template.getReferencePrice() * markup);
													TradeItem item = new TradeItem();
													item.setItemId(itemId);
													final int itemCount = i.getAttributes().getNamedItem("count") != null ? Integer.parseInt(i.getAttributes().getNamedItem("count").getNodeValue()) : 0;
													// Время респауна задается минутах
													final int itemRechargeTime = i.getAttributes().getNamedItem("time") != null ? Integer.parseInt(i.getAttributes().getNamedItem("time").getNodeValue()) : 0;
													item.setOwnersPrice(price);
													item.setCount(itemCount);
													item.setCurrentValue(itemCount);
													item.setLastRechargeTime((int) (System.currentTimeMillis() / 60000));
													item.setRechargeTime(itemRechargeTime);
													tl.addItem(item);
												}
											_lists.put(shop_id, tl);
										}
						}

			_log.info("TradeController: Loaded " + counterFiles + " file(s).");
			_log.info("TradeController: Loaded " + counterItems + " Items.");
			_log.info("TradeController: Loaded " + _lists.size() + " Buylists.");
		}
		catch(Exception e)
		{
			_log.warning("TradeController: Buylists could not be initialized.");
			e.printStackTrace();
		}
	}

	public boolean checkItem(L2Item template)
	{
		if(template.isCommonItem() && !ConfigValue.AllowSellCommon)
			return false;
		if(template.isEquipment() && !template.isForPet() && ConfigValue.ShopPriceLimits.length > 0)
			for(int i = 0; i < ConfigValue.ShopPriceLimits.length; i += 2)
				if(template.getBodyPart() == ConfigValue.ShopPriceLimits[i])
				{
					if(template.getReferencePrice() > ConfigValue.ShopPriceLimits[i + 1])
						return false;
					break;
				}
		if(ConfigValue.ShopUnallowedItems.length > 0)
			for(int i : ConfigValue.ShopUnallowedItems)
				if(template.getItemId() == i)
					return false;
		return true;
	}

	public NpcTradeList getBuyList(int listId)
	{
		return _lists.get(listId);
	}

	public void addToBuyList(int listId, NpcTradeList list)
	{
		_lists.put(listId, list);
	}

	public static class NpcTradeList
	{
		private static final GArray<TradeItem> emptyList = new GArray<TradeItem>(0);
		private GArray<TradeItem> tradeList;
		private int _id;
		private int _npcId;

		public NpcTradeList(int id)
		{
			_id = id;
		}

		public int getListId()
		{
			return _id;
		}

		public void setNpcId(int id)
		{
			_npcId = id;
		}

		public int getNpcId()
		{
			return _npcId;
		}

		public void addItem(TradeItem ti)
		{
			if(tradeList == null)
				tradeList = new GArray<TradeItem>();
			tradeList.add(ti);
		}

		public GArray<TradeItem> getItems()
		{
			return tradeList == null ? emptyList : tradeList;
		}
	}
}