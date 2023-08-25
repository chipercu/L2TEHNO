package l2open.extensions.multilang;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Даный класс является обработчиком интернациональных сообщений.
 * Поддержживается полностью юникод.
 *
 * По функциональности он не уступает SystemMessage, но поддерживает одновременно несколько языков.
 *
 * @Author: Death
 * @Date: 10/6/2007
 * @Time: 10:34:57
 */
public class CustomMessage
{
	private static String localizationDirSrc = "data/localization/";
	private static String localizationDirASCII = "data/localization/ascii/";

	private static URLClassLoader loader;

	static
	{
		reload();
	}

	public synchronized static void reload()
	{
		File src = new File(localizationDirSrc);

		for(File prop : src.listFiles(new PropertiesFilter()))
			ASCIIBuilder.createPropASCII(prop);

		try
		{
			loader = new URLClassLoader(new URL[] { new File(localizationDirASCII).toURI().toURL() });
			en = ResourceBundle.getBundle("messages", new Locale("en"), loader);
			ru = ResourceBundle.getBundle("messages", new Locale("ru"), loader);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace(System.err);
		}
	}

	private String _text;
	private int mark = 0;

	/**
	 * Создает новый инстанс сообщения.
	 * @param address адрес(ключ) параметра с языком интернационализации
	 * @param player игрок у которого будет взят язык
	 */
	public CustomMessage(String address, L2Object player, Object... args)
	{
		if(player != null && player.isPlayer())
			getString(address, ((L2Player) player).getLang());
		add(args);
	}

	/**
	 * Создает новый инстанс сообщения
	 * @param address адрес(ключ) параметра с языком интернационализации
	 * @param language язык по которому будет взято сообщение
	 */
	public CustomMessage(String address, String language, Object... args)
	{
		getString(address, language);
		add(args);
	}

	private static ResourceBundle en;
	private static ResourceBundle ru;

	private void getString(String address, String lang)
	{
		if(lang != null)
			lang = lang.toLowerCase();
		else
			lang = "en";

		ResourceBundle rb;

		// TODO если потребуется, добавить новые языки
		if(lang.equals("ru"))
			rb = ru;
		else
			rb = en;

		try
		{
			_text = rb.getString(address);
		}
		catch(Exception e)
		{
			_text = "Custom message with address: \"" + address + "\" is unsupported!";
		}

		if(_text == null)
			_text = "Custom message with address: \"" + address + "\" not found!";
	}

	/**
	 * Заменяет следующий елемент числом.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param number чем мы хотим заменить
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addNumber(long number)
	{
		_text = _text.replace("{" + mark + "}", String.valueOf(number));
		mark++;
		return this;
	}

	public CustomMessage add(Object... args)
	{
		for(Object arg : args) {
			if(arg instanceof String)
				addString((String) arg);
			else if(arg instanceof Integer)
				addNumber((Integer) arg);
			else if(arg instanceof Long)
				addNumber((Long) arg);
			else if(arg instanceof L2Item)
				addItemName((L2Item) arg);
			else if(arg instanceof L2ItemInstance)
				addItemName((L2ItemInstance) arg);
			else if(arg instanceof L2Character)
				addCharName((L2Character) arg);
			else if(arg instanceof L2Skill)
				this.addSkillName((L2Skill) arg);
			else
			{
				System.out.println("unknown CustomMessage arg type: " + arg);
				Thread.dumpStack();
			}
        }
		return this;
	}

	/**
	 * Заменяет следующий елемент строкой.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param str чем мы хотим заменить
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addString(String str)
	{
		_text = _text.replace("{" + mark + "}", str);
		mark++;
		return this;
	}

	/**
	 * Заменяет следующий елемент именем скилла.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param skill именем которого мы хотим заменить.
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addSkillName(L2Skill skill)
	{
		_text = _text.replace("{" + mark + "}", skill.getName());
		mark++;
		return this;
	}

	/**
	 * Заменяет следующий елемент именем скилла.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param skillId именем которого мы хотим заменить.
	 * @param skillLevel уровень скилла
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addSkillName(short skillId, short skillLevel)
	{
		return addSkillName(SkillTable.getInstance().getInfo(skillId, skillLevel));
	}

	/**
	 * Заменяет следующий елемент именем предмета.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param item именем которого мы хотим заменить.
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addItemName(L2Item item)
	{
		_text = _text.replace("{" + mark + "}", item.getName());
		mark++;
		return this;
	}

	/**
	 * Заменяет следующий елемент именем предмета.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param itemId именем которого мы хотим заменить.
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addItemName(int itemId)
	{
		return addItemName(ItemTemplates.getInstance().getTemplate(itemId));
	}

	/**
	 * Заменяет следующий елемент именем предмета.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param item именем которого мы хотим заменить.
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addItemName(L2ItemInstance item)
	{
		return addItemName(item.getItem());
	}

	/**
	 * Заменяет следующий елемент именем персонажа.<br>
	 * {0} {1} ... {Integer.MAX_VALUE}
	 * @param cha именем которого мы хотим заменить.
	 * @return этот инстанс уже с имененным текстом
	 */
	public CustomMessage addCharName(L2Character cha)
	{
		_text = _text.replace("{" + mark + "}", cha.getName());
		mark++;
		return this;
	}

	/**
	 * Возвращает локализированную строку, полученную после всех действий.
	 * @return строка.
	 */
	@Override
	public String toString()
	{
		return _text;
	}
}