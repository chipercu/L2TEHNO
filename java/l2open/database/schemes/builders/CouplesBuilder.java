package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CouplesResource;

public class CouplesBuilder extends ResourceBuilder<CouplesResource> {

    public CouplesBuilder() {
        try {
            resourceClass = CouplesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CouplesBuilder withId(Integer value) {
        with(CouplesResource.ID, value);
        return this;
    }

    public CouplesBuilder withPlayer1Id(Integer value) {
        with(CouplesResource.PLAYER1_ID, value);
        return this;
    }

    public CouplesBuilder withPlayer2Id(Integer value) {
        with(CouplesResource.PLAYER2_ID, value);
        return this;
    }

    public CouplesBuilder withMaried(String value) {
        with(CouplesResource.MARIED, value);
        return this;
    }

    public CouplesBuilder withAffiancedDate(Long value) {
        with(CouplesResource.AFFIANCED_DATE, value);
        return this;
    }

    public CouplesBuilder withWeddingDate(Long value) {
        with(CouplesResource.WEDDING_DATE, value);
        return this;
    }

}
