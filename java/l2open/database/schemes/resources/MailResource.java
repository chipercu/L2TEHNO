package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.MailResource.*;

@Table(
        name = "mail",
        primary_key = {MESSAGE_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MESSAGE_ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = SENDER , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = RECEIVER , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TIMESTAMP , type_size = 0 , name = EXPIRE , defValue = "1980-01-01 00:00:00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 30 , name = TOPIC , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = BODY , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ATTACHMENTS , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = NEEDS_PAYMENT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = PRICE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = SYSTEM , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = UNREAD , defValue = "1"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = HIDE_SENDER , defValue = "0"),
        }
)
public class MailResource extends DataBaseTable<MailResource> {

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

    public MailResource() {
        super(MailResource.class);
}

    public Integer getMessageId() {
        return get(MESSAGE_ID);
    }
    public Integer getSender() {
        return get(SENDER);
    }
    public Integer getReceiver() {
        return get(RECEIVER);
    }
    public String getExpire() {
        return get(EXPIRE);
    }
    public String getTopic() {
        return get(TOPIC);
    }
    public String getBody() {
        return get(BODY);
    }
    public Integer getAttachments() {
        return get(ATTACHMENTS);
    }
    public Integer getNeedsPayment() {
        return get(NEEDS_PAYMENT);
    }
    public Long getPrice() {
        return get(PRICE);
    }
    public Integer getSystem() {
        return get(SYSTEM);
    }
    public Integer getUnread() {
        return get(UNREAD);
    }
    public Integer getHideSender() {
        return get(HIDE_SENDER);
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
    public void setExpire(String value) {
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
