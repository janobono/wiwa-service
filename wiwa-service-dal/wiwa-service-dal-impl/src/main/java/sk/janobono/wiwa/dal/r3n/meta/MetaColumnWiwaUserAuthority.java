package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaUserAuthority {

    USER_ID("user_id", DataType.LONG),
    AUTHORITY_ID("authority_id", DataType.LONG);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaUserAuthority(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_USER_AUTHORITY.table());
    }

    public Column column(String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_USER_AUTHORITY.table(tableAlias));
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnWiwaUserAuthority metaColumnWiwaUserAuthority : values()) {
            columnList.add(metaColumnWiwaUserAuthority.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnWiwaUserAuthority metaColumnWiwaUserAuthority : values()) {
            columnList.add(metaColumnWiwaUserAuthority.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
