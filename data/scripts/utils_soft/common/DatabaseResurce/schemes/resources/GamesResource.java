package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.GamesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "games",
        primary_key = {ID,IDNR},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = IDNR , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NUMBER1 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NUMBER2 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PRIZE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = NEWPRIZE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PRIZE1 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PRIZE2 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = PRIZE3 , defValue = "0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = ENDDATE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FINISHED , defValue = "0"),
        }
)
public class GamesResource extends DataBaseTable<GamesResource> {

    public static final String ID = "id";
    public static final String IDNR = "idnr";
    public static final String NUMBER1 = "number1";
    public static final String NUMBER2 = "number2";
    public static final String PRIZE = "prize";
    public static final String NEWPRIZE = "newprize";
    public static final String PRIZE1 = "prize1";
    public static final String PRIZE2 = "prize2";
    public static final String PRIZE3 = "prize3";
    public static final String ENDDATE = "enddate";
    public static final String FINISHED = "finished";

    public GamesResource() {
        super(GamesResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getIdnr() {
        return get(IDNR);
    }
    public Integer getNumber1() {
        return get(NUMBER1);
    }
    public Integer getNumber2() {
        return get(NUMBER2);
    }
    public Long getPrize() {
        return get(PRIZE);
    }
    public Long getNewprize() {
        return get(NEWPRIZE);
    }
    public Long getPrize1() {
        return get(PRIZE1);
    }
    public Long getPrize2() {
        return get(PRIZE2);
    }
    public Long getPrize3() {
        return get(PRIZE3);
    }
    public Double getEnddate() {
        return get(ENDDATE);
    }
    public Integer getFinished() {
        return get(FINISHED);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setIdnr(Integer value) {
        set(IDNR, value);
    }
    public void setNumber1(Integer value) {
        set(NUMBER1, value);
    }
    public void setNumber2(Integer value) {
        set(NUMBER2, value);
    }
    public void setPrize(Long value) {
        set(PRIZE, value);
    }
    public void setNewprize(Long value) {
        set(NEWPRIZE, value);
    }
    public void setPrize1(Long value) {
        set(PRIZE1, value);
    }
    public void setPrize2(Long value) {
        set(PRIZE2, value);
    }
    public void setPrize3(Long value) {
        set(PRIZE3, value);
    }
    public void setEnddate(Double value) {
        set(ENDDATE, value);
    }
    public void setFinished(Integer value) {
        set(FINISHED, value);
    }

}
