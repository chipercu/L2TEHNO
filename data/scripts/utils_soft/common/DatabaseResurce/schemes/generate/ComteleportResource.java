package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ComteleportResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "comteleport",
        primary_key = {TP_ID},
        fields = {
                @Field(name = TP_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = NAME , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = CHAR_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = X_POS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Y_POS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Z_POS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public ComteleportResource(String TpId){
        super(ComteleportResource.class);
        getSTAT_SET().set(TP_ID, TpId);
    }

    public Integer getTpId() {
        return get(TP_ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getXPos() {
        return get(X_POS, Integer.class);
    }
    public Integer getYPos() {
        return get(Y_POS, Integer.class);
    }
    public Integer getZPos() {
        return get(Z_POS, Integer.class);
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
