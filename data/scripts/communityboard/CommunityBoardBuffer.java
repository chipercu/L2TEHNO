package communityboard;

import gnu.trove.list.array.TIntArrayList;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import l2open.gameserver.cache.Msg;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.Buff;
import l2open.gameserver.common.BuffScheme;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.common.GenerateElement;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.ArrayUtils;
import l2open.util.EffectsComparator;
import l2open.util.Files;
import l2open.util.Util;

public class CommunityBoardBuffer extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	private static final TIntArrayList allowBuff = new TIntArrayList(ConfigValue.BufferBuffs); // ConfigValue.BufferOnlyPaBuffs
	private static final TIntArrayList allow_pr_Buff = new TIntArrayList(ConfigValue.BufferBuffsPremium);
	private static final TIntArrayList allow_pr_Buff2 = new TIntArrayList(ConfigValue.BufferBuffs2Premium);
	private static ArrayList<String> player_buff_page;
	private static ArrayList<String> player_premium_buff_page;
	private static ArrayList<String> player_premium_buff2_page;
	private static ArrayList<String> pet_buff_page;
	public static StringBuilder buffSchemes = new StringBuilder();

	private static enum Commands
	{
		_bbsbuffer,
		_bbsplayerbuffer,
		_bbsplayerprbuffer,
		_bbsplayer_pr_buffer,
		_bbsplayerpr2buffer,
		_bbspetbuffer,
		_bbscastbuff,
		_bbscastpr2buff,
		_bbscastgroupbuff,
		_bbsbuffersave,
		_bbsbufferuse,
		_bbsbufferdelete,
		_bbsbufferheal,
		_bbsbufferremovebuffs
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block)
			return;
		if(!check(player))
			return;

		if(ConfigValue.BufferAffterRes)
		{
			long time = (player.getResTime() + (ConfigValue.BufferAffterResTime * 1000) - System.currentTimeMillis());
			if(time > 0)
			{
				int wait = (int) (time / 1000);
				player.sendMessage(new CustomMessage("common.not.yet.wait", player).addNumber(wait <= 0 ? 1 : wait).addString(DifferentMethods.declension(player, wait, "Second")));
				return;
			}
		}

		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/scheme.htm", player);
		if("bbsbuffer".equals(cmd))
		{
			html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/index.htm", player);
			if(html != null)
			{
				html = html.replace("%scheme%", buffSchemes.toString());
				html = html.replace("%buffgrps%", showBuffList(player));
				
			}
		}
		else if(bypass.startsWith("_bbsplayerbuffer"))
		{
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			int page = Integer.parseInt(st1.nextToken());
			if(player_buff_page.get(page) != null)
				html = html.replace("%content%", player_buff_page.get(page));
		}
		else if(bypass.startsWith("_bbsplayerprbuffer") || bypass.startsWith("_bbsplayer_pr_buffer"))
		{
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			int page = Integer.parseInt(st1.nextToken());
			if(player_premium_buff_page.get(page) != null)
				html = html.replace("%content%", player_premium_buff_page.get(page));
		}
		else if(bypass.startsWith("_bbsplayerpr2buffer"))
		{
			if(!canBuff(player))
			{
				if(ConfigValue.PremiumBufferEnable)
				{
					html = html.replace("%premium_info%", new CustomMessage("PremiumBufferNoActive", player).toString());
				}
				else
				{
					if(ConfigValue.BufferUsePremiumItem > 0)
						player.sendMessage(new CustomMessage("BufferUsePremiumItem.No", player));
					else
					{
						String content = readHtml(ConfigValue.CommunityBoardHtmlRoot + "buffer/no_pa.htm", player);
						NpcHtmlMessage html2 = new NpcHtmlMessage(player, null);
						html2.setHtml(content);
						player.sendPacket(html2);
					}
					return;
				}
			}

			html = html.replace("%premium_info%", new CustomMessage("PremiumBufferActive", player).addString("").toString());
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			int page = st1.hasMoreTokens() ? Integer.parseInt(st1.nextToken()) : 0;
			if(player_premium_buff2_page.get(page) != null)
				html = html.replace("%content%", player_premium_buff2_page.get(page));
		}
		else if(bypass.startsWith("_bbspetbuffer"))
		{
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();
			int page = Integer.parseInt(st1.nextToken());
			if(pet_buff_page.get(page) != null)
			{
				html = html.replace("%content%", pet_buff_page.get(page));
			}
		}
		else if(bypass.startsWith("_bbscastpr2buff"))
		{
		
			if(!canBuff(player))
			{
				if(ConfigValue.PremiumBufferEnable)
				{
					player.sendPacket(Msg.THERE_IS_A_SIGNIFICANT_DIFFERENCE_BETWEEN_THE_ITEMS_PRICE_AND_ITS_STANDARD_PRICE_PLEASE_CHECK_AGAIN);
					html = html.replace("%premium_info%", new CustomMessage("PremiumBufferNoActive", player).toString());
				}
				else
				{
					if(ConfigValue.BufferUsePremiumItem > 0)
						player.sendMessage(new CustomMessage("BufferUsePremiumItem.No", player));
				}
				return;
			}

			html = html.replace("%premium_info%", new CustomMessage("PremiumBufferActive", player).addString("").toString());
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();

			int id = Integer.parseInt(st1.nextToken());
			int level = Integer.parseInt(st1.nextToken());
			int page = Integer.parseInt(st1.nextToken());
			String type = "Player";
			if(st1.hasMoreTokens())
				type = st1.nextToken();

			L2Playable playable = null;
			if("Player".equals(type))
				playable = player;
			else if("Pet".equals(type))
				playable = player.getPet();

			int check = allow_pr_Buff2.indexOf(id);
			if(playable != null && check != -1 && allow_pr_Buff2.get(check + 1) == level && (!Util.contains(ConfigValue.BufferOnlyPaBuffs, id) || canBuff(player)))
			{
				buff(id, level, playable);
				playable.updateEffectIcons();
			}

			if(page > -1 && "Player".equals(type))
				html = html.replace("%content%", player_premium_buff2_page.get(page));
		}
		else if(bypass.startsWith("_bbscastbuff"))
		{
			StringTokenizer st1 = new StringTokenizer(bypass, ":");
			st1.nextToken();

			int id = Integer.parseInt(st1.nextToken());
			int level = Integer.parseInt(st1.nextToken());
			int page = Integer.parseInt(st1.nextToken());
			String type = "Player";
			if(st1.hasMoreTokens())
				type = st1.nextToken();

			L2Playable playable = null;
			if("Player".equals(type))
				playable = player;
			else if("Pet".equals(type))
				playable = player.getPet();

			boolean premium=false;
			int check = allow_pr_Buff.indexOf(id);
			if(playable != null && check != -1 && allow_pr_Buff.get(check + 1) == level)
			{
				if((!Util.contains(ConfigValue.BufferOnlyPaBuffs, id) || canBuff(player)) && DifferentMethods.getPay(player, ConfigValue.BufferPremiumItem, ConfigValue.BufferPremiumPriceOne, true))
				{
					buff(id, level, playable);
					playable.updateEffectIcons();
				}
				premium=true;
			}
			else
			{
				check = allowBuff.indexOf(id);

				if(playable != null && check != -1 && allowBuff.get(check + 1) <= level && (!Util.contains(ConfigValue.BufferOnlyPaBuffs, id) || canBuff(player)))
				{
					if(player.getActiveClass().getLevel() < ConfigValue.BufferFreeLevel)
					{
						buff(id, level, playable);
						playable.updateEffectIcons();
					}
					else if(DifferentMethods.getPay(player, ConfigValue.BufferItem, ConfigValue.BufferPriceOne, true))
					{
						buff(id, level, playable);
						playable.updateEffectIcons();
					}
				}
			}

			if(page > -1 || premium)
			{
				if("Player".equals(type) && premium)
					html = html.replace("%content%", player_premium_buff_page.get(page));
				else if("Player".equals(type))
					html = html.replace("%content%", player_buff_page.get(page));
				else if("Pet".equals(type))
					html = html.replace("%content%", pet_buff_page.get(page));
			}
		}
		else if(bypass.startsWith("_bbscastgroupbuff"))
		{
			StringTokenizer st1 = new StringTokenizer(bypass, " ");
			st1.nextToken();
			int id = Integer.parseInt(st1.nextToken());
			int priceId = BuffScheme.buffSchemes.get(id).getPriceId();
			int priceCount = BuffScheme.buffSchemes.get(id).getPriceCount();
			String type = "Player";
			if(st1.hasMoreTokens())
				type = st1.nextToken();

			L2Playable playable = null;
			if("Player".equals(type))
				playable = player;
			else if("Pet".equals(type))
				playable = player.getPet();

			if(playable != null)
			{
				if(player.getActiveClass().getLevel() < ConfigValue.BufferFreeLevel)
				{
					playable.setMassUpdating(true);
					for(Buff buffId : BuffScheme.buffSchemes.get(id).getBuffIds())
					{
						if(Util.contains(ConfigValue.BufferOnlyPaBuffs, id) && !canBuff(player))
							continue;
						buff(buffId.getId(), buffId.getLevel(), playable);
					}
					playable.setMassUpdating(false);
					playable.updateEffectIcons();
				}
				else if(DifferentMethods.getPay(player, priceId, priceCount, true))
				{
					playable.setMassUpdating(true);
					for(Buff buffId : BuffScheme.buffSchemes.get(id).getBuffIds())
					{
						if(Util.contains(ConfigValue.BufferOnlyPaBuffs, id) && !canBuff(player))
							continue;
						buff(buffId.getId(), buffId.getLevel(), playable);
					}
					playable.setMassUpdating(false);
					playable.updateEffectIcons();
				}
			}

			DifferentMethods.communityNextPage(player, "_bbsbuffer");
			return;
		}
		else if(bypass.startsWith("_bbsbuffersave"))
		{
			if(player._buffSchem.size() >= ConfigValue.BufferSaveMax)
			{
				player.sendMessage("Можно сохранять не более " + ConfigValue.BufferSaveMax + " схем.");
				DifferentMethods.communityNextPage(player, "_bbsbuffer");
				return;
			}

			StringTokenizer st1 = new StringTokenizer(bypass, " ");

			if(st1.countTokens() < 3)
			{
				DifferentMethods.communityNextPage(player, "_bbsbuffer");
				return;
			}

			st1.nextToken();

			String name = st1.nextToken().trim();
			String type = "Player";
			if(st1.hasMoreTokens())
				type = st1.nextToken();

			L2Playable playable = null;
			if("Player".equals(type))
				playable = player;
			else if("Pet".equals(type))
				playable = player.getPet();

			if(playable == null)
				return;

			if(playable.getEffectList().getAllEffects().size() == 0)
			{
				DifferentMethods.communityNextPage(player, "_bbsbuffer");
				return;
			}

			if(DifferentMethods.getPay(player, ConfigValue.BufferSaveItem, ConfigValue.BufferSavePrice, true))
				SAVE(player, name);
			DifferentMethods.communityNextPage(player, "_bbsbuffer");
			return;
		}
		else if(bypass.startsWith("_bbsbufferuse"))
		{
			StringTokenizer st1 = new StringTokenizer(bypass, " ");
			st1.nextToken();
			int schameId = Integer.parseInt(st1.nextToken());
			String type = "Player";
			if(st1.hasMoreTokens())
				type = st1.nextToken();

			L2Playable playable = null;
			if("Player".equals(type))
				playable = player;
			else if("Pet".equals(type))
				playable = player.getPet();
			if(playable != null)
			{
				playable.setMassUpdating(true);
				if(player._buffSchem != null && player._buffSchem.containsKey(schameId))
				{
					long price_mod=0;
					if(player.getActiveClass().getLevel() < ConfigValue.BufferFreeLevel || player.getInventory().getCountOf(ConfigValue.BufferItem) >= (ConfigValue.BufferPriceOne*player._buffSchem.get(schameId)._buffList.length))
						for(long ptsId : player._buffSchem.get(schameId)._buffList)
						{
							int skillId = 0;
							int level = 0;
							if(ptsId > 65536)
							{
								skillId = (int) (ptsId / 65536);
								level = (int) (ptsId - skillId * 65536);
							}
							else
								// Для совместимости со старыми схемами.
								skillId = (int) ptsId;

							int lvl = SkillTable.getInstance().getBaseLevel(skillId);
							if(level == 0)
								level = lvl;

							// не ресторим премиум баф
							int check = allow_pr_Buff.indexOf(skillId);
							if(check != -1 && allow_pr_Buff.get(check + 1) == level)
								continue;

							check = allow_pr_Buff2.indexOf(skillId);
							if(check != -1 && allow_pr_Buff2.get(check + 1) == level)
							{
								if(canBuff(player))
									buff(skillId, level, playable);
								continue;
							}

							if(lvl < level)
								level = lvl;

							if(allowBuff.indexOf(skillId) != -1 && (!Util.contains(ConfigValue.BufferOnlyPaBuffs, skillId) || canBuff(player)))
							{
								buff(skillId, level, playable);
								price_mod++;
							}
						}
					if(player.getActiveClass().getLevel() >= ConfigValue.BufferFreeLevel)
						DifferentMethods.getPay(player, ConfigValue.BufferItem, ConfigValue.BufferPriceOne*price_mod, false);
				}
				playable.setMassUpdating(false);
				playable.updateEffectIcons();
			}
			DifferentMethods.communityNextPage(player, "_bbsbuffer");
			return;
		}
		else if(bypass.startsWith("_bbsbufferdelete"))
		{
			try
			{
				StringTokenizer st1 = new StringTokenizer(bypass, " ");
				st1.nextToken();
				int schameId = Integer.parseInt(st1.nextToken());
				delschame(player, schameId);
				DifferentMethods.communityNextPage(player, "_bbsbuffer");
				return;
			}
			catch(Exception e)
			{}
		}
		else if(bypass.startsWith("_bbsbufferheal"))
		{
			if(player.isInEvent() == 5)
				return;
			StringTokenizer st1 = new StringTokenizer(bypass, " ");
			st1.nextToken();
			String type = st1.nextToken();
			String target = "Player";
			if(st1.hasMoreTokens())
				target = st1.nextToken();

			L2Playable playable = null;
			if("Player".equals(target))
				playable = player;
			else if("Pet".equals(target))
				playable = player.getPet();

			if(playable != null)
			{
				if(ConfigValue.BufferRestoreOnlyPeace && !player.isInZone(ZoneType.peace_zone))
				{
					player.sendMessage("Функция доступна только в мирной зоне.");
					return;
				}
				if("HP".equals(type))
				{
					if(ConfigValue.BufferRecovery[0] == 0)
						player.sendMessage(new CustomMessage("scripts.services.off", player));
					else
					{
						if(playable.getCurrentHp() != playable.getMaxHp())
						{
							playable.setCurrentHp(playable.getMaxHp(), false);
							playable.broadcastSkill(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0), true);
						}
					}
				}
				else if("MP".equals(type))
				{
					if(ConfigValue.BufferRecovery[1] == 0)
						player.sendMessage(new CustomMessage("scripts.services.off", player));
					else
					{
						if(playable.getCurrentMp() != playable.getMaxMp())
						{
							playable.setCurrentMp(playable.getMaxMp());
							playable.broadcastSkill(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0), true);
						}
					}
				}
				else if("CP".equals(type))
				{
					if(ConfigValue.BufferRecovery[2] == 0)
						player.sendMessage(new CustomMessage("scripts.services.off", player));
					else
					{
						if(playable.getCurrentCp() != playable.getMaxCp())
						{
							playable.setCurrentCp(playable.getMaxCp());
							playable.broadcastSkill(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0), true);
						}
					}
				}
				else if("ALL".equals(type))
				{
					if(ConfigValue.BufferRecovery[3] == 0)
						player.sendMessage(new CustomMessage("scripts.services.off", player));
					else
					{
						if(playable.getCurrentCp() != playable.getMaxCp() || playable.getCurrentHp() != playable.getMaxHp() || playable.getCurrentMp() != playable.getMaxMp())
						{
							playable.setCurrentCp(playable.getMaxCp());
							playable.setCurrentHp(playable.getMaxHp(), false);
							playable.setCurrentMp(playable.getMaxMp());
							playable.broadcastSkill(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0), true);
						}
					}
				}
				else
					DifferentMethods.clear(player);
			}
			DifferentMethods.communityNextPage(player, "_bbsbuffer");
			return;
		}
		else if(bypass.startsWith("_bbsbufferremovebuffs"))
		{
			if(!ConfigValue.BufferClear)
				player.sendMessage(new CustomMessage("scripts.services.off", player));
			else
			{
				StringTokenizer st1 = new StringTokenizer(bypass, " ");
				st1.nextToken();
				String type = "Player";
				if(st1.hasMoreTokens())
					type = st1.nextToken();
				L2Playable playable = null;
				if("Player".equals(type))
					playable = player;
				else if("Pet".equals(type))
					playable = player.getPet();

				if(playable != null)
				{
					if(playable.getEffectList().getEffectsBySkillId(L2Skill.SKILL_RAID_CURSE) == null && playable.getEffectList().getEffectsBySkillId(5076) == null)
					{
						for(L2Effect e : playable.getEffectList().getAllCancelableEffects(0))
							if(e != null)
								e.exit(false, false);
						playable.setMassUpdating(false);
						playable.sendChanges();
						playable.updateEffectIcons();
					}
					playable.broadcastSkill(new MagicSkillUse(playable, playable, 6696, 1, 1000, 0), true);
				}
			}
			DifferentMethods.communityNextPage(player, "_bbsbuffer");
			return;
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, bypass), player);

		html = html.replace("%premium_info%", "");
		ShowBoard.separateAndSend(addCustomReplace(html), player);
	}

	private static void genPage(ArrayList<String> list, String type)
	{
		StringBuilder sb = new StringBuilder("<table><tr>");
		sb.append("<td width=70>Навигация: </td>");

		for(int i = 0; i < list.size(); i++)
			sb.append(GenerateElement.buttonTD(String.valueOf(i + 1), "_bbs" + type + "buffer:" + i, 25, 25, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF"));

		sb.append("<td>" + GenerateElement.button("Назад", "_bbsbuffer", 60, 25, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF") + "</td></tr></table><br><br>");

		for(int i = 0; i < list.size(); i++)
			list.set(i, sb.toString() + list.get(i));
	}

	private static void genPageBuff(ArrayList<String> list, int start, String type)
	{
		StringBuilder buffPages = new StringBuilder("<table><tr>");
		int i = start;
		Boolean next = false;
		for(; i < allowBuff.size(); i += 2)
		{
			if(next && i % 12 == 0)
				buffPages.append("</tr><tr>");
			if(next && i % (12 * 4) == 0)
				break;
			buffPages.append("<td>").append(buttonBuff(allowBuff.get(i), allowBuff.get(i + 1), list.size(), type, false)).append("</td>");
			next = true;
		}
		buffPages.append("</tr></table>");

		list.add(buffPages.toString());

		if(i + 2 <= allowBuff.size())
			genPageBuff(list, i, type);
	}

	private static void genPageBuff2(ArrayList<String> list, int start, String type)
	{
		StringBuilder buffPages = new StringBuilder("<table><tr>");
		int i = start;
		Boolean next = false;
		for(; i < allow_pr_Buff.size(); i += 2)
		{
			if(next && i % 12 == 0)
				buffPages.append("</tr><tr>");
			if(next && i % (12 * 4) == 0)
				break;
			buffPages.append("<td>").append(buttonBuff(allow_pr_Buff.get(i), allow_pr_Buff.get(i + 1), list.size(), type, false)).append("</td>");
			next = true;
		}
		buffPages.append("</tr></table>");

		list.add(buffPages.toString());

		if(i + 2 <= allow_pr_Buff.size())
			genPageBuff2(list, i, type);
	}

	private static void genPageBuff3(ArrayList<String> list, int start, String type)
	{
		StringBuilder buffPages = new StringBuilder("<table><tr>");
		int i = start;
		Boolean next = false;
		for(; i < allow_pr_Buff2.size(); i += 2)
		{
			if(next && i % 12 == 0)
				buffPages.append("</tr><tr>");
			if(next && i % (12 * 4) == 0)
				break;
			buffPages.append("<td>").append(buttonBuff(allow_pr_Buff2.get(i), allow_pr_Buff2.get(i + 1), list.size(), type, true)).append("</td>");
			next = true;
		}
		buffPages.append("</tr></table>");

		list.add(buffPages.toString());

		if(i + 2 <= allow_pr_Buff2.size())
			genPageBuff3(list, i, type);
	}

	private static String buttonBuff(int id, int level, int page, String type, boolean is_premium)
	{
		String skillId = Integer.toString(id);
		StringBuilder sb = new StringBuilder("<table width=100>");

		L2Skill skill = SkillTable.getInstance().getInfo(id, level > 100 ? 1 : level);
		int buff_level=level;
		if(level > 100)
		{
			level = SkillTreeTable.convertEnchantLevel(SkillTable.getInstance().getBaseLevel(id), level, skill.getEnchantLevelCount());
			skill = SkillTable.getInstance().getInfo(id, level);
		}

		String name = skill.getName();
		name = name.replace("Dance of the", "D.");
		name = name.replace("Dance of", "D.");
		name = name.replace("Song of", "S.");
		name = name.replace("Improved", "I.");
		name = name.replace("Awakening", "A.");
		name = name.replace("Blessing", "Bless.");
		name = name.replace("Protection", "Protect.");
		name = name.replace("Critical", "C.");
		name = name.replace("Condition", "Con.");
		sb.append("<tr><td><center><img src=").append(skill.getIcon()).append(" width=32 height=32><br><font color=F2C202>Level ").append(buff_level).append("</font></center></td></tr>");
		sb.append(GenerateElement.buttonTR(name, (is_premium ? "_bbscastpr2buff:" : "_bbscastbuff:") + id + ":" + buff_level + ":" + page + ":" + type, 100, 25, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF"));
		sb.append("</table>");
		return sb.toString();
	}

	private static void buff(int id, int level, L2Playable playable)
	{
		if(id < 20)
			return;

		if(playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel))
		{
			playable.sendMessage("Баффер доступен для игроков с уровней не ниже " + ConfigValue.BufferMinLevel + " и не выше " + ConfigValue.BufferMaxLevel + ".");
			return;
		}

		final double hp = playable.getCurrentHp();
		final double mp = playable.getCurrentMp();
		final double cp = playable.getCurrentCp();
		int buff_level = level > 0 ? level : SkillTable.getInstance().getBaseLevel(id);
		L2Skill skill = SkillTable.getInstance().getInfo(id, buff_level > 100 ? 1 : buff_level);
		if(buff_level > 100)
		{
			buff_level = SkillTreeTable.convertEnchantLevel(SkillTable.getInstance().getBaseLevel(id), buff_level, skill.getEnchantLevelCount());
			skill = SkillTable.getInstance().getInfo(id, buff_level);
		}

		if(!skill.checkSkillAbnormal(playable) && !skill.isBlockedByChar(playable, skill))
		{
			for(EffectTemplate et : skill.getEffectTemplates())
			{
				int result;
				Env env = new Env(playable, playable, skill);
				L2Effect effect = et.getEffect(env);
				if(effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle())
				{
					// Эффекты однократного действия не шедулятся, а применяются немедленно
					// Как правило это побочные эффекты для скиллов моментального действия
					effect.onStart();
					effect.onActionTime();
					effect.onExit();
				}
				else if(effect != null && !effect.getEffected().p_block_buff.get())
				{
					if(ConfigValue.BufferTime > 0)
						effect.setPeriod(ConfigValue.BufferTime * 60000);
					if((result = playable.getEffectList().addEffect(effect)) > 0)
					{
						if((result & 2) == 2)
							playable.setCurrentHp(hp, false);
						if((result & 4) == 4)
							playable.setCurrentMp(mp);
						if((result & 8) == 8)
							playable.setCurrentCp(cp);
					}
				}
			}
		}
		//skill.getEffects(playable, playable, false, false, ConfigValue.BBS_BUFFER_ALT_TIME * 60000, 0, false);
	}

	private void delschame(L2Player player, int shameid)
	{
		player._buffSchem.remove(shameid);
		ThreadConnection conDel = null;
		FiltredPreparedStatement statementDel = null;
		try
		{
			conDel = L2DatabaseFactory.getInstance().getConnection();
			statementDel = conDel.prepareStatement("DELETE FROM community_skillsave WHERE charId=? AND schameid=?");
			statementDel.setInt(1, player.getObjectId());
			statementDel.setInt(2, shameid);
			statementDel.execute();
		}
		catch(Exception e)
		{
			_log.warning("data error on Delete Teleport: " + e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeConnection(conDel);
		}
	}

	private void SAVE(L2Player player, String SchName)
	{
		if(!Util.isMatchingRegexp(SchName, "([0-9A-Za-z]{1,16})|([0-9\u0410-\u044f]{1,16})"))
		{
			player.sendMessage("Символы запрещены.");
			return;
		}

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			String allbuff = "";
			L2Effect skill[] = player.getEffectList().getAllFirstEffects();
			Arrays.sort(skill, EffectsComparator.getInstance());

			long[] sch = new long[0];
			boolean _name = true;
			int id;
			int level;
			long ptsId;
			int check;

			for(int i = 0; i < skill.length; i++)
			{
				
				id = skill[i].getSkill().getId();
				level = skill[i].getSkill().getLevel();
				
				// не сейвим премиум баф
				check = allow_pr_Buff.indexOf(id);
				if(check != -1 && allow_pr_Buff.get(check + 1) == level)
					continue;

				check = allow_pr_Buff2.indexOf(id);
				if(check != -1 && allow_pr_Buff2.get(check + 1) == level && canBuff(player))
				{
					//_log.info("CommunityBoardBuffer->: SAVE: ["+id+"]["+level+"]");
					ptsId = id * 65536 + level;
					allbuff = new StringBuilder().append(allbuff).append(ptsId + ";").toString();
					sch = ArrayUtils.add(sch, ptsId);
					continue;
				}

				check = allowBuff.indexOf(id);
				level = level > allowBuff.get(check + 1) ? allowBuff.get(check + 1) : level;

				if(check != -1)
				{
					ptsId = id * 65536 + level;
					allbuff = new StringBuilder().append(allbuff).append(ptsId + ";").toString();
					sch = ArrayUtils.add(sch, ptsId);
				}
			}

			for(CBBuffSch sch1 : player._buffSchem.values())
				if(sch1.SchName.equalsIgnoreCase(SchName))
					_name = false;
			if(_name)
			{
				statement = con.prepareStatement("INSERT INTO community_skillsave (charId,name,skills) VALUES(?,?,?)");
				statement.setInt(1, player.getObjectId());
				statement.setString(2, SchName);
				statement.setString(3, allbuff);
				statement.execute();
				DatabaseUtils.closeStatement(statement);

				statement = con.prepareStatement("SELECT schameid FROM community_skillsave WHERE charId=? AND name=?;");
				statement.setInt(1, player.getObjectId());
				statement.setString(2, SchName);
				rs = statement.executeQuery();
				rs.next();
				id = rs.getInt(1);
				player._buffSchem.put(id, new CBBuffSch(id, SchName, sch));
				sch = null;
			}
			else
				player.sendMessage("Это название уже занято.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rs);
		}
	}

	private String showBuffList(L2Player player)
	{
		StringBuilder html = new StringBuilder();
		html.append("<table>");
		for(CBBuffSch sch : player._buffSchem.values())
		{
			html.append("<tr>");
			html.append("<td valign=\"top\" align=\"center\">");
			html.append(GenerateElement.button(sch.SchName, "_bbsbufferuse " + sch.id + " $Who", 150, 25, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF"));
			html.append("</td>");
			html.append("<td valign=\"top\" align=\"center\"><table><tr><td></td></tr></table>");
			html.append(GenerateElement.button(" ", "_bbsbufferdelete " + sch.id, 15, 15, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete"));
			html.append("</td>");
			html.append("</tr>");
		}
		html.append("</table>");
		return html.toString();
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
		player_buff_page = new ArrayList<String>();
		player_premium_buff_page = new ArrayList<String>();
		player_premium_buff2_page = new ArrayList<String>();
		pet_buff_page = new ArrayList<String>();
		buffSchemes = new StringBuilder();

		genPageBuff(player_buff_page, 0, "Player");
		genPage(player_buff_page, "player");

		genPageBuff2(player_premium_buff_page, 0, "Player");
		genPage(player_premium_buff_page, "playerpr");

		genPageBuff3(player_premium_buff2_page, 0, "Player");
		genPage(player_premium_buff2_page, "playerpr2");

		genPageBuff(pet_buff_page, 0, "Pet");
		genPage(pet_buff_page, "pet");

		BuffScheme.load();

		for(int id : BuffScheme.buffSchemes.keySet())
		{
			StringBuilder parametrs = new StringBuilder();
			parametrs.append("_bbscastgroupbuff ").append(id).append(" $Who");
			StringBuilder name = new StringBuilder();
			name.append(BuffScheme.buffSchemes.get(id).getName());

			// затычка...мне похую...
			long price = BuffScheme.buffSchemes.get(id).getPriceCount();
			if(price > 0)
				buffSchemes.append(GenerateElement.button(name.toString(), parametrs.toString(), 169, 25, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF") + "<br1>" + Util.formatAdena(price) + " " + DifferentMethods.getItemName(BuffScheme.buffSchemes.get(id).getPriceId()));
			else
				buffSchemes.append(GenerateElement.button(name.toString(), parametrs.toString(), 169, 25, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF"));
		}
	}

	/**
	 * <b>1</b> - <font color=red>Fight Club</font><br> 
	 * <b>2</b> - <font color=red>Last Hero</font><br> 
	 * <b>3</b> - <font color=red>Capture The Flag</font><br> 
	 * <b>4</b> - <font color=red>Team vs Team</font><br>
	 * <b>5</b> - <font color=red>Tournament</font><br>
	 **/
	private boolean check_event(L2Player player)
	{
		switch(player.isInEvent())
		{
			case 1:
				return ConfigValue.FightClubBattleUseBuffer;
			case 2:
				return ConfigValue.LastHeroBattleUseBuffer;
			case 3:
				return ConfigValue.CaptureTheFlagBattleUseBuffer;
			case 4:
				return ConfigValue.TeamvsTeamBattleUseBuffer;
			case 5:
				return ConfigValue.TournamentBattleUseBuffer;
			case 6:
				return ConfigValue.EventBoxUseBuffer;
			case 11:
				return player.getEventMaster().state == 1;
			case 12:
				return ConfigValue.Tournament_UseBuffer;
			case 13:
				return ConfigValue.DeathMatchUseBuffer;
		}
		return false;
	}

	private boolean check(L2Player player)
	{
		if(player == null)
			return false;
		else if(player.isGM())
			return true;
		else if(player.isInOlympiadMode())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.getReflection().getId() != ReflectionTable.DEFAULT && !ConfigValue.BufferInInstance && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInDuel())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInCombat() && !ConfigValue.BufferInCombat)
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if((player.isOnSiegeField() || player.isInZoneBattle()) && !ConfigValue.BufferOnSiege && player.isInEvent() != 5 && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInEvent() > 0 && !check_event(player))
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isFlying())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isInWater() && !ConfigValue.BufferInWater)
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(player.isDead() || player.isMovementDisabled() || player.isAlikeDead() || player.isCastingNow() || player.isAttackingNow() || player.getVar("jailed") != null || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
		{
			player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
			return false;
		}
		else if(ConfigValue.BufferOnlyPeace && !player.isInZone(ZoneType.peace_zone) && !player.isInZone(ZoneType.epic) && player.getReflection().getId() == ReflectionTable.DEFAULT)
		{
			player.sendMessage("Функция доступна только в мирной зоне, эпик зоне, а так же в инстансах.");
			return false;
		}
		else
			return true;
	}

	private boolean canBuff(L2Player player)
	{
		return ConfigValue.PremiumBufferEnable ? player.getBonus().PremiumBuffer : ConfigValue.BufferUsePremiumItem <= 0 && player.hasBonus() || ConfigValue.BufferUsePremiumItem > 0 && player.getInventory().getCountOf(ConfigValue.BufferUsePremiumItem) > 0;
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}