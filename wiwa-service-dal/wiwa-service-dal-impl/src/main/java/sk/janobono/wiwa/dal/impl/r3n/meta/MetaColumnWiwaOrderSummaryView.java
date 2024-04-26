package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.LinkedList;
import java.util.List;

public enum MetaColumnWiwaOrderSummaryView {

    ID("id", DataType.LONG),
    CODE("code", DataType.STRING),
    AMOUNT("amount", DataType.BIG_DECIMAL);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderSummaryView(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_SUMMARY_VIEW.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_SUMMARY_VIEW.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderSummaryView metaColumnWiwaOrderSummaryView : values()) {
            columnList.add(metaColumnWiwaOrderSummaryView.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new LinkedList<>();
        for (final MetaColumnWiwaOrderSummaryView metaColumnWiwaOrderSummaryView : values()) {
            columnList.add(metaColumnWiwaOrderSummaryView.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
