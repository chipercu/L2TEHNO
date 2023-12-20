package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CommunityPerformBuffsConfigResource;

public class CommunityPerformBuffsConfigBuilder extends ResourceBuilder<CommunityPerformBuffsConfigResource> {

    public CommunityPerformBuffsConfigBuilder() {
        try {
            resourceClass = CommunityPerformBuffsConfigResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunityPerformBuffsConfigBuilder withName(String value) {
        with(CommunityPerformBuffsConfigResource.NAME, value);
        return this;
    }

    public CommunityPerformBuffsConfigBuilder withValue(Integer value) {
        with(CommunityPerformBuffsConfigResource.VALUE, value);
        return this;
    }

}
