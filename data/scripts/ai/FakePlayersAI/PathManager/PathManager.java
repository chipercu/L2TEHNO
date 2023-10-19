package ai.FakePlayersAI.PathManager;

import com.google.gson.Gson;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Location;
import l2open.util.Rnd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PathManager {
    private static final String bypass = "bypass -h path_manager:";
    private final Map<String, PathMap> pathMap;
    private static PathManager INSTANCE;

    private List<L2NpcInstance> newMapPoints = new ArrayList<>();

    public static PathManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PathManager();
        }
        return INSTANCE;
    }





    public PathManager() {
        this.pathMap = new HashMap<>();
    }

    public Map<String, PathMap> getPathMap() {
        return pathMap;
    }

    public PathMap getMapByName(String name){
        for(Map.Entry<String, PathMap> map: pathMap.entrySet()){
            final String replace = map.getKey().replace("_Map.json", "");
            if (replace.equals(name)){
                return map.getValue();
            }
        }
        return null;
    }

    public void parseMaps(){
        final File file = new File("data/scripts/ai/FakePlayersAI/PathManager/JsonMaps");
        for(File f: Objects.requireNonNull(file.listFiles())){
            if (f.getName().contains("_Map.json")){
                final PathMap map = parseMap(Paths.get(f.getPath()));
                getPathMap().put(map.getPathMapName(), map);
            }
        }
    }

    private PathMap parseMap(Path file) {
        final String collect;
        try {
            collect = String.join("", Files.readAllLines(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Gson().fromJson(collect, PathMap.class);
    }


//    public void generateTestMap(String name){
//
//        List<Point> pathPointList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            List<Location> runAwayPoints = new ArrayList<>();
//            for (int j = 0; j < 3; j++) {
//                runAwayPoints.add(new Location(Rnd.get(100, 1000), Rnd.get(100, 1000),Rnd.get(100, 1000)));
//            }
//            final Point pathPoint = new Point(Rnd.get(100, 1000), Rnd.get(100, 1000),Rnd.get(100, 1000));
//            pathPointList.add(pathPoint);
//        }
//        final PathMap map = new PathMap(name, pathPointList);
//
//        final String jsonMap = new Gson().toJson(map);
//        try (FileWriter writer = new FileWriter("data/scripts/ai/FakePlayersAI/PathManager/JsonMaps/" + name + "_Map.json", true)) {
//                writer.write(jsonMap);
//                writer.flush();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void showControlPanel(L2Player player){
        final String replace = l2open.util.Files.read("data/scripts/ai/FakePlayersAI/PathManager/html/admin-page.htm", player);
        sendDialog(player, replace);
    }
    public void sendDialog(L2Player player, String html) {
        final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        player.sendPacket(npcHtmlMessage.setHtml(html));
    }


    public void handleCommands(L2GameClient client, String command) {
        L2Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (command.startsWith("spawn_point")){
            spawn_point(player);
        }else if (command.startsWith("delete_point")){
            delete_point(player);
        }else if (command.startsWith("save_map")){
            save_map(player);
        }else if (command.startsWith("clear_map")){
            clear_map(player);
        }

    }

    private void clear_map(L2Player player) {

        showControlPanel(player);
    }

    private void save_map(L2Player player) {


        showControlPanel(player);
    }

    private void delete_point(L2Player player) {


        showControlPanel(player);
    }

    private void spawn_point(L2Player player) {


        showControlPanel(player);
    }
}
