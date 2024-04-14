package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderItemDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaOrderItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.OrderItemDoMapper;
import sk.r3n.jdbc.SqlBuilder;
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
public class OrderItemRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderItemDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public void deleteById(final Long id) {
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

    public boolean existsById(final Long id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                            .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .map(i -> i > 0)
                    .orElse(false);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<OrderItemDo> findAllByOrderId(final Long orderId) {
        log.debug("findAllByOrderId({})", orderId);
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

    public Optional<OrderItemDo> findById(final Long id) {
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

    public OrderItemDo save(final OrderItemDo orderItemDo) {
        log.debug("save({})", orderItemDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderItemDto wiwaOrderItemDto;
            if (orderItemDo.getId() == null) {
                wiwaOrderItemDto = insert(connection, mapper.toWiwaOrderItemDto(orderItemDo));
            } else {
                wiwaOrderItemDto = update(connection, mapper.toWiwaOrderItemDto(orderItemDo));
            }
            return mapper.toOrderItemDo(wiwaOrderItemDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaOrderItemDto insert(final Connection connection, final WiwaOrderItemDto wiwaOrderItemDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderItem.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderItemDto.toArray(wiwaOrderItemDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_ITEM.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrderItem.ID.column()));

        return WiwaOrderItemDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
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
