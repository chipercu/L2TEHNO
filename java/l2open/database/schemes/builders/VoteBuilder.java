package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.VoteResource;

public class VoteBuilder extends ResourceBuilder<VoteResource> {

    public VoteBuilder() {
        try {
            resourceClass = VoteResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public VoteBuilder withId(Integer value) {
        with(VoteResource.ID, value);
        return this;
    }

    public VoteBuilder withHWID(String value) {
        with(VoteResource.H_WI_D, value);
        return this;
    }

    public VoteBuilder withVote(Integer value) {
        with(VoteResource.VOTE, value);
        return this;
    }

}
