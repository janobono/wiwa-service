package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    WIWA_AUTHORITY_ID_SEQ("wiwa_authority_id_seq"),
    WIWA_USER_ID_SEQ("wiwa_user_id_seq");

    private final String sequenceName;

    MetaSequence(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}
