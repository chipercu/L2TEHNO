package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Raidboss_pointsTable.*;

@Table(
        name = "raidboss_points",
        fields = {
                @Field(name = OWNER_ID,
                @Field(name = BOSS_ID,
                @Field(name = POINTS,
        }
)
public class Raidboss_pointsTable extends DataBaseTable<Raidboss_pointsTable> {

    public static final String OWNER_ID = "owner_id";
    public static final String BOSS_ID = "boss_id";
    public static final String POINTS = "points";
    public Raidboss_pointsTable() {
super(Raidboss_pointsTable.class);
}
    public IntegergetOwner_id() {
        return get(OWNER_ID, Integer.class);
    }
    public IntegergetBoss_id() {
        return get(BOSS_ID, Integer.class);
    }
    public IntegergetPoints() {
        return get(POINTS, Integer.class);
    }

    public void setOwner_id() {
        set(OWNER_ID, value);
    }
    public void setBoss_id() {
        set(BOSS_ID, value);
    }
    public void setPoints() {
        set(POINTS, value);
    }

}
