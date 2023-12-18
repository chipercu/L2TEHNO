package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.PetsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "pets",
        primary_key = {ITEM_OBJ_ID},
        fields = {
                @Field(name = ITEM_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = OBJ_ID , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = NAME , type = VARCHAR , type_size = 12 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = LEVEL , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CUR_HP , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CUR_MP , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = EXP , type = BIGINT , nullable = true , defValue = @DefValue(Long = NULL)),
                @Field(name = SP , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = FED , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = MAX_FED , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = NULL)),
        }
)
public class PetsResource extends DataBaseTable<PetsResource> {

    public static final String ITEM_OBJ_ID = "item_obj_id";
    public static final String OBJ_ID = "objId";
    public static final String NAME = "name";
    public static final String LEVEL = "level";
    public static final String CUR_HP = "curHp";
    public static final String CUR_MP = "curMp";
    public static final String EXP = "exp";
    public static final String SP = "sp";
    public static final String FED = "fed";
    public static final String MAX_FED = "max_fed";

    public PetsResource() {
super(PetsResource.class);
}


    public PetsResource(String item_obj_id){
        super(PetsResource.class);
        getSTAT_SET().set(ITEM_OBJ_ID, item_obj_id);
    }

    public Integer getItemObjId() {
        return get(ITEM_OBJ_ID, Integer.class);
    }
    public Integer getObjId() {
        return get(OBJ_ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Integer getCurHp() {
        return get(CUR_HP, Integer.class);
    }
    public Integer getCurMp() {
        return get(CUR_MP, Integer.class);
    }
    public Long getExp() {
        return get(EXP, Long.class);
    }
    public Integer getSp() {
        return get(SP, Integer.class);
    }
    public Integer getFed() {
        return get(FED, Integer.class);
    }
    public Integer getMaxFed() {
        return get(MAX_FED, Integer.class);
    }

    public void setItemObjId(Integer value) {
        set(ITEM_OBJ_ID, value);
    }
    public void setObjId(Integer value) {
        set(OBJ_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setCurHp(Integer value) {
        set(CUR_HP, value);
    }
    public void setCurMp(Integer value) {
        set(CUR_MP, value);
    }
    public void setExp(Long value) {
        set(EXP, value);
    }
    public void setSp(Integer value) {
        set(SP, value);
    }
    public void setFed(Integer value) {
        set(FED, value);
    }
    public void setMaxFed(Integer value) {
        set(MAX_FED, value);
    }

}
