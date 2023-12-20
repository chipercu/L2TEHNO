package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AutoChatResource;

public class AutoChatBuilder extends ResourceBuilder<AutoChatResource> {

    public AutoChatBuilder() {
        try {
            resourceClass = AutoChatResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AutoChatBuilder withGroupId(Integer value) {
        with(AutoChatResource.GROUP_ID, value);
        return this;
    }

    public AutoChatBuilder withNpcId(Integer value) {
        with(AutoChatResource.NPC_ID, value);
        return this;
    }

    public AutoChatBuilder withChatDelay(Integer value) {
        with(AutoChatResource.CHAT_DELAY, value);
        return this;
    }

}
