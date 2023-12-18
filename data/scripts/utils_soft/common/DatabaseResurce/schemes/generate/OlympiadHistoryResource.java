package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.OlympiadHistoryResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "olympiad_history",
        primary_key = {},
        fields = {
                @Field(name = OBJECT_ID_1 , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = OBJECT_ID_2 , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = CLASS_ID_1 , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = CLASS_ID_2 , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = NAME_1 , type = VARCHAR , type_size = 255 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = NAME_2 , type = VARCHAR , type_size = 255 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = GAME_START_TIME , type = BIGINT , nullable = false , defValue = @DefValue(Long = null)),
                @Field(name = GAME_TIME , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = GAME_STATUS , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = GAME_TYPE , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = OLD , type = TINYINT , nullable = false , defValue = @DefValue(Integer = null)),
        }
)
public class OlympiadHistoryResource extends DataBaseTable<OlympiadHistoryResource> {

    public static final String OBJECT_ID_1 = "object_id_1";
    public static final String OBJECT_ID_2 = "object_id_2";
    public static final String CLASS_ID_1 = "class_id_1";
    public static final String CLASS_ID_2 = "class_id_2";
    public static final String NAME_1 = "name_1";
    public static final String NAME_2 = "name_2";
    public static final String GAME_START_TIME = "game_start_time";
    public static final String GAME_TIME = "game_time";
    public static final String GAME_STATUS = "game_status";
    public static final String GAME_TYPE = "game_type";
    public static final String OLD = "old";

    public OlympiadHistoryResource() {
super(OlympiadHistoryResource.class);
}

    public Integer getObjectId1() {
        return get(OBJECT_ID_1, Integer.class);
    }
    public Integer getObjectId2() {
        return get(OBJECT_ID_2, Integer.class);
    }
    public Integer getClassId1() {
        return get(CLASS_ID_1, Integer.class);
    }
    public Integer getClassId2() {
        return get(CLASS_ID_2, Integer.class);
    }
    public String getName1() {
        return get(NAME_1, String.class);
    }
    public String getName2() {
        return get(NAME_2, String.class);
    }
    public Long getGameStartTime() {
        return get(GAME_START_TIME, Long.class);
    }
    public Integer getGameTime() {
        return get(GAME_TIME, Integer.class);
    }
    public Integer getGameStatus() {
        return get(GAME_STATUS, Integer.class);
    }
    public Integer getGameType() {
        return get(GAME_TYPE, Integer.class);
    }
    public Integer getOld() {
        return get(OLD, Integer.class);
    }

    public void setObjectId1(Integer value) {
        set(OBJECT_ID_1, value);
    }
    public void setObjectId2(Integer value) {
        set(OBJECT_ID_2, value);
    }
    public void setClassId1(Integer value) {
        set(CLASS_ID_1, value);
    }
    public void setClassId2(Integer value) {
        set(CLASS_ID_2, value);
    }
    public void setName1(String value) {
        set(NAME_1, value);
    }
    public void setName2(String value) {
        set(NAME_2, value);
    }
    public void setGameStartTime(Long value) {
        set(GAME_START_TIME, value);
    }
    public void setGameTime(Integer value) {
        set(GAME_TIME, value);
    }
    public void setGameStatus(Integer value) {
        set(GAME_STATUS, value);
    }
    public void setGameType(Integer value) {
        set(GAME_TYPE, value);
    }
    public void setOld(Integer value) {
        set(OLD, value);
    }

}
