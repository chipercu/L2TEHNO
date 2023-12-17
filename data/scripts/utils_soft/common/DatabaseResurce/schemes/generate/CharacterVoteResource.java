package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterVoteResource.*;

@Table(
        name = "character_vote",
        primary_key = {},
        fields = {
                @Field(name = TYPE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = VOTE_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DATE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NICK , data_type = "varchar" , defValue = @DefValue(String = "''''")),
                @Field(name = MULTIPLER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = HAS_REWARD , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class CharacterVoteResource extends DataBaseTable<CharacterVoteResource> {

    public static final String TYPE = "type";
    public static final String VOTE_ID = "vote_id";
    public static final String DATE = "date";
    public static final String ID = "id";
    public static final String NICK = "nick";
    public static final String MULTIPLER = "multipler";
    public static final String HAS_REWARD = "has_reward";

    public CharacterVoteResource() {
super(CharacterVoteResource.class);
}

    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public Integer getVoteId() {
        return get(VOTE_ID, Integer.class);
    }
    public Long getDate() {
        return get(DATE, Long.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getNick() {
        return get(NICK, String.class);
    }
    public Integer getMultipler() {
        return get(MULTIPLER, Integer.class);
    }
    public Integer getHasReward() {
        return get(HAS_REWARD, Integer.class);
    }

    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setVoteId(Integer value) {
        set(VOTE_ID, value);
    }
    public void setDate(Long value) {
        set(DATE, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }
    public void setNick(String value) {
        set(NICK, value);
    }
    public void setMultipler(Integer value) {
        set(MULTIPLER, value);
    }
    public void setHasReward(Integer value) {
        set(HAS_REWARD, value);
    }

}
