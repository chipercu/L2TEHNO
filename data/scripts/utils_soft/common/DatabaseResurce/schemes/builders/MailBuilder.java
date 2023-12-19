package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.MailResource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

public class MailBuilder extends ResourceBuilder<MailResource> {

    public MailBuilder() {
        try {
            resourceClass = MailResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MailBuilder withMessageId(Integer value) {
        with(MailResource.MESSAGE_ID, value);
        return this;
    }

    public MailBuilder withSender(Integer value) {
        with(MailResource.SENDER, value);
        return this;
    }

    public MailBuilder withReceiver(Integer value) {
        with(MailResource.RECEIVER, value);
        return this;
    }

    public MailBuilder withExpire(Timestamp value) {
        with(MailResource.EXPIRE, value);
        return this;
    }

    public MailBuilder withTopic(String value) {
        with(MailResource.TOPIC, value);
        return this;
    }

    public MailBuilder withBody(String value) {
        with(MailResource.BODY, value);
        return this;
    }

    public MailBuilder withAttachments(Integer value) {
        with(MailResource.ATTACHMENTS, value);
        return this;
    }

    public MailBuilder withNeedsPayment(Integer value) {
        with(MailResource.NEEDS_PAYMENT, value);
        return this;
    }

    public MailBuilder withPrice(Long value) {
        with(MailResource.PRICE, value);
        return this;
    }

    public MailBuilder withSystem(Integer value) {
        with(MailResource.SYSTEM, value);
        return this;
    }

    public MailBuilder withUnread(Integer value) {
        with(MailResource.UNREAD, value);
        return this;
    }

    public MailBuilder withHideSender(Integer value) {
        with(MailResource.HIDE_SENDER, value);
        return this;
    }

}
