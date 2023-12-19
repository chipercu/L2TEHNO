package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterRecipebookResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_recipebook",
        primary_key = {CHAR_ID,ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ID , defValue = "0"),
        }
)
public class CharacterRecipebookResource extends DataBaseTable<CharacterRecipebookResource> {

    public static final String CHAR_ID = "char_id";
    public static final String ID = "id";

    public CharacterRecipebookResource() {
        super(CharacterRecipebookResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getId() {
        return get(ID);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }

}
