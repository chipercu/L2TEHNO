package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RaidbossPointsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "raidboss_points",
        primary_key = {},
        fields = {
                @Field(name = OWNER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = BOSS_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = POINTS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class RaidbossPointsResource extends DataBaseTable<RaidbossPointsResource> {

    public static final String OWNER_ID = "owner_id";
    public static final String BOSS_ID = "boss_id";
    public static final String POINTS = "points";

    public RaidbossPointsResource() {
super(RaidbossPointsResource.class);
}

    public Integer getOwnerId() {
        return get(OWNER_ID, Integer.class);
    }
    public Integer getBossId() {
        return get(BOSS_ID, Integer.class);
    }
    public Integer getPoints() {
        return get(POINTS, Integer.class);
    }

    public void setOwnerId(Integer value) {
        set(OWNER_ID, value);
    }
    public void setBossId(Integer value) {
        set(BOSS_ID, value);
    }
    public void setPoints(Integer value) {
        set(POINTS, value);
    }

}
