package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterFriendsResource;

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
