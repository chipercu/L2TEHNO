package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.SevenSignsFestivalResource.*;

@Table(
        name = "seven_signs_festival",
        primary_key = {FESTIVAL_ID,CABAL,CYCLE},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = FESTIVAL_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 4 , name = CABAL , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = CYCLE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.BIGINT , type_size = 0 , name = _DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.MEDIUMINT , type_size = 0 , name = SCORE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = MEMBERS , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYTEXT , type_size = 255 , name = NAMES , defValue = "null"),
        }
)
public class SevenSignsFestivalResource extends DataBaseTable<SevenSignsFestivalResource> {

    public static final String FESTIVAL_ID = "festivalId";
    public static final String CABAL = "cabal";
    public static final String CYCLE = "cycle";
    public static final String _DATE = "date";
    public static final String SCORE = "score";
    public static final String MEMBERS = "members";
    public static final String NAMES = "names";

    public SevenSignsFestivalResource() {
        super(SevenSignsFestivalResource.class);
}

    public Integer getFestivalId() {
        return get(FESTIVAL_ID);
    }
    public String getCabal() {
        return get(CABAL);
    }
    public Integer getCycle() {
        return get(CYCLE);
    }
    public Long getDate() {
        return get(_DATE);
    }
    public Integer getScore() {
        return get(SCORE);
    }
    public String getMembers() {
        return get(MEMBERS);
    }
    public String getNames() {
        return get(NAMES);
    }

    public void setFestivalId(Integer value) {
        set(FESTIVAL_ID, value);
    }
    public void setCabal(String value) {
        set(CABAL, value);
    }
    public void setCycle(Integer value) {
        set(CYCLE, value);
    }
    public void setDate(Long value) {
        set(_DATE, value);
    }
    public void setScore(Integer value) {
        set(SCORE, value);
    }
    public void setMembers(String value) {
        set(MEMBERS, value);
    }
    public void setNames(String value) {
        set(NAMES, value);
    }

}
