package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Seven_signs_statusTable.*;

@Table(
        name = "seven_signs_status",
        fields = {
                @Field(name = CURRENT_CYCLE,
                @Field(name = FESTIVAL_CYCLE,
                @Field(name = ACTIVE_PERIOD,
                @Field(name = DATE,
                @Field(name = PREVIOUS_WINNER,
                @Field(name = DAWN_STONE_SCORE,
                @Field(name = DAWN_FESTIVAL_SCORE,
                @Field(name = DUSK_STONE_SCORE,
                @Field(name = DUSK_FESTIVAL_SCORE,
                @Field(name = AVARICE_OWNER,
                @Field(name = GNOSIS_OWNER,
                @Field(name = STRIFE_OWNER,
                @Field(name = AVARICE_DAWN_SCORE,
                @Field(name = GNOSIS_DAWN_SCORE,
                @Field(name = STRIFE_DAWN_SCORE,
                @Field(name = AVARICE_DUSK_SCORE,
                @Field(name = GNOSIS_DUSK_SCORE,
                @Field(name = STRIFE_DUSK_SCORE,
                @Field(name = ACCUMULATED_BONUS0,
                @Field(name = ACCUMULATED_BONUS1,
                @Field(name = ACCUMULATED_BONUS2,
                @Field(name = ACCUMULATED_BONUS3,
                @Field(name = ACCUMULATED_BONUS4,
        }
)
public class Seven_signs_statusTable extends DataBaseTable<Seven_signs_statusTable> {

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
    public Seven_signs_statusTable() {
super(Seven_signs_statusTable.class);
}
    public IntegergetCurrent_cycle() {
        return get(CURRENT_CYCLE, Integer.class);
    }
    public IntegergetFestival_cycle() {
        return get(FESTIVAL_CYCLE, Integer.class);
    }
    public IntegergetActive_period() {
        return get(ACTIVE_PERIOD, Integer.class);
    }
    public IntegergetDate() {
        return get(DATE, Integer.class);
    }
    public IntegergetPrevious_winner() {
        return get(PREVIOUS_WINNER, Integer.class);
    }
    public ObjectgetDawn_stone_score() {
        return get(DAWN_STONE_SCORE, Object.class);
    }
    public ObjectgetDawn_festival_score() {
        return get(DAWN_FESTIVAL_SCORE, Object.class);
    }
    public ObjectgetDusk_stone_score() {
        return get(DUSK_STONE_SCORE, Object.class);
    }
    public ObjectgetDusk_festival_score() {
        return get(DUSK_FESTIVAL_SCORE, Object.class);
    }
    public IntegergetAvarice_owner() {
        return get(AVARICE_OWNER, Integer.class);
    }
    public IntegergetGnosis_owner() {
        return get(GNOSIS_OWNER, Integer.class);
    }
    public IntegergetStrife_owner() {
        return get(STRIFE_OWNER, Integer.class);
    }
    public IntegergetAvarice_dawn_score() {
        return get(AVARICE_DAWN_SCORE, Integer.class);
    }
    public IntegergetGnosis_dawn_score() {
        return get(GNOSIS_DAWN_SCORE, Integer.class);
    }
    public IntegergetStrife_dawn_score() {
        return get(STRIFE_DAWN_SCORE, Integer.class);
    }
    public IntegergetAvarice_dusk_score() {
        return get(AVARICE_DUSK_SCORE, Integer.class);
    }
    public IntegergetGnosis_dusk_score() {
        return get(GNOSIS_DUSK_SCORE, Integer.class);
    }
    public IntegergetStrife_dusk_score() {
        return get(STRIFE_DUSK_SCORE, Integer.class);
    }
    public ObjectgetAccumulated_bonus0() {
        return get(ACCUMULATED_BONUS0, Object.class);
    }
    public ObjectgetAccumulated_bonus1() {
        return get(ACCUMULATED_BONUS1, Object.class);
    }
    public ObjectgetAccumulated_bonus2() {
        return get(ACCUMULATED_BONUS2, Object.class);
    }
    public ObjectgetAccumulated_bonus3() {
        return get(ACCUMULATED_BONUS3, Object.class);
    }
    public ObjectgetAccumulated_bonus4() {
        return get(ACCUMULATED_BONUS4, Object.class);
    }

    public void setCurrent_cycle() {
        set(CURRENT_CYCLE, value);
    }
    public void setFestival_cycle() {
        set(FESTIVAL_CYCLE, value);
    }
    public void setActive_period() {
        set(ACTIVE_PERIOD, value);
    }
    public void setDate() {
        set(DATE, value);
    }
    public void setPrevious_winner() {
        set(PREVIOUS_WINNER, value);
    }
    public void setDawn_stone_score() {
        set(DAWN_STONE_SCORE, value);
    }
    public void setDawn_festival_score() {
        set(DAWN_FESTIVAL_SCORE, value);
    }
    public void setDusk_stone_score() {
        set(DUSK_STONE_SCORE, value);
    }
    public void setDusk_festival_score() {
        set(DUSK_FESTIVAL_SCORE, value);
    }
    public void setAvarice_owner() {
        set(AVARICE_OWNER, value);
    }
    public void setGnosis_owner() {
        set(GNOSIS_OWNER, value);
    }
    public void setStrife_owner() {
        set(STRIFE_OWNER, value);
    }
    public void setAvarice_dawn_score() {
        set(AVARICE_DAWN_SCORE, value);
    }
    public void setGnosis_dawn_score() {
        set(GNOSIS_DAWN_SCORE, value);
    }
    public void setStrife_dawn_score() {
        set(STRIFE_DAWN_SCORE, value);
    }
    public void setAvarice_dusk_score() {
        set(AVARICE_DUSK_SCORE, value);
    }
    public void setGnosis_dusk_score() {
        set(GNOSIS_DUSK_SCORE, value);
    }
    public void setStrife_dusk_score() {
        set(STRIFE_DUSK_SCORE, value);
    }
    public void setAccumulated_bonus0() {
        set(ACCUMULATED_BONUS0, value);
    }
    public void setAccumulated_bonus1() {
        set(ACCUMULATED_BONUS1, value);
    }
    public void setAccumulated_bonus2() {
        set(ACCUMULATED_BONUS2, value);
    }
    public void setAccumulated_bonus3() {
        set(ACCUMULATED_BONUS3, value);
    }
    public void setAccumulated_bonus4() {
        set(ACCUMULATED_BONUS4, value);
    }

}
