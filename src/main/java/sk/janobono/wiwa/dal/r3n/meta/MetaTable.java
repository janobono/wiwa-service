package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    WIWA_APPLICATION_IMAGE("wiwa_application_image", "t1"),
    WIWA_APPLICATION_PROPERTY("wiwa_application_property", "t2"),
    WIWA_AUTHORITY("wiwa_authority", "t3"),
    WIWA_QUANTITY_UNIT("wiwa_quantity_unit", "t4"),
    WIWA_CODE_LIST("wiwa_code_list", "t5"),
    WIWA_CODE_LIST_ITEM("wiwa_code_list_item", "t6"),
    WIWA_PRODUCT("wiwa_product", "t7"),
    WIWA_PRODUCT_ATTRIBUTE("wiwa_product_attribute", "t8"),
    WIWA_PRODUCT_IMAGE("wiwa_product_image", "t9"),
    WIWA_PRODUCT_QUANTITY("wiwa_product_quantity", "t10"),
    WIWA_PRODUCT_UNIT_PRICE("wiwa_product_unit_price", "t11"),
    WIWA_USER("wiwa_user", "t12"),
    WIWA_USER_AUTHORITY("wiwa_user_authority", "t13"),
    WIWA_PRODUCT_CODE_LIST_ITEM("wiwa_product_code_list_item", "t14"),
    WIWA_USER_CODE_LIST_ITEM("wiwa_user_code_list_item", "t15");

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
