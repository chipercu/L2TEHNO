package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SevenSignsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "seven_signs",
        primary_key = {CHAR_OBJ_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_OBJ_ID , defValue = "0"),
                @Column(is_null = NO , type = ENUM , type_size = 8 , name = CABAL , defValue = "No Cabal"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = SEAL , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DAWN_RED_STONES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DAWN_GREEN_STONES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DAWN_BLUE_STONES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DAWN_ANCIENT_ADENA_AMOUNT , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DAWN_CONTRIBUTION_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DUSK_RED_STONES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DUSK_GREEN_STONES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DUSK_BLUE_STONES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DUSK_ANCIENT_ADENA_AMOUNT , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DUSK_CONTRIBUTION_SCORE , defValue = "0"),
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

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID);
    }
    public String getCabal() {
        return get(CABAL);
    }
    public Integer getSeal() {
        return get(SEAL);
    }
    public Integer getDawnRedStones() {
        return get(DAWN_RED_STONES);
    }
    public Integer getDawnGreenStones() {
        return get(DAWN_GREEN_STONES);
    }
    public Integer getDawnBlueStones() {
        return get(DAWN_BLUE_STONES);
    }
    public Integer getDawnAncientAdenaAmount() {
        return get(DAWN_ANCIENT_ADENA_AMOUNT);
    }
    public Integer getDawnContributionScore() {
        return get(DAWN_CONTRIBUTION_SCORE);
    }
    public Integer getDuskRedStones() {
        return get(DUSK_RED_STONES);
    }
    public Integer getDuskGreenStones() {
        return get(DUSK_GREEN_STONES);
    }
    public Integer getDuskBlueStones() {
        return get(DUSK_BLUE_STONES);
    }
    public Integer getDuskAncientAdenaAmount() {
        return get(DUSK_ANCIENT_ADENA_AMOUNT);
    }
    public Integer getDuskContributionScore() {
        return get(DUSK_CONTRIBUTION_SCORE);
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
