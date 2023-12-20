package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterMailResource;

public class CharacterMailBuilder extends ResourceBuilder<CharacterMailResource> {

    public CharacterMailBuilder() {
        try {
            resourceClass = CharacterMailResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterMailBuilder withObjId(Integer value) {
        with(CharacterMailResource.OBJ_ID, value);
        return this;
    }

    public CharacterMailBuilder withLetterId(Integer value) {
        with(CharacterMailResource.LETTER_ID, value);
        return this;
    }

    public CharacterMailBuilder withSenderId(Integer value) {
        with(CharacterMailResource.SENDER_ID, value);
        return this;
    }

    public CharacterMailBuilder withLocation(String value) {
        with(CharacterMailResource.LOCATION, value);
        return this;
    }

    public CharacterMailBuilder withRecipientNames(String value) {
        with(CharacterMailResource.RECIPIENT_NAMES, value);
        return this;
    }

    public CharacterMailBuilder withSubject(String value) {
        with(CharacterMailResource.SUBJECT, value);
        return this;
    }

    public CharacterMailBuilder withMessage(String value) {
        with(CharacterMailResource.MESSAGE, value);
        return this;
    }

    public CharacterMailBuilder withSendDate(Double value) {
        with(CharacterMailResource.SEND_DATE, value);
        return this;
    }

    public CharacterMailBuilder withDeleteDate(Double value) {
        with(CharacterMailResource.DELETE_DATE, value);
        return this;
    }

    public CharacterMailBuilder withUnread(String value) {
        with(CharacterMailResource.UNREAD, value);
        return this;
    }

}
