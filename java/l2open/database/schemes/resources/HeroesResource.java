package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.HeroesResource.*;

@Table(
        name = "heroes",
        primary_key = {CHAR_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = COUNT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = PLAYED , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ACTIVE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 300 , name = MESSAGE , defValue = ""),
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
