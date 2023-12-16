package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Seven_signsTable.*;

@Table(
        name = "seven_signs",
        fields = {
                @Field(name = CHAR_OBJ_ID,
                @Field(name = CABAL,
                @Field(name = SEAL,
                @Field(name = DAWN_RED_STONES,
                @Field(name = DAWN_GREEN_STONES,
                @Field(name = DAWN_BLUE_STONES,
                @Field(name = DAWN_ANCIENT_ADENA_AMOUNT,
                @Field(name = DAWN_CONTRIBUTION_SCORE,
                @Field(name = DUSK_RED_STONES,
                @Field(name = DUSK_GREEN_STONES,
                @Field(name = DUSK_BLUE_STONES,
                @Field(name = DUSK_ANCIENT_ADENA_AMOUNT,
                @Field(name = DUSK_CONTRIBUTION_SCORE,
        }
)
public class Seven_signsTable extends DataBaseTable<Seven_signsTable> {

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
    public Seven_signsTable() {
super(Seven_signsTable.class);
}
    public IntegergetChar_obj_id() {
        return get(CHAR_OBJ_ID, Integer.class);
    }
    public ObjectgetCabal() {
        return get(CABAL, Object.class);
    }
    public IntegergetSeal() {
        return get(SEAL, Integer.class);
    }
    public IntegergetDawn_red_stones() {
        return get(DAWN_RED_STONES, Integer.class);
    }
    public IntegergetDawn_green_stones() {
        return get(DAWN_GREEN_STONES, Integer.class);
    }
    public IntegergetDawn_blue_stones() {
        return get(DAWN_BLUE_STONES, Integer.class);
    }
    public IntegergetDawn_ancient_adena_amount() {
        return get(DAWN_ANCIENT_ADENA_AMOUNT, Integer.class);
    }
    public IntegergetDawn_contribution_score() {
        return get(DAWN_CONTRIBUTION_SCORE, Integer.class);
    }
    public IntegergetDusk_red_stones() {
        return get(DUSK_RED_STONES, Integer.class);
    }
    public IntegergetDusk_green_stones() {
        return get(DUSK_GREEN_STONES, Integer.class);
    }
    public IntegergetDusk_blue_stones() {
        return get(DUSK_BLUE_STONES, Integer.class);
    }
    public IntegergetDusk_ancient_adena_amount() {
        return get(DUSK_ANCIENT_ADENA_AMOUNT, Integer.class);
    }
    public IntegergetDusk_contribution_score() {
        return get(DUSK_CONTRIBUTION_SCORE, Integer.class);
    }

    public void setChar_obj_id() {
        set(CHAR_OBJ_ID, value);
    }
    public void setCabal() {
        set(CABAL, value);
    }
    public void setSeal() {
        set(SEAL, value);
    }
    public void setDawn_red_stones() {
        set(DAWN_RED_STONES, value);
    }
    public void setDawn_green_stones() {
        set(DAWN_GREEN_STONES, value);
    }
    public void setDawn_blue_stones() {
        set(DAWN_BLUE_STONES, value);
    }
    public void setDawn_ancient_adena_amount() {
        set(DAWN_ANCIENT_ADENA_AMOUNT, value);
    }
    public void setDawn_contribution_score() {
        set(DAWN_CONTRIBUTION_SCORE, value);
    }
    public void setDusk_red_stones() {
        set(DUSK_RED_STONES, value);
    }
    public void setDusk_green_stones() {
        set(DUSK_GREEN_STONES, value);
    }
    public void setDusk_blue_stones() {
        set(DUSK_BLUE_STONES, value);
    }
    public void setDusk_ancient_adena_amount() {
        set(DUSK_ANCIENT_ADENA_AMOUNT, value);
    }
    public void setDusk_contribution_score() {
        set(DUSK_CONTRIBUTION_SCORE, value);
    }

}
