package npc.model;

import l2open.config.ConfigValue;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Multisell.MultiSellListContainer;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.base.MultiSellIngredient;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

import java.util.StringTokenizer;

import marcket.MarcketLoad;
import marcket.MarcketLoad.ItemInfo;

/**

Каждуюу Пятницу-Субботу запускается 'Ярмарка Острова Фантазий) (не Ивент)
На острове фантазий появляется НПСы для продажи ресурсов, кусков(до Ы) , рецептов (до Ы). НПСы появляется в районе базарчика (есть там место такое). Должны продавать определенное количество всем игрокам. Т.е. если кто то скупил все ресурсы, то другому уже не продаст. Если все продал в окне должно быть 'Я уже все продал... Приходи в следующий раз'
Цены(по возможности' должны быть плавающими. т.е. рендомно не ниже магаза, и не выше чем цена магаза умноженная на 3.
После продажи всех итемов (если все прям продали -'Ярмарка закрыта. Все продано', либо 'Ярмарка закрыта, приходите на следующей неделе'

Обязательное условие - цены рендомные!. НПСов можно сделать штук 15 . если надо координаты для каждого дам

**/
public final class MarketNpcInstance extends L2NpcInstance
{
	// Количество итемо в трейде НПСа...
	private static int sellCount = 5;

	public MarketNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("buy_my"))
		{
			SeparateAndSend(player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void SeparateAndSend(L2Player player)
	{
		MultiSellListContainer list = generateMultiSell(player);
		if(list == null)
			return;
		MultiSellListContainer temp = new MultiSellListContainer();
		int page = 1;

		temp.setListId(list.getListId());

		// Запоминаем отсылаемый лист, чтобы не подменили
		player.setMultisell(list);

		for(MultiSellEntry e : list.getEntries())
		{
			if(temp.getEntries().size() == ConfigValue.MultisellPageSize)
			{
				player.sendPacket(new MultiSellList(temp, page, 0));
				page++;
				temp = new MultiSellListContainer();
				temp.setListId(list.getListId());
			}
			temp.addEntry(e);
		}

		player.sendPacket(new MultiSellList(temp, page, 1));
	}

	private MultiSellListContainer generateMultiSell(L2Player player)
	{
		MultiSellListContainer list = new MultiSellListContainer();
		list.setListId(-1);
		list.setShowAll(true);
		list.setKeepEnchant(false);
		list.setNoTax(true);
		list.setNoKey(true);

		MultiSellEntry entry = new MultiSellEntry();
		ItemInfo ii = null;
		for(int i = 0; i < sellCount; i++)
		{
			int get = 0;
			int[] ready = new int[sellCount];
			int _listSize = MarcketLoad._sellList.size();
			for(int i2 = 0; i2 < _listSize; i2++)
			{
				get = Rnd.get(_listSize);
				for(int id : ready)
					if(get == id)
						continue;
				ready[i] = get;
				break;
			}
			ii = MarcketLoad._sellList.get(get);
			if(ii != null)
			{
				entry.addIngredient(new MultiSellIngredient(57, ii.countAdena, 0, -2, -2, 0, false, false));
				entry.addProduct(new MultiSellIngredient(ii.id, Rnd.get(ii.countMin, ii.countMax), ii.enchant, ii.element, ii.elementValue, 0, false, false));
			}
			else
				System.out.println("MarketNpcInstance Error-1...");
		}
		list.addEntry(entry);
		return list;
	}
}