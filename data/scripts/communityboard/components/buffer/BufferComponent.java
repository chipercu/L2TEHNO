package communityboard.components.buffer;

import communityboard.config.BufferConfig;
import communityboard.html.buffer.Elements;
import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import communityboard.service.buffer.BuffService;
import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Edit;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.Table;
import l2open.common.Html_Constructor.tags.parameters.EditType;
import l2open.common.Html_Constructor.tags.parameters.Position;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.TutorialShowHtml;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.Rnd;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static communityboard.html.buffer.Elements.*;
import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.gameserver.communitybbs.Manager.BaseBBSManager.addCustomReplace;
import static l2open.gameserver.model.L2Skill.SkillTargetType.*;

public class BufferComponent {

    private BuffService buffService;
    private static final int SYSTEM_LISTS = -1;

    public BufferComponent(BuffService buffService) {
        this.buffService = buffService;
    }

        public void showChangeBuffParams(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buffId = Integer.parseInt(args[0]);
        String type = args[1];

        String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/scheme.htm", player);

        final Table table = new Table(4, 4).setParams(width(400), border(1));

        final Buff buff = buffService.getBuff(buffId);
        table.row(0).col(0);
        table.row(0).col(1).insert(new Img(buff.getIcon()).build());
        table.row(0).col(2).insert(buff.getName());
        table.row(0).col(3).insert(backButtonMain());
        ShowBoard.separateAndSend(addCustomReplace(html.replace("%content%", table.build())), player);
    }

    public void changeListIndex(L2Player player, String[] args) {
//        String list_type = args[0];
//        int currentIndex = Integer.parseInt(args[1]);
//        String direct = args[2];
//
//        long owner = player.getObjectId();
//        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
//            owner = SYSTEM_LISTS;
//        }
//
//        final Scheme buffsList = buffService.getBuffsList(list_type, owner);
//
//        if ("left".equals(direct)){
//            buffService.changeListIndex(buffsList, currentIndex, currentIndex - 1);
//        } else if ("right".equals(direct)) {
//            buffService.changeListIndex(buffsList, currentIndex, currentIndex + 1);
//        }
//        showCreateReadySet(player, args);
    }

    private Table controlButtons(String list_type, int currentIndex, boolean left, boolean delete, boolean right){
        final Table controlButtons = new Table(1, 3).setParams(cellpadding(0), cellspacing(0));
        controlButtons.row(0).col(0).setParams(height(20))
                .insert(left ? new Button( action("bypass -h bbs_change_list_index " + list_type + " " + currentIndex + " left"), 16, 16, "L2UI_CT1.Button_DF_Left_Down", "L2UI_CT1.Button_DF_Left").build() : "");

        controlButtons.row(0).col(1)
                .insert(delete ? new Button(action(""), 16, 16, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build(): "");

        controlButtons.row(0).col(2)
                .insert(right ? new Button(action("bypass -h bbs_change_list_index " + list_type + " " + currentIndex + " right"), 16, 16,"L2UI_CT1.Button_DF_Right_Down", "L2UI_CT1.Button_DF_Right").build(): "");
        return controlButtons;
    }

    public void castScheme(L2Player player, String[] args) {
        int schemeId = Integer.parseInt(args[0]);
        String target = args[1];

        final Scheme scheme = buffService.getScheme(schemeId);

        if (scheme != null){
            final List<SchemeBuff> schemeBuffs = scheme.getBuffs().values().stream().sorted(Comparator.comparingInt(SchemeBuff::getIndex)).collect(Collectors.toList());
            for (SchemeBuff schemeBuff : schemeBuffs) {
                Buff buff = buffService.getBuff(schemeBuff.getBuff_id());
                if (player.getBonus().RATE_XP <= 1) {
                    buffService.applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
                }
                buffService.applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
            }
        }
        showMainPage(player);
    }

    public void showMainPage(L2Player player) {
        String html = Files.read(BufferConfig.HTML_PATCH + "index.htm", player);
        if (html != null) {
            final int bufferPriceOne = ConfigValue.BufferPriceOne;
            final int bufferTime = ConfigValue.BufferTime;
            html = html.replace("%price%", String.valueOf(bufferPriceOne));
            html = html.replace("%time%", String.valueOf(bufferTime));
            html = html.replace("%scheme%", getSystemSchemes(player));
            html = html.replace("%buffgrps%", getPersonalSchemes(player));
        }
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }

    private String getPersonalSchemes(L2Player player) {
        final List<Scheme> schemes = buffService.getSchemes(player.getObjectId());
        if (schemes == null){
            return "";
        }

        final Table table = new Table(schemes.size() + 1 , 3);
        for (int i = 0; i < schemes.size(); i++) {
            final Scheme scheme = schemes.get(i);
            table.row(i).col(0).insert(new Button(scheme.getName(), action("bypass -h bbs_cast_save_set " + scheme.getId() + " $Who"), 130, 32).build());
            table.row(i).col(1).insert(new Button("@", action("bypass -h bbs_show_redact_scheme " + scheme.getId()), 32, 32).build());
            table.row(i).col(2).insert(new Button("-", action("bypass -h bbs_remove_scheme " + scheme.getOwner() + " " + scheme.getId()), 32, 32).build());
        }
        table.row(schemes.size()).col(0).insert(new Edit("addScheme", 130, 20, EditType.text, 20).build());
        table.row(schemes.size()).col(1).insert(new Button("+", action("bypass -h bbs_create_scheme $addScheme"), 32, 32).build());

        return table.build();
    }

    private String getSystemSchemes(L2Player player) {
        final List<Scheme> systemSchemes = buffService.getSchemes(SYSTEM_LISTS);
        final Table table = new Table(systemSchemes.size() + 1, 3);
        int index = 0;
        for (Scheme scheme : systemSchemes) {
            table.row(index).col(0).insert(new Button(scheme.getName(), action("bypass -h bbs_cast_scheme " + scheme.getId() + " $Who"), 130, 32).build());
            if (player.isGM()) {
                table.row(index).col(1).insert(new Button("@", action("bypass -h bbs_show_redact_scheme " + scheme.getId()), 32, 32).build());
                table.row(index).col(2).insert(new Button("-", action("bypass -h bbs_remove_scheme " + scheme.getOwner() + " " + scheme.getId()), 32, 32).build());
            }
        }
        if (player.isGM()) {
            table.row(systemSchemes.size()).col(0).insert(new Edit("addSystemScheme", 130, 20, EditType.text, 20).build());
            table.row(systemSchemes.size()).col(1).insert(new Button("+", action("bypass -h bbs_create_ready_scheme $addSystemScheme"), 32, 32).build());
        }
        return table.build();
    }

    public void clearScheme(L2Player player, String[] args) {
        int schemeId = Integer.parseInt(args[0]);
        int owner = Integer.parseInt(args[1]);
        buffService.clearScheme(owner, schemeId);
        showBuffs(player, args);
    }

    public void showAllBuffs(L2Player player, String[] args) {
        if (!player.isGM()){
            return;
        }

        String list_type = args[0];
        final Table main = new Table(3, 1);
        final Table header = new Table(1, 5);
        header.row(0).col(0).setParams(width(250));
        header.row(0).col(1).setParams(width(100));
        header.row(0).col(2).setParams(width(100));
        header.row(0).col(3).setParams(width(150));
        header.row(0).col(4).setParams(width(100), align(Position.RIGHT)).insert(player.isGM() ? new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25, "L2UI_CT1.OlympiadWnd_DF_Back", "L2UI_CT1.OlympiadWnd_DF_Back_Down").build() : "");

        final List<Integer> integers = loadAllBuffs(player);
        int cols = 6;
        int rows = (int) Math.ceil((double) integers.size() / cols);
        final Table table = new Table(rows, cols).setParams(width(720));

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < integers.size()) {
                    final String buffButton = selectBuffButton(integers.get(index), list_type);
                    table.row(i).col(j).insert(buffButton);
                    index++;
                } else {
                    break;  // Если значения в списке закончились, прерываем цикл
                }
            }
        }
        main.row(0).col(0).insert(header.build());
        main.row(1).col(0).insert(table.build());
        main.row(2).col(0).insert("");
        String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/scheme.htm", player);
        html = html.replace("%content%", main.build());
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }

    public void changeEnchantType(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buffId = Integer.parseInt(args[0]);
        String type = args[1];

        final Buff buff = buffService.getBuff(buffId);

        if (buff != null){
            buff.setNextEnchantType();
            buffService.updateBuff(buff);
        }
        showBuffs(player, new String[]{type});
    }

    public void addBuffToScheme(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int schemeId = Integer.parseInt(args[0]);
        int buffId = Integer.parseInt(args[1]);
        int index = Integer.parseInt(args[2]);

        buffService.addBuffInScheme(schemeId, buffId, index);
        showRedactScheme(player, args);
    }

    public void removeBuffFromScheme(String[] args, L2Player player) {
        if (args == null) {
            showMainPage(player);
            return;
        }
        int index = Integer.parseInt(args[0]);
        String schemeName = args[1];
        int owner = Integer.parseInt(args[2]);

        final Optional<Scheme> optionalScheme = buffService.getScheme(owner, schemeName);

        if (optionalScheme.isPresent()) {
            final Scheme scheme = optionalScheme.get();
            buffService.removeBuffFromScheme(scheme, index);
            showRedactScheme(player, new String[]{String.valueOf(scheme.getId())});
        }
    }

    public void createScheme(L2Player player, String[] args, int owner) {
        if (args == null || args.length < 1){
            return;
        }
        StringBuilder schemeName = new StringBuilder();
        for (String s: args){
            if (s == null){
                continue;
            }
            schemeName.append(s).append(" ");
        }
        final Optional<Scheme> schemeOptional = buffService.getScheme(owner, schemeName.toString());
        if (schemeOptional.isPresent()){
            player.sendMessage("Это название уже занято.");
            return;
        }else {
            final Scheme scheme = new Scheme(owner, schemeName.toString());
            buffService.createScheme(scheme);
        }
        showMainPage(player);
    }

    public void createPersonalScheme(L2Player player, String[] args){
        createScheme(player, args, player.getObjectId());
    }

    public void createSystemScheme(L2Player player, String[] args) {
        if (!player.isGM()){
            return;
        }
        createScheme(player, args, SYSTEM_LISTS);
    }

    public void showBuffs(L2Player player, String[] args) {
        String type = args[0];

        final Table header = new Table(1, 5);
        header.row(0).col(0).setParams(width(250));
        header.row(0).col(1).setParams(width(100)).insert(player.isGM() ? new Button("Добавить", action("bypass -h bbs_show_all_buffs " + type), 100, 25).build() : "");
        header.row(0).col(2).setParams(width(100)).insert(player.isGM() ? new Button("Удалить все!", action("bypass -h bbs_clear_buffs " + type), 100, 25).build() : "");
        header.row(0).col(3).setParams(width(150)).insert("<combobox width=60 height=10 var=\"Who\" list=\"Player;Pet\">");
        header.row(0).col(4).setParams(width(100), align(Position.RIGHT)).insert(new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25, "L2UI_CT1.OlympiadWnd_DF_Back_Down", "L2UI_CT1.OlympiadWnd_DF_Back").build());

        final List<Buff> buffs = buffService.getBuffs(type);
        int cols = 6;
        int rows = (int) Math.ceil((double) buffs.size() / cols);
        final Table table = new Table(rows, cols);

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < buffs.size()) {
                    final Buff buff = buffs.get(index);
                    table.row(i).col(j).insert(Elements.showBuffsButton(buff));
                    index++;
                } else {
                    break;  // Если значения в списке закончились, прерываем цикл
                }
            }
        }
        final Table main = new Table(3, 1);
        main.row(0).col(0).insert(header.build());
        main.row(1).col(0).insert(table.build());
        main.row(2).col(0).insert("");
        String html = Files.read(BufferConfig.HTML_PATCH + "scheme.htm", player);
        html = html.replace("%title%", type).replace("%content%", main.build());
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }





    private List<Integer> loadAllBuffs(L2Player player) {
        ArrayList<Integer> list = new ArrayList<>();
        final String[] split = Files.read(BufferConfig.ALL_BUFFS_FILE, player).replaceAll("\\s", "").split(",");
        for (String s : split) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }
    private List<Integer> getAllBuffs(){
        final List<L2Skill> skills = SkillTable.getInstance().getSkills(L2Skill.SkillType.BUFF);

        HashSet<Integer> set = new HashSet<>();

        for (L2Skill skill: skills){
            final L2Skill.SkillTargetType targetType = skill.getTargetType();
            if (targetType == TARGET_ONE
                    || targetType == TARGET_PARTY
                    || targetType == TARGET_PARTY_ONE
                    || targetType == TARGET_CLAN
                    || targetType == TARGET_CLAN_ONLY
                    || targetType == TARGET_COMMAND_CHANEL
            ){
                set.add(skill.getId());
            }
        }
        return new ArrayList<>(set);
    }





    private boolean canBuff(L2Player player) {
        return ConfigValue.PremiumBufferEnable ? player.getBonus().PremiumBuffer : ConfigValue.BufferUsePremiumItem <= 0 && player.hasBonus() || ConfigValue.BufferUsePremiumItem > 0 && player.getInventory().getCountOf(ConfigValue.BufferUsePremiumItem) > 0;
    }


    public void removeScheme(L2Player player, String[] arg) {
        int owner = Integer.parseInt(arg[0]);
        int schemeId = Integer.parseInt(arg[1]);
        buffService.removeScheme(owner, schemeId);
        showMainPage(player);
    }

    public void castBuff(L2Player player, String[] args){
        buffService.castBuff(player, args);
    }



    public void addBuff(L2Player player, String[] args) {

        if (!player.isGM()) {
            return;
        }

        int buff_id = Integer.parseInt(args[0]);
        int buff_level = Integer.parseInt(args[1]);
        String type = args[2];

        final L2Skill skill = SkillTable.getInstance().getInfo(buff_id, buff_level);
        if (skill != null){
            final Buff buff = new Buff(skill, type);
            buffService.createBuff(buff);
        }
        showBuffs(player, new String[]{type});
    }

    public void removeBuff(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buff_id = Integer.parseInt(args[0]);
        String type = args[1];
        final Buff buff = buffService.getBuff(buff_id);
        if (buff != null){
            buffService.removeBuff(buff);
        }
        showBuffs(player, new String[]{type});
    }

    public void clearBuffs(L2Player player, String[] args) {
        String type = args[0];

        final List<Buff> buffs = buffService.getBuffs(type);
        for (Buff buff: buffs){
            buffService.removeBuff(buff);
        }
        showBuffs(player, new String[]{type});
    }

    public void showRedactScheme(L2Player player, String[] args) {

        int schemeId = Integer.parseInt(args[0]);
        final Scheme scheme = buffService.getScheme(schemeId);
        if (scheme == null){
            return;
        }
        ShowBoard.separateAndSend(addCustomReplace(showEditSchemePage(scheme)), player);
    }

    public String showEditSchemePage(Scheme scheme){

        String page = getFile( "editSchemaPage.htm");
        String schemeButton = getFile( "schemeButton.htm");

        String simpleBackground = "L2UI_CH3.calculate1_back";
        String premiumBackground = "L2UI_CH3.br_partyon_back2";

        final List<Buff> buffList = scheme.getBuffs().values().stream()
                .map(schemeBuff -> buffService.getBuff(schemeBuff.getBuff_id())).collect(Collectors.toList());


        final List<Buff> _buffs = buffList.stream().filter(buff -> !buff.isSong()).collect(Collectors.toList());
        final List<Buff> _songs = buffList.stream().filter(Buff::isSong).collect(Collectors.toList());

        final long buffs_count = _buffs.size();
        final long songs_count = _songs.size();


        page = page.replace("<?maxCountBuff?>", String.valueOf(ConfigValue.BuffLimit))
                .replace("<?currentCountBuff?>", String.valueOf(buffs_count))
                .replace("<?maxCountSong?>", String.valueOf(ConfigValue.SongLimit))
                .replace("<?currentCountSong?>", String.valueOf(songs_count));

        int index_counter;
        for (int i = 1; i <= 52 ; i++){

            final SchemeBuff schemeBuff = scheme.getBuffs().get(i);
            String button;

            if (schemeBuff != null){
                final Buff buff = buffService.getBuff(schemeBuff.getBuff_id());

                button = schemeButton
                        .replace("<?icon?>", new Img(buff.getIcon()).build())
                        .replace("<?index?>", String.valueOf(schemeBuff.getIndex()))
                        .replace("<?schemeName?>", scheme.getName())
                        .replace("<?owner?>", String.valueOf(scheme.getOwner()))
                        .replace("<?schemeId?>", String.valueOf(scheme.getId()))
                        .replace("<?background?>", buff.getType().equals("PREMIUM") ? premiumBackground : simpleBackground);

                page = page.replace("<?buffIndex" + schemeBuff.getIndex() + "?>" , button);

            }else {
                button = schemeButton
                        .replace("<?icon?>", "")
                        .replace("<?background?>", simpleBackground)
                        .replace("<?index?>", String.valueOf(i))
                        .replace("<?schemeName?>", scheme.getName())
                        .replace("<?owner?>", String.valueOf(-100))
                        .replace("<?schemeId?>", String.valueOf(scheme.getId()));

                page = page.replace("<?buffIndex" + i + "?>" , button);
            }

        }
        return page;
    }

    public boolean checkSchemeContainBuff(Scheme scheme, int buffId){
        for (SchemeBuff schemeBuff : scheme.getBuffs().values()){
            if (schemeBuff.getBuff_id() == buffId){
                return true;
            }
        }
        return false;
    }

    public void showAddBuffToScheme(String[] args, L2Player player) {
        int schemeId = Integer.parseInt(args[0]);
        int index = Integer.parseInt(args[1]);
        String type = args[2];

        final Scheme scheme = buffService.getScheme(schemeId);

        List<Buff> buffs;

        if (index <= 36){
            buffs = buffService.getBuffs(type).stream()
                    .filter(buff -> !buff.isSong())
                    .filter(buff -> !checkSchemeContainBuff(scheme, buff.getId()))
                    .collect(Collectors.toList());
        }else {
            buffs = buffService.getBuffs(type).stream()
                    .filter(Buff::isSong)
                    .filter(buff -> !checkSchemeContainBuff(scheme, buff.getId()))
                    .collect(Collectors.toList());
        }

        final Table main = new Table(2, 1);

        final Table header = new Table(1, 3);
        header.row(0).col(0).setParams(width(100)).insert(new Button("Обычные", action("bypass -h bbs_show_add_buff_to_scheme_page " + scheme.getId() + " " + index + " SIMPLE"), 80, 32).build());
        header.row(0).col(1).setParams(width(100)).insert(new Button("Премиум", action("bypass -h bbs_show_add_buff_to_scheme_page " + scheme.getId() + " " + index + " PREMIUM"), 80, 32).build());
        header.row(0).col(2).setParams(width(80)).insert(new Button(action("bypass -h TE00"), 32, 32, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());

        final Table buffsTable = new Table(buffs.size(), 3);

        for (int i = 0; i < buffs.size(); i++){
            buffsTable.row(i).col(0).insert(new Img(buffs.get(i).getIcon()).build());
            buffsTable.row(i).col(1).insert(buffs.get(i).getName());
            buffsTable.row(i).col(2).insert(new Button("+", action("bypass -h bbs_add_buff_to_scheme " + scheme.getId() + " " + buffs.get(i).getId() + " " + index)).build());
        }

        main.row(0).col(0).insert(header.build());
        main.row(1).col(0).insert(buffsTable.build());

        player.sendPacket(new TutorialShowHtml("<html><body><title>Доступные баффы</title>" + main.build() + " </body></html>"));
    }
}