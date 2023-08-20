package items;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Summon;
import npc.model.L2BallistaInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.SkillTable;

public abstract class BallistaBomb implements IItemHandler, ScriptFile
{
	private static final int[] ITEM_IDS = new int[] {9688};
	private static final int BALLISTA_BOMB_SKILL_ID = 2342;
	private static final int BALLISTA_BOMB_SKILL_LEVEL = 1;

	public void useItem(final L2Playable playable, final L2ItemInstance item)
	{
		if(playable.isCastingNow() || playable.isAttackingNow())
			return;

		if(playable != null)
		{
			L2Player player = null;
			if(playable instanceof L2Summon)
			{
				player = ((L2Summon) playable).getPlayer();
				player.sendPacket(Msg.YOU_CANNOT_SUMMON_A_BASE_BECAUSE_YOU_ARE_NOT_IN_BATTLE);
			}
			else if(playable instanceof L2Player)
			{
				player = (L2Player) playable;
				if(player.getSiegeState() == 0)
					player.sendActionFailed();
				else
				{
					if(player.getTarget() instanceof L2BallistaInstance)
					{
						L2BallistaInstance trg = (L2BallistaInstance) player.getTarget();

						if(trg.isDead())
							return;

						final L2Skill skill = SkillTable.getInstance().getInfo(BALLISTA_BOMB_SKILL_ID, BALLISTA_BOMB_SKILL_LEVEL);
						player.doCast(skill, (L2Character) player.getTarget(), false);
					}
					else
						player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
				}
			}
		}
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
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