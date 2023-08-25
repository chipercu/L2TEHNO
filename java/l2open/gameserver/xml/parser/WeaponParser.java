package l2open.gameserver.xml.parser;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import org.dom4j.Element;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author : Ragnarok
 * @date : 09.01.11    12:38
 */
public class WeaponParser extends Parser {
    public static void main(String[] args) {
        initialize("./data/stats/items/weapon");
        ThreadConnection con = null;
        FiltredPreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SElECT * FROM weapon ORDER BY item_id");
            rs = st.executeQuery();
            while(rs.next()) {
                Element list = getListByItemId(rs.getInt("item_id"));
                Element weapon = list.addElement("weapon");
                weapon.addComment("Done By Ragnarok (c)L2Open-Team");
                weapon.addComment("LastChange: " + new SimpleDateFormat("dd.MM.yyyy-HH:mm").format(new Date(System.currentTimeMillis())));
                weapon.addAttribute("id", rs.getString("item_id"));
                weapon.addAttribute("name", rs.getString("name"));
                weapon.addAttribute("type", rs.getString("weaponType").toUpperCase());
                if(!rs.getString("bodypart").equalsIgnoreCase("none") && !rs.getString("bodypart").isEmpty())
                    addSet(weapon, "bodypart", rs.getString("bodypart").toUpperCase());
                if(!rs.getString("icon").isEmpty())
                    addSet(weapon, "icon", rs.getString("icon"));
                if(!rs.getString("additional_name").isEmpty())
                    addSet(weapon, "additional_name", rs.getString("additional_name"));
                if(rs.getInt("flags") != 0)
                    addSet(weapon, "flags", rs.getString("flags"));
                if(rs.getInt("weight") > 0)
                    addSet(weapon, "weight", rs.getString("weight"));
                if(rs.getInt("soulshots") > 0)
                    addSet(weapon, "soulshots", rs.getString("soulshots"));
                if(rs.getInt("spiritshots") > 0)
                    addSet(weapon, "spiritshots", rs.getString("spiritshots"));
                if(!rs.getString("crystal_type").equalsIgnoreCase("none"))
                    addSet(weapon, "crystal_type", rs.getString("crystal_type").toUpperCase());
                if(rs.getInt("crystal_count") > 0)
                    addSet(weapon, "crystal_count", rs.getString("crystal_count"));
                if(rs.getInt("p_dam") > 0)
                    addSet(weapon, "p_dam", rs.getString("p_dam"));
                if(rs.getInt("m_dam") > 0)
                    addSet(weapon, "m_dam", rs.getString("m_dam"));
                if(rs.getInt("atk_speed") > 0)
                    addSet(weapon, "atk_speed", rs.getString("atk_speed"));
                if(rs.getInt("rnd_dam") > 0)
                    addSet(weapon, "rnd_dam", rs.getString("rnd_dam"));
                if(rs.getInt("critical") > 0)
                    addSet(weapon, "critical", rs.getString("critical"));
                if(rs.getInt("hit_modify") > 0)
                    addSet(weapon, "hit_modify", rs.getString("hit_modify"));
                if(rs.getInt("avoid_modify") > 0)
                    addSet(weapon, "avoid_modify", rs.getString("avoid_modify"));
                if(rs.getInt("shield_def") > 0)
                    addSet(weapon, "shield_def", rs.getString("shield_def"));
                if(rs.getInt("shield_def_rate") > 0)
                    addSet(weapon, "shield_def_rate", rs.getString("shield_def_rate"));
                if(rs.getInt("mp_consume") > 0)
                    addSet(weapon, "mp_consume", rs.getString("mp_consume"));
                if(rs.getLong("durability") != -1)
                    addSet(weapon, "durability", rs.getString("durability"));
                if(rs.getInt("temporal") == 1)
                    addSet(weapon, "temporal", true);
                if(rs.getInt("tradeable") == 0)
                    addSet(weapon, "tradeable", false);
                if(rs.getInt("price") > 0)
                    addSet(weapon, "price", rs.getString("price"));
                if(rs.getInt("dropable") == 0)
                    addSet(weapon, "dropable", false);
                if(rs.getInt("destroyable") == 0)
                    addSet(weapon, "destroyable", false);
                if(rs.getString("name").startsWith("Common Item - "))
                    addSet(weapon, "isCommon", true);
                if(rs.getString("skill_id").length() != 1 && !rs.getString("skill_id").startsWith("0")) {
                    String[] skill_ids = rs.getString("skill_id").split(";");
                    String[] skill_lvls = rs.getString("skill_level").split(";");
                    Element skills = weapon.addElement("skills");
                    for (int i = 0; i < skill_ids.length; i++)
                        skills.addElement("skill").addAttribute("id", skill_ids[i]).addAttribute("lvl", skill_lvls[i]);
                }
                if(rs.getInt("enchant4_skill_id") != 0) {
                    Element enchSkill = weapon.addElement("enchant4_skill");
                    enchSkill.addAttribute("id", rs.getString("enchant4_skill_id"));
                    enchSkill.addAttribute("lvl", rs.getString("enchant4_skill_lvl"));
                }
            }
            DatabaseUtils.closeDatabaseSR(st, rs);
            st = con.prepareStatement("SELECT * FROM weapon_ex WHERE kamaex>0");
            rs = st.executeQuery();
            ArrayList<Integer> IDs = new ArrayList<Integer>();
            kamaelLoop: while(rs.next()) {
                Element list = getListByItemId(rs.getInt("item_id"));
                for(Iterator<Element> i = list.elementIterator("weapon");i.hasNext();) {
                    Element weapon = i.next();
                    if(weapon.attributeValue("id").equalsIgnoreCase(rs.getString("item_id"))){
                        IDs.add(rs.getInt("item_id"));
                        addSet(weapon, "kamaelAnalog", rs.getString("kamaex"));
                        continue kamaelLoop;
                    }
                }
            }
            DatabaseUtils.closeDatabaseSR(st, rs);
            // Следующий участок кода анализу не подлежит, сам до конца не понял.
            String[] opts = {"sa1", "sa2", "sa3", "common", "raresa1", "raresa2", "raresa3", "rare"};
            for(int id : IDs) {
                for(String opt : opts) {
                    st = con.prepareStatement("SELECT * FROM weapon_ex WHERE item_id="+id);
                    rs = st.executeQuery();
                    rs.next();
                    if(rs.getInt(opt) <= 0) {
                        DatabaseUtils.closeDatabaseSR(st, rs);
                        continue;
                    }
                    Element list = getListByItemId(rs.getInt(opt));
                    for(Iterator<Element> i = list.elementIterator("weapon");i.hasNext();) {
                        Element weapon = i.next();
                        if(!weapon.attributeValue("id").equalsIgnoreCase(rs.getString(opt)))
                            continue;
                        FiltredPreparedStatement st2 = con.prepareStatement("SELECT "+opt+" FROM weapon_ex WHERE item_id="+rs.getString("kamaex"));
                        ResultSet rs2 = st2.executeQuery();
                        rs2.next();
                        if(rs2.getInt(opt) > 0)
                            addSet(weapon, "kamaelAnalog", rs2.getString(opt));
                        DatabaseUtils.closeDatabaseSR(st2, rs2);
                    }
                    DatabaseUtils.closeDatabaseSR(st, rs);
                }
            }
            opts = new String[]{"rare", "sa1", "sa2", "sa3", "raresa1", "raresa2", "raresa3", "rarepvp1","rarepvp2","rarepvp3", "pvp1","pvp2","pvp3"};
            for(String opt : opts) {
                FiltredPreparedStatement st2 = con.prepareStatement("SELECT "+opt+" FROM weapon_ex WHERE "+opt+">0");
                ResultSet rs2 = st2.executeQuery();
                while(rs2.next()) {
                    Element list = getListByItemId(rs2.getInt(opt));
                    Element weapon = null;
                    for(Iterator<Element> i = list.elementIterator("weapon");i.hasNext();) {
                        Element e = i.next();
                        if(e.attributeValue("id").equals(rs2.getString(opt))) {
                            weapon = e;
                            break;
                        }
                    }
                    if(weapon != null) {
                        if(opt.contains("rare") && weapon.attributeValue("isRare") == null)
                            addSet(weapon, "isRare", true);
                        if(opt.contains("sa" ) && weapon.attributeValue("isSa") == null)
                            addSet(weapon, "isSa", true);
                        if(opt.contains("pvp")) {
                            if(weapon.attributeValue("isPvP") == null)
                                addSet(weapon, "isPvP", true);
                            if(weapon.attributeValue("isSa") == null)
                                addSet(weapon, "isSa", true);
                        }
                    }
                }
                DatabaseUtils.closeDatabaseSR(st2, rs2);
            }

            saveAll("./data/stats/items/weapon/", "\t", true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, st, rs);
        }
    }
}
