package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderNumberDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderNumber;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderNumberRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderNumberRepositoryImpl implements OrderNumberRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;

    @Override
    public long getNextOrderNumber(final long userId) {
        log.debug("getNextOrderNumber({})", userId);
        try (final Connection connection = dataSource.getConnection()) {
            final var saved = findByUserId(connection, userId);
            if (saved.isPresent()) {
                return update(connection, new WiwaOrderNumberDto(userId, saved.get().orderNumber() + 1)).orderNumber();
            } else {
                return insert(connection, new WiwaOrderNumberDto(userId, 1L)).orderNumber();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<WiwaOrderNumberDto> findByUserId(final Connection connection, final Long userId) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaOrderNumber.columns())
                        .FROM(MetaTable.WIWA_ORDER_NUMBER.table())
                        .WHERE(MetaColumnWiwaOrderNumber.USER_ID.column(), Condition.EQUALS, userId)
        );
        return rows.stream()
                .findFirst()
                .map(WiwaOrderNumberDto::toObject);
    }

    private WiwaOrderNumberDto insert(final Connection connection, final WiwaOrderNumberDto wiwaOrderNumberDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_NUMBER.table(), MetaColumnWiwaOrderNumber.columns())
                        .VALUES(WiwaOrderNumberDto.toArray(wiwaOrderNumberDto)));

        return wiwaOrderNumberDto;
    }

    private WiwaOrderNumberDto update(final Connection connection, final WiwaOrderNumberDto wiwaOrderNumberDto) throws SQLException {
        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_NUMBER.table())
                        .SET(MetaColumnWiwaOrderNumber.ORDER_NUMBER.column(), wiwaOrderNumberDto.orderNumber())
                        .WHERE(MetaColumnWiwaOrderNumber.USER_ID.column(), Condition.EQUALS, wiwaOrderNumberDto.userId())
        );

        return wiwaOrderNumberDto;
    }
}
