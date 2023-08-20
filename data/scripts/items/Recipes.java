package items;

import java.util.Collection;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.RecipeController;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Recipe;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.RecipeBookItemList;
import l2open.gameserver.serverpackets.SystemMessage;

public class Recipes implements IItemHandler, ScriptFile
{
	private static int[] _itemIds = null;

	public Recipes()
	{
		Collection<L2Recipe> rc = RecipeController.getInstance().getRecipes();
		_itemIds = new int[rc.size()];
		int i = 0;
		for(L2Recipe r : rc)
			_itemIds[i++] = r.getRecipeId();
	}

	public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		if(item == null || item.getCount() < 1)
		{
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			return;
		}

		L2Recipe rp = RecipeController.getInstance().getRecipeByRecipeItem(item.getItemId());
		if(rp.isDwarvenRecipe())
		{
			if(player.getDwarvenRecipeLimit() > 0)
			{
				if(player.getDwarvenRecipeBook().size() >= player.getDwarvenRecipeLimit())
				{
					player.sendPacket(Msg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
					return;
				}

				if(rp.getLevel() > player.getSkillLevel(L2Skill.SKILL_CRAFTING))
				{
					player.sendPacket(Msg.CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
					return;
				}
				if(player.hasRecipe(rp))
				{
					player.sendPacket(Msg.THAT_RECIPE_IS_ALREADY_REGISTERED);
					return;
				}
				// add recipe to recipebook
				player.registerRecipe(rp, true);
				player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED).addString(item.getName()));
				player.getInventory().destroyItem(item, 1, true);
				RecipeBookItemList response = new RecipeBookItemList(true, (int) player.getCurrentMp());
				response.setRecipes(player.getDwarvenRecipeBook());
				player.sendPacket(response);
			}
			else
				player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
		}
		else if(player.getCommonRecipeLimit() > 0)
		{
			if(player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit())
			{
				player.sendPacket(Msg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
				return;
			}
			if(player.hasRecipe(rp))
			{
				player.sendPacket(Msg.THAT_RECIPE_IS_ALREADY_REGISTERED);
				return;
			}
			player.registerRecipe(rp, true);
			player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED).addString(item.getName()));
			player.getInventory().destroyItem(item, 1, true);
			RecipeBookItemList response = new RecipeBookItemList(false, (int) player.getCurrentMp());
			response.setRecipes(player.getCommonRecipeBook());
			player.sendPacket(response);
		}
		else
			player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
	}

	public int[] getItemIds()
	{
		return _itemIds;
	}

	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}