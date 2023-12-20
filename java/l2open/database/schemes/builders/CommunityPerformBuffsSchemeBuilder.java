package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CommunityPerformBuffsSchemeResource;

public class CommunityPerformBuffsSchemeBuilder extends ResourceBuilder<CommunityPerformBuffsSchemeResource> {

    public CommunityPerformBuffsSchemeBuilder() {
        try {
            resourceClass = CommunityPerformBuffsSchemeResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunityPerformBuffsSchemeBuilder withId(Integer value) {
        with(CommunityPerformBuffsSchemeResource.ID, value);
        return this;
    }

    public CommunityPerformBuffsSchemeBuilder withOwner(Integer value) {
        with(CommunityPerformBuffsSchemeResource.OWNER, value);
        return this;
    }

    public CommunityPerformBuffsSchemeBuilder withName(String value) {
        with(CommunityPerformBuffsSchemeResource.NAME, value);
        return this;
    }

}
