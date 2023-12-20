package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterRecipebookResource;

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
