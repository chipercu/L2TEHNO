package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ComteleportResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ComteleportBuilder extends ResourceBuilder<ComteleportResource> {

    public ComteleportBuilder() {
        try {
            resourceClass = ComteleportResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ComteleportBuilder withTpId(Integer value) {
        with(ComteleportResource.TP_ID, value);
        return this;
    }

    public ComteleportBuilder withName(String value) {
        with(ComteleportResource.NAME, value);
        return this;
    }

    public ComteleportBuilder withCharId(Integer value) {
        with(ComteleportResource.CHAR_ID, value);
        return this;
    }

    public ComteleportBuilder withXPos(Integer value) {
        with(ComteleportResource.X_POS, value);
        return this;
    }

    public ComteleportBuilder withYPos(Integer value) {
        with(ComteleportResource.Y_POS, value);
        return this;
    }

    public ComteleportBuilder withZPos(Integer value) {
        with(ComteleportResource.Z_POS, value);
        return this;
    }

}
