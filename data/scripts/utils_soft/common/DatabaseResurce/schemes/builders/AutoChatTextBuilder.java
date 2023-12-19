package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.AutoChatTextResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class AutoChatTextBuilder extends ResourceBuilder<AutoChatTextResource> {

    public AutoChatTextBuilder() {
        try {
            resourceClass = AutoChatTextResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AutoChatTextBuilder withGroupId(Integer value) {
        with(AutoChatTextResource.GROUP_ID, value);
        return this;
    }

    public AutoChatTextBuilder withChatText(String value) {
        with(AutoChatTextResource.CHAT_TEXT, value);
        return this;
    }

}
