package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MapregionTable.*;

@Table(
        name = "mapregion",
        fields = {
                @Field(name = Y10_PLUS),
                @Field(name = X11),
                @Field(name = X12),
                @Field(name = X13),
                @Field(name = X14),
                @Field(name = X15),
                @Field(name = X16),
                @Field(name = X17),
                @Field(name = X18),
                @Field(name = X19),
                @Field(name = X20),
                @Field(name = X21),
                @Field(name = X22),
                @Field(name = X23),
                @Field(name = X24),
                @Field(name = X25),
                @Field(name = X26),
        }
)
public class MapregionTable extends DataBaseTable<MapregionTable> {

    public static final String Y10_PLUS = "y10_plus";
    public static final String X11 = "x11";
    public static final String X12 = "x12";
    public static final String X13 = "x13";
    public static final String X14 = "x14";
    public static final String X15 = "x15";
    public static final String X16 = "x16";
    public static final String X17 = "x17";
    public static final String X18 = "x18";
    public static final String X19 = "x19";
    public static final String X20 = "x20";
    public static final String X21 = "x21";
    public static final String X22 = "x22";
    public static final String X23 = "x23";
    public static final String X24 = "x24";
    public static final String X25 = "x25";
    public static final String X26 = "x26";

    public MapregionTable() {
super(MapregionTable.class);
}

    public Integer getY10Plus() {
        return get(Y10_PLUS, Integer.class);
    }
    public Integer getX11() {
        return get(X11, Integer.class);
    }
    public Integer getX12() {
        return get(X12, Integer.class);
    }
    public Integer getX13() {
        return get(X13, Integer.class);
    }
    public Integer getX14() {
        return get(X14, Integer.class);
    }
    public Integer getX15() {
        return get(X15, Integer.class);
    }
    public Integer getX16() {
        return get(X16, Integer.class);
    }
    public Integer getX17() {
        return get(X17, Integer.class);
    }
    public Integer getX18() {
        return get(X18, Integer.class);
    }
    public Integer getX19() {
        return get(X19, Integer.class);
    }
    public Integer getX20() {
        return get(X20, Integer.class);
    }
    public Integer getX21() {
        return get(X21, Integer.class);
    }
    public Integer getX22() {
        return get(X22, Integer.class);
    }
    public Integer getX23() {
        return get(X23, Integer.class);
    }
    public Integer getX24() {
        return get(X24, Integer.class);
    }
    public Integer getX25() {
        return get(X25, Integer.class);
    }
    public Integer getX26() {
        return get(X26, Integer.class);
    }

    public void setY10Plus(Integer value) {
        set(Y10_PLUS, value);
    }
    public void setX11(Integer value) {
        set(X11, value);
    }
    public void setX12(Integer value) {
        set(X12, value);
    }
    public void setX13(Integer value) {
        set(X13, value);
    }
    public void setX14(Integer value) {
        set(X14, value);
    }
    public void setX15(Integer value) {
        set(X15, value);
    }
    public void setX16(Integer value) {
        set(X16, value);
    }
    public void setX17(Integer value) {
        set(X17, value);
    }
    public void setX18(Integer value) {
        set(X18, value);
    }
    public void setX19(Integer value) {
        set(X19, value);
    }
    public void setX20(Integer value) {
        set(X20, value);
    }
    public void setX21(Integer value) {
        set(X21, value);
    }
    public void setX22(Integer value) {
        set(X22, value);
    }
    public void setX23(Integer value) {
        set(X23, value);
    }
    public void setX24(Integer value) {
        set(X24, value);
    }
    public void setX25(Integer value) {
        set(X25, value);
    }
    public void setX26(Integer value) {
        set(X26, value);
    }

}
