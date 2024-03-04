package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    WIWA_APPLICATION_IMAGE("wiwa_application_image", "t1"),
    WIWA_APPLICATION_PROPERTY("wiwa_application_property", "t2"),
    WIWA_CODE_LIST("wiwa_code_list", "t3"),
    WIWA_CODE_LIST_ITEM("wiwa_code_list_item", "t4"),
    WIWA_ORDER("wiwa_order", "t5"),
    WIWA_ORDER_ITEM("wiwa_order_item", "t6"),
    WIWA_AUTHORITY("wiwa_authority", "t7"),
    WIWA_ORDER_ITEM_ATTRIBUTE("wiwa_order_item_attribute", "t8"),
    WIWA_PRODUCT("wiwa_product", "t9"),
    WIWA_PRODUCT_ATTRIBUTE("wiwa_product_attribute", "t10"),
    WIWA_PRODUCT_IMAGE("wiwa_product_image", "t11"),
    WIWA_PRODUCT_QUANTITY("wiwa_product_quantity", "t12"),
    WIWA_PRODUCT_UNIT_PRICE("wiwa_product_unit_price", "t13"),
    WIWA_TASK("wiwa_task", "t14"),
    WIWA_USER("wiwa_user", "t15"),
    WIWA_USER_AUTHORITY("wiwa_user_authority", "t16"),
    WIWA_PRODUCT_CODE_LIST_ITEM("wiwa_product_code_list_item", "t17");

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
