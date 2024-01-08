package utils_soft.NpcEditor;

import l2open.database.Filter;
import l2open.database.Resource;
import l2open.database.ResourceProvider;
import l2open.database.schemes.builders.*;
import l2open.database.schemes.resources.*;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a.kiperku
 * Date: 21.12.2023
 */

public class NpcEditorUtils {

    private static final Resource<NpcResource, NpcBuilder> npcResource = new ResourceProvider<>(NpcResource.class);
    private static final Resource<NpcElementResource, NpcElementBuilder> npcElementResource = new ResourceProvider<>(NpcElementResource.class);
    private static final Resource<DroplistResource, DroplistBuilder> dropListResource = new ResourceProvider<>(DroplistResource.class);
    private static final Resource<NpcskillsResource, NpcskillsBuilder> npcSkillsResource = new ResourceProvider<>(NpcskillsResource.class);


    public static void updateSpawnLoc(L2NpcInstance npc, Location loc, SpawnlistResource npcSpawn){
        npc.setLoc(loc);
        npc.setSpawnedLoc(loc);
        npc.setHeading(loc.h);
        final HashMap<String, Object> map = new HashMap<>();
        map.put(SpawnlistResource.LOCY, npc.getLoc().y);
        map.put(SpawnlistResource.LOCX, npc.getLoc().x);
        map.put(SpawnlistResource.HEADING, npc.getLoc().h);
        npcSpawn.multiUpdate(map);
    }

    public static List<NpcResource> getNpcFilteredList(String filter, String filterValue, int offset){
        switch (filter) {
            case "npcname": return npcResource.findList(new Filter().LIKE(NpcResource.NAME, filterValue).ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
            case "npcid": return npcResource.findList(new Filter().LIKE(NpcResource.ID, filterValue).ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
            case "npctype": return npcResource.findList(new Filter().LIKE(NpcResource.TYPE, filterValue).ORDER_BY(NpcResource.ORDINAL).LIMIT(17).OFFSET(offset));
            default: return npcResource.findList(new Filter().ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
        }
    }

    public static Map<Integer, List<DroplistResource>> partitionDroplistByCategory(List<DroplistResource> list){
        Map<Integer, List<DroplistResource>> categoryMap = new HashMap<>();
        for (DroplistResource element : list) {
            int category = element.getCategory();
            List<DroplistResource> categoryList = categoryMap.getOrDefault(category, new ArrayList<>());
            categoryList.add(element);
            categoryMap.put(category, categoryList);
        }
        return categoryMap;
    }


    public static void updateNpcStat(int npcId, String stat, String value){
        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);

        if (npc != null){
            final L2NpcTemplate template = npc.getTemplate();
            switch (stat){
                case NpcResource.LEVEL: {template.level = Byte.parseByte(value);break;}
                case NpcResource.HP:{template.baseHpMax = Float.parseFloat(value);break;}
                case NpcResource.MP: {template.baseMpMax = Float.parseFloat(value);break;}
                case NpcResource.PATK: {template.basePAtk = Integer.parseInt(value);break;}
                case NpcResource.PDEF: {template.basePDef = Integer.parseInt(value);break;}
                case NpcResource.MATK: {template.baseMAtk = Integer.parseInt(value);break;}
                case NpcResource.MDEF: {template.baseMDef = Integer.parseInt(value);break;}
                case NpcResource.ATKSPD: {template.basePAtkSpd = Integer.parseInt(value);break;}
                case NpcResource.MATKSPD: {template.baseMAtkSpd = Integer.parseInt(value);break;}
                case NpcResource.WALKSPD: {template.baseWalkSpd = Integer.parseInt(value);break;}
                case NpcResource.RUNSPD: {template.baseRunSpd = Integer.parseInt(value);break;}
                case NpcResource.EXP: {template.revardExp = Integer.parseInt(value);break;}
                case NpcResource.SP: {template.revardSp = Integer.parseInt(value);break;}
                case NpcResource.STR: {template.baseSTR = Byte.parseByte(value);break;}
                case NpcResource.CON: {template.baseCON = Byte.parseByte(value);break;}
                case NpcResource.DEX: {template.baseDEX = Byte.parseByte(value);break;}
                case NpcResource._INT: {template.baseINT = Byte.parseByte(value);break;}
                case NpcResource.WIT: {template.baseWIT = Byte.parseByte(value);break;}
                case NpcResource.MEN: {template.baseMEN = Byte.parseByte(value);break;}

                case NpcElementResource.ATK_ELEMENT: {template.atkElement = Integer.parseInt(value);break;}
                case NpcElementResource.ELEM_ATK_POWER: {template.elemAtkPower = Integer.parseInt(value);break;}
                case NpcElementResource.FIRE_RES: {template.baseFireRes = Integer.parseInt(value);break;}
                case NpcElementResource.WATER_RES: {template.baseWaterRes = Integer.parseInt(value);break;}
                case NpcElementResource.WIND_RES: {template.baseWindRes = Integer.parseInt(value);break;}
                case NpcElementResource.EARTH_RES: {template.baseEarthRes = Integer.parseInt(value);break;}
                case NpcElementResource.HOLY_RES: {template.baseHolyRes = Integer.parseInt(value);break;}
                case NpcElementResource.DARK_RES: {template.baseDarkRes = Integer.parseInt(value);break;}
            }
        }


        final List<L2NpcInstance> allByNpcId = L2ObjectsStorage.getAllByNpcId(npcId, true);

        for (L2NpcInstance npcInstance : allByNpcId){
            npcInstance.setCurrentHpMp(npcInstance.getMaxHp(), npcInstance.getMaxMp());
        }

    }


}
