package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.LvlupgainTable.*;

@Table(
        name = "lvlupgain",
        fields = {
                @Field(name = CLASSID,
                @Field(name = DEFAULTHPBASE,
                @Field(name = DEFAULTHPADD,
                @Field(name = DEFAULTHPMOD,
                @Field(name = DEFAULTCPBASE,
                @Field(name = DEFAULTCPADD,
                @Field(name = DEFAULTCPMOD,
                @Field(name = DEFAULTMPBASE,
                @Field(name = DEFAULTMPADD,
                @Field(name = DEFAULTMPMOD,
                @Field(name = CLASS_LVL,
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
    public IntegergetClassid() {
        return get(CLASSID, Integer.class);
    }
    public DoublegetDefaulthpbase() {
        return get(DEFAULTHPBASE, Double.class);
    }
    public DoublegetDefaulthpadd() {
        return get(DEFAULTHPADD, Double.class);
    }
    public DoublegetDefaulthpmod() {
        return get(DEFAULTHPMOD, Double.class);
    }
    public DoublegetDefaultcpbase() {
        return get(DEFAULTCPBASE, Double.class);
    }
    public DoublegetDefaultcpadd() {
        return get(DEFAULTCPADD, Double.class);
    }
    public DoublegetDefaultcpmod() {
        return get(DEFAULTCPMOD, Double.class);
    }
    public DoublegetDefaultmpbase() {
        return get(DEFAULTMPBASE, Double.class);
    }
    public DoublegetDefaultmpadd() {
        return get(DEFAULTMPADD, Double.class);
    }
    public DoublegetDefaultmpmod() {
        return get(DEFAULTMPMOD, Double.class);
    }
    public IntegergetClass_lvl() {
        return get(CLASS_LVL, Integer.class);
    }

    public void setClassid() {
        set(CLASSID, value);
    }
    public void setDefaulthpbase() {
        set(DEFAULTHPBASE, value);
    }
    public void setDefaulthpadd() {
        set(DEFAULTHPADD, value);
    }
    public void setDefaulthpmod() {
        set(DEFAULTHPMOD, value);
    }
    public void setDefaultcpbase() {
        set(DEFAULTCPBASE, value);
    }
    public void setDefaultcpadd() {
        set(DEFAULTCPADD, value);
    }
    public void setDefaultcpmod() {
        set(DEFAULTCPMOD, value);
    }
    public void setDefaultmpbase() {
        set(DEFAULTMPBASE, value);
    }
    public void setDefaultmpadd() {
        set(DEFAULTMPADD, value);
    }
    public void setDefaultmpmod() {
        set(DEFAULTMPMOD, value);
    }
    public void setClass_lvl() {
        set(CLASS_LVL, value);
    }

}
