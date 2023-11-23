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
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.EffectsComparator;
import l2open.util.Files;
import l2open.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static communityboard.html.buffer.Elements.*;
import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.gameserver.communitybbs.Manager.BaseBBSManager.addCustomReplace;

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

//    public void showCreateReadySet(L2Player player, String[] args) {
//        String schemeName = args[0];
//        Long owner = Long.parseLong(args[1]);
//
//        if (!player.isGM()) {
//            showMainPage(player);
//            return;
//        }
//
//        final Scheme scheme = buffService.getScheme(owner, schemeName).get();
//
//        final List<SchemeBuff> buffModelList = scheme.getBuffs();
//        int rows = (int) Math.ceil((ConfigValue.BuffLimit + 10) / 12.0);
//        final Table buffsTable = new Table(rows, 12).setParams(cellpadding(0), cellspacing(0));
//
//        final List<Buff> buffs = buffModelList.stream()
//                .filter(buffModel -> !SkillTable.getInstance().getInfo((int) buffModel.getSkill_id(), (int) buffModel.getSkill_level()).isMusic())
//                .collect(Collectors.toList());
//        int index = 0;
//        for (int i = 0; i< buffsTable.getRows().size() ; i++){
//            Row row = buffsTable.row(i);
//            for (int j = 0; j <  row.getColumns().size(); j++){
//                Col col = row.getColumns().get(j);
//                String icon = "L2UI_CT1.Windows_DF_Drawer_Bg";
//                int currentIndex = 9999;
//                try {
//                    icon = buffs.get(index).getIcon();
//                    currentIndex = buffs.get(index).getList_index();
//                }catch (Exception ignore){}
//
//                final Table table = new Table(4, 1).setParams(cellpadding(0), cellspacing(0));
//                col.setParams(align(Position.CENTER)).insert(new Img(icon).build());
//                col.setParams(height(10));
//                col.insert(controlButtons(scheme.getList_type(), currentIndex,  index != 0 && index < buffs.size(), index < buffs.size(), index < buffs.size() - 1).build());
//                col.setParams(height(10));
//                col.insert(table.build());
//                index++;
//            }
//        }
//
//        final List<Buff> songs = buffModelList.stream()
//                .filter(buffModel -> SkillTable.getInstance().getInfo(buffModel.getSkill_id(), buffModel.getSkill_level()).isMusic())
//                .collect(Collectors.toList());
//
//        index = 0;
//        final Table songsTable = new Table(2, ConfigValue.SongLimit).setParams(cellpadding(1), cellspacing(1));
//        for (int i = 0; i < ConfigValue.SongLimit; i++){
//            String icon = "L2UI_CT1.Windows_DF_Drawer_Bg";
//            int currentIndex = 9999;
//            try {
//                icon = songs.get(index).getIcon();
//                currentIndex = songs.get(index).getList_index();
//            }catch (Exception ignore){}
//
//            songsTable.row(0).col(i).setParams(align(Position.CENTER)).insert(new Img(icon).build());
//            songsTable.row(1).col(i).insert(controlButtons(scheme.getList_type(), currentIndex, index != 0 && index < songs.size(), index < songs.size(), index < songs.size() - 1).build());
//            index++;
//        }
//
//        final Table table = new Table(5, 1);
//        table.row(0).col(0).insert(buffsTable.build());
//        table.row(1).col(0).setParams(height(10));
//        table.row(2).col(0).insert(songsTable.build());
//        table.row(3).col(0);
//
//
//        final Table readyListButtons = new Table(1, 4);
//        readyListButtons.row(0).col(0).setParams(width(100));
//        readyListButtons.row(0).col(1).setParams(width(200)).insert(new Button("simpleList", action(""), 100, 20).build());
//        readyListButtons.row(0).col(2).setParams(width(200)).insert(new Button("simpleList", action(""), 100, 20).build());
//        readyListButtons.row(0).col(3);
////        table.row(4).col(0).insert(readyListButtons.build());
//
//        final String editPage = Files.read("data/scripts/services/PremiumBuffer/editSchemaTable.htm")
//                .replace("%title%", "Конструктор набора баффов")
//                .replace("%readySet%", table.build())
//                .replace("%buttons%", readyListButtons.build());
//
//        ShowBoard.separateAndSend(addCustomReplace(editPage), player);
//
//    }

    public void castScheme(L2Player player, String[] args) {
        String schemeName = args[0];
        int owner = Integer.parseInt(args[1]);
        String target = args[2];

        final Optional<Scheme> scheme = buffService.getScheme(owner, schemeName);

        if (scheme.isPresent()){
            final List<SchemeBuff> buffs = scheme.get().getBuffs();
            for (SchemeBuff schemeBuff : buffs) {
                Buff buff = buffService.getBuff(schemeBuff.getBuff_id());
                if (player.getBonus().RATE_XP <= 1) {
                    applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
                }
                applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
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
            html = html.replace("%buffgrps%", getSaveSchemes(player));
        }
        ShowBoard.separateAndSend(addCustomReplace(html), player);
    }

    private String getSaveSchemes(L2Player player) {
        final List<Scheme> schemes = buffService.getSchemes(player.getObjectId());
        if (schemes == null){
            return "";
        }

        final Table table = new Table(schemes.size() + 1 , 3);
        for (int i = 0; i < schemes.size(); i++) {
            final Scheme scheme = schemes.get(i);
            table.row(i).col(0).insert(new Button(scheme.getName(), action("bypass -h bbs_cast_save_set " + + scheme.getOwner() + " " + scheme.getId() + " $Who"), 130, 32).build());
            table.row(i).col(1).insert(new Button("@", action("bypass -h bbs_show_redact_scheme " + scheme.getOwner() + " " + scheme.getId()), 32, 32).build());
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
            table.row(index).col(0).insert(new Button(scheme.getName(), action("bypass -h bbs_cast_scheme " + scheme.getId() + " " + scheme.getOwner() + " $Who"), 130, 32).build());
            if (player.isGM()) {
                table.row(index).col(1).insert(new Button("@", action("bypass -h bbs_show_ready_set " + scheme.getId()), 32, 32).build());
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
        int buff_id = Integer.parseInt(args[0]);
        int buff_level = Integer.parseInt(args[1]);
        String type = args[2];

        final L2Skill skill = SkillTable.getInstance().getInfo(buff_id, buff_level);
        if (skill != null){
            new Buff(skill, type);
        }



//        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
//            owner = SYSTEM_LISTS;
//        }
//
//        final L2Skill skill = SkillTable.getInstance().getInfo(buff_id, buff_level);
//        if (skill == null) {
//            return;
//        }
//        final int index = buffService.getBuffsList(list_type, owner).getBuffList().size();
//
//
//
//        final Buff buffModel = new Buff(skill, list_type, owner, index);
//        buffService.createBuff(buffModel, owner);
        showBuffs(player, new String[]{type});
    }

    private long getOwner(String list_type, L2Player player){
        if (list_type.equals("ORDINARY") || list_type.equals("PREMIUM")){
            return  SYSTEM_LISTS;
        }
        return player.getObjectId();
    }

    public void removeBuffFromScheme(String[] args, L2Player player) {
        if (args == null) {
            showMainPage(player);
            return;
        }
        int buffId = Integer.parseInt(args[0]);
        String schemeName = args[1];
        int owner = Integer.parseInt(args[2]);

        final Optional<Scheme> optionalScheme = buffService.getScheme(owner, schemeName);

        if (optionalScheme.isPresent()) {
            final Scheme scheme = optionalScheme.get();
            buffService.removeBuffFromScheme(scheme, buffId);
            showBuffs(player, new String[]{schemeName});
        }else {
            showBuffs(player, new String[]{schemeName});
        }

    }

    public void saveScheme(L2Player player, String[] args) {
        String schemeName = args[0];
        if (schemeName == null || schemeName.isEmpty()) {
            return;
        }
        if (!Util.isMatchingRegexp(schemeName, "([0-9A-Za-z]{1,16})|([0-9\u0410-\u044f]{1,16})")) {
            player.sendMessage("Символы запрещены.");
            return;
        }
        L2Effect[] effects = player.getEffectList().getAllFirstEffects();
        Arrays.sort(effects, EffectsComparator.getInstance());

        final Optional<Scheme> schemeOptional = buffService.getScheme(player.getObjectId(), schemeName);
        if (schemeOptional.isPresent()){
            player.sendMessage("Это название уже занято.");
            return;
        }else {
            final Scheme scheme = new Scheme(player.getObjectId(), schemeName);
            buffService.createScheme(scheme);
        }
        showMainPage(player);

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

    private static boolean checkPlayerLevel(L2Playable playable) {
        return playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel);
    }

    public void castBuff(L2Player player, String[] args) {
        int buffId = Integer.parseInt(args[0]);
        String target = args[1];

        final Buff buff = buffService.getBuff(buffId);
        if (buff == null) {
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
        applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
    }

    private static void applyBuff(L2Player player, long id, long level, String target) {
        L2Skill skill = SkillTable.getInstance().getInfo((int) id, (int) level);
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

    private List<Integer> loadAllBuffs(L2Player player) {
        ArrayList<Integer> list = new ArrayList<>();
        final String[] split = Files.read(BufferConfig.ALL_BUFFS_FILE, player).replaceAll("\\s", "").split(",");
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

    public boolean check(L2Player player) {
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


    public void removeScheme(L2Player player, String[] arg) {
        int owner = Integer.parseInt(arg[0]);
        int schemeId = Integer.parseInt(arg[1]);
        buffService.removeScheme(owner, schemeId);
        showMainPage(player);
    }

    public void createReadyScheme(L2Player player, String[] args) {
        if (!player.isGM()){
            return;
        }

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

        final Scheme scheme = new Scheme(SYSTEM_LISTS, schemeName.toString());
        buffService.createScheme(scheme);
        showMainPage(player);
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
}