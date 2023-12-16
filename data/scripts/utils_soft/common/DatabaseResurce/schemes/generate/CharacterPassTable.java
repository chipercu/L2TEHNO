package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterPassTable.*;

@Table(
        name = "character_pass",
        fields = {
                @Field(name = LOGIN),
                @Field(name = OBJ_ID),
                @Field(name = QUESTION),
                @Field(name = ANSWER),
                @Field(name = PASSWORD),
        }
)
public class CharacterPassTable extends DataBaseTable<CharacterPassTable> {

    public static final String LOGIN = "login";
    public static final String OBJ_ID = "obj_id";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String PASSWORD = "password";

    public CharacterPassTable() {
super(CharacterPassTable.class);
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
