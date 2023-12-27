package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaQuantityUnit {

    ID("id", DataType.LONG),
    TYPE("type", DataType.STRING),
    NAME("name", DataType.STRING),
    UNIT("unit", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaQuantityUnit(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_QUANTITY_UNIT.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_QUANTITY_UNIT.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaQuantityUnit metaColumnWiwaQuantityUnit : values()) {
            columnList.add(metaColumnWiwaQuantityUnit.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaQuantityUnit metaColumnWiwaQuantityUnit : values()) {
            columnList.add(metaColumnWiwaQuantityUnit.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
