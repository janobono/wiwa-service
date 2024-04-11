package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    WIWA_APPLICATION_IMAGE("wiwa_application_image", "t1"),
    WIWA_APPLICATION_PROPERTY("wiwa_application_property", "t2"),
    WIWA_CODE_LIST("wiwa_code_list", "t3"),
    WIWA_CODE_LIST_ITEM("wiwa_code_list_item", "t4"),
    WIWA_ORDER("wiwa_order", "t5"),
    WIWA_ORDER_DATA("wiwa_order_data", "t6"),
    WIWA_AUTHORITY("wiwa_authority", "t7"),
    WIWA_ORDER_ITEM("wiwa_order_item", "t8"),
    WIWA_ORDER_ITEM_DATA("wiwa_order_item_data", "t9"),
    WIWA_PRODUCT("wiwa_product", "t10"),
    WIWA_PRODUCT_ATTRIBUTE("wiwa_product_attribute", "t11"),
    WIWA_PRODUCT_IMAGE("wiwa_product_image", "t12"),
    WIWA_PRODUCT_QUANTITY("wiwa_product_quantity", "t13"),
    WIWA_PRODUCT_UNIT_PRICE("wiwa_product_unit_price", "t14"),
    WIWA_TASK("wiwa_task", "t15"),
    WIWA_USER("wiwa_user", "t16"),
    WIWA_USER_AUTHORITY("wiwa_user_authority", "t17"),
    WIWA_PRODUCT_CODE_LIST_ITEM("wiwa_product_code_list_item", "t18"),
    WIWA_BOARD_PRODUCT_VIEW("wiwa_board_product_view", "t19"),
    WIWA_EDGE_PRODUCT_VIEW("wiwa_edge_product_view", "t20"),
    WIWA_FREE_SALE_PRODUCT_VIEW("wiwa_free_sale_product_view", "t21");

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
