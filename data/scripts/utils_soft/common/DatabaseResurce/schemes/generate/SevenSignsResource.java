package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SevenSignsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "seven_signs",
        primary_key = {CHAR_OBJ_ID},
        fields = {
                @Field(name = CHAR_OBJ_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CABAL , type = ENUM , type_size = 8 , nullable = false , defValue = @DefValue(String = "No Cabal")),
                @Field(name = SEAL , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DAWN_RED_STONES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DAWN_GREEN_STONES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DAWN_BLUE_STONES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DAWN_ANCIENT_ADENA_AMOUNT , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DAWN_CONTRIBUTION_SCORE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DUSK_RED_STONES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DUSK_GREEN_STONES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DUSK_BLUE_STONES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DUSK_ANCIENT_ADENA_AMOUNT , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DUSK_CONTRIBUTION_SCORE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class SevenSignsResource extends DataBaseTable<SevenSignsResource> {

    public static final String CHAR_OBJ_ID = "char_obj_id";
    public static final String CABAL = "cabal";
    public static final String SEAL = "seal";
    public static final String DAWN_RED_STONES = "dawn_red_stones";
    public static final String DAWN_GREEN_STONES = "dawn_green_stones";
    public static final String DAWN_BLUE_STONES = "dawn_blue_stones";
    public static final String DAWN_ANCIENT_ADENA_AMOUNT = "dawn_ancient_adena_amount";
    public static final String DAWN_CONTRIBUTION_SCORE = "dawn_contribution_score";
    public static final String DUSK_RED_STONES = "dusk_red_stones";
    public static final String DUSK_GREEN_STONES = "dusk_green_stones";
    public static final String DUSK_BLUE_STONES = "dusk_blue_stones";
    public static final String DUSK_ANCIENT_ADENA_AMOUNT = "dusk_ancient_adena_amount";
    public static final String DUSK_CONTRIBUTION_SCORE = "dusk_contribution_score";

    public SevenSignsResource() {
super(SevenSignsResource.class);
}


    public SevenSignsResource(String char_obj_id){
        super(SevenSignsResource.class);
        getSTAT_SET().set(CHAR_OBJ_ID, char_obj_id);
    }

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public String getCabal() {
        return get(CABAL, String.class);
    }
    public Integer getSeal() {
        return get(SEAL, Integer.class);
    }
    public Integer getDawnRedStones() {
        return get(DAWN_RED_STONES, Integer.class);
    }
    public Integer getDawnGreenStones() {
        return get(DAWN_GREEN_STONES, Integer.class);
    }
    public Integer getDawnBlueStones() {
        return get(DAWN_BLUE_STONES, Integer.class);
    }
    public Integer getDawnAncientAdenaAmount() {
        return get(DAWN_ANCIENT_ADENA_AMOUNT, Integer.class);
    }
    public Integer getDawnContributionScore() {
        return get(DAWN_CONTRIBUTION_SCORE, Integer.class);
    }
    public Integer getDuskRedStones() {
        return get(DUSK_RED_STONES, Integer.class);
    }
    public Integer getDuskGreenStones() {
        return get(DUSK_GREEN_STONES, Integer.class);
    }
    public Integer getDuskBlueStones() {
        return get(DUSK_BLUE_STONES, Integer.class);
    }
    public Integer getDuskAncientAdenaAmount() {
        return get(DUSK_ANCIENT_ADENA_AMOUNT, Integer.class);
    }
    public Integer getDuskContributionScore() {
        return get(DUSK_CONTRIBUTION_SCORE, Integer.class);
    }

    public void setCharObjId(Integer value) {
        set(CHAR_OBJ_ID, value);
    }
    public void setCabal(String value) {
        set(CABAL, value);
    }
    public void setSeal(Integer value) {
        set(SEAL, value);
    }
    public void setDawnRedStones(Integer value) {
        set(DAWN_RED_STONES, value);
    }
    public void setDawnGreenStones(Integer value) {
        set(DAWN_GREEN_STONES, value);
    }
    public void setDawnBlueStones(Integer value) {
        set(DAWN_BLUE_STONES, value);
    }
    public void setDawnAncientAdenaAmount(Integer value) {
        set(DAWN_ANCIENT_ADENA_AMOUNT, value);
    }
    public void setDawnContributionScore(Integer value) {
        set(DAWN_CONTRIBUTION_SCORE, value);
    }
    public void setDuskRedStones(Integer value) {
        set(DUSK_RED_STONES, value);
    }
    public void setDuskGreenStones(Integer value) {
        set(DUSK_GREEN_STONES, value);
    }
    public void setDuskBlueStones(Integer value) {
        set(DUSK_BLUE_STONES, value);
    }
    public void setDuskAncientAdenaAmount(Integer value) {
        set(DUSK_ANCIENT_ADENA_AMOUNT, value);
    }
    public void setDuskContributionScore(Integer value) {
        set(DUSK_CONTRIBUTION_SCORE, value);
    }

}
