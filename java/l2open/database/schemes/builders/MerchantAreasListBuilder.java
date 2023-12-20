package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.MerchantAreasListResource;

public class MerchantAreasListBuilder extends ResourceBuilder<MerchantAreasListResource> {

    public MerchantAreasListBuilder() {
        try {
            resourceClass = MerchantAreasListResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MerchantAreasListBuilder withMerchantAreaId(Integer value) {
        with(MerchantAreasListResource.MERCHANT_AREA_ID, value);
        return this;
    }

    public MerchantAreasListBuilder withMerchantAreaName(String value) {
        with(MerchantAreasListResource.MERCHANT_AREA_NAME, value);
        return this;
    }

    public MerchantAreasListBuilder withTax(Double value) {
        with(MerchantAreasListResource.TAX, value);
        return this;
    }

    public MerchantAreasListBuilder withChaotic(Integer value) {
        with(MerchantAreasListResource.CHAOTIC, value);
        return this;
    }

}
