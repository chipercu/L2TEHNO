package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.GamesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "games",
        primary_key = {ID,IDNR},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = IDNR , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NUMBER1 , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NUMBER2 , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PRIZE , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = NEWPRIZE , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = PRIZE1 , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = PRIZE2 , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = PRIZE3 , type = BIGINT , nullable = false , defValue = @DefValue(Long = 0)),
                @Field(name = ENDDATE , type = DECIMAL , nullable = false , defValue = @DefValue(Double = 0)),
                @Field(name = FINISHED , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public GamesResource(String id,String idnr){
        super(GamesResource.class);
        getSTAT_SET().set(ID, id);
        getSTAT_SET().set(IDNR, idnr);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getIdnr() {
        return get(IDNR, Integer.class);
    }
    public Integer getNumber1() {
        return get(NUMBER1, Integer.class);
    }
    public Integer getNumber2() {
        return get(NUMBER2, Integer.class);
    }
    public Long getPrize() {
        return get(PRIZE, Long.class);
    }
    public Long getNewprize() {
        return get(NEWPRIZE, Long.class);
    }
    public Long getPrize1() {
        return get(PRIZE1, Long.class);
    }
    public Long getPrize2() {
        return get(PRIZE2, Long.class);
    }
    public Long getPrize3() {
        return get(PRIZE3, Long.class);
    }
    public Double getEnddate() {
        return get(ENDDATE, Double.class);
    }
    public Integer getFinished() {
        return get(FINISHED, Integer.class);
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
