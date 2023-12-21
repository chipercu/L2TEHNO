package l2open.database.schemes.resources;

import l2open.database.anotations.Table;
import l2open.database.anotations.Column;
import l2open.database.DataBaseTable;
import static l2open.database.schemes.resources.MapregionResource.*;
import static l2open.database.anotations.IS_NULLABLE.*;
import static l2open.database.anotations.DATA_TYPE.*;

@Table(
        name = "mapregion",
        primary_key = {Y10_PLUS},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = Y10_PLUS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X11 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X12 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X13 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X14 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X15 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X16 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X17 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X18 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X19 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X20 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X21 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X22 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X23 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X24 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X25 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X26 , defValue = "0"),
        }
)
public class MapregionResource extends DataBaseTable<MapregionResource> {

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

    public MapregionResource() {
        super(MapregionResource.class);
}

    public Integer getY10Plus() {
        return get(Y10_PLUS);
    }
    public Integer getX11() {
        return get(X11);
    }
    public Integer getX12() {
        return get(X12);
    }
    public Integer getX13() {
        return get(X13);
    }
    public Integer getX14() {
        return get(X14);
    }
    public Integer getX15() {
        return get(X15);
    }
    public Integer getX16() {
        return get(X16);
    }
    public Integer getX17() {
        return get(X17);
    }
    public Integer getX18() {
        return get(X18);
    }
    public Integer getX19() {
        return get(X19);
    }
    public Integer getX20() {
        return get(X20);
    }
    public Integer getX21() {
        return get(X21);
    }
    public Integer getX22() {
        return get(X22);
    }
    public Integer getX23() {
        return get(X23);
    }
    public Integer getX24() {
        return get(X24);
    }
    public Integer getX25() {
        return get(X25);
    }
    public Integer getX26() {
        return get(X26);
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