package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaUserAuthority {

    USER_ID("user_id", DataType.LONG),
    AUTHORITY_ID("authority_id", DataType.LONG);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaUserAuthority(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_USER_AUTHORITY.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_USER_AUTHORITY.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaUserAuthority metaColumnWiwaUserAuthority : values()) {
            columnList.add(metaColumnWiwaUserAuthority.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaUserAuthority metaColumnWiwaUserAuthority : values()) {
            columnList.add(metaColumnWiwaUserAuthority.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
