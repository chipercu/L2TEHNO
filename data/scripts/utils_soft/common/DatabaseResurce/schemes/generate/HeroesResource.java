package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.HeroesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "heroes",
        primary_key = {CHAR_ID},
        fields = {
                @Field(name = CHAR_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = COUNT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PLAYED , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ACTIVE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MESSAGE , type = VARCHAR , type_size = 300 , nullable = false , defValue = @DefValue(String = "")),
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


    public HeroesResource(String char_id){
        super(HeroesResource.class);
        getSTAT_SET().set(CHAR_ID, char_id);
    }

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getCount() {
        return get(COUNT, Integer.class);
    }
    public Integer getPlayed() {
        return get(PLAYED, Integer.class);
    }
    public Integer getActive() {
        return get(ACTIVE, Integer.class);
    }
    public String getMessage() {
        return get(MESSAGE, String.class);
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
