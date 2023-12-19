package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.FortsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "forts",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = NAME , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LAST_SIEGE_DATE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SIEGE_DATE , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 32 , name = SKILLS , defValue = "0;0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OWN_DATE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = STATE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CASTLE_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MERCENARY_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = MERCENARY_LOC , defValue = "null"),
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
