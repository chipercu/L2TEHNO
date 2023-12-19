package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.PetitionsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "petitions",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SERV_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ACT_TIME , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PETITION_TYPE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ACTOR , defValue = "0"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = LOCATION_X , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = LOCATION_Y , defValue = "NULL"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = LOCATION_Z , defValue = "NULL"),
                @Column(is_null = NO , type = TEXT , type_size = 65535 , name = PETITION_TEXT , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = S_TR_ACTOR , defValue = "NULL"),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = S_TR_ACTOR_ACCOUNT , defValue = "NULL"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PETITION_STATUS , defValue = "0"),
        }
)
public class PetitionsResource extends DataBaseTable<PetitionsResource> {

    public static final String SERV_ID = "serv_id";
    public static final String ACT_TIME = "act_time";
    public static final String PETITION_TYPE = "petition_type";
    public static final String ACTOR = "actor";
    public static final String LOCATION_X = "location_x";
    public static final String LOCATION_Y = "location_y";
    public static final String LOCATION_Z = "location_z";
    public static final String PETITION_TEXT = "petition_text";
    public static final String S_TR_ACTOR = "STR_actor";
    public static final String S_TR_ACTOR_ACCOUNT = "STR_actor_account";
    public static final String PETITION_STATUS = "petition_status";

    public PetitionsResource() {
        super(PetitionsResource.class);
}

    public Integer getServId() {
        return get(SERV_ID);
    }
    public Integer getActTime() {
        return get(ACT_TIME);
    }
    public Integer getPetitionType() {
        return get(PETITION_TYPE);
    }
    public Integer getActor() {
        return get(ACTOR);
    }
    public Integer getLocationX() {
        return get(LOCATION_X);
    }
    public Integer getLocationY() {
        return get(LOCATION_Y);
    }
    public Integer getLocationZ() {
        return get(LOCATION_Z);
    }
    public String getPetitionText() {
        return get(PETITION_TEXT);
    }
    public String getSTRActor() {
        return get(S_TR_ACTOR);
    }
    public String getSTRActorAccount() {
        return get(S_TR_ACTOR_ACCOUNT);
    }
    public Integer getPetitionStatus() {
        return get(PETITION_STATUS);
    }

    public void setServId(Integer value) {
        set(SERV_ID, value);
    }
    public void setActTime(Integer value) {
        set(ACT_TIME, value);
    }
    public void setPetitionType(Integer value) {
        set(PETITION_TYPE, value);
    }
    public void setActor(Integer value) {
        set(ACTOR, value);
    }
    public void setLocationX(Integer value) {
        set(LOCATION_X, value);
    }
    public void setLocationY(Integer value) {
        set(LOCATION_Y, value);
    }
    public void setLocationZ(Integer value) {
        set(LOCATION_Z, value);
    }
    public void setPetitionText(String value) {
        set(PETITION_TEXT, value);
    }
    public void setSTRActor(String value) {
        set(S_TR_ACTOR, value);
    }
    public void setSTRActorAccount(String value) {
        set(S_TR_ACTOR_ACCOUNT, value);
    }
    public void setPetitionStatus(Integer value) {
        set(PETITION_STATUS, value);
    }

}
