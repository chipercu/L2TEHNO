package bosses;

import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.serverpackets.ExSendUIEvent;

public class ZakenReflection extends Reflection
{
	public ZakenReflection(String name)
	{
		super(name);
	}

	@Override
	public void collapse()
	{
		super.collapse();

		ZakenManager.instances.remove(getId());
		ZakenManager.getMembersCC().remove(getId());
		ZakenManager.getMembersParty().remove(getId());
	}

	@Override
	public void removeObject(L2Object o)
	{
		super.removeObject(o);

		if(o.isPlayable())
			((L2Playable)o).sendPacket(new ExSendUIEvent((L2Playable)o, true, true, 0, 10, "")); // остановка счётчика (для инстанта закена).
	}
}