package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrderItemData {

    ID("id", DataType.LONG),
    ORDER_ITEM_ID("order_item_id", DataType.LONG),
    KEY("key", DataType.STRING),
    DATA("data", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderItemData(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ITEM_DATA.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ITEM_DATA.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderItemData metaColumnWiwaOrderItemData : values()) {
            columnList.add(metaColumnWiwaOrderItemData.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderItemData metaColumnWiwaOrderItemData : values()) {
            columnList.add(metaColumnWiwaOrderItemData.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
