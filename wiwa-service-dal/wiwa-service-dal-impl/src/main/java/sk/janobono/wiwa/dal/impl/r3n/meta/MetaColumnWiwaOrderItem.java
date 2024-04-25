package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderItem {

    ID("id", DataType.LONG),
    ORDER_ID("order_id", DataType.LONG),
    SORT_NUM("sort_num", DataType.INTEGER),
    WEIGHT("weight", DataType.BIG_DECIMAL),
    TOTAL("total", DataType.BIG_DECIMAL),
    DATA("data", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderItem(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ITEM.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ITEM.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderItem metaColumnWiwaOrderItem : values()) {
            columnList.add(metaColumnWiwaOrderItem.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderItem metaColumnWiwaOrderItem : values()) {
            columnList.add(metaColumnWiwaOrderItem.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
