package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LvlupgainTable.*;

@Table(
        name = "lvlupgain",
        fields = {
                @Field(name = CLASSID),
                @Field(name = DEFAULTHPBASE),
                @Field(name = DEFAULTHPADD),
                @Field(name = DEFAULTHPMOD),
                @Field(name = DEFAULTCPBASE),
                @Field(name = DEFAULTCPADD),
                @Field(name = DEFAULTCPMOD),
                @Field(name = DEFAULTMPBASE),
                @Field(name = DEFAULTMPADD),
                @Field(name = DEFAULTMPMOD),
                @Field(name = CLASS_LVL),
        }
)
public class LvlupgainTable extends DataBaseTable<LvlupgainTable> {

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

    public LvlupgainTable() {
super(LvlupgainTable.class);
}

    public Integer getClassid() {
        return get(CLASSID, Integer.class);
    }
    public Double getDefaulthpbase() {
        return get(DEFAULTHPBASE, Double.class);
    }
    public Double getDefaulthpadd() {
        return get(DEFAULTHPADD, Double.class);
    }
    public Double getDefaulthpmod() {
        return get(DEFAULTHPMOD, Double.class);
    }
    public Double getDefaultcpbase() {
        return get(DEFAULTCPBASE, Double.class);
    }
    public Double getDefaultcpadd() {
        return get(DEFAULTCPADD, Double.class);
    }
    public Double getDefaultcpmod() {
        return get(DEFAULTCPMOD, Double.class);
    }
    public Double getDefaultmpbase() {
        return get(DEFAULTMPBASE, Double.class);
    }
    public Double getDefaultmpadd() {
        return get(DEFAULTMPADD, Double.class);
    }
    public Double getDefaultmpmod() {
        return get(DEFAULTMPMOD, Double.class);
    }
    public Integer getClassLvl() {
        return get(CLASS_LVL, Integer.class);
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
