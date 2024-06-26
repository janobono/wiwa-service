package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrder;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderDoMapper mapper;

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_ORDER.table())
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Optional<Long> getOrderUserId(final long id) {
        log.debug("getOrderCreatorId({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrder.USER_ID.column())
                .FROM(MetaTable.WIWA_ORDER.table())
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, new Column[]{MetaColumnWiwaOrder.USER_ID.column()});
        return rows.stream()
                .findFirst()
                .map(row -> (Long) row[0]);
    }

    @Override
    public Optional<OrderDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrder.columns())
                .FROM(MetaTable.WIWA_ORDER.table())
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrder.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderDto::toObject)
                .map(mapper::toOrderDo);
    }

    @Transactional
    @Override
    public OrderDo insert(final OrderDo orderDo) {
        log.debug("insert({})", orderDo);
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrder.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderDto.toArray(mapper.toWiwaOrderDto(orderDo)), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaOrder.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return mapper.toOrderDo(WiwaOrderDto.toObject(r3nUtil.concat(new Object[]{id}, values)));
    }

    @Transactional
    @Override
    public void setContact(final long id, final String setContact) {
        log.debug("setContact({},{})", id, setContact);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER.table())
                .SET(MetaColumnWiwaOrder.CONTACT.column(), setContact)
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Transactional
    @Override
    public void setDelivery(final long id, final LocalDate delivery) {
        log.debug("setDelivery({},{})", id, delivery);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER.table())
                .SET(MetaColumnWiwaOrder.DELIVERY.column(), delivery)
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Transactional
    @Override
    public void setPackageType(final long id, final OrderPackageType packageType) {
        log.debug("setPackageType({},{})", id, packageType);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER.table())
                .SET(MetaColumnWiwaOrder.PACKAGE_TYPE.column(), packageType.name())
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Transactional
    @Override
    public void setWeight(final long id, final BigDecimal weight) {
        log.debug("setWeight({},{})", id, weight);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER.table())
                .SET(MetaColumnWiwaOrder.WEIGHT.column(), weight)
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Transactional
    @Override
    public void setTotal(final long id, final BigDecimal total) {
        log.debug("setTotal({},{})", id, total);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER.table())
                .SET(MetaColumnWiwaOrder.TOTAL.column(), total)
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Transactional
    @Override
    public void setSummary(final long id, final String summary) {
        log.debug("setSummary({},{})", id, summary);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER.table())
                .SET(MetaColumnWiwaOrder.SUMMARY.column(), summary)
                .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }
}
