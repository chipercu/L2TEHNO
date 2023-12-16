package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.ComteleportTable.*;

@Table(
        name = "comteleport",
        fields = {
                @Field(name = TPID,
                @Field(name = NAME,
                @Field(name = CHARID,
                @Field(name = XPOS,
                @Field(name = YPOS,
                @Field(name = ZPOS,
        }
)
public class ComteleportTable extends DataBaseTable<ComteleportTable> {

    public static final String TPID = "TpId";
    public static final String NAME = "name";
    public static final String CHARID = "charId";
    public static final String XPOS = "xPos";
    public static final String YPOS = "yPos";
    public static final String ZPOS = "zPos";
    public ComteleportTable() {
super(ComteleportTable.class);
}
    public IntegergetTpId() {
        return get(TPID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetCharId() {
        return get(CHARID, Integer.class);
    }
    public IntegergetXPos() {
        return get(XPOS, Integer.class);
    }
    public IntegergetYPos() {
        return get(YPOS, Integer.class);
    }
    public IntegergetZPos() {
        return get(ZPOS, Integer.class);
    }

    public void setTpId() {
        set(TPID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setCharId() {
        set(CHARID, value);
    }
    public void setXPos() {
        set(XPOS, value);
    }
    public void setYPos() {
        set(YPOS, value);
    }
    public void setZPos() {
        set(ZPOS, value);
    }

}
