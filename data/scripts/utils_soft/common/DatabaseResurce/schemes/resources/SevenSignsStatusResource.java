package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.SevenSignsStatusResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "seven_signs_status",
        primary_key = {},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CURRENT_CYCLE , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FESTIVAL_CYCLE , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ACTIVE_PERIOD , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = _DATE , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PREVIOUS_WINNER , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = DAWN_STONE_SCORE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = DAWN_FESTIVAL_SCORE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = DUSK_STONE_SCORE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = DUSK_FESTIVAL_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AVARICE_OWNER , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GNOSIS_OWNER , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = STRIFE_OWNER , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AVARICE_DAWN_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GNOSIS_DAWN_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = STRIFE_DAWN_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AVARICE_DUSK_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = GNOSIS_DUSK_SCORE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = STRIFE_DUSK_SCORE , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ACCUMULATED_BONUS0 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ACCUMULATED_BONUS1 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ACCUMULATED_BONUS2 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ACCUMULATED_BONUS3 , defValue = "0"),
                @Column(is_null = NO , type = BIGINT , type_size = 0 , name = ACCUMULATED_BONUS4 , defValue = "0"),
        }
)
public class SevenSignsStatusResource extends DataBaseTable<SevenSignsStatusResource> {

    public static final String CURRENT_CYCLE = "current_cycle";
    public static final String FESTIVAL_CYCLE = "festival_cycle";
    public static final String ACTIVE_PERIOD = "active_period";
    public static final String _DATE = "date";
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
        return get(CURRENT_CYCLE);
    }
    public Integer getFestivalCycle() {
        return get(FESTIVAL_CYCLE);
    }
    public Integer getActivePeriod() {
        return get(ACTIVE_PERIOD);
    }
    public Integer getDate() {
        return get(_DATE);
    }
    public Integer getPreviousWinner() {
        return get(PREVIOUS_WINNER);
    }
    public Long getDawnStoneScore() {
        return get(DAWN_STONE_SCORE);
    }
    public Long getDawnFestivalScore() {
        return get(DAWN_FESTIVAL_SCORE);
    }
    public Long getDuskStoneScore() {
        return get(DUSK_STONE_SCORE);
    }
    public Long getDuskFestivalScore() {
        return get(DUSK_FESTIVAL_SCORE);
    }
    public Integer getAvariceOwner() {
        return get(AVARICE_OWNER);
    }
    public Integer getGnosisOwner() {
        return get(GNOSIS_OWNER);
    }
    public Integer getStrifeOwner() {
        return get(STRIFE_OWNER);
    }
    public Integer getAvariceDawnScore() {
        return get(AVARICE_DAWN_SCORE);
    }
    public Integer getGnosisDawnScore() {
        return get(GNOSIS_DAWN_SCORE);
    }
    public Integer getStrifeDawnScore() {
        return get(STRIFE_DAWN_SCORE);
    }
    public Integer getAvariceDuskScore() {
        return get(AVARICE_DUSK_SCORE);
    }
    public Integer getGnosisDuskScore() {
        return get(GNOSIS_DUSK_SCORE);
    }
    public Integer getStrifeDuskScore() {
        return get(STRIFE_DUSK_SCORE);
    }
    public Long getAccumulatedBonus0() {
        return get(ACCUMULATED_BONUS0);
    }
    public Long getAccumulatedBonus1() {
        return get(ACCUMULATED_BONUS1);
    }
    public Long getAccumulatedBonus2() {
        return get(ACCUMULATED_BONUS2);
    }
    public Long getAccumulatedBonus3() {
        return get(ACCUMULATED_BONUS3);
    }
    public Long getAccumulatedBonus4() {
        return get(ACCUMULATED_BONUS4);
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
        set(_DATE, value);
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
