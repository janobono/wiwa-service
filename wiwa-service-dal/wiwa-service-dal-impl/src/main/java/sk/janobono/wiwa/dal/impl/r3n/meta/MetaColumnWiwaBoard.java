package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaBoard {

    ID("id", DataType.LONG),
    CODE("code", DataType.STRING),
    BOARD_CODE("board_code", DataType.STRING),
    STRUCTURE_CODE("structure_code", DataType.STRING),
    NAME("name", DataType.STRING),
    DESCRIPTION("description", DataType.STRING),
    ORIENTATION("orientation", DataType.BOOLEAN),
    WEIGHT("weight", DataType.BIG_DECIMAL),
    LENGTH("length", DataType.BIG_DECIMAL),
    WIDTH("width", DataType.BIG_DECIMAL),
    THICKNESS("thickness", DataType.BIG_DECIMAL),
    PRICE("price", DataType.BIG_DECIMAL);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaBoard(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_BOARD.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_BOARD.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaBoard metaColumnWiwaBoard : values()) {
            columnList.add(metaColumnWiwaBoard.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaBoard metaColumnWiwaBoard : values()) {
            columnList.add(metaColumnWiwaBoard.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
