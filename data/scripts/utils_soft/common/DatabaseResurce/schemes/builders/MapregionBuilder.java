package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.MapregionResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class MapregionBuilder extends ResourceBuilder<MapregionResource> {

    public MapregionBuilder() {
        try {
            resourceClass = MapregionResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MapregionBuilder withY10Plus(Integer value) {
        with(MapregionResource.Y10_PLUS, value);
        return this;
    }

    public MapregionBuilder withX11(Integer value) {
        with(MapregionResource.X11, value);
        return this;
    }

    public MapregionBuilder withX12(Integer value) {
        with(MapregionResource.X12, value);
        return this;
    }

    public MapregionBuilder withX13(Integer value) {
        with(MapregionResource.X13, value);
        return this;
    }

    public MapregionBuilder withX14(Integer value) {
        with(MapregionResource.X14, value);
        return this;
    }

    public MapregionBuilder withX15(Integer value) {
        with(MapregionResource.X15, value);
        return this;
    }

    public MapregionBuilder withX16(Integer value) {
        with(MapregionResource.X16, value);
        return this;
    }

    public MapregionBuilder withX17(Integer value) {
        with(MapregionResource.X17, value);
        return this;
    }

    public MapregionBuilder withX18(Integer value) {
        with(MapregionResource.X18, value);
        return this;
    }

    public MapregionBuilder withX19(Integer value) {
        with(MapregionResource.X19, value);
        return this;
    }

    public MapregionBuilder withX20(Integer value) {
        with(MapregionResource.X20, value);
        return this;
    }

    public MapregionBuilder withX21(Integer value) {
        with(MapregionResource.X21, value);
        return this;
    }

    public MapregionBuilder withX22(Integer value) {
        with(MapregionResource.X22, value);
        return this;
    }

    public MapregionBuilder withX23(Integer value) {
        with(MapregionResource.X23, value);
        return this;
    }

    public MapregionBuilder withX24(Integer value) {
        with(MapregionResource.X24, value);
        return this;
    }

    public MapregionBuilder withX25(Integer value) {
        with(MapregionResource.X25, value);
        return this;
    }

    public MapregionBuilder withX26(Integer value) {
        with(MapregionResource.X26, value);
        return this;
    }

}
