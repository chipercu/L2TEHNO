package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.TopicResource;

public class TopicBuilder extends ResourceBuilder<TopicResource> {

    public TopicBuilder() {
        try {
            resourceClass = TopicResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TopicBuilder withTopicId(Integer value) {
        with(TopicResource.TOPIC_ID, value);
        return this;
    }

    public TopicBuilder withTopicForumId(Integer value) {
        with(TopicResource.TOPIC_FORUM_ID, value);
        return this;
    }

    public TopicBuilder withTopicName(String value) {
        with(TopicResource.TOPIC_NAME, value);
        return this;
    }

    public TopicBuilder withTopicDate(Long value) {
        with(TopicResource.TOPIC_DATE, value);
        return this;
    }

    public TopicBuilder withTopicOwnername(String value) {
        with(TopicResource.TOPIC_OWNERNAME, value);
        return this;
    }

    public TopicBuilder withTopicOwnerid(Integer value) {
        with(TopicResource.TOPIC_OWNERID, value);
        return this;
    }

    public TopicBuilder withTopicType(Integer value) {
        with(TopicResource.TOPIC_TYPE, value);
        return this;
    }

    public TopicBuilder withTopicReply(Integer value) {
        with(TopicResource.TOPIC_REPLY, value);
        return this;
    }

}
