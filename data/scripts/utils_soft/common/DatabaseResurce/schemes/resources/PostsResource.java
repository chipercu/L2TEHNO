package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.PostsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "posts",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = POST_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = POST_OWNER_NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POST_OWNERID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = POST_DATE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POST_TOPIC_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POST_FORUM_ID , defValue = "0"),
                @Column(is_null = NO , type = TEXT , type_size = 65535 , name = POST_TXT , defValue = "null"),
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
        return get(POST_ID);
    }
    public String getPostOwnerName() {
        return get(POST_OWNER_NAME);
    }
    public Integer getPostOwnerid() {
        return get(POST_OWNERID);
    }
    public Long getPostDate() {
        return get(POST_DATE);
    }
    public Integer getPostTopicId() {
        return get(POST_TOPIC_ID);
    }
    public Integer getPostForumId() {
        return get(POST_FORUM_ID);
    }
    public String getPostTxt() {
        return get(POST_TXT);
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
