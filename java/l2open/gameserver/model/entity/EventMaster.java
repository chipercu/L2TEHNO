package l2open.gameserver.model.entity;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.tables.MapRegion;
import l2open.util.Location;

public class EventMaster
{
	public Reflection _ref;
	public int flag1id=-1;
	public int flag2id=-1;
	public int state=0;
	public boolean siege_event=false;
	public static SiegeClan _defender_clan = null;

	public boolean Engrave(L2Player player, int objId)
	{
		return false;
	}

	public SiegeClan getSiegeClan(L2Player player)
	{
		return null;
	}

	public void onPlayerExitReflection(L2Player player, Reflection ref)
	{}

	public void onPlayerEnterReflection(L2Player player, Reflection ref)
	{}

	public void onPlayerExit(L2Player player)
	{}

	public void onEscape(L2Player player)
	{}

	public void onTeleportPlayer(L2Player player, int x, int y, int z, int ref)
	{}

	public void captureFlag(L2Player player, L2Character target)
	{}

	public void mountingFlag(L2Player player)
	{}

	public void dropFlag(L2Player player, boolean spawn_loc)
	{}

	public void addFlag(L2Player player, int flagId)
	{}

	public void doDie(L2Character self, L2Character killer)
	{}

	public void doRevive(L2Character player)
	{}

	public void reduceCurrentHp(double damage, L2Player player, L2Character attacker, L2Skill skill)
	{}

	/**
	 * @reflection_name - название инстанса, можно любое.
	 * @core_loc - место, к которому кидает при использовании SoE/unstuck, иначе выбрасывает в основной мир.
	 * @return_loc - если не прописано core, но прописан return, то телепортит туда, одновременно перемещая в основной мир
	 * @teleport_loc - точка входа
	 **/
	public void create_reflection(String reflection_name, Location core_loc, Location return_loc, Location teleport_loc, int time)
	{
		_ref = new Reflection(reflection_name);
		_ref.setCoreLoc(core_loc);
		_ref.setReturnLoc(return_loc);
		_ref.setTeleportLoc(teleport_loc);
		_ref.startCollapseTimer(time*1000);
	}

	public Location doReviveLoc(L2Player player, int type)
	{
		return MapRegion.getTeleToClosestTown(player).setId(0);
	}

	public int getReflection()
	{
		if(_ref == null)
			return 0;
		return _ref.getId();
	}

	// ------------------------------------------------------------------------------------------------
	public int getCharNameColor(L2Player player)
	{
		return player.getNameColor();
	}

	public int getCharTitleColor(L2Player player)
	{
		return player.getTitleColor();
	}

	public String getCharTitle(L2Player player)
	{
		return player.getTitle();
	}

	public String getCharName(L2Character player)
	{
		return player.getName();
	}

	public boolean canUseItem(L2Player player, L2ItemInstance item)
	{
		return true;
	}

	public boolean sendUserModInfo(L2Player player)
	{
		return false;
	}

	public boolean canUseSkill(L2Player player, L2Skill skill)
	{
		return true;
	}

	public boolean buffAnotherTeam()
	{
		return true;
	}

	public int getEnchantLevel(L2Player player, L2ItemInstance item)
	{
		return item._enchantLevel;
	}
	// ------------------------------------------------------------------------------------------------
	public String getBbsIndex(L2Player player, String command)
	{
		return null;
	}

	public long getOfflineTime(long time)
	{
		return time;
	}

	public boolean canPickupItem(L2Player player, L2ItemInstance item)
	{
		return true;
	}

	public boolean blockChat(L2Player player, int type)
	{
		return false;
	}

	public int getPaperdollItemId(L2Player player, int slot, boolean is_visual_id)
	{
		L2ItemInstance item = player.getInventory().getPaperdollItems()[slot];
		if(item != null)
			return is_visual_id && item.getVisualItemId() > 0 ? item.getVisualItemId() : item.getItemId();
		else if(slot == Inventory.PAPERDOLL_HAIR)
		{
			item = player.getInventory().getPaperdollItems()[Inventory.PAPERDOLL_DHAIR];
			if(item != null)
				return is_visual_id && item.getVisualItemId() > 0 ? item.getVisualItemId() : item.getItemId();
		}
		return 0;
	}

	public boolean sendVisualTeam(L2Player player)
	{
		return true;
	}

	public boolean canJoinClan(L2Player player)
	{
		if(player.getLeaveClanTime() == 0)
			return true;
		if(System.currentTimeMillis() - player.getLeaveClanTime() >= ConfigValue.EXPELLED_PLAYER_PENALTY * 1000L)
		{
			player.setLeaveClanTime(0);
			return true;
		}
		return false;
	}

	public boolean setWear(L2Player player)
	{
		return false;
	}

	public boolean notShowLockItems(L2Player player)
	{
		return false;
	}

	public boolean blockBbs()
	{
		return false;
	}

	public boolean blockNpcBypass()
	{
		return false;
	}

	public boolean attackFirst(L2Player player)
	{
		return false;
	}

	public boolean isClanWarIcon()
	{
		return true;
	}

	public byte getHeroAura(L2Player player)
	{
		return player.isHero() || player.isGM() && ConfigValue.GMHeroAura ? (byte) 1 : (byte) 0;
	}

	public int getClanId(L2Player player)
	{
		return player.getClanId();
	}

	public int getClanCrestId(L2Player player)
	{
		return player.getClan() == null ? 0 : player.getClan().getCrestId();
	}

	public int getClanCrestLargeId(L2Player player)
	{
		return player.getClan() == null ? 0 : player.getClan().getCrestLargeId();
	}

	public int getAllyCrestId(L2Player player)
	{
		return player.getAlliance() == null ? 0 : player.getAlliance().getAllyCrestId();
	}

	public void dissolveParty(L2Party party)
	{}

	public void addPartyMember(L2Party party, L2Player player)
	{}

	public void removePartyMember(L2Party party, L2Player player)
	{}

	public void sendStatusUpdate(StatusUpdate s_u, L2Player player)
	{}

	public void updateEffectIcons(L2Player player)
	{}

	public boolean tutorialLinkHtml(L2Player player, String bypass)
	{
		return false;
	}

	public boolean useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl)
	{
		return false;
	}
}