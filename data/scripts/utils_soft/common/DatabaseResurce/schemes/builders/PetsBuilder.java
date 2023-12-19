package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.PetsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class PetsBuilder extends ResourceBuilder<PetsResource> {

    public PetsBuilder() {
        try {
            resourceClass = PetsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public PetsBuilder withItemObjId(Integer value) {
        with(PetsResource.ITEM_OBJ_ID, value);
        return this;
    }

    public PetsBuilder withObjId(Integer value) {
        with(PetsResource.OBJ_ID, value);
        return this;
    }

    public PetsBuilder withName(String value) {
        with(PetsResource.NAME, value);
        return this;
    }

    public PetsBuilder withLevel(Integer value) {
        with(PetsResource.LEVEL, value);
        return this;
    }

    public PetsBuilder withCurHp(Integer value) {
        with(PetsResource.CUR_HP, value);
        return this;
    }

    public PetsBuilder withCurMp(Integer value) {
        with(PetsResource.CUR_MP, value);
        return this;
    }

    public PetsBuilder withExp(Long value) {
        with(PetsResource.EXP, value);
        return this;
    }

    public PetsBuilder withSp(Integer value) {
        with(PetsResource.SP, value);
        return this;
    }

    public PetsBuilder withFed(Integer value) {
        with(PetsResource.FED, value);
        return this;
    }

    public PetsBuilder withMaxFed(Integer value) {
        with(PetsResource.MAX_FED, value);
        return this;
    }

}
