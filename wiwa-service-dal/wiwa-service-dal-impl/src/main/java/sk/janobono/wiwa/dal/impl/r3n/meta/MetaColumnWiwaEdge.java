package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaEdge {

    ID("id", DataType.LONG),
    CODE("code", DataType.STRING),
    NAME("name", DataType.STRING),
    DESCRIPTION("description", DataType.STRING),
    WEIGHT("weight", DataType.BIG_DECIMAL),
    WIDTH("width", DataType.BIG_DECIMAL),
    THICKNESS("thickness", DataType.BIG_DECIMAL),
    PRICE("price", DataType.BIG_DECIMAL);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaEdge(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_EDGE.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_EDGE.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaEdge metaColumnWiwaEdge : values()) {
            columnList.add(metaColumnWiwaEdge.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaEdge metaColumnWiwaEdge : values()) {
            columnList.add(metaColumnWiwaEdge.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
