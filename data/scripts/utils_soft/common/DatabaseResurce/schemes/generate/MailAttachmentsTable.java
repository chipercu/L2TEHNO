package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MailAttachmentsTable.*;

@Table(
        name = "mail_attachments",
        fields = {
                @Field(name = MESSAGE_ID),
                @Field(name = ITEM_ID),
        }
)
public class MailAttachmentsTable extends DataBaseTable<MailAttachmentsTable> {

    public static final String MESSAGE_ID = "messageId";
    public static final String ITEM_ID = "itemId";

    public MailAttachmentsTable() {
super(MailAttachmentsTable.class);
}

    public Integer getMessageId() {
        return get(MESSAGE_ID, Integer.class);
    }
    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }

    public void setMessageId(Integer value) {
        set(MESSAGE_ID, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }

}
