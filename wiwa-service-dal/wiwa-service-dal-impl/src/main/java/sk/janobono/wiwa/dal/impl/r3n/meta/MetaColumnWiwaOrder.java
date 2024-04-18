package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrder {

    ID("id", DataType.LONG),
    USER_ID("user_id", DataType.LONG),
    CREATED("created", DataType.TIME_STAMP),
    STATUS("status", DataType.STRING),
    ORDER_NUMBER("order_number", DataType.LONG),
    WEIGHT_VALUE("weight_value", DataType.BIG_DECIMAL),
    WEIGHT_UNIT("weight_unit", DataType.STRING),
    NET_WEIGHT_VALUE("net_weight_value", DataType.BIG_DECIMAL),
    NET_WEIGHT_UNIT("net_weight_unit", DataType.STRING),
    TOTAL_VALUE("total_value", DataType.BIG_DECIMAL),
    TOTAL_UNIT("total_unit", DataType.STRING);

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
