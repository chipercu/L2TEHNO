package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterPostFriendsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterPostFriendsBuilder extends ResourceBuilder<CharacterPostFriendsResource> {

    public CharacterPostFriendsBuilder() {
        try {
            resourceClass = CharacterPostFriendsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterPostFriendsBuilder withObjectId(Integer value) {
        with(CharacterPostFriendsResource.OBJECT_ID, value);
        return this;
    }

    public CharacterPostFriendsBuilder withPostFriend(Integer value) {
        with(CharacterPostFriendsResource.POST_FRIEND, value);
        return this;
    }

}
