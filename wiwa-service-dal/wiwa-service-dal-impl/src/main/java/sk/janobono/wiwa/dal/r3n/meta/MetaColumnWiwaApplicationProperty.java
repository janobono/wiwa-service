package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaApplicationProperty {

    PROPERTY_GROUP("property_group", DataType.STRING),
    PROPERTY_KEY("property_key", DataType.STRING),
    PROPERTY_LANGUAGE("property_language", DataType.STRING),
    PROPERTY_VALUE("property_value", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaApplicationProperty(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_APPLICATION_PROPERTY.table());
    }

    public Column column(String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_APPLICATION_PROPERTY.table(tableAlias));
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnWiwaApplicationProperty metaColumnWiwaApplicationProperty : values()) {
            columnList.add(metaColumnWiwaApplicationProperty.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnWiwaApplicationProperty metaColumnWiwaApplicationProperty : values()) {
            columnList.add(metaColumnWiwaApplicationProperty.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
