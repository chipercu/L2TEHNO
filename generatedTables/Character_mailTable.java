package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_mailTable.*;

@Table(
        name = "character_mail",
        fields = {
                @Field(name = OBJ_ID,
                @Field(name = LETTERID,
                @Field(name = SENDERID,
                @Field(name = LOCATION,
                @Field(name = RECIPIENTNAMES,
                @Field(name = SUBJECT,
                @Field(name = MESSAGE,
                @Field(name = SENDDATE,
                @Field(name = DELETEDATE,
                @Field(name = UNREAD,
        }
)
public class Character_mailTable extends DataBaseTable<Character_mailTable> {

    public static final String OBJ_ID = "obj_id";
    public static final String LETTERID = "letterId";
    public static final String SENDERID = "senderId";
    public static final String LOCATION = "location";
    public static final String RECIPIENTNAMES = "recipientNames";
    public static final String SUBJECT = "subject";
    public static final String MESSAGE = "message";
    public static final String SENDDATE = "sendDate";
    public static final String DELETEDATE = "deleteDate";
    public static final String UNREAD = "unread";
    public Character_mailTable() {
super(Character_mailTable.class);
}
    public IntegergetObj_id() {
        return get(OBJ_ID, Integer.class);
    }
    public IntegergetLetterId() {
        return get(LETTERID, Integer.class);
    }
    public IntegergetSenderId() {
        return get(SENDERID, Integer.class);
    }
    public StringgetLocation() {
        return get(LOCATION, String.class);
    }
    public StringgetRecipientNames() {
        return get(RECIPIENTNAMES, String.class);
    }
    public StringgetSubject() {
        return get(SUBJECT, String.class);
    }
    public StringgetMessage() {
        return get(MESSAGE, String.class);
    }
    public DoublegetSendDate() {
        return get(SENDDATE, Double.class);
    }
    public DoublegetDeleteDate() {
        return get(DELETEDATE, Double.class);
    }
    public StringgetUnread() {
        return get(UNREAD, String.class);
    }

    public void setObj_id() {
        set(OBJ_ID, value);
    }
    public void setLetterId() {
        set(LETTERID, value);
    }
    public void setSenderId() {
        set(SENDERID, value);
    }
    public void setLocation() {
        set(LOCATION, value);
    }
    public void setRecipientNames() {
        set(RECIPIENTNAMES, value);
    }
    public void setSubject() {
        set(SUBJECT, value);
    }
    public void setMessage() {
        set(MESSAGE, value);
    }
    public void setSendDate() {
        set(SENDDATE, value);
    }
    public void setDeleteDate() {
        set(DELETEDATE, value);
    }
    public void setUnread() {
        set(UNREAD, value);
    }

}
