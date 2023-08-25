package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Recipe;

import java.util.Collection;

public class RecipeBookItemList extends L2GameServerPacket
{
	private Collection<L2Recipe> _recipes;

	private final boolean _isDwarvenCraft;
	private final int _CurMP;

	public RecipeBookItemList(boolean isDwarvenCraft, int CurMP)
	{
		_isDwarvenCraft = isDwarvenCraft;
		_CurMP = CurMP;
	}

	public void setRecipes(Collection<L2Recipe> recipeBook)
	{
		_recipes = recipeBook;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xdc); //Точно: назначение пакета

		writeD(_isDwarvenCraft ? 0x00 : 0x01); // Точно: 0 = Dwarven 1 = Common
		writeD(_CurMP); //Точно: текущее количество MP

		if(_recipes == null)
			writeD(0);
		else
		{
			writeD(_recipes.size()); //Точно: количество рецептов в книге

			for(L2Recipe recipe : _recipes)
			{
				writeD(recipe.getId()); //Точно: ID рецепта
				writeD(1); //Вероятно заглушка
			}
		}
	}
}