package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;

import static services.Talks.Kief.getPoints;

///@author Ragnarok
///@date 28.01.2010

public class HellboundVoiced extends Functions implements IVoicedCommandHandler, ScriptFile {

	//	Голосовая команда .hellbound
	//	Предназначена для вывода информации о текущем состоянии острова

	public static L2Object self;
	public static L2NpcInstance npc;

	private String[] _commandList = new String[]{"hellbound"};

	public boolean useVoicedCommand(String command, L2Player activeChar, String args) {

		if (command.equals("hellbound")){

			HellboundManager.getInstance().checkLevel();
			int level = HellboundManager.getInstance().getLevel();
			long trust = HellboundManager.getInstance().getPoints();
            boolean _isOpen = HellboundManager.getInstance().checkIsOpen();
			long life_points = getPoints();

			String dialog = Files.read("data/scripts/commands/voiced/hellbound.htm", activeChar);
			String status = "";
			if (_isOpen)
				status = "Открыт";
			else
				status = "Закрыт";
			dialog = dialog.replaceFirst("%status%", status);
			dialog = dialog.replaceFirst("%level%", ""+level);
			dialog = dialog.replaceFirst("%trust%", ""+trust);
            if (level == 7)
				dialog = dialog.replaceFirst("%life_points%", "<tr><td width=5></td><td width=120>Количество очков Жизни:</td><td width=40><font color=LEVEL>"+life_points+" из 1.000.000</font></td></tr>");
			show(dialog, activeChar);
			return true;

		}
		return true;
	}

	public String[] getVoicedCommandList(){
		return _commandList;
	}

	public void onLoad(){
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}