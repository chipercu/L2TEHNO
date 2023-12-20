package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterPostFriendsResource;

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
