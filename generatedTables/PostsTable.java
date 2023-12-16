package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.PostsTable.*;

@Table(
        name = "posts",
        fields = {
                @Field(name = POST_ID,
                @Field(name = POST_OWNER_NAME,
                @Field(name = POST_OWNERID,
                @Field(name = POST_DATE,
                @Field(name = POST_TOPIC_ID,
                @Field(name = POST_FORUM_ID,
                @Field(name = POST_TXT,
        }
)
public class PostsTable extends DataBaseTable<PostsTable> {

    public static final String POST_ID = "post_id";
    public static final String POST_OWNER_NAME = "post_owner_name";
    public static final String POST_OWNERID = "post_ownerid";
    public static final String POST_DATE = "post_date";
    public static final String POST_TOPIC_ID = "post_topic_id";
    public static final String POST_FORUM_ID = "post_forum_id";
    public static final String POST_TXT = "post_txt";
    public PostsTable() {
super(PostsTable.class);
}
    public IntegergetPost_id() {
        return get(POST_ID, Integer.class);
    }
    public StringgetPost_owner_name() {
        return get(POST_OWNER_NAME, String.class);
    }
    public IntegergetPost_ownerid() {
        return get(POST_OWNERID, Integer.class);
    }
    public ObjectgetPost_date() {
        return get(POST_DATE, Object.class);
    }
    public IntegergetPost_topic_id() {
        return get(POST_TOPIC_ID, Integer.class);
    }
    public IntegergetPost_forum_id() {
        return get(POST_FORUM_ID, Integer.class);
    }
    public StringgetPost_txt() {
        return get(POST_TXT, String.class);
    }

    public void setPost_id() {
        set(POST_ID, value);
    }
    public void setPost_owner_name() {
        set(POST_OWNER_NAME, value);
    }
    public void setPost_ownerid() {
        set(POST_OWNERID, value);
    }
    public void setPost_date() {
        set(POST_DATE, value);
    }
    public void setPost_topic_id() {
        set(POST_TOPIC_ID, value);
    }
    public void setPost_forum_id() {
        set(POST_FORUM_ID, value);
    }
    public void setPost_txt() {
        set(POST_TXT, value);
    }

}
