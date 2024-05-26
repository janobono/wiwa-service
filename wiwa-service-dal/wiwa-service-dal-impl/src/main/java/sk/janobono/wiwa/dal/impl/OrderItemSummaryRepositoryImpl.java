package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderItemSummaryDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemSummaryDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderItemSummary;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderItemSummaryRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderItemSummaryRepositoryImpl implements OrderItemSummaryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderItemSummaryDoMapper mapper;

    @Override
    public List<OrderItemSummaryDo> findAllByOrderItemId(final long orderItemId) {
        log.debug("findAllByOrderItemId({})", orderItemId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderItemSummary.columns())
                .FROM(MetaTable.WIWA_ORDER_ITEM_SUMMARY.table())
                .WHERE(MetaColumnWiwaOrderItemSummary.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
                .ORDER_BY(MetaColumnWiwaOrderItemSummary.CODE.column(), Order.ASC)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderItemSummary.columns());
        return rows.stream()
                .map(WiwaOrderItemSummaryDto::toObject)
                .map(mapper::toOrderItemSummaryDo)
                .toList();
    }

    @Transactional
    @Override
    public void saveAll(final long orderItemId, final List<OrderItemSummaryDo> batch) {
        log.debug("saveAll({},{})", orderItemId, batch);
        delete(orderItemId);
        for (final OrderItemSummaryDo orderItemSummaryDo : batch) {
            insert(mapper.toWiwaOrderItemSummaryDto(orderItemSummaryDo));
        }
    }

    private void delete(final long orderItemId) {
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_ORDER_ITEM_SUMMARY.table())
                .WHERE(MetaColumnWiwaOrderItemSummary.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    private void insert(final WiwaOrderItemSummaryDto wiwaOrderItemSummaryDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_ITEM_SUMMARY.table(), MetaColumnWiwaOrderItemSummary.columns())
                .VALUES(WiwaOrderItemSummaryDto.toArray(wiwaOrderItemSummaryDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }
}
