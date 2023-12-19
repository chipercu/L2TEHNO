package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterFriendsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterFriendsBuilder extends ResourceBuilder<CharacterFriendsResource> {

    public CharacterFriendsBuilder() {
        try {
            resourceClass = CharacterFriendsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterFriendsBuilder withCharId(Integer value) {
        with(CharacterFriendsResource.CHAR_ID, value);
        return this;
    }

    public CharacterFriendsBuilder withFriendId(Integer value) {
        with(CharacterFriendsResource.FRIEND_ID, value);
        return this;
    }

}
