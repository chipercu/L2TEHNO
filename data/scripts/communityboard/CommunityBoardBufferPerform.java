package communityboard;

import communityboard.bufferService.BuffModel;
import communityboard.bufferService.BuffRepository;
import communityboard.bufferService.BuffService;
import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.Table;
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
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.barahlo.CBBuffSchemePerform;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.*;

import java.sql.ResultSet;
import java.util.*;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class CommunityBoardBufferPerform extends BaseBBSManager implements ICommunityHandler, ScriptFile {

    private BuffService buffService;

    private static enum BUFFS_TYPE {
        SIMPLE("simple_buffs"), PREMIUM("premium_buffs"), MAGE("mage_buffs"), FIGHTER("fighter_buffs");

        final String name;

        BUFFS_TYPE(String name) {
            this.name = name;
        }
    }

    private static enum Commands {
        _bbsbuffer,
        bbs_test_buffer,
        bbs_show_simple_buffs,
        bbs_show_premium_buffs,
        bbs_show_ready_set,
        bbs_add_buff_set,
        bbs_remove_buff_from_set,
        bbs_remove_set,
        bbs_cast_buff,
        bbs_cast_ready_set,
        bbs_save_set,
        bbs_cast_save_set,
        bbs_show_all_buffs,
        bbs_clear_buffs,
        bbs_change_enchant_type
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
            showBuffs(player, BUFFS_TYPE.SIMPLE);
        } else if ("bbs_show_premium_buffs".equals(cmd)) {
            showBuffs(player, BUFFS_TYPE.PREMIUM);
        } else if ("bbs_show_ready_set".equals(cmd)) {
            showReadySet(player, args);
        } else if ("bbs_add_buff_set".equals(cmd)) {
            addBuffToSet(args, player);
        } else if ("bbs_add_buff_ready_set".equals(cmd)) {
            addBuffReadySet(args, player);
        } else if ("bbs_remove_buff_from_set".equals(cmd)) {
            removeBuffSet(args, player);
        } else if ("bbs_remove_buff_premium_set".equals(cmd)) {
            removeBuffSet(args, player);
        } else if ("bbs_remove_buff_ready_set".equals(cmd)) {
            removeBuffReadySet(args, player);
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
            showAllBuffs(player, BUFFS_TYPE.valueOf(args[0]));
        } else if ("bbs_clear_buffs".equals(cmd)) {
            clearBuffSet(player, args);
        } else if ("bbs_change_enchant_type".equals(cmd)) {
            changeEnchantType(args, player);
        } else if ("bbs_test_buffer".equals(cmd)) {
            String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "buffer/test.htm", player);
            ShowBoard.separateAndSend(addCustomReplace(html), player);
        }
    }

    private void castReadySet(L2Player player, String[] args) {
        BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[0]);
        String target = args[1];
        final List<String> buffsFromDB = getBuffsFromDB(type);
        for (String buff : buffsFromDB) {
            final String[] split = buff.split(":");
            int buff_id = Integer.parseInt(split[0]);
            int buff_level = Integer.parseInt(split[1]);
            SkillInfo skillInfo = new SkillInfo(buff_id, buff_level);
            if (player.getBonus().RATE_XP <= 1) {
                buff_level = skillInfo.getMaxLevel();
            }
            applyBuff(player, buff_id, buff_level, target);
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
        final ArrayList<CBBuffSchemePerform> schemes = new ArrayList<>(player._buffSchemePerform.values());
        final Table table = new Table(schemes.size(), 3);
        for (int i = 0; i < schemes.size(); i++) {
            final String schName = schemes.get(i).SchName;
            table.row(i).col(0).insert(new Button(schName, action("bypass -h bbs_cast_save_set " + schName + " $Who"), 100, 25).build());
            table.row(i).col(1).insert(new Button("-", action("bypass -h bbs_remove_set " + schName), 25, 25).build());
            //table.row(i).col(2).insert(new Button("@", action("bypass -h bbs_cast_save_set " + schName), 25, 25).build());
        }
        return table.build();
    }

    private String getReadySets(L2Player player) {
        final Table table = new Table(2, 2);
        table.row(0).col(0).insert(new Button("Набор для воина", action("bypass -h bbs_cast_ready_set " + BUFFS_TYPE.FIGHTER + " $Who"), 100, 25).build());
        table.row(1).col(0).insert(new Button("Набор для мага", action("bypass -h bbs_cast_ready_set " + BUFFS_TYPE.MAGE + " $Who"), 100, 25).build());
        if (player.isGM()) {
            table.row(0).col(1).insert(new Button("@", action("bypass -h bbs_show_ready_set " + BUFFS_TYPE.FIGHTER), 25, 25).build());
            table.row(1).col(1).insert(new Button("@", action("bypass -h bbs_show_ready_set " + BUFFS_TYPE.MAGE), 25, 25).build());
        }
        return table.build();
    }

    private void clearBuffList(L2Player player, String[] args){



        String list_type = args[0];
        if (list_type == null){
            showBuffs(player, list_type);
            return;
        }
        if (!player.isGM()) {
            showBuffs(player, list_type);
            return;
        }
        buffRepository.clearBuffList(list_type);
        showBuffs(player, list_type);
    }

    private void clearBuffSet(L2Player player, String[] args) {
        if (!player.isGM()) {
            return;
        }
        BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[0]);
        updateBuffsToDB(new ArrayList<>(), type);
        showBuffs(player, type);
    }

    private void showAllBuffs(L2Player player, BUFFS_TYPE type) {
        final Table main = new Table(3, 1);
        final Table header = new Table(1, 5);
        header.row(0).col(0).setParams(width(250));
        header.row(0).col(1).setParams(width(100));
        header.row(0).col(2).setParams(width(100));
        header.row(0).col(3).setParams(width(150));
        header.row(0).col(4).setParams(width(100), align(Position.RIGHT)).insert(player.isGM() ? new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25).build() : "");

        final List<Integer> integers = loadAllBuffs(player);
        int cols = 6;
        int rows = (int) Math.ceil((double) integers.size() / cols);
        final Table table = new Table(rows, cols).setParams(width(720));

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < integers.size()) {
                    final String buffButton = selectBuffButton(integers.get(index), type);
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

    private static String selectBuffButton(int id, BUFFS_TYPE type) {
        L2Skill skill = SkillTable.getInstance().getInfo(id, 1);
        SkillInfo skillInfo = new SkillInfo(skill);
        String name = formatSkillName(skill.getName());
        final String html = Files.read("data/scripts/services/PremiumBuffer/selectBuffButton.htm");
        return html.replace("<?icon?>", skill.getIcon())
                .replace("<?name?>", name)
                .replace("<?action?>", "bypass -h bbs_add_buff_set " + skillInfo.getId() + " " + skillInfo.getMaxLevel() + " none" + " " + type);
    }


    private void changeEnchantType(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int index = Integer.parseInt(args[0]);
        String direction = args[1];
        BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[2]);

        final List<String> buffsFromDB = getBuffsFromDB(type);
        final SkillInfo buff = getBuffByIndex(index, type);
        final String newBuff = buff.getId() + ":" + buff.getLevel() + ":" + buff.getEnchantType();
        final List<String> newList = new ArrayList<>();
        for (String b : buffsFromDB) {
            if (b.equals(newBuff)) {
                if ("next".equals(direction)) {
                    int nextEnchantLevel = buff.getLevel() + buff.getEnchantMaxLevel();
                    int maxEnchantLevels = buff.getMaxLevel() + buff.getEnchants().size() * buff.getEnchantMaxLevel();
                    if (nextEnchantLevel > maxEnchantLevels) {
                        nextEnchantLevel = buff.getMaxLevel();
                    }
                    final int i = (buff.displayLevel / 100);
                    final String replace = i == buff.enchants.size() ? "none" : buff.enchants.get(Math.max(i, 0)).getType().replace("+1 ", "").replaceAll("\\s", "");
                    ;
                    final String s = buff.getId() + ":" + nextEnchantLevel + ":" + replace;
                    newList.add(s);
                } else if ("prev".equals(direction)) {
                    newList.add(b);
                }
            } else {
                newList.add(b);
            }
        }
        updateBuffsToDB(newList, type);
        showBuffs(player, type);
    }

    private void addBuffToSet(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buff_id = Integer.parseInt(args[0]);
        int buff_level = Integer.parseInt(args[1]);
        String enchant = args[2];
        BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[3]);
        final List<String> buffsFromDB = getBuffsFromDB(type);
        final String newBuff = buff_id + ":" + buff_level + ":" + enchant;
        if (buffsFromDB.contains(newBuff)) {
            return;
        }
        buffsFromDB.add(newBuff);
        updateBuffsToDB(buffsFromDB, type);
        showBuffs(player, type);
    }

    private void addBuffReadySet(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int buff_id = Integer.parseInt(args[0]);
        int buff_level = Integer.parseInt(args[1]);
        String setName = args[2];
        final BUFFS_TYPE type = BUFFS_TYPE.valueOf(setName);
        final List<String> buffsFromDB = getBuffsFromDB(type);
        final String newBuff = buff_id + ":" + buff_level;
        if (buffsFromDB.contains(newBuff)) {
            return;
        }
        buffsFromDB.add(newBuff);
        updateBuffsToDB(buffsFromDB, type);
        showReadySet(player, new String[]{});
    }

    private void removeBuffSet(String[] args, L2Player player) {
        if (!player.isGM()) {
            return;
        }
        int index = Integer.parseInt(args[0]);
        BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[1]);
        final SkillInfo buff = getBuffByIndex(index, type);
        final String newBuff = buff.getId() + ":" + buff.getLevel() + ":" + buff.getEnchantType();
        final List<String> buffsFromDB = getBuffsFromDB(type);
        List<String> newBuffsList = new ArrayList<>();
        for (String b : buffsFromDB) {
            if (!b.equals(newBuff)) {
                newBuffsList.add(b);
            }
        }
        updateBuffsToDB(newBuffsList, type);
        showBuffs(player, type);
    }

    private void removeBuffReadySet(String[] args, L2Player player) {
        int buff_id = Integer.parseInt(args[0]);
        int buff_level = Integer.parseInt(args[1]);
        String setName = args[2];
        final BUFFS_TYPE type = BUFFS_TYPE.valueOf(setName);
        final List<String> buffsFromDB = getBuffsFromDB(type);
        final String removeBuff = buff_id + ":" + buff_level;
        List<String> newBuffsList = new ArrayList<>();
        for (String buff : buffsFromDB) {
            if (!buff.equals(removeBuff)) {
                newBuffsList.add(buff);
            }
        }
        updateBuffsToDB(newBuffsList, type);
        showReadySet(player, new String[]{});
    }

    private boolean checkIfBuffExistInSet(String buff, BUFFS_TYPE type) {
        return getBuffsFromDB(type).contains(buff);
    }


    private void castSaveSet(L2Player player, String[] args) {
        String setName = args[0];
        String target = args[1];
        final Optional<CBBuffSchemePerform> first = player._buffSchemePerform.values().stream().filter(e -> e.SchName.equals(setName)).findFirst();
        if (first.isPresent()) {
            final CBBuffSchemePerform schema = first.get();
            for (String buff : schema._buffList) {
                final String[] split = buff.split(":");
                int buff_id = Integer.parseInt(split[0]);
                int buff_level = Integer.parseInt(split[1]);
                SkillInfo skillInfo = new SkillInfo(buff_id, buff_level);

                if (player.getBonus().RATE_XP <= 1) {
                    buff_level = skillInfo.getMaxLevel();
                }
                applyBuff(player, buff_id, buff_level, target);
            }
        }
    }

    private static SkillInfo getBuffByIndex(int index, BUFFS_TYPE type) {
        final String[] split = getBuffsFromDB(type).get(index).split(":");
        final int id = Integer.parseInt(split[0]);
        final int level = Integer.parseInt(split[1]);
        return new SkillInfo(id, level);
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
        L2Effect[] skills = player.getEffectList().getAllFirstEffects();
        Arrays.sort(skills, EffectsComparator.getInstance());
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            boolean _name = true;
            for (CBBuffSchemePerform scheme : player._buffSchemePerform.values()) {
                if (scheme.SchName.equalsIgnoreCase(setName)) {
                    _name = false;
                    break;
                }
            }

            if (_name) {
                StringBuilder allBuff = new StringBuilder();
                ArrayList<String> sch = new ArrayList<>();
                int id;
                for (L2Effect skill : skills) {
                    final SkillInfo skillInfo = new SkillInfo(skill.getSkill());
                    final String buff = skillInfo.getId() + ":" + skillInfo.getLevel() + ":" + skillInfo.getEnchantType();
                    if (checkIfBuffExistInSet(buff, BUFFS_TYPE.SIMPLE) || checkIfBuffExistInSet(buff, BUFFS_TYPE.PREMIUM)) {
                        allBuff.append(buff).append(";");
                        sch.add(buff);
                    }
                }

                statement = con.prepareStatement("INSERT INTO community_skillsave (charId,name,skills) VALUES(?,?,?)");
                statement.setInt(1, player.getObjectId());
                statement.setString(2, setName);
                statement.setString(3, allBuff.toString());
                statement.execute();
                DatabaseUtils.closeStatement(statement);

                statement = con.prepareStatement("SELECT schameid FROM community_skillsave WHERE charId=? AND name=?;");
                statement.setInt(1, player.getObjectId());
                statement.setString(2, setName);
                rs = statement.executeQuery();
                rs.next();
                id = rs.getInt(1);

                String[] arr = new String[sch.size()];
                final String[] strings = sch.toArray(arr);
                player._buffSchemePerform.put(id, new CBBuffSchemePerform(id, setName, strings));
                sch = null;
            } else
                player.sendMessage("Это название уже занято.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
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

    private void showBuffs(L2Player player, String list_type) {
        final Table header = new Table(1, 5);
        header.row(0).col(0).setParams(width(250));
        header.row(0).col(1).setParams(width(100)).insert(player.isGM() ? new Button("Добавить", action("bypass -h bbs_show_all_buffs " + type), 100, 25).build() : "");
        header.row(0).col(2).setParams(width(100)).insert(player.isGM() ? new Button("Удалить все!", action("bypass -h bbs_clear_buffs " + type), 100, 25).build() : "");
        header.row(0).col(3).setParams(width(150)).insert("<combobox width=60 height=10 var=\"Who\" list=\"Player;Pet\">");
        header.row(0).col(4).setParams(width(100), align(Position.RIGHT)).insert(player.isGM() ? new Button("Назад!", action("bypass -h _bbsbuffer"), 100, 25).build() : "");

        final List<BuffModel> buffsList = buffService.getBuffsList(list_type);
        int cols = 6;
        int rows = (int) Math.ceil((double) buffsList.size() / cols);
        final Table table = new Table(rows, cols);

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < buffsList.size()) {
                    table.row(i).col(j).insert(buttonBuff(index, list_type));
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

    private void showReadySet(L2Player player, String[] args) {
        if (!player.isGM()) {
            return;
        }
        final BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[0]);
        showBuffs(player, type);
    }

    private static boolean checkPlayerLevel(L2Playable playable) {
        return playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel);
    }

    private static List<String> getBuffsFromDB(BUFFS_TYPE type) {
        List<String> buffList = new ArrayList<>();
        final String string = ServerVariables.getString(type.name);
        if (string != null && !string.isEmpty()) {
            final String[] split = string.split(";");
            buffList.addAll(Arrays.asList(split));
        }
        return buffList;
    }

    private void updateBuffsToDB(List<String> buffs, BUFFS_TYPE type) {
        String stringBuffs = String.join(";", buffs);
        ServerVariables.set(type.name, stringBuffs);
    }

    private static void castBuff(L2Player player, String[] args) {
        int index = Integer.parseInt(args[0]);
        final BUFFS_TYPE type = BUFFS_TYPE.valueOf(args[1]);
        String target = args[2];
        final SkillInfo buff = getBuffByIndex(index, type);

        if (type == BUFFS_TYPE.PREMIUM && player.getBonus().RATE_XP <= 1) {
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
        applyBuff(player, buff.getId(), buff.getLevel(), target);
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


    private static String buttonBuff(int index, BUFFS_TYPE type) {
        final Table table = new Table(4, 1);

        final String[] s = getBuffsFromDB(type).get(index).split(":");
        SkillInfo skillInfo = new SkillInfo(Integer.parseInt(s[0]), Integer.parseInt(s[1]));

        table.row(0).col(0).setParams(align(Position.CENTER)).insert(new Img(skillInfo.getIcon()).build());
        table.row(1).col(0).setParams(align(Position.CENTER)).insert("<br><font color=F2C202>" + skillInfo.getEnchantType() + "</font>");
        table.row(2).col(0).setParams(align(Position.CENTER))
                .insert(new Button(formatSkillName(skillInfo.getName()), action("bypass -h bbs_cast_buff " + index + " " + type + " $Who"), 100, 25).build());

        Table actionTable;
        if (skillInfo.getEnchants().isEmpty()) {
            actionTable = new Table(1, 1).setParams(cellpadding(0), cellspacing(0));
            actionTable.row(0).col(0).insert(new Button("-", action("bypass -h bbs_remove_buff_from_set " + index + " " + type), 50, 25).build());
        } else {
            actionTable = new Table(1, 3).setParams(cellpadding(0), cellspacing(0));
            actionTable.row(0).col(0).setParams(height(25)).insert(new Button("<", action("bypass -h bbs_change_enchant_type " + index + " prev " + type), 30, 25).build());
            actionTable.row(0).col(1).insert(new Button("-", action("bypass -h bbs_remove_buff_from_set " + index + " " + type), 30, 25).build());
            actionTable.row(0).col(2).insert(new Button(">", action("bypass -h bbs_change_enchant_type " + index + " next " + type), 30, 25).build());
        }
        table.row(3).col(0).setParams(height(25)).insert(actionTable.build());
        return table.build();
    }

    private static String formatSkillName(String name) {
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

    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player) {
    }



    public void onLoad() {
        buffService = new BuffService();

        CommunityHandler.getInstance().registerCommunityHandler(this);
        for (BUFFS_TYPE type : BUFFS_TYPE.values()) {
            try {
                final String simple_buffs = ServerVariables.getString(type.name);
                if (simple_buffs == null) {
                    ServerVariables.set(type.name, "");
                }
            } catch (Exception e) {
                ServerVariables.set(type.name, "");
            }
        }
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

    public static class SkillInfo {
        private int id;
        private String name;
        private String icon;
        private int displayLevel;
        private int level;
        private int maxLevel = 1;
        private String enchantType = "none";
        private int enchantMaxLevel = 1;
        private GArray<L2EnchantSkillLearn> enchants;
        private GArray<L2EnchantSkillLearn> enchantsForChange;

        public SkillInfo(L2Skill skill) {
            this.id = skill.getId();
            this.name = skill.getName();
            this.icon = skill.getIcon();
            this.displayLevel = skill.getDisplayLevel();
            this.level = skill.getLevel();

            this.enchants = SkillTreeTable.getFirstEnchantsForSkill(skill.getId());
            this.enchantsForChange = SkillTreeTable.getEnchantsForChange(skill.getId(), skill.getLevel());
            if (enchants != null && !enchants.isEmpty()) {
                final int i = (displayLevel / 100) - 1;
                final L2EnchantSkillLearn skillEnchant = enchants.get(Math.max(i, 0));
                this.maxLevel = skillEnchant.getBaseLevel();
                if (displayLevel > 100) {
                    this.enchantType = skillEnchant.getType().replace("+1 ", "").replaceAll("\\s", "");
                }
                this.enchantMaxLevel = skillEnchant.getMaxLevel();
            }
        }

        public SkillInfo(int id, int level) {
            final L2Skill skill = SkillTable.getInstance().getInfo(id, level);
            this.id = skill.getId();
            this.name = skill.getName();
            this.icon = skill.getIcon();
            this.displayLevel = skill.getDisplayLevel();
            this.level = skill.getLevel();

            this.enchants = SkillTreeTable.getFirstEnchantsForSkill(skill.getId());
            this.enchantsForChange = SkillTreeTable.getEnchantsForChange(skill.getId(), skill.getLevel());
            if (enchants != null && !enchants.isEmpty()) {
                final int i = (displayLevel / 100) - 1;
                final L2EnchantSkillLearn skillEnchant = enchants.get(Math.max(i, 0));
                this.maxLevel = skillEnchant.getBaseLevel();
                if (displayLevel > 100) {
                    this.enchantType = skillEnchant.getType().replace("+1 ", "").replaceAll("\\s", "");
                }
                this.enchantMaxLevel = skillEnchant.getMaxLevel();
            }
        }


        public GArray<L2EnchantSkillLearn> getEnchants() {
            return enchants;
        }

        public int getId() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        public GArray<L2EnchantSkillLearn> getEnchantsForChange() {
            return enchantsForChange;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public int getDisplayLevel() {
            return displayLevel;
        }

        public int getEnchantMaxLevel() {
            return enchantMaxLevel;
        }

        public void setEnchantMaxLevel(int enchantMaxLevel) {
            this.enchantMaxLevel = enchantMaxLevel;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getEnchantType() {
            return enchantType;
        }

        public void setEnchantType(String enchantType) {
            this.enchantType = enchantType;
        }

        public void setEnchants(GArray<L2EnchantSkillLearn> enchants) {
            this.enchants = enchants;
        }
    }


}