package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.RaidbossPointsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "raidboss_points",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OWNER_ID , defValue = "null"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = BOSS_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POINTS , defValue = "0"),
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
        return get(OWNER_ID);
    }
    public Integer getBossId() {
        return get(BOSS_ID);
    }
    public Integer getPoints() {
        return get(POINTS);
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
