package commands.admin;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.skills.Calculator;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.GArray;
import l2open.util.Log;

public class AdminSkill implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_show_skills,
		admin_remove_skills,
		admin_skill_list,
		admin_skill_index,
		admin_add_skill,
		admin_remove_skill,
		admin_get_skills,
		admin_reset_skills,
		admin_give_all_skills,
		admin_show_effects,
		admin_debug_stats,
		admin_remove_cooldown,
		admin_buff,
		admin_callskill,
        admin_remove_all_skills,
		admin_setskill
	}

	private static L2Skill[] adminSkills;

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		switch(command)
		{
			case admin_show_skills:
				showSkillsPage(activeChar);
				break;
			case admin_show_effects:
				showEffects(activeChar);
				break;
			case admin_remove_skills:
				removeSkillsPage(activeChar);
				break;
			case admin_skill_list:
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/skills.htm"));
				break;
			case admin_skill_index:
				if(wordList.length > 1)
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/skills/" + wordList[1] + ".htm"));
				break;
			case admin_add_skill:
				adminAddSkill(activeChar, wordList);
				break;
			case admin_setskill:
				adminAddSkill(activeChar, wordList, false);
				break;
			case admin_remove_skill:
				adminRemoveSkill(activeChar, wordList);
				break;
			case admin_get_skills:
				adminGetSkills(activeChar);
				break;
			case admin_reset_skills:
				adminResetSkills(activeChar);
				break;
			case admin_give_all_skills:
				adminGiveAllSkills(activeChar);
				break;
			case admin_debug_stats:
				debug_stats(activeChar);
				break;
			case admin_remove_cooldown:
				activeChar.resetSkillsReuse();
				activeChar.sendMessage("Откат всех скилов обнулен.");
				break;
			case admin_buff:
				for(int i = 7041; i <= 7064; i++)
					activeChar.addSkill(SkillTable.getInstance().getInfo(i, 1));
				activeChar.sendPacket(new SkillList(activeChar));
				activeChar.updateStats();
				activeChar.sendUserInfo(true);
				break;
			case admin_callskill:
				int skillid;
				int skilllevel;
				if(wordList.length < 3)
				{
					activeChar.sendMessage("USAGE: //callskill skillid skilllevel");
					return false;
				}

				try
				{
					skillid = Integer.parseInt(wordList[1]);
					skilllevel = Integer.parseInt(wordList[2]);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("USAGE: //callskill skillid skilllevel");
					return false;
				}

				L2Skill skill = SkillTable.getInstance().getInfo(skillid, skilllevel);
				if(skill == null)
				{
					activeChar.sendMessage("USAGE: //callskill skillid skilllevel");
					return false;
				}

				L2Character target = null;
				L2Object obj = activeChar.getTarget();
				if(obj != null && obj.isCharacter())
					target = (L2Character) obj;

				if(target == null)
					target = activeChar;

				GArray<L2Character> targets = new GArray<L2Character>();
				targets.add(target);

				activeChar.callSkill(skill, targets, false);
				break;
            case admin_remove_all_skills:
				adminRemoveAllSkills(activeChar);
				break;
		}

		return true;
	}

	private void debug_stats(L2Player activeChar)
	{
		L2Object target_obj = activeChar.getTarget();
		if(!target_obj.isCharacter())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Character target = (L2Character) target_obj;

		Calculator[] calculators = target.getCalculators();

		String log_str = "--- Debug for " + target.getName() + " ---\r\n";

		for(Calculator calculator : calculators)
		{
			if(calculator == null/* || calculator.getBase() == null*/)
				continue;
			Env env = new Env(target, activeChar, null);
			env.value = calculator.getBase() == null ? 1 : calculator.getBase();
			log_str += "Stat: " + calculator._stat.getValue() + ", limit: " + calculator._stat._max + ", prevValue: " + calculator.getLast() + "\r\n";
			Func[] funcs = calculator.getFunctions();
			for(int i = 0; i < funcs.length; i++)
			{
				String order = Integer.toHexString(funcs[i]._order).toUpperCase();
				if(order.length() == 1)
					order = "0" + order;
				log_str += "\tFunc #" + i + "@ [0x" + order + "]" + funcs[i].getClass().getSimpleName() + "\t" + env.value;
				if(funcs[i].getCondition() == null || funcs[i].getCondition().test(env))
					funcs[i].calc(env);
				log_str += " -> " + env.value + (funcs[i]._funcOwner != null ? "; owner: " + funcs[i]._funcOwner.toString() : "; no owner") + "\r\n";
			}
		}

		Log.addMy(log_str, "debug_stats"+(target.isPlayable() ? (target.isPlayer() ? "_player" : "_pet") : "_npc"), target.getName());
	}

	/**
	 * This function will give all the skills that the gm target can have at its
	 * level to the traget
	 *
	 * @param activeChar: the gm char
	 */
	private void adminGiveAllSkills(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player = null;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}
		int unLearnable = 0;
		int skillCounter = 0;
		GArray<L2SkillLearn> skills = player.getAvailableSkills(player.getClassId());
		while(skills.size() > unLearnable)
		{
			unLearnable = 0;
			for(L2SkillLearn s : skills)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.id, s.skillLevel);
				if(sk == null || !sk.getCanLearn(player.getClassId()))
				{
					unLearnable++;
					continue;
				}
				if(player.getSkillLevel(sk.getId()) == -1)
					skillCounter++;
				player.addSkill(sk, true);
				s.deleteSkills(player);
			}
			skills = player.getAvailableSkills(player.getClassId());
		}

		player.updateStats();
		player.sendUserInfo(true);
		player.sendMessage("Admin gave you " + skillCounter + " skills.");
		player.sendPacket(new SkillList(player));
		activeChar.sendMessage("You gave " + skillCounter + " skills to " + player.getName());
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

    public void adminRemoveAllSkills(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player = null;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Skill[] skills = player.getAllSkillsArray();
		for(L2Skill skill : skills)
			player.removeSkill(skill, true, true);
		activeChar.sendMessage("You removed all skills from " + player.getName());
		player.sendMessage("Admin removed all skills from you.");
		player.sendPacket(new SkillList(player));
		player.updateStats();
		player.sendUserInfo(true);
	}

	private void removeSkillsPage(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Skill[] list = player.getAllSkillsArray();

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		/*StringBuilder replyMSG = new StringBuilder("<html><body><center><font color=\"0\">");
		replyMSG.append(target.getName()).append("<br></font></center>");

		if(list != null && !list.isEmpty())
		{
			replyMSG.append("<br><center>Active:</center><br1>");
			replyMSG.append("	<center>");
			replyMSG.append("		<table border=0 cellspacing=4 cellpadding=2 width=295>");
			for(L2Skill s : list)
				if(s.isActive())
				{
					replyMSG.append("			<tr>");
					replyMSG.append("				<td width=36 height=32>");
					replyMSG.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
					replyMSG.append("				</td>");
					replyMSG.append("				<td>");
					replyMSG.append("					<br><font color=LEVEL>"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]</font></font>");
					replyMSG.append("				</td>");
					replyMSG.append("			</tr>");
			}
			replyMSG.append("		</table>");
			replyMSG.append("	</center>");
			// ------------------------------------------------------
			replyMSG.append("<br><center>Passive:</center><br1>");
			replyMSG.append("	<center>");
			replyMSG.append("		<table border=0 cellspacing=4 cellpadding=2 width=295>");
			for(L2Skill s : list)
				if(!s.isActive())
				{
					replyMSG.append("			<tr>");
					replyMSG.append("				<td width=36 height=32>");
					replyMSG.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
					replyMSG.append("				</td>");
					replyMSG.append("				<td>");
					replyMSG.append("					<br><font color=LEVEL>"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]</font></font>");
					replyMSG.append("				</td>");
					replyMSG.append("			</tr>");
			}
			replyMSG.append("		</table>");
			replyMSG.append("	</center>");
		}

		replyMSG.append("</body></html>");*/
		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center>");
		replyMSG.append("<br><table width=270><tr><td>Lv: " + player.getLevel() + " " + player.getTemplate().className + "</td></tr></table>");
		replyMSG.append("<br><center>Click on the skill you wish to remove:</center>");
		
		replyMSG.append("	<center>");
		replyMSG.append("		<br><table border=0 cellspacing=4 cellpadding=2 width=295>");
		//for(L2Skill s : list)
		for(int i=0;i<Math.min(list.length, 40);i++)
		{
			L2Skill s = list[i];
			replyMSG.append("			<tr>");
			replyMSG.append("				<td width=36 height=32>");
			replyMSG.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
			replyMSG.append("				</td>");
			replyMSG.append("				<td>");
			replyMSG.append("					<br><font color=LEVEL><a action=\"bypass -h admin_remove_skill " + s.getId() + "\">"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]</a></font></font>");
			replyMSG.append("				</td>");
			replyMSG.append("			</tr>");
		}
		replyMSG.append("		</table>");
		replyMSG.append("	</center>");

		replyMSG.append("<br><center><table>");
		replyMSG.append("Remove custom skill:");
		replyMSG.append("<tr><td>Id: </td>");
		replyMSG.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		replyMSG.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showSkillsPage(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center>");
		replyMSG.append("<br><table width=270><tr><td>Lv: " + player.getLevel() + " " + player.getTemplate().className + "</td></tr></table>");
		replyMSG.append("<br><center><table>");
		replyMSG.append("<tr><td><button value=\"Add skills\" action=\"bypass -h admin_skill_list\" width=130 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=130 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills\" width=130 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=130 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=130 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Remove All Skill\" action=\"bypass -h admin_remove_all_skills\" width=130 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showEffects(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center>");

		replyMSG.append("<br><center><button value=\"");
		replyMSG.append(player.isLangRus() ? "Обновить" : "Refresh");
		replyMSG.append("\" action=\"bypass -h admin_show_effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");
		replyMSG.append("<br>");

		replyMSG.append("	<center>");
		replyMSG.append("		<table border=0 cellspacing=4 cellpadding=2 width=295>");
		ConcurrentLinkedQueue<L2Skill> list = player.getEffectList().getAllSkills(false);
		if(list != null && !list.isEmpty())
			for(L2Skill s : list)
			{
				replyMSG.append("			<tr>");
				replyMSG.append("				<td width=36 height=32>");
				replyMSG.append("					<img src=\""+s.getIcon()+"\" width=32 height=32>");
				replyMSG.append("				</td>");
				replyMSG.append("				<td>");
				replyMSG.append("					<br><font color=LEVEL>"+s.getName()+" <font color=00ff00>["+s.getId()+":"+s.getDisplayLevel()+"]["+(player.getEffectList().getEffectBySkillId(s.getId()).getTimeLeft()/1000)+"s]</font></font>");
				replyMSG.append("				</td>");
				replyMSG.append("			</tr>");
			}
		replyMSG.append("		</table>");
		replyMSG.append("	</center>");

		replyMSG.append("<br></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void adminGetSkills(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		if(player.getName().equals(activeChar.getName()))
			player.sendMessage("There is no point in doing it on your character.");
		else
		{
			Collection<L2Skill> skills = player.getAllSkills();
			adminSkills = activeChar.getAllSkillsArray();
			for(L2Skill element : adminSkills)
				activeChar.removeSkill(element, true, true);
			for(L2Skill element : skills)
				activeChar.addSkill(element, true);
			activeChar.updateStats();
			activeChar.sendUserInfo(true);
			activeChar.sendMessage("You now have all the skills of  " + player.getName() + ".");
		}

		showSkillsPage(activeChar);
	}

	private void adminResetSkills(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		L2Player player = null;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Skill[] skills = player.getAllSkillsArray();
		int counter = 0;
		for(L2Skill element : skills)
			if(!element.isCommon() && !SkillTreeTable.getInstance().isSkillPossible(player, element.getId(), element.getLevel()))
			{
				player.removeSkill(element, true, true);
				counter++;
			}
		player.checkSkills(10);
		player.sendPacket(new SkillList(player));
		player.updateStats();
		player.sendUserInfo(true);
		player.sendMessage("[GM]" + activeChar.getName() + " has updated your skills.");
		activeChar.sendMessage(counter + " skills removed.");

		showSkillsPage(activeChar);
	}

	private void adminAddSkill(L2Player activeChar, String[] wordList)
	{
		adminAddSkill(activeChar, wordList, true);
	}
	
	private void adminAddSkill(L2Player activeChar, String[] wordList, boolean show)
	{
		L2Object target = activeChar.getTarget();
		L2Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		if(wordList.length == 3)
		{
			int id = Integer.parseInt(wordList[1]);
			int level = Integer.parseInt(wordList[2]);
			L2Skill skill = SkillTable.getInstance().getInfo(id, level > 100 ? 1 : level);
			if(skill != null)
			{
				if(level > 100)
				{
					level = SkillTreeTable.convertEnchantLevel(SkillTable.getInstance().getBaseLevel(id), level, skill.getEnchantLevelCount());
					skill = SkillTable.getInstance().getInfo(id, level);
				}
			
				player.sendMessage("Admin gave you the skill " + skill.getName() + ".");
				player.addSkill(skill, true);
				player.sendPacket(new SkillList(player));
				player.updateStats();
				player.sendUserInfo(true);
				activeChar.sendMessage("You gave the skill " + skill.getName() + " to " + player.getName() + ".");
			}
			else
				activeChar.sendMessage("Error: there is no such skill.");
		}
		if(show)
			showSkillsPage(activeChar);
	}

	private void adminRemoveSkill(L2Player activeChar, String[] wordList)
	{
		L2Object target = activeChar.getTarget();
		L2Player player = null;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		if(wordList.length == 2)
		{
			int id = Integer.parseInt(wordList[1]);
			int level = player.getSkillLevel(id);
			L2Skill skill = SkillTable.getInstance().getInfo(id, level);
			if(skill != null)
			{
				player.sendMessage("Admin removed the skill " + skill.getName() + ".");
				player.removeSkill(skill, true, true);
				player.sendPacket(new SkillList(player));
				player.updateStats();
				player.sendUserInfo(true);
				activeChar.sendMessage("You removed the skill " + skill.getName() + " from " + player.getName() + ".");
			}
			else
				activeChar.sendMessage("Error: there is no such skill.");
		}

		removeSkillsPage(activeChar);
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