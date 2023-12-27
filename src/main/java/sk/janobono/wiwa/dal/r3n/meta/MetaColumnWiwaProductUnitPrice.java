package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaProductUnitPrice {

    ID("id", DataType.LONG),
    PRODUCT_ID("product_id", DataType.LONG),
    UNIT_ID("unit_id", DataType.LONG),
    VALID_FROM("valid_from", DataType.DATE),
    VALID_TO("valid_to", DataType.DATE),
    VALUE("value", DataType.BIG_DECIMAL);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaProductUnitPrice(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_UNIT_PRICE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_UNIT_PRICE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductUnitPrice metaColumnWiwaProductUnitPrice : values()) {
            columnList.add(metaColumnWiwaProductUnitPrice.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductUnitPrice metaColumnWiwaProductUnitPrice : values()) {
            columnList.add(metaColumnWiwaProductUnitPrice.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
