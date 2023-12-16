package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.PetitionsTable.*;

@Table(
        name = "petitions",
        fields = {
                @Field(name = SERV_ID,
                @Field(name = ACT_TIME,
                @Field(name = PETITION_TYPE,
                @Field(name = ACTOR,
                @Field(name = LOCATION_X,
                @Field(name = LOCATION_Y,
                @Field(name = LOCATION_Z,
                @Field(name = PETITION_TEXT,
                @Field(name = STR_ACTOR,
                @Field(name = STR_ACTOR_ACCOUNT,
                @Field(name = PETITION_STATUS,
        }
)
public class PetitionsTable extends DataBaseTable<PetitionsTable> {

    public static final String SERV_ID = "serv_id";
    public static final String ACT_TIME = "act_time";
    public static final String PETITION_TYPE = "petition_type";
    public static final String ACTOR = "actor";
    public static final String LOCATION_X = "location_x";
    public static final String LOCATION_Y = "location_y";
    public static final String LOCATION_Z = "location_z";
    public static final String PETITION_TEXT = "petition_text";
    public static final String STR_ACTOR = "STR_actor";
    public static final String STR_ACTOR_ACCOUNT = "STR_actor_account";
    public static final String PETITION_STATUS = "petition_status";
    public PetitionsTable() {
super(PetitionsTable.class);
}
    public IntegergetServ_id() {
        return get(SERV_ID, Integer.class);
    }
    public IntegergetAct_time() {
        return get(ACT_TIME, Integer.class);
    }
    public IntegergetPetition_type() {
        return get(PETITION_TYPE, Integer.class);
    }
    public IntegergetActor() {
        return get(ACTOR, Integer.class);
    }
    public IntegergetLocation_x() {
        return get(LOCATION_X, Integer.class);
    }
    public IntegergetLocation_y() {
        return get(LOCATION_Y, Integer.class);
    }
    public IntegergetLocation_z() {
        return get(LOCATION_Z, Integer.class);
    }
    public StringgetPetition_text() {
        return get(PETITION_TEXT, String.class);
    }
    public StringgetSTR_actor() {
        return get(STR_ACTOR, String.class);
    }
    public StringgetSTR_actor_account() {
        return get(STR_ACTOR_ACCOUNT, String.class);
    }
    public IntegergetPetition_status() {
        return get(PETITION_STATUS, Integer.class);
    }

    public void setServ_id() {
        set(SERV_ID, value);
    }
    public void setAct_time() {
        set(ACT_TIME, value);
    }
    public void setPetition_type() {
        set(PETITION_TYPE, value);
    }
    public void setActor() {
        set(ACTOR, value);
    }
    public void setLocation_x() {
        set(LOCATION_X, value);
    }
    public void setLocation_y() {
        set(LOCATION_Y, value);
    }
    public void setLocation_z() {
        set(LOCATION_Z, value);
    }
    public void setPetition_text() {
        set(PETITION_TEXT, value);
    }
    public void setSTR_actor() {
        set(STR_ACTOR, value);
    }
    public void setSTR_actor_account() {
        set(STR_ACTOR_ACCOUNT, value);
    }
    public void setPetition_status() {
        set(PETITION_STATUS, value);
    }

}
