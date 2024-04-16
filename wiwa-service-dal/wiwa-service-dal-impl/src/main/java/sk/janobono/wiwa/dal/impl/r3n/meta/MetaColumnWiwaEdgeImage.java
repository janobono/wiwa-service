package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaEdgeImage {

    ID("id", DataType.LONG),
    EDGE_ID("edge_id", DataType.LONG),
    FILE_NAME("file_name", DataType.STRING),
    FILE_TYPE("file_type", DataType.STRING),
    THUMBNAIL("thumbnail", DataType.BLOB),
    DATA("data", DataType.BLOB);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaEdgeImage(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_EDGE_IMAGE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_EDGE_IMAGE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaEdgeImage metaColumnWiwaEdgeImage : values()) {
            columnList.add(metaColumnWiwaEdgeImage.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaEdgeImage metaColumnWiwaEdgeImage : values()) {
            columnList.add(metaColumnWiwaEdgeImage.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
