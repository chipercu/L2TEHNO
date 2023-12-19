package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsSchemeResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
