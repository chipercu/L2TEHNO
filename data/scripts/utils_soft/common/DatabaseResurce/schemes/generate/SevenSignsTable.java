package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SevenSignsTable.*;

@Table(
        name = "seven_signs",
        fields = {
                @Field(name = CHAR_OBJ_ID),
                @Field(name = CABAL),
                @Field(name = SEAL),
                @Field(name = DAWN_RED_STONES),
                @Field(name = DAWN_GREEN_STONES),
                @Field(name = DAWN_BLUE_STONES),
                @Field(name = DAWN_ANCIENT_ADENA_AMOUNT),
                @Field(name = DAWN_CONTRIBUTION_SCORE),
                @Field(name = DUSK_RED_STONES),
                @Field(name = DUSK_GREEN_STONES),
                @Field(name = DUSK_BLUE_STONES),
                @Field(name = DUSK_ANCIENT_ADENA_AMOUNT),
                @Field(name = DUSK_CONTRIBUTION_SCORE),
        }
)
public class SevenSignsTable extends DataBaseTable<SevenSignsTable> {

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

    public SevenSignsTable() {
super(SevenSignsTable.class);
}

    public Integer getCharObjId() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public Object getCabal() {
        return get(CABAL, Object.class);
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
    public void setCabal(Object value) {
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
