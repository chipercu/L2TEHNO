package items;

import bosses.AntharasManager;
import bosses.ValakasManager;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 24.02.2012 20:58
 * @ Хандлер для тотемов и остальных итемов которые используются в зоне антраса\валакаса.
 */
public class Totem implements IItemHandler, ScriptFile
{
	private static final int[] ITEM_IDS = {21899, 21900, 21901, 21902, 21903, 21904, 17268};
	private static final int[] SKILL_IDS = {23308, 23309, 23310, 23311, 22298, 22299, 9179};

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
	
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, Boolean val)
	{
		int itemId = item.getItemId();
		switch(itemId)
		{
			case 21899:
			{
				if(!playable.isInZone(AntharasManager.getZone()) && !playable.isInZone(ValakasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
					return;
				}
				for(L2NpcInstance obj : L2World.getAroundNpc(playable, 3000, 3000))
				{
					if(obj != null && obj.getNpcId() == 143)
					{
						playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
						return;
					}
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[0]*65536L+1 : SKILL_IDS[0]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[0], 1);
					playable.doCast(skill, playable, false);
					spawnSingle(143, Location.findPointToStay(playable.getLoc(), 50, 100, playable.getReflection().getGeoIndex()), 1800000);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[0], 1);
					playable.sendReuseMessage(skill);
					return;
				}
				break;
			}
			case 21900:
			{
				if(!playable.isInZone(AntharasManager.getZone()) && !playable.isInZone(ValakasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21900));
					return;
				}
				for(L2NpcInstance obj : L2World.getAroundNpc(playable, 3000, 3000))
				{
					if(obj != null && obj.getNpcId() == 144)
					{
						playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
						return;
					}
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[1]*65536L+1 : SKILL_IDS[1]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[1], 1);
					playable.doCast(skill, playable, false);
					spawnSingle(144, Location.findPointToStay(playable.getLoc(), 50, 100, playable.getReflection().getGeoIndex()), 1800000);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[1], 1);
					playable.sendReuseMessage(skill);
					return;
				}
				break;
			}
			case 21901:
			{
				if(!playable.isInZone(AntharasManager.getZone()) && !playable.isInZone(ValakasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21901));
					return;
				}
				for(L2NpcInstance obj : L2World.getAroundNpc(playable, 3000, 3000))
				{
					if(obj != null && obj.getNpcId() == 145)
					{
						playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
						return;
					}
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[2]*65536L+1 : SKILL_IDS[2]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[2], 1);
					playable.doCast(skill, playable, false);
					spawnSingle(145, Location.findPointToStay(playable.getLoc(), 50, 100, playable.getReflection().getGeoIndex()), 1800000);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[2], 1);
					playable.sendReuseMessage(skill);
					return;
				}
				break;
			}
			case 21902:
			{
				if(!playable.isInZone(AntharasManager.getZone()) && !playable.isInZone(ValakasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21902));
					return;
				}
				for(L2NpcInstance obj : L2World.getAroundNpc(playable, 3000, 3000))
				{
					if(obj != null && obj.getNpcId() == 146)
					{
						playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
						return;
					}
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[3]*65536L+1 : SKILL_IDS[3]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[3], 1);
					playable.doCast(skill, playable, false);
					spawnSingle(146, Location.findPointToStay(playable.getLoc(), 50, 100, playable.getReflection().getGeoIndex()), 1800000);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[3], 1);
					playable.sendReuseMessage(skill);
					return;
				}
				break;
			}
			case 21903:
			{
				if(!playable.isInZone(AntharasManager.getZone()) && !playable.isInZone(ValakasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21903));
					return;
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[4]*65536L+1 : SKILL_IDS[4]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[4], 1);
					playable.doCast(skill, playable, false);
					Functions.removeItem(playable, 21903, 1);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[4], 1);
					playable.sendReuseMessage(skill);
					return;
				}
				break;
			}
			case 21904:
			{
				if(!playable.isInZone(AntharasManager.getZone()) && !playable.isInZone(ValakasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21904));
					return;
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[5]*65536L+1 : SKILL_IDS[5]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[5], 1);
					playable.doCast(skill, playable, false);
					Functions.removeItem(playable, 21904, 1);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[5], 1);
					playable.sendReuseMessage(skill);
					return;
				}
				break;
			}
			case 17268:
			{
				if(!playable.isInZone(AntharasManager.getZone()))
				{
					playable.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17268));
					return;
				}
				if(!playable.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? SKILL_IDS[6]*65536L+1 : SKILL_IDS[6]))
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[6], 1);
					playable.doCast(skill, playable, false);
					Functions.removeItem(playable, 17268, 1);
				}
				else
				{
					L2Skill skill = SkillTable.getInstance().getInfo(SKILL_IDS[6], 1);
					playable.sendReuseMessage(skill);
					return;
				}
			}
		}
	}

	public static L2NpcInstance spawnSingle(int npcId, Location loc,  long despawnTime)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npcId);
		if(template == null)
			throw new NullPointerException("Npc template id : " + npcId + " not found!");

		L2NpcInstance npc = template.getNewInstance();
		npc.setHeading(loc.h < 0 ? Rnd.get(0xFFFF) : loc.h);
		npc.setSpawnedLoc(loc);
		npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
		npc.spawnMe(npc.getSpawnedLoc());
		if(despawnTime > 0)
			ThreadPoolManager.getInstance().schedule(new DeleteCauldron(npc), despawnTime);
		return npc;
	}

	public static class DeleteCauldron extends l2open.common.RunnableImpl
	{
		L2NpcInstance _npc;

		public DeleteCauldron(L2NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			_npc.deleteMe();
		}
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