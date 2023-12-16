package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_passTable.*;

@Table(
        name = "character_pass",
        fields = {
                @Field(name = LOGIN,
                @Field(name = OBJ_ID,
                @Field(name = QUESTION,
                @Field(name = ANSWER,
                @Field(name = PASSWORD,
        }
)
public class Character_passTable extends DataBaseTable<Character_passTable> {

    public static final String LOGIN = "login";
    public static final String OBJ_ID = "obj_id";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String PASSWORD = "password";
    public Character_passTable() {
super(Character_passTable.class);
}
    public StringgetLogin() {
        return get(LOGIN, String.class);
    }
    public IntegergetObj_id() {
        return get(OBJ_ID, Integer.class);
    }
    public StringgetQuestion() {
        return get(QUESTION, String.class);
    }
    public StringgetAnswer() {
        return get(ANSWER, String.class);
    }
    public StringgetPassword() {
        return get(PASSWORD, String.class);
    }

    public void setLogin() {
        set(LOGIN, value);
    }
    public void setObj_id() {
        set(OBJ_ID, value);
    }
    public void setQuestion() {
        set(QUESTION, value);
    }
    public void setAnswer() {
        set(ANSWER, value);
    }
    public void setPassword() {
        set(PASSWORD, value);
    }

}
