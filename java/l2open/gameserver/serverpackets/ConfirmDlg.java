package l2open.gameserver.serverpackets;

import l2open.util.Location;

import java.util.Vector;

/**
 * @author SYS Format: cdddsddddcdd
 * @Date: 07/8/2007
 */

/**
 * Пакет с подтверждением при саммоне персонажей скилом Summon Friend и подобных.
 * "Nemu wishes to summon you from Imperial Tomb. Do you accept?"
 * Время ответа на диалог лимитировано.
 * ED
 * 32 07 00 00 - Номер системного сообщения int 1842 "$s1 wishes to summon you from $s2. Do you accept?"
 * 02 00 00 00 00 00 00 - unknow
 * 00 4E 00 65 00 6D 00 75 00 00 - Имя персонажа, в данном случае Nemu
 * 00 07 00 00 - Тип 7 - есть полоска времени
 * 00 B5 C6 02 00 B0 B8 FE FF 00 E7 FF - координаты локации: x,y,z
 * FF - unknow
 * 30 75 00 00 - время, даваемое на ответ 30000мс (уменьшающаяся полоска).
 * EE DB 30 4B - Идентификатор запроса, после выбора ответа возвращается клиентом серверу
 * Название локации определяется клиентом по полученным координатам.
 *
 * Пакет с подтверждением при ресуректе:
 * "Nemu is making an attempt at resurrection. Do you want to continue with this resurrection?"
 * ED
 * E6 05 00 00 - Номер системного сообщения int 1510 "$s1 is making an attempt at resurrection. Do you want to continue with this resurrection?"
 * 02 00 00 00 00 00 00 - unknown
 * 00 4E 00 65 00 6D 00 75 00 00 - Имя персонажа, в данном случае Nemu
 * 00 06 00 00 - Время ответа на диалог не лимитировано.
 * 00 00 00 00 00 00 00 00 00 00 00 00 - unused coods
 * 00 - unknown
 * BC 39 B6 1E - Идентификатор запроса, после выбора ответа возвращается клиентом серверу
 */
public class ConfirmDlg extends L2GameServerPacket
{
	private int _messageId;
	private int _Time;
	private int _requestId;

	private static final int TYPE_ZONE_NAME = 7;
	private static final int TYPE_LONG = 6;
	private static final int TYPE_UNKNOWN_5 = 5;
	private static final int TYPE_SKILL_NAME = 4;
	private static final int TYPE_ITEM_NAME = 3;
	private static final int TYPE_NPC_NAME = 2;
	private static final int TYPE_NUMBER = 1;
	private static final int TYPE_TEXT = 0;
	private Vector<Integer> _types = new Vector<Integer>();
	private Vector<Object> _values = new Vector<Object>();

	public ConfirmDlg(int requestId, int time, int id)
	{
		_messageId = requestId;
		_Time = time;
		_requestId = id;
	}

	public ConfirmDlg addString(String text)
	{
		_types.add(TYPE_TEXT);
		_values.add(text);
		return this;
	}

	public ConfirmDlg addNumber(Integer number)
	{
		_types.add(TYPE_NUMBER);
		_values.add(number);
		return this;
	}

	public ConfirmDlg addNumber(Short number)
	{
		return addNumber(new Integer(number));
	}

	public ConfirmDlg addNumber(Byte number)
	{
		return addNumber(new Integer(number));
	}

	public ConfirmDlg addNumber(Long number)
	{
		if(number > Integer.MAX_VALUE)
			return addLong(number);
		return addNumber(number.intValue());
	}

	public ConfirmDlg addNpcName(int id)
	{
		_types.add(TYPE_NPC_NAME);
		_values.add(new Integer(1000000 + id));
		return this;
	}

	public ConfirmDlg addItemName(Short id)
	{
		_types.add(TYPE_ITEM_NAME);
		_values.add(new Integer(id));
		return this;
	}

	public ConfirmDlg addItemName(Integer id)
	{
		_types.add(TYPE_ITEM_NAME);
		_values.add(id);
		return this;
	}

	public ConfirmDlg addZoneName(Location loc)
	{
		_types.add(new Integer(TYPE_ZONE_NAME));
		_values.add(loc);
		return this;
	}

	public ConfirmDlg addSkillName(Short id, Short level)
	{
		_types.add(TYPE_SKILL_NAME);
		int[] skill = { id, level };
		_values.add(skill);
		return this;
	}

	public ConfirmDlg addLong(Long number)
	{
		_types.add(TYPE_LONG);
		_values.add(number);
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf3);
		writeD(_messageId);

		writeD(_types.size());

		for(int i = 0; i < _types.size(); i++)
		{
			int t = _types.get(i);

			writeD(t);

			switch(t)
			{
				case TYPE_TEXT:
				{
					if(_values.size() >= i)
						writeS((String) _values.get(i));
					break;
				}
				case TYPE_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_ITEM_NAME:
				case TYPE_UNKNOWN_5:
				{
					if(_values.size() < i)
						break;
					int t1 = (Integer) _values.get(i);
					writeD(t1);

					break;
				}
				case TYPE_SKILL_NAME:
				{
					if(_values.size() < i)
						break;

					int[] skill = (int[]) _values.get(i);

					writeD(skill[0]); // id
					writeD(skill[1]); // level

					break;
				}
				case TYPE_LONG:
				{
					if(_values.size() < i)
						break;
					long t1 = (Long) _values.get(i);
					writeQ(t1);

					break;
				}
				case TYPE_ZONE_NAME:
				{
					Location coord = (Location) _values.get(i);
					writeD(coord.x);
					writeD(coord.y);
					writeD(coord.z);
					break;
				}
			}
		}
		writeD(_Time);
		writeD(_requestId);
	}

	public void setRequestId(int requestId)
	{
		_requestId = requestId;
	}
}