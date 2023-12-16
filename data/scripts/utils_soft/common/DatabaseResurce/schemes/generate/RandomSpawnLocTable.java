package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RandomSpawnLocTable.*;

@Table(
        name = "random_spawn_loc",
        fields = {
                @Field(name = GROUP_ID),
                @Field(name = X),
                @Field(name = Y),
                @Field(name = Z),
                @Field(name = HEADING),
        }
)
public class RandomSpawnLocTable extends DataBaseTable<RandomSpawnLocTable> {

    public static final String GROUP_ID = "groupId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";

    public RandomSpawnLocTable() {
super(RandomSpawnLocTable.class);
}

    public Integer getGroupId() {
        return get(GROUP_ID, Integer.class);
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

    public void setGroupId(Integer value) {
        set(GROUP_ID, value);
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

}
