package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.RaidbossPointsResource.*;

@Table(
        name = "raidboss_points",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OWNER_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = BOSS_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = POINTS , defValue = "0"),
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
