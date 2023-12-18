package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.RandomSpawnLocResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "random_spawn_loc",
        primary_key = {GROUP_ID,X,Y,Z,HEADING},
        fields = {
                @Field(name = GROUP_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = X , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Y , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Z , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HEADING , type = INT , nullable = false , defValue = @DefValue(Integer = -1)),
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


    public RandomSpawnLocResource(String groupId,String x,String y,String z,String heading){
        super(RandomSpawnLocResource.class);
        getSTAT_SET().set(GROUP_ID, groupId);
        getSTAT_SET().set(X, x);
        getSTAT_SET().set(Y, y);
        getSTAT_SET().set(Z, z);
        getSTAT_SET().set(HEADING, heading);
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
