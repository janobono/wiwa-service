package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaApplicationImage {

    FILE_NAME("file_name", DataType.STRING),
    FILE_TYPE("file_type", DataType.STRING),
    THUMBNAIL("thumbnail", DataType.BLOB),
    DATA("data", DataType.BLOB);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaApplicationImage(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_APPLICATION_IMAGE.table());
    }

    public Column column(String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_APPLICATION_IMAGE.table(tableAlias));
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnWiwaApplicationImage metaColumnWiwaApplicationImage : values()) {
            columnList.add(metaColumnWiwaApplicationImage.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnWiwaApplicationImage metaColumnWiwaApplicationImage : values()) {
            columnList.add(metaColumnWiwaApplicationImage.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
