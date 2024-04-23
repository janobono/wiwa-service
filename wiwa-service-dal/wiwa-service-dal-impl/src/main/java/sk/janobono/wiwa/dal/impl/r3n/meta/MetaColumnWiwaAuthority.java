package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaAuthority {

    ID("id", DataType.LONG),
    AUTHORITY("authority", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaAuthority(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_AUTHORITY.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_AUTHORITY.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaAuthority metaColumnWiwaAuthority : values()) {
            columnList.add(metaColumnWiwaAuthority.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaAuthority metaColumnWiwaAuthority : values()) {
            columnList.add(metaColumnWiwaAuthority.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
