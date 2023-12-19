package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaProductImage {

    ID("id", DataType.LONG),
    PRODUCT_ID("product_id", DataType.LONG),
    FILE_NAME("file_name", DataType.STRING),
    FILE_TYPE("file_type", DataType.STRING),
    THUMBNAIL("thumbnail", DataType.BLOB),
    DATA("data", DataType.BLOB);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaProductImage(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_IMAGE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_IMAGE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductImage metaColumnWiwaProductImage : values()) {
            columnList.add(metaColumnWiwaProductImage.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductImage metaColumnWiwaProductImage : values()) {
            columnList.add(metaColumnWiwaProductImage.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
