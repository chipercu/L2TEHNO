package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SiegeDoorupgradeResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
