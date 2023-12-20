package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.MailAttachmentsResource;

public class MailAttachmentsBuilder extends ResourceBuilder<MailAttachmentsResource> {

    public MailAttachmentsBuilder() {
        try {
            resourceClass = MailAttachmentsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MailAttachmentsBuilder withMessageId(Integer value) {
        with(MailAttachmentsResource.MESSAGE_ID, value);
        return this;
    }

    public MailAttachmentsBuilder withItemId(Integer value) {
        with(MailAttachmentsResource.ITEM_ID, value);
        return this;
    }

}
