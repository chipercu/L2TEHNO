package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.TopicTable.*;

@Table(
        name = "topic",
        fields = {
                @Field(name = TOPIC_ID,
                @Field(name = TOPIC_FORUM_ID,
                @Field(name = TOPIC_NAME,
                @Field(name = TOPIC_DATE,
                @Field(name = TOPIC_OWNERNAME,
                @Field(name = TOPIC_OWNERID,
                @Field(name = TOPIC_TYPE,
                @Field(name = TOPIC_REPLY,
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
    public IntegergetTopic_id() {
        return get(TOPIC_ID, Integer.class);
    }
    public IntegergetTopic_forum_id() {
        return get(TOPIC_FORUM_ID, Integer.class);
    }
    public StringgetTopic_name() {
        return get(TOPIC_NAME, String.class);
    }
    public ObjectgetTopic_date() {
        return get(TOPIC_DATE, Object.class);
    }
    public StringgetTopic_ownername() {
        return get(TOPIC_OWNERNAME, String.class);
    }
    public IntegergetTopic_ownerid() {
        return get(TOPIC_OWNERID, Integer.class);
    }
    public IntegergetTopic_type() {
        return get(TOPIC_TYPE, Integer.class);
    }
    public IntegergetTopic_reply() {
        return get(TOPIC_REPLY, Integer.class);
    }

    public void setTopic_id() {
        set(TOPIC_ID, value);
    }
    public void setTopic_forum_id() {
        set(TOPIC_FORUM_ID, value);
    }
    public void setTopic_name() {
        set(TOPIC_NAME, value);
    }
    public void setTopic_date() {
        set(TOPIC_DATE, value);
    }
    public void setTopic_ownername() {
        set(TOPIC_OWNERNAME, value);
    }
    public void setTopic_ownerid() {
        set(TOPIC_OWNERID, value);
    }
    public void setTopic_type() {
        set(TOPIC_TYPE, value);
    }
    public void setTopic_reply() {
        set(TOPIC_REPLY, value);
    }

}
