package sk.janobono.wiwa.dal.impl.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaBoardCodeListItem {

    BOARD_ID("board_id", DataType.LONG),
    CODE_LIST_ITEM_ID("code_list_item_id", DataType.LONG);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaBoardCodeListItem(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_BOARD_CODE_LIST_ITEM.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_BOARD_CODE_LIST_ITEM.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaBoardCodeListItem metaColumnWiwaBoardCodeListItem : values()) {
            columnList.add(metaColumnWiwaBoardCodeListItem.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaBoardCodeListItem metaColumnWiwaBoardCodeListItem : values()) {
            columnList.add(metaColumnWiwaBoardCodeListItem.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
