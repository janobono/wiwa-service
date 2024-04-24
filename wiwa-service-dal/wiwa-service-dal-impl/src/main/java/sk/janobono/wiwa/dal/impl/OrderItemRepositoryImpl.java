package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderItemDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.OderItemSortNumDo;
import sk.janobono.wiwa.dal.model.OderItemSummaryDo;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderItemDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public int countByOrderId(final long orderId) {
        log.debug("countByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderItemDo> findById(final long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItem.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderItemDto::toObject)
                    .map(mapper::toOrderItemDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderItemDo> findByOrderIdAndSortNum(final long orderId, final int sortNum) {
        log.debug("findByOrderIdAndSortNum({},{})", orderId, sortNum);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItem.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
                            .AND(MetaColumnWiwaOrderItem.SORT_NUM.column(), Condition.EQUALS, sortNum)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderItemDto::toObject)
                    .map(mapper::toOrderItemDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OrderItemDo> findAllByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItem.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
                            .ORDER_BY(MetaColumnWiwaOrderItem.SORT_NUM.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderItemDto::toObject)
                    .map(mapper::toOrderItemDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderItemDo insert(final OrderItemDo orderItemDo) {
        log.debug("insert({})", orderItemDo);
        try (final Connection connection = dataSource.getConnection()) {
            final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderItem.columns(), 1);
            final Object[] values = criteriaUtil.removeFirst(WiwaOrderItemDto.toArray(mapper.toWiwaOrderItemDto(orderItemDo)), 1);

            final Long id = (Long) sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_ORDER_ITEM.table(), columns)
                            .VALUES(values).RETURNING(MetaColumnWiwaOrderItem.ID.column()));

            return mapper.toOrderItemDo(WiwaOrderItemDto.toObject(criteriaUtil.concat(new Object[]{id}, values)));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setSortNums(final List<OderItemSortNumDo> sortNums) {
        log.debug("setSortNums({})", sortNums);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            for (final OderItemSortNumDo oderItemSortNumDo : sortNums) {
                sqlBuilder.update(connection,
                        Query.UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                                .SET(MetaColumnWiwaOrderItem.SORT_NUM.column(), oderItemSortNumDo.sortNum())
                                .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, oderItemSortNumDo.id())
                );
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

    @Override
    public void setData(final long id, final String data) {
        log.debug("setData({},{})", id, data);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection,
                    Query.UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                            .SET(MetaColumnWiwaOrderItem.DATA.column(), data)
                            .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setName(final long id, final String name) {
        log.debug("setName({},{})", id, name);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection,
                    Query.UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                            .SET(MetaColumnWiwaOrderItem.NAME.column(), name)
                            .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setSummary(final long id, final OderItemSummaryDo oderItemSummary) {
        log.debug("setSummary({},{})", id, oderItemSummary);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection,
                    Query.UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                            .SET(MetaColumnWiwaOrderItem.PART_PRICE.column(), oderItemSummary.partPrice())
                            .SET(MetaColumnWiwaOrderItem.PART_NET_WEIGHT.column(), oderItemSummary.partNetWeight())
                            .SET(MetaColumnWiwaOrderItem.AMOUNT.column(), oderItemSummary.amount())
                            .SET(MetaColumnWiwaOrderItem.NET_WEIGHT.column(), oderItemSummary.netWeight())
                            .SET(MetaColumnWiwaOrderItem.TOTAL.column(), oderItemSummary.total())
                            .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
