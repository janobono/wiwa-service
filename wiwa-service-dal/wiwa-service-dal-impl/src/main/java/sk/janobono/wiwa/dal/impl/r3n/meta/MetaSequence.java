package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    WIWA_AUTHORITY_ID_SEQ("wiwa_authority_id_seq"),
    WIWA_USER_ID_SEQ("wiwa_user_id_seq"),
    WIWA_CODE_LIST_ID_SEQ("wiwa_code_list_id_seq"),
    WIWA_CODE_LIST_ITEM_ID_SEQ("wiwa_code_list_item_id_seq"),
    WIWA_BOARD_ID_SEQ("wiwa_board_id_seq"),
    WIWA_EDGE_ID_SEQ("wiwa_edge_id_seq"),
    WIWA_ORDER_ID_SEQ("wiwa_order_id_seq"),
    WIWA_ORDER_COMMENT_ID_SEQ("wiwa_order_comment_id_seq"),
    WIWA_ORDER_ITEM_ID_SEQ("wiwa_order_item_id_seq"),
    WIWA_ORDER_STATUS_ID_SEQ("wiwa_order_status_id_seq");

    private final String sequenceName;

    MetaSequence(final String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}
