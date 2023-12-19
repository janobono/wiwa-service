package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaCodeListItem {

    ID("id", DataType.LONG),
    CODE_LIST_ID("code_list_id", DataType.LONG),
    PARENT_ID("parent_id", DataType.LONG),
    TREE_CODE("tree_code", DataType.STRING),
    CODE("code", DataType.STRING),
    VALUE("value", DataType.STRING),
    SORT_NUM("sort_num", DataType.INTEGER);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaCodeListItem(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_CODE_LIST_ITEM.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_CODE_LIST_ITEM.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaCodeListItem metaColumnWiwaCodeListItem : values()) {
            columnList.add(metaColumnWiwaCodeListItem.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaCodeListItem metaColumnWiwaCodeListItem : values()) {
            columnList.add(metaColumnWiwaCodeListItem.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
