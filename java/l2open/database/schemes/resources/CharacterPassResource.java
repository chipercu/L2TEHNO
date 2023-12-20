package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterPassResource.*;

@Table(
        name = "character_pass",
        primary_key = {LOGIN,OBJ_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = LOGIN , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = OBJ_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = QUESTION , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = ANSWER , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = PASSWORD , defValue = ""),
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
