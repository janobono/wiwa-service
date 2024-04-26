package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderMaterial {

    ORDER_ID("order_id", DataType.LONG),
    MATERIAL_ID("material_id", DataType.LONG),
    CODE("code", DataType.STRING),
    DATA("data", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderMaterial(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_MATERIAL.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_MATERIAL.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderMaterial metaColumnWiwaOrderMaterial : values()) {
            columnList.add(metaColumnWiwaOrderMaterial.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderMaterial metaColumnWiwaOrderMaterial : values()) {
            columnList.add(metaColumnWiwaOrderMaterial.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
