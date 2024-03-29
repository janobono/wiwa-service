package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrder {

    ID("id", DataType.LONG),
    CREATOR("creator", DataType.STRING),
    CREATED("created", DataType.TIME_STAMP),
    MODIFIER("modifier", DataType.STRING),
    MODIFIED("modified", DataType.TIME_STAMP),
    STATUS("status", DataType.STRING),
    TOTAL("total", DataType.BIG_DECIMAL),
    NAME("name", DataType.STRING),
    DESCRIPTION("description", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrder(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrder metaColumnWiwaOrder : values()) {
            columnList.add(metaColumnWiwaOrder.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrder metaColumnWiwaOrder : values()) {
            columnList.add(metaColumnWiwaOrder.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
