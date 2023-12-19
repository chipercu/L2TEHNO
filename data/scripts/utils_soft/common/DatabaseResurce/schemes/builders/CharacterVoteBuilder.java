package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterVoteResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterVoteBuilder extends ResourceBuilder<CharacterVoteResource> {

    public CharacterVoteBuilder() {
        try {
            resourceClass = CharacterVoteResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterVoteBuilder withType(Integer value) {
        with(CharacterVoteResource.TYPE, value);
        return this;
    }

    public CharacterVoteBuilder withVoteId(Integer value) {
        with(CharacterVoteResource.VOTE_ID, value);
        return this;
    }

    public CharacterVoteBuilder withDate(Long value) {
        with(CharacterVoteResource._DATE, value);
        return this;
    }

    public CharacterVoteBuilder withId(Integer value) {
        with(CharacterVoteResource.ID, value);
        return this;
    }

    public CharacterVoteBuilder withNick(String value) {
        with(CharacterVoteResource.NICK, value);
        return this;
    }

    public CharacterVoteBuilder withMultipler(Integer value) {
        with(CharacterVoteResource.MULTIPLER, value);
        return this;
    }

    public CharacterVoteBuilder withHasReward(Integer value) {
        with(CharacterVoteResource.HAS_REWARD, value);
        return this;
    }

}
