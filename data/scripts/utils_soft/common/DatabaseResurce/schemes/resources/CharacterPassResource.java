package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterPassResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_pass",
        primary_key = {LOGIN,OBJ_ID},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = LOGIN , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = QUESTION , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = ANSWER , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = PASSWORD , defValue = ""),
        }
)
public class CharacterPassResource extends DataBaseTable<CharacterPassResource> {

    public static final String LOGIN = "login";
    public static final String OBJ_ID = "obj_id";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String PASSWORD = "password";

    public CharacterPassResource() {
        super(CharacterPassResource.class);
}

    public String getLogin() {
        return get(LOGIN);
    }
    public Integer getObjId() {
        return get(OBJ_ID);
    }
    public String getQuestion() {
        return get(QUESTION);
    }
    public String getAnswer() {
        return get(ANSWER);
    }
    public String getPassword() {
        return get(PASSWORD);
    }

    public void setLogin(String value) {
        set(LOGIN, value);
    }
    public void setObjId(Integer value) {
        set(OBJ_ID, value);
    }
    public void setQuestion(String value) {
        set(QUESTION, value);
    }
    public void setAnswer(String value) {
        set(ANSWER, value);
    }
    public void setPassword(String value) {
        set(PASSWORD, value);
    }

}
