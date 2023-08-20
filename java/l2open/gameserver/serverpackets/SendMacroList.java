package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Macro;

/**
 * packet type id 0xe7
 *
 * sample
 *
 * e7
 * d // unknown change of Macro edit,add,delete
 * c // unknown
 * c //count of Macros
 * c // unknown
 *
 * d // id
 * S // macro name
 * S // desc
 * S // acronym
 * c // icon
 * c // count
 *
 * c // entry
 * c // type
 * d // skill id
 * c // shortcut id
 * S // command name
 *
 * format:		cdccdSSScc (ccdcS)
 */
public class SendMacroList extends L2GameServerPacket
{
	private final int _macroId;
	private final int _count;
	private final int _action;
	private final int _revision;
	private final L2Macro _macro;

	public SendMacroList(int revision, int macroId, int count, L2Macro macro, int action)
	{
		_macroId = macroId;
		_count = count;
		_macro = macro;
		_action = action;
		_revision = revision;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe8);

		writeD(_revision); // macro change revision (changes after each macro edition)
		writeC(0x00); //unknown
		writeC(_count); //count of Macros
		writeC(_macro != null ? 1 : 0); //unknown

		if(_macro != null)
		{
			writeD(_macro.id); //Macro ID
			writeS(_macro.name); //Macro Name
			writeS(_macro.descr); //Desc
			writeS(_macro.acronym); //acronym
			writeC(_macro.icon); //icon

			writeC(_macro.commands.length); //count

			for(int i = 0; i < _macro.commands.length; i++)
			{
				L2Macro.L2MacroCmd cmd = _macro.commands[i];
				writeC(i + 1); //i of count
				writeC(cmd.type); //type  1 = skill, 3 = action, 4 = shortcut
				writeD(cmd.d1); // skill id
				writeC(cmd.d2); // shortcut id
				writeS(cmd.cmd); // command name
			}
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
        writeC(0xe8);

        writeC(_action); //  writeC(_action.ordinal()); 0 - dell, 1 - add, 2 - update
        writeD(_macroId);
        writeC(_count);

        if (_macro != null)
		{
            writeC(1);
			writeD(_macro.id); //Macro ID
			writeS(_macro.name); //Macro Name
			writeS(_macro.descr); //Desc
			writeS(_macro.acronym); //acronym
			writeC(_macro.icon); //icon

			writeC(_macro.commands.length); //count

            for (int i = 0; i < _macro.commands.length; i++)
			{
                L2Macro.L2MacroCmd cmd = _macro.commands[i];
                writeC(i + 1);
                writeC(cmd.type);
                writeD(cmd.d1);
                writeC(cmd.d2);
                writeS(cmd.cmd);
            }
        }
		else
            writeC(0);
		return true;
    }
}