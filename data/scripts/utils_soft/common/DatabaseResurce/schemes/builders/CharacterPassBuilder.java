package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterPassResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
