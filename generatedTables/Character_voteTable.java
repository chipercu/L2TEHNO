package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_voteTable.*;

@Table(
        name = "character_vote",
        fields = {
                @Field(name = TYPE,
                @Field(name = VOTE_ID,
                @Field(name = DATE,
                @Field(name = ID,
                @Field(name = NICK,
                @Field(name = MULTIPLER,
                @Field(name = HAS_REWARD,
        }
)
public class Character_voteTable extends DataBaseTable<Character_voteTable> {

    public static final String TYPE = "type";
    public static final String VOTE_ID = "vote_id";
    public static final String DATE = "date";
    public static final String ID = "id";
    public static final String NICK = "nick";
    public static final String MULTIPLER = "multipler";
    public static final String HAS_REWARD = "has_reward";
    public Character_voteTable() {
super(Character_voteTable.class);
}
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }
    public IntegergetVote_id() {
        return get(VOTE_ID, Integer.class);
    }
    public ObjectgetDate() {
        return get(DATE, Object.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetNick() {
        return get(NICK, String.class);
    }
    public IntegergetMultipler() {
        return get(MULTIPLER, Integer.class);
    }
    public IntegergetHas_reward() {
        return get(HAS_REWARD, Integer.class);
    }

    public void setType() {
        set(TYPE, value);
    }
    public void setVote_id() {
        set(VOTE_ID, value);
    }
    public void setDate() {
        set(DATE, value);
    }
    public void setId() {
        set(ID, value);
    }
    public void setNick() {
        set(NICK, value);
    }
    public void setMultipler() {
        set(MULTIPLER, value);
    }
    public void setHas_reward() {
        set(HAS_REWARD, value);
    }

}
