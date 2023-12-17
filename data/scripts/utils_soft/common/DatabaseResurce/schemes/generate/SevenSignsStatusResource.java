package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.SevenSignsStatusResource.*;

@Table(
        name = "seven_signs_status",
        primary_key = {},
        fields = {
                @Field(name = CURRENT_CYCLE , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = FESTIVAL_CYCLE , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = ACTIVE_PERIOD , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = DATE , data_type = "int" , defValue = @DefValue(Integer = 1)),
                @Field(name = PREVIOUS_WINNER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DAWN_STONE_SCORE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = DAWN_FESTIVAL_SCORE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = DUSK_STONE_SCORE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = DUSK_FESTIVAL_SCORE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = AVARICE_OWNER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = GNOSIS_OWNER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = STRIFE_OWNER , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = AVARICE_DAWN_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = GNOSIS_DAWN_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = STRIFE_DAWN_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = AVARICE_DUSK_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = GNOSIS_DUSK_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = STRIFE_DUSK_SCORE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = ACCUMULATED_BONUS0 , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = ACCUMULATED_BONUS1 , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = ACCUMULATED_BONUS2 , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = ACCUMULATED_BONUS3 , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = ACCUMULATED_BONUS4 , data_type = "bigint" , defValue = @DefValue(Long = 0)),
        }
)
public class SevenSignsStatusResource extends DataBaseTable<SevenSignsStatusResource> {

    public static final String CURRENT_CYCLE = "current_cycle";
    public static final String FESTIVAL_CYCLE = "festival_cycle";
    public static final String ACTIVE_PERIOD = "active_period";
    public static final String DATE = "date";
    public static final String PREVIOUS_WINNER = "previous_winner";
    public static final String DAWN_STONE_SCORE = "dawn_stone_score";
    public static final String DAWN_FESTIVAL_SCORE = "dawn_festival_score";
    public static final String DUSK_STONE_SCORE = "dusk_stone_score";
    public static final String DUSK_FESTIVAL_SCORE = "dusk_festival_score";
    public static final String AVARICE_OWNER = "avarice_owner";
    public static final String GNOSIS_OWNER = "gnosis_owner";
    public static final String STRIFE_OWNER = "strife_owner";
    public static final String AVARICE_DAWN_SCORE = "avarice_dawn_score";
    public static final String GNOSIS_DAWN_SCORE = "gnosis_dawn_score";
    public static final String STRIFE_DAWN_SCORE = "strife_dawn_score";
    public static final String AVARICE_DUSK_SCORE = "avarice_dusk_score";
    public static final String GNOSIS_DUSK_SCORE = "gnosis_dusk_score";
    public static final String STRIFE_DUSK_SCORE = "strife_dusk_score";
    public static final String ACCUMULATED_BONUS0 = "accumulated_bonus0";
    public static final String ACCUMULATED_BONUS1 = "accumulated_bonus1";
    public static final String ACCUMULATED_BONUS2 = "accumulated_bonus2";
    public static final String ACCUMULATED_BONUS3 = "accumulated_bonus3";
    public static final String ACCUMULATED_BONUS4 = "accumulated_bonus4";

    public SevenSignsStatusResource() {
super(SevenSignsStatusResource.class);
}

    public Integer getCurrentCycle() {
        return get(CURRENT_CYCLE, Integer.class);
    }
    public Integer getFestivalCycle() {
        return get(FESTIVAL_CYCLE, Integer.class);
    }
    public Integer getActivePeriod() {
        return get(ACTIVE_PERIOD, Integer.class);
    }
    public Integer getDate() {
        return get(DATE, Integer.class);
    }
    public Integer getPreviousWinner() {
        return get(PREVIOUS_WINNER, Integer.class);
    }
    public Long getDawnStoneScore() {
        return get(DAWN_STONE_SCORE, Long.class);
    }
    public Long getDawnFestivalScore() {
        return get(DAWN_FESTIVAL_SCORE, Long.class);
    }
    public Long getDuskStoneScore() {
        return get(DUSK_STONE_SCORE, Long.class);
    }
    public Long getDuskFestivalScore() {
        return get(DUSK_FESTIVAL_SCORE, Long.class);
    }
    public Integer getAvariceOwner() {
        return get(AVARICE_OWNER, Integer.class);
    }
    public Integer getGnosisOwner() {
        return get(GNOSIS_OWNER, Integer.class);
    }
    public Integer getStrifeOwner() {
        return get(STRIFE_OWNER, Integer.class);
    }
    public Integer getAvariceDawnScore() {
        return get(AVARICE_DAWN_SCORE, Integer.class);
    }
    public Integer getGnosisDawnScore() {
        return get(GNOSIS_DAWN_SCORE, Integer.class);
    }
    public Integer getStrifeDawnScore() {
        return get(STRIFE_DAWN_SCORE, Integer.class);
    }
    public Integer getAvariceDuskScore() {
        return get(AVARICE_DUSK_SCORE, Integer.class);
    }
    public Integer getGnosisDuskScore() {
        return get(GNOSIS_DUSK_SCORE, Integer.class);
    }
    public Integer getStrifeDuskScore() {
        return get(STRIFE_DUSK_SCORE, Integer.class);
    }
    public Long getAccumulatedBonus0() {
        return get(ACCUMULATED_BONUS0, Long.class);
    }
    public Long getAccumulatedBonus1() {
        return get(ACCUMULATED_BONUS1, Long.class);
    }
    public Long getAccumulatedBonus2() {
        return get(ACCUMULATED_BONUS2, Long.class);
    }
    public Long getAccumulatedBonus3() {
        return get(ACCUMULATED_BONUS3, Long.class);
    }
    public Long getAccumulatedBonus4() {
        return get(ACCUMULATED_BONUS4, Long.class);
    }

    public void setCurrentCycle(Integer value) {
        set(CURRENT_CYCLE, value);
    }
    public void setFestivalCycle(Integer value) {
        set(FESTIVAL_CYCLE, value);
    }
    public void setActivePeriod(Integer value) {
        set(ACTIVE_PERIOD, value);
    }
    public void setDate(Integer value) {
        set(DATE, value);
    }
    public void setPreviousWinner(Integer value) {
        set(PREVIOUS_WINNER, value);
    }
    public void setDawnStoneScore(Long value) {
        set(DAWN_STONE_SCORE, value);
    }
    public void setDawnFestivalScore(Long value) {
        set(DAWN_FESTIVAL_SCORE, value);
    }
    public void setDuskStoneScore(Long value) {
        set(DUSK_STONE_SCORE, value);
    }
    public void setDuskFestivalScore(Long value) {
        set(DUSK_FESTIVAL_SCORE, value);
    }
    public void setAvariceOwner(Integer value) {
        set(AVARICE_OWNER, value);
    }
    public void setGnosisOwner(Integer value) {
        set(GNOSIS_OWNER, value);
    }
    public void setStrifeOwner(Integer value) {
        set(STRIFE_OWNER, value);
    }
    public void setAvariceDawnScore(Integer value) {
        set(AVARICE_DAWN_SCORE, value);
    }
    public void setGnosisDawnScore(Integer value) {
        set(GNOSIS_DAWN_SCORE, value);
    }
    public void setStrifeDawnScore(Integer value) {
        set(STRIFE_DAWN_SCORE, value);
    }
    public void setAvariceDuskScore(Integer value) {
        set(AVARICE_DUSK_SCORE, value);
    }
    public void setGnosisDuskScore(Integer value) {
        set(GNOSIS_DUSK_SCORE, value);
    }
    public void setStrifeDuskScore(Integer value) {
        set(STRIFE_DUSK_SCORE, value);
    }
    public void setAccumulatedBonus0(Long value) {
        set(ACCUMULATED_BONUS0, value);
    }
    public void setAccumulatedBonus1(Long value) {
        set(ACCUMULATED_BONUS1, value);
    }
    public void setAccumulatedBonus2(Long value) {
        set(ACCUMULATED_BONUS2, value);
    }
    public void setAccumulatedBonus3(Long value) {
        set(ACCUMULATED_BONUS3, value);
    }
    public void setAccumulatedBonus4(Long value) {
        set(ACCUMULATED_BONUS4, value);
    }

}
