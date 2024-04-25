package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaBoardImage {

    BOARD_ID("board_id", DataType.LONG),
    FILE_TYPE("file_type", DataType.STRING),
    DATA("data", DataType.BLOB);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaBoardImage(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_BOARD_IMAGE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_BOARD_IMAGE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaBoardImage metaColumnWiwaBoardImage : values()) {
            columnList.add(metaColumnWiwaBoardImage.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaBoardImage metaColumnWiwaBoardImage : values()) {
            columnList.add(metaColumnWiwaBoardImage.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
