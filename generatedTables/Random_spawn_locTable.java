package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Random_spawn_locTable.*;

@Table(
        name = "random_spawn_loc",
        fields = {
                @Field(name = GROUPID,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
                @Field(name = HEADING,
        }
)
public class Random_spawn_locTable extends DataBaseTable<Random_spawn_locTable> {

    public static final String GROUPID = "groupId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";
    public Random_spawn_locTable() {
super(Random_spawn_locTable.class);
}
    public IntegergetGroupId() {
        return get(GROUPID, Integer.class);
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

    public void setGroupId() {
        set(GROUPID, value);
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

}
