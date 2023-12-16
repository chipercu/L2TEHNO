package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Bbs_newsTable.*;

@Table(
        name = "bbs_news",
        fields = {
                @Field(name = ID,
                @Field(name = TYPE,
                @Field(name = TITLE_RU,
                @Field(name = TITLE_EN,
                @Field(name = TEXT_RU,
                @Field(name = TEXT_EN,
                @Field(name = INFO_RU,
                @Field(name = INFO_EN,
                @Field(name = AUTHOR,
                @Field(name = DATE,
        }
)
public class Bbs_newsTable extends DataBaseTable<Bbs_newsTable> {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String TITLE_RU = "title_ru";
    public static final String TITLE_EN = "title_en";
    public static final String TEXT_RU = "text_ru";
    public static final String TEXT_EN = "text_en";
    public static final String INFO_RU = "info_ru";
    public static final String INFO_EN = "info_en";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public Bbs_newsTable() {
super(Bbs_newsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetType() {
        return get(TYPE, Integer.class);
    }
    public StringgetTitle_ru() {
        return get(TITLE_RU, String.class);
    }
    public StringgetTitle_en() {
        return get(TITLE_EN, String.class);
    }
    public StringgetText_ru() {
        return get(TEXT_RU, String.class);
    }
    public StringgetText_en() {
        return get(TEXT_EN, String.class);
    }
    public StringgetInfo_ru() {
        return get(INFO_RU, String.class);
    }
    public StringgetInfo_en() {
        return get(INFO_EN, String.class);
    }
    public StringgetAuthor() {
        return get(AUTHOR, String.class);
    }
    public ObjectgetDate() {
        return get(DATE, Object.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setTitle_ru() {
        set(TITLE_RU, value);
    }
    public void setTitle_en() {
        set(TITLE_EN, value);
    }
    public void setText_ru() {
        set(TEXT_RU, value);
    }
    public void setText_en() {
        set(TEXT_EN, value);
    }
    public void setInfo_ru() {
        set(INFO_RU, value);
    }
    public void setInfo_en() {
        set(INFO_EN, value);
    }
    public void setAuthor() {
        set(AUTHOR, value);
    }
    public void setDate() {
        set(DATE, value);
    }

}
