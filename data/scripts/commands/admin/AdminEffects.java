package commands.admin;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2WorldRegion;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.Earthquake;
import l2open.gameserver.serverpackets.NpcInfo;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Rnd;
import l2open.util.Util;

public class AdminEffects implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_invis,
		admin_vis,
		admin_offline_vis,
		admin_offline_invis,
		admin_earthquake,
		admin_unpara_all,
		admin_para_all,
		admin_unpara,
		admin_para,
		admin_changename,
		admin_gmspeed,
		admin_invul,
		admin_setinvul,
		admin_getinvul,
		admin_social,
		admin_abnormal,
		admin_transform,
		admin_state,
		admin_inv
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().GodMode)
			return false;

		int val;
		AbnormalVisualEffect ae = AbnormalVisualEffect.ave_none;
		L2Object target = activeChar.getTarget();

		switch(command)
		{
			case admin_inv:
				if(target == null || !target.isCharacter() )
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				activeChar.sendMessage("Invul info: isInvul="+((L2Character) target).isInvul()+"["+((L2Character) target)._isInvul+"]["+((L2Character) target)._isInvul_skill+"]");
				break;
			case admin_invis:
			case admin_vis:
				if(activeChar.isInvisible())
				{
					activeChar.setInvisible(false);
					activeChar.broadcastUserInfo(true);
					if(activeChar.getPet() != null)
						activeChar.getPet().broadcastPetInfo();
					activeChar.broadcastRelationChanged();
					/*if(activeChar._fraction != Fraction.NONE)
						for(L2Player player : L2World.getAroundPlayers(activeChar))
							if(player != null && player != activeChar)
								player.sendPacket(new MagicSkillUse(activeChar, activeChar, 9550+activeChar._fraction.fraction_id, 1, 0, 0));*/
				}
				else
				{
					activeChar.setInvisible(true);
					activeChar.sendUserInfo(true);
					if(activeChar.getCurrentRegion() != null)
						for(L2WorldRegion neighbor : activeChar.getCurrentRegion().getNeighbors())
							neighbor.removePlayerFromOtherPlayers(activeChar);
				}
				break;
			case admin_gmspeed:
				if(wordList.length < 2)
					val = 0;
				else
					try
					{
						val = Integer.parseInt(wordList[1]);
					}
					catch(Exception e)
					{
						activeChar.sendMessage("USAGE: //gmspeed value=[0..4]");
						return false;
					}
				GArray<L2Effect> superhaste = activeChar.getEffectList().getEffectsBySkillId(7029);
				int sh_level = superhaste == null ? 0 : superhaste.isEmpty() ? 0 : superhaste.get(0).getSkill().getLevel();

				if(val == 0)
				{
					if(sh_level != 0)
						activeChar.doCast(SkillTable.getInstance().getInfo(7029, sh_level), activeChar, true); //снимаем еффект
					activeChar.unsetVar("gm_gmspeed");
				}
				else if(val >= 1 && val <= 4)
				{
					if(ConfigValue.SaveGMEffects)
						activeChar.setVar("gm_gmspeed", String.valueOf(val));
					if(val != sh_level)
					{
						L2Skill skills;
						if(sh_level != 0)
							skills = SkillTable.getInstance().getInfo(7029, sh_level);
						skills = SkillTable.getInstance().getInfo(7029, val);
						if(!skills.checkSkillAbnormal(activeChar) && !skills.isBlockedByChar(activeChar, skills))
							try
							{
								for (EffectTemplate et : skills.getEffectTemplates())
								{
									Env env = new Env(activeChar, activeChar, skills);
									L2Effect effect = et.getEffect(env);
									activeChar.getEffectList().addEffect(effect);
								}
							}
							catch (Exception e)
							{
							}
					}
				}
				else
					activeChar.sendMessage("USAGE: //gmspeed value=[0..4]");
				activeChar.updateEffectIcons();
				break;
			case admin_invul:
				handleInvul(activeChar, activeChar);
				if(activeChar.isInvul())
				{
					if(ConfigValue.SaveGMEffects)
						activeChar.setVar("gm_invul", "true");
				}
				else
					activeChar.unsetVar("gm_invul");
				break;
		}

		if(!activeChar.isGM())
			return false;

		switch(command)
		{
			case admin_offline_vis:
				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null && player.isInOfflineMode())
					{
						player.setInvisible(false);
						player.decayMe();
						player.spawnMe();
						player.broadcastRelationChanged();
					}
				break;
			case admin_offline_invis:
				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null && player.isInOfflineMode())
					{
						player.setInvisible(true);
						player.decayMe();
					}
				break;
			case admin_earthquake:
				try
				{
					int intensity = Integer.parseInt(wordList[1]);
					int duration = Integer.parseInt(wordList[2]);
					activeChar.broadcastPacket(new Earthquake(activeChar.getLoc(), intensity, duration));
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //earthquake intensity duration");
					return false;
				}
				break;
			case admin_unpara_all:
				for(L2Player player : L2World.getAroundPlayers(activeChar, 1250, 200))
				{
					player.stopAbnormalEffect(AbnormalVisualEffect.ave_paralyze);
					player.stopAbnormalEffect(AbnormalVisualEffect.ave_flesh_stone);
					player.setParalyzed(false);
				}
				break;
			case admin_para_all:
				ae = wordList.length > 1 && wordList[1].equalsIgnoreCase("2") ? AbnormalVisualEffect.ave_flesh_stone : AbnormalVisualEffect.ave_paralyze;
				for(L2Player player : L2World.getAroundPlayers(activeChar, 1250, 200))
					if(player != null && !player.isGM())
					{
						player.startAbnormalEffect(ae);
						player.setParalyzed(true);
					}
				break;
			case admin_unpara:
				if(target == null || !target.isCharacter())
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				((L2Character) target).stopAbnormalEffect(AbnormalVisualEffect.ave_paralyze);
				((L2Character) target).stopAbnormalEffect(AbnormalVisualEffect.ave_flesh_stone);
				((L2Character) target).setParalyzed(false);
				break;
			case admin_para:
				ae = wordList.length > 1 && wordList[1].equalsIgnoreCase("2") ? AbnormalVisualEffect.ave_flesh_stone : AbnormalVisualEffect.ave_paralyze;
				if(target == null || !target.isCharacter())
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				((L2Character) target).startAbnormalEffect(ae);
				((L2Character) target).setParalyzed(true);
				break;
			case admin_changename:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //changename newName");
					return false;
				}
				if(target == null)
					target = activeChar;
				if(!target.isCharacter())
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				String oldName = ((L2Character) target).getName();
				String newName = Util.joinStrings(" ", wordList, 1);

				((L2Character) target).setName(newName);

				if(target.isPlayer())
					((L2Character) target).broadcastUserInfo(true);
				else if(target.isNpc())
					((L2Character) target).broadcastPacket(new NpcInfo((L2NpcInstance) target, null));

				activeChar.sendMessage("Changed name from " + oldName + " to " + newName + ".");
				break;
			case admin_setinvul:
				if(target == null || !target.isPlayer())
				{
					activeChar.sendPacket(Msg.INVALID_TARGET);
					return false;
				}
				handleInvul(activeChar, (L2Player) target);
				break;
			case admin_getinvul:
				if(target != null && target.isCharacter())
					activeChar.sendMessage("Target " + target.getName() + "(object ID: " + target.getObjectId() + ") is " + (!((L2Character) target).isInvul() ? "NOT " : "") + "invul");
				break;
			case admin_social:
				if(wordList.length < 2)
					val = Rnd.get(1, 7);
				else
					try
					{
						val = Integer.parseInt(wordList[1]);
					}
					catch(NumberFormatException nfe)
					{
						activeChar.sendMessage("USAGE: //social value");
						return false;
					}
				if(target == null || target == activeChar)
					activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), val));
				else if(target.isCharacter())
					((L2Character) target).broadcastPacket(new SocialAction(target.getObjectId(), val));
				break;
			case admin_state:
                try 
				{
					val = Integer.parseInt(wordList[1]);
                } 
				catch (Exception e) 
				{
                    activeChar.sendMessage("USAGE: //state state_id");
					return false;
                }
                if((activeChar.getTarget().isPlayer())) 
				{
                    activeChar.sendPacket(Msg.INVALID_TARGET);
                    return false;
                }
				((L2Character)activeChar.getTarget()).setNpcState(val);
				break;
			case admin_abnormal:
				try
				{
					if(wordList.length > 1)
						ae = AbnormalVisualEffect.valueOf(wordList[1]);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //abnormal name");
					activeChar.sendMessage("//abnormal - Clears all abnormal effects");
					return false;
				}

				L2Character effectTarget = target == null ? activeChar : (L2Character) target;

				effectTarget.startAbnormalEffect(ae);
				if(ae == AbnormalVisualEffect.ave_none)
				{
					effectTarget.sendMessage("Abnormal effects clearned by admin.");
					if(effectTarget != activeChar)
						effectTarget.sendMessage("Abnormal effects clearned.");
				}
				else
				{
					effectTarget.sendMessage("Admin added abnormal effect: " + ae);
					if(effectTarget != activeChar)
						effectTarget.sendMessage("Aadded abnormal effect: " + ae);
				}
				break;
			case admin_transform:
				try
				{
					val = Integer.parseInt(wordList[1]);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //transform transform_id");
					return false;
				}
				activeChar.setTransformation(val);
				break;
		}

		return true;
	}

	private void handleInvul(L2Player activeChar, L2Player target)
	{
		if(target.isInvul())
		{
			target.setIsInvul(false);
			target.stopAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
			if(target.getPet() != null)
			{
				target.getPet().setIsInvul(false);
				target.getPet().stopAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
			}
			activeChar.sendMessage(target.getName() + " is now mortal.");
		}
		else
		{
			target.setIsInvul(true);
			target.startAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
			if(target.getPet() != null)
			{
				target.getPet().setIsInvul(true);
				target.getPet().startAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
			}
			activeChar.sendMessage(target.getName() + " is now immortal.");
		}
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}