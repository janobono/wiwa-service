package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrderAttribute {

    ORDER_ID("order_id", DataType.LONG),
    ATTRIBUTE_KEY("attribute_key", DataType.STRING),
    ATTRIBUTE_VALUE("attribute_value", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderAttribute(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ATTRIBUTE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_ATTRIBUTE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderAttribute metaColumnWiwaOrderAttribute : values()) {
            columnList.add(metaColumnWiwaOrderAttribute.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderAttribute metaColumnWiwaOrderAttribute : values()) {
            columnList.add(metaColumnWiwaOrderAttribute.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
