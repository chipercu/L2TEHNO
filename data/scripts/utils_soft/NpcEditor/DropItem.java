package utils_soft.NpcEditor;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */

public class DropItem {

    private int id;
    private int min;
    private int max;
    private int group;
    private int chance;
    private boolean isSpoil;
    private boolean isHerb;
    private String icon;
    private String name;

    public DropItem(int id, int min, int max, int group, int chance, boolean isSpoil,boolean isHerb, String icon, String name) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.group = group;
        this.chance = chance;
        this.isSpoil = isSpoil;
        this.isHerb = isHerb;
        this.icon = icon;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public boolean getIsSpoil() {
        return isSpoil;
    }

    public void setIsSpoil(boolean isSpoil) {
        this.isSpoil = isSpoil;
    }

    public boolean isSpoil() {
        return isSpoil;
    }

    public boolean isHerb() {
        return isHerb;
    }

    public void setHerb(boolean herb) {
        isHerb = herb;
    }

    public void setSpoil(boolean spoil) {
        isSpoil = spoil;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
