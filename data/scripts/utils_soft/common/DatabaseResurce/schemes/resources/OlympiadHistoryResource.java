package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.OlympiadHistoryResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "olympiad_history",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJECT_ID_1 , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJECT_ID_2 , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLASS_ID_1 , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLASS_ID_2 , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = NAME_1 , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = NAME_2 , defValue = ""),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = GAME_START_TIME , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GAME_TIME , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GAME_STATUS , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GAME_TYPE , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = OLD , defValue = "null"),
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
        return get(OBJECT_ID_1);
    }
    public Integer getObjectId2() {
        return get(OBJECT_ID_2);
    }
    public Integer getClassId1() {
        return get(CLASS_ID_1);
    }
    public Integer getClassId2() {
        return get(CLASS_ID_2);
    }
    public String getName1() {
        return get(NAME_1);
    }
    public String getName2() {
        return get(NAME_2);
    }
    public Long getGameStartTime() {
        return get(GAME_START_TIME);
    }
    public Integer getGameTime() {
        return get(GAME_TIME);
    }
    public Integer getGameStatus() {
        return get(GAME_STATUS);
    }
    public Integer getGameType() {
        return get(GAME_TYPE);
    }
    public Integer getOld() {
        return get(OLD);
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
