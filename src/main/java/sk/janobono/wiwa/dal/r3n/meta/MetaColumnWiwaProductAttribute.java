package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaProductAttribute {

    ID("id", DataType.LONG),
    PRODUCT_ID("product_id", DataType.LONG),
    KEY("key", DataType.STRING),
    VALUE("value", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaProductAttribute(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_ATTRIBUTE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_ATTRIBUTE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductAttribute metaColumnWiwaProductAttribute : values()) {
            columnList.add(metaColumnWiwaProductAttribute.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductAttribute metaColumnWiwaProductAttribute : values()) {
            columnList.add(metaColumnWiwaProductAttribute.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
