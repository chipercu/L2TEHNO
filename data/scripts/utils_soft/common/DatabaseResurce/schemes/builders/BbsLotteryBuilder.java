package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.BbsLotteryResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
