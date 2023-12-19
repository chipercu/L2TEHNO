package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsConfigResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
