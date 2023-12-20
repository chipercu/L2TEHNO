package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.MailAttachmentsResource.*;

@Table(
        name = "mail_attachments",
        primary_key = {MESSAGE_ID,ITEM_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MESSAGE_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ITEM_ID , defValue = "null"),
        }
)
public class MailAttachmentsResource extends DataBaseTable<MailAttachmentsResource> {

    public static final String MESSAGE_ID = "messageId";
    public static final String ITEM_ID = "itemId";

    public MailAttachmentsResource() {
        super(MailAttachmentsResource.class);
}

    public Integer getMessageId() {
        return get(MESSAGE_ID);
    }
    public Integer getItemId() {
        return get(ITEM_ID);
    }

    public void setMessageId(Integer value) {
        set(MESSAGE_ID, value);
    }
    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }

}
