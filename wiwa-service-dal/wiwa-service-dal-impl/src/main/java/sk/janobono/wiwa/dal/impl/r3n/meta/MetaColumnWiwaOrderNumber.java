package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrderNumber {

    USER_ID("user_id", DataType.LONG),
    ORDER_NUMBER("order_number", DataType.LONG);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderNumber(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_NUMBER.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_NUMBER.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderNumber metaColumnWiwaOrderNumber : values()) {
            columnList.add(metaColumnWiwaOrderNumber.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderNumber metaColumnWiwaOrderNumber : values()) {
            columnList.add(metaColumnWiwaOrderNumber.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
