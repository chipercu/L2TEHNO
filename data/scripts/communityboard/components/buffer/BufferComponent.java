package communityboard.components.buffer;

import communityboard.config.BufferConfig;
import communityboard.html.buffer.Elements;
import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import communityboard.service.buffer.BuffService;
import l2open.common.HtmlBuilder.Button;
import l2open.common.HtmlBuilder.Edit;
import l2open.common.HtmlBuilder.Img;
import l2open.common.HtmlBuilder.Table;
import l2open.common.Html_Builder_JSOUP.Elements.enums.EditType;
import l2open.common.HtmlBuilder.components.Position;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.TutorialCloseHtml;
import l2open.gameserver.serverpackets.TutorialShowHtml;
import l2open.gameserver.tables.SkillTable;
import l2open.util.EffectsComparator;
import l2open.util.Files;

import java.util.*;
import java.util.stream.Collectors;

import static communityboard.html.buffer.Elements.backButtonMain;
import static communityboard.html.buffer.Elements.getFile;
import static l2open.common.HtmlBuilder.parameters.Parameters.*;
import static l2open.gameserver.communitybbs.Manager.BaseBBSManager.addCustomReplace;

public class BufferComponent {

    private final BuffService buffService;
    private BufferConfig bufferConfig;
    private static final int SYSTEM_LISTS = -1;

    public BufferComponent(BuffService buffService) {
        this.buffService = buffService;
        this.bufferConfig = BufferConfig.getInstance();
    }

    public void setConfig(L2Player player, String[] args) {
        if (!player.isGM()){
            return;
        }

        String configName = args[0];
        int value = Integer.parseInt(args[1]);
        buffService.setConfig(configName, value);
        buffService.loadBufferConfig();
        showBufferConfigPage(player);
    }

    public void showChangeBuffParams(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buffId = Integer.parseInt(args[0]);
        String type = args[1];

        String html = Files.read(BufferConfig.HTML_PATCH + "scheme.htm", player);

        final Table table = new Table(4, 4).setParams(width(400), border(1));

        final Buff buff = buffService.getBuff(buffId);
        table.row(0).col(0);
        table.row(0).col(1).insert(new Img(buff.getIcon()).build());
        table.row(0).col(2).insert(buff.getName());
        table.row(0).col(3).insert(backButtonMain());
        ShowBoard.separateAndSend(addCustomReplace(html.replace("%content%", table.build())), player);
    }

    public void castScheme(L2Player player, String[] args) {
        int schemeId = Integer.parseInt(args[0]);
        String target = args[1];
        final Scheme scheme = buffService.getScheme(schemeId);
        if (scheme != null) {
            final List<SchemeBuff> schemeBuffs = scheme.getBuffs().values().stream().sorted(Comparator.comparingInt(SchemeBuff::getIndex)).collect(Collectors.toList());
            for (SchemeBuff schemeBuff : schemeBuffs) {
                Buff buff = buffService.getBuff(schemeBuff.getBuff_id());
                if (player.getBonus().RATE_XP <= 1 && buff.getType().equals("PREMIUM")) {
                    player.sendMessage("У вас нет активирован премиум-статус!");
                    continue;
                }
                final boolean pay = DifferentMethods.getPay(player,
                        buff.getType().equals("PREMIUM") ? BufferConfig.getInstance().getDefaultPremiumBuffItem() : BufferConfig.getInstance().getDefaultSimpleBuffItem(),
                        buff.getType().equals("PREMIUM") ? BufferConfig.getInstance().getDefaultPremiumBuffPrice() : BufferConfig.getInstance().getDefaultSimpleBuffPrice(),
                        true);

                if (pay){
                    buffService.applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
                }
            }
        }
        showMainPage(player);
    }

    public void castScheme(L2Player player, String schemeName, String target){
        final Scheme scheme = buffService.getScheme(player.getObjectId(), schemeName);
        if (scheme != null) {
            final List<SchemeBuff> schemeBuffs = scheme.getBuffs().values().stream().sorted(Comparator.comparingInt(SchemeBuff::getIndex)).collect(Collectors.toList());
            for (SchemeBuff schemeBuff : schemeBuffs) {
                Buff buff = buffService.getBuff(schemeBuff.getBuff_id());
                if (player.getBonus().RATE_XP <= 1 && buff.getType().equals("PREMIUM")) {
                    player.sendMessage("У вас нет активирован премиум-статус!");
                    continue;
                }
                final boolean pay = DifferentMethods.getPay(player,
                        buff.getType().equals("PREMIUM") ? BufferConfig.getInstance().getDefaultPremiumBuffItem() : BufferConfig.getInstance().getDefaultSimpleBuffItem(),
                        buff.getType().equals("PREMIUM") ? BufferConfig.getInstance().getDefaultPremiumBuffPrice() : BufferConfig.getInstance().getDefaultSimpleBuffPrice(),
                        true);

                if (pay){
                    buffService.applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
                }
            }
        }
    }

    public void showMainPage(L2Player player) {
        String html = Files.read(BufferConfig.HTML_PATCH + "index.htm", player);
        if (html != null) {
            final int bufferPriceOne = bufferConfig.getDefaultSimpleBuffPrice();
            final int bufferTime = bufferConfig.getBuffTime();
            html = html.replace("%price%", String.valueOf(bufferPriceOne));
            html = html.replace("%time%", String.valueOf(bufferTime));
            html = html.replace("%scheme%", getSystemSchemes(player));
            html = html.replace("%buffgrps%", getPersonalSchemes(player));
            final Button settingsButton = new Button("Общие настройки", action("bypass -h bbs_buffer_global_settings"), 150, 32);
            html = html.replace("<?globalSettingsButton?>", player.isGM()? settingsButton.build() : "");
        }
        player.sendPacket(TutorialCloseHtml.STATIC);
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }

    private String getPersonalSchemes(L2Player player) {
        final List<Scheme> schemes = buffService.getSchemes(player.getObjectId());
        if (schemes == null) {
            return "";
        }

        final Table table = new Table(schemes.size() + 1, 3);
        for (int i = 0; i < schemes.size(); i++) {
            final Scheme scheme = schemes.get(i);
            table.row(i).col(0).insert(new Button(scheme.getName(), action("bypass -h bbs_cast_scheme " + scheme.getId() + " $Who"), 130, 32).build());
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
        showRedactScheme(player, new String[]{String.valueOf(schemeId)});
    }

    public static <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            partitions.add(list.subList(i, end));
        }

        return partitions;
    }

    public void showAllBuffsWindow(L2Player player, String[] args) {
        if (!player.isGM()) {
            return;
        }
        String type = args[0];
        int communityPage = Integer.parseInt(args[1]);
        int page = Integer.parseInt(args[2]) - 1;

        final List<Integer> integers = loadAllBuffs(player);
        final List<List<Integer>> lists = partitionList(integers, 30);
        int rows = (int) Math.ceil((double) 30 / 6);

        final Table main = new Table(rows + 1, 6);
        main.row(0).col(0).setParams(width(50)).insert(new Button("1", action("bypass -h bbs_show_all_buffs " + type + " " + communityPage + " 1")).build());
        main.row(0).col(1).setParams(width(50)).insert(new Button("2", action("bypass -h bbs_show_all_buffs " + type + " " + communityPage + " 2")).build());
        main.row(0).col(2).setParams(width(50)).insert(new Button("3", action("bypass -h bbs_show_all_buffs " + type + " " + communityPage + " 3")).build());
        main.row(0).col(3).setParams(width(50)).insert(new Button("4", action("bypass -h bbs_show_all_buffs " + type + " " + communityPage + " 4")).build());
        main.row(0).col(4).setParams(width(50)).insert(new Button("5", action("bypass -h bbs_show_all_buffs " + type + " " + communityPage + " 5")).build());
        main.row(0).col(5).setParams(width(32)).insert(new Button(action("bypass -h TE00"), 32, 32, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());

        int index = 0;
        try {
            List<Integer> group = lists.get(page);
            if (page < group.size()) {
                for (int i = 1; i <= rows; i++) {
                    for (int j = 0; j < 6; j++) {
                        if (index < group.size()) {
                            final L2Skill skill = SkillTable.getInstance().getInfo(group.get(index), 1);
                            final Table table = new Table(2, 1);
                            table.row(0).col(0).insert(new Img(skill.getIcon()).build());
                            table.row(1).col(0).insert(new Button("+", action("bypass -h bbs_add_buff " + skill.getId() + " " + communityPage + " " + type), 32, 20).build());
                            main.row(i).col(j).insert(table.build());
                        }
                        index++;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        player.sendPacket(new TutorialShowHtml("<html><body><title>Доступные баффы</title>" + main.build() + " </body></html>"));
    }

    public void changeEnchantType(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buffId = Integer.parseInt(args[0]);
        String type = args[1];
        String page = args[2];

        final Buff buff = buffService.getBuff(buffId);

        if (buff != null) {
            buff.setNextEnchantType();
            buffService.updateBuff(buff);
        }
        showBuffs(player, new String[]{type, page});
    }

    public void addBuffToScheme(String[] args, L2Player player) {
//        if (!player.isGM()) {
//            return;
//        }
        int schemeId = Integer.parseInt(args[0]);
        int buffId = Integer.parseInt(args[1]);
        int index = Integer.parseInt(args[2]);

        buffService.addBuffInScheme(schemeId, buffId, index);
        showRedactScheme(player, args);
        player.sendPacket(TutorialCloseHtml.STATIC);
    }

    public void removeBuffFromScheme(String[] args, L2Player player) {
        if (args == null) {
            showMainPage(player);
            return;
        }
        int index = Integer.parseInt(args[0]);
        String schemeName = args[1];
        int owner = Integer.parseInt(args[2]);
        final Scheme scheme = buffService.getScheme(owner, schemeName);
        if (scheme != null) {
            buffService.removeBuffFromScheme(scheme, index);
            showRedactScheme(player, new String[]{String.valueOf(scheme.getId())});
        }
    }

    public void createScheme(L2Player player, String[] args, int owner) {
        if (args == null || args.length < 1) {
            return;
        }
        StringBuilder schemeName = new StringBuilder();
        for (String s : args) {
            if (s == null) {
                continue;
            }
            schemeName.append(s).append(" ");
        }

        if (schemeName.toString().isEmpty()) {
            player.sendMessage("Название не может быть пустым.");
            return;
        }

        final Scheme temp = buffService.getScheme(owner, schemeName.toString());
        if (temp != null) {
            player.sendMessage("Это название уже занято.");
            return;
        } else {
            final Scheme scheme = new Scheme(owner, schemeName.toString());
            final Scheme scheme1 = buffService.createScheme(scheme);

            L2Effect[] effects = player.getEffectList().getAllFirstEffects();
            Arrays.sort(effects, EffectsComparator.getInstance());

            int buff_index = 1;
            int song_index = 37;

            for (L2Effect effect : effects) {
                final L2Skill skill = effect.getSkill();
                final Buff buff = buffService.getBuffs().stream()
                        .filter(b -> b.getSkill_id() == skill.getId())
                        .filter(b -> b.getSkill_level() == skill.getLevel())
                        .findFirst().orElse(null);

                if (buff != null) {
                    if (buff.isSong()) {
                        if (song_index < bufferConfig.getSongLimit() + song_index) {
                            buffService.addBuffInScheme(scheme1.getId(), buff.getId(), song_index);
                            song_index++;
                        }
                    } else {
                        if (buff_index < bufferConfig.getBuffLimit()) {
                            buffService.addBuffInScheme(scheme1.getId(), buff.getId(), buff_index);
                            buff_index++;
                        }
                    }
                }
            }
        }
        showMainPage(player);
    }

    public void createPersonalScheme(L2Player player, String[] args) {
        createScheme(player, args, player.getObjectId());
    }

    public void createSystemScheme(L2Player player, String[] args) {
        if (!player.isGM()) {
            return;
        }
        createScheme(player, args, SYSTEM_LISTS);
    }

    public void showBuffs(L2Player player, String[] args) {
        String type = args[0];
        int page = Integer.parseInt(args[1]);

        final List<Buff> buffs = buffService.getBuffs(type);
        List<Buff> lists = new ArrayList<>();
        if (buffs.size() > 0) {
            lists = partitionList(buffs, 24).get(page - 1);
        }

        final int pages = (int) Math.ceil((double) buffs.size() / 24);

        final Table pagesTable = new Table(1, pages);

        for (int i = 0; i < pages; i++) {
            pagesTable.row(0).col(i).insert(new Button(String.valueOf(i + 1), action("bypass -h bbs_show_buffs " + type + " " + (i + 1)), 32, 32).build());
        }

        final Table header = new Table(1, 5);
        header.row(0).col(0).setParams(width(250)).insert(pagesTable.build());
        header.row(0).col(1).setParams(width(100)).insert(player.isGM() ? new Button("Добавить", action("bypass -h bbs_show_all_buffs " + type + " " + page + " 1"), 100, 25).build() : "");
        header.row(0).col(2).setParams(width(100)).insert(player.isGM() ? new Button("Удалить все!", action("bypass -h bbs_clear_buffs " + type), 100, 25).build() : "");
        header.row(0).col(3).setParams(width(150)).insert("<combobox width=60 height=10 var=\"Who\" list=\"Player;Pet\">");
        header.row(0).col(4).setParams(width(100), align(Position.RIGHT)).insert(new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25, "L2UI_CT1.OlympiadWnd_DF_Back_Down", "L2UI_CT1.OlympiadWnd_DF_Back").build());

        int cols = 6;
        int rows = 4;
        final Table table = new Table(rows, cols);

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < lists.size()) {
                    final Buff buff = lists.get(index);
                    table.row(i).col(j).insert(Elements.showBuffsButton(player, buff, page));
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

    public void removeScheme(L2Player player, String[] arg) {
        int owner = Integer.parseInt(arg[0]);
        int schemeId = Integer.parseInt(arg[1]);
        buffService.clearScheme(owner, schemeId);
        buffService.removeScheme(owner, schemeId);
        showMainPage(player);
    }

    public void castBuff(L2Player player, String[] args) {
        buffService.castBuff(player, args);
    }

    public void addBuff(L2Player player, String[] args) {

        if (!player.isGM()) {
            return;
        }

        int buff_id = Integer.parseInt(args[0]);
        int communityPage = Integer.parseInt(args[1]);
        String type = args[2];

        final L2Skill skill = SkillTable.getInstance().getInfo(buff_id, 1);
        if (skill != null) {
            final Buff buff = new Buff(skill, type);
            buffService.createBuff(buff);
        }
        final int pages = (int) Math.ceil((double) buffService.getBuffs(type).size() / 24);

        showBuffs(player, new String[]{type, String.valueOf(pages)});
    }

    public void removeBuff(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buff_id = Integer.parseInt(args[0]);
        String type = args[1];
        String page = args[2];
        final Buff buff = buffService.getBuff(buff_id);
        if (buff != null) {
            buffService.removeBuff(buff);
        }
        showBuffs(player, new String[]{type, page});
    }

    public void clearBuffs(L2Player player, String[] args) {
        String type = args[0];

        final List<Buff> buffs = buffService.getBuffs(type);
        for (Buff buff : buffs) {
            buffService.removeBuff(buff);
        }
        showBuffs(player, new String[]{type, "1"});
    }

    public void showRedactScheme(L2Player player, String[] args) {

        int schemeId = Integer.parseInt(args[0]);
        final Scheme scheme = buffService.getScheme(schemeId);
        if (scheme == null) {
            return;
        }
        ShowBoard.separateAndSend(addCustomReplace(showEditSchemePage(scheme)), player);
    }

    public String showEditSchemePage(Scheme scheme) {

        String page = getFile("editSchemaPage.htm");
        String schemeButton = getFile("schemeButton.htm");
        String simpleBackground = "L2UI_CH3.calculate1_back";
        String premiumBackground = "L2UI_CH3.br_partyon_back2";

        final List<Buff> buffList = scheme.getBuffs().values().stream()
                .map(schemeBuff -> buffService.getBuff(schemeBuff.getBuff_id())).collect(Collectors.toList());

        final boolean empty = buffList.isEmpty();
        final List<Buff> _buffs = empty ? new ArrayList<>() : buffList.stream().filter(buff -> !buff.isSong()).collect(Collectors.toList());
        final List<Buff> _songs = empty ? new ArrayList<>() : buffList.stream().filter(Buff::isSong).collect(Collectors.toList());
        final long buffs_count = _buffs.size();
        final long songs_count = _songs.size();

        page = page.replace("<?maxCountBuff?>", String.valueOf(bufferConfig.getBuffLimit()))
                .replace("<?currentCountBuff?>", String.valueOf(buffs_count))
                .replace("<?maxCountSong?>", String.valueOf(bufferConfig.getSongLimit()))
                .replace("<?schemeId?>", String.valueOf(scheme.getId()))
                .replace("<?owner?>", String.valueOf(scheme.getOwner()))
                .replace("<?schemeName?>", scheme.getName())
                .replace("<?currentCountSong?>", String.valueOf(songs_count));

        for (int i = 1; i <= 52; i++) {

            final SchemeBuff schemeBuff = scheme.getBuffs().get(i);
            String button;

            if (schemeBuff != null) {
                final Buff buff = buffService.getBuff(schemeBuff.getBuff_id());

                button = schemeButton
                        .replace("<?icon?>", new Img(buff.getIcon()).build())
                        .replace("<?index?>", String.valueOf(schemeBuff.getIndex()))
                        .replace("<?schemeName?>", scheme.getName())
                        .replace("<?owner?>", String.valueOf(scheme.getOwner()))
                        .replace("<?schemeId?>", String.valueOf(scheme.getId()))
                        .replace("<?background?>", buff.getType().equals("PREMIUM") ? premiumBackground : simpleBackground);

                page = page.replace("<?buffIndex" + schemeBuff.getIndex() + "?>", button);

            } else {
                button = schemeButton
                        .replace("<?icon?>", "")
                        .replace("<?background?>", simpleBackground)
                        .replace("<?index?>", String.valueOf(i))
                        .replace("<?schemeName?>", scheme.getName())
                        .replace("<?owner?>", String.valueOf(-100))
                        .replace("<?schemeId?>", String.valueOf(scheme.getId()));

                page = page.replace("<?buffIndex" + i + "?>", button);
            }
        }
        return page;
    }

    public boolean checkSchemeContainBuff(Scheme scheme, int buffId) {
        for (SchemeBuff schemeBuff : scheme.getBuffs().values()) {
            if (schemeBuff.getBuff_id() == buffId) {
                return true;
            }
        }
        return false;
    }

    private Table settingsTable(String title, String settingsName, int value){
        final Table main = new Table(3, 1);
        main.row(0).col(0).setParams(align(Position.CENTER)).insert(title);

        final Table table = new Table(1, 3);
        table.row(0).col(0).setParams(width(50)).insert(String.valueOf(value));
        table.row(0).col(1).setParams(width(100)).insert(new Edit(settingsName, 50, 12, EditType.num, 10).build());
        table.row(0).col(2).setParams(width(100)).insert(new Button("Применить", action("bypass -h bbs_buffer_save_config " + settingsName + " $" + settingsName), 70, 20).build());

        main.row(1).col(0).insert(table.build());
        main.row(2).col(0).setParams(height(1)).insert(new Img("l2ui.squaregray" , 250, 1).build());

        return main;
    }


    public void showBufferConfigPage(L2Player player){
        if (!player.isGM()){
            return;
        }
        final Table main = new Table(10, 1);
        main.row(0).col(0).setParams(valign(Position.RIGHT)).insert(new Button(action("bypass -h TE00"), 32, 32, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());
        main.row(1).col(0).insert(settingsTable("<br>Цена на бафф из обычного списка", "defaultSimpleBuffPrice", bufferConfig.getDefaultSimpleBuffPrice()).build());
        main.row(2).col(0).insert(settingsTable("<br>ID итема", "defaultSimpleBuffItem", bufferConfig.getDefaultSimpleBuffItem()).build());
        main.row(3).col(0).insert(settingsTable("<br>Цена на бафф из премиум списка","defaultPremiumBuffPrice", bufferConfig.getDefaultPremiumBuffPrice()).build());
        main.row(4).col(0).insert(settingsTable("<br>ID итема","defaultPremiumBuffItem", bufferConfig.getDefaultPremiumBuffItem()).build());
        main.row(5).col(0).insert(settingsTable("<br>Лимит баффов","buffLimit", bufferConfig.getBuffLimit()).build());
        main.row(6).col(0).insert(settingsTable("<br>Лимит Сонгов/Денсов","songLimit", bufferConfig.getSongLimit()).build());
        main.row(7).col(0).insert(settingsTable("<br>Мин. лвл для использования баффера","minLevel", bufferConfig.getMinLevel()).build());
        main.row(8).col(0).insert(settingsTable("<br>Макс. лвл для использования баффера","maxLevel", bufferConfig.getMaxLevel()).build());
        main.row(9).col(0).insert(settingsTable("<br>Время наложения баффа в минутах","buffTime", bufferConfig.getBuffTime()).build());
        player.sendPacket(new TutorialShowHtml("<html><body><title>Общие настройки баффера</title>" + main.build() + " </body></html>"));
    }

    public void showAddBuffToScheme(String[] args, L2Player player) {
        int schemeId = Integer.parseInt(args[0]);
        int index = Integer.parseInt(args[1]);
        String type = args[2];
        int page = Integer.parseInt(args[3]);
        final Scheme scheme = buffService.getScheme(schemeId);

        List<Buff> buffs;
        if (index <= 36) {
            buffs = buffService.getBuffs(type).stream()
                    .filter(buff -> !buff.isSong())
                    .filter(buff -> !checkSchemeContainBuff(scheme, buff.getId()))
                    .collect(Collectors.toList());
        } else {
            buffs = buffService.getBuffs(type).stream()
                    .filter(Buff::isSong)
                    .filter(buff -> !checkSchemeContainBuff(scheme, buff.getId()))
                    .collect(Collectors.toList());
        }

        final List<List<Buff>> partitionList = partitionList(buffs, 16);


        final Table main = new Table(3, 1);
        final Table header = new Table(1, 3);
        header.row(0).col(0).setParams(width(100)).insert(new Button("Обычные", action("bypass -h bbs_show_add_buff_to_scheme_page " + scheme.getId() + " " + index + " SIMPLE 1"), 80, 32).build());
        header.row(0).col(1).setParams(width(100)).insert(new Button("Премиум", action("bypass -h bbs_show_add_buff_to_scheme_page " + scheme.getId() + " " + index + " PREMIUM 1"), 80, 32).build());
        header.row(0).col(2).setParams(width(80)).insert(new Button(action("bypass -h TE00"), 32, 32, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());

        final Table pageButtons = new Table(1, partitionList.size());
        for (int i = 0; i < partitionList.size(); i++){
            pageButtons.row(0).col(i).insert(new Button(String.valueOf(i + 1), action("bypass -h bbs_show_add_buff_to_scheme_page " + scheme.getId() + " " + index + " " + type + " " + (i + 1))).build());
        }

        List<Buff> buffList = new ArrayList<>();
        if (!partitionList.isEmpty()){
            buffList = partitionList.get(page - 1);
        }

        final Table buffsTable = new Table(buffList.size() + 1, 3);
        for (int i = 0; i < buffList.size(); i++) {
            final Buff buff = buffList.get(i);
            buffsTable.row(i).col(0).insert(new Img(buff.getIcon()).build());
            buffsTable.row(i).col(1).insert(buff.getName() + " : <font color=\"LEVEL\">" + buff.getEnchant_name() + "</font>");
            buffsTable.row(i).col(2).insert(new Button("+", action("bypass -h bbs_add_buff_to_scheme " + scheme.getId() + " " + buff.getId() + " " + index)).build());
        }

        main.row(0).col(0).insert(header.build());
        main.row(1).col(0).insert(pageButtons.build());
        main.row(2).col(0).insert(buffsTable.build());

        player.sendPacket(new TutorialShowHtml("<html><body><title>Доступные баффы</title>" + main.build() + " </body></html>"));
    }
}