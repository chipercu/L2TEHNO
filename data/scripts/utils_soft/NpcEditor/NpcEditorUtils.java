package utils_soft.NpcEditor;

import l2open.database.Filter;
import l2open.database.Resource;
import l2open.database.ResourceProvider;
import l2open.database.schemes.builders.DroplistBuilder;
import l2open.database.schemes.builders.NpcBuilder;
import l2open.database.schemes.builders.NpcElementBuilder;
import l2open.database.schemes.builders.NpcskillsBuilder;
import l2open.database.schemes.resources.DroplistResource;
import l2open.database.schemes.resources.NpcElementResource;
import l2open.database.schemes.resources.NpcResource;
import l2open.database.schemes.resources.NpcskillsResource;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.List;

/**
 * Created by a.kiperku
 * Date: 21.12.2023
 */

public class NpcEditorUtils {

    private static final Resource<NpcResource, NpcBuilder> npcResource = new ResourceProvider<>(NpcResource.class);
    private static final Resource<NpcElementResource, NpcElementBuilder> npcElementResource = new ResourceProvider<>(NpcElementResource.class);
    private static final Resource<DroplistResource, DroplistBuilder> dropListResource = new ResourceProvider<>(DroplistResource.class);
    private static final Resource<NpcskillsResource, NpcskillsBuilder> npcSkillsResource = new ResourceProvider<>(NpcskillsResource.class);

    public static List<NpcResource> getNpcFilteredList(String filter, String filterValue, int offset){
        switch (filter) {
            case "npcname": return npcResource.findList(new Filter().LIKE(NpcResource.NAME, filterValue).ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
            case "npcid": return npcResource.findList(new Filter().LIKE(NpcResource.ID, filterValue).ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
            case "npctype": return npcResource.findList(new Filter().LIKE(NpcResource.TYPE, filterValue).ORDER_BY(NpcResource.ORDINAL).LIMIT(17).OFFSET(offset));
            default: return npcResource.findList(new Filter().ORDER_BY(NpcResource.ID).LIMIT(17).OFFSET(offset));
        }
    }

    public static void updateNpcStat(int npcId, String stat, Object value){
        final L2NpcInstance npc = L2ObjectsStorage.getByNpcId(npcId);
        if (npc != null){
            final L2NpcTemplate template = npc.getTemplate();
            switch (stat){
                case NpcResource.LEVEL: {template.level = (byte) value;break;}
                case NpcResource.HP:{template.baseHpMax = (float) value;break;}
                case NpcResource.MP: {template.baseMpMax = (float) value;break;}
                case NpcResource.PATK: {template.basePAtk = (int) value;break;}
                case NpcResource.PDEF: {template.basePDef = (int) value;break;}
                case NpcResource.MATK: {template.baseMAtk = (int) value;break;}
                case NpcResource.MDEF: {template.baseMDef = (int) value;break;}
                case NpcResource.ATKSPD: {template.basePAtkSpd = (int) value;break;}
                case NpcResource.MATKSPD: {template.baseMAtkSpd = (int) value;break;}
                case NpcResource.WALKSPD: {template.baseWalkSpd = (int) value;break;}
                case NpcResource.RUNSPD: {template.baseRunSpd = (int) value;break;}
                case NpcResource.EXP: {template.revardExp = (int) value;break;}
                case NpcResource.SP: {template.revardSp = (int) value;break;}
                case NpcResource.STR: {template.baseSTR = (byte) value;break;}
                case NpcResource.CON: {template.baseCON = (byte) value;break;}
                case NpcResource.DEX: {template.baseDEX = (byte) value;break;}
                case NpcResource._INT: {template.baseINT = (byte) value;break;}
                case NpcResource.WIT: {template.baseWIT = (byte) value;break;}
                case NpcResource.MEN: {template.baseMEN = (byte) value;break;}

            }
        }
    }


}
