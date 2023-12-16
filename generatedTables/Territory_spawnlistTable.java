package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Territory_spawnlistTable.*;

@Table(
        name = "territory_spawnlist",
        fields = {
                @Field(name = CASTLEID,
                @Field(name = NPCID,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
                @Field(name = HEADING,
                @Field(name = SPAWNTYPE,
        }
)
public class Territory_spawnlistTable extends DataBaseTable<Territory_spawnlistTable> {

    public static final String CASTLEID = "castleId";
    public static final String NPCID = "npcId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";
    public static final String SPAWNTYPE = "spawnType";
    public Territory_spawnlistTable() {
super(Territory_spawnlistTable.class);
}
    public IntegergetCastleId() {
        return get(CASTLEID, Integer.class);
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
    public IntegergetSpawnType() {
        return get(SPAWNTYPE, Integer.class);
    }

    public void setCastleId() {
        set(CASTLEID, value);
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
    public void setSpawnType() {
        set(SPAWNTYPE, value);
    }

}
