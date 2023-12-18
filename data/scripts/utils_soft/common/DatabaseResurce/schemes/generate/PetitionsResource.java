package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.PetitionsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "petitions",
        primary_key = {},
        fields = {
                @Field(name = SERV_ID , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ACT_TIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PETITION_TYPE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ACTOR , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LOCATION_X , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = LOCATION_Y , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = LOCATION_Z , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = PETITION_TEXT , type = TEXT , type_size = 65535 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = S_TR_ACTOR , type = VARCHAR , type_size = 50 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = S_TR_ACTOR_ACCOUNT , type = VARCHAR , type_size = 50 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = PETITION_STATUS , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
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
        return get(SERV_ID, Integer.class);
    }
    public Integer getActTime() {
        return get(ACT_TIME, Integer.class);
    }
    public Integer getPetitionType() {
        return get(PETITION_TYPE, Integer.class);
    }
    public Integer getActor() {
        return get(ACTOR, Integer.class);
    }
    public Integer getLocationX() {
        return get(LOCATION_X, Integer.class);
    }
    public Integer getLocationY() {
        return get(LOCATION_Y, Integer.class);
    }
    public Integer getLocationZ() {
        return get(LOCATION_Z, Integer.class);
    }
    public String getPetitionText() {
        return get(PETITION_TEXT, String.class);
    }
    public String getSTRActor() {
        return get(S_TR_ACTOR, String.class);
    }
    public String getSTRActorAccount() {
        return get(S_TR_ACTOR_ACCOUNT, String.class);
    }
    public Integer getPetitionStatus() {
        return get(PETITION_STATUS, Integer.class);
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
