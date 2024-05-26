package sk.janobono.wiwa.dal.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlParam;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Order;
import sk.r3n.sql.impl.ColumnFunction;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class R3nUtil {

    private final SqlBuilder sqlBuilder;

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

    public DataType[] dataTypes(final Column[] columns) {
        return Stream.of(columns)
                .map(Column::dataType)
                .toArray(DataType[]::new);
    }

    public boolean exists(final JdbcTemplate jdbcTemplate, final Sql sql) {
        return count(jdbcTemplate, sql) > 0;
    }

    public int count(final JdbcTemplate jdbcTemplate, final Sql sql) {
        final Integer result = jdbcTemplate.query(
                sql.toSql(),
                (rs) -> rs.next() ? rs.getInt(1) : 0,
                sql.getParamsObjects());
        assert result != null;
        return result;
    }

    public List<Object[]> query(final JdbcTemplate jdbcTemplate, final Sql sql, final Column[] columns) {
        return jdbcTemplate.query(
                sql.toSql(),
                (rs, rowNum) -> sqlBuilder.getRow(rs, dataTypes(columns)),
                sql.getParamsObjects());
    }

    public Long insert(final JdbcTemplate jdbcTemplate, final Sql sql) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql.toSql(), Statement.RETURN_GENERATED_KEYS);
            sqlBuilder.setParams(ps, sql.getParams().toArray(new SqlParam[0]));
            return ps;
        }, keyHolder);
        final Long result = (Long) keyHolder.getKey();
        assert result != null;
        return result;
    }
}
