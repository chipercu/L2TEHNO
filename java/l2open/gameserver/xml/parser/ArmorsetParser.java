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

/**
 * @author : Ragnarok
 * @date : 02.01.11    22:17
 */
public class ArmorsetParser extends Parser {
    public static void main(String[] args) {
        initialize("./data/stats/armorsets");
        ThreadConnection con = null;
        FiltredPreparedStatement st = null;
        ResultSet rs = null;
        FiltredPreparedStatement st2 = null;
        ResultSet rs2 = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SELECT * FROM armorsets ORDER BY chest");
            rs = st.executeQuery();
            int i = 0;
            while (rs.next()) {
                Element list = getListByItemId(i);
                Element armorset = list.addElement("armorset");
                armorset.addComment("Done By Ragnarok (c)L2Open-Team");
                armorset.addComment("LastChange: " + new SimpleDateFormat("dd.MM.yyyy-HH:mm").format(new Date(System.currentTimeMillis())));
                armorset.addAttribute("id", "" + i);
                armorset.addAttribute("name", "set" + i);
                st2 = con.prepareStatement("SELECT * FROM armor_ex");
                rs2 = st2.executeQuery();
                String chest = rs.getString("chest");
                String legs = "";
                String head = "";
                String feet = "";
                String gloves = "";
                String shield = "";
                if (rs.getInt("legs") > 0)
                    legs += rs.getString("legs");
                if (rs.getInt("head") > 0)
                    head += rs.getString("head");
                if (rs.getInt("feet") > 0)
                    feet += rs.getString("feet");
                if (rs.getInt("shield") > 0)
                    shield += rs.getString("shield");
                if (rs.getInt("gloves") > 0)
                    gloves += rs.getString("gloves");
                while (rs2.next()) {
                    if (rs2.getInt("srare1") == rs.getInt("chest"))
                        chest += "," + rs2.getString("srare1");
                    if (rs2.getInt("srare2") == rs.getInt("chest"))
                        chest += "," + rs2.getString("srare2");
                    if (rs2.getInt("srare3") == rs.getInt("chest"))
                        chest += "," + rs2.getString("srare3");
                    if (rs2.getInt("uns1") == rs.getInt("chest"))
                        chest += "," + rs2.getString("rare1");
                    if (rs2.getInt("uns2") == rs.getInt("chest"))
                        chest += "," + rs2.getString("rare2");
                    if (rs2.getInt("uns3") == rs.getInt("chest"))
                        chest += "," + rs2.getString("rare3");

                    if (rs2.getInt("srare1") > 0 && rs2.getInt("srare1") == rs.getInt("legs"))
                        legs += "," + rs2.getString("srare1");
                    if (rs2.getInt("srare2") > 0 && rs2.getInt("srare2") == rs.getInt("legs"))
                        legs += "," + rs2.getString("srare2");
                    if (rs2.getInt("srare3") > 0 && rs2.getInt("srare3") == rs.getInt("legs"))
                        legs += "," + rs2.getString("srare3");
                    if (rs.getInt("legs") > 0 && rs2.getInt("uns1") == rs.getInt("legs"))
                        legs += "," + rs2.getString("rare1");
                    if (rs.getInt("legs") > 0 && rs2.getInt("uns2") == rs.getInt("legs"))
                        legs += "," + rs2.getString("rare2");
                    if (rs.getInt("legs") > 0 && rs2.getInt("uns3") == rs.getInt("legs"))
                        legs += "," + rs2.getString("rare3");

                    if (rs.getInt("head") > 0 && rs2.getInt("uns1") == rs.getInt("head"))
                        head += "," + rs2.getString("rare1");
                    if (rs.getInt("head") > 0 && rs2.getInt("uns2") == rs.getInt("head"))
                        head += "," + rs2.getString("rare2");
                    if (rs.getInt("head") > 0 && rs2.getInt("uns3") == rs.getInt("head"))
                        head += "," + rs2.getString("rare3");
                    if (rs2.getInt("srare1") > 0 && rs2.getInt("srare1") == rs.getInt("head"))
                        head += "," + rs2.getString("srare1");
                    if (rs2.getInt("srare2") > 0 && rs2.getInt("srare2") == rs.getInt("head"))
                        head += "," + rs2.getString("srare2");
                    if (rs2.getInt("srare3") > 0 && rs2.getInt("srare3") == rs.getInt("head"))
                        head += "," + rs2.getString("srare3");

                    if (rs.getInt("feet") > 0 && rs2.getInt("uns1") == rs.getInt("feet"))
                        feet += "," + rs2.getString("rare1");
                    if (rs.getInt("feet") > 0 && rs2.getInt("uns2") == rs.getInt("feet"))
                        feet += "," + rs2.getString("rare2");
                    if (rs.getInt("feet") > 0 && rs2.getInt("uns3") == rs.getInt("feet"))
                        feet += "," + rs2.getString("rare3");
                    if (rs2.getInt("srare1") > 0 && rs2.getInt("srare1") == rs.getInt("feet"))
                        feet += "," + rs2.getString("srare1");
                    if (rs2.getInt("srare2") > 0 && rs2.getInt("srare2") == rs.getInt("feet"))
                        feet += "," + rs2.getString("srare2");
                    if (rs2.getInt("srare3") > 0 && rs2.getInt("srare3") == rs.getInt("feet"))
                        feet += "," + rs2.getString("srare3");

                    if (rs.getInt("shield") > 0 && rs2.getInt("uns1") == rs.getInt("shield"))
                        shield += "," + rs2.getString("rare1");
                    if (rs.getInt("shield") > 0 && rs2.getInt("uns2") == rs.getInt("shield"))
                        shield += "," + rs2.getString("rare2");
                    if (rs.getInt("shield") > 0 && rs2.getInt("uns3") == rs.getInt("shield"))
                        shield += "," + rs2.getString("rare3");
                    if (rs2.getInt("srare1") > 0 && rs2.getInt("srare1") == rs.getInt("shield"))
                        shield += "," + rs2.getString("srare1");
                    if (rs2.getInt("srare2") > 0 && rs2.getInt("srare2") == rs.getInt("shield"))
                        shield += "," + rs2.getString("srare2");
                    if (rs2.getInt("srare3") > 0 && rs2.getInt("srare3") == rs.getInt("shield"))
                        shield += "," + rs2.getString("srare3");

                    if (rs.getInt("gloves") > 0 && rs2.getInt("uns1") == rs.getInt("gloves"))
                        gloves += "," + rs2.getString("rare1");
                    if (rs.getInt("gloves") > 0 && rs2.getInt("uns2") == rs.getInt("gloves"))
                        gloves += "," + rs2.getString("rare2");
                    if (rs.getInt("gloves") > 0 && rs2.getInt("uns3") == rs.getInt("gloves"))
                        gloves += "," + rs2.getString("rare3");
                    if (rs2.getInt("srare1") > 0 && rs2.getInt("srare1") == rs.getInt("gloves"))
                        gloves += "," + rs2.getString("srare1");
                    if (rs2.getInt("srare2") > 0 && rs2.getInt("srare2") == rs.getInt("gloves"))
                        gloves+= "," + rs2.getString("srare2");
                    if (rs2.getInt("srare3") > 0 && rs2.getInt("srare3") == rs.getInt("gloves"))
                        gloves+= "," + rs2.getString("srare3");
                }
                armorset.addAttribute("chest", chest);
                for (String s : legs.split(","))
                    if (!s.trim().isEmpty())
                        addSet(armorset, "legs", s);
                for (String s : head.split(","))
                    if (!s.trim().isEmpty())
                        addSet(armorset, "head", s);
                for (String s : gloves.split(","))
                    if (!s.trim().isEmpty())
                        addSet(armorset, "gloves", s);
                for (String s : feet.split(","))
                    if (!s.trim().isEmpty())
                        addSet(armorset, "feet", s);
                for (String s : shield.split(","))
                    if (!s.trim().isEmpty())
                        addSet(armorset, "shield", s);
                Element skill = armorset.addElement("skill");
                skill.addAttribute("id", rs.getString("skill").split(";")[0]);
                skill.addAttribute("lvl", rs.getString("skill").split(";")[1]);
                if (rs.getString("shield_skill").length() > 3) {
                    Element sh_skill = armorset.addElement("shield_skill");
                    sh_skill.addAttribute("id", rs.getString("shield_skill").split(";")[0]);
                    sh_skill.addAttribute("lvl", rs.getString("shield_skill").split(";")[1]);
                }
                if (rs.getString("enchant6skill").length() > 3) {
                    Element enchant6skill = armorset.addElement("enchant6skill");
                    enchant6skill.addAttribute("id", rs.getString("enchant6skill").split(";")[0]);
                    enchant6skill.addAttribute("lvl", rs.getString("enchant6skill").split(";")[1]);
                }
                DatabaseUtils.closeDatabaseSR(st2, rs2);
                i++;
            }
            saveAll("./data/stats/armorsets/", "\t", true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, st, rs);
        }
    }
}
