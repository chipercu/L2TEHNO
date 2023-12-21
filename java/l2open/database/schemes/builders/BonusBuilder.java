package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.BonusResource;

public class BonusBuilder extends ResourceBuilder<BonusResource> {

    public BonusBuilder() {
        try {
            resourceClass = BonusResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BonusBuilder withObjId(Integer value) {
        with(BonusResource.OBJ_ID, value);
        return this;
    }

    public BonusBuilder withAccount(String value) {
        with(BonusResource.ACCOUNT, value);
        return this;
    }

    public BonusBuilder withBonusName(String value) {
        with(BonusResource.BONUS_NAME, value);
        return this;
    }

    public BonusBuilder withBonusValue(Double value) {
        with(BonusResource.BONUS_VALUE, value);
        return this;
    }

    public BonusBuilder withBonusExpireTime(Integer value) {
        with(BonusResource.BONUS_EXPIRE_TIME, value);
        return this;
    }

}