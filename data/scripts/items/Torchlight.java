package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.SkillTable;
import motion.MonasteryOfSilenceGame;

/**
 * Заебашил: Diagod
 * open-team.ru
 ********************************************************************************************
 * Не нравится мой код, иди на 8====> "в лес".
 ********************************************************************************************
 * Инстанс итема для мини-игры в МоС.
 **/
public class Torchlight implements IItemHandler, ScriptFile
{
	private static final int[] _itemIds = { 15485 };
	L2Player player;
	L2MonsterInstance target;
	MonasteryOfSilenceGame msg = new MonasteryOfSilenceGame();

	public void useItem(L2Playable playable, L2ItemInstance _item, Boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return;
		L2Player player = (L2Player) playable;

		// Цель не выделена, цель не моб
		if(player.getTarget() == null || !player.getTarget().isMonster())
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return;
		}

		target = (L2MonsterInstance) player.getTarget();

		if(target.getNpcId() != 18913)
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return;
		}
		int roomId = target.getAI().ROOM_ID;
		if(!msg.isPlayer(player.getObjectId(), roomId) || !msg.playGame(roomId))
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
			return;
		}
		L2Skill skill = SkillTable.getInstance().getInfo(9059, 1);
		target.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, player);
	}

	public final int[] getItemIds()
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