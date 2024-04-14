package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    WIWA_AUTHORITY_ID_SEQ("wiwa_authority_id_seq"),
    WIWA_USER_ID_SEQ("wiwa_user_id_seq"),
    WIWA_CODE_LIST_ID_SEQ("wiwa_code_list_id_seq"),
    WIWA_CODE_LIST_ITEM_ID_SEQ("wiwa_code_list_item_id_seq"),
    WIWA_BOARD_ID_SEQ("wiwa_board_id_seq"),
    WIWA_BOARD_IMAGE_ID_SEQ("wiwa_board_image_id_seq"),
    WIWA_EDGE_ID_SEQ("wiwa_edge_id_seq"),
    WIWA_EDGE_IMAGE_ID_SEQ("wiwa_edge_image_id_seq"),
    WIWA_ORDER_NUMBER_ORDER_NUMBER_SEQ("wiwa_order_number_order_number_seq"),
    WIWA_ORDER_ID_SEQ("wiwa_order_id_seq"),
    WIWA_ORDER_ORDER_NUMBER_SEQ("wiwa_order_order_number_seq"),
    WIWA_ORDER_CONTACT_ID_SEQ("wiwa_order_contact_id_seq"),
    WIWA_ORDER_ITEM_ID_SEQ("wiwa_order_item_id_seq");

    private final String sequenceName;

    MetaSequence(final String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}
