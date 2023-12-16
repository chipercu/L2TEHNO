package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.MailTable.*;

@Table(
        name = "mail",
        fields = {
                @Field(name = MESSAGEID,
                @Field(name = SENDER,
                @Field(name = RECEIVER,
                @Field(name = EXPIRE,
                @Field(name = TOPIC,
                @Field(name = BODY,
                @Field(name = ATTACHMENTS,
                @Field(name = NEEDSPAYMENT,
                @Field(name = PRICE,
                @Field(name = SYSTEM,
                @Field(name = UNREAD,
                @Field(name = HIDESENDER,
        }
)
public class MailTable extends DataBaseTable<MailTable> {

    public static final String MESSAGEID = "messageId";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String EXPIRE = "expire";
    public static final String TOPIC = "topic";
    public static final String BODY = "body";
    public static final String ATTACHMENTS = "attachments";
    public static final String NEEDSPAYMENT = "needsPayment";
    public static final String PRICE = "price";
    public static final String SYSTEM = "system";
    public static final String UNREAD = "unread";
    public static final String HIDESENDER = "hideSender";
    public MailTable() {
super(MailTable.class);
}
    public IntegergetMessageId() {
        return get(MESSAGEID, Integer.class);
    }
    public IntegergetSender() {
        return get(SENDER, Integer.class);
    }
    public IntegergetReceiver() {
        return get(RECEIVER, Integer.class);
    }
    public ObjectgetExpire() {
        return get(EXPIRE, Object.class);
    }
    public StringgetTopic() {
        return get(TOPIC, String.class);
    }
    public StringgetBody() {
        return get(BODY, String.class);
    }
    public IntegergetAttachments() {
        return get(ATTACHMENTS, Integer.class);
    }
    public IntegergetNeedsPayment() {
        return get(NEEDSPAYMENT, Integer.class);
    }
    public ObjectgetPrice() {
        return get(PRICE, Object.class);
    }
    public IntegergetSystem() {
        return get(SYSTEM, Integer.class);
    }
    public IntegergetUnread() {
        return get(UNREAD, Integer.class);
    }
    public IntegergetHideSender() {
        return get(HIDESENDER, Integer.class);
    }

    public void setMessageId() {
        set(MESSAGEID, value);
    }
    public void setSender() {
        set(SENDER, value);
    }
    public void setReceiver() {
        set(RECEIVER, value);
    }
    public void setExpire() {
        set(EXPIRE, value);
    }
    public void setTopic() {
        set(TOPIC, value);
    }
    public void setBody() {
        set(BODY, value);
    }
    public void setAttachments() {
        set(ATTACHMENTS, value);
    }
    public void setNeedsPayment() {
        set(NEEDSPAYMENT, value);
    }
    public void setPrice() {
        set(PRICE, value);
    }
    public void setSystem() {
        set(SYSTEM, value);
    }
    public void setUnread() {
        set(UNREAD, value);
    }
    public void setHideSender() {
        set(HIDESENDER, value);
    }

}
