package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaTask {

    ID("id", DataType.LONG),
    CREATOR("creator", DataType.STRING),
    CREATED("created", DataType.TIME_STAMP),
    TYPE("type", DataType.STRING),
    STATUS("status", DataType.STRING),
    DATA("data", DataType.STRING),
    LOG("log", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaTask(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_TASK.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_TASK.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaTask metaColumnWiwaTask : values()) {
            columnList.add(metaColumnWiwaTask.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaTask metaColumnWiwaTask : values()) {
            columnList.add(metaColumnWiwaTask.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
