package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.TopicResource.*;

@Table(
        name = "topic",
        primary_key = {},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TOPIC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TOPIC_FORUM_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = TOPIC_NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.BIGINT , type_size = 0 , name = TOPIC_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = TOPIC_OWNERNAME , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TOPIC_OWNERID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TOPIC_TYPE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = TOPIC_REPLY , defValue = "0"),
        }
)
public class TopicResource extends DataBaseTable<TopicResource> {

    public static final String TOPIC_ID = "topic_id";
    public static final String TOPIC_FORUM_ID = "topic_forum_id";
    public static final String TOPIC_NAME = "topic_name";
    public static final String TOPIC_DATE = "topic_date";
    public static final String TOPIC_OWNERNAME = "topic_ownername";
    public static final String TOPIC_OWNERID = "topic_ownerid";
    public static final String TOPIC_TYPE = "topic_type";
    public static final String TOPIC_REPLY = "topic_reply";

    public TopicResource() {
        super(TopicResource.class);
}

    public Integer getTopicId() {
        return get(TOPIC_ID);
    }
    public Integer getTopicForumId() {
        return get(TOPIC_FORUM_ID);
    }
    public String getTopicName() {
        return get(TOPIC_NAME);
    }
    public Long getTopicDate() {
        return get(TOPIC_DATE);
    }
    public String getTopicOwnername() {
        return get(TOPIC_OWNERNAME);
    }
    public Integer getTopicOwnerid() {
        return get(TOPIC_OWNERID);
    }
    public Integer getTopicType() {
        return get(TOPIC_TYPE);
    }
    public Integer getTopicReply() {
        return get(TOPIC_REPLY);
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
