package communityboard.html.buffer;

import communityboard.config.BufferConfig;
import communityboard.models.buffer.Buff;
import l2open.common.HtmlBuilder.Button;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;

import static l2open.common.HtmlBuilder.parameters.Parameters.action;

public class Elements {

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
        return Files.read(BufferConfig.HTML_PATCH + name);
    }

    public static String selectBuffButton(int id, String list_type) {
        L2Skill skill = SkillTable.getInstance().getInfo(id, 1);
        final int baseLevel = SkillTable.getInstance().getBaseLevel(id);

        String name = formatSkillName(skill.getName());
        final String html = getFile( "selectBuffButton.htm");
        return html.replace("<?icon?>", skill.getIcon())
                .replace("<?name?>", name)
                .replace("<?action?>", "bypass -h bbs_add_buff " + skill.getId() + " " + baseLevel + " " + list_type);
    }

    public static String showBuffsButton(L2Player player, Buff buff, int page){
        final String html = getFile( "showBuffsButton.htm");
        final String redactButtons = getFile( "redactBuffButtons.htm")
                .replace("<?buffId?>", String.valueOf(buff.getId()))
                .replace("<?buffType?>", buff.getType())
                .replace("<?page?>", String.valueOf(page));

        return html
                .replace("<?icon?>", buff.getIcon())
                .replace("<?enchantName?>", buff.getEnchant_name())
                .replace("<?buffName?>", formatSkillName(buff.getName()))
                .replace("<?buffId?>", String.valueOf(buff.getId()))
                .replace("<?buffType?>", buff.getType())
                .replace("<?redactBuffButtons?>", player.isGM() ? redactButtons : "")
                .replace("<?page?>", String.valueOf(page));
    }







}
