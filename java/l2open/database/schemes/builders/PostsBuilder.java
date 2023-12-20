package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.PostsResource;

public class PostsBuilder extends ResourceBuilder<PostsResource> {

    public PostsBuilder() {
        try {
            resourceClass = PostsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public PostsBuilder withPostId(Integer value) {
        with(PostsResource.POST_ID, value);
        return this;
    }

    public PostsBuilder withPostOwnerName(String value) {
        with(PostsResource.POST_OWNER_NAME, value);
        return this;
    }

    public PostsBuilder withPostOwnerid(Integer value) {
        with(PostsResource.POST_OWNERID, value);
        return this;
    }

    public PostsBuilder withPostDate(Long value) {
        with(PostsResource.POST_DATE, value);
        return this;
    }

    public PostsBuilder withPostTopicId(Integer value) {
        with(PostsResource.POST_TOPIC_ID, value);
        return this;
    }

    public PostsBuilder withPostForumId(Integer value) {
        with(PostsResource.POST_FORUM_ID, value);
        return this;
    }

    public PostsBuilder withPostTxt(String value) {
        with(PostsResource.POST_TXT, value);
        return this;
    }

}
