package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.MailAttachmentsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "mail_attachments",
        primary_key = {MESSAGE_ID,ITEM_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = MESSAGE_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEM_ID , defValue = "null"),
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
