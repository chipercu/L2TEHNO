package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterVoteResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_vote",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = TYPE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = VOTE_ID , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = _DATE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = NICK , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MULTIPLER , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HAS_REWARD , defValue = "0"),
        }
)
public class CharacterVoteResource extends DataBaseTable<CharacterVoteResource> {

    public static final String TYPE = "type";
    public static final String VOTE_ID = "vote_id";
    public static final String _DATE = "date";
    public static final String ID = "id";
    public static final String NICK = "nick";
    public static final String MULTIPLER = "multipler";
    public static final String HAS_REWARD = "has_reward";

    public CharacterVoteResource() {
        super(CharacterVoteResource.class);
}

    public Integer getType() {
        return get(TYPE);
    }
    public Integer getVoteId() {
        return get(VOTE_ID);
    }
    public Long getDate() {
        return get(_DATE);
    }
    public Integer getId() {
        return get(ID);
    }
    public String getNick() {
        return get(NICK);
    }
    public Integer getMultipler() {
        return get(MULTIPLER);
    }
    public Integer getHasReward() {
        return get(HAS_REWARD);
    }

    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setVoteId(Integer value) {
        set(VOTE_ID, value);
    }
    public void setDate(Long value) {
        set(_DATE, value);
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
