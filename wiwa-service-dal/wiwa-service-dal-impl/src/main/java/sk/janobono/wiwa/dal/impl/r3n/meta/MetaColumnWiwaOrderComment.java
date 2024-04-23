package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderComment {

    ID("id", DataType.LONG),
    ORDER_ID("order_id", DataType.LONG),
    USER_ID("user_id", DataType.LONG),
    CREATED("created", DataType.TIME_STAMP),
    COMMENT("comment", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderComment(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_COMMENT.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_COMMENT.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderComment metaColumnWiwaOrderComment : values()) {
            columnList.add(metaColumnWiwaOrderComment.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderComment metaColumnWiwaOrderComment : values()) {
            columnList.add(metaColumnWiwaOrderComment.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
