package l2open.gameserver.communitybbs.PartyMaker;

import com.sun.tools.javac.util.List;
import l2open.common.Html_Constructor.tags.Button;
import l2open.common.Html_Constructor.tags.Img;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.listener.CharListenerList;
import l2open.gameserver.listener.actor.player.OnPlayerExitListener;
import l2open.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassType2;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Files;
import l2open.util.Strings;

import java.util.HashMap;
import java.util.Map;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.action;
import static l2open.gameserver.communitybbs.PartyMaker.PartyMaker.ICON.*;

public class PartyMaker extends Functions implements ScriptFile, OnPlayerExitListener, OnPlayerPartyLeaveListener {

    private final Map<Integer, PartyMakerGroup> partyMakerGroupMap = new HashMap<>();
    private static final String bypass = "bypass -h party_maker:";
    private static final List<String> instances = List.of("Freya", "Zaken", "Frinteza", "7RB");
    private static final String groupTypes = "Freya;Zaken;Frinteza;7RB;";

    @Override
    public void onPlayerExit(L2Player player) {
            //changeLeaderOrDelete(player);
    }

    public void changeLeaderOrDelete(L2Player player){
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(player.getObjectId());
        if (partyMakerGroup != null){
            if (player.getParty() != null){
                final L2Player partyMember = player.getParty().getPartyMember(0);
                partyMakerGroup.setLeader(partyMember);
            }else {
                partyMakerGroupMap.remove(player.getObjectId());
            }
        }
    }

    @Override
    public void onPartyLeave(L2Player player) {
        changeLeaderOrDelete(player);
    }

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




        final String icon;

        ICON(String icon) {
            this.icon = icon;
        }
    }

    public PartyMaker(){
        CharListenerList.addGlobal(this);
    }

    private static PartyMaker _instance;

    public static PartyMaker getInstance() {
        if (_instance == null){
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
            }catch (Exception e){
                showGroups(player);
            }
        }else if(command.startsWith("unscribe")){
            final String[] params = command.split(" ");
            try {
                unscribe(player, Strings.parseInt(params[1]));
            }catch (Exception e){
                showGroups(player);
            }
        } else if (command.startsWith("playerInfo")) {
            final String[] params = command.split(" ");
            playerInfo(player, params[1], params[2]);
        }else if (command.startsWith("acceptToParty")) {
            final String[] params = command.split(" ");
            acceptToParty(player, params[1], params[2]);
        } else if (command.startsWith("excludeFromParty")) {
            final String[] params = command.split(" ");
            excludeFromParty(player, params[1], params[2]);
        } else if (command.startsWith("myGroup")) {
            myGroup(player);
        } else if (command.startsWith("deletePlayer")) {
            System.out.println("delete player");
        }
    }

    private void acceptToParty(L2Player player, String playerId, String groupId) {
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(Integer.parseInt(groupId));
        if (partyMakerGroup != null){
            final L2Party party = partyMakerGroup.getLeader().getParty();
            if (party != null){
                final L2Player candidate = L2ObjectsStorage.getPlayer(Integer.parseInt(playerId));
                if (candidate != null){
                    party.addPartyMember(candidate);
                    partyMakerGroup.getCandidates().remove(candidate);
                    myGroup(player);
                }
            }else {
                final L2Party l2Party = new L2Party(player, 1);
                player.setParty(l2Party);
                final L2Player candidate = L2ObjectsStorage.getPlayer(Integer.parseInt(playerId));
                if (candidate != null){
                    l2Party.addPartyMember(candidate);
                    partyMakerGroup.getCandidates().remove(candidate);
                    myGroup(player);
                }
            }
        }

    }

    private void playerInfo(L2Player player, String playerId, String groupId) {


    }

    public void excludeFromParty(L2Player player, String member, String groupId){
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(Integer.parseInt(groupId));
        if (partyMakerGroup != null){
            final L2Party party = player.getParty();
            if (party != null){
                final L2Player playerByName = party.getPlayerByName(member);
                if (playerByName != null){
                    party.oustPartyMember(playerByName);
                }
            }
            myGroup(player);
        }
    }


    public void unscribe(L2Player player, int groupId){
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(groupId);
        if (partyMakerGroup != null){
            partyMakerGroup.getCandidates().remove(player);
            if (partyMakerGroup.getLeader() != null){
                myGroup(partyMakerGroup.getLeader());
            }
        }
        showGroups(player);
    }

    public void subscribe(L2Player player, int groupId){
        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(groupId);
        final Map<Integer, Long> instanceReuses = player.getInstanceReuses();
        if (instanceReuses != null && !instanceReuses.isEmpty()){
            if (player.getInstanceReuses().containsKey(partyMakerGroup.getInstanceId())){
                final String page = Files.read("data/scripts/services/PartyMakerUtil/player-instances.htm", player);
                sendDialog(player, page);
                return;
            }
        }
        if (partyMakerGroup.getLeader().getParty() != null){
            if (partyMakerGroup.getLeader().getParty().getPartyMembers().contains(player)){
                showGroups(player);
                return;
            }
        }
        if (player.getParty() == null){
            partyMakerGroup.getCandidates().add(player);
            showGroups(player);
            if (partyMakerGroup.getLeader() != null){
                myGroup(partyMakerGroup.getLeader());
            }
        }else {
            final String page = Files.read("data/scripts/services/PartyMakerUtil/player-inParty.htm", player);
            sendDialog(player, page);
        }
    }

    public void createGroup(L2Player player, int minLevel, int maxLevel, String instance, String description) {
        if (minLevel < 1){
            minLevel = 1;
        }
        if (maxLevel > 85){
            maxLevel = 85;
        }
        final PartyMakerGroup partyMakerGroup = new PartyMakerGroup(minLevel, maxLevel, player, description, instance);

        if (player.getParty() != null){
            for (L2Player member: player.getParty().getPartyMembers()){
                if (member.getObjectId() == player.getObjectId()){
                    continue;
                }
                partyMakerGroup.getAcceptedPlayers().add(member);
            }
        }

        partyMakerGroupMap.put(player.getObjectId(), partyMakerGroup);
        showGroups(player);
    }

    public void myGroup(L2Player player) {

        final PartyMakerGroup partyMakerGroup = partyMakerGroupMap.get(player.getObjectId());
        if (partyMakerGroup == null) {
            showGroups(player);
            return;
        }
        final String playerRow = Files.read("data/scripts/services/PartyMakerUtil/playerMember.htm", player);
        final String leader = playerRow
                .replace("<?playerClass?>", getMemberIcon(player))
                .replace("<?playerName?>", player.getName())
                .replace("<?playerlevel?>", String.valueOf(player.getLevel()))
                .replace("<?button?>", new Img(ICON.leader.icon).build());
        String acceptedPlayers = "";
        if (player.getParty() != null){
            for (L2Player member: player.getParty().getPartyMembers()){
                if (member == null){
                    continue;
                }
                if (member.getObjectId() == player.getObjectId()){
                    continue;
                }
                acceptedPlayers += playerRow.replace("<?playerClass?>", getMemberIcon(member))
                        .replace("<?playerName?>", member.getName())
                        .replace("<?playerlevel?>", String.valueOf(member.getLevel()))
                        .replace("<?button?>", new Button("-", action(bypass + "excludeFromParty " + member.getName() + " " + player.getObjectId())).build());
            }
        }
        final String playerCandidate = Files.read("data/scripts/services/PartyMakerUtil/playerCandidat.htm", player);
        String requestPlayers = "";
        for (L2Player acceptedPlayer: partyMakerGroup.getCandidates()){
            final L2Player member = L2ObjectsStorage.getPlayer(acceptedPlayer.getObjectId());
            if (member == null){
                continue;
            }
            requestPlayers += playerCandidate.replace("<?playerClass?>", getMemberIcon(member))
                    .replace("<?playerName?>", member.getName())
                    .replace("<?playerlevel?>", String.valueOf(member.getLevel()))
                    .replace("<?info?>", new Button("?", action(bypass + "playerInfo " + member.getObjectId() + " " + player.getObjectId())).build())
                    .replace("<?accept?>", new Button("+", action(bypass + "acceptToParty " + member.getObjectId() + " " + player.getObjectId())).build())
                    .replace("<?decline?>", new Button("-", action(bypass + "excludeFromCandidates " + member.getObjectId() + " " + player.getObjectId())).build());
        }
        final String replace = Files.read("data/scripts/services/PartyMakerUtil/myGroup.htm", player)
                .replace("<?leader?>", leader)
                .replace("<?GroupIcon?>", groupImage(partyMakerGroup).build())
                .replace("<?description?>", partyMakerGroup.getDescription())
                .replace("<?acceptedPlayers?>", acceptedPlayers)
                .replace("<?requestPlayers?>", requestPlayers);
        sendDialog(player, replace);
    }

    private String getMemberIcon(L2Player player){
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
        return memberIcon;
    }

    public static String generateGroup(PartyMakerGroup group, L2Player player, boolean isLeader){
        int limit = 45;
        String description = group.getDescription().codePointCount(0, group.getDescription().length()) > limit ?
                group.getDescription().substring(0, group.getDescription().offsetByCodePoints(0, limit)) + "..." : group.getDescription();
        String actions;


        if (isLeader){
            actions = "";
        } else if (group.getLeader().getParty() !=null && group.getLeader().getParty().getPartyMembers().contains(player)) {
            actions = "";
        } else if (player.getLevel() < group.getMinLevel() || player.getLevel() > group.getMaxLevel()) {
            actions = "";
        } else if (group.getGroupLeaderId() == player.getObjectId()){
            actions =  new Button("my", action(""), 40, 32).build();
        }else if (group.getCandidates().contains(player)){
            actions = new Button("-", action(bypass + "unscribe " + group.getGroupLeaderId()), 40, 32).build();
        }else {
            actions = new Button("+", action(bypass + "subscribe " + group.getGroupLeaderId()), 40, 32).build();
        }
        return Files.read("data/scripts/services/PartyMakerUtil/main-group.htm", player)
                .replace("<?GroupIcon?>", groupImage(group).build())
                .replace("<?level?>", group.getMinLevel() + "-" + group.getMaxLevel())
                .replace("<?description?>", description)
                .replace("<?leader?>", group.getLeader().getName())
                .replace("<?button?>", actions)
                .replace("<?partyCount?>", player.getParty() == null ? "1/9" : player.getParty().getMemberCount() + "/9");
    }

    public void showGroups(L2Player player) {
        for (Map.Entry<Integer, PartyMakerGroup> group : partyMakerGroupMap.entrySet()){
            final L2Party party = group.getValue().getLeader().getParty();
            if (party != null && party.getPartyMembers().contains(player)){
                myGroup(player);
                return;
            }
        }

        String groups = "";
        for (Map.Entry<Integer, PartyMakerGroup> group : partyMakerGroupMap.entrySet()){
            groups += generateGroup(group.getValue() , player, partyMakerGroupMap.containsKey(player.getObjectId()));
        }
        final String replace = Files.read("data/scripts/services/PartyMakerUtil/main.htm", player)
                .replace("<?groups?>", groups);
        sendDialog(player, replace);
    }

    private static Img groupImage(PartyMakerGroup group) {
        String groupIcon;
        switch (group.getInstance()) {
            case "Zaken":
                groupIcon = zaken.icon;
                break;
            case "Freya":
                groupIcon = freya.icon;
                break;
            case "Frinteza":
                groupIcon = frinteza.icon;
                break;
            case "7RB":
                groupIcon = rb7.icon;
                break;
            default:
                groupIcon = defaultParty.icon;
                break;
        }
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
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }


}
