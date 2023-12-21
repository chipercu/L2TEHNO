package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterBookmarksResource;

public class CharacterBookmarksBuilder extends ResourceBuilder<CharacterBookmarksResource> {

    public CharacterBookmarksBuilder() {
        try {
            resourceClass = CharacterBookmarksResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterBookmarksBuilder withCharId(Integer value) {
        with(CharacterBookmarksResource.CHAR__ID, value);
        return this;
    }

    public CharacterBookmarksBuilder withIdx(Integer value) {
        with(CharacterBookmarksResource.IDX, value);
        return this;
    }

    public CharacterBookmarksBuilder withName(String value) {
        with(CharacterBookmarksResource.NAME, value);
        return this;
    }

    public CharacterBookmarksBuilder withAcronym(String value) {
        with(CharacterBookmarksResource.ACRONYM, value);
        return this;
    }

    public CharacterBookmarksBuilder withIcon(Integer value) {
        with(CharacterBookmarksResource.ICON, value);
        return this;
    }

    public CharacterBookmarksBuilder withX(Integer value) {
        with(CharacterBookmarksResource.X, value);
        return this;
    }

    public CharacterBookmarksBuilder withY(Integer value) {
        with(CharacterBookmarksResource.Y, value);
        return this;
    }

    public CharacterBookmarksBuilder withZ(Integer value) {
        with(CharacterBookmarksResource.Z, value);
        return this;
    }

}