package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.BbsLotteryResource;

public class BbsLotteryBuilder extends ResourceBuilder<BbsLotteryResource> {

    public BbsLotteryBuilder() {
        try {
            resourceClass = BbsLotteryResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BbsLotteryBuilder withCount(Long value) {
        with(BbsLotteryResource.COUNT, value);
        return this;
    }

    public BbsLotteryBuilder withType(String value) {
        with(BbsLotteryResource.TYPE, value);
        return this;
    }

    public BbsLotteryBuilder withName(String value) {
        with(BbsLotteryResource.NAME, value);
        return this;
    }

}
