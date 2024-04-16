package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrderItem {

    ID("id", DataType.LONG),
    ORDER_ID("order_id", DataType.LONG),
    NAME("name", DataType.STRING),
    SORT_NUM("sort_num", DataType.INTEGER),
    DESCRIPTION("description", DataType.STRING),
    WEIGHT_VALUE("weight_value", DataType.BIG_DECIMAL),
    WEIGHT_UNIT("weight_unit", DataType.STRING),
    NET_WEIGHT_VALUE("net_weight_value", DataType.BIG_DECIMAL),
    NET_WEIGHT_UNIT("net_weight_unit", DataType.STRING),
    PRICE_VALUE("price_value", DataType.BIG_DECIMAL),
    PRICE_UNIT("price_unit", DataType.STRING),
    AMOUNT_VALUE("amount_value", DataType.BIG_DECIMAL),
    AMOUNT_UNIT("amount_unit", DataType.STRING),
    TOTAL_VALUE("total_value", DataType.BIG_DECIMAL),
    TOTAL_UNIT("total_unit", DataType.STRING);

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
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderItem metaColumnWiwaOrderItem : values()) {
            columnList.add(metaColumnWiwaOrderItem.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderItem metaColumnWiwaOrderItem : values()) {
            columnList.add(metaColumnWiwaOrderItem.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
