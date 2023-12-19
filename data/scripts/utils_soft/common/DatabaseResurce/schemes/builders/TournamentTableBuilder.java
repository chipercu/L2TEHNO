package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.TournamentTableResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class TournamentTableBuilder extends ResourceBuilder<TournamentTableResource> {

    public TournamentTableBuilder() {
        try {
            resourceClass = TournamentTableResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TournamentTableBuilder withCategory(Integer value) {
        with(TournamentTableResource.CATEGORY, value);
        return this;
    }

    public TournamentTableBuilder withTeam1id(Integer value) {
        with(TournamentTableResource.TEAM1ID, value);
        return this;
    }

    public TournamentTableBuilder withTeam1name(String value) {
        with(TournamentTableResource.TEAM1NAME, value);
        return this;
    }

    public TournamentTableBuilder withTeam2id(Integer value) {
        with(TournamentTableResource.TEAM2ID, value);
        return this;
    }

    public TournamentTableBuilder withTeam2name(String value) {
        with(TournamentTableResource.TEAM2NAME, value);
        return this;
    }

}
