package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CommunityPerformBuffsSchemebuffResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CommunityPerformBuffsSchemebuffBuilder extends ResourceBuilder<CommunityPerformBuffsSchemebuffResource> {

    public CommunityPerformBuffsSchemebuffBuilder() {
        try {
            resourceClass = CommunityPerformBuffsSchemebuffResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunityPerformBuffsSchemebuffBuilder withSchemeId(Integer value) {
        with(CommunityPerformBuffsSchemebuffResource.SCHEME_ID, value);
        return this;
    }

    public CommunityPerformBuffsSchemebuffBuilder withBuffId(Integer value) {
        with(CommunityPerformBuffsSchemebuffResource.BUFF_ID, value);
        return this;
    }

    public CommunityPerformBuffsSchemebuffBuilder withIndex(String value) {
        with(CommunityPerformBuffsSchemebuffResource.INDEX_, value);
        return this;
    }

}
