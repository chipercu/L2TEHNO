package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SkillSpellbooksResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "skill_spellbooks",
        primary_key = {SKILL_ID,LEVEL,ITEM_ID},
        fields = {
                @Field(name = SKILL_ID , type = INT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = ITEM_ID , type = INT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = ITEM_COUNT , type = INT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = MIN_LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = ITEM_NAME , type = VARCHAR , type_size = 100 , nullable = false , defValue = @DefValue(String = "")),
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


    public SkillSpellbooksResource(String skill_id,String level,String item_id){
        super(SkillSpellbooksResource.class);
        getSTAT_SET().set(SKILL_ID, skill_id);
        getSTAT_SET().set(LEVEL, level);
        getSTAT_SET().set(ITEM_ID, item_id);
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
