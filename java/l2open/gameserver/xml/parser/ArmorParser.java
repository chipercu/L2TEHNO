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
import java.util.Date;
import java.util.Iterator;

/**
 * @author : Ragnarok
 * @date : 28.12.10    8:39
 */
public class ArmorParser extends Parser{
    public static void main(String[] args) {
        initialize(".data/stats/items/armor");
        ThreadConnection con = null;
        FiltredPreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SELECT COUNT(*) FROM armor");
            rs = st.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            DatabaseUtils.closeDatabaseSR(st, rs);
            st = con.prepareStatement("SELECT * FROM armor ORDER BY item_id");
            rs = st.executeQuery();
            int j = 0;
            while (rs.next()) {
                j++;
                System.out.println("Processed id = " + rs.getString("item_id") + " name = " + rs.getString("name") + " | " + Math.min(Math.round(j / (count / 100)), 100) + "%");
                Element list = getListByItemId(rs.getInt("item_id"));
                Element armor = list.addElement("armor");
                armor.addComment("Done By Ragnarok (c)L2Open-Team");
                armor.addComment("LastChange: " + new SimpleDateFormat("dd.MM.yyyy-HH:mm").format(new Date(System.currentTimeMillis())));
                armor.addAttribute("id", rs.getString("item_id"));
                armor.addAttribute("name", rs.getString("name"));
                String bodypart = rs.getString("bodypart");
                if(bodypart.startsWith("dhair"))
                    bodypart = "HAIRALL";
                else if(bodypart.startsWith("rear,lear"))
                    bodypart = "LREAR";
                else if (bodypart.startsWith("rfinger,lfinger"))
                    bodypart = "LRFINGER";
                armor.addAttribute("type", rs.getString("armor_type").toUpperCase());
                if(!bodypart.equalsIgnoreCase("NONE"))
                    addSet(armor, "bodypart", bodypart.toUpperCase());
                if(!rs.getString("icon").isEmpty())
                    addSet(armor, "icon", rs.getString("icon"));
                if(!rs.getString("additional_name").isEmpty())
                    addSet(armor, "additional_name", rs.getString("additional_name"));
                if(rs.getInt("weight") != 0)
                    addSet(armor, "weight", rs.getString("weight"));
                if(rs.getInt("flags") != 0)
                    addSet(armor, "flags", rs.getString("flags"));

                if(rs.getString("player_class") != null) {
                    armor.addComment("Bard, BowMaster, DaggerMaster, Enchanter, ForceMaster, Healer, ShieldMaster, Summoner, WeaponMaster, Wizard");
                    addSet(armor, "player_class", rs.getString("player_class"));
                }

                if(!rs.getString("crystal_type").equalsIgnoreCase("none"))
                    addSet(armor, "crystal_type", rs.getString("crystal_type").toUpperCase());
                if(rs.getInt("crystal_count") > 0)
                    addSet(armor, "crystal_count", rs.getString("crystal_count"));

                if(rs.getInt("avoid_modify") != 0)
                    addSet(armor, "avoid_modify", rs.getString("avoid_modify"));
                if(rs.getInt("temporal") == 1)
                    addSet(armor, "temporal", true);
                if(rs.getLong("durability") != -1)
                    addSet(armor, "durability", rs.getString("durability"));
                if(rs.getInt("p_def") > 0)
                    addSet(armor, "p_def", rs.getString("p_def"));
                if(rs.getInt("m_def") > 0)
                    addSet(armor, "m_def", rs.getString("m_def"));
                if(rs.getInt("mp_bonus") > 0)
                    addSet(armor, "mp_bonus", rs.getString("mp_bonus"));
                if(rs.getInt("tradeable") == 0)
                    addSet(armor, "tradeable", false);
                if(rs.getInt("price") > 0)
                    addSet(armor, "price", rs.getString("price"));
                if(rs.getInt("dropable") == 0)
                    addSet(armor, "dropable", false);
                if(rs.getInt("destroyable") == 0)
                    addSet(armor, "destroyable", false);
                if(rs.getString("name").startsWith("Common Item - "))
                    addSet(armor, "isCommon", true);
                if(rs.getString("skill_id").length() != 1 && !rs.getString("skill_id").startsWith("0")) {
                    String[] skill_ids = rs.getString("skill_id").split(";");
                    String[] skill_lvls = rs.getString("skill_level").split(";");
                    Element skills = armor.addElement("skills");
                    for (int i = 0; i < skill_ids.length; i++)
                        skills.addElement("skill").addAttribute("id", skill_ids[i]).addAttribute("lvl", skill_lvls[i]);
                }
                if(rs.getInt("enchant4_skill_id") != 0) {
                    Element enchSkill = armor.addElement("enchant4_skill");
                    enchSkill.addAttribute("id", rs.getString("enchant4_skill_id"));
                    enchSkill.addAttribute("lvl", rs.getString("enchant4_skill_lvl"));
                }
            }
            String[] opts = {"srare1","srare2","srare3","rare1","rare2","rare3","pvp"};
            for(String opt : opts) {
                FiltredPreparedStatement st2 = con.prepareStatement("SELECT "+opt+" FROM armor_ex WHERE "+opt+">0");
                ResultSet rs2 = st2.executeQuery();
                while(rs2.next()) {
                    Element list = getListByItemId(rs2.getInt(opt));
                    Element armor = null;
                    for(Iterator<Element> i = list.elementIterator("armor");i.hasNext();) {
                        Element e = i.next();
                        if(e.attributeValue("id").equalsIgnoreCase(rs2.getString(opt))) {
                            armor = e;
                            break;
                        }
                    }
                    if(armor != null) {
                        if(opt.equals("pvp"))
                            addSet(armor, "isPvP", true);
                        else
                            addSet(armor, "isRare", true);
                    }
                }
                DatabaseUtils.closeDatabaseSR(st2, rs2);
            }
            saveAll(".data/stats/items/armor/", "\t", true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, st, rs);
        }
    }
}
