package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.PostsResource.*;

@Table(
        name = "posts",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = POST_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = POST_OWNER_NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = POST_OWNERID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = POST_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = POST_TOPIC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = POST_FORUM_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = POST_TXT , defValue = "null"),
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
