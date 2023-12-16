package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.TopicTable.*;

@Table(
        name = "topic",
        fields = {
                @Field(name = TOPIC_ID),
                @Field(name = TOPIC_FORUM_ID),
                @Field(name = TOPIC_NAME),
                @Field(name = TOPIC_DATE),
                @Field(name = TOPIC_OWNERNAME),
                @Field(name = TOPIC_OWNERID),
                @Field(name = TOPIC_TYPE),
                @Field(name = TOPIC_REPLY),
        }
)
public class TopicTable extends DataBaseTable<TopicTable> {

    public static final String TOPIC_ID = "topic_id";
    public static final String TOPIC_FORUM_ID = "topic_forum_id";
    public static final String TOPIC_NAME = "topic_name";
    public static final String TOPIC_DATE = "topic_date";
    public static final String TOPIC_OWNERNAME = "topic_ownername";
    public static final String TOPIC_OWNERID = "topic_ownerid";
    public static final String TOPIC_TYPE = "topic_type";
    public static final String TOPIC_REPLY = "topic_reply";

    public TopicTable() {
super(TopicTable.class);
}

    public Integer getTopicId() {
        return get(TOPIC_ID, Integer.class);
    }
    public Integer getTopicForumId() {
        return get(TOPIC_FORUM_ID, Integer.class);
    }
    public String getTopicName() {
        return get(TOPIC_NAME, String.class);
    }
    public Long getTopicDate() {
        return get(TOPIC_DATE, Long.class);
    }
    public String getTopicOwnername() {
        return get(TOPIC_OWNERNAME, String.class);
    }
    public Integer getTopicOwnerid() {
        return get(TOPIC_OWNERID, Integer.class);
    }
    public Integer getTopicType() {
        return get(TOPIC_TYPE, Integer.class);
    }
    public Integer getTopicReply() {
        return get(TOPIC_REPLY, Integer.class);
    }

    public void setTopicId(Integer value) {
        set(TOPIC_ID, value);
    }
    public void setTopicForumId(Integer value) {
        set(TOPIC_FORUM_ID, value);
    }
    public void setTopicName(String value) {
        set(TOPIC_NAME, value);
    }
    public void setTopicDate(Long value) {
        set(TOPIC_DATE, value);
    }
    public void setTopicOwnername(String value) {
        set(TOPIC_OWNERNAME, value);
    }
    public void setTopicOwnerid(Integer value) {
        set(TOPIC_OWNERID, value);
    }
    public void setTopicType(Integer value) {
        set(TOPIC_TYPE, value);
    }
    public void setTopicReply(Integer value) {
        set(TOPIC_REPLY, value);
    }

}
