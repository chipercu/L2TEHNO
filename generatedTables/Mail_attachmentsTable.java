package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Mail_attachmentsTable.*;

@Table(
        name = "mail_attachments",
        fields = {
                @Field(name = MESSAGEID,
                @Field(name = ITEMID,
        }
)
public class Mail_attachmentsTable extends DataBaseTable<Mail_attachmentsTable> {

    public static final String MESSAGEID = "messageId";
    public static final String ITEMID = "itemId";
    public Mail_attachmentsTable() {
super(Mail_attachmentsTable.class);
}
    public IntegergetMessageId() {
        return get(MESSAGEID, Integer.class);
    }
    public IntegergetItemId() {
        return get(ITEMID, Integer.class);
    }

    public void setMessageId() {
        set(MESSAGEID, value);
    }
    public void setItemId() {
        set(ITEMID, value);
    }

}
