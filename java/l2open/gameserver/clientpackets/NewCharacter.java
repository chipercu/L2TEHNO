package l2open.gameserver.clientpackets;

import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.serverpackets.NewCharacterSuccess;
import l2open.gameserver.tables.CharTemplateTable;

public class NewCharacter extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		NewCharacterSuccess ct = new NewCharacterSuccess();

		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.fighter, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.mage, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.elvenFighter, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.elvenMage, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.darkFighter, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.darkMage, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.orcFighter, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.orcMage, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.dwarvenFighter, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.maleSoldier, false));
		ct.addChar(CharTemplateTable.getInstance().getTemplate(ClassId.femaleSoldier, false));

		sendPacket(ct);
	}
}