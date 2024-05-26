package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderNumberDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderNumber;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderNumberRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderNumberRepositoryImpl implements OrderNumberRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;

    @Transactional
    @Override
    public long getNextOrderNumber(final long userId) {
        log.debug("getNextOrderNumber({})", userId);
        final var saved = findByUserId(userId);
        if (saved.isPresent()) {
            return update(new WiwaOrderNumberDto(userId, saved.get().orderNumber() + 1)).orderNumber();
        } else {
            return insert(new WiwaOrderNumberDto(userId, 1L)).orderNumber();
        }
    }

    private Optional<WiwaOrderNumberDto> findByUserId(final Long userId) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderNumber.columns())
                .FROM(MetaTable.WIWA_ORDER_NUMBER.table())
                .WHERE(MetaColumnWiwaOrderNumber.USER_ID.column(), Condition.EQUALS, userId)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderNumber.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderNumberDto::toObject);
    }

    private WiwaOrderNumberDto insert(final WiwaOrderNumberDto wiwaOrderNumberDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_NUMBER.table(), MetaColumnWiwaOrderNumber.columns())
                .VALUES(WiwaOrderNumberDto.toArray(wiwaOrderNumberDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderNumberDto;
    }

    private WiwaOrderNumberDto update(final WiwaOrderNumberDto wiwaOrderNumberDto) {
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER_NUMBER.table())
                .SET(MetaColumnWiwaOrderNumber.ORDER_NUMBER.column(), wiwaOrderNumberDto.orderNumber())
                .WHERE(MetaColumnWiwaOrderNumber.USER_ID.column(), Condition.EQUALS, wiwaOrderNumberDto.userId())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderNumberDto;
    }
}
