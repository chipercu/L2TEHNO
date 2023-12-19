package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SevenSignsStatusResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SevenSignsStatusBuilder extends ResourceBuilder<SevenSignsStatusResource> {

    public SevenSignsStatusBuilder() {
        try {
            resourceClass = SevenSignsStatusResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SevenSignsStatusBuilder withCurrentCycle(Integer value) {
        with(SevenSignsStatusResource.CURRENT_CYCLE, value);
        return this;
    }

    public SevenSignsStatusBuilder withFestivalCycle(Integer value) {
        with(SevenSignsStatusResource.FESTIVAL_CYCLE, value);
        return this;
    }

    public SevenSignsStatusBuilder withActivePeriod(Integer value) {
        with(SevenSignsStatusResource.ACTIVE_PERIOD, value);
        return this;
    }

    public SevenSignsStatusBuilder withDate(Integer value) {
        with(SevenSignsStatusResource._DATE, value);
        return this;
    }

    public SevenSignsStatusBuilder withPreviousWinner(Integer value) {
        with(SevenSignsStatusResource.PREVIOUS_WINNER, value);
        return this;
    }

    public SevenSignsStatusBuilder withDawnStoneScore(Long value) {
        with(SevenSignsStatusResource.DAWN_STONE_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withDawnFestivalScore(Long value) {
        with(SevenSignsStatusResource.DAWN_FESTIVAL_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withDuskStoneScore(Long value) {
        with(SevenSignsStatusResource.DUSK_STONE_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withDuskFestivalScore(Long value) {
        with(SevenSignsStatusResource.DUSK_FESTIVAL_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withAvariceOwner(Integer value) {
        with(SevenSignsStatusResource.AVARICE_OWNER, value);
        return this;
    }

    public SevenSignsStatusBuilder withGnosisOwner(Integer value) {
        with(SevenSignsStatusResource.GNOSIS_OWNER, value);
        return this;
    }

    public SevenSignsStatusBuilder withStrifeOwner(Integer value) {
        with(SevenSignsStatusResource.STRIFE_OWNER, value);
        return this;
    }

    public SevenSignsStatusBuilder withAvariceDawnScore(Integer value) {
        with(SevenSignsStatusResource.AVARICE_DAWN_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withGnosisDawnScore(Integer value) {
        with(SevenSignsStatusResource.GNOSIS_DAWN_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withStrifeDawnScore(Integer value) {
        with(SevenSignsStatusResource.STRIFE_DAWN_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withAvariceDuskScore(Integer value) {
        with(SevenSignsStatusResource.AVARICE_DUSK_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withGnosisDuskScore(Integer value) {
        with(SevenSignsStatusResource.GNOSIS_DUSK_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withStrifeDuskScore(Integer value) {
        with(SevenSignsStatusResource.STRIFE_DUSK_SCORE, value);
        return this;
    }

    public SevenSignsStatusBuilder withAccumulatedBonus0(Long value) {
        with(SevenSignsStatusResource.ACCUMULATED_BONUS0, value);
        return this;
    }

    public SevenSignsStatusBuilder withAccumulatedBonus1(Long value) {
        with(SevenSignsStatusResource.ACCUMULATED_BONUS1, value);
        return this;
    }

    public SevenSignsStatusBuilder withAccumulatedBonus2(Long value) {
        with(SevenSignsStatusResource.ACCUMULATED_BONUS2, value);
        return this;
    }

    public SevenSignsStatusBuilder withAccumulatedBonus3(Long value) {
        with(SevenSignsStatusResource.ACCUMULATED_BONUS3, value);
        return this;
    }

    public SevenSignsStatusBuilder withAccumulatedBonus4(Long value) {
        with(SevenSignsStatusResource.ACCUMULATED_BONUS4, value);
        return this;
    }

}
