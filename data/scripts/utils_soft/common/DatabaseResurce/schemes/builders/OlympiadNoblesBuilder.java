package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.OlympiadNoblesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class OlympiadNoblesBuilder extends ResourceBuilder<OlympiadNoblesResource> {

    public OlympiadNoblesBuilder() {
        try {
            resourceClass = OlympiadNoblesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public OlympiadNoblesBuilder withCharId(Integer value) {
        with(OlympiadNoblesResource.CHAR_ID, value);
        return this;
    }

    public OlympiadNoblesBuilder withClassId(Integer value) {
        with(OlympiadNoblesResource.CLASS_ID, value);
        return this;
    }

    public OlympiadNoblesBuilder withCharName(String value) {
        with(OlympiadNoblesResource.CHAR_NAME, value);
        return this;
    }

    public OlympiadNoblesBuilder withOlympiadPoints(Integer value) {
        with(OlympiadNoblesResource.OLYMPIAD_POINTS, value);
        return this;
    }

    public OlympiadNoblesBuilder withOlympiadPointsPast(Integer value) {
        with(OlympiadNoblesResource.OLYMPIAD_POINTS_PAST, value);
        return this;
    }

    public OlympiadNoblesBuilder withOlympiadPointsPastStatic(Integer value) {
        with(OlympiadNoblesResource.OLYMPIAD_POINTS_PAST_STATIC, value);
        return this;
    }

    public OlympiadNoblesBuilder withCompetitionsDone(Integer value) {
        with(OlympiadNoblesResource.COMPETITIONS_DONE, value);
        return this;
    }

    public OlympiadNoblesBuilder withCompetitionsWin(Integer value) {
        with(OlympiadNoblesResource.COMPETITIONS_WIN, value);
        return this;
    }

    public OlympiadNoblesBuilder withCompetitionsLoose(Integer value) {
        with(OlympiadNoblesResource.COMPETITIONS_LOOSE, value);
        return this;
    }

    public OlympiadNoblesBuilder withNoneclassCompetitions(Integer value) {
        with(OlympiadNoblesResource.NONECLASS_COMPETITIONS, value);
        return this;
    }

    public OlympiadNoblesBuilder withClassCompetitions(Integer value) {
        with(OlympiadNoblesResource.CLASS_COMPETITIONS, value);
        return this;
    }

    public OlympiadNoblesBuilder withTeamCompetitions(Integer value) {
        with(OlympiadNoblesResource.TEAM_COMPETITIONS, value);
        return this;
    }

}
