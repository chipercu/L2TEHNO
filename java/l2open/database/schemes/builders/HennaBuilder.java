package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.HennaResource;

public class HennaBuilder extends ResourceBuilder<HennaResource> {

    public HennaBuilder() {
        try {
            resourceClass = HennaResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HennaBuilder withSymbolId(Integer value) {
        with(HennaResource.SYMBOL_ID, value);
        return this;
    }

    public HennaBuilder withSymbolName(String value) {
        with(HennaResource.SYMBOL_NAME, value);
        return this;
    }

    public HennaBuilder withDyeId(Integer value) {
        with(HennaResource.DYE_ID, value);
        return this;
    }

    public HennaBuilder withDyeAmount(Integer value) {
        with(HennaResource.DYE_AMOUNT, value);
        return this;
    }

    public HennaBuilder withPrice(Integer value) {
        with(HennaResource.PRICE, value);
        return this;
    }

    public HennaBuilder withStatINT(Integer value) {
        with(HennaResource.STAT__IN_T, value);
        return this;
    }

    public HennaBuilder withStatSTR(Integer value) {
        with(HennaResource.STAT__ST_R, value);
        return this;
    }

    public HennaBuilder withStatCON(Integer value) {
        with(HennaResource.STAT__CO_N, value);
        return this;
    }

    public HennaBuilder withStatMEM(Integer value) {
        with(HennaResource.STAT__ME_M, value);
        return this;
    }

    public HennaBuilder withStatDEX(Integer value) {
        with(HennaResource.STAT__DE_X, value);
        return this;
    }

    public HennaBuilder withStatWIT(Integer value) {
        with(HennaResource.STAT__WI_T, value);
        return this;
    }

}
