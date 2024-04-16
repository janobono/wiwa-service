package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaCodeList {

    ID("id", DataType.LONG),
    CODE("code", DataType.STRING),
    NAME("name", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaCodeList(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_CODE_LIST.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_CODE_LIST.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaCodeList metaColumnWiwaCodeList : values()) {
            columnList.add(metaColumnWiwaCodeList.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaCodeList metaColumnWiwaCodeList : values()) {
            columnList.add(metaColumnWiwaCodeList.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
