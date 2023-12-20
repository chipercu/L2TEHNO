package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.BbsNewsResource.*;

@Table(
        name = "bbs_news",
        primary_key = {ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = TYPE , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = TITLE_RU , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = TITLE_EN , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = TEXT_RU , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TEXT , type_size = 65535 , name = TEXT_EN , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = INFO_RU , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = INFO_EN , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = AUTHOR , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DATE , type_size = 0 , name = _DATE , defValue = "null"),
        }
)
public class BbsNewsResource extends DataBaseTable<BbsNewsResource> {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String TITLE_RU = "title_ru";
    public static final String TITLE_EN = "title_en";
    public static final String TEXT_RU = "text_ru";
    public static final String TEXT_EN = "text_en";
    public static final String INFO_RU = "info_ru";
    public static final String INFO_EN = "info_en";
    public static final String AUTHOR = "author";
    public static final String _DATE = "date";

    public BbsNewsResource() {
        super(BbsNewsResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getType() {
        return get(TYPE);
    }
    public String getTitleRu() {
        return get(TITLE_RU);
    }
    public String getTitleEn() {
        return get(TITLE_EN);
    }
    public String getTextRu() {
        return get(TEXT_RU);
    }
    public String getTextEn() {
        return get(TEXT_EN);
    }
    public String getInfoRu() {
        return get(INFO_RU);
    }
    public String getInfoEn() {
        return get(INFO_EN);
    }
    public String getAuthor() {
        return get(AUTHOR);
    }
    public String getDate() {
        return get(_DATE);
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
        set(_DATE, value);
    }

}
