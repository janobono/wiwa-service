package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    WIWA_APPLICATION_IMAGE("wiwa_application_image", "t1"),
    WIWA_APPLICATION_PROPERTY("wiwa_application_property", "t2"),
    WIWA_USER("wiwa_user", "t3"),
    WIWA_USER_AUTHORITY("wiwa_user_authority", "t4"),
    WIWA_AUTHORITY("wiwa_authority", "t5");

    private final String tableName;

    private final String tableAlias;

    MetaTable(String tableName, String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public Table table() {
        return new Table(tableName, tableAlias);
    }

    public Table table(String tableAlias) {
        return new Table(tableName, tableAlias);
    }
}
