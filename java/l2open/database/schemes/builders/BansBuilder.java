package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.BansResource;

public class BansBuilder extends ResourceBuilder<BansResource> {

    public BansBuilder() {
        try {
            resourceClass = BansResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BansBuilder withAccountName(String value) {
        with(BansResource.ACCOUNT_NAME, value);
        return this;
    }

    public BansBuilder withObjId(Integer value) {
        with(BansResource.OBJ__ID, value);
        return this;
    }

    public BansBuilder withBaned(String value) {
        with(BansResource.BANED, value);
        return this;
    }

    public BansBuilder withUnban(String value) {
        with(BansResource.UNBAN, value);
        return this;
    }

    public BansBuilder withReason(String value) {
        with(BansResource.REASON, value);
        return this;
    }

    public BansBuilder withGM(String value) {
        with(BansResource.G_M, value);
        return this;
    }

    public BansBuilder withEndban(Integer value) {
        with(BansResource.ENDBAN, value);
        return this;
    }

    public BansBuilder withKarma(Integer value) {
        with(BansResource.KARMA, value);
        return this;
    }

}