package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClanDataResource;

import java.sql.Array;

public class ClanDataBuilder extends ResourceBuilder<ClanDataResource> {

    public ClanDataBuilder() {
        try {
            resourceClass = ClanDataResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanDataBuilder withClanId(Integer value) {
        with(ClanDataResource.CLAN_ID, value);
        return this;
    }

    public ClanDataBuilder withClanName(String value) {
        with(ClanDataResource.CLAN_NAME, value);
        return this;
    }

    public ClanDataBuilder withClanLevel(Integer value) {
        with(ClanDataResource.CLAN_LEVEL, value);
        return this;
    }

    public ClanDataBuilder withHasCastle(Integer value) {
        with(ClanDataResource.HAS_CASTLE, value);
        return this;
    }

    public ClanDataBuilder withHasFortress(Integer value) {
        with(ClanDataResource.HAS_FORTRESS, value);
        return this;
    }

    public ClanDataBuilder withHasHideout(Integer value) {
        with(ClanDataResource.HAS_HIDEOUT, value);
        return this;
    }

    public ClanDataBuilder withAllyId(Integer value) {
        with(ClanDataResource.ALLY_ID, value);
        return this;
    }

    public ClanDataBuilder withLeaderId(Integer value) {
        with(ClanDataResource.LEADER_ID, value);
        return this;
    }

    public ClanDataBuilder withCrest(Array value) {
        with(ClanDataResource.CREST, value);
        return this;
    }

    public ClanDataBuilder withLargecrest(Array value) {
        with(ClanDataResource.LARGECREST, value);
        return this;
    }

    public ClanDataBuilder withReputationScore(Integer value) {
        with(ClanDataResource.REPUTATION_SCORE, value);
        return this;
    }

    public ClanDataBuilder withWarehouse(Integer value) {
        with(ClanDataResource.WAREHOUSE, value);
        return this;
    }

    public ClanDataBuilder withExpelledMember(Integer value) {
        with(ClanDataResource.EXPELLED_MEMBER, value);
        return this;
    }

    public ClanDataBuilder withLeavedAlly(Integer value) {
        with(ClanDataResource.LEAVED_ALLY, value);
        return this;
    }

    public ClanDataBuilder withDissolvedAlly(Integer value) {
        with(ClanDataResource.DISSOLVED_ALLY, value);
        return this;
    }

    public ClanDataBuilder withAuctionBidAt(Integer value) {
        with(ClanDataResource.AUCTION_BID_AT, value);
        return this;
    }

    public ClanDataBuilder withAirship(Integer value) {
        with(ClanDataResource.AIRSHIP, value);
        return this;
    }

    public ClanDataBuilder withPoint(Integer value) {
        with(ClanDataResource.POINT, value);
        return this;
    }

}
