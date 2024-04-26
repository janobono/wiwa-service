package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderItemSummary {

    ORDER_ITEM_ID("order_item_id", DataType.LONG),
    CODE("code", DataType.STRING),
    AMOUNT("amount", DataType.BIG_DECIMAL);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderItemSummary(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ITEM_SUMMARY.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ITEM_SUMMARY.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderItemSummary metaColumnWiwaOrderItemSummary : values()) {
            columnList.add(metaColumnWiwaOrderItemSummary.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderItemSummary metaColumnWiwaOrderItemSummary : values()) {
            columnList.add(metaColumnWiwaOrderItemSummary.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
