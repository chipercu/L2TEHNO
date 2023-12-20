package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CastleManorProcureResource;

public class CastleManorProcureBuilder extends ResourceBuilder<CastleManorProcureResource> {

    public CastleManorProcureBuilder() {
        try {
            resourceClass = CastleManorProcureResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CastleManorProcureBuilder withCastleId(Integer value) {
        with(CastleManorProcureResource.CASTLE_ID, value);
        return this;
    }

    public CastleManorProcureBuilder withCropId(Integer value) {
        with(CastleManorProcureResource.CROP_ID, value);
        return this;
    }

    public CastleManorProcureBuilder withCanBuy(Long value) {
        with(CastleManorProcureResource.CAN_BUY, value);
        return this;
    }

    public CastleManorProcureBuilder withStartBuy(Long value) {
        with(CastleManorProcureResource.START_BUY, value);
        return this;
    }

    public CastleManorProcureBuilder withPrice(Long value) {
        with(CastleManorProcureResource.PRICE, value);
        return this;
    }

    public CastleManorProcureBuilder withRewardType(Integer value) {
        with(CastleManorProcureResource.REWARD_TYPE, value);
        return this;
    }

    public CastleManorProcureBuilder withPeriod(Integer value) {
        with(CastleManorProcureResource.PERIOD, value);
        return this;
    }

}
