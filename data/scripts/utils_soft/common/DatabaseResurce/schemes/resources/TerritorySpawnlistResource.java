package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.TerritorySpawnlistResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "territory_spawnlist",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CASTLE_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Y , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Z , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HEADING , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SPAWN_TYPE , defValue = "0"),
        }
)
public class TerritorySpawnlistResource extends DataBaseTable<TerritorySpawnlistResource> {

    public static final String CASTLE_ID = "castleId";
    public static final String NPC_ID = "npcId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";
    public static final String SPAWN_TYPE = "spawnType";

    public TerritorySpawnlistResource() {
        super(TerritorySpawnlistResource.class);
}

    public Integer getCastleId() {
        return get(CASTLE_ID);
    }
    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getX() {
        return get(X);
    }
    public Integer getY() {
        return get(Y);
    }
    public Integer getZ() {
        return get(Z);
    }
    public Integer getHeading() {
        return get(HEADING);
    }
    public Integer getSpawnType() {
        return get(SPAWN_TYPE);
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
