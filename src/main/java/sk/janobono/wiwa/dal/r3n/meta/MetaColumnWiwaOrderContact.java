package sk.janobono.wiwa.dal.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnWiwaOrderContact {

    ORDER_ID("order_id", DataType.LONG),
    NAME("name", DataType.STRING),
    STREET("street", DataType.STRING),
    ZIP_CODE("zip_code", DataType.STRING),
    CITY("city", DataType.STRING),
    STATE("state", DataType.STRING),
    PHONE("phone", DataType.STRING),
    EMAIL("email", DataType.STRING),
    BUSINESS_ID("business_id", DataType.STRING),
    TAX_ID("tax_id", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnWiwaOrderContact(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_CONTACT.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.WIWA_ORDER_CONTACT.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderContact metaColumnWiwaOrderContact : values()) {
            columnList.add(metaColumnWiwaOrderContact.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnWiwaOrderContact metaColumnWiwaOrderContact : values()) {
            columnList.add(metaColumnWiwaOrderContact.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
