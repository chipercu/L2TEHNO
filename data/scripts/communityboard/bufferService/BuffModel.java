package communityboard.bufferService;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * Created by a.kiperku
 * Date: 19.10.2023
 */

public class BuffModel {

    private int buff_id;
    private int buff_level;
    private int display_level;
    private String name;
    private String enchant_name;
    private long duration;
    private int price;
    private int price_item;
    private String icon;
    private String list_type;


    public BuffModel(int buff_id, int buff_level, int display_level, String name, String enchant_name, long duration, int price, int price_item, String icon, String list_type) {
        this.buff_id = buff_id;
        this.buff_level = buff_level;
        this.display_level = display_level;
        this.name = name;
        this.enchant_name = enchant_name;
        this.duration = duration;
        this.price = price;
        this.price_item = price_item;
        this.icon = icon;
        this.list_type = list_type;
    }

    public BuffModel(int buff_id, int buff_level, String enchant_name, String list_type) {
        final L2Skill skill = SkillTable.getInstance().getInfo(buff_id, buff_level);
        this.buff_id = buff_id;
        this.buff_level = buff_level;
        this.display_level = skill.getDisplayLevel();
        this.name = skill.getName();
        this.enchant_name = enchant_name;
        this.duration = ConfigValue.BufferTime * 60000L;
        this.price = ConfigValue.BufferPriceOne;
        this.price_item = ConfigValue.BufferItem;
        this.icon = skill.getIcon();
        this.list_type = list_type;
    }

    public BuffModel(L2Skill skill, String enchant_name, String list_type) {
        this.buff_id = skill.getId();
        this.buff_level = skill.getLevel();
        this.display_level = skill.getDisplayLevel();
        this.name = skill.getName();
        this.enchant_name = enchant_name;
        this.duration = ConfigValue.BufferTime * 60000L;
        this.price = ConfigValue.BufferPriceOne;
        this.price_item = ConfigValue.BufferItem;
        this.icon = skill.getIcon();
        this.list_type = list_type;
    }

    public int getBuff_id() {
        return buff_id;
    }

    public void setBuff_id(int buff_id) {
        this.buff_id = buff_id;
    }

    public int getBuff_level() {
        return buff_level;
    }

    public void setBuff_level(int buff_level) {
        this.buff_level = buff_level;
    }

    public int getDisplay_level() {
        return display_level;
    }

    public void setDisplay_level(int display_level) {
        this.display_level = display_level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnchant_name() {
        return enchant_name;
    }

    public void setEnchant_name(String enchant_name) {
        this.enchant_name = enchant_name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice_item() {
        return price_item;
    }

    public void setPrice_item(int price_item) {
        this.price_item = price_item;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getList_type() {
        return list_type;
    }

    public void setList_type(String list_type) {
        this.list_type = list_type;
    }
}
