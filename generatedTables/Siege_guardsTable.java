package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Siege_guardsTable.*;

@Table(
        name = "siege_guards",
        fields = {
                @Field(name = UNITID,
                @Field(name = ID,
                @Field(name = NPCID,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
                @Field(name = HEADING,
                @Field(name = RESPAWNDELAY,
                @Field(name = ISHIRED,
        }
)
public class Siege_guardsTable extends DataBaseTable<Siege_guardsTable> {

    public static final String UNITID = "unitId";
    public static final String ID = "id";
    public static final String NPCID = "npcId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";
    public static final String RESPAWNDELAY = "respawnDelay";
    public static final String ISHIRED = "isHired";
    public Siege_guardsTable() {
super(Siege_guardsTable.class);
}
    public IntegergetUnitId() {
        return get(UNITID, Integer.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetNpcId() {
        return get(NPCID, Integer.class);
    }
    public IntegergetX() {
        return get(X, Integer.class);
    }
    public IntegergetY() {
        return get(Y, Integer.class);
    }
    public IntegergetZ() {
        return get(Z, Integer.class);
    }
    public IntegergetHeading() {
        return get(HEADING, Integer.class);
    }
    public IntegergetRespawnDelay() {
        return get(RESPAWNDELAY, Integer.class);
    }
    public IntegergetIsHired() {
        return get(ISHIRED, Integer.class);
    }

    public void setUnitId() {
        set(UNITID, value);
    }
    public void setId() {
        set(ID, value);
    }
    public void setNpcId() {
        set(NPCID, value);
    }
    public void setX() {
        set(X, value);
    }
    public void setY() {
        set(Y, value);
    }
    public void setZ() {
        set(Z, value);
    }
    public void setHeading() {
        set(HEADING, value);
    }
    public void setRespawnDelay() {
        set(RESPAWNDELAY, value);
    }
    public void setIsHired() {
        set(ISHIRED, value);
    }

}
