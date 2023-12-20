package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.FortsResource.*;

@Table(
        name = "forts",
        primary_key = {ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LAST_SIEGE_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SIEGE_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = SKILLS , defValue = "0;0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OWN_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = STATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = CASTLE_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MERCENARY_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = MERCENARY_LOC , defValue = "null"),
        }
)
public class FortsResource extends DataBaseTable<FortsResource> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String LAST_SIEGE_DATE = "lastSiegeDate";
    public static final String SIEGE_DATE = "siegeDate";
    public static final String SKILLS = "skills";
    public static final String OWN_DATE = "ownDate";
    public static final String STATE = "state";
    public static final String CASTLE_ID = "castleId";
    public static final String MERCENARY_ID = "mercenaryId";
    public static final String MERCENARY_LOC = "mercenaryLoc";

    public FortsResource() {
        super(FortsResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getLastSiegeDate() {
        return get(LAST_SIEGE_DATE);
    }
    public Integer getSiegeDate() {
        return get(SIEGE_DATE);
    }
    public String getSkills() {
        return get(SKILLS);
    }
    public Integer getOwnDate() {
        return get(OWN_DATE);
    }
    public Integer getState() {
        return get(STATE);
    }
    public Integer getCastleId() {
        return get(CASTLE_ID);
    }
    public Integer getMercenaryId() {
        return get(MERCENARY_ID);
    }
    public String getMercenaryLoc() {
        return get(MERCENARY_LOC);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setLastSiegeDate(Integer value) {
        set(LAST_SIEGE_DATE, value);
    }
    public void setSiegeDate(Integer value) {
        set(SIEGE_DATE, value);
    }
    public void setSkills(String value) {
        set(SKILLS, value);
    }
    public void setOwnDate(Integer value) {
        set(OWN_DATE, value);
    }
    public void setState(Integer value) {
        set(STATE, value);
    }
    public void setCastleId(Integer value) {
        set(CASTLE_ID, value);
    }
    public void setMercenaryId(Integer value) {
        set(MERCENARY_ID, value);
    }
    public void setMercenaryLoc(String value) {
        set(MERCENARY_LOC, value);
    }

}
