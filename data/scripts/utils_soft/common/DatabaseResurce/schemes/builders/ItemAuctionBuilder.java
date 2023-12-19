package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemAuctionResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemAuctionBuilder extends ResourceBuilder<ItemAuctionResource> {

    public ItemAuctionBuilder() {
        try {
            resourceClass = ItemAuctionResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemAuctionBuilder withAuctionId(Integer value) {
        with(ItemAuctionResource.AUCTION_ID, value);
        return this;
    }

    public ItemAuctionBuilder withInstanceId(Integer value) {
        with(ItemAuctionResource.INSTANCE_ID, value);
        return this;
    }

    public ItemAuctionBuilder withAuctionItemId(Integer value) {
        with(ItemAuctionResource.AUCTION_ITEM_ID, value);
        return this;
    }

    public ItemAuctionBuilder withStartingTime(Long value) {
        with(ItemAuctionResource.STARTING_TIME, value);
        return this;
    }

    public ItemAuctionBuilder withEndingTime(Long value) {
        with(ItemAuctionResource.ENDING_TIME, value);
        return this;
    }

    public ItemAuctionBuilder withAuctionStateId(Integer value) {
        with(ItemAuctionResource.AUCTION_STATE_ID, value);
        return this;
    }

}
