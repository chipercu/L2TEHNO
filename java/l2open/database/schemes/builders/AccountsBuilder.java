package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AccountsResource;

public class AccountsBuilder extends ResourceBuilder<AccountsResource> {

    public AccountsBuilder() {
        try {
            resourceClass = AccountsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountsBuilder withLogin(String value) {
        with(AccountsResource.LOGIN, value);
        return this;
    }

    public AccountsBuilder withPassword(String value) {
        with(AccountsResource.PASSWORD, value);
        return this;
    }

    public AccountsBuilder withLastactive(Integer value) {
        with(AccountsResource.LASTACTIVE, value);
        return this;
    }

    public AccountsBuilder withAccessLevel(Integer value) {
        with(AccountsResource.ACCESS_LEVEL, value);
        return this;
    }

    public AccountsBuilder withLastIP(String value) {
        with(AccountsResource.LAST_IP, value);
        return this;
    }

    public AccountsBuilder withLastServer(Integer value) {
        with(AccountsResource.LAST_SERVER, value);
        return this;
    }

    public AccountsBuilder withComments(String value) {
        with(AccountsResource.COMMENTS, value);
        return this;
    }

    public AccountsBuilder withEmail(String value) {
        with(AccountsResource.EMAIL, value);
        return this;
    }

    public AccountsBuilder withPayStat(Integer value) {
        with(AccountsResource.PAY_STAT, value);
        return this;
    }

    public AccountsBuilder withBonus(Double value) {
        with(AccountsResource.BONUS, value);
        return this;
    }

    public AccountsBuilder withBonusExpire(Integer value) {
        with(AccountsResource.BONUS_EXPIRE, value);
        return this;
    }

    public AccountsBuilder withBanExpires(Integer value) {
        with(AccountsResource.BAN_EXPIRES, value);
        return this;
    }

    public AccountsBuilder withAllowIPs(String value) {
        with(AccountsResource.ALLOW_IPS, value);
        return this;
    }

    public AccountsBuilder withPoints(Integer value) {
        with(AccountsResource.POINTS, value);
        return this;
    }

    public AccountsBuilder withLockExpire(Integer value) {
        with(AccountsResource.LOCK_EXPIRE, value);
        return this;
    }

    public AccountsBuilder withActivated(Integer value) {
        with(AccountsResource.ACTIVATED, value);
        return this;
    }

    public AccountsBuilder withLastHwid(String value) {
        with(AccountsResource.LAST_HWID, value);
        return this;
    }

}
