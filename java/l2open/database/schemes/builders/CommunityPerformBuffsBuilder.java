package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CommunityPerformBuffsResource;

public class CommunityPerformBuffsBuilder extends ResourceBuilder<CommunityPerformBuffsResource> {

    public CommunityPerformBuffsBuilder() {
        try {
            resourceClass = CommunityPerformBuffsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunityPerformBuffsBuilder withId(Integer value) {
        with(CommunityPerformBuffsResource.ID, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withDisplayLevel(Integer value) {
        with(CommunityPerformBuffsResource.DISPLAY_LEVEL, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withSkillId(Integer value) {
        with(CommunityPerformBuffsResource.SKILL_ID, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withSkillLevel(Integer value) {
        with(CommunityPerformBuffsResource.SKILL_LEVEL, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withName(String value) {
        with(CommunityPerformBuffsResource.NAME, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withPrice(Integer value) {
        with(CommunityPerformBuffsResource.PRICE, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withPriceItem(Integer value) {
        with(CommunityPerformBuffsResource.PRICE_ITEM, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withMinLevel(Integer value) {
        with(CommunityPerformBuffsResource.MIN_LEVEL, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withMaxLevel(Integer value) {
        with(CommunityPerformBuffsResource.MAX_LEVEL, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withIcon(String value) {
        with(CommunityPerformBuffsResource.ICON, value);
        return this;
    }

    public CommunityPerformBuffsBuilder withType(String value) {
        with(CommunityPerformBuffsResource.TYPE_, value);
        return this;
    }

}
