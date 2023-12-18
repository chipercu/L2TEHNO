package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterPassResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_pass",
        primary_key = {LOGIN,OBJ_ID},
        fields = {
                @Field(name = LOGIN , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = QUESTION , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = ANSWER , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = PASSWORD , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
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


    public CharacterPassResource(String login,String obj_id){
        super(CharacterPassResource.class);
        getSTAT_SET().set(LOGIN, login);
        getSTAT_SET().set(OBJ_ID, obj_id);
    }

    public String getLogin() {
        return get(LOGIN, String.class);
    }
    public Integer getObjId() {
        return get(OBJ_ID, Integer.class);
    }
    public String getQuestion() {
        return get(QUESTION, String.class);
    }
    public String getAnswer() {
        return get(ANSWER, String.class);
    }
    public String getPassword() {
        return get(PASSWORD, String.class);
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
