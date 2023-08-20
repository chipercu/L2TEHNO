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
 * @date : 10.01.11    15:24
 */
public class EtcItemParser extends Parser {
    public static void main(String[] args) {
        initialize(".data/stats/items/etcitem");
        ThreadConnection con = null;
        FiltredPreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("SElECT * FROM etcitem ORDER BY item_id");
            rs = st.executeQuery();
            while(rs.next()) {
                Element list = getListByItemId(rs.getInt("item_id"));
                Element etcitem = list.addElement("etcitem");
                etcitem.addComment("Done By Ragnarok (c)L2Open-Team");
                etcitem.addComment("LastChange: " + new SimpleDateFormat("dd.MM.yyyy-HH:mm").format(new Date(System.currentTimeMillis())));
                etcitem.addAttribute("id", rs.getString("item_id"));
                etcitem.addAttribute("name", rs.getString("name"));
                String type;
                String stackable = "false";

                if(rs.getString("item_type").equals("none"))
                    type = "OTHER";
                else if (rs.getString("item_type").equals("mticket"))
                    type = "SCROLL";
                else if (rs.getString("item_type").equals("lotto"))
                    type = "OTHER";
                else if (rs.getString("item_type").equals("race_ticket"))
                    type = "OTHER";
                else if (rs.getString("item_type").equals("dye"))
                    type = "OTHER";
                else if (rs.getString("item_type").equals("harvest"))
                    type = "OTHER";
                else if (rs.getString("item_type").equals("herb"))
                    type = "OTHER";
                else if (rs.getString("item_type").isEmpty())
                    type = "OTHER";
                else
                    type = rs.getString("item_type").toUpperCase();
                if(rs.getString("consume_type").equals("asset")) {
                    type = "MONEY";
                    stackable = "true";
                } else if (rs.getString("consume_type").equals("stackable"))
                    stackable = "true";
                etcitem.addAttribute("type", type);

                String bodypart = "NONE";
                if(rs.getString("item_type").equals("arrow")
                        || rs.getString("item_type").equals("bolt")
                        || rs.getString("item_type").equals("bait"))
                    bodypart = "LHAND";
                if(!bodypart.equalsIgnoreCase("NONE"))
                    addSet(etcitem, "bodypart", bodypart);

                if(!rs.getString("icon").isEmpty())
                    addSet(etcitem, "icon", rs.getString("icon"));
                if(!rs.getString("additional_name").isEmpty())
                    addSet(etcitem, "additional_name", rs.getString("additional_name"));
                if(!rs.getString("class").equalsIgnoreCase("OTHER"))
                    addSet(etcitem, "class", rs.getString("class"));
                if(rs.getInt("weight") > 0)
                    addSet(etcitem, "weight", rs.getString("weight"));
                if(Boolean.valueOf(stackable))
                    addSet(etcitem, "stackable", true);
                if(!rs.getString("crystal_type").equalsIgnoreCase("none"))
                    addSet(etcitem, "crystal_type", rs.getString("crystal_type").toUpperCase());
                if(rs.getInt("crystal_count") > 0)
                    addSet(etcitem, "crystal_count", rs.getString("crystal_count"));
                if(rs.getInt("temporal") == 1)
                    addSet(etcitem, "temporal", true);
                if(rs.getLong("durability") > 0)
                    addSet(etcitem, "durability", rs.getString("durability"));
                if(rs.getInt("tradeable") == 0)
                    addSet(etcitem, "tradeable", false);
                if(rs.getInt("price") > 0)
                    addSet(etcitem, "price", rs.getString("price"));
                if(rs.getInt("dropable") == 0)
                    addSet(etcitem, "dropable", false);
                if(rs.getInt("destroyable") == 0)
                    addSet(etcitem, "destroyable", false);
                if(rs.getInt("flags") != 0)
                    addSet(etcitem, "flags", rs.getString("flags"));

                if(rs.getString("skill_id").length() != 1 && !rs.getString("skill_id").startsWith("0")) {
                    String[] skill_ids = rs.getString("skill_id").split(";");
                    String[] skill_lvls = rs.getString("skill_level").split(";");
                    Element skills = etcitem.addElement("skills");
                    for (int i = 0; i < skill_ids.length; i++)
                        skills.addElement("skill").addAttribute("id", skill_ids[i]).addAttribute("lvl", skill_lvls[i]);
                }
            }
            saveAll(".data/stats/items/etcitem/", "\t", true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, st, rs);
        }
    }
}
