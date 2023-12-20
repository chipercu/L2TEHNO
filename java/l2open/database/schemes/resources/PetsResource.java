package l2open.database.schemes.resources;

import l2open.database.anotations.Column;
import l2open.database.anotations.Table;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.PetsResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "pets",
        primary_key = {ITEM_OBJ_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_OBJ_ID , defValue = "0"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = OBJ_ID , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 12 , name = NAME , defValue = "NULL"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = LEVEL , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = CUR_HP , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = CUR_MP , defValue = "NULL"),
                @Column(is_null = YES , type = BIGINT , type_size = 0 , name = EXP , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = SP , defValue = "NULL"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = FED , defValue = "NULL"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = MAX_FED , defValue = "NULL"),
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

    public Integer getItemObjId() {
        return get(ITEM_OBJ_ID);
    }
    public Integer getObjId() {
        return get(OBJ_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Integer getCurHp() {
        return get(CUR_HP);
    }
    public Integer getCurMp() {
        return get(CUR_MP);
    }
    public Long getExp() {
        return get(EXP);
    }
    public Integer getSp() {
        return get(SP);
    }
    public Integer getFed() {
        return get(FED);
    }
    public Integer getMaxFed() {
        return get(MAX_FED);
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
