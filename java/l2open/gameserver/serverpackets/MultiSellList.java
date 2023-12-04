package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Multisell.MultiSellListContainer;
import l2open.gameserver.model.base.MultiSellEntry;
import l2open.gameserver.model.base.MultiSellIngredient;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;

import java.util.ArrayList;
import java.util.List;

public class MultiSellList extends L2GameServerPacket
{
	protected int _page;
	protected int _finished;
	private final boolean _isnew;

	private int _listId;
	private List<MultiSellEntry> _possiblelist = new ArrayList<>();

	public MultiSellList(MultiSellListContainer list, int page, int finished)
	{
		_possiblelist = list.getEntries();
		_listId = list.getListId();
		_page = page;
		_finished = finished;
		_isnew = list.isNew();
	}

	@Override
	protected final void writeImpl()
	{
		// ddddd (dchddddddddddhh (ddhdhdddddddddd)(dhdhdddddddddd))
		writeC(0xD0);
		writeD(_listId); // list id
		writeD(_page); // page
		writeD(_finished); // finished
		writeD(ConfigValue.MultisellPageSize); // size of pages
		writeD(_possiblelist != null ? _possiblelist.size() : 0); //list lenght

		if(_possiblelist == null)
			return;

		GArray<MultiSellIngredient> ingredients;
		for(MultiSellEntry ent : _possiblelist)
		{
			ingredients = fixIngredients(ent.getIngredients());
			//ingredients = ent.getIngredients();
			writeD(ent.getEntryId());
			writeC(!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0); // stackable?
			writeH(0x00); // unknown

			writeD(0x00); // инкрустация
			writeD(0x00); // инкрустация

			writeH(-2);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);

			writeH(ent.getProduction().size());
			writeH(ingredients.size());

			for(MultiSellIngredient prod : ent.getProduction())
			{
				int itemId = prod.getItemId();
				L2Item template = itemId > 0 ? ItemTemplates.getInstance().getTemplate(prod.getItemId()) : null;
				writeD(itemId);
				writeD(itemId > 0 ? template.getBodyPart() : 0);
				writeH(itemId > 0 ? template.getType2ForPackets() : 0);
				writeQ(prod.getItemCount());
				writeH(prod.getItemEnchant());
				writeD(0x00); // инкрустация
				writeD(0x00); // инкрустация
				writeItemElements(prod);
			}

			for(MultiSellIngredient i : ingredients)
			{
				int itemId = i.getItemId();
				final L2Item item = itemId > 0 ? ItemTemplates.getInstance().getTemplate(i.getItemId()) : null;
				writeD(itemId); //ID
				writeH(itemId > 0 ? item.getType2() : 0xffff);
				writeQ(i.getItemCount()); //Count
				writeH((itemId > 0 ? item.getType2() : 0x00) <= L2Item.TYPE2_ACCESSORY ? i.getItemEnchant() : 0); //Enchant Level
				writeD(0x00); // инкрустация
				writeD(0x00); // инкрустация
				writeItemElements(i);
			}
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xD0);
        writeD(_listId); // list id
        writeD(_page); // page
        writeD(_finished); // finished
        writeD(ConfigValue.MultisellPageSize); // size of pages
		writeD(_possiblelist != null ? _possiblelist.size() : 0); //list lenght
        writeC(_isnew ? 0x01 : 0x00); //L2WT GOD при 1 открывается новый тип мультисела, с обменником
        GArray<MultiSellIngredient> ingredients;
        for (MultiSellEntry ent : _possiblelist)
		{
            ingredients = fixIngredients(ent.getIngredients());

            writeD(ent.getEntryId());
            writeC(!ent.getProduction().isEmpty() && ent.getProduction().get(0).isStackable() ? 1 : 0); // stackable?
            writeH(0x00); // unknown
            writeD(0x00); // инкрустация
            writeD(0x00); // инкрустация

			writeH(-2);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);
			writeH(0x00);

            writeH(ent.getProduction().size());
            writeH(ingredients.size());

            for (MultiSellIngredient prod : ent.getProduction())
			{
                int itemId = prod.getItemId();
				L2Item template = itemId > 0 ? ItemTemplates.getInstance().getTemplate(prod.getItemId()) : null;
                writeD(itemId);
                writeD(itemId > 0 ? template.getBodyPart() : 0);
                writeH(itemId > 0 ? template.getType2ForPackets() : 0);
                writeQ(prod.getItemCount());
                writeH(prod.getItemEnchant());
                writeD(0x00); // инкрустация
                writeD(0x00); // инкрустация
                writeD(0x00); //L2WT god
                writeItemElements(prod);
            }

            for (MultiSellIngredient i : ingredients)
			{
                int itemId = i.getItemId();
				final L2Item template = itemId > 0 ? ItemTemplates.getInstance().getTemplate(i.getItemId()) : null;
                writeD(itemId); //ID
                writeH(itemId > 0 ? template.getType2() : 0xffff);
                writeQ(i.getItemCount()); //Count
                writeH(i.getItemEnchant()); //Enchant Level
                writeD(0x00); // инкрустация
                writeD(0x00); // инкрустация
                writeItemElements(i);
            }
        }
		return true;
	}

	private static GArray<MultiSellIngredient> fixIngredients(GArray<MultiSellIngredient> ingredients)
	{
		if(ConfigValue.MultiSellListNoFix)
			return ingredients;
		//FIXME временная затычка, пока NCSoft не починят в клиенте отображение мультиселов где кол-во больше Integer.MAX_VALUE
		int needFix = 0;
		for(MultiSellIngredient ingredient : ingredients)
			if(ingredient.getItemCount() > Integer.MAX_VALUE)
				needFix++;

		if(needFix == 0)
			return ingredients;

		MultiSellIngredient temp;
		GArray<MultiSellIngredient> result = new GArray<MultiSellIngredient>(ingredients.size() + needFix);
		for(MultiSellIngredient ingredient : ingredients)
		{
			ingredient = ingredient.clone();
			while(ingredient.getItemCount() > Integer.MAX_VALUE)
			{
				temp = ingredient.clone();
				temp.setItemCount(2000000000);
				result.add(temp);
				ingredient.setItemCount(ingredient.getItemCount() - 2000000000);
			}
			if(ingredient.getItemCount() > 0)
				result.add(ingredient);
		}

		return result;
	}
}