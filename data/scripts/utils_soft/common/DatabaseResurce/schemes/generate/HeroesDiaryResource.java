package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HeroesDiaryResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "heroes_diary",
        primary_key = {},
        fields = {
                @Field(name = CHAR_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = _TIME , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = ACTION , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PARAM , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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
        return get(CHAR_ID, Integer.class);
    }
    public Long getTime() {
        return get(_TIME, Long.class);
    }
    public Integer getAction() {
        return get(ACTION, Integer.class);
    }
    public Integer getParam() {
        return get(PARAM, Integer.class);
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
