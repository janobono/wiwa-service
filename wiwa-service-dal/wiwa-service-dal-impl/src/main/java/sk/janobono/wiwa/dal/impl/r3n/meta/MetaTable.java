package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    WIWA_USER("wiwa_user", "t1"),
    WIWA_USER_AUTHORITY("wiwa_user_authority", "t2"),
    WIWA_AUTHORITY("wiwa_authority", "t3"),
    WIWA_APPLICATION_IMAGE("wiwa_application_image", "t4"),
    WIWA_APPLICATION_PROPERTY("wiwa_application_property", "t5"),
    WIWA_CODE_LIST("wiwa_code_list", "t6"),
    WIWA_CODE_LIST_ITEM("wiwa_code_list_item", "t7"),
    WIWA_BOARD("wiwa_board", "t8"),
    WIWA_BOARD_IMAGE("wiwa_board_image", "t9"),
    WIWA_BOARD_CODE_LIST_ITEM("wiwa_board_code_list_item", "t10"),
    WIWA_EDGE("wiwa_edge", "t11"),
    WIWA_EDGE_IMAGE("wiwa_edge_image", "t12"),
    WIWA_EDGE_CODE_LIST_ITEM("wiwa_edge_code_list_item", "t13"),
    WIWA_ORDER_NUMBER("wiwa_order_number", "t14"),
    WIWA_ORDER("wiwa_order", "t15"),
    WIWA_ORDER_CONTACT("wiwa_order_contact", "t16"),
    WIWA_ORDER_COMMENT("wiwa_order_comment", "t17"),
    WIWA_ORDER_ITEM("wiwa_order_item", "t18"),
    WIWA_ORDER_STATUS("wiwa_order_status", "t19"),
    WIWA_ORDER_VIEW("wiwa_order_view", "t20");

    private final String tableName;

    private final String tableAlias;

    MetaTable(final String tableName, final String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public Table table() {
        return new Table(tableName, tableAlias);
    }

    public Table table(final String tableAlias) {
        return new Table(tableName, tableAlias);
    }
}
