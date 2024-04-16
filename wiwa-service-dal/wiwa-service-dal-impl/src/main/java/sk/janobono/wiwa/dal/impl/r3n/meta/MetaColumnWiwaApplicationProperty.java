package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaApplicationProperty {

    PROPERTY_KEY("property_key", DataType.STRING),
    PROPERTY_VALUE("property_value", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaApplicationProperty(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_APPLICATION_PROPERTY.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_APPLICATION_PROPERTY.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaApplicationProperty metaColumnWiwaApplicationProperty : values()) {
            columnList.add(metaColumnWiwaApplicationProperty.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaApplicationProperty metaColumnWiwaApplicationProperty : values()) {
            columnList.add(metaColumnWiwaApplicationProperty.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
