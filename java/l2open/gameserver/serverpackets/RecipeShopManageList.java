package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2ManufactureItem;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Recipe;
import l2open.util.GArray;

import java.util.Collection;

/**
 * dd d(dd) d(ddd)
 */
public class RecipeShopManageList extends L2GameServerPacket
{
	private GArray<CreateItemInfo> infos = new GArray<CreateItemInfo>();
	private GArray<RecipeInfo> recipes = new GArray<RecipeInfo>();
	private int seller_id;
	private long seller_adena;
	private boolean _isDwarven;

	public RecipeShopManageList(L2Player seller, boolean isDvarvenCraft)
	{
		seller_id = seller.getObjectId();
		seller_adena = seller.getAdena();

		_isDwarven = isDvarvenCraft;
		Collection<L2Recipe> _recipes;
		if(_isDwarven)
			_recipes = seller.getDwarvenRecipeBook();
		else
			_recipes = seller.getCommonRecipeBook();

		int i = 1;
		for(L2Recipe r : _recipes)
			recipes.add(new RecipeInfo(r.getId(), i++));

		if(seller.getCreateList() != null)
			for(L2ManufactureItem item : seller.getCreateList().getList())
				infos.add(new CreateItemInfo(item.getRecipeId(), 0, item.getCost()));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xde);
		writeD(seller_id);
		writeD((int) Math.min(seller_adena, Integer.MAX_VALUE)); //FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
		writeD(_isDwarven ? 0x00 : 0x01);
		writeD(recipes.size());
		for(RecipeInfo _recipe : recipes)
		{
			writeD(_recipe._id);
			writeD(_recipe.n);
		}
		recipes.clear();

		writeD(infos.size());
		for(CreateItemInfo _info : infos)
		{
			writeD(_info._id);
			writeD(_info.unk1);
			writeQ(_info.cost);
		}
		infos.clear();
	}

	static class RecipeInfo
	{
		public int _id, n;

		public RecipeInfo(int __id, int _n)
		{
			_id = __id;
			n = _n;
		}
	}

	static class CreateItemInfo
	{
		public int _id, unk1;
		public long cost;

		public CreateItemInfo(int __id, int _unk1, long _cost)
		{
			_id = __id;
			unk1 = _unk1;
			cost = _cost;
		}
	}
}