package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.PetitionsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class PetitionsBuilder extends ResourceBuilder<PetitionsResource> {

    public PetitionsBuilder() {
        try {
            resourceClass = PetitionsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public PetitionsBuilder withServId(Integer value) {
        with(PetitionsResource.SERV_ID, value);
        return this;
    }

    public PetitionsBuilder withActTime(Integer value) {
        with(PetitionsResource.ACT_TIME, value);
        return this;
    }

    public PetitionsBuilder withPetitionType(Integer value) {
        with(PetitionsResource.PETITION_TYPE, value);
        return this;
    }

    public PetitionsBuilder withActor(Integer value) {
        with(PetitionsResource.ACTOR, value);
        return this;
    }

    public PetitionsBuilder withLocationX(Integer value) {
        with(PetitionsResource.LOCATION_X, value);
        return this;
    }

    public PetitionsBuilder withLocationY(Integer value) {
        with(PetitionsResource.LOCATION_Y, value);
        return this;
    }

    public PetitionsBuilder withLocationZ(Integer value) {
        with(PetitionsResource.LOCATION_Z, value);
        return this;
    }

    public PetitionsBuilder withPetitionText(String value) {
        with(PetitionsResource.PETITION_TEXT, value);
        return this;
    }

    public PetitionsBuilder withSTRActor(String value) {
        with(PetitionsResource.S_TR_ACTOR, value);
        return this;
    }

    public PetitionsBuilder withSTRActorAccount(String value) {
        with(PetitionsResource.S_TR_ACTOR_ACCOUNT, value);
        return this;
    }

    public PetitionsBuilder withPetitionStatus(Integer value) {
        with(PetitionsResource.PETITION_STATUS, value);
        return this;
    }

}
