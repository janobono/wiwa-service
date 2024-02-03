package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    WIWA_AUTHORITY_ID_SEQ("wiwa_authority_id_seq"),
    WIWA_CODE_LIST_ID_SEQ("wiwa_code_list_id_seq"),
    WIWA_CODE_LIST_ITEM_ID_SEQ("wiwa_code_list_item_id_seq"),
    WIWA_ORDER_ID_SEQ("wiwa_order_id_seq"),
    WIWA_ORDER_ITEM_ID_SEQ("wiwa_order_item_id_seq"),
    WIWA_ORDER_ITEM_ATTRIBUTE_ID_SEQ("wiwa_order_item_attribute_id_seq"),
    WIWA_PRODUCT_ID_SEQ("wiwa_product_id_seq"),
    WIWA_PRODUCT_ATTRIBUTE_ID_SEQ("wiwa_product_attribute_id_seq"),
    WIWA_PRODUCT_IMAGE_ID_SEQ("wiwa_product_image_id_seq"),
    WIWA_PRODUCT_QUANTITY_ID_SEQ("wiwa_product_quantity_id_seq"),
    WIWA_PRODUCT_UNIT_PRICE_ID_SEQ("wiwa_product_unit_price_id_seq"),
    WIWA_TASK_ID_SEQ("wiwa_task_id_seq"),
    WIWA_USER_ID_SEQ("wiwa_user_id_seq");

    private final String sequenceName;

    MetaSequence(final String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}
