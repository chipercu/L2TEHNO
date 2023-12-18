package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SiegeGuardsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "siege_guards",
        primary_key = {ID},
        fields = {
                @Field(name = UNIT_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = NPC_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = X , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Y , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Z , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HEADING , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = RESPAWN_DELAY , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = IS_HIRED , type = INT , nullable = false , defValue = @DefValue(Integer = 1)),
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


    public SiegeGuardsResource(String id){
        super(SiegeGuardsResource.class);
        getSTAT_SET().set(ID, id);
    }

    public Integer getUnitId() {
        return get(UNIT_ID, Integer.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
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
    public Integer getRespawnDelay() {
        return get(RESPAWN_DELAY, Integer.class);
    }
    public Integer getIsHired() {
        return get(IS_HIRED, Integer.class);
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
