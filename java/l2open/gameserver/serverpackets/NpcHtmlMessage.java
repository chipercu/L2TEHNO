package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.cache.ImagesChache;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Strings;
import l2open.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the HTML parser in the client knowns these standard and non-standard tags and attributes
 * VOLUMN
 * UNKNOWN
 * UL
 * U
 * TT
 * TR
 * TITLE
 * TEXTCODE
 * TEXTAREA
 * TD
 * TABLE
 * SUP
 * SUB
 * STRIKE
 * SPIN
 * SELECT
 * RIGHT
 * PRE
 * P
 * OPTION
 * OL
 * MULTIEDIT
 * LI
 * LEFT
 * INPUT
 * IMG
 * I
 * HTML
 * H7
 * H6
 * H5
 * H4
 * H3
 * H2
 * H1
 * FONT
 * EXTEND
 * EDIT
 * COMMENT
 * COMBOBOX
 * CENTER
 * BUTTON
 * BR
 * BODY
 * BAR
 * ADDRESS
 * A
 * SEL
 * LIST
 * VAR
 * FORE
 * READONL
 * ROWS
 * VALIGN
 * FIXWIDTH
 * BORDERCOLORLI
 * BORDERCOLORDA
 * BORDERCOLOR
 * BORDER
 * BGCOLOR
 * BACKGROUND
 * ALIGN
 * VALU
 * READONLY
 * MULTIPLE
 * SELECTED
 * TYP
 * TYPE
 * MAXLENGTH
 * CHECKED
 * SRC
 * Y
 * X
 * QUERYDELAY
 * NOSCROLLBAR
 * IMGSRC
 * B
 * FG
 * SIZE
 * FACE
 * COLOR
 * DEFFON
 * DEFFIXEDFONT
 * WIDTH
 * VALUE
 * TOOLTIP
 * NAME
 * MIN
 * MAX
 * HEIGHT
 * DISABLED
 * ALIGN
 * MSG
 * LINK
 * HREF
 * ACTION
 */
public class NpcHtmlMessage extends L2GameServerPacket
{
	// d S
	// d is usually 0, S is the html text starting with <html> and ending with </html>
	//
	private int _npcObjId, _questId;
	private String _html;
	private String _file = null;
	private GArray<String> _replaces = new GArray<String>();
	private int item_id = 0;
	private boolean have_appends = false;
	public boolean have_encode = true;
	private final StackTraceElement[] ceatedFrom; //TODO убрать отладку

	public NpcHtmlMessage(L2Player player, L2NpcInstance npc, String filename, int val)
	{
		_npcObjId = npc.getObjectId();

		player.setLastNpc(npc);

		GArray<ScriptClassAndMethod> appends = Scripts.dialogAppends.get(npc.getNpcId());
		if(appends != null && appends.size() > 0)
		{
			have_appends = true;
			if(filename != null && filename.equalsIgnoreCase("./data/html/npcdefault.htm"))
				setHtml(""); // контент задается скриптами через DialogAppend_
			else
				setFile(filename);

			String replaces = "";

			// Добавить в конец странички текст, определенный в скриптах.
			Object[] script_args = new Object[] { new Integer(val) };
			for(ScriptClassAndMethod append : appends)
			{
				Object obj = player.callScripts(append.scriptClass, append.method, script_args);
				if(obj != null)
					replaces += obj;
			}

			if(!replaces.equals(""))
				replace("</body>", "\n" + Strings.bbParse(replaces) + "</body>");
		}
		else
			setFile(filename);

		replace("%npcId%", String.valueOf(npc.getNpcId()));
		replace("%npcname%", npc.getName());
		replace("%festivalMins%", SevenSignsFestival.getInstance().getTimeToNextFestivalStr());
		ceatedFrom = Thread.currentThread().getStackTrace();
	}

	public NpcHtmlMessage(L2Player player, L2NpcInstance npc)
	{
		if(npc == null)
		{
			_npcObjId = 5;
			player.setLastNpc(null);
		}
		else
		{
			_npcObjId = npc.getObjectId();
			player.setLastNpc(npc);
		}
		ceatedFrom = Thread.currentThread().getStackTrace();
	}

	public NpcHtmlMessage(int npcObjId)
	{
		_npcObjId = npcObjId;
		// TODO player.setLastNpc(null);
		ceatedFrom = Thread.currentThread().getStackTrace();
	}

	public final NpcHtmlMessage setHtml(String text)
	{
		if(!text.contains("<html>"))
			text = "<html><body>" + text + "</body></html>"; //<title>Message:</title> <br><br><br>
		_html = text;
		return this;
	}

	public final NpcHtmlMessage setFile(String file)
	{
		_file = file;
		return this;
	}

	/** WTF is this? Never used. */
	public final NpcHtmlMessage setItemId(int _item_id)
	{
		item_id = _item_id;
		return this;
	}

	public void setQuest(int quest)
	{
		_questId = quest;
	}

	protected String html_load(String name, String lang)
	{
		String content = Files.read(name, lang);
		if(content == null)
			content = "Can't find file'" + name + "'";
		return content;
	}

	public NpcHtmlMessage replace(String pattern, String value)
	{
		_replaces.add(pattern);
		_replaces.add(value);
		return this;
	}

	private static final Pattern objectId = Pattern.compile("%objectId%");

	@Override
	protected final void writeImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(_file != null) //TODO может быть не очень хорошо сдесь это делать...
		{
			String content = Files.read(_file, player);
			if(content == null)
			{
				if(player.isGM())
					player.sendMessage("HTML: " + _file);
				setHtml(have_appends && _file.endsWith(".htm") ? "" : _file);
			}
			else
				setHtml(content);
			player.setLastFile(_file);
		}

		for(int i = 0; i < _replaces.size(); i += 2)
		{
			if(_replaces.get(i) == null || _replaces.get(i + 1) == null)
			{
				_log.info("NpcHtmlMessage, _replaces == null, Player: " + player.getName() + ", Target: " + (L2ObjectsStorage.getNpc(_npcObjId) == null ? "NULL!!! " : L2ObjectsStorage.getNpc(_npcObjId).toString()) + ", HTML: " + (_html == null ? "NULL!!!" : _html) + "  _replaces.get(i) " + (_replaces.get(i) == null ? "NULL!!!" : _replaces.get(i)));
				return;
			}
			try
			{
				_html = _html.replaceAll(_replaces.get(i), _replaces.get(i + 1));
			}
			catch(IllegalArgumentException e)
			{
				_log.info("NpcHtmlMessage(266) -" + _replaces.get(i) + "- -" + _replaces.get(i + 1) + "-");
				//e.printStackTrace();
			}
		}

		if(objectId == null)
		{
			_log.info("objectId == null");
			Thread.dumpStack();
		}

		if(_html == null)
		{
			L2NpcInstance npc = L2ObjectsStorage.getNpc(_npcObjId);
			_log.info("[WARNING] NpcHtmlMessage, _html == null, npc: " + npc.toString());
			for(StackTraceElement e : ceatedFrom)
				_log.info("\t" + e);
			_log.info(Util.dumpObject(this, true, false, true));
			return;
		}

		Matcher m = objectId.matcher(_html);
		if(m != null)
			_html = m.replaceAll(String.valueOf(_npcObjId));

		_html = _html.replace("%playername%", player.getName());

		player.cleanBypasses(false, false);
		//_log.info("NpcHtmlMessage1: "+_html);
		if(have_encode)
			_html = player.encodeBypasses(_html, false, false);
	//	_log.info("\nNpcHtmlMessage2: "+_html);

		// TODO: Удалить, дистанция 100...
		// TODO: REV
		//if(!player.isCastingNow() && player.getTarget() != null && player.getTarget().isNpc() && !player.getTarget().isMonster() && !((L2Character)player.getTarget()).isMoving)
		//	player.sendPacket(new MoveToPawn(player, (L2Character)player.getTarget(), 100/*(int)player.getDistance((L2Character)player.getTarget())*/));
			//player.moveToPawn((L2Character)player.getTarget(), false);

		if(_questId > 0)
		{
			writeC(0xfe);
			writeHG(0x8d);
			writeD(_npcObjId);
			writeS(_html);
			writeD(_questId);
		}
		else
		{
			m = ImagesChache.HTML_PATTERN.matcher(_html);
			while(m.find())
			{
				String image_name = m.group(1);
				int image_id = ImagesChache.getInstance().getImageId(image_name);
				_html = _html.replaceAll("%img:" + image_name + "%", "Crest.crest_" + ConfigValue.RequestServerID + "_" + image_id);
				byte[] image = ImagesChache.getInstance().getImage(image_id);
				if(image != null)
					player.sendPacket(new PledgeCrest(image_id, image));
			}
			writeC(0x19);
			writeD(_npcObjId);
			writeS(_html);
			writeD(item_id);
			if(getClient().isLindvior())
				writeD(0x00);// Unknown GOD
		}
	}
}