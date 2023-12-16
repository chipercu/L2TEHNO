package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterMailTable.*;

@Table(
        name = "character_mail",
        fields = {
                @Field(name = OBJ_ID),
                @Field(name = LETTER_ID),
                @Field(name = SENDER_ID),
                @Field(name = LOCATION),
                @Field(name = RECIPIENT_NAMES),
                @Field(name = SUBJECT),
                @Field(name = MESSAGE),
                @Field(name = SEND_DATE),
                @Field(name = DELETE_DATE),
                @Field(name = UNREAD),
        }
)
public class CharacterMailTable extends DataBaseTable<CharacterMailTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String LETTER_ID = "letterId";
    public static final String SENDER_ID = "senderId";
    public static final String LOCATION = "location";
    public static final String RECIPIENT_NAMES = "recipientNames";
    public static final String SUBJECT = "subject";
    public static final String MESSAGE = "message";
    public static final String SEND_DATE = "sendDate";
    public static final String DELETE_DATE = "deleteDate";
    public static final String UNREAD = "unread";

    public CharacterMailTable() {
super(CharacterMailTable.class);
}

    public Integer getObjId() {
        return get(OBJ_ID, Integer.class);
    }
    public Integer getLetterId() {
        return get(LETTER_ID, Integer.class);
    }
    public Integer getSenderId() {
        return get(SENDER_ID, Integer.class);
    }
    public String getLocation() {
        return get(LOCATION, String.class);
    }
    public String getRecipientNames() {
        return get(RECIPIENT_NAMES, String.class);
    }
    public String getSubject() {
        return get(SUBJECT, String.class);
    }
    public String getMessage() {
        return get(MESSAGE, String.class);
    }
    public Double getSendDate() {
        return get(SEND_DATE, Double.class);
    }
    public Double getDeleteDate() {
        return get(DELETE_DATE, Double.class);
    }
    public String getUnread() {
        return get(UNREAD, String.class);
    }

    public void setObjId(Integer value) {
        set(OBJ_ID, value);
    }
    public void setLetterId(Integer value) {
        set(LETTER_ID, value);
    }
    public void setSenderId(Integer value) {
        set(SENDER_ID, value);
    }
    public void setLocation(String value) {
        set(LOCATION, value);
    }
    public void setRecipientNames(String value) {
        set(RECIPIENT_NAMES, value);
    }
    public void setSubject(String value) {
        set(SUBJECT, value);
    }
    public void setMessage(String value) {
        set(MESSAGE, value);
    }
    public void setSendDate(Double value) {
        set(SEND_DATE, value);
    }
    public void setDeleteDate(Double value) {
        set(DELETE_DATE, value);
    }
    public void setUnread(String value) {
        set(UNREAD, value);
    }

}
