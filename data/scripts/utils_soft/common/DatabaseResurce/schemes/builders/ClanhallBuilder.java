package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ClanhallResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ClanhallBuilder extends ResourceBuilder<ClanhallResource> {

    public ClanhallBuilder() {
        try {
            resourceClass = ClanhallResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanhallBuilder withId(Integer value) {
        with(ClanhallResource.ID, value);
        return this;
    }

    public ClanhallBuilder withName(String value) {
        with(ClanhallResource.NAME, value);
        return this;
    }

    public ClanhallBuilder withOwnerId(Integer value) {
        with(ClanhallResource.OWNER_ID, value);
        return this;
    }

    public ClanhallBuilder withLease(Integer value) {
        with(ClanhallResource.LEASE, value);
        return this;
    }

    public ClanhallBuilder withDesc(String value) {
        with(ClanhallResource.DESC, value);
        return this;
    }

    public ClanhallBuilder withLocation(String value) {
        with(ClanhallResource.LOCATION, value);
        return this;
    }

    public ClanhallBuilder withPaidUntil(Long value) {
        with(ClanhallResource.PAID_UNTIL, value);
        return this;
    }

    public ClanhallBuilder withGrade(Integer value) {
        with(ClanhallResource.GRADE, value);
        return this;
    }

    public ClanhallBuilder withPrice(Long value) {
        with(ClanhallResource.PRICE, value);
        return this;
    }

    public ClanhallBuilder withDeposit(Integer value) {
        with(ClanhallResource.DEPOSIT, value);
        return this;
    }

    public ClanhallBuilder withInDebt(Integer value) {
        with(ClanhallResource.IN_DEBT, value);
        return this;
    }

    public ClanhallBuilder withSkills(String value) {
        with(ClanhallResource.SKILLS, value);
        return this;
    }

    public ClanhallBuilder withSiegeDate(Integer value) {
        with(ClanhallResource.SIEGE_DATE, value);
        return this;
    }

    public ClanhallBuilder withSiegeDayOfWeek(Integer value) {
        with(ClanhallResource.SIEGE_DAY_OF_WEEK, value);
        return this;
    }

    public ClanhallBuilder withSiegeHourOfDay(Integer value) {
        with(ClanhallResource.SIEGE_HOUR_OF_DAY, value);
        return this;
    }

}
