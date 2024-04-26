package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.impl.mapper.OrderItemSummaryDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemSummaryDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderItemSummary;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderItemSummaryRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderItemSummaryRepositoryImpl implements OrderItemSummaryRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderItemSummaryDoMapper mapper;

    @Override
    public List<OrderItemSummaryDo> findAllByOrderItemId(final long orderItemId) {
        log.debug("findAllByOrderItemId({})", orderItemId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItemSummary.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM_SUMMARY.table())
                            .WHERE(MetaColumnWiwaOrderItemSummary.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
                            .ORDER_BY(MetaColumnWiwaOrderItemSummary.CODE.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderItemSummaryDto::toObject)
                    .map(mapper::toOrderItemSummaryDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAll(final long orderItemId, final List<OrderItemSummaryDo> batch) {
        log.debug("saveAll({},{})", orderItemId, batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            delete(connection, orderItemId);
            for (final OrderItemSummaryDo orderItemSummaryDo : batch) {
                insert(connection, mapper.toWiwaOrderItemSummaryDto(orderItemSummaryDo));
            }

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void delete(final Connection connection, final long orderItemId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_ORDER_ITEM_SUMMARY.table())
                        .WHERE(MetaColumnWiwaOrderItemSummary.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
        );
    }

    private void insert(final Connection connection, final WiwaOrderItemSummaryDto wiwaOrderItemSummaryDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_ITEM_SUMMARY.table(), MetaColumnWiwaOrderItemSummary.columns())
                        .VALUES(WiwaOrderItemSummaryDto.toArray(wiwaOrderItemSummaryDto))
        );
    }
}
