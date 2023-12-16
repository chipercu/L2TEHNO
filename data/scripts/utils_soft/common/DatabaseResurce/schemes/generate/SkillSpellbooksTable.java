package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillSpellbooksTable.*;

@Table(
        name = "skill_spellbooks",
        fields = {
                @Field(name = SKILL_ID),
                @Field(name = LEVEL),
                @Field(name = ITEM_ID),
                @Field(name = ITEM_COUNT),
                @Field(name = MIN_LEVEL),
                @Field(name = ITEM_NAME),
        }
)
public class SkillSpellbooksTable extends DataBaseTable<SkillSpellbooksTable> {

    public static final String SKILL_ID = "skill_id";
    public static final String LEVEL = "level";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_COUNT = "item_count";
    public static final String MIN_LEVEL = "min_level";
    public static final String ITEM_NAME = "item_name";

    public SkillSpellbooksTable() {
super(SkillSpellbooksTable.class);
}

    public Integer getSkillId() {
        return get(SKILL_ID, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Integer getItemCount() {
        return get(ITEM_COUNT, Integer.class);
    }
    public Integer getMinLevel() {
        return get(MIN_LEVEL, Integer.class);
    }
    public String getItemName() {
        return get(ITEM_NAME, String.class);
    }

    public void setSkillId(Integer value) {
        set(SKILL_ID, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setItemCount(Integer value) {
        set(ITEM_COUNT, value);
    }
    public void setMinLevel(Integer value) {
        set(MIN_LEVEL, value);
    }
    public void setItemName(String value) {
        set(ITEM_NAME, value);
    }

}
