package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SiegeDoorupgradeResource;

public class SiegeDoorupgradeBuilder extends ResourceBuilder<SiegeDoorupgradeResource> {

    public SiegeDoorupgradeBuilder() {
        try {
            resourceClass = SiegeDoorupgradeResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SiegeDoorupgradeBuilder withDoorId(Integer value) {
        with(SiegeDoorupgradeResource.DOOR_ID, value);
        return this;
    }

    public SiegeDoorupgradeBuilder withHp(Integer value) {
        with(SiegeDoorupgradeResource.HP, value);
        return this;
    }

}
