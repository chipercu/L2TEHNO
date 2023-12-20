package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.BbsNewsResource;

import java.sql.Date;

public class BbsNewsBuilder extends ResourceBuilder<BbsNewsResource> {

    public BbsNewsBuilder() {
        try {
            resourceClass = BbsNewsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BbsNewsBuilder withId(Integer value) {
        with(BbsNewsResource.ID, value);
        return this;
    }

    public BbsNewsBuilder withType(Integer value) {
        with(BbsNewsResource.TYPE, value);
        return this;
    }

    public BbsNewsBuilder withTitleRu(String value) {
        with(BbsNewsResource.TITLE_RU, value);
        return this;
    }

    public BbsNewsBuilder withTitleEn(String value) {
        with(BbsNewsResource.TITLE_EN, value);
        return this;
    }

    public BbsNewsBuilder withTextRu(String value) {
        with(BbsNewsResource.TEXT_RU, value);
        return this;
    }

    public BbsNewsBuilder withTextEn(String value) {
        with(BbsNewsResource.TEXT_EN, value);
        return this;
    }

    public BbsNewsBuilder withInfoRu(String value) {
        with(BbsNewsResource.INFO_RU, value);
        return this;
    }

    public BbsNewsBuilder withInfoEn(String value) {
        with(BbsNewsResource.INFO_EN, value);
        return this;
    }

    public BbsNewsBuilder withAuthor(String value) {
        with(BbsNewsResource.AUTHOR, value);
        return this;
    }

    public BbsNewsBuilder withDate(Date value) {
        with(BbsNewsResource._DATE, value);
        return this;
    }

}
