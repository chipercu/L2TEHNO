package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SevenSignsResource;

public class SevenSignsBuilder extends ResourceBuilder<SevenSignsResource> {

    public SevenSignsBuilder() {
        try {
            resourceClass = SevenSignsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SevenSignsBuilder withCharObjId(Integer value) {
        with(SevenSignsResource.CHAR_OBJ_ID, value);
        return this;
    }

    public SevenSignsBuilder withCabal(Enum value) {
        with(SevenSignsResource.CABAL, value);
        return this;
    }

    public SevenSignsBuilder withSeal(Integer value) {
        with(SevenSignsResource.SEAL, value);
        return this;
    }

    public SevenSignsBuilder withDawnRedStones(Integer value) {
        with(SevenSignsResource.DAWN_RED_STONES, value);
        return this;
    }

    public SevenSignsBuilder withDawnGreenStones(Integer value) {
        with(SevenSignsResource.DAWN_GREEN_STONES, value);
        return this;
    }

    public SevenSignsBuilder withDawnBlueStones(Integer value) {
        with(SevenSignsResource.DAWN_BLUE_STONES, value);
        return this;
    }

    public SevenSignsBuilder withDawnAncientAdenaAmount(Integer value) {
        with(SevenSignsResource.DAWN_ANCIENT_ADENA_AMOUNT, value);
        return this;
    }

    public SevenSignsBuilder withDawnContributionScore(Integer value) {
        with(SevenSignsResource.DAWN_CONTRIBUTION_SCORE, value);
        return this;
    }

    public SevenSignsBuilder withDuskRedStones(Integer value) {
        with(SevenSignsResource.DUSK_RED_STONES, value);
        return this;
    }

    public SevenSignsBuilder withDuskGreenStones(Integer value) {
        with(SevenSignsResource.DUSK_GREEN_STONES, value);
        return this;
    }

    public SevenSignsBuilder withDuskBlueStones(Integer value) {
        with(SevenSignsResource.DUSK_BLUE_STONES, value);
        return this;
    }

    public SevenSignsBuilder withDuskAncientAdenaAmount(Integer value) {
        with(SevenSignsResource.DUSK_ANCIENT_ADENA_AMOUNT, value);
        return this;
    }

    public SevenSignsBuilder withDuskContributionScore(Integer value) {
        with(SevenSignsResource.DUSK_CONTRIBUTION_SCORE, value);
        return this;
    }

}
