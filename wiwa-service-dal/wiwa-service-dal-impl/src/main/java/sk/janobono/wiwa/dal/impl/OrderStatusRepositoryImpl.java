package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderStatusDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderStatusDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderStatusDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderStatus;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderStatusRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
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
public class OrderStatusRepositoryImpl implements OrderStatusRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderStatusDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public List<OrderStatusDo> findAllByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.columns())
                            .FROM(MetaTable.WIWA_ORDER_STATUS.table())
                            .WHERE(MetaColumnWiwaOrderStatus.ORDER_ID.column(), Condition.EQUALS, orderId)
                            .ORDER_BY(MetaColumnWiwaOrderStatus.CREATED.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderStatusDto::toObject)
                    .map(mapper::toOrderStatusDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderStatusDo save(final OrderStatusDo orderStatusDo) {
        log.debug("save({})", orderStatusDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderStatusDto wiwaOrderStatusDto;
            if (orderStatusDo.getId() == null) {
                wiwaOrderStatusDto = insert(connection, mapper.toWiwaOrderStatusDto(orderStatusDo));
            } else {
                wiwaOrderStatusDto = update(connection, mapper.toWiwaOrderStatusDto(orderStatusDo));
            }
            return mapper.toOrderStatusDo(wiwaOrderStatusDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaOrderStatusDto insert(final Connection connection, final WiwaOrderStatusDto wiwaOrderStatusDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderStatus.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderStatusDto.toArray(wiwaOrderStatusDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_STATUS.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrderStatus.ID.column()));

        return WiwaOrderStatusDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderStatusDto update(final Connection connection, final WiwaOrderStatusDto wiwaOrderStatusDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderStatus.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderStatusDto.toArray(wiwaOrderStatusDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_STATUS.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderStatus.ID.column(), Condition.EQUALS, wiwaOrderStatusDto.id())
        );

        return wiwaOrderStatusDto;
    }
}
