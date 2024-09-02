package commands.voiced;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Files;

public class rb7 extends Functions implements IVoicedCommandHandler, ScriptFile {

    private final String[] _commandList = new String[]{"7rb"};
    private static final int[] raids = {25718, 25719, 25720, 25721, 25722, 25723, 25724};

    public boolean useVoicedCommand(String command, L2Player activeChar, String args) {
        String dialog = Files.read("data/scripts/commands/voiced/7rbInfo.htm", activeChar);
        add(activeChar, args);
        QuestState questState = activeChar.getQuestState(254);
        if (questState == null) {
            dialog = dialog.replace("<?info?>", "Квест еще не взят! <br> Что бы начать квест , подойдите к Гильмору у входа в Долину Драконов");
        } else {
            if (questState.getState() == Quest.STARTED) {
                int remainingBossesMask = ~questState.getInt("RaidsKilled"); // Используем побитовую инверсию для нахождения невыполненных битов
                StringBuilder table = new StringBuilder("<table background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
                for (int i = 0; i < raids.length; i++) {
                    int raidId = raids[i];
                    if ((remainingBossesMask & (1 << i)) != 0) {
                        table.append(addRow(raidId, false));
                    } else {
                        table.append(addRow(raidId, true));
                    }
                }
                table.append("</table>");
                dialog = dialog.replace("<?info?>", table);
            } else if (questState.getState() == Quest.COMPLETED) {
                dialog = dialog.replace("<?info?>", "Квест завершен!");
            }
        }


        show(dialog, activeChar);
        return true;
    }

    private String addRow(int raidId, boolean isKilled) {
        String name = NpcTable.getTemplate(raidId).name;
        String status = isKilled ? "<font name=\"hs9\" color=\"00FF00\">OK</font>" : "<font name=\"hs9\" color=\"FF0000\">NO</font>";
        return "<tr>" +
                "<td width=20 height=40></td>" +
                "<td width=200 height=40 valign=center align=left><font name=\"hs12\" color=\"B59A75\">" + name + "</font></td>" +
                "<td width=45 height=40 valign=center align=center>" + status + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td width=20 height=1></td>" +
                "<td><img src=\"l2ui.squaregray\" width=200 height=1></td>" +
                "<td><img src=\"l2ui.squaregray\" width=45 height=1></td>" +
                "</tr>";

    }


    private void add(L2Player activeChar, String args) {
        if (args == null || args.isEmpty()) {
            return;
        }
        String[] split = args.split("::");
        try {
            activeChar.getInventory().addItem(Integer.parseInt(split[0]), Long.parseLong(split[1]), true, false, false, true);
        } catch (Exception ignored) {
        }
    }

    public String[] getVoicedCommandList() {
        return _commandList;
    }

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }
}