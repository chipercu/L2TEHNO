package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AuctionResource;

public class AuctionBuilder extends ResourceBuilder<AuctionResource> {

    public AuctionBuilder() {
        try {
            resourceClass = AuctionResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AuctionBuilder withId(Integer value) {
        with(AuctionResource.ID, value);
        return this;
    }

    public AuctionBuilder withSellerId(Integer value) {
        with(AuctionResource.SELLER_ID, value);
        return this;
    }

    public AuctionBuilder withSellerName(String value) {
        with(AuctionResource.SELLER_NAME, value);
        return this;
    }

    public AuctionBuilder withSellerClanName(String value) {
        with(AuctionResource.SELLER_CLAN_NAME, value);
        return this;
    }

    public AuctionBuilder withItemName(String value) {
        with(AuctionResource.ITEM_NAME, value);
        return this;
    }

    public AuctionBuilder withStartingBid(Long value) {
        with(AuctionResource.STARTING_BID, value);
        return this;
    }

    public AuctionBuilder withCurrentBid(Long value) {
        with(AuctionResource.CURRENT_BID, value);
        return this;
    }

    public AuctionBuilder withEndDate(Double value) {
        with(AuctionResource.END_DATE, value);
        return this;
    }

}
