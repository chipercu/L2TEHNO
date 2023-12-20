package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AutoChatTextResource;

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
