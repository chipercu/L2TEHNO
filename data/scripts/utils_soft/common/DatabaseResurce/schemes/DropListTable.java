package utils_soft.common.DatabaseResurce.schemes;

import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import utils_soft.common.DatabaseResurce.DataBaseTable;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */

public class DropListTable extends DataBaseTable<DropListTable> {

    private boolean isHerb;
    private String icon;
    private String name;

    public DropListTable() {
        super(DropListTable.class);
        final L2Item l2Item = ItemTemplates.getInstance().getTemplate(getItemId());
        this.isHerb = l2Item.isHerb();
        this.icon = l2Item.getIcon();
        this.name = l2Item.getName();
    }

    public int getMobId(){
        return getStat("mobId", 0);
    }

    public int getItemId() {
        return getStat("itemId", 0);
    }

    public int getMin() {
        return getStat("min", 0);
    }

    public int getMax() {
        return getStat("max",0);
    }

    public int getSweep() {
        return getStat("sweep",0);
    }

    public int getChance() {
        return getStat("chance",0);
    }

    public int getCategory() {
        return getStat("category",0);
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
