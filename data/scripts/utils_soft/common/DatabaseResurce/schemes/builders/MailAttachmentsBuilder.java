package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.MailAttachmentsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
