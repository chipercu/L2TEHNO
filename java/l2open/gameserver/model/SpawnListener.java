package l2open.gameserver.model;

import l2open.gameserver.model.instances.L2NpcInstance;

public interface SpawnListener
{
	public void npcSpawned(L2NpcInstance npc);

	public void npcDeSpawned(L2NpcInstance npc);
}
