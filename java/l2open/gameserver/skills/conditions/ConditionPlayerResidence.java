package l2open.gameserver.skills.conditions;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.skills.Env;

public class ConditionPlayerResidence extends Condition
{
	private final int _id;
	private final Class<? extends Residence> _type;

	@SuppressWarnings("unchecked")
	public ConditionPlayerResidence(int id, String type)
	{
		_id = id;
		try
		{
			_type = (Class<? extends Residence>) Class.forName("l2open.gameserver.model.entity.residence." + type);
		}
		catch(ClassNotFoundException e)
		{
			throw new Error(e);
		}
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		L2Player player = (L2Player)env.character;
		L2Clan clan = player.getClan();
		if(clan == null)
			return false;

		int residenceId = clan.getResidenceId(_type);

		return _id > 0 ? residenceId == _id : residenceId > 0;
	}
}
