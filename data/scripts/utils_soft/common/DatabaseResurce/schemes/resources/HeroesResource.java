package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.HeroesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "heroes",
        primary_key = {CHAR_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PLAYED , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ACTIVE , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 300 , name = MESSAGE , defValue = ""),
        }
)
public class HeroesResource extends DataBaseTable<HeroesResource> {

    public static final String CHAR_ID = "char_id";
    public static final String COUNT = "count";
    public static final String PLAYED = "played";
    public static final String ACTIVE = "active";
    public static final String MESSAGE = "message";

    public HeroesResource() {
        super(HeroesResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getCount() {
        return get(COUNT);
    }
    public Integer getPlayed() {
        return get(PLAYED);
    }
    public Integer getActive() {
        return get(ACTIVE);
    }
    public String getMessage() {
        return get(MESSAGE);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setCount(Integer value) {
        set(COUNT, value);
    }
    public void setPlayed(Integer value) {
        set(PLAYED, value);
    }
    public void setActive(Integer value) {
        set(ACTIVE, value);
    }
    public void setMessage(String value) {
        set(MESSAGE, value);
    }

}
