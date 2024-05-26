package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderStatusDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderStatusDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderStatusDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderStatus;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderStatusRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderStatusRepositoryImpl implements OrderStatusRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderStatusDoMapper mapper;

    @Override
    public List<OrderStatusDo> findAllByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.columns())
                .FROM(MetaTable.WIWA_ORDER_STATUS.table())
                .WHERE(MetaColumnWiwaOrderStatus.ORDER_ID.column(), Condition.EQUALS, orderId)
                .ORDER_BY(MetaColumnWiwaOrderStatus.CREATED.column(), Order.ASC)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaCodeListItem.columns());
        return rows.stream()
                .map(WiwaOrderStatusDto::toObject)
                .map(mapper::toOrderStatusDo)
                .toList();
    }

    @Transactional
    @Override
    public OrderStatusDo save(final OrderStatusDo orderStatusDo) {
        log.debug("save({})", orderStatusDo);
        final WiwaOrderStatusDto wiwaOrderStatusDto;
        if (orderStatusDo.getId() == null) {
            wiwaOrderStatusDto = insert(mapper.toWiwaOrderStatusDto(orderStatusDo));
        } else {
            wiwaOrderStatusDto = update(mapper.toWiwaOrderStatusDto(orderStatusDo));
        }
        return mapper.toOrderStatusDo(wiwaOrderStatusDto);
    }

    private WiwaOrderStatusDto insert(final WiwaOrderStatusDto wiwaOrderStatusDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderStatus.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderStatusDto.toArray(wiwaOrderStatusDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_STATUS.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaOrderStatus.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaOrderStatusDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderStatusDto update(final WiwaOrderStatusDto wiwaOrderStatusDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderStatus.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderStatusDto.toArray(wiwaOrderStatusDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER_STATUS.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaOrderStatus.ID.column(), Condition.EQUALS, wiwaOrderStatusDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderStatusDto;
    }
}
