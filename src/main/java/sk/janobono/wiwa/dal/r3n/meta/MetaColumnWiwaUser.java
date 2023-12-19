package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaUser {

    ID("id", DataType.LONG),
    USERNAME("username", DataType.STRING),
    PASSWORD("password", DataType.STRING),
    TITLE_BEFORE("title_before", DataType.STRING),
    FIRST_NAME("first_name", DataType.STRING),
    MID_NAME("mid_name", DataType.STRING),
    LAST_NAME("last_name", DataType.STRING),
    TITLE_AFTER("title_after", DataType.STRING),
    EMAIL("email", DataType.STRING),
    GDPR("gdpr", DataType.BOOLEAN),
    CONFIRMED("confirmed", DataType.BOOLEAN),
    ENABLED("enabled", DataType.BOOLEAN);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaUser(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_USER.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_USER.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaUser metaColumnWiwaUser : values()) {
            columnList.add(metaColumnWiwaUser.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaUser metaColumnWiwaUser : values()) {
            columnList.add(metaColumnWiwaUser.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
