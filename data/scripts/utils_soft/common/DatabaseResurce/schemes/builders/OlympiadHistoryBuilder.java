package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.OlympiadHistoryResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class OlympiadHistoryBuilder extends ResourceBuilder<OlympiadHistoryResource> {

    public OlympiadHistoryBuilder() {
        try {
            resourceClass = OlympiadHistoryResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public OlympiadHistoryBuilder withObjectId1(Integer value) {
        with(OlympiadHistoryResource.OBJECT_ID_1, value);
        return this;
    }

    public OlympiadHistoryBuilder withObjectId2(Integer value) {
        with(OlympiadHistoryResource.OBJECT_ID_2, value);
        return this;
    }

    public OlympiadHistoryBuilder withClassId1(Integer value) {
        with(OlympiadHistoryResource.CLASS_ID_1, value);
        return this;
    }

    public OlympiadHistoryBuilder withClassId2(Integer value) {
        with(OlympiadHistoryResource.CLASS_ID_2, value);
        return this;
    }

    public OlympiadHistoryBuilder withName1(String value) {
        with(OlympiadHistoryResource.NAME_1, value);
        return this;
    }

    public OlympiadHistoryBuilder withName2(String value) {
        with(OlympiadHistoryResource.NAME_2, value);
        return this;
    }

    public OlympiadHistoryBuilder withGameStartTime(Long value) {
        with(OlympiadHistoryResource.GAME_START_TIME, value);
        return this;
    }

    public OlympiadHistoryBuilder withGameTime(Integer value) {
        with(OlympiadHistoryResource.GAME_TIME, value);
        return this;
    }

    public OlympiadHistoryBuilder withGameStatus(Integer value) {
        with(OlympiadHistoryResource.GAME_STATUS, value);
        return this;
    }

    public OlympiadHistoryBuilder withGameType(Integer value) {
        with(OlympiadHistoryResource.GAME_TYPE, value);
        return this;
    }

    public OlympiadHistoryBuilder withOld(Integer value) {
        with(OlympiadHistoryResource.OLD, value);
        return this;
    }

}
