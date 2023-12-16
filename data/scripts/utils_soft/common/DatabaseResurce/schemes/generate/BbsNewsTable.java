package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.BbsNewsTable.*;

@Table(
        name = "bbs_news",
        fields = {
                @Field(name = ID),
                @Field(name = TYPE),
                @Field(name = TITLE_RU),
                @Field(name = TITLE_EN),
                @Field(name = TEXT_RU),
                @Field(name = TEXT_EN),
                @Field(name = INFO_RU),
                @Field(name = INFO_EN),
                @Field(name = AUTHOR),
                @Field(name = DATE),
        }
)
public class BbsNewsTable extends DataBaseTable<BbsNewsTable> {

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

    public BbsNewsTable() {
super(BbsNewsTable.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public String getTitleRu() {
        return get(TITLE_RU, String.class);
    }
    public String getTitleEn() {
        return get(TITLE_EN, String.class);
    }
    public String getTextRu() {
        return get(TEXT_RU, String.class);
    }
    public String getTextEn() {
        return get(TEXT_EN, String.class);
    }
    public String getInfoRu() {
        return get(INFO_RU, String.class);
    }
    public String getInfoEn() {
        return get(INFO_EN, String.class);
    }
    public String getAuthor() {
        return get(AUTHOR, String.class);
    }
    public String getDate() {
        return get(DATE, String.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setTitleRu(String value) {
        set(TITLE_RU, value);
    }
    public void setTitleEn(String value) {
        set(TITLE_EN, value);
    }
    public void setTextRu(String value) {
        set(TEXT_RU, value);
    }
    public void setTextEn(String value) {
        set(TEXT_EN, value);
    }
    public void setInfoRu(String value) {
        set(INFO_RU, value);
    }
    public void setInfoEn(String value) {
        set(INFO_EN, value);
    }
    public void setAuthor(String value) {
        set(AUTHOR, value);
    }
    public void setDate(String value) {
        set(DATE, value);
    }

}
