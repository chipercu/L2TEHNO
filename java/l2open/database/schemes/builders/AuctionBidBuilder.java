package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AuctionBidResource;

public class AuctionBidBuilder extends ResourceBuilder<AuctionBidResource> {

    public AuctionBidBuilder() {
        try {
            resourceClass = AuctionBidResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AuctionBidBuilder withId(Integer value) {
        with(AuctionBidResource.ID, value);
        return this;
    }

    public AuctionBidBuilder withAuctionId(Integer value) {
        with(AuctionBidResource.AUCTION_ID, value);
        return this;
    }

    public AuctionBidBuilder withBidderId(Integer value) {
        with(AuctionBidResource.BIDDER_ID, value);
        return this;
    }

    public AuctionBidBuilder withBidderName(String value) {
        with(AuctionBidResource.BIDDER_NAME, value);
        return this;
    }

    public AuctionBidBuilder withClanName(String value) {
        with(AuctionBidResource.CLAN_NAME, value);
        return this;
    }

    public AuctionBidBuilder withMaxBid(Long value) {
        with(AuctionBidResource.MAX_BID, value);
        return this;
    }

    public AuctionBidBuilder withTimeBid(Long value) {
        with(AuctionBidResource.TIME_BID, value);
        return this;
    }

}
