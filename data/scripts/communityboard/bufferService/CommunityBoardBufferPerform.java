package communityboard.bufferService;

import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Position;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CBBuffSchemePerform;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;

import java.util.*;
import java.util.stream.Collectors;

import static communityboard.bufferService.HTML_Elements.*;
import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class CommunityBoardBufferPerform extends BaseBBSManager implements ICommunityHandler, ScriptFile {

    private BuffService buffService;
    private static final long SYSTEM_LISTS = -1;

    private static enum Commands {
        _bbsbuffer,
        bbs_test_buffer,
        bbs_show_simple_buffs,
        bbs_show_premium_buffs,
        bbs_show_ready_set,
        bbs_show_create_ready_set,
        bbs_add_buff_set,
        bbs_remove_buff_from_set,
        bbs_remove_set,
        bbs_cast_buff,
        bbs_cast_ready_set,
        bbs_save_set,
        bbs_cast_save_set,
        bbs_show_all_buffs,
        bbs_clear_buffs,
        bbs_change_enchant_type,
        bbs_show_change_buff_params,
        bbs_change_list_index
    }

    @Override
    public void parsecmd(String bypass, L2Player player) {
        if (player.getEventMaster() != null && player.getEventMaster().blockBbs())
            return;
        if (player.is_block)
            return;
        if (!check(player))
            return;

        if (ConfigValue.BufferAffterRes) {
            long time = (player.getResTime() + (ConfigValue.BufferAffterResTime * 1000L) - System.currentTimeMillis());
            if (time > 0) {
                int wait = (int) (time / 1000);
                player.sendMessage(new CustomMessage("common.not.yet.wait", player).addNumber(wait <= 0 ? 1 : wait).addString(DifferentMethods.declension(player, wait, "Second")));
                return;
            }
        }

        StringTokenizer st = new StringTokenizer(bypass);
        String cmd = st.nextToken();
        String[] args = new String[10];
        int i = 0;
        while (st.hasMoreTokens()) {
            args[i] = st.nextToken();
            i++;
        }
        if ("_bbsbuffer".equals(cmd)) {
            showMainPage(player);
        } else if ("bbs_show_simple_buffs".equals(cmd)) {
            showBuffs(player, args);
        } else if ("bbs_show_premium_buffs".equals(cmd)) {
            showBuffs(player, args);
        } else if ("bbs_show_ready_set".equals(cmd)) {
            showBuffs(player, args);
        } else if ("bbs_show_create_ready_set".equals(cmd)) {
            showCreateReadySet(player, args);
        } else if ("bbs_add_buff_set".equals(cmd)) {
            addBuffToList(args, player);
        } else if ("bbs_add_buff_ready_set".equals(cmd)) {
            addBuffToList(args, player);
        } else if ("bbs_remove_buff_from_set".equals(cmd)) {
            removeBuffFromSet(args, player);
        } else if ("bbs_remove_buff_premium_set".equals(cmd)) {
            removeBuffFromSet(args, player);
        } else if ("bbs_remove_buff_ready_set".equals(cmd)) {
            removeBuffFromSet(args, player);
        } else if ("bbs_remove_set".equals(cmd)) {
            removeSet(player, args[0]);
        } else if ("bbs_cast_buff".equals(cmd)) {
            castBuff(player, args);
        } else if ("bbs_cast_premium_buff".equals(cmd)) {
            castBuff(player, args);
        } else if ("bbs_cast_ready_set".equals(cmd)) {
            castReadySet(player, args);
        } else if ("bbs_cast_save_set".equals(cmd)) {
            castSaveSet(player, args);
        } else if ("bbs_save_set".equals(cmd)) {
            saveSet(player, args);
        } else if ("bbs_show_all_buffs".equals(cmd)) {
            showAllBuffs(player, args);
        } else if ("bbs_clear_buffs".equals(cmd)) {
            clearBuffList(player, args);
        } else if ("bbs_change_enchant_type".equals(cmd)) {
            changeEnchantType(args, player);
        } else if ("bbs_show_change_buff_params".equals(cmd)) {
            showChangeBuffParams(args, player);
        } else if ("bbs_change_list_index".equals(cmd)) {
            changeListIndex(player, args);
        } else if ("bbs_test_buffer".equals(cmd)) {
            String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/test.htm", player);
            ShowBoard.separateAndSend(addCustomReplace(html), player);
        }
    }

    private void showChangeBuffParams(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int index = Integer.parseInt(args[0]);
        String list_type = args[1];

        String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/scheme.htm", player);

        final Table table = new Table(4, 4).setParams(width(400), border(1));

        final Buff buffModel = buffService.getBuffsList(list_type, SYSTEM_LISTS).getBuffList().get(index);
        table.row(0).col(0);
        table.row(0).col(1).insert(new Img(buffModel.getIcon()).build());
        table.row(0).col(2).insert(buffModel.getName());
        table.row(0).col(3).insert(backButtonMain());
        ShowBoard.separateAndSend(addCustomReplace(html.replace("%content%", table.build())), player);
    }




    private void changeListIndex(L2Player player, String[] args) {
        String list_type = args[0];
        int currentIndex = Integer.parseInt(args[1]);
        String direct = args[2];

        long owner = player.getObjectId();
        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
            owner = SYSTEM_LISTS;
        }

        final Scheme buffsList = buffService.getBuffsList(list_type, owner);

        if ("left".equals(direct)){
            buffService.changeListIndex(buffsList, currentIndex, currentIndex - 1);
        } else if ("right".equals(direct)) {
            buffService.changeListIndex(buffsList, currentIndex, currentIndex + 1);
        }

        showCreateReadySet(player, args);

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


    private void showCreateReadySet(L2Player player, String[] args) {

        String list_type = args[0];


        long owner = player.getObjectId();
        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
            owner = SYSTEM_LISTS;
        }

        if (!player.isGM()) {
            showMainPage(player);
            return;
        }
        final Scheme buffListModel = buffService.getBuffsList(list_type, owner);

        final List<Buff> buffModelList = buffListModel.getBuffList();
        int rows = (int) Math.ceil((ConfigValue.BuffLimit + 10) / 12.0);
        final Table buffsTable = new Table(rows, 12).setParams(cellpadding(0), cellspacing(0));

        final List<Buff> buffs = buffModelList.stream()
                .filter(buffModel -> !SkillTable.getInstance().getInfo(buffModel.getSkill_id(), buffModel.getSkill_level()).isMusic())
                .collect(Collectors.toList());
        int index = 0;
        for (int i = 0; i< buffsTable.getRows().size() ; i++){
            Row row = buffsTable.row(i);
            for (int j = 0; j <  row.getColumns().size(); j++){
                Col col = row.getColumns().get(j);
                String icon = "L2UI_CT1.Windows_DF_Drawer_Bg";
                int currentIndex = 9999;
                try {
                    icon = buffs.get(index).getIcon();
                    currentIndex = buffs.get(index).getList_index();
                }catch (Exception ignore){}

                final Table table = new Table(4, 1).setParams(cellpadding(0), cellspacing(0));
                col.setParams(align(Position.CENTER)).insert(new Img(icon).build());
                col.setParams(height(10));
                col.insert(controlButtons(buffListModel.getList_type(), currentIndex,  index != 0 && index < buffs.size(), index < buffs.size(), index < buffs.size() - 1).build());
                col.setParams(height(10));
                col.insert(table.build());
                index++;
            }
        }

        final List<Buff> songs = buffModelList.stream()
                .filter(buffModel -> SkillTable.getInstance().getInfo(buffModel.getSkill_id(), buffModel.getSkill_level()).isMusic())
                .collect(Collectors.toList());

        index = 0;
        final Table songsTable = new Table(2, ConfigValue.SongLimit).setParams(cellpadding(1), cellspacing(1));
        for (int i = 0; i < ConfigValue.SongLimit; i++){
            String icon = "L2UI_CT1.Windows_DF_Drawer_Bg";
            int currentIndex = 9999;
            try {
                icon = songs.get(index).getIcon();
                currentIndex = songs.get(index).getList_index();
            }catch (Exception ignore){}

            songsTable.row(0).col(i).setParams(align(Position.CENTER)).insert(new Img(icon).build());
            songsTable.row(1).col(i).insert(controlButtons(buffListModel.getList_type(), currentIndex, index != 0 && index < songs.size(), index < songs.size(), index < songs.size() - 1).build());
            index++;
        }

        final Table table = new Table(5, 1);
        table.row(0).col(0).insert(buffsTable.build());
        table.row(1).col(0).setParams(height(10));
        table.row(2).col(0).insert(songsTable.build());
        table.row(3).col(0);


        final Table readyListButtons = new Table(1, 4);
        readyListButtons.row(0).col(0).setParams(width(100));
        readyListButtons.row(0).col(1).setParams(width(200)).insert(new Button("simpleList", action(""), 100, 20).build());
        readyListButtons.row(0).col(2).setParams(width(200)).insert(new Button("simpleList", action(""), 100, 20).build());
        readyListButtons.row(0).col(3);
//        table.row(4).col(0).insert(readyListButtons.build());

        final String editPage = Files.read("data/scripts/services/PremiumBuffer/editSchemaTable.htm")
                .replace("%title%", "Конструктор набора баффов")
                .replace("%readySet%", table.build())
                .replace("%buttons%", readyListButtons.build());

        ShowBoard.separateAndSend(addCustomReplace(editPage), player);

    }

    private void castReadySet(L2Player player, String[] args) {
        String list_type = args[0];
        String target = args[1];

        long owner = getOwner(list_type, player);

        final Scheme buffsList = buffService.getBuffsList(list_type, owner);

        if (buffsList.getBuffList().isEmpty()) {
            showMainPage(player);
            return;
        }

        for (Buff buff : buffsList.getBuffList()) {
            if (player.getBonus().RATE_XP <= 1) {
                applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
            }
            applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
        }
        showMainPage(player);
    }

    private void showMainPage(L2Player player) {
        String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/index.htm", player);
        if (html != null) {
            final int bufferPriceOne = ConfigValue.BufferPriceOne;
            final int bufferTime = ConfigValue.BufferTime;
            html = html.replace("%price%", String.valueOf(bufferPriceOne));
            html = html.replace("%time%", String.valueOf(bufferTime));
            html = html.replace("%scheme%", getReadySets(player));
            html = html.replace("%buffgrps%", getSaveSets(player));
        }
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }

    private String getSaveSets(L2Player player) {
        final List<Scheme> readyListsByOwner = buffService.getReadyListsByOwner(player.getObjectId());
        if (readyListsByOwner == null){
            return "";
        }

        final Table table = new Table(readyListsByOwner.size(), 3);
        for (int i = 0; i < readyListsByOwner.size(); i++) {
            final String schName = readyListsByOwner.get(i).getList_type();
            table.row(i).col(0).insert(new Button(schName, action("bypass -h bbs_cast_save_set " + schName + " $Who"), 100, 25).build());
            table.row(i).col(1).insert(new Button("-", action("bypass -h bbs_remove_set " + schName ), 25, 25).build());
            table.row(i).col(2).insert(new Button("@", action("bypass -h bbs_show_create_ready_set " + schName), 25, 25).build());
        }
        return table.build();
    }

    private String getReadySets(L2Player player) {

        final List<Scheme> readyLists = buffService.getReadyLists();

        final Table table = new Table(readyLists.size() + 1, 2);
        int index = 0;
        for (Scheme readyList : readyLists) {
            String list_type = readyList.getList_type();
            table.row(index).col(0).insert(new Button("Набор для воина", action("bypass -h bbs_cast_ready_set " + list_type + " $Who"), 100, 25).build());
            if (player.isGM()) {
                table.row(index).col(1).insert(new Button("@", action("bypass -h bbs_show_ready_set " + list_type), 25, 25).build());
            }
        }
        if (player.isGM()) {
            table.row(readyLists.size()).col(0).insert(new Button("добавить", action("bypass -h bbs_show_create_ready_set $GrpNameAdd"), 100, 25).build());
        }
        return table.build();
    }

    private void clearBuffList(L2Player player, String[] args) {
        String list_type = args[0];
        long owner = getOwner(list_type, player);

        if (!player.isGM()) {
            showBuffs(player, args);
            return;
        }
        buffService.clearBuffList(list_type, owner);
        showBuffs(player, args);
    }

    private void showAllBuffs(L2Player player, String[] args) {
        String list_type = args[0];

        long owner = getOwner(list_type, player);

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
                    final String buffButton = selectBuffButton(integers.get(index), list_type, owner);
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

    private void changeEnchantType(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int index = Integer.parseInt(args[0]);
        String direction = args[1];
        String list_type = args[2];

        final Scheme buffsList = buffService.getBuffsList(list_type, SYSTEM_LISTS);
        if (buffsList == null){
            return;
        }
        final Buff buffModel = buffsList.getBuffList().get(index);
        if (buffModel == null){
            return;
        }
        final Buff old = buffModel.clone();

        buffModel.setNextEnchantType();

        buffService.updateBuff(old, buffModel, SYSTEM_LISTS);
        showBuffs(player, new String[]{list_type});
    }

    private void addBuffToList(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buff_id = Integer.parseInt(args[0]);
        int buff_level = Integer.parseInt(args[1]);
        String list_type = args[2];
        long owner = player.getObjectId();
        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
            owner = SYSTEM_LISTS;
        }

        final L2Skill skill = SkillTable.getInstance().getInfo(buff_id, buff_level);
        if (skill == null) {
            return;
        }
        final int index = buffService.getBuffsList(list_type, owner).getBuffList().size();



        final Buff buffModel = new Buff(skill, list_type, owner, index);
        buffService.createBuff(buffModel, owner);
        showBuffs(player, new String[]{list_type});
    }
    private long getOwner(String list_type, L2Player player){
        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
            return  SYSTEM_LISTS;
        }
        return player.getObjectId();
    }


    private void removeBuffFromSet(String[] args, L2Player player) {
        if (args == null) {
            showMainPage(player);
            return;
        }
        int index = Integer.parseInt(args[0]);
        String list_type = args[1];

        long owner = getOwner(list_type, player);

        final Scheme buffsList = buffService.getBuffsList(list_type, owner);
        if (buffsList.getBuffList().isEmpty()) {
            showBuffs(player, new String[]{list_type});
            return;
        }
        final Buff buffModel = buffsList.getBuffList().get(index);
        buffService.removeBuff(buffModel);
        showBuffs(player, new String[]{list_type});
    }

    private void castSaveSet(L2Player player, String[] args) {
        String setName = args[0];
        String target = args[1];

        final Scheme buffsList = buffService.getBuffsList(setName, player.getObjectId());

        if (buffsList == null) {
            return;
        }
        for (Buff buffModel: buffsList.getBuffList()) {
            int buff_level = buffModel.getSkill_level();
            if (player.getBonus().RATE_XP <= 1) {
                buff_level = buffModel.getSkill_level();
            }
            applyBuff(player, buffModel.getSkill_id(), buff_level, target);
        }
    }

    private void saveSet(L2Player player, String[] args) {
        String setName = args[0];
        if (setName == null || setName.isEmpty()) {
            return;
        }
        if (!Util.isMatchingRegexp(setName, "([0-9A-Za-z]{1,16})|([0-9\u0410-\u044f]{1,16})")) {
            player.sendMessage("Символы запрещены.");
            return;
        }
        L2Effect[] effects = player.getEffectList().getAllFirstEffects();
        Arrays.sort(effects, EffectsComparator.getInstance());


        try {
            final Scheme buffsList = buffService.getBuffsList(setName, player.getObjectId());
            player.sendMessage("Это название уже занято.");
            return;
        }catch (Exception e){
            buffService.getBUFF_CASH().put((long) player.getObjectId(), new HashMap<>());
        }

        final ArrayList<Buff> buffModels = new ArrayList<>(buffService.getOrdinaryList().getBuffList());
        buffModels.addAll(buffService.getPremiumList().getBuffList());

        final ArrayList<Buff> list = new ArrayList<>();

        for (L2Effect effect : effects) {
            final L2Skill skill = effect.getSkill();



            final Optional<Buff> first = buffModels.stream()
                    .filter(buffModel -> buffModel.getSkill_id() == skill.getId())
                    .filter(buffModel -> buffModel.getSkill_level() == skill.getLevel())
                    .findFirst();
            first.ifPresent(list::add);
        }


        for (Buff buffModel: list){
            buffService.createBuff(buffModel, player.getObjectId());
        }

        showMainPage(player);

    }

    private void removeSet(L2Player player, String name) {
        final Optional<CBBuffSchemePerform> first = player._buffSchemePerform.values().stream().filter(s -> s.SchName.equals(name)).findFirst();
        if (first.isPresent()) {
            if (player._buffSchemePerform.containsValue(first.get())) {
                player._buffSchemePerform.remove(first.get().id);

                ThreadConnection conDel = null;
                FiltredPreparedStatement statementDel = null;
                try {
                    conDel = L2DatabaseFactory.getInstance().getConnection();
                    statementDel = conDel.prepareStatement("DELETE FROM community_skillsave WHERE charId=? AND schameid=?");
                    statementDel.setInt(1, player.getObjectId());
                    statementDel.setInt(2, first.get().id);
                    statementDel.execute();
                } catch (Exception e) {
                    _log.warning("data error on Delete Teleport: " + e);
                    e.printStackTrace();
                } finally {
                    DatabaseUtils.closeConnection(conDel);
                }
            }
        }
        showMainPage(player);
    }

    private void showBuffs(L2Player player, String[] args) {
        String list_type = args[0];
        long owner = getOwner(list_type, player);

        final Table header = new Table(1, 5);
        header.row(0).col(0).setParams(width(250));
        header.row(0).col(1).setParams(width(100)).insert(player.isGM() ? new Button("Добавить", action("bypass -h bbs_show_all_buffs " + list_type), 100, 25).build() : "");
        header.row(0).col(2).setParams(width(100)).insert(player.isGM() ? new Button("Удалить все!", action("bypass -h bbs_clear_buffs " + list_type), 100, 25).build() : "");
        header.row(0).col(3).setParams(width(150)).insert("<combobox width=60 height=10 var=\"Who\" list=\"Player;Pet\">");
        header.row(0).col(4).setParams(width(100), align(Position.RIGHT)).insert(new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25, "L2UI_CT1.OlympiadWnd_DF_Back_Down", "L2UI_CT1.OlympiadWnd_DF_Back").build());

        final List<Buff> buffsList = buffService.getBuffsList(list_type, owner).getBuffList();
        int cols = 6;
        int rows = (int) Math.ceil((double) buffsList.size() / cols);
        final Table table = new Table(rows, cols);

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < buffsList.size()) {
                    table.row(i).col(j).insert(buttonBuff(index, list_type, owner));
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
        String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/scheme.htm", player);
        html = html.replace("%title%", list_type).replace("%content%", main.build());
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }

    private static boolean checkPlayerLevel(L2Playable playable) {
        return playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel);
    }

    private void castBuff(L2Player player, String[] args) {
        int index = Integer.parseInt(args[0]);
        final String list_type = args[1];
        String target = args[2];
        long owner = getOwner(list_type, player);

        final List<Buff> buffsList = buffService.getBuffsList(list_type, owner).getBuffList();

        if (buffsList.isEmpty()) {
            return;
        }
        if (index > buffsList.size()) {
            return;
        }

        final Buff buffModel = buffsList.get(index);
        if (buffModel == null) {
            return;
        }


        if (checkPlayerLevel(player)) {
            player.sendMessage("Баффер доступен для игроков с уровней не ниже " + ConfigValue.BufferMinLevel + " и не выше " + ConfigValue.BufferMaxLevel + ".");
            return;
        }
        L2Playable playable = null;
        if ("Player".equals(target)) {
            playable = player;
        } else if ("Pet".equals(target)) {
            playable = player.getPet();
        }

        if (playable == null) {
            return;
        }
        applyBuff(player, buffModel.getSkill_id(), buffModel.getSkill_level(), target);
    }

    private static void applyBuff(L2Player player, int id, int level, String target) {
        L2Skill skill = SkillTable.getInstance().getInfo(id, level);
        L2Playable playable = null;
        if ("Player".equals(target)) {
            playable = player;
        } else if ("Pet".equals(target)) {
            playable = player.getPet();
        }
        if (playable == null) {
            return;
        }
        final double hp = playable.getCurrentHp();
        final double mp = playable.getCurrentMp();
        final double cp = playable.getCurrentCp();

        if (!skill.checkSkillAbnormal(playable) && !skill.isBlockedByChar(playable, skill)) {
            for (EffectTemplate et : skill.getEffectTemplates()) {
                int result;
                Env env = new Env(playable, playable, skill);
                L2Effect effect = et.getEffect(env);
                if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                    effect.onStart();
                    effect.onActionTime();
                    effect.onExit();
                } else if (effect != null && !effect.getEffected().p_block_buff.get()) {
                    if (ConfigValue.BufferTime > 0)
                        effect.setPeriod(ConfigValue.BufferTime * 60000L);
                    if ((result = playable.getEffectList().addEffect(effect)) > 0) {
                        if ((result & 2) == 2)
                            playable.setCurrentHp(hp, false);
                        if ((result & 4) == 4)
                            playable.setCurrentMp(mp);
                        if ((result & 8) == 8)
                            playable.setCurrentCp(cp);
                    }
                }
            }
        }
        playable.updateEffectIcons();
    }


    private String buttonBuff(int index, String list_type, long owner) {
        final Table table = new Table(4, 1);
        final Buff buffModel = buffService.getBuffsList(list_type, owner).getBuffList().get(index);
        if (buffModel == null) {
            return null;
        }
        table.row(0).col(0).setParams(align(Position.CENTER)).insert(new Img(buffModel.getIcon()).build());
        table.row(1).col(0).setParams(align(Position.CENTER)).insert("<br><font color=F2C202>" + buffModel.getEnchant_name() + "</font>");
        table.row(2).col(0).setParams(align(Position.CENTER))
                .insert(new Button(formatSkillName(buffModel.getName()), action("bypass -h bbs_cast_buff " + index + " " + list_type + " $Who"), 100, 25).build());

        Table actionTable;
        if (buffModel.getSkillEnchants().isEmpty()) {
            actionTable = new Table(1, 2).setParams(cellpadding(0), cellspacing(0));
            actionTable.row(0).col(0).setParams(height(25)).insert(new Button(action("bypass -h bbs_show_change_buff_params " + index + " " + list_type), 16, 16, "L2UI_CT1.Button_DF_PartyOption2_Down", "L2UI_CT1.Button_DF_PartyOption2").build());
            actionTable.row(0).col(1).insert(new Button(action("bypass -h bbs_remove_buff_from_set " + index + " " + list_type), 16, 16, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());
        } else {
            actionTable = new Table(1, 3).setParams(cellpadding(0), cellspacing(0));
            actionTable.row(0).col(0).setParams(height(25)).insert(new Button(action("bypass -h bbs_show_change_buff_params " + index + " " + list_type), 16, 16, "L2UI_CT1.Button_DF_PartyOption2_Down", "L2UI_CT1.Button_DF_PartyOption2").build());
            actionTable.row(0).col(1).insert(new Button(action("bypass -h bbs_remove_buff_from_set " + index + " " + list_type), 16, 16, "L2UI_CT1.Button_DF_Delete_Down", "L2UI_CT1.Button_DF_Delete").build());
            actionTable.row(0).col(2).insert(new Button(action("bypass -h bbs_change_enchant_type " + index + " next " + list_type), 16, 16,"L2UI_CT1.Button_DF_Right_Down", "L2UI_CT1.Button_DF_Right").build());
        }
        table.row(3).col(0).setParams(height(25)).insert(actionTable.build());
        return table.build();
    }

    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player) {
    }


    public void onLoad() {
        buffService = new BuffService();
        CommunityHandler.getInstance().registerCommunityHandler(this);
    }

    private List<Integer> loadAllBuffs(L2Player player) {
        ArrayList<Integer> list = new ArrayList<>();
        final String[] split = Files.read("data/scripts/services/PremiumBuffer/allBuffList.txt", player).replaceAll("\\s", "").split(",");
        for (String s : split) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    private boolean check_event(L2Player player) {
        switch (player.isInEvent()) {
            case 1:
                return ConfigValue.FightClubBattleUseBuffer;
            case 2:
                return ConfigValue.LastHeroBattleUseBuffer;
            case 3:
                return ConfigValue.CaptureTheFlagBattleUseBuffer;
            case 4:
                return ConfigValue.TeamvsTeamBattleUseBuffer;
            case 5:
                return ConfigValue.TournamentBattleUseBuffer;
            case 6:
                return ConfigValue.EventBoxUseBuffer;
            case 11:
                return player.getEventMaster().state == 1;
            case 12:
                return ConfigValue.Tournament_UseBuffer;
            case 13:
                return ConfigValue.DeathMatchUseBuffer;
        }
        return false;
    }

    private boolean check(L2Player player) {
        if (player == null)
            return false;
        else if (player.isGM())
            return true;
        else if (player.isInOlympiadMode()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.getReflection().getId() != ReflectionTable.DEFAULT && !ConfigValue.BufferInInstance && !check_event(player)) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInDuel()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInCombat() && !ConfigValue.BufferInCombat) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if ((player.isOnSiegeField() || player.isInZoneBattle()) && !ConfigValue.BufferOnSiege && player.isInEvent() != 5 && !check_event(player)) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInEvent() > 0 && !check_event(player)) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isFlying()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isInWater() && !ConfigValue.BufferInWater) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (player.isDead() || player.isMovementDisabled() || player.isAlikeDead() || player.isCastingNow() || player.isAttackingNow() || player.getVar("jailed") != null || player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped()) {
            player.sendMessage(new CustomMessage("communityboard.buffer.terms.incorrect", player));
            return false;
        } else if (ConfigValue.BufferOnlyPeace && !player.isInZone(ZoneType.peace_zone) && !player.isInZone(ZoneType.epic) && player.getReflection().getId() == ReflectionTable.DEFAULT) {
            player.sendMessage("Функция доступна только в мирной зоне, эпик зоне, а так же в инстансах.");
            return false;
        } else
            return true;
    }

    private boolean canBuff(L2Player player) {
        return ConfigValue.PremiumBufferEnable ? player.getBonus().PremiumBuffer : ConfigValue.BufferUsePremiumItem <= 0 && player.hasBonus() || ConfigValue.BufferUsePremiumItem > 0 && player.getInventory().getCountOf(ConfigValue.BufferUsePremiumItem) > 0;
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    @SuppressWarnings("rawtypes")
    public Enum[] getCommunityCommandEnum() {
        return Commands.values();
    }


}