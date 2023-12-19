package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterRecipebookResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterRecipebookBuilder extends ResourceBuilder<CharacterRecipebookResource> {

    public CharacterRecipebookBuilder() {
        try {
            resourceClass = CharacterRecipebookResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterRecipebookBuilder withCharId(Integer value) {
        with(CharacterRecipebookResource.CHAR_ID, value);
        return this;
    }

    public CharacterRecipebookBuilder withId(Integer value) {
        with(CharacterRecipebookResource.ID, value);
        return this;
    }

}
