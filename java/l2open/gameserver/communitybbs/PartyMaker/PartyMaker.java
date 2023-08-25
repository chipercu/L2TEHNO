package l2open.gameserver.communitybbs.PartyMaker;

import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Font;
import l2open.common.Html_Constructor.tags.Img;
import l2open.common.Html_Constructor.tags.parameters.Color;
import l2open.common.Html_Constructor.tags.parameters.Parameters;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.listener.CharListenerList;
import l2open.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.TutorialShowHtml;
import l2open.gameserver.skills.skillclasses.Call;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.Strings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;
import static l2open.gameserver.communitybbs.PartyMaker.PartyMaker.STATUS.*;

public class PartyMaker extends Functions implements ScriptFile, OnPlayerPartyLeaveListener {

    private final Map<Integer, PartyMakerGroup> partyMakerGroupMap = new HashMap<>();
    private static final String bypass = "bypass -h party_maker:";
    private static final String groupTypes = "Freya;Zaken;Frinteza;ZI;Labyrinth;Kamaloka;7RB;LevelUp;Farm;Spoil";
    private static final Map<String, Integer> classIcons = new HashMap<>();
    private static final Map<String, String> instanceIcons = new HashMap<>();
    public static int SUMMON_PRICE = 1;

    enum STATUS{
        leader, member
    }


    public void changeLeaderOrDelete(L2Player player) {
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(player.getObjectId());
        if (partyMakerGroup != null) {
            if (player.getParty() != null) {
                final L2Player partyMember = player.getParty().getPartyMember(0);
                partyMakerGroup.setLeader(partyMember);
            } else {
                partyMakerGroupMap.remove(player.getObjectId());
            }
        }
    }

    @Override
    public void onPartyLeave(L2Player player) {
        changeLeaderOrDelete(player);
    }

    public PartyMaker() {
        CharListenerList.addGlobal(this);
        loadClassIcons();
        loadInstanceIcons();
    }

    private static PartyMaker _instance;

    public static PartyMaker getInstance() {
        if (_instance == null) {
            _instance = new PartyMaker();
        }
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
        } else if (command.startsWith("deleteGroup")) {
            confirmDeleteGroup(player);
        } else if (command.startsWith("createGroup")) {
            final String[] params = command.split(" ");
            try {
                StringBuilder description = new StringBuilder();
                for (int i = 4; i < params.length; i++) {
                    description.append(params[i]).append(" ");
                }
                createGroup(player, Strings.parseInt(params[1]), Strings.parseInt(params[2]), params[3], description.toString());
            } catch (Exception e) {
                showGroups(player);
            }
        } else if (command.startsWith("subscribe")) {
            final String[] params = command.split(" ");
            try {
                subscribe(player, Strings.parseInt(params[1]));
            } catch (Exception e) {
                showGroups(player);
            }
        } else if (command.startsWith("unscribe")) {
            final String[] params = command.split(" ");
            try {
                unscribe(player, Strings.parseInt(params[1]));
            } catch (Exception e) {
                showGroups(player);
            }
        } else if (command.startsWith("playerInfo")) {
            final String[] params = command.split(" ");
            playerInfo(player, params[1]);
        } else if (command.startsWith("detailPlayerInfo")) {
            final String[] params = command.split(" ");
            playerInfoDetail(player, params[1]);
        } else if (command.startsWith("announce")) {
            groupAnons(player);
        } else if (command.startsWith("acceptToParty")) {
            final String[] params = command.split(" ");
            acceptToParty(player, params[1], params[2]);
        } else if (command.startsWith("excludeFromParty")) {
            final String[] params = command.split(" ");
            confirmExcludeFromParty(player, params[1]);
        } else if (command.startsWith("myGroup")) {
            showGroup(player);
        } else if (command.startsWith("toLeader")) {
            toLeader(player);
        }
    }

    public Map<Integer, PartyMakerGroup> getPartyMakerGroupMap() {
        return partyMakerGroupMap;
    }

    public void deleteGroup(L2Player player) {
        final PartyMakerGroup group = partyMakerGroupMap.get(player.getObjectId());
        if (group != null && group.getLeader().getObjectId() == player.getObjectId()) {
            partyMakerGroupMap.remove(player.getObjectId());
            showGroups(player);
        }
    }
    public void confirmDeleteGroup(L2Player player){
        player.scriptRequest("Вы уверены что хотите удалить группу?", "PartyMaker:deleteGroup", new Object[0]);
        showGroup(player);
    }


    private void groupAnons(L2Player player) {
        final PartyMakerGroup group = partyMakerGroupMap.get(player.getObjectId());
        if (group != null) {
            for (L2Player p : L2ObjectsStorage.getPlayers()) {
                if (p != null) {
                    p.sendPacket(new Say2(0, 16, "PARTY_MAKER", "Идет сбор группы на "
                            + group.getInstance() + " , ур. " + group.getMinLevel() + "-" + group.getMaxLevel()
                            + ", лидер : " + group.getLeader().getName()));
                }
            }
        }
        showGroup(player);
    }

    private void acceptToParty(L2Player player, String playerId, String groupId) {
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(Integer.parseInt(groupId));
        if (partyMakerGroup != null) {
            final L2Party party = partyMakerGroup.getLeader().getParty();
            if (party != null) {
                final L2Player candidate = L2ObjectsStorage.getPlayer(Integer.parseInt(playerId));
                if (candidate != null) {
                    partyMakerGroupMap.remove(candidate.getObjectId());
                    candidate.joinParty(party);
                    removeCandidateFromAllGroups(candidate);
                    showGroup(player);
                    showGroup(candidate);
                }
            } else {
                final L2Party l2Party = new L2Party(player, 1);
                player.setParty(l2Party);
                final L2Player candidate = L2ObjectsStorage.getPlayer(Integer.parseInt(playerId));
                if (candidate != null) {
                    candidate.joinParty(l2Party);
                    partyMakerGroup.getCandidates().remove(candidate);
                    removeCandidateFromAllGroups(candidate);
                    showGroup(player);
                    showGroup(candidate);
                }
            }
        }
    }

    private void Info(L2Player player, String playerId, String file) {
        final L2Player playerInfo = L2ObjectsStorage.getPlayer(Integer.parseInt(playerId));
        if (playerInfo != null) {
            L2ItemInstance weapon = playerInfo.getActiveWeaponInstance();
            final L2ItemInstance armor = playerInfo.getInventory().getPaperdollItem(10);
            final String page = Files.read(file, player).
                    replace("<?playerName?>", playerInfo.getName()).
                    replace("<?playerClassIcon?>", getMemberIcon(playerInfo)).
                    replace("<?playerClass?>", playerInfo.getClassId().name().toUpperCase()).
                    replace("<?playerlevel?>", String.valueOf(playerInfo.getLevel())).
                    replace("<?WeaponIcon?>", weapon != null ? playerInfo.getActiveWeaponItem().getIcon() : "icon.NOICON").
                    replace("<?WeaponEnchant?>", weapon != null ? String.valueOf(weapon.getEnchantLevel()) : "0").
                    replace("<?AttIcon?>", instanceIcons.get("weapon_att")).
                    replace("<?WeaponAtt?>", weapon != null ? String.valueOf(weapon.getAttackElementValue()) : "0").
                    replace("<?ArmorIcon?>", armor != null ? armor.getItem().getIcon() : "icon.NOICON").
                    replace("<?FreyaIcon?>", instanceIcons.get("Freya")).
                    replace("<?freya?>", checkInstance(playerInfo, 139, 144) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?ZakenIcon?>", instanceIcons.get("Zaken")).
                    replace("<?zaken?>", checkInstance(playerInfo, 114, 133, 135) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?FrintezaIcon?>", instanceIcons.get("Frinteza")).
                    replace("<?Frinteza?>", checkInstance(playerInfo, 136) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?ZIIcon81?>", instanceIcons.get("ZI")).
                    replace("<?ZI81?>", checkInstance(playerInfo, 131) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?ZIIcon83?>", instanceIcons.get("ZI")).
                    replace("<?ZI83?>", checkInstance(playerInfo, 132) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?KamalokaIcon?>", instanceIcons.get("Kamaloka")).
                    replace("<?Kamaloka?>", checkInstance(playerInfo, 57, 58, 60, 61, 63, 64, 66, 67, 69, 70, 72) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?LabyrinthIcon?>", instanceIcons.get("Labyrinth")).
                    replace("<?Labyrinth?>", checkInstance(playerInfo, 73, 74, 75, 76, 77, 78, 79, 134) ? new Font(Color.RED, "Откат").build() : new Font(Color.GOLD, "Доступно").build()).
                    replace("<?info?>", new Button("Больше", action(bypass + "detailPlayerInfo " + playerInfo.getObjectId()), 50, 32).build());
            showGroup(player);
            player.sendPacket(new TutorialShowHtml("<html><body>" + page + " </body></html>"));
        }
    }

    public void playerInfoDetail(L2Player player, String playerId) {
        Info(player, playerId, "data/scripts/services/PartyMakerUtil/player-info-detail.htm");
    }

    public void playerInfo(L2Player player, String playerId) {
        Info(player, playerId, "data/scripts/services/PartyMakerUtil/player-info.htm");
    }

    public static boolean checkInstance(L2Player player, int... ids) {
        for (int id : ids) {
            if (player.getInstanceReuses().containsKey(id)) {
                return true;
            }
        }
        return false;
    }
    public void confirmExcludeFromParty(L2Player player, String member){
        player.scriptRequest("Вы уверены что хотите исключить " + member + " из группы?", "PartyMaker:excludeFromParty:" + member, new Object[0]);
        showGroup(player);
    }
    public void excludeFromParty(L2Player player, String member) {
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(player.getObjectId());
        if (partyMakerGroup != null) {
            final L2Party party = player.getParty();
            if (party != null) {
                final L2Player playerByName = party.getPlayerByName(member);
                if (playerByName != null) {
                    if (playerByName.getParty() != null) {
                        playerByName.leaveParty();
                    }
                }
            }
            showGroup(player);
        }
    }


    public void unscribe(L2Player player, int groupId) {
        if (player.getVarLong("subscribe", 0L) > System.currentTimeMillis()) {
            showGroups(player);
            return;
        }
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(groupId);
        if (partyMakerGroup != null) {
            partyMakerGroup.getCandidates().remove(player);
            player.setVar("subscribe", String.valueOf(System.currentTimeMillis() + 2 * 1000));
            if (partyMakerGroup.getLeader() != null) {
                showGroup(partyMakerGroup.getLeader());
            }
        }
        showGroups(player);
    }

    public void subscribe(L2Player player, int groupId) {
        if (player.getVarLong("subscribe", 0L) > System.currentTimeMillis()) {
            showGroups(player);
            return;
        }

        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(groupId);
        final Map<Integer, Long> instanceReuses = player.getInstanceReuses();
        if (instanceReuses != null && !instanceReuses.isEmpty()) {
            if (player.getInstanceReuses().containsKey(partyMakerGroup.getInstanceId())) {
                final String page = Files.read("data/scripts/services/PartyMakerUtil/player-instances.htm", player);
                sendDialog(player, page);
                return;
            }
        }
        if (partyMakerGroup.getLeader().getParty() != null) {
            if (partyMakerGroup.getLeader().getParty().getPartyMembers().contains(player)) {
                showGroups(player);
                return;
            }
        }
        if (player.getParty() == null) {
            if (!partyMakerGroup.containCandidate(player)){
                partyMakerGroup.getCandidates().add(player);
                player.setVar("subscribe", String.valueOf(System.currentTimeMillis() + 2 * 1000));
            }
            showGroups(player);
            if (partyMakerGroup.getLeader() != null) {
                showGroup(partyMakerGroup.getLeader());
            }
        } else {
            final String page = Files.read("data/scripts/services/PartyMakerUtil/player-inParty.htm", player);
            sendDialog(player, page);
        }
    }

    public void toLeader(L2Player player){
        if (player.getVarLong("subscribe", 0L) > System.currentTimeMillis()) {
            showGroup(player);
            return;
        }
        teleportToLeader(player);
        player.setVar("subscribe", String.valueOf(System.currentTimeMillis() + 2 * 1000));
    }

    public void teleportToLeader(L2Player activeChar){
        if (activeChar.getParty() == null){
            activeChar.sendPacket(new Say2(0, 2, "PARTY_MAKER", "В вашей группе еще нет участников"));
            showGroup(activeChar);
            return;
        }
        if (!activeChar.getParty().isLeader(activeChar)) {
            activeChar.sendPacket(new Say2(0, 2, "PARTY_MAKER", "Вы не являетесь лидером группы что бы использовать призыв"));
            showGroup(activeChar);
            return;
        }
        SystemMessage msg = Call.canSummonHere(activeChar);
        if (msg != null) {
            activeChar.sendPacket(msg);
            showGroup(activeChar);
            return;
        } else if (activeChar.isAlikeDead()) {
            activeChar.sendPacket(new Say2(0, 2, "PARTY_MAKER", "Нельзя использовать призыв если вы мертвы"));
            showGroup(activeChar);
            return;
        } else if (activeChar.getVarLong("toLeader", 0L) > System.currentTimeMillis()) {
            activeChar.sendPacket(new Say2(0, 2, "PARTY_MAKER", "Команду можно использовать раз в 60 секунд."));
            showGroup(activeChar);
            return;
        }
        activeChar.setVar("toLeader", String.valueOf(System.currentTimeMillis() + 60 * 1000));
        activeChar.getParty().getPartyMembers().stream()
                .filter(p -> p.getObjectId() != activeChar.getObjectId())
                .filter(p -> Call.canBeSummoned(p) == null)
                .filter(p -> activeChar.getReflectionId() == p.getReflectionId())
                .forEach(p -> p.summonCharacterRequest(activeChar, GeoEngine.findPointToStayPet(activeChar.getPlayer(), 100, 150, activeChar.getReflection().getGeoIndex()), SUMMON_PRICE));
        showGroup(activeChar);
    }

    public void createGroup(L2Player player, int minLevel, int maxLevel, String instance, String description) {
        if (!partyMakerGroupMap.containsKey(player.getObjectId())){
            if (player.getParty() != null){
                if (player.getParty().isLeader(player)){
                    final PartyMakerGroup group = new PartyMakerGroup(Math.max(minLevel, 1), Math.min(maxLevel, 85), player, description, instance);
                    partyMakerGroupMap.put(player.getObjectId(), group);
                    for (L2Player member: player.getParty().getPartyMembers()){
                        showGroup(member);
                    }
                }else {
                    final String page = Files.read("data/scripts/services/PartyMakerUtil/error-isMember.htm", player);
                    sendDialog(player, page);
                }
            }else {
                partyMakerGroupMap.put(player.getObjectId(), new PartyMakerGroup(Math.max(minLevel, 1), Math.min(maxLevel, 85), player, description, instance));
                showGroups(player);
            }
        }else {
            final String page = Files.read("data/scripts/services/PartyMakerUtil/error-isLeader.htm", player);
            sendDialog(player, page);
        }
    }

    public void removeCandidateFromAllGroups(L2Player player){
        for (Map.Entry<Integer, PartyMakerGroup> group : partyMakerGroupMap.entrySet()){
            group.getValue().getCandidates().remove(player);
        }
    }

    public void showGroup(L2Player player) {
        PartyMakerGroup group = partyMakerGroupMap.get(player.getObjectId());
        if (group != null) {
            myGroup(player, group, leader);
        } else {
            if (player.getParty() != null) {
                group = partyMakerGroupMap.get(player.getParty().getPartyLeader().getObjectId());
                if (group != null) {
                    myGroup(player, group, member);
                } else {
                    showGroups(player);
                }
            } else {
                showGroups(player);
            }
        }
    }

    public void myGroup(L2Player player, PartyMakerGroup group, STATUS status) {

        final String playerRow = Files.read("data/scripts/services/PartyMakerUtil/playerMember-" + status +".htm", player);
        final String leader = playerRow.replace("<?playerClass?>", getMemberIcon(group.getLeader()))
                .replace("<?playerName?>", group.getLeader().getName())
                .replace("<?playerlevel?>", String.valueOf(group.getLeader().getLevel()))
                .replace("<?info?>", new Button("?", action(bypass + "playerInfo " + group.getLeader().getObjectId())).build())
                .replace("<?button?>", new Img(instanceIcons.get("leader")).build());
        StringBuilder acceptedPlayers = new StringBuilder();
        if (group.getLeader().getParty() != null) {
            for (L2Player member : group.getLeader().getParty().getPartyMembers()) {
                if (member.getObjectId() == group.getLeader().getObjectId()) {
                    continue;
                }
                acceptedPlayers.append(playerRow.replace("<?playerClass?>", getMemberIcon(member))
                        .replace("<?playerName?>", member.getName())
                        .replace("<?playerlevel?>", String.valueOf(member.getLevel()))
                        .replace("<?info?>", new Button("?", action(bypass + "playerInfo " + member.getObjectId())).build())
                        .replace("<?button?>", new Button("-", action(bypass + "excludeFromParty " + member.getName())).build()));
            }
        }
        final String playerCandidate = Files.read("data/scripts/services/PartyMakerUtil/playerCandidate-" + status + ".htm", player);
        StringBuilder requestPlayers = new StringBuilder();
        for (L2Player acceptedPlayer : group.getCandidates()) {
            if (acceptedPlayer != null) {
                requestPlayers.append(playerCandidate.replace("<?playerClass?>", getMemberIcon(acceptedPlayer))
                        .replace("<?playerName?>", acceptedPlayer.getName())
                        .replace("<?playerlevel?>", String.valueOf(acceptedPlayer.getLevel()))
                        .replace("<?info?>", new Button("?", action(bypass + "playerInfo " + acceptedPlayer.getObjectId())).build())
                        .replace("<?accept?>", new Button("+", action(bypass + "acceptToParty " + acceptedPlayer.getObjectId() + " " + player.getObjectId())).build())
                        .replace("<?decline?>", new Button("-", action(bypass + "excludeFromCandidates " + acceptedPlayer.getObjectId() + " " + player.getObjectId())).build()));
            }
        }
        final String replace = Files.read("data/scripts/services/PartyMakerUtil/myGroup-" + status +".htm", player)
                .replace("<?leader?>", leader)
                .replace("<?GroupIcon?>", groupImage(group).build())
                .replace("<?description?>", group.getDescription())
                .replace("<?acceptedPlayers?>", acceptedPlayers.toString())
                .replace("<?requestPlayers?>", requestPlayers.toString());
        sendDialog(player, replace);
    }
    private String getMemberIcon(L2Player player) {
        try {
            final Integer integer = classIcons.get(player.getClassId().name());
            return SkillTable.getInstance().getInfo(integer, 1).getIcon();
        } catch (Exception e) {
            return "icon.action010";
        }
    }

    public static String generateGroup(PartyMakerGroup group, L2Player player, boolean isLeader) {
        int limit = 45;
        String description = group.getDescription().codePointCount(0, group.getDescription().length()) > limit ?
                group.getDescription().substring(0, group.getDescription().offsetByCodePoints(0, limit)) + "..." : group.getDescription();
        String actions;


        if (isLeader) {
            actions = "";
        } else if (group.getLeader().getParty() != null && group.getLeader().getParty().getPartyMembers().contains(player)) {
            actions = "";
        } else if (player.getLevel() < group.getMinLevel() || player.getLevel() > group.getMaxLevel()) {
            actions = "";
        } else if (checkInstanceFromGroups(player, group)) {
            actions = "";
        } else if (group.getGroupLeaderId() == player.getObjectId()) {
            actions = new Button("my", action(""), 40, 32).build();
        } else if (group.getCandidates().contains(player)) {
            actions = new Button("-", action(bypass + "unscribe " + group.getGroupLeaderId()), 40, 32).build();
        } else {
            actions = new Button("+", action(bypass + "subscribe " + group.getGroupLeaderId()), 40, 32).build();
        }
        return Files.read("data/scripts/services/PartyMakerUtil/main-group.htm", player)
                .replace("<?GroupIcon?>", groupImage(group).build())
                .replace("<?level?>", group.getMinLevel() + "-" + group.getMaxLevel())
                .replace("<?description?>", description)
                .replace("<?leader?>", group.getLeader().getName())
                .replace("<?button?>", actions)
                .replace("<?partyCount?>", player.getParty() == null ? "1/9" : group.getLeader().getParty().getMemberCount() + "/9");
    }

    public static boolean checkInstanceFromGroups(L2Player player, PartyMakerGroup group) {
        boolean result = false;
        switch (group.getInstance()) {
            case "Zaken":
                result = checkInstance(player, 114, 133, 135);
                break;
            case "Freya":
                result = checkInstance(player, 139, 144);
                break;
            case "Frinteza":
                result = checkInstance(player, 136);
                break;
            case "ZI":
                result = checkInstance(player, 131) && checkInstance(player, 132);
                break;
            case "Labyrinth":
                result = checkInstance(player, 73, 74, 75, 76, 77, 78, 79, 134);
                break;
            case "Kamaloka":
                result = checkInstance(player, 57, 58, 60, 61, 63, 64, 66, 67, 69, 70, 72);
                break;
        }
        return result;
    }


    public void showGroups(L2Player player) {
        StringBuilder groups = new StringBuilder();
        for (Map.Entry<Integer, PartyMakerGroup> group : partyMakerGroupMap.entrySet()) {
            groups.append(generateGroup(group.getValue(), player, partyMakerGroupMap.containsKey(player.getObjectId())));
        }
        final String replace = Files.read("data/scripts/services/PartyMakerUtil/main.htm", player)
                .replace("<?info?>", new Button("?", action(bypass + "playerInfo " + player.getObjectId())).build())
                .replace("<?groups?>", groups.toString());
        sendDialog(player, replace);
    }

    private static Img groupImage(PartyMakerGroup group) {
        String groupIcon = instanceIcons.get(group.getInstance());
        return new Img(groupIcon, 32, 32);
    }

    public void showCreateDialog(L2Player player) {
        final String replace = Files.read("data/scripts/services/PartyMakerUtil/createGroup.htm", player)
                .replace("<?groups?>", groupTypes);
        sendDialog(player, replace);

    }

    public void sendDialog(L2Player player, String html) {
        final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        player.sendPacket(npcHtmlMessage.setHtml(html));

    }

    @Override
    public void onLoad() {
        classIcons.clear();
        instanceIcons.clear();
        loadClassIcons();
        loadInstanceIcons();
    }

    @Override
    public void onReload() {


    }

    @Override
    public void onShutdown() {

    }

    public static void loadInstanceIcons() {
        String icon = "";
        String instanceId = "";
        LineNumberReader lnr = null;
        try {
            String path = "./data/scripts/services/PartyMakerUtil/InstanceIcons.config";
            if (Boolean.parseBoolean(System.getenv("DEVELOP"))) {
                path = "data/scripts/services/PartyMakerUtil/InstanceIcons.config";
            }
            lnr = new LineNumberReader(new BufferedReader(new FileReader(path)));
            String line;
            while ((line = lnr.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "=");
                instanceId = st.nextToken();
                icon = st.nextToken();
                instanceIcons.put(instanceId, icon);
            }
        } catch (Exception e) {
            _log.warning("Error!!!: " + icon);
            e.printStackTrace();
        } finally {
            try {
                if (lnr != null)
                    lnr.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void loadClassIcons() {
        String className = "";
        int skillId;
        LineNumberReader lnr = null;
        try {
            String path = "./data/scripts/services/PartyMakerUtil/ClassIcons.config";
            if (Boolean.parseBoolean(System.getenv("DEVELOP"))) {
                path = "data/scripts/services/PartyMakerUtil/ClassIcons.config";
            }
            lnr = new LineNumberReader(new BufferedReader(new FileReader(path)));
            String line;
            while ((line = lnr.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "=");
                className = st.nextToken();
                skillId = Integer.parseInt(st.nextToken());
                classIcons.put(className, skillId);
            }
        } catch (Exception e) {
            _log.warning("Error!!!: " + className);
            e.printStackTrace();
        } finally {
            try {
                if (lnr != null)
                    lnr.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


}
