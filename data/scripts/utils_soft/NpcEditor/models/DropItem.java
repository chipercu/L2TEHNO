package utils_soft.NpcEditor.models;

import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.StatsSet;
import l2open.gameserver.xml.ItemTemplates;
import utils_soft.common.Model;

import java.sql.ResultSet;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */

public class DropItem extends Model {

    private boolean isHerb;
    private String icon;
    private String name;

    public DropItem(int mobId) {
        super("droplist", String.valueOf(mobId));
        final L2Item l2Item = ItemTemplates.getInstance().getTemplate(getItemId());
        this.isHerb = l2Item.isHerb();
        this.icon = l2Item.getIcon();
        this.name = l2Item.getName();
    }

    public int getMobId(){
        return getStatsSet().getInteger("mobId");
    }

    public int getItemId() {
        return getStatsSet().getInteger("itemId");
    }

    public int getMin() {
        return getStatsSet().getInteger("min");
    }

    public int getMax() {
        return getStatsSet().getInteger("max");
    }

    public int getSweep() {
        return getStatsSet().getInteger("sweep");
    }

    public int getChance() {
        return getStatsSet().getInteger("chance");
    }

    public int getCategory() {
        return getStatsSet().getInteger("category");
    }

    public boolean isSpoil() {
        return getSweep() == 1;
    }

    public boolean isHerb() {
        return isHerb;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
