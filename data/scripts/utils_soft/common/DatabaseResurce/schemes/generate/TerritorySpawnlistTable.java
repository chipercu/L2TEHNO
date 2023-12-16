package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TerritorySpawnlistTable.*;

@Table(
        name = "territory_spawnlist",
        fields = {
                @Field(name = CASTLE_ID),
                @Field(name = NPC_ID),
                @Field(name = X),
                @Field(name = Y),
                @Field(name = Z),
                @Field(name = HEADING),
                @Field(name = SPAWN_TYPE),
        }
)
public class TerritorySpawnlistTable extends DataBaseTable<TerritorySpawnlistTable> {

    public static final String CASTLE_ID = "castleId";
    public static final String NPC_ID = "npcId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";
    public static final String SPAWN_TYPE = "spawnType";

    public TerritorySpawnlistTable() {
super(TerritorySpawnlistTable.class);
}

    public Integer getCastleId() {
        return get(CASTLE_ID, Integer.class);
    }
    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public Integer getX() {
        return get(X, Integer.class);
    }
    public Integer getY() {
        return get(Y, Integer.class);
    }
    public Integer getZ() {
        return get(Z, Integer.class);
    }
    public Integer getHeading() {
        return get(HEADING, Integer.class);
    }
    public Integer getSpawnType() {
        return get(SPAWN_TYPE, Integer.class);
    }

    public void setCastleId(Integer value) {
        set(CASTLE_ID, value);
    }
    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setX(Integer value) {
        set(X, value);
    }
    public void setY(Integer value) {
        set(Y, value);
    }
    public void setZ(Integer value) {
        set(Z, value);
    }
    public void setHeading(Integer value) {
        set(HEADING, value);
    }
    public void setSpawnType(Integer value) {
        set(SPAWN_TYPE, value);
    }

}