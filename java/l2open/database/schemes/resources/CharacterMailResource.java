package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterMailResource.*;

@Table(
        name = "character_mail",
        primary_key = {LETTER_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LETTER_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.INT , type_size = 0 , name = SENDER_ID , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 45 , name = LOCATION , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = RECIPIENT_NAMES , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = SUBJECT , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = MESSAGE , defValue = "null"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.DECIMAL , type_size = 0 , name = SEND_DATE , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.DECIMAL , type_size = 0 , name = DELETE_DATE , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 10 , name = UNREAD , defValue = "NULL"),
        }
)
public class CharacterMailResource extends DataBaseTable<CharacterMailResource> {

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

    public CharacterMailResource() {
        super(CharacterMailResource.class);
}

    public Integer getObjId() {
        return get(OBJ_ID);
    }
    public Integer getLetterId() {
        return get(LETTER_ID);
    }
    public Integer getSenderId() {
        return get(SENDER_ID);
    }
    public String getLocation() {
        return get(LOCATION);
    }
    public String getRecipientNames() {
        return get(RECIPIENT_NAMES);
    }
    public String getSubject() {
        return get(SUBJECT);
    }
    public String getMessage() {
        return get(MESSAGE);
    }
    public Double getSendDate() {
        return get(SEND_DATE);
    }
    public Double getDeleteDate() {
        return get(DELETE_DATE);
    }
    public String getUnread() {
        return get(UNREAD);
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
