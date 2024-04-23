package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrder;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderRepository;
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
public class OrderRepositoryImpl implements OrderRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_ORDER.table())
                            .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Long> getOrderUserId(final long id) {
        log.debug("getOrderCreatorId({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrder.USER_ID.column())
                            .FROM(MetaTable.WIWA_ORDER.table())
                            .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Long) row[0]);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderDo> findById(final long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrder.columns())
                            .FROM(MetaTable.WIWA_ORDER.table())
                            .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderDto::toObject)
                    .map(mapper::toOrderDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderDo save(final OrderDo orderDo) {
        log.debug("save({})", orderDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderDto wiwaOrderDto;
            if (orderDo.getId() == null) {
                wiwaOrderDto = insert(connection, mapper.toWiwaOrderDto(orderDo));
            } else {
                wiwaOrderDto = update(connection, mapper.toWiwaOrderDto(orderDo));
            }
            return mapper.toOrderDo(wiwaOrderDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaOrderDto insert(final Connection connection, final WiwaOrderDto wiwaOrderDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrder.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderDto.toArray(wiwaOrderDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrder.ID.column()));

        return WiwaOrderDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderDto update(final Connection connection, final WiwaOrderDto wiwaOrderDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrder.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderDto.toArray(wiwaOrderDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, wiwaOrderDto.id())
        );

        return wiwaOrderDto;
    }
}
