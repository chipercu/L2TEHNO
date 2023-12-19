package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.HeroesDiaryResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "heroes_diary",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "null"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = _TIME , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ACTION , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PARAM , defValue = "0"),
        }
)
public class HeroesDiaryResource extends DataBaseTable<HeroesDiaryResource> {

    public static final String CHAR_ID = "charId";
    public static final String _TIME = "time";
    public static final String ACTION = "action";
    public static final String PARAM = "param";

    public HeroesDiaryResource() {
        super(HeroesDiaryResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Long getTime() {
        return get(_TIME);
    }
    public Integer getAction() {
        return get(ACTION);
    }
    public Integer getParam() {
        return get(PARAM);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setTime(Long value) {
        set(_TIME, value);
    }
    public void setAction(Integer value) {
        set(ACTION, value);
    }
    public void setParam(Integer value) {
        set(PARAM, value);
    }

}
