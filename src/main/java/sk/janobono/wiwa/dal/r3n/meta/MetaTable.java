package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    WIWA_PRODUCT_QUANTITY("wiwa_product_quantity", "t1"),
    WIWA_PRODUCT_UNIT_PRICE("wiwa_product_unit_price", "t2"),
    WIWA_USER("wiwa_user", "t3"),
    WIWA_USER_AUTHORITY("wiwa_user_authority", "t4"),
    WIWA_PRODUCT_CODE_LIST_ITEM("wiwa_product_code_list_item", "t5"),
    WIWA_USER_CODE_LIST_ITEM("wiwa_user_code_list_item", "t6"),
    WIWA_APPLICATION_IMAGE("wiwa_application_image", "t7"),
    WIWA_APPLICATION_PROPERTY("wiwa_application_property", "t8"),
    WIWA_CODE_LIST("wiwa_code_list", "t9"),
    WIWA_CODE_LIST_ITEM("wiwa_code_list_item", "t10"),
    WIWA_AUTHORITY("wiwa_authority", "t11"),
    WIWA_PRODUCT("wiwa_product", "t12"),
    WIWA_PRODUCT_ATTRIBUTE("wiwa_product_attribute", "t13"),
    WIWA_PRODUCT_IMAGE("wiwa_product_image", "t14");

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
