package l2open.gameserver.skills;

import l2open.config.*;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.conditions.*;
import l2open.gameserver.skills.conditions.ConditionGameTime.CheckGameTime;
import l2open.gameserver.skills.conditions.ConditionPlayerRiding.CheckPlayerRiding;
import l2open.gameserver.skills.conditions.ConditionPlayerState.CheckPlayerState;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.skills.triggers.TriggerInfo;
import l2open.gameserver.skills.triggers.TriggerType;
import l2open.gameserver.templates.L2Armor.ArmorType;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class DocumentBase {
    static Logger _log = Logger.getLogger(DocumentBase.class.getName());

    public static HashMap<Integer, String> _list1 = new HashMap<Integer, String>();
    public static HashMap<String, Integer> _list2 = new HashMap<String, Integer>();

    private File file;
    protected HashMap<String, Object[]> tables;

    DocumentBase(File file) {
        this.file = file;
        tables = new HashMap<String, Object[]>();
    }

    Document parse() {
        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            doc = factory.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Error loading file " + file, e);
            return null;
        }
        try {
            parseDocument(doc);
        } catch (Exception e) {
            _log.log(Level.SEVERE, "Error in file " + file, e);
            return null;
        }
        return doc;
    }

    protected abstract void parseDocument(Document doc);

    protected abstract Object getTableValue(String name);

    protected abstract Object getTableValue(String name, int idx);

    protected void resetTable() {
        tables = new HashMap<String, Object[]>();
    }

    protected void setTable(String name, Object[] table) {
        tables.put(name, table);
    }

    protected void parseTemplate(Node n, Object template) {
        n = n.getFirstChild();
        if (n == null)
            return;
        for (; n != null; n = n.getNextSibling()) {
            String nodeName = n.getNodeName();
            if ("add".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Add");
            else if ("sub".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Sub");
            else if ("mul".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Mul");
            else if ("div".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Div");
            else if ("set".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Set");
            else if ("enchant".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Enchant");
            else if ("effect".equalsIgnoreCase(nodeName)) {
                if (template instanceof EffectTemplate)
                    throw new RuntimeException("Nested effects");
                attachEffect(n, template);
            } else if (template instanceof EffectTemplate)
                if ("def".equalsIgnoreCase(nodeName))
                    parseBeanSet(n, ((EffectTemplate) template).getParam(), new Integer(((L2Skill) ((EffectTemplate) template).getParam().getObject("object")).getLevel()));
                else {
                    Condition cond = parseCondition(n);
                    if (cond != null)
                        ((EffectTemplate) template).attachCond(cond);
                }
        }
    }

    protected void attachFunc(Node n, Object template, String name) {
        Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
        String order = n.getAttributes().getNamedItem("order").getNodeValue();
        int ord = parseNumber(order).intValue();
        Condition applyCond = parseCondition(n.getFirstChild());
        double val = 0;
        if (n.getAttributes().getNamedItem("val") != null)
            val = parseNumber(n.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
        FuncTemplate ft = new FuncTemplate(applyCond, name, stat, ord, val);
        if (template instanceof L2Item)
            ((L2Item) template).attachFunction(ft);
        else if (template instanceof L2Skill) {
            ((L2Skill) template).attach(ft);
            //if(ord != 48 && ord != 64)
            //	_log.info("name_s["+(((L2Skill)template).getId())+"]: "+name+" ord="+ord+" val="+val);
        } else if (template instanceof EffectTemplate) {
            ((EffectTemplate) template).attachFunc(ft);
            //if(ord != 48 && ord != 64)
            //	_log.info("name["+(((L2Skill) ((EffectTemplate) template).getParam().getObject("object")).getId())+"]: "+name+" ord="+ord+" val="+val);
        }
    }

    protected void attachEffect(Node n, Object template) {
        NamedNodeMap attrs = n.getAttributes();
        StatsSet set = new StatsSet();

        set.set("name", attrs.getNamedItem("name") == null ? "Buff" : attrs.getNamedItem("name").getNodeValue());
        set.set("object", template);

        //TODO set.set("attachCond", attachCond);

        if (attrs.getNamedItem("count") != null) {
            Integer count = parseNumber(attrs.getNamedItem("count").getNodeValue()).intValue();
            set.set("count", count);
            if (count > 1)
                //_log.info("skill: "+((L2Skill) template).getId()+" count: "+count);
                _list1.put(((L2Skill) template).getId(), count + "");
        }
        if (attrs.getNamedItem("time") != null) {
            int times = parseNumber(attrs.getNamedItem("time").getNodeValue()).intValue();
			/*L2Skill skill = (L2Skill)template;
			if(skill.getAbnormalTime2() < 1 && times > 0)
				skill.setAbnormalTime2(times);
			_log.info("Skill: "+((L2Skill) template).getId()+" getAbnormalTime: "+times);
			if(times > 0 && getAbnormalTime() < 1)
				_log.info("Skill: "+((L2Skill) template).getId()+" getAbnormalTime: "+times);*/
            if (ConfigValue.EnableModifySkillDuration) {
                if (ConfigSystem.SKILL_DURATION_LIST.containsKey(((L2Skill) template).getId()) && times > 0) {
                    if (((L2Skill) template).getLevel() < 100)
                        times = ConfigSystem.SKILL_DURATION_LIST.get(((L2Skill) template).getId());
                    else if ((((L2Skill) template).getLevel() >= 100) && (((L2Skill) template).getLevel() < 140))
                        times += ConfigSystem.SKILL_DURATION_LIST.get(((L2Skill) template).getId());
                    else if (((L2Skill) template).getLevel() > 140)
                        times = ConfigSystem.SKILL_DURATION_LIST.get(((L2Skill) template).getId());
                }
            }
            set.set("time", times);
        }

        set.set("value", attrs.getNamedItem("val") != null ? parseNumber(attrs.getNamedItem("val").getNodeValue()).doubleValue() : 0.);

        if (attrs.getNamedItem("applyOnCaster") != null)
            set.set("applyOnCaster", Boolean.valueOf(attrs.getNamedItem("applyOnCaster").getNodeValue()));

        if (attrs.getNamedItem("displayId") != null)
            set.set("displayId", parseNumber(attrs.getNamedItem("displayId").getNodeValue()).intValue());
        if (attrs.getNamedItem("displayLevel") != null)
            set.set("displayLevel", parseNumber(attrs.getNamedItem("displayLevel").getNodeValue()).intValue());
        if (attrs.getNamedItem("cancelOnAction") != null)
            set.set("cancelOnAction", Boolean.valueOf(attrs.getNamedItem("cancelOnAction").getNodeValue()));
        if (attrs.getNamedItem("isOffensive") != null)
            set.set("isOffensive", Boolean.valueOf(attrs.getNamedItem("isOffensive").getNodeValue()));
        if (attrs.getNamedItem("param") != null)
            parseBeanSetEff(n, set, new Integer(((L2Skill) template).getLevel()), new Integer(((L2Skill) template).getId()));

        if (attrs.getNamedItem("level_min") != null)
            set.set("level_min", parseNumber(attrs.getNamedItem("level_min").getNodeValue()).intValue());
        if (attrs.getNamedItem("level_max") != null)
            set.set("level_max", parseNumber(attrs.getNamedItem("level_max").getNodeValue()).intValue());
        if (attrs.getNamedItem("instantly") != null)
            set.set("instantly", Boolean.valueOf(attrs.getNamedItem("instantly").getNodeValue()));

        EffectTemplate lt = new EffectTemplate(set);

        parseTemplate(n, lt);
        for (Node n1 = n.getFirstChild(); n1 != null; n1 = n1.getNextSibling()) {
            if ("triggers".equalsIgnoreCase(n1.getNodeName())) {
                parseTrigger(n1, lt);
            }
        }
        if (template instanceof L2Skill) {
            ((L2Skill) template).attach(lt);
            String name = attrs.getNamedItem("name").getNodeValue();
            if (name.startsWith("CPHeal") || name.startsWith("Heal") || name.startsWith("ManaHeal"))
                ((L2Skill) template).setHealSkill(true);
        }
        if (Math.min(Integer.MAX_VALUE, 1000 * (set.getInteger("time", 0) < 0 ? Integer.MAX_VALUE : set.getInteger("time", 0))) == 0) {
            //_list1.put(((L2Skill)set.getObject("object")).getId(), set.getString("name"));
            //_list2.put(set.getString("name"), ((L2Skill)set.getObject("object")).getId());
            //_log.info("EffectTemplate: "+set.getString("name")+" "+((L2Skill)set.getObject("object")).getId());
        }
    }

    protected Condition parseCondition(Node n) {
        while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
            n = n.getNextSibling();
        if (n == null)
            return null;
        if ("and".equalsIgnoreCase(n.getNodeName()))
            return parseLogicAnd(n);
        if ("or".equalsIgnoreCase(n.getNodeName()))
            return parseLogicOr(n);
        if ("not".equalsIgnoreCase(n.getNodeName()))
            return parseLogicNot(n);
        if ("player".equalsIgnoreCase(n.getNodeName()))
            return parsePlayerCondition(n);
        if ("target".equalsIgnoreCase(n.getNodeName()))
            return parseTargetCondition(n);
        if ("has".equalsIgnoreCase(n.getNodeName()))
            return parseHasCondition(n);
        if ("using".equalsIgnoreCase(n.getNodeName()))
            return parseUsingCondition(n);
        if ("game".equalsIgnoreCase(n.getNodeName()))
            return parseGameCondition(n);
        if ("zone".equalsIgnoreCase(n.getNodeName()))
            return parseZoneCondition(n);
        if ("InstancedZone".equalsIgnoreCase(n.getNodeName()))
            return parseInstancedZoneCondition(n);
        return null;
    }

    protected Condition parseLogicAnd(Node n) {
        ConditionLogicAnd cond = new ConditionLogicAnd();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == Node.ELEMENT_NODE)
                cond.add(parseCondition(n));
        if (cond._conditions == null || cond._conditions.length == 0)
            _log.severe("Empty <and> condition in " + file);
        return cond;
    }

    protected Condition parseLogicOr(Node n) {
        ConditionLogicOr cond = new ConditionLogicOr();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == Node.ELEMENT_NODE)
                cond.add(parseCondition(n));
        if (cond._conditions == null || cond._conditions.length == 0)
            _log.severe("Empty <or> condition in " + file);
        return cond;
    }

    protected Condition parseLogicNot(Node n) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == Node.ELEMENT_NODE)
                return new ConditionLogicNot(parseCondition(n));
        _log.severe("Empty <not> condition in " + file);
        return null;
    }

    protected Condition parsePlayerCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName();
            if ("race".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionPlayerRace(a.getNodeValue()));
            else if ("minLevel".equalsIgnoreCase(nodeName)) {
                int lvl = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMinLevel(lvl));
            } else if ("maxLevel".equalsIgnoreCase(nodeName)) {
                int lvl = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMaxLevel(lvl));
            } else if ("not_event".equalsIgnoreCase(nodeName)) {
                int event = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerNotEvent(event));
            } else if ("maxPK".equalsIgnoreCase(nodeName)) {
                int pk = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMaxPK(pk));
            } else if ("resting".equalsIgnoreCase(nodeName)) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
            } else if ("moving".equalsIgnoreCase(nodeName)) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
            } else if ("running".equalsIgnoreCase(nodeName)) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
            } else if ("standing".equalsIgnoreCase(nodeName)) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.STANDING, val));
            } else if ("flying".equalsIgnoreCase(a.getNodeName())) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
            } else if ("flyingTransform".equalsIgnoreCase(a.getNodeName())) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING_TRANSFORM, val));
            } else if ("percentHP".equalsIgnoreCase(nodeName)) {
                int percentHP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerPercentHp(percentHP));
            } else if ("minHP".equalsIgnoreCase(nodeName)) {
                int minHP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerMinHp(minHP));
            } else if ("percentMP".equalsIgnoreCase(nodeName)) {
                int percentMP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerPercentMp(percentMP));
            } else if ("percentCP".equalsIgnoreCase(nodeName)) {
                int percentCP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionPlayerPercentCp(percentCP));
            } else if ("agathion".equalsIgnoreCase(nodeName)) {
                int agathionId = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionAgathion(agathionId));
            } else if ("riding".equalsIgnoreCase(nodeName)) {
                String riding = a.getNodeValue();
                if ("strider".equalsIgnoreCase(riding))
                    cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.STRIDER));
                else if ("wyvern".equalsIgnoreCase(riding))
                    cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.WYVERN));
                else if ("none".equalsIgnoreCase(riding))
                    cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.NONE));
            } else if ("classId".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionPlayerClassId(parseNumber(a.getNodeValue()).intValue()));
            else if ("hasBuffId".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                int id = Integer.parseInt(st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens())
                    level = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionPlayerHasBuffId(id, level));
            } else if ("hasBuff".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens())
                    level = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionPlayerHasBuff(et, level));
            } else if ("residence".equalsIgnoreCase(nodeName)) {
                String[] st = a.getNodeValue().split(";");
                cond = joinAnd(cond, new ConditionPlayerResidence(Integer.parseInt(st[1]), st[0]));
            } else {
                if (!"damage".equalsIgnoreCase(nodeName))
                    continue;
                String[] st = a.getNodeValue().split(";");
                cond = joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(st[0]), Double.parseDouble(st[1])));
            }
        }

        if (cond == null)
            _log.severe("Unrecognized <player> condition in " + file);
        return cond;
    }

    protected Condition parseTargetCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName();
            String nodeValue = a.getNodeValue();
            if ("aggro".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetAggro(Boolean.valueOf(nodeValue)));
            else if ("pvp".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetPlayable(Boolean.valueOf(nodeValue)));
            else if ("mob".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetMob(Boolean.valueOf(nodeValue)));
            else if ("mobId".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetMobId(Integer.parseInt(nodeValue)));
            else if ("instanceof".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionInstanceOf(nodeValue, true));
            else if ("thisinstanceof".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionInstanceOf(nodeValue, false));
            else if ("race".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetRace(nodeValue));
            else if ("playerRace".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetPlayerRace(nodeValue));
            else if ("forbiddenClassIds".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetForbiddenClassId(nodeValue.split(";")));
            else if ("playerSameClan".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetClan(nodeValue));
            else if ("castledoor".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetCastleDoor(Boolean.valueOf(nodeValue)));
            else if ("direction".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionTargetDirection(Util.TargetDirection.valueOf(nodeValue.toUpperCase())));
            else if ("hasBuffId".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                int id = parseNumber(st.nextToken().trim()).intValue();
                int level = -1;
                if (st.hasMoreTokens())
                    level = parseNumber(st.nextToken().trim()).intValue();
                cond = joinAnd(cond, new ConditionTargetHasBuffId(id, level));
            } else if ("hasBuff".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens())
                    level = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionTargetHasBuff(et, level));
            } else if ("hasAbnormal".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                SkillAbnormalType et = Enum.valueOf(SkillAbnormalType.class, st.nextToken().trim());
                int level = -1;
                if (st.hasMoreTokens())
                    level = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionTargetHasAbnormal(et, level));
            } else if ("percentHP".equalsIgnoreCase(nodeName)) {
                int percentHP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionTargetPercentHp(percentHP));
            } else if ("percentMP".equalsIgnoreCase(nodeName)) {
                int percentMP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionTargetPercentMp(percentMP));
            } else if ("percentCP".equalsIgnoreCase(nodeName)) {
                int percentCP = parseNumber(a.getNodeValue()).intValue();
                cond = joinAnd(cond, new ConditionTargetPercentCp(percentCP));
            } else if ("slotitem".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                short slot = Short.parseShort(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens())
                    enchant = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
            }
        }
        if (cond == null)
            _log.severe("Unrecognized <target> condition in " + file);
        return cond;
    }

    protected Condition parseUsingCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName();
            String nodeValue = a.getNodeValue();
            if ("kind".equalsIgnoreCase(nodeName)) {
                long mask = 0;
                StringTokenizer st = new StringTokenizer(nodeValue, ",");
                tokens:
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    for (WeaponType wt : WeaponType.values())
                        if (wt.toString().equalsIgnoreCase(item)) {
                            mask |= wt.mask();
                            continue tokens;
                        }
                    for (ArmorType at : ArmorType.values())
                        if (at.toString().equalsIgnoreCase(item)) {
                            mask |= at.mask();
                            continue tokens;
                        }
                    new IllegalArgumentException("Invalid item kind: " + item).printStackTrace();
                }
                if (mask > 0)
                    cond = joinAnd(cond, new ConditionUsingItemType(mask));
            } else if ("armor".equalsIgnoreCase(nodeName)) {
                ArmorType armor = ArmorType.valueOf(nodeValue.toUpperCase());
                cond = joinAnd(cond, new ConditionUsingArmor(armor));
            } else if ("skill".equalsIgnoreCase(nodeName)) {
                if (Util.isNumber(nodeValue))
                    cond = joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(nodeValue)));
                else
                    cond = joinAnd(cond, new ConditionUsingSkill(nodeValue));
            } else if ("slotitem".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                short slot = Short.parseShort(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens())
                    enchant = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
            } else if ("equipeventflag".equalsIgnoreCase(nodeName)) {
                cond = joinAnd(cond, new ConditionEquipEventFlag());
            } else if ("direction".equalsIgnoreCase(nodeName)) {
                Util.TargetDirection Direction = Util.TargetDirection.valueOf(nodeValue.toUpperCase());
                cond = joinAnd(cond, new ConditionTargetDirection(Direction));
            } else if ("instanceof".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionInstanceOf(nodeValue, true));
            else if ("thisinstanceof".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionInstanceOf(nodeValue, false));
        }
        if (cond == null)
            _log.severe("Unrecognized <using> condition in " + file);
        return cond;
    }

    protected Condition parseHasCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName();
            String nodeValue = a.getNodeValue();
            if ("skill".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                Integer id = parseNumber(st.nextToken().trim()).intValue();
                short level = parseNumber(st.nextToken().trim()).shortValue();
                cond = joinAnd(cond, new ConditionHasSkill(id, level));
            }
        }
        if (cond == null)
            _log.severe("Unrecognized <has> condition in " + file);
        return cond;
    }

    protected Condition parseGameCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            if ("night".equalsIgnoreCase(a.getNodeName())) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
            }
        }
        if (cond == null)
            _log.severe("Unrecognized <game> condition in " + file);
        return cond;
    }

    protected Condition parseZoneCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            if ("type".equalsIgnoreCase(a.getNodeName()))
                cond = joinAnd(cond, new ConditionZone(a.getNodeValue()));
        }
        if (cond == null)
            _log.severe("Unrecognized <zone> condition in " + file);
        return cond;
    }

    protected Condition parseInstancedZoneCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            if ("name".equalsIgnoreCase(a.getNodeName()))
                cond = joinAnd(cond, new ConditionInstancedZone(a.getNodeValue()));
        }
        if (cond == null)
            _log.severe("Unrecognized <InstancedZone> condition in " + file);
        return cond;
    }

    protected Object[] parseTable(Node n) {
        NamedNodeMap attrs = n.getAttributes();
        String name = attrs.getNamedItem("name").getNodeValue();
        if (name.charAt(0) != '#')
            throw new IllegalArgumentException("Table name must start with #");
        StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
        GArray<String> array = new GArray<String>();
        while (data.hasMoreTokens())
            array.add(data.nextToken());
        Object[] res = array.toArray(new Object[array.size()]);
        setTable(name, res);
        return res;
    }

    protected void parseBeanSet(Node n, StatsSet set, Integer level) {
        String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
        String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
        char ch = value.length() == 0 ? ' ' : value.charAt(0);
        if (value.contains("#") && ch != '#') // кошмарная затычка на таблицы в сложных строках вроде triggerActions
            for (String str : value.split("[;: ]+"))
                if (str.charAt(0) == '#')
                    value = value.replace(str, String.valueOf(getTableValue(str, level)));
        if (ch == '#') {
            Object tableVal = getTableValue(value, level);
            Number parsedVal = parseNumber(tableVal.toString());
            set.set(name, parsedVal == null ? tableVal : String.valueOf(parsedVal));
        } else if ((Character.isDigit(ch) || ch == '-') && !value.contains(" ") && !value.contains(";"))
            set.set(name, String.valueOf(parseNumber(value)));
        else
            set.set(name, value);
    }

    protected void parseBeanSetEff(Node n, StatsSet set, Integer level, Integer id) {
        boolean debug = id == 329;
        //	if(debug)
        {
            String value = n.getAttributes().getNamedItem("param").getNodeValue().trim();
            char ch = value.length() == 0 ? ' ' : value.charAt(0);
            if (value.contains("#")) // кошмарная затычка на таблицы в сложных строках вроде triggerActions
            {
                //if(debug)
                //	_log.info("parseBeanSetEff(676): value='"+value+"'");
                for (String str : value.split("[;: ]+")) {
                    //if(debug)
                    //	_log.info("parseBeanSetEff(680): str='"+str+"'");
                    if (str.charAt(0) == '#') {
                        value = value.replace(str, String.valueOf(getTableValue(str, level)));
                        //if(debug)
                        //	_log.info("parseBeanSetEff(684): value2='"+value+"'");
                    }
                }
            }
            if ((Character.isDigit(ch) || ch == '-') && !value.contains(" ") && !value.contains(";"))
                set.set("param", String.valueOf(parseNumber(value)));
            else
                set.set("param", value);
        }
    }

    protected void parseTrigger(Node n, StatTemplate template) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (!("trigger".equalsIgnoreCase(n.getNodeName())))
                continue;
            NamedNodeMap map = n.getAttributes();

            int id = parseNumber(map.getNamedItem("id").getNodeValue()).intValue();
            int level = parseNumber(map.getNamedItem("level").getNodeValue()).intValue();
            TriggerType t = TriggerType.valueOf(map.getNamedItem("type").getNodeValue());
            double chance = parseNumber(map.getNamedItem("chance").getNodeValue()).doubleValue();

            TriggerInfo trigger = new TriggerInfo(id, level, t, chance, (byte) 0);

            template.addTrigger(trigger);
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                Condition condition = parseCondition(n.getFirstChild());
                if (condition != null)
                    trigger.addCondition(condition);
            }
        }
    }

    /**
     * Разбирает параметр Value как число, приводя его к Number, либо возвращает значение из таблицы если строка начинается с #
     */
    protected Number parseNumber(String value) {
        if (value.charAt(0) == '#')
            value = getTableValue(value).toString();
        try {
            if (value.indexOf('.') == -1) {
                int radix = 10;
                if (value.length() > 2 && value.substring(0, 2).equalsIgnoreCase("0x")) {
                    value = value.substring(2);
                    radix = 16;
                }
                return Integer.valueOf(value, radix);
            }
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Condition joinAnd(Condition cond, Condition c) {
        if (cond == null)
            return c;
        if (cond instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) cond).add(c);
            return cond;
        }
        ConditionLogicAnd and = new ConditionLogicAnd();
        and.add(cond);
        and.add(c);
        return and;
    }
}