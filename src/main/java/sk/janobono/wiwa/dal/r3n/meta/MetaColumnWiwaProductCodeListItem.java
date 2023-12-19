package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaProductCodeListItem {

    PRODUCT_ID("product_id", DataType.LONG),
    CODE_LIST_ITEM_ID("code_list_item_id", DataType.LONG);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaProductCodeListItem(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_CODE_LIST_ITEM.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_PRODUCT_CODE_LIST_ITEM.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductCodeListItem metaColumnWiwaProductCodeListItem : values()) {
            columnList.add(metaColumnWiwaProductCodeListItem.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaProductCodeListItem metaColumnWiwaProductCodeListItem : values()) {
            columnList.add(metaColumnWiwaProductCodeListItem.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
