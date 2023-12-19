package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemAuctionBidResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemAuctionBidBuilder extends ResourceBuilder<ItemAuctionBidResource> {

    public ItemAuctionBidBuilder() {
        try {
            resourceClass = ItemAuctionBidResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemAuctionBidBuilder withAuctionId(Integer value) {
        with(ItemAuctionBidResource.AUCTION_ID, value);
        return this;
    }

    public ItemAuctionBidBuilder withPlayerObjId(Integer value) {
        with(ItemAuctionBidResource.PLAYER_OBJ_ID, value);
        return this;
    }

    public ItemAuctionBidBuilder withPlayerBid(Long value) {
        with(ItemAuctionBidResource.PLAYER_BID, value);
        return this;
    }

}
