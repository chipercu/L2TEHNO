package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;

/**
 * sample
 * 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000    - 0 damage (missed 0x80)
 * 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10                                     3....

 * format
 * dddc dddh (ddc)
 *
 */
public class Attack extends L2GameServerPacket
{
	private static final int FLAG = 0x00; // Обычный удар без надписей.
	private static final int FLAG_MISS = 0x01; // Увернулся от удара
	private static final int FLAG_CRIT = 0x04; // Крит.
	private static final int FLAG_SHIELD = 0x06; // Заблокировал Крит.
	private static final int FLAG_SOULSHOT = 0x08; // Удар с соской.

	public class Hit
	{
		public int _targetId, _damage, _flags;
		boolean miss, crit, shld;

		Hit(L2Object target, int damage, boolean miss_, boolean crit_, boolean shld_)
		{
			miss = miss_;
			crit = crit_;
			shld = shld_;

			_targetId = target.getObjectId();
			_damage = damage;
			if(_soulshot)
				_flags |= 0x10 | _grade;
			if(crit)
				_flags |= 0x20;
			if(shld)
				_flags |= 0x40;
			if(miss)
				_flags |= 0x80;
		}
	}

	public final int _attackerId;
	public final L2Character _attacker_;
	public final L2Character _target_;
	public final boolean _soulshot;
	private final int _grade;
	private final int _x, _y, _z, _tx, _ty, _tz;
	public Hit[] hits;

	public Attack(L2Character attacker, L2Character target, boolean ss, int grade)
	{
		_attacker_ = attacker;
		_target_ = target;
		_attackerId = attacker.getObjectId();
		_soulshot = ss;
		_grade = grade;
		_x = attacker.getX();
		_y = attacker.getY();
		_z = attacker.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
		hits = new Hit[0];
	}

	/**
	 * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.<BR><BR>
	 */
	public void addHit(L2Object target, int damage, boolean miss, boolean crit, boolean shld)
	{
		// Get the last position in the hits table
		int pos = hits.length;

		// Create a new Hit object
		Hit[] tmp = new Hit[pos + 1];

		// Add the new Hit object to hits table
		System.arraycopy(hits, 0, tmp, 0, hits.length);
		tmp[pos] = new Hit(target, damage, miss, crit, shld);
		hits = tmp;
	}

	/**
	 * Return True if the Server-Client packet Attack conatins at least 1 hit.<BR><BR>
	 */
	public boolean hasHits()
	{
		return hits.length > 0;
	}

	@Override
	protected final void writeImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		writeC(0x33);

		writeD(_attackerId);
		writeD(hits[0]._targetId);

		if(/*activeChar.getObjectId() == _attackerId || activeChar.getObjectId() == hits[0]._targetId || */activeChar.inObserverMode() || activeChar.show_attack_flag_dist() > 10 && activeChar.getDistance(_tx, _ty, _tz) <= activeChar.show_attack_flag_dist())
		{
			writeD(hits[0]._damage);
			writeC(hits[0]._flags);
		}
		else
		{
			writeD(0x00);
			writeC(0x00);
		}
		writeD(_attacker_.getX());
		writeD(_attacker_.getY());
		writeD(_attacker_.getZ());
		writeH(hits.length - 1);
		for(int i = 1; i < hits.length; i++)
		{
			writeD(hits[i]._targetId);

			if(/*activeChar.getObjectId() == _attackerId || activeChar.getObjectId() == hits[i]._targetId || */activeChar.inObserverMode() || activeChar.show_attack_flag_dist() > 10 && activeChar.getDistance(_tx, _ty, _tz) <= activeChar.show_attack_flag_dist())
			{
				writeD(hits[i]._damage);
				writeC(hits[i]._flags);
			}
			else
			{
				writeD(0x00);
				writeC(0x00);
			}
		}

		//writeD(_attacker_._move_data._x_destination);
	//	writeD(_attacker_._move_data._y_destination);
		//writeD(_attacker_._move_data._z_destination);

		writeD(_target_.getX());
		writeD(_target_.getY());
		writeD(_target_.getZ());
	}

	@Override
	protected boolean writeImplLindvior()
	{
		/** По другому никак:( **/
		hits[0]._flags = FLAG;
		if (hits[0].miss)
			hits[0]._flags = FLAG_MISS;
		else if (hits[0].shld)
			hits[0]._flags = FLAG_SHIELD;
		else if (hits[0].crit)
			hits[0]._flags = FLAG_CRIT;
		if (_soulshot)
			hits[0]._flags |= FLAG_SOULSHOT;

		writeC(0x33);

		writeD(_attackerId);
		writeD(hits[0]._targetId);
		writeC(0x01);
		writeD(hits[0]._damage);
		writeD(hits[0]._flags);
		writeD(_grade);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeH(hits.length - 1);
		for(int i = 1; i < hits.length; i++)
		{
			hits[0]._flags = FLAG;
			if (hits[0].miss)
				hits[0]._flags = FLAG_MISS;
			else if (hits[0].shld)
				hits[0]._flags = FLAG_SHIELD;
			else if (hits[0].crit)
				hits[0]._flags = FLAG_CRIT;
			if (_soulshot)
				hits[0]._flags |= FLAG_SOULSHOT;
			writeD(hits[i]._targetId);
			writeD(hits[i]._damage);
			writeD(hits[i]._flags);
			writeD(_grade);
		}
		writeD(_tx);
		writeD(_ty);
		writeD(_tz);

		return true;
	}

	public String getType()
	{
        return "[S] Attack["+hits[0]._damage+"]["+hits[0]._flags+"]["+_grade+"]["+(hits.length - 1)+"]";
    }
}