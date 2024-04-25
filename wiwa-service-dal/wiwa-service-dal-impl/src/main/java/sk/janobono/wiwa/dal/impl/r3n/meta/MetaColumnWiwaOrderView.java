package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderView {

    ID("id", DataType.LONG),
    USER_ID("user_id", DataType.LONG),
    CREATED("created", DataType.TIME_STAMP),
    ORDER_NUMBER("order_number", DataType.LONG),
    DELIVERY("delivery", DataType.DATE),
    PACKAGE_TYPE("package_type", DataType.STRING),
    STATUS("status", DataType.STRING),
    WEIGHT("weight", DataType.BIG_DECIMAL),
    TOTAL("total", DataType.BIG_DECIMAL);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderView(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_VIEW.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_VIEW.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderView metaColumnWiwaOrderView : values()) {
            columnList.add(metaColumnWiwaOrderView.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderView metaColumnWiwaOrderView : values()) {
            columnList.add(metaColumnWiwaOrderView.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
