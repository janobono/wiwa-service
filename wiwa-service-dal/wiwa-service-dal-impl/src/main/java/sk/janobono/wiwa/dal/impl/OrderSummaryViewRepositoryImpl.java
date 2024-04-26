package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderSummaryViewDo;
import sk.janobono.wiwa.dal.impl.mapper.OrderSummaryViewDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderSummaryViewDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderSummaryView;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderSummaryViewRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderSummaryViewRepositoryImpl implements OrderSummaryViewRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderSummaryViewDoMapper mapper;

    @Override
    public List<OrderSummaryViewDo> findAllById(final long id) {
        log.debug("findAllById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderSummaryView.columns())
                            .FROM(MetaTable.WIWA_ORDER_SUMMARY_VIEW.table())
                            .WHERE(MetaColumnWiwaOrderSummaryView.ID.column(), Condition.EQUALS, id)
                            .ORDER_BY(MetaColumnWiwaOrderSummaryView.CODE.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderSummaryViewDto::toObject)
                    .map(mapper::toOrderSummaryViewDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
