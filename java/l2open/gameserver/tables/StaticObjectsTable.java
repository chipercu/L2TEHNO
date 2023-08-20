package l2open.gameserver.tables;

import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.instances.L2StaticObjectInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Location;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class StaticObjectsTable {
    private static Logger _log = Logger.getLogger(StaticObjectsTable.class.getName());

    private static StaticObjectsTable _instance;
    private HashMap<Integer, L2StaticObjectInstance> _staticObjects;

    public static StaticObjectsTable getInstance() {
        if (_instance == null)
            _instance = new StaticObjectsTable();
        return _instance;
    }

    public StaticObjectsTable() {
        _staticObjects = new HashMap<Integer, L2StaticObjectInstance>();
        parseData();
        _log.info("StaticObject: Loaded " + _staticObjects.size() + " StaticObject Templates.");
    }

    public void reloadStaticObjects() {
        for (L2StaticObjectInstance obj : _staticObjects.values())
            if (obj != null)
                obj.decayMe();
        _instance = new StaticObjectsTable();
    }

    private void parseData() {
        LineNumberReader lnr = null;
        try {
            String path = "./";
            if (Boolean.parseBoolean(System.getenv("DEVELOP"))){
                path = "";
            }
            File doorData = new File(path, "data/csv/staticobjects.csv");
            lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData)));

            String line = null;
            while ((line = lnr.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#"))
                    continue;

                L2StaticObjectInstance obj = parse(line);
                _staticObjects.put(obj.getStaticObjectId(), obj);
            }
        } catch (FileNotFoundException e) {
            _log.warning("staticobjects.csv is missing in data folder");
        } catch (Exception e) {
            _log.warning("error while creating StaticObjects table " + e);
            e.printStackTrace();
        } finally {
            try {
                if (lnr != null)
                    lnr.close();
            } catch (Exception e) {
            }
        }
    }

    public static L2StaticObjectInstance parse(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");

        st.nextToken(); // Pass over static object name (not used in server)

        int id = Integer.parseInt(st.nextToken());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int z = Integer.parseInt(st.nextToken());
        int type = Integer.parseInt(st.nextToken()); // 0 arena board, 1 throne, 2 town map
        String filePath = st.nextToken();
        int mapX = Integer.parseInt(st.nextToken());
        int mapY = Integer.parseInt(st.nextToken());

        StatsSet npcDat = L2NpcTemplate.getEmptyStatsSet();
        npcDat.set("npcId", id);
        npcDat.set("name", type == 0 ? "Arena" : "");
        npcDat.set("jClass", "static");
        npcDat.set("type", "L2StaticObject");

        L2NpcTemplate template = new L2NpcTemplate(npcDat);

        L2StaticObjectInstance obj = new L2StaticObjectInstance(IdFactory.getInstance().getNextId(), template);

        obj.setType(type);
        obj.setStaticObjectId(id);
        obj.setFilePath(filePath);
        obj.setMapX(mapX);
        obj.setMapY(mapY);
        obj.spawnMe(new Location(x, y, z));

        return obj;
    }
}