package l2open.gameserver.communitybbs.PartyMaker;

import com.sun.tools.javac.util.List;
import l2open.common.Html_Constructor.tags.*;
import l2open.common.Html_Constructor.tags.parameters.Parameters;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassType2;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static l2open.common.Html_Constructor.tags.parameters.Position.*;
import static l2open.gameserver.communitybbs.PartyMaker.PartyMaker.ICON.*;

public class PartyMaker extends Functions implements ScriptFile, Parameters {

    private static final Map<Integer, PartyMakerGroup> partyMakerGroupMap = new HashMap<>();
    private static final String bypass = "bypass -h party_maker:";
    private static int border = 0;

    enum ICON {
        zaken("icon.accessory_earring_of_zaken_i00"),
        freya("icon.accessary_queen_of_ice_necklace_i00"),
        rb7("icon.big_dragon_bone_summon"),
        defaultParty("icon.action011"),
        leader("L2UI_CT1.clan_DF_clanwaricon_bluecrownleader"),
        None("icon.action010"),
        Warrior("icon.skill0758"),
        Knight("icon.skill0528"),
        Rogue("icon.skill0759"),
        Healer("icon.skill0058"),
        Enchanter("icon.skill1062"),
        Summoner("icon.skill1557"),
        Wizard("icon.skill0433"),
        frinteza("icon.accessory_necklace_of_frintessa_i00");




        String icon;

        ICON(String icon) {
            this.icon = icon;
        }
    }


    private static PartyMaker _instance;

    public static PartyMaker getInstance() {
        if (_instance == null)
            _instance = new PartyMaker();
        return _instance;
    }

    public void handleCommands(L2GameClient client, String command) {

        L2Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (command.startsWith("showCreateDialog")) {
            showCreateDialog(player);
        } else if (command.startsWith("showGroups")) {
            showGroups(player);
        } else if (command.startsWith("createGroup")) {
            final String[] params = command.split(" ");
            try {
                createGroup(player, Strings.parseInt(params[1]), Strings.parseInt(params[2]), params[3], params[4]);
            } catch (Exception e) {
                showGroups(player);
            }
        } else if (command.startsWith("subscribe")) {
            final String[] params = command.split(" ");
            try {
                subscribe(player, Strings.parseInt(params[1]));
            }catch (Exception e){
                showGroups(player);
            }
        } else if (command.startsWith("myGroup")) {
            myGroup(player);
        } else if (command.startsWith("deletePlayer")) {
            System.out.println("delete player");
        }
    }
    public void subscribe(L2Player player, int groupId){

        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(groupId);
        partyMakerGroup.getAcceptedPlayers().add(player.getObjectId());
        showGroups(player);
        final L2Player leader = L2ObjectsStorage.getPlayer(partyMakerGroup.groupLeaderId);
        if (leader != null){
            showGroups(leader);
        }
    }

    public void createGroup(L2Player player, int minLevel, int maxLevel, String instance, String description) {
        final PartyMakerGroup partyMakerGroup = new PartyMakerGroup(minLevel, maxLevel, player.getObjectId(), description, instance);
        partyMakerGroupMap.put(player.getObjectId(), partyMakerGroup);
        showGroups(player);
    }

    public void myGroup(L2Player player) {

        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(player.getObjectId());
        if (partyMakerGroup == null) {
            showGroups(player);
            return;
        }
        StringBuilder HTML = new StringBuilder("<title>Моя группа</title>");
        Table mainTable = new Table(partyMakerGroup.getAcceptedPlayers().size() + 3, 1).setParams(border(border), width(320));

        final Table header = new Table(1, 4).setParams(width(320), background("l2ui_ct1.Windows_DF_TooltipBG"));
        header.row(0).col(0).setParams(height(20), width(64)).insert("Профа");
        header.row(0).col(1).setParams(height(20), width(100)).insert("Имя");
        header.row(0).col(2).setParams(height(20), width(64)).insert("Уровень");
        header.row(0).col(3).setParams(height(20), width(52)).insert("Удалить");

        mainTable.row(0).col(0).setParams(height(40), width(320)).setParams(align(CENTER), valign(BOTTOM)).insert(partyMakerGroup.getDescription());
        mainTable.row(1).col(0).setParams(height(20), width(320)).insert(header.build());

        final L2Player creator = L2ObjectsStorage.getPlayer(partyMakerGroup.getGroupLeaderId());
        if (creator != null) {
            mainTable.row(2).col(0).setParams(height(20), width(320)).insert(playerRow(creator, true).build());
        }
        for (int i = 3; i < partyMakerGroup.getAcceptedPlayers().size(); i++) {
            final L2Player member = L2ObjectsStorage.getPlayer(partyMakerGroup.getAcceptedPlayers().get(i - 3));
            mainTable.row(i).col(0).setParams(height(40), width(320)).insert(playerRow(member, false).build());
        }

        sendDialog(player, HTML.append(mainTable.build()).toString());
    }

    private Img getMemberIcon(L2Player player){
        final ClassType2 type2 = player.getClassId().getType2();
        String memberIcon = None.icon;
        if (type2 != null) {
            switch (type2){
                case Warrior:{
                    memberIcon = Warrior.icon;
                    break;
                }
                case Wizard:{
                    memberIcon = Wizard.icon;
                    break;
                }
                case Rogue:{
                    memberIcon = Rogue.icon;
                    break;
                }
                case Enchanter:{
                    memberIcon = Enchanter.icon;
                    break;
                }
                case Knight:{
                    memberIcon = Knight.icon;
                    break;
                }
                case Healer:{
                    memberIcon = Healer.icon;
                    break;
                }
                case Summoner:{
                    memberIcon = Summoner.icon;
                    break;
                }
            }
        }
        return new Img(memberIcon, 32, 32);
    }

    private Table playerRow(L2Player player, boolean leader) {
        final Table row = new Table(1, 4).setParams(width(320), background("l2ui_ct1.Windows_DF_TooltipBG"));
        row.row(0).col(0).setParams(height(40), width(64)).insert(getMemberIcon(player).build());
        row.row(0).col(1).setParams(height(40), width(100), align(CENTER)).insert(player.getName());
        row.row(0).col(2).setParams(height(40), width(64)).insert(String.valueOf(player.getLevel()));
        String insert;
        if (leader) {
            insert = new Img(ICON.leader.icon, 32, 32).build();
        } else {
            insert = new Button("X", action(bypass + "deletePlayer " + player.getObjectId()), 32, 32).build();
        }
        row.row(0).col(3).setParams(height(40), width(52)).setParams(align(CENTER), valign(TOP)).insert(insert);
        return row;
    }

    public void deletePlayer() {

    }

    public void showGroups(L2Player player) {

        StringBuilder HTML = new StringBuilder("<title>Группы</title>");
        Table mainTable = new Table(partyMakerGroupMap.size() + 3, 1)
                .setParams(border(border), width(320));

        mainTable.row(0).col(0).setParams(height(20), width(320)).insert("");
        Table buttonsTable = new Table(1, 6).setParams(border(border), background("l2ui_ct1.Windows_DF_TooltipBG"));
        buttonsTable.row(0).col(0).setParams(width(30), height(32)).insert("");
        buttonsTable.row(0).col(1).setParams(width(76), height(32)).insert(new Button("Отмена", action(""), 60, 32).build());
        buttonsTable.row(0).col(2).setParams(width(30), height(32)).insert("");
        buttonsTable.row(0).col(3).setParams(width(76), height(32)).insert(new Button("Создать", action(bypass + "showCreateDialog"), 60, 32).build());
        buttonsTable.row(0).col(4).setParams(width(30), height(32)).insert("");
        buttonsTable.row(0).col(5).setParams(width(76), height(32)).insert(new Button("Моя группа", action(bypass + "myGroup"), 60, 32).build());

        mainTable.row(1).col(0).setParams(height(20), width(320)).insert(buttonsTable.build());

        final Table header = new Table(1, 5).setParams(width(320), background("l2ui_ct1.Windows_DF_TooltipBG"));
        header.row(0).col(0).setParams(height(20), width(32)).insert("Тип");
        header.row(0).col(1).setParams(height(20), width(186)).insert("Описание");
        header.row(0).col(2).setParams(height(20), width(70)).insert("Лидер");
        header.row(0).col(3).setParams(height(20), width(32)).insert("");

        mainTable.row(2).col(0).setParams(height(32), width(320)).insert(header.build());

        int count = 3;
        for (Map.Entry<Integer, PartyMakerGroup> group : partyMakerGroupMap.entrySet()) {
            final Table table = new Table(1, 4).setParams(border(border), width(320), background("l2ui_ct1.Windows_DF_TooltipBG"));
            table.row(0).col(0).setParams(width(32), height(40)).insert(groupImage(group.getValue()).build());
            table.row(0).col(1).setParams(width(186), height(40)).insert("   " + group.getValue().getDescription());
            final L2Player creator = L2ObjectsStorage.getPlayer(group.getValue().getGroupLeaderId());
            table.row(0).col(2).setParams(width(70), height(40)).insert(creator.getName());

            if (group.getValue().getAcceptedPlayers().contains(player.getObjectId())){
                table.row(0).col(3).setParams(width(32), height(40)).insert(new Button("-", action(""), 32, 32).build());
            }else {
                table.row(0).col(3).setParams(width(32), height(40)).insert(new Button("+", action(bypass + "subscribe " + group.getValue().groupLeaderId), 32, 32).build());
            }

            mainTable.row(count).col(0).setParams(height(40), width(320)).insert(table.build());
            count++;
        }
        sendDialog(player, HTML.append(mainTable.build()).toString());

    }

    private Img groupImage(PartyMakerGroup group) {
        String groupIcon;
        if (group.getInstance().equals("Zaken")) {
            groupIcon = zaken.icon;
        } else if (group.getInstance().equals("Freya")) {
            groupIcon = freya.icon;
        } else if (group.getInstance().equals("Frinteza")) {
            groupIcon = frinteza.icon;
        } else if (group.getInstance().equals("7RB")) {
            groupIcon = rb7.icon;
        } else {
            groupIcon = defaultParty.icon;
        }
        return new Img(groupIcon, 32, 32);
    }


    public void showCreateDialog(L2Player player) {
        String descriptionText = "Description Description Description Description Description Description Description Description Description ";
        StringBuilder HTML = new StringBuilder("<title>Создание группы</title>");
        Table mainTable = new Table(8, 1)
                .setParams(border(border), width(320));
        Table levelTable = new Table(2, 5).setParams(border(border), width(320), background("l2ui_ct1.Windows_DF_TooltipBG"));
        levelTable.row(0).col(0).setParams(height(20), width(60)).insert("<center>Мин.Ур.</center>");
        levelTable.row(0).col(1).setParams(height(20), width(10)).insert("");
        levelTable.row(0).col(2).setParams(height(20), width(60)).insert("<center>Макс.Ур.</center>");
        levelTable.row(1).col(0).setParams(height(32), width(60)).insert("<center>" + new Edit("minLevel").build() + "</center>");
        levelTable.row(1).col(1).setParams(height(20), width(10)).insert("");
        levelTable.row(1).col(2).setParams(height(32), width(60)).insert("<center>" + new Edit("maxLevel").build() + "</center>");
        levelTable.row(0).col(3).setParams(height(20), width(10)).insert("");
        levelTable.row(1).col(3).setParams(height(32), width(10)).insert("");
        levelTable.row(0).col(4).setParams(height(20), width(60)).insert("<center>Выбор Инсты</center>");
        final List<String> instances = List.of("Freya", "Zaken", "Frinteza", "7RB");
        levelTable.row(1).col(4).setParams(height(32), width(60)).insert(new Combobox("instance", instances).setParams(width(60)).build());

        mainTable.row(0).col(0).setParams(height(20)).insert("");
        mainTable.row(1).col(0).setParams(height(60), align(CENTER), valign(TOP)).insert("<center>" + descriptionText + "</center>");
        mainTable.row(2).col(0).setParams(height(20)).insert("");
        mainTable.row(3).col(0).setParams(height(20)).insert(levelTable.build());
        mainTable.row(4).col(0).setParams(height(20)).insert("");
        mainTable.row(5).col(0).setParams(height(20)).insert(new Edit("description").setParams(width(320), height(50)).build());
        mainTable.row(4).col(0).setParams(height(20)).insert("");
        Table buttonsTable = new Table(1, 5).setParams(background("l2ui_ct1.Windows_DF_TooltipBG"));
        buttonsTable.row(0).col(0).setParams(width(30), height(32)).insert("");
        buttonsTable.row(0).col(1).setParams(width(60), height(32))
                .insert(new Button("Отмена", action(bypass + "showGroups"), 80, 32).build());
        buttonsTable.row(0).col(2).setParams(width(30), height(32)).insert("");
        buttonsTable.row(0).col(3).setParams(width(60), height(32))
                .insert(new Button("Создать", action(bypass + "createGroup $minLevel $maxLevel $instance $description"), 80, 32).build());
        buttonsTable.row(0).col(3).setParams(width(30), height(32)).insert("");

        mainTable.row(7).col(0).setParams(height(20)).insert(buttonsTable.build());

        sendDialog(player, HTML.append(mainTable.build()).toString());

    }

    public void sendDialog(L2Player player, String html) {
        final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        player.sendPacket(npcHtmlMessage.setHtml(html));

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    public class PartyMakerGroup {

        private int minLevel;
        private int maxLevel;
        private int groupLeaderId;
        private java.util.List<Integer> acceptedPlayers;
        private java.util.List<Integer> candidates;

        private String description;
        private String instance;

        public PartyMakerGroup(int minLevel, int maxLevel, int groupLeaderId, String description, String instance) {
            this.acceptedPlayers = new ArrayList<>();
            this.candidates = new ArrayList<>();
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.groupLeaderId = groupLeaderId;
            this.description = description;
            this.instance = instance;
        }

        public java.util.List<Integer> getAcceptedPlayers() {
            return acceptedPlayers;
        }

        public void setAcceptedPlayers(java.util.List<Integer> acceptedPlayers) {
            this.acceptedPlayers = acceptedPlayers;
        }

        public java.util.List<Integer> getCandidates() {
            return candidates;
        }

        public void setCandidates(java.util.List<Integer> candidates) {
            this.candidates = candidates;
        }

        public String getInstance() {
            return instance;
        }

        public void setInstance(String instance) {
            this.instance = instance;
        }

        public void setMinLevel(int minLevel) {
            this.minLevel = minLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public void setGroupLeaderId(int groupLeaderId) {
            this.groupLeaderId = groupLeaderId;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getMinLevel() {
            return minLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public int getGroupLeaderId() {
            return groupLeaderId;
        }

        public String getDescription() {
            return description;
        }
    }


}
