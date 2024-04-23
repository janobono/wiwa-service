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
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Column;
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
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderItemDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

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
    public List<OrderItemDo> findAllByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItem.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
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
    public OrderItemDo save(final OrderItemDo orderItemDo) {
        log.debug("save({})", orderItemDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderItemDto result = save(connection, mapper.toWiwaOrderItemDto(orderItemDo));
            return mapper.toOrderItemDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAll(final List<OrderItemDo> batch) {
        log.debug("saveAll({})", batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            for (final OrderItemDo orderItemDo : batch) {
                save(connection, mapper.toWiwaOrderItemDto(orderItemDo));
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

    private WiwaOrderItemDto insert(final Connection connection, final WiwaOrderItemDto wiwaCodeListDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderItem.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderItemDto.toArray(wiwaCodeListDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_ITEM.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrderItem.ID.column()));

        return WiwaOrderItemDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderItemDto save(final Connection connection, final WiwaOrderItemDto wiwaOrderItemDto) throws SQLException {
        if (wiwaOrderItemDto.id() == null) {
            return insert(connection, wiwaOrderItemDto);
        } else {
            return update(connection, wiwaOrderItemDto);
        }
    }

    private WiwaOrderItemDto update(final Connection connection, final WiwaOrderItemDto wiwaOrderItemDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderItem.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderItemDto.toArray(wiwaOrderItemDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, wiwaOrderItemDto.id())
        );

        return wiwaOrderItemDto;
    }
}
