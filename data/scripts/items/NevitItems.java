package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;

/**
 * @author : Ragnarok
 * @date : 14.10.2010   18:28:32
 */
public class NevitItems implements IItemHandler, ScriptFile 
{
    private static final int[] itemId = 
	{
            17094
    };

    @Override
    public void useItem(L2Playable playable, L2ItemInstance item, Boolean val) 
	{
        if(playable == null || !playable.isPlayer())
            return;
        L2Player player = (L2Player) playable;
        player.getInventory().destroyItem(item, 1, false);
        player.getRecommendation().addRecomHave(10);
        player.sendPacket(new SystemMessage(3207).addNumber(10));
		player.getRecommendation().updateVoteInfo();
    }

    @Override
    public int[] getItemIds() 
	{
        return itemId;
    }

    @Override
    public void onLoad() 
	{
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() 
	{}

    @Override
    public void onShutdown() 
	{}
}
