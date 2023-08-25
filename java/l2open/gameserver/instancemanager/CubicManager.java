package l2open.gameserver.instancemanager;

import l2open.gameserver.model.L2Cubic;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.XmlUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author: Ragnarok
 * @date: 22.08.11   0:24
 */
public final class CubicManager {
    private static final Logger log = Logger.getLogger(CubicManager.class.getName());
    private static CubicManager ourInstance = new CubicManager();
    private ConcurrentHashMap<Integer, L2Cubic> cubics = new ConcurrentHashMap<Integer, L2Cubic>();

    public static CubicManager getInstance() {
        return ourInstance;
    }

    private CubicManager() {
        load();
    }

    private void load() {
        File directory = new File("./data/stats/cubics");
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".xml")) {
                    try {
                        Document document = XmlUtils.readFile(file);
                        Element eList = document.getRootElement();
                        for (Element eCubic : eList.elements("cubic")) {
                            int id = XmlUtils.getSafeInt(eCubic, "id", 0);
                            int level = XmlUtils.getSafeInt(eCubic, "level", 0);
                            if (id > 0 && level > 0) {
                                StatsSet set = new StatsSet();
                                for (Element eSet : eCubic.elements("set")) {
                                    set.set(eSet.attributeValue("name"), eSet.attributeValue("val"));
                                }
                                L2Cubic cubic = new L2Cubic(id, level, set);
                                Element eSkills = eCubic.element("skills");
                                if (eSkills != null) {
                                    for (Element eSkill : eSkills.elements("skill")) {
                                        int choiseChance = XmlUtils.getSafeInt(eSkill, "choiseChance", 0);
                                        int[] skill = XmlUtils.getSafeIntArray(eSkill, "skill", ",", new int[2]);
                                        int activateChance = XmlUtils.getSafeInt(eSkill, "activateChance", 0);
                                        boolean castToStatic = XmlUtils.getSafeBoolean(eSkill, "castToStatic", false);
                                        L2Cubic.CubicSkill cubicSkill = new L2Cubic.CubicSkill(choiseChance,
                                                SkillTable.getInstance().getInfo(skill[0], skill[1]),
                                                activateChance, castToStatic);
                                        if (cubic.getTargetType().startsWith("by_skill")) {
                                            cubicSkill.setSkillTarget(eSkill.attributeValue("skillTarget"));
                                            cubicSkill.setSkillCond(eSkill.attributeValue("skillCond"));
                                        }
                                        cubic.addSkill(cubicSkill);
                                    }
                                }
                                cubics.put(cubic.getMask(), cubic);
                            }
                        }
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("CubicManager: Loaded " + cubics.size() + " cubics.");
    }

    /**
     * Возвращает копию объекта - кубика
     *
     * @param npcId    - ид кубика
     * @param npcLevel - уровень кубика
     * @return - готовая к использованию копия, либо null
     */
    public L2Cubic getCubic(int npcId, int npcLevel) {
        if (!cubics.containsKey(L2Cubic.getMask(npcId, npcLevel))) {
            log.warning("CubicManager: Not found cubic " + npcId + " " + npcLevel);
            return null;
        }
        return cubics.get(L2Cubic.getMask(npcId, npcLevel)).copy();
    }
}
