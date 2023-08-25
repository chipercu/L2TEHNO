package l2open.gameserver.model.barahlo.academ;

import l2open.common.ThreadPoolManager;
import l2open.common.RunnableImpl;
import l2open.gameserver.model.barahlo.academ.dao.AcademiciansDAO;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.tables.ClanTable;

public class AcademicCheck extends RunnableImpl
{
	@Override
	public void runImpl() throws Exception
	{
		for(Academicians academic : AcademiciansStorage.getInstance().get())
		{
			if(academic.getTime() < System.currentTimeMillis())
			{
				AcademyRequest academy = AcademyStorage.getInstance().getReguest(academic.getClanId());

				if(academy != null)
				{
					AcademiciansDAO.getInstance().delete(academic);
					AcademyStorage.getInstance().updateList();
					academy.updateSeats();
				}
			}
			else
			{
				L2Player player = L2ObjectsStorage.getPlayer(academic.getObjId());
				if(player != null)
				{
					int time = (int) ((academic.getTime() - System.currentTimeMillis()) / 1000);
					if(time / 3600 < 1)
						player.sendPacket(new ExShowScreenMessage("Сообщение: " + time / 3600 + " ч.", 3000, ScreenMessageAlign.TOP_CENTER, true));
				}
			}
		}

		ThreadPoolManager.getInstance().schedule(this, 60000L);
	}
}