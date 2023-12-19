package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RecipesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RecipesBuilder extends ResourceBuilder<RecipesResource> {

    public RecipesBuilder() {
        try {
            resourceClass = RecipesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RecipesBuilder withId(Integer value) {
        with(RecipesResource.ID, value);
        return this;
    }

    public RecipesBuilder withName(String value) {
        with(RecipesResource.NAME, value);
        return this;
    }

    public RecipesBuilder withItem(Integer value) {
        with(RecipesResource.ITEM, value);
        return this;
    }

    public RecipesBuilder withFoundation(Integer value) {
        with(RecipesResource.FOUNDATION, value);
        return this;
    }

    public RecipesBuilder withQ(Integer value) {
        with(RecipesResource.Q, value);
        return this;
    }

    public RecipesBuilder withLvl(Integer value) {
        with(RecipesResource.LVL, value);
        return this;
    }

    public RecipesBuilder withSuccess(Integer value) {
        with(RecipesResource.SUCCESS, value);
        return this;
    }

    public RecipesBuilder withRecid(Integer value) {
        with(RecipesResource.RECID, value);
        return this;
    }

    public RecipesBuilder withMp(Integer value) {
        with(RecipesResource.MP, value);
        return this;
    }

    public RecipesBuilder withExp(Integer value) {
        with(RecipesResource.EXP, value);
        return this;
    }

    public RecipesBuilder withSp(Integer value) {
        with(RecipesResource.SP, value);
        return this;
    }

    public RecipesBuilder withDwarven(Integer value) {
        with(RecipesResource.DWARVEN, value);
        return this;
    }

}
