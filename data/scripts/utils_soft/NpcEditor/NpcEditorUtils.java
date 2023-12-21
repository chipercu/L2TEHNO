package utils_soft.NpcEditor;

import l2open.database.schemes.resources.NpcResource;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.List;

/**
 * Created by a.kiperku
 * Date: 21.12.2023
 */

public class NpcEditorUtils {

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
