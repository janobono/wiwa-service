package sk.janobono.wiwa.dal.impl.component;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import sk.r3n.sql.Column;
import sk.r3n.sql.Order;
import sk.r3n.sql.impl.ColumnFunction;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class CriteriaUtil {

    public Order mapDirection(final Sort.Order order) {
        return order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;
    }

    public ColumnFunction scDf(final String columnId, final Column column) {
        return new ColumnFunction(
                columnId,
                column.dataType(),
                "lower(unaccent({0}))",
                column
        );
    }

    public Object[] concat(final Object[] arrayOne, final Object[] arrayTwo) {
        return Stream.concat(Arrays.stream(arrayOne), Arrays.stream(arrayTwo)).toArray();
    }

    public Object[] removeFirst(final Object[] values, final int n) {
        return Arrays.stream(values).skip(n).toList().toArray();
    }

    public Column[] removeFirst(final Column[] columns, final int n) {
        return Arrays.stream(columns).skip(n).toList().toArray(new Column[columns.length - n]);
    }
}
