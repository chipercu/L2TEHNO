package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.PostsResource.*;

@Table(
        name = "posts",
        primary_key = {},
        fields = {
                @Field(name = POST_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = POST_OWNER_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = POST_OWNERID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = POST_DATE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = POST_TOPIC_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = POST_FORUM_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = POST_TXT , data_type = "text"),
        }
)
public class PostsResource extends DataBaseTable<PostsResource> {

    public static final String POST_ID = "post_id";
    public static final String POST_OWNER_NAME = "post_owner_name";
    public static final String POST_OWNERID = "post_ownerid";
    public static final String POST_DATE = "post_date";
    public static final String POST_TOPIC_ID = "post_topic_id";
    public static final String POST_FORUM_ID = "post_forum_id";
    public static final String POST_TXT = "post_txt";

    public PostsResource() {
super(PostsResource.class);
}

    public Integer getPostId() {
        return get(POST_ID, Integer.class);
    }
    public String getPostOwnerName() {
        return get(POST_OWNER_NAME, String.class);
    }
    public Integer getPostOwnerid() {
        return get(POST_OWNERID, Integer.class);
    }
    public Long getPostDate() {
        return get(POST_DATE, Long.class);
    }
    public Integer getPostTopicId() {
        return get(POST_TOPIC_ID, Integer.class);
    }
    public Integer getPostForumId() {
        return get(POST_FORUM_ID, Integer.class);
    }
    public String getPostTxt() {
        return get(POST_TXT, String.class);
    }

    public void setPostId(Integer value) {
        set(POST_ID, value);
    }
    public void setPostOwnerName(String value) {
        set(POST_OWNER_NAME, value);
    }
    public void setPostOwnerid(Integer value) {
        set(POST_OWNERID, value);
    }
    public void setPostDate(Long value) {
        set(POST_DATE, value);
    }
    public void setPostTopicId(Integer value) {
        set(POST_TOPIC_ID, value);
    }
    public void setPostForumId(Integer value) {
        set(POST_FORUM_ID, value);
    }
    public void setPostTxt(String value) {
        set(POST_TXT, value);
    }

}
