package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaProduct {

    ID("id", DataType.LONG),
    CODE("code", DataType.STRING),
    NAME("name", DataType.STRING),
    DESCRIPTION("description", DataType.STRING),
    STOCK_STATUS("stock_status", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaProduct(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProduct metaColumnWiwaProduct : values()) {
            columnList.add(metaColumnWiwaProduct.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProduct metaColumnWiwaProduct : values()) {
            columnList.add(metaColumnWiwaProduct.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
