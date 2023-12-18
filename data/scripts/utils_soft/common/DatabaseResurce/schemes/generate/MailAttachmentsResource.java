package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MailAttachmentsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "mail_attachments",
        primary_key = {MESSAGE_ID,ITEM_ID},
        fields = {
                @Field(name = MESSAGE_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = ITEM_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
        }
)
public class MailAttachmentsResource extends DataBaseTable<MailAttachmentsResource> {

    public static final String MESSAGE_ID = "messageId";
    public static final String ITEM_ID = "itemId";

    public MailAttachmentsResource() {
super(MailAttachmentsResource.class);
}


    public MailAttachmentsResource(String messageId,String itemId){
        super(MailAttachmentsResource.class);
        getSTAT_SET().set(MESSAGE_ID, messageId);
        getSTAT_SET().set(ITEM_ID, itemId);
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
