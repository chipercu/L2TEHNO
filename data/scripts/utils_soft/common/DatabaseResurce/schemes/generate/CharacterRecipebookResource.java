package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterRecipebookResource.*;

@Table(
        name = "character_recipebook",
        primary_key = {CHAR_ID,ID},
        fields = {
                @Field(name = CHAR_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ID , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
        }
)
public class CharacterRecipebookResource extends DataBaseTable<CharacterRecipebookResource> {

    public static final String CHAR_ID = "char_id";
    public static final String ID = "id";

    public CharacterRecipebookResource() {
super(CharacterRecipebookResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }

}
