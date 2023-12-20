package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ComteleportResource.*;

@Table(
        name = "comteleport",
        primary_key = {TP_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TP_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = X_POS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = Y_POS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = Z_POS , defValue = "0"),
        }
)
public class ComteleportResource extends DataBaseTable<ComteleportResource> {

    public static final String TP_ID = "TpId";
    public static final String NAME = "name";
    public static final String CHAR_ID = "charId";
    public static final String X_POS = "xPos";
    public static final String Y_POS = "yPos";
    public static final String Z_POS = "zPos";

    public ComteleportResource() {
        super(ComteleportResource.class);
}

    public Integer getTpId() {
        return get(TP_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getXPos() {
        return get(X_POS);
    }
    public Integer getYPos() {
        return get(Y_POS);
    }
    public Integer getZPos() {
        return get(Z_POS);
    }

    public void setTpId(Integer value) {
        set(TP_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setXPos(Integer value) {
        set(X_POS, value);
    }
    public void setYPos(Integer value) {
        set(Y_POS, value);
    }
    public void setZPos(Integer value) {
        set(Z_POS, value);
    }

}
