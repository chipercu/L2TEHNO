package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.RandomSpawnLocResource.*;

@Table(
        name = "random_spawn_loc",
        primary_key = {GROUP_ID,X,Y,Z,HEADING},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = GROUP_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = X , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = Y , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = Z , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = HEADING , defValue = "-1"),
        }
)
public class RandomSpawnLocResource extends DataBaseTable<RandomSpawnLocResource> {

    public static final String GROUP_ID = "groupId";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String HEADING = "heading";

    public RandomSpawnLocResource() {
        super(RandomSpawnLocResource.class);
}

    public Integer getGroupId() {
        return get(GROUP_ID);
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
