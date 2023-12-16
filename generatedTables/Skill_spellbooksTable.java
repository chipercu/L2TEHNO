package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Skill_spellbooksTable.*;

@Table(
        name = "skill_spellbooks",
        fields = {
                @Field(name = SKILL_ID,
                @Field(name = LEVEL,
                @Field(name = ITEM_ID,
                @Field(name = ITEM_COUNT,
                @Field(name = MIN_LEVEL,
                @Field(name = ITEM_NAME,
        }
)
public class Skill_spellbooksTable extends DataBaseTable<Skill_spellbooksTable> {

    public static final String SKILL_ID = "skill_id";
    public static final String LEVEL = "level";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_COUNT = "item_count";
    public static final String MIN_LEVEL = "min_level";
    public static final String ITEM_NAME = "item_name";
    public Skill_spellbooksTable() {
super(Skill_spellbooksTable.class);
}
    public IntegergetSkill_id() {
        return get(SKILL_ID, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public IntegergetItem_id() {
        return get(ITEM_ID, Integer.class);
    }
    public IntegergetItem_count() {
        return get(ITEM_COUNT, Integer.class);
    }
    public IntegergetMin_level() {
        return get(MIN_LEVEL, Integer.class);
    }
    public StringgetItem_name() {
        return get(ITEM_NAME, String.class);
    }

    public void setSkill_id() {
        set(SKILL_ID, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setItem_id() {
        set(ITEM_ID, value);
    }
    public void setItem_count() {
        set(ITEM_COUNT, value);
    }
    public void setMin_level() {
        set(MIN_LEVEL, value);
    }
    public void setItem_name() {
        set(ITEM_NAME, value);
    }

}
