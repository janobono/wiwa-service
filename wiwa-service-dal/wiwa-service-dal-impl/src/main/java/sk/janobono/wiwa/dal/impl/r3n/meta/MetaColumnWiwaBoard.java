package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaBoard {

    ID("id", DataType.LONG),
    CODE("code", DataType.STRING),
    BOARD_CODE("board_code", DataType.STRING),
    STRUCTURE_CODE("structure_code", DataType.STRING),
    NAME("name", DataType.STRING),
    DESCRIPTION("description", DataType.STRING),
    ORIENTATION("orientation", DataType.BOOLEAN),
    SALE_VALUE("sale_value", DataType.BIG_DECIMAL),
    SALE_UNIT("sale_unit", DataType.STRING),
    WEIGHT_VALUE("weight_value", DataType.BIG_DECIMAL),
    WEIGHT_UNIT("weight_unit", DataType.STRING),
    NET_WEIGHT_VALUE("net_weight_value", DataType.BIG_DECIMAL),
    NET_WEIGHT_UNIT("net_weight_unit", DataType.STRING),
    LENGTH_VALUE("length_value", DataType.BIG_DECIMAL),
    LENGTH_UNIT("length_unit", DataType.STRING),
    WIDTH_VALUE("width_value", DataType.BIG_DECIMAL),
    WIDTH_UNIT("width_unit", DataType.STRING),
    THICKNESS_VALUE("thickness_value", DataType.BIG_DECIMAL),
    THICKNESS_UNIT("thickness_unit", DataType.STRING),
    PRICE_VALUE("price_value", DataType.BIG_DECIMAL),
    PRICE_UNIT("price_unit", DataType.STRING);

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
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaBoard metaColumnWiwaBoard : values()) {
            columnList.add(metaColumnWiwaBoard.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaBoard metaColumnWiwaBoard : values()) {
            columnList.add(metaColumnWiwaBoard.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
