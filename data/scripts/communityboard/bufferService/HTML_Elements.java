package communityboard.bufferService;

import l2open.common.Html_Constructor.tags.Button;
import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.action;

public class HTML_Elements {

    public static final String html_path = "data/scripts/services/PremiumBuffer/";

    public static String formatSkillName(String name) {
        name = name.replace("Dance of the", "D.");
        name = name.replace("Dance of", "D.");
        name = name.replace("Song of", "S.");
        name = name.replace("Improved", "I.");
        name = name.replace("Awakening", "A.");
        name = name.replace("Blessing", "Bless.");
        name = name.replace("Protection", "Protect.");
        name = name.replace("Critical", "C.");
        name = name.replace("Condition", "Con.");
        return name;
    }

    public static String backButtonMain(){
        return new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25, "L2UI_CT1.OlympiadWnd_DF_Back", "L2UI_CT1.OlympiadWnd_DF_Back_Down").build();
    }

    public static String getFile(String name){
        return Files.read(html_path + name);
    }

    public static String selectBuffButton(int id, String list_type, long owner) {
        L2Skill skill = SkillTable.getInstance().getInfo(id, 1);
        final int baseLevel = SkillTable.getInstance().getBaseLevel(id);

        String name = formatSkillName(skill.getName());
        final String html = getFile( "selectBuffButton.htm");
        return html.replace("<?icon?>", skill.getIcon())
                .replace("<?name?>", name)
                .replace("<?action?>", "bypass -h bbs_add_buff_set " + skill.getId() + " " + baseLevel + " " + list_type + " " + owner);
    }



}
