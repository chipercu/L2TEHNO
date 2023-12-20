package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CastleManorProductionResource;

public class CastleManorProductionBuilder extends ResourceBuilder<CastleManorProductionResource> {

    public CastleManorProductionBuilder() {
        try {
            resourceClass = CastleManorProductionResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CastleManorProductionBuilder withCastleId(Integer value) {
        with(CastleManorProductionResource.CASTLE_ID, value);
        return this;
    }

    public CastleManorProductionBuilder withSeedId(Integer value) {
        with(CastleManorProductionResource.SEED_ID, value);
        return this;
    }

    public CastleManorProductionBuilder withCanProduce(Long value) {
        with(CastleManorProductionResource.CAN_PRODUCE, value);
        return this;
    }

    public CastleManorProductionBuilder withStartProduce(Long value) {
        with(CastleManorProductionResource.START_PRODUCE, value);
        return this;
    }

    public CastleManorProductionBuilder withSeedPrice(Long value) {
        with(CastleManorProductionResource.SEED_PRICE, value);
        return this;
    }

    public CastleManorProductionBuilder withPeriod(Integer value) {
        with(CastleManorProductionResource.PERIOD, value);
        return this;
    }

}
