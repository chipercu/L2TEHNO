package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

import java.util.Vector;

/**
 * Даные параметры актуальны для С6(Interlude), 04/10/2007, протокол 746
 */
public class StatusUpdate extends L2GameServerPacket
{
	/**
	 * Даный параметр отсылается оффом в паре с p_max_hp
	 * Сначала CUR_HP, потом p_max_hp
	 */
	public final static int CUR_HP = 0x09;
	public final static int p_max_hp = 0x0a;

	/**
	 * Даный параметр отсылается оффом в паре с p_max_mp
	 * Сначала CUR_MP, потом p_max_mp
	 */
	public final static int CUR_MP = 0x0b;
	public final static int p_max_mp = 0x0c;

	/**
	 * Меняется отображение только в инвентаре, для статуса требуется UserInfo
	 */
	public final static int CUR_LOAD = 0x0e;

	/**
	 * Меняется отображение только в инвентаре, для статуса требуется UserInfo
	 */
	public final static int MAX_LOAD = 0x0f;

	public final static int PVP_FLAG = 0x1a;
	public final static int KARMA = 0x1b;

	/**
	 * Даный параметр отсылается оффом в паре с p_max_cp
	 * Сначала CUR_CP, потом p_max_cp
	 */
	public final static int CUR_CP = 0x21;
	public final static int p_max_cp = 0x22;

	/**
     * GOD отображение демага
     */
    public final static int DAMAGE = 0x23;

	private final int _objectId;
	private final Vector<Attribute> _attributes = new Vector<Attribute>();
	private int _playerId;
	private boolean hpRegActive = true;

	class Attribute
	{
		public final int id;
		public final int value;

		Attribute(int id, int value)
		{
			this.id = id;
			this.value = value;
		}
	}

	public StatusUpdate(int objectId)
	{
		_objectId = objectId;
	}

	public StatusUpdate addAttribute(int id, int level)
	{
		_attributes.add(new Attribute(id, level));
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x18);
		writeD(_objectId);
		writeD(_attributes.size());

		for(Attribute temp : _attributes)
		{
			writeD(temp.id);
			writeD(temp.value);
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x18);
		writeD(_objectId);
		writeD(_playerId);
		writeD(hpRegActive ? 0x01 : 0x00);
		writeD(_attributes.size());

		for(Attribute temp : _attributes)
		{
			writeD(temp.id);
			writeD(temp.id == KARMA ? -temp.value : temp.value);
		}

		//writeD(0x00); //TODO а на NA оффе этих дшек нету
		//writeD(0x00); //TODO а на NA оффе этих дшек нету
		//writeD(0x00); //TODO а на NA оффе этих дшек нету
		return true;
	}

	public boolean hasAttributes()
	{
		return !_attributes.isEmpty();
	}
}