package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.KillcountResource;

public class KillcountBuilder extends ResourceBuilder<KillcountResource> {

    public KillcountBuilder() {
        try {
            resourceClass = KillcountResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public KillcountBuilder withCharId(Integer value) {
        with(KillcountResource.CHAR_ID, value);
        return this;
    }

    public KillcountBuilder withNpcId(Integer value) {
        with(KillcountResource.NPC_ID, value);
        return this;
    }

    public KillcountBuilder withCount(Integer value) {
        with(KillcountResource.COUNT, value);
        return this;
    }

}
