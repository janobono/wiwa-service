package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderStatus {

    ID("id", DataType.LONG),
    ORDER_ID("order_id", DataType.LONG),
    USER_ID("user_id", DataType.LONG),
    CREATED("created", DataType.TIME_STAMP),
    STATUS("status", DataType.STRING),
    COMMENT("comment", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderStatus(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_STATUS.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_STATUS.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderStatus metaColumnWiwaOrderStatus : values()) {
            columnList.add(metaColumnWiwaOrderStatus.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderStatus metaColumnWiwaOrderStatus : values()) {
            columnList.add(metaColumnWiwaOrderStatus.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
