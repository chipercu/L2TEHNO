package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterPassResource;

public class CharacterPassBuilder extends ResourceBuilder<CharacterPassResource> {

    public CharacterPassBuilder() {
        try {
            resourceClass = CharacterPassResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterPassBuilder withLogin(String value) {
        with(CharacterPassResource.LOGIN, value);
        return this;
    }

    public CharacterPassBuilder withObjId(Integer value) {
        with(CharacterPassResource.OBJ_ID, value);
        return this;
    }

    public CharacterPassBuilder withQuestion(String value) {
        with(CharacterPassResource.QUESTION, value);
        return this;
    }

    public CharacterPassBuilder withAnswer(String value) {
        with(CharacterPassResource.ANSWER, value);
        return this;
    }

    public CharacterPassBuilder withPassword(String value) {
        with(CharacterPassResource.PASSWORD, value);
        return this;
    }

}
