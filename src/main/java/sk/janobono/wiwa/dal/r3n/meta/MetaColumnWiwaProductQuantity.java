package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaProductQuantity {

    ID("id", DataType.LONG),
    PRODUCT_ID("product_id", DataType.LONG),
    KEY("key", DataType.STRING),
    VALUE("value", DataType.BIG_DECIMAL),
    UNIT("unit", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaProductQuantity(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_QUANTITY.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_QUANTITY.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductQuantity metaColumnWiwaProductQuantity : values()) {
            columnList.add(metaColumnWiwaProductQuantity.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductQuantity metaColumnWiwaProductQuantity : values()) {
            columnList.add(metaColumnWiwaProductQuantity.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
