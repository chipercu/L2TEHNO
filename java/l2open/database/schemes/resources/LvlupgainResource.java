package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.LvlupgainResource.*;

@Table(
        name = "lvlupgain",
        primary_key = {CLASSID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLASSID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTHPBASE , defValue = "0.0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTHPADD , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTHPMOD , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTCPBASE , defValue = "0.0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTCPADD , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTCPMOD , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTMPBASE , defValue = "0.0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTMPADD , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DEFAULTMPMOD , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLASS_LVL , defValue = "0"),
        }
)
public class LvlupgainResource extends DataBaseTable<LvlupgainResource> {

    public static final String CLASSID = "classid";
    public static final String DEFAULTHPBASE = "defaulthpbase";
    public static final String DEFAULTHPADD = "defaulthpadd";
    public static final String DEFAULTHPMOD = "defaulthpmod";
    public static final String DEFAULTCPBASE = "defaultcpbase";
    public static final String DEFAULTCPADD = "defaultcpadd";
    public static final String DEFAULTCPMOD = "defaultcpmod";
    public static final String DEFAULTMPBASE = "defaultmpbase";
    public static final String DEFAULTMPADD = "defaultmpadd";
    public static final String DEFAULTMPMOD = "defaultmpmod";
    public static final String CLASS_LVL = "class_lvl";

    public LvlupgainResource() {
        super(LvlupgainResource.class);
}

    public Integer getClassid() {
        return get(CLASSID);
    }
    public Double getDefaulthpbase() {
        return get(DEFAULTHPBASE);
    }
    public Double getDefaulthpadd() {
        return get(DEFAULTHPADD);
    }
    public Double getDefaulthpmod() {
        return get(DEFAULTHPMOD);
    }
    public Double getDefaultcpbase() {
        return get(DEFAULTCPBASE);
    }
    public Double getDefaultcpadd() {
        return get(DEFAULTCPADD);
    }
    public Double getDefaultcpmod() {
        return get(DEFAULTCPMOD);
    }
    public Double getDefaultmpbase() {
        return get(DEFAULTMPBASE);
    }
    public Double getDefaultmpadd() {
        return get(DEFAULTMPADD);
    }
    public Double getDefaultmpmod() {
        return get(DEFAULTMPMOD);
    }
    public Integer getClassLvl() {
        return get(CLASS_LVL);
    }

    public void setClassid(Integer value) {
        set(CLASSID, value);
    }
    public void setDefaulthpbase(Double value) {
        set(DEFAULTHPBASE, value);
    }
    public void setDefaulthpadd(Double value) {
        set(DEFAULTHPADD, value);
    }
    public void setDefaulthpmod(Double value) {
        set(DEFAULTHPMOD, value);
    }
    public void setDefaultcpbase(Double value) {
        set(DEFAULTCPBASE, value);
    }
    public void setDefaultcpadd(Double value) {
        set(DEFAULTCPADD, value);
    }
    public void setDefaultcpmod(Double value) {
        set(DEFAULTCPMOD, value);
    }
    public void setDefaultmpbase(Double value) {
        set(DEFAULTMPBASE, value);
    }
    public void setDefaultmpadd(Double value) {
        set(DEFAULTMPADD, value);
    }
    public void setDefaultmpmod(Double value) {
        set(DEFAULTMPMOD, value);
    }
    public void setClassLvl(Integer value) {
        set(CLASS_LVL, value);
    }

}
