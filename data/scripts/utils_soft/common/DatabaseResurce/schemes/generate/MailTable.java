package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MailTable.*;

@Table(
        name = "mail",
        fields = {
                @Field(name = MESSAGE_ID),
                @Field(name = SENDER),
                @Field(name = RECEIVER),
                @Field(name = EXPIRE),
                @Field(name = TOPIC),
                @Field(name = BODY),
                @Field(name = ATTACHMENTS),
                @Field(name = NEEDS_PAYMENT),
                @Field(name = PRICE),
                @Field(name = SYSTEM),
                @Field(name = UNREAD),
                @Field(name = HIDE_SENDER),
        }
)
public class MailTable extends DataBaseTable<MailTable> {

    public static final String MESSAGE_ID = "messageId";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String EXPIRE = "expire";
    public static final String TOPIC = "topic";
    public static final String BODY = "body";
    public static final String ATTACHMENTS = "attachments";
    public static final String NEEDS_PAYMENT = "needsPayment";
    public static final String PRICE = "price";
    public static final String SYSTEM = "system";
    public static final String UNREAD = "unread";
    public static final String HIDE_SENDER = "hideSender";

    public MailTable() {
super(MailTable.class);
}

    public Integer getMessageId() {
        return get(MESSAGE_ID, Integer.class);
    }
    public Integer getSender() {
        return get(SENDER, Integer.class);
    }
    public Integer getReceiver() {
        return get(RECEIVER, Integer.class);
    }
    public Object getExpire() {
        return get(EXPIRE, Object.class);
    }
    public String getTopic() {
        return get(TOPIC, String.class);
    }
    public String getBody() {
        return get(BODY, String.class);
    }
    public Integer getAttachments() {
        return get(ATTACHMENTS, Integer.class);
    }
    public Integer getNeedsPayment() {
        return get(NEEDS_PAYMENT, Integer.class);
    }
    public Long getPrice() {
        return get(PRICE, Long.class);
    }
    public Integer getSystem() {
        return get(SYSTEM, Integer.class);
    }
    public Integer getUnread() {
        return get(UNREAD, Integer.class);
    }
    public Integer getHideSender() {
        return get(HIDE_SENDER, Integer.class);
    }

    public void setMessageId(Integer value) {
        set(MESSAGE_ID, value);
    }
    public void setSender(Integer value) {
        set(SENDER, value);
    }
    public void setReceiver(Integer value) {
        set(RECEIVER, value);
    }
    public void setExpire(Object value) {
        set(EXPIRE, value);
    }
    public void setTopic(String value) {
        set(TOPIC, value);
    }
    public void setBody(String value) {
        set(BODY, value);
    }
    public void setAttachments(Integer value) {
        set(ATTACHMENTS, value);
    }
    public void setNeedsPayment(Integer value) {
        set(NEEDS_PAYMENT, value);
    }
    public void setPrice(Long value) {
        set(PRICE, value);
    }
    public void setSystem(Integer value) {
        set(SYSTEM, value);
    }
    public void setUnread(Integer value) {
        set(UNREAD, value);
    }
    public void setHideSender(Integer value) {
        set(HIDE_SENDER, value);
    }

}
