package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterBlocklistResource;

public class CharacterBlocklistBuilder extends ResourceBuilder<CharacterBlocklistResource> {

    public CharacterBlocklistBuilder() {
        try {
            resourceClass = CharacterBlocklistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterBlocklistBuilder withObjId(Integer value) {
        with(CharacterBlocklistResource.OBJ__ID, value);
        return this;
    }

    public CharacterBlocklistBuilder withTargetId(Integer value) {
        with(CharacterBlocklistResource.TARGET__ID, value);
        return this;
    }

}
