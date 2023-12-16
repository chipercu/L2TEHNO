package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Community_perform_buffsTable.*;

@Table(
        name = "community_perform_buffs",
        fields = {
                @Field(name = ID,
                @Field(name = DISPLAY_LEVEL,
                @Field(name = SKILL_ID,
                @Field(name = SKILL_LEVEL,
                @Field(name = NAME,
                @Field(name = PRICE,
                @Field(name = PRICE_ITEM,
                @Field(name = MINLEVEL,
                @Field(name = MAXLEVEL,
                @Field(name = ICON,
                @Field(name = TYPE_,
        }
)
public class Community_perform_buffsTable extends DataBaseTable<Community_perform_buffsTable> {

    public static final String ID = "id";
    public static final String DISPLAY_LEVEL = "display_level";
    public static final String SKILL_ID = "skill_id";
    public static final String SKILL_LEVEL = "skill_level";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String PRICE_ITEM = "price_item";
    public static final String MINLEVEL = "minLevel";
    public static final String MAXLEVEL = "maxLevel";
    public static final String ICON = "icon";
    public static final String TYPE_ = "type_";
    public Community_perform_buffsTable() {
super(Community_perform_buffsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetDisplay_level() {
        return get(DISPLAY_LEVEL, Integer.class);
    }
    public IntegergetSkill_id() {
        return get(SKILL_ID, Integer.class);
    }
    public IntegergetSkill_level() {
        return get(SKILL_LEVEL, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetPrice() {
        return get(PRICE, Integer.class);
    }
    public IntegergetPrice_item() {
        return get(PRICE_ITEM, Integer.class);
    }
    public IntegergetMinLevel() {
        return get(MINLEVEL, Integer.class);
    }
    public IntegergetMaxLevel() {
        return get(MAXLEVEL, Integer.class);
    }
    public StringgetIcon() {
        return get(ICON, String.class);
    }
    public StringgetType_() {
        return get(TYPE_, String.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setDisplay_level() {
        set(DISPLAY_LEVEL, value);
    }
    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setSkill_level() {
        set(SKILL_LEVEL, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setPrice() {
        set(PRICE, value);
    }
    public void setPrice_item() {
        set(PRICE_ITEM, value);
    }
    public void setMinLevel() {
        set(MINLEVEL, value);
    }
    public void setMaxLevel() {
        set(MAXLEVEL, value);
    }
    public void setIcon() {
        set(ICON, value);
    }
    public void setType_() {
        set(TYPE_, value);
    }

}
