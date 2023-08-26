package l2open.gameserver.xml.loader;

import l2open.config.*;
import l2open.gameserver.model.L2ArmorSet;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.XmlUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author : Ragnarok
 * @date : 02.01.11    22:36
 */
public class XmlArmorsetLoader {
    private static Logger log = Logger.getLogger(XmlArmorsetLoader.class.getName());

    private static XmlArmorsetLoader ourInstance = new XmlArmorsetLoader();
    private ConcurrentHashMap<Integer, L2ArmorSet> armorSets = new ConcurrentHashMap<Integer, L2ArmorSet>();

    public static XmlArmorsetLoader getInstance() {
        return ourInstance;
    }

    private XmlArmorsetLoader() {
        armorSets.clear();
        load();
    }

    private void load() {
        File file;

        if (ConfigValue.develop){
            file = new File("data/stats/armorsets");
        }else {
            file = new File(ConfigValue.DatapackRoot + "/data/stats/armorsets");
        }



        try {
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".xml")) {
                    Document doc = XmlUtils.readFile(f);
                    Element list = doc.getRootElement();
                    for (Element armorset : list.elements("armorset")) {
                        String setId = armorset.attributeValue("id");
                        String name = armorset.attributeValue("name");
                        int[] chests = XmlUtils.getSafeIntArray(armorset, "chest", ",", new int[0]);
                        ArrayList<Integer> legs = new ArrayList<Integer>();
                        ArrayList<Integer> head = new ArrayList<Integer>();
                        ArrayList<Integer> feet = new ArrayList<Integer>();
                        ArrayList<Integer> gloves = new ArrayList<Integer>();
                        ArrayList<Integer> shield = new ArrayList<Integer>();
                        for (Element set : armorset.elements("set")) {
                            if (set.attributeValue("name").equalsIgnoreCase("legs"))
                                legs.add(XmlUtils.getSafeInt(set, "val", -1));
                            else if (set.attributeValue("name").equalsIgnoreCase("head"))
                                head.add(XmlUtils.getSafeInt(set, "val", -1));
                            else if (set.attributeValue("name").equalsIgnoreCase("feet"))
                                feet.add(XmlUtils.getSafeInt(set, "val", -1));
                            else if (set.attributeValue("name").equalsIgnoreCase("shield"))
                                shield.add(XmlUtils.getSafeInt(set, "val", -1));
                            else if (set.attributeValue("name").equalsIgnoreCase("gloves"))
                                gloves.add(XmlUtils.getSafeInt(set, "val", -1));
                        }
                        Element sk = armorset.element("skill");
                        Element sh_skill = armorset.element("shield_skill");
                        Element en_skill = armorset.element("enchant6skill");

                        L2Skill skill = null;
                        L2Skill shield_skill = null;
                        L2Skill enchant6skill = null;
                        if (sk != null)
                            skill = SkillTable.getInstance().getInfo(XmlUtils.getSafeInt(sk, "id", 0), XmlUtils.getSafeInt(sk, "lvl", 0));
                        if (sh_skill != null)
                            shield_skill = SkillTable.getInstance().getInfo(XmlUtils.getSafeInt(sh_skill, "id", 0), XmlUtils.getSafeInt(sh_skill, "lvl", 0));
                        if (en_skill != null)
                            enchant6skill = SkillTable.getInstance().getInfo(XmlUtils.getSafeInt(en_skill, "id", 0), XmlUtils.getSafeInt(en_skill, "lvl", 0));
                        for (int chest : chests) {
                            if (armorSets.containsKey(chest)) {
                                log.warning("Duplicate chest! Set: " + name + " id: " + setId + " chest: " + chest);
                                armorSets.remove(chest);
                            }
                            armorSets.put(chest, new L2ArmorSet(chest, legs, head, gloves, feet, skill, shield, shield_skill, enchant6skill));
                        }
                    }
                }
            }
            log.info("XmlArmorsetLoader: Loaded " + armorSets.size() + " armor sets.");
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public L2ArmorSet getSet(int chestId) {
        return armorSets.get(chestId);
    }
}
