package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderSummaryViewDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderSummaryViewDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderSummaryView;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderSummaryViewRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderSummaryViewRepositoryImpl implements OrderSummaryViewRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderSummaryViewDoMapper mapper;

    @Override
    public List<OrderSummaryViewDo> findAllById(final long id) {
        log.debug("findAllById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderSummaryView.columns())
                .FROM(MetaTable.WIWA_ORDER_SUMMARY_VIEW.table())
                .WHERE(MetaColumnWiwaOrderSummaryView.ID.column(), Condition.EQUALS, id)
                .ORDER_BY(MetaColumnWiwaOrderSummaryView.CODE.column(), Order.ASC)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderSummaryView.columns());
        return rows.stream()
                .map(WiwaOrderSummaryViewDto::toObject)
                .map(mapper::toOrderSummaryViewDo)
                .toList();
    }
}
