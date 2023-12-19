package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SiegeGuardsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_guards",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = UNIT_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Y , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Z , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HEADING , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RESPAWN_DELAY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = IS_HIRED , defValue = "1"),
        }
)
public class SiegeGuardsResource extends DataBaseTable<SiegeGuardsResource> {

    public static final String UNIT_ID = "unitId";
    public static final String ID = "id";
    public static final String NPC_ID = "npcId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";
    public static final String RESPAWN_DELAY = "respawnDelay";
    public static final String IS_HIRED = "isHired";

    public SiegeGuardsResource() {
        super(SiegeGuardsResource.class);
}

    public Integer getUnitId() {
        return get(UNIT_ID);
    }
    public Integer getId() {
        return get(ID);
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
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY);
    }
    public Integer getIsHired() {
        return get(IS_HIRED);
    }

    public void setUnitId(Integer value) {
        set(UNIT_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
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
    public void setRespawnDelay(Integer value) {
        set(RESPAWN_DELAY, value);
    }
    public void setIsHired(Integer value) {
        set(IS_HIRED, value);
    }

}
