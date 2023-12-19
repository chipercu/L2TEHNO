package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.TournamentTeamsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class TournamentTeamsBuilder extends ResourceBuilder<TournamentTeamsResource> {

    public TournamentTeamsBuilder() {
        try {
            resourceClass = TournamentTeamsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TournamentTeamsBuilder withObjId(Integer value) {
        with(TournamentTeamsResource.OBJ_ID, value);
        return this;
    }

    public TournamentTeamsBuilder withType(Integer value) {
        with(TournamentTeamsResource.TYPE, value);
        return this;
    }

    public TournamentTeamsBuilder withTeamId(Integer value) {
        with(TournamentTeamsResource.TEAM_ID, value);
        return this;
    }

    public TournamentTeamsBuilder withTeamName(String value) {
        with(TournamentTeamsResource.TEAM_NAME, value);
        return this;
    }

    public TournamentTeamsBuilder withLeader(Integer value) {
        with(TournamentTeamsResource.LEADER, value);
        return this;
    }

    public TournamentTeamsBuilder withCategory(Integer value) {
        with(TournamentTeamsResource.CATEGORY, value);
        return this;
    }

    public TournamentTeamsBuilder withWins(Integer value) {
        with(TournamentTeamsResource.WINS, value);
        return this;
    }

    public TournamentTeamsBuilder withLosts(Integer value) {
        with(TournamentTeamsResource.LOSTS, value);
        return this;
    }

    public TournamentTeamsBuilder withStatus(Integer value) {
        with(TournamentTeamsResource.STATUS, value);
        return this;
    }

}
