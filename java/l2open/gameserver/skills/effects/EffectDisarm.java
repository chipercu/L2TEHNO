package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.skills.Env;
import l2open.gameserver.xml.ItemTemplates;

public final class EffectDisarm extends L2Effect
{
	public EffectDisarm(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isPlayer()) 
		{
            L2Player player = _effected.getPlayer();
            if(player == null)
				return;
            if(player.isCursedWeaponEquipped() || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
				return;
            if(player.getActiveWeaponInstance() == null)
				return;

            L2ItemInstance weapon = player.getActiveWeaponInstance();
            if(weapon != null)
			{
				player.sendDisarmMessage(weapon);
				if(player.isCastingNow())
					player._disarm.getAndSet(true);
				else
					player.getInventory().unEquipItemInBodySlotAndNotify(weapon.getBodyPart(), weapon, false);
			}
        }
        else if(_effected.isNpc() && _effector.isPlayer()) 
		{
            L2NpcInstance npc = (L2NpcInstance) _effected;
            if(npc.getLeftHandItem() != 0 && ItemTemplates.getInstance().createItem(npc.getLeftHandItem()).isWeapon())
                npc.setLHandId(0);
            if(npc.getRightHandItem() != 0 && ItemTemplates.getInstance().createItem(npc.getRightHandItem()).isWeapon())
                npc.setRHandId(0);
            npc.updateAbnormalEffect();
        }
	}

    @Override
    public void onExit() 
	{
        super.onExit();
        if(_effected.isNpc() && _effector.isPlayer()) 
		{
            L2NpcInstance npc = (L2NpcInstance) _effected;
            npc.setLHandId(npc.getTemplate().lhand);
            npc.setRHandId(npc.getTemplate().rhand);
            npc.updateAbnormalEffect();
        }
    }

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}