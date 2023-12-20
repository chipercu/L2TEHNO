package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CastleResource;

public class CastleBuilder extends ResourceBuilder<CastleResource> {

    public CastleBuilder() {
        try {
            resourceClass = CastleResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CastleBuilder withId(Integer value) {
        with(CastleResource.ID, value);
        return this;
    }

    public CastleBuilder withName(String value) {
        with(CastleResource.NAME, value);
        return this;
    }

    public CastleBuilder withTaxPercent(Integer value) {
        with(CastleResource.TAX_PERCENT, value);
        return this;
    }

    public CastleBuilder withTreasury(Long value) {
        with(CastleResource.TREASURY, value);
        return this;
    }

    public CastleBuilder withSiegeDate(Integer value) {
        with(CastleResource.SIEGE_DATE, value);
        return this;
    }

    public CastleBuilder withSiegeDayOfWeek(Integer value) {
        with(CastleResource.SIEGE_DAY_OF_WEEK, value);
        return this;
    }

    public CastleBuilder withSiegeHourOfDay(Integer value) {
        with(CastleResource.SIEGE_HOUR_OF_DAY, value);
        return this;
    }

    public CastleBuilder withTownId(Integer value) {
        with(CastleResource.TOWN_ID, value);
        return this;
    }

    public CastleBuilder withSkills(String value) {
        with(CastleResource.SKILLS, value);
        return this;
    }

    public CastleBuilder withFlags(String value) {
        with(CastleResource.FLAGS, value);
        return this;
    }

    public CastleBuilder withOwnDate(Integer value) {
        with(CastleResource.OWN_DATE, value);
        return this;
    }

    public CastleBuilder withDominionLord(Integer value) {
        with(CastleResource.DOMINION_LORD, value);
        return this;
    }

    public CastleBuilder withSetSiege(Integer value) {
        with(CastleResource.SET_SIEGE, value);
        return this;
    }

}
