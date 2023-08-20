import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Item;
import l2open.util.Util;

public class LD extends Functions implements ScriptFile
{
	public static void OnDie(L2Character self, L2Character killer)
	{
		if(ConfigValue.SetNoobleForKillBarakiel && self.getNpcId() == 25325 && killer.getPlayer() != null)
		{
			L2Player k_player = killer.getPlayer();
			if(k_player.getParty() != null)
			{
				if(k_player.getParty().getCommandChannel() != null)
					for(L2Player member : k_player.getParty().getCommandChannel().getMembers())
						setNooble(member);
				else
					for(L2Player member : k_player.getParty().getPartyMembers())
						setNooble(member);
			}
			else
				setNooble(k_player);
		}
	}

	private static void setNooble(L2Player player)
	{
		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));
		player.broadcastUserInfo(true);
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}