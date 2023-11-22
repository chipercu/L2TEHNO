package communityboard.bufferService;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.base.L2EnchantSkillLearn;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.GArray;

/**
 * Created by a.kiperku
 * Date: 19.10.2023
 */

public class Buff {

    private long id;
    private long skill_id;
    private long skill_level;
    private int display_level;
    private String name;
    private String enchant_name;
    private long duration;
    private int price;
    private int price_item;
    private int minLevel;
    private int maxLevel;
    private String icon;
    private String type;
    private final GArray<L2EnchantSkillLearn> skillEnchants;

    public Buff(long id, int display_level, String name, long duration, int price, int price_item, int minLevel, int maxLevel, String icon, String type) {
        this.id = id;
        this.skill_id = id / 1000;
        this.skill_level = id % 1000;
        this.display_level = display_level;
        this.skillEnchants = SkillTreeTable.getFirstEnchantsForSkill(skill_id);
        this.name = name;
        this.enchant_name = setEnchantName();
        this.duration = duration;
        this.price = price;
        this.price_item = price_item;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.icon = icon;
        this.type = type;
    }

    public Buff(L2Skill skill, String type) {
        this.id = skill.getId() * 1000L + skill.getLevel();
        this.skillEnchants = SkillTreeTable.getFirstEnchantsForSkill(skill.getId());
        this.skill_id = skill.getId();
        this.skill_level = skill.getLevel();
        this.display_level = skill.getDisplayLevel();
        this.name = skill.getName();
        this.enchant_name = setEnchantName();
        this.duration = ConfigValue.BufferTime * 60000L;
        this.price = ConfigValue.BufferPriceOne;
        this.price_item = ConfigValue.BufferItem;
        this.minLevel = ConfigValue.BufferMinLevel;
        this.maxLevel = ConfigValue.BufferMaxLevel;
        this.icon = skill.getIcon();
        this.type = type;

    }

    public Buff clone(){
        return new Buff(
                this.id,
                this.display_level,
                this.name,
                this.duration,
                this.price,
                this.price_item,
                this.minLevel,
                this.maxLevel,
                this.icon,
                this.type);
    }

    public long getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(long skill_id) {
        this.skill_id = skill_id;
    }

    public long getSkill_level() {
        return skill_level;
    }

    public void setSkill_level(long skill_level) {
        this.skill_level = skill_level;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GArray<L2EnchantSkillLearn> getSkillEnchants() {
        return skillEnchants;
    }

    private String setEnchantName() {
        final int index = (display_level / 100) - 1;
        try {
            final L2EnchantSkillLearn skillLearn = skillEnchants.get(index);
            return skillLearn.getType().replace("+1 ", "");
        }catch (Exception e){
            return "none";
        }
    }

    private L2EnchantSkillLearn setEnchant(){
        if (skillEnchants.isEmpty()){
            return null;
        }
        return skillEnchants.get(display_level / 100);
    }

    public void setNextEnchantType() {
        if (skillEnchants == null || skillEnchants.isEmpty()) {
            return;
        }
        final int size = skillEnchants.size();
        final int index = Math.max((display_level / 100) - 1 , 0);
        final L2EnchantSkillLearn skillLearn = skillEnchants.get(index);
        final int maxSkillLevel = skillLearn.getBaseLevel() + (skillLearn.getMaxLevel() * size);
        long newLevel = skill_level + skillLearn.getMaxLevel();

        if (newLevel > maxSkillLevel){
            newLevel = skillLearn.getBaseLevel();
        }
        skill_level = newLevel;

        display_level = SkillTable.getInstance().getInfo((int) skill_id, (int) skill_level).getDisplayLevel();
        enchant_name = setEnchantName();

    }
}
