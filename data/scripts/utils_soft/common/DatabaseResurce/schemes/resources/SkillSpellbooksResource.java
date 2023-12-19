package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SkillSpellbooksResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "skill_spellbooks",
        primary_key = {SKILL_ID,LEVEL,ITEM_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = SKILL_ID , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEVEL , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_ID , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_COUNT , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MIN_LEVEL , defValue = "1"),
                @Column(is_null = NO , type = VARCHAR , type_size = 100 , name = ITEM_NAME , defValue = ""),
        }
)
public class SkillSpellbooksResource extends DataBaseTable<SkillSpellbooksResource> {

    public static final String SKILL_ID = "skill_id";
    public static final String LEVEL = "level";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_COUNT = "item_count";
    public static final String MIN_LEVEL = "min_level";
    public static final String ITEM_NAME = "item_name";

    public SkillSpellbooksResource() {
        super(SkillSpellbooksResource.class);
}

    public Integer getSkillId() {
        return get(SKILL_ID);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Integer getItemCount() {
        return get(ITEM_COUNT);
    }
    public Integer getMinLevel() {
        return get(MIN_LEVEL);
    }
    public String getItemName() {
        return get(ITEM_NAME);
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
