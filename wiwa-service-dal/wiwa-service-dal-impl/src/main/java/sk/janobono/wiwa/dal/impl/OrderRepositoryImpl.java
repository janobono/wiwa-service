package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrder;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
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
    public boolean existsById(final long id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrder.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_ORDER.table())
                            .WHERE(MetaColumnWiwaOrder.ID.column(), Condition.EQUALS, id)
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

    @Override
    public Page<OrderDo> findAll(final OrderSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaOrder.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_ORDER.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
            if (totalRows > 0) {
                final Query.Select select = Query.SELECT(MetaColumnWiwaOrder.columns())
                        .FROM(MetaTable.WIWA_ORDER.table());

                if (pageable.isPaged()) {
                    select.page(pageable.getPageNumber(), pageable.getPageSize());
                }

                if (pageable.getSort().isSorted()) {
                    mapOrderBy(pageable, select);
                } else {
                    select.ORDER_BY(MetaColumnWiwaOrder.ID.column(), Order.DESC);
                }

                mapCriteria(criteria, select);

                final List<Object[]> rows = sqlBuilder.select(connection, select);
                final List<OrderDo> content = rows.stream()
                        .map(WiwaOrderDto::toObject)
                        .map(this::toOrderDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
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
                    .map(this::toOrderDo);
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
                wiwaOrderDto = insert(connection, toWiwaOrderDto(orderDo));
            } else {
                wiwaOrderDto = update(connection, toWiwaOrderDto(orderDo));
            }
            return toOrderDo(wiwaOrderDto);
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

    private void mapCriteria(final OrderSearchCriteriaDo criteria, final Query.Select select) {
        // user ids
        if (Optional.ofNullable(criteria.userIds()).filter(s -> !s.isEmpty()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.USER_ID.column(), Condition.IN, criteria.userIds());
        }

        // created from
        if (Optional.ofNullable(criteria.createdFrom()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.CREATED.column(), Condition.EQUALS_MORE, criteria.createdFrom());
        }

        // created to
        if (Optional.ofNullable(criteria.createdTo()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.CREATED.column(), Condition.EQUALS_LESS, criteria.createdTo());
        }

        // statuses
        if (Optional.ofNullable(criteria.statuses()).filter(s -> !s.isEmpty()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.STATUS.column(), Condition.IN,
                    criteria.statuses().stream().map(OrderStatus::name).toList()
            );
        }

        // total from
        if (Optional.ofNullable(criteria.totalFrom()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.TOTAL_VALUE.column(), Condition.EQUALS_MORE, criteria.totalFrom().amount())
                    .AND(MetaColumnWiwaOrder.TOTAL_UNIT.column(), Condition.EQUALS, criteria.totalFrom().currency().name());
        }

        // total to
        if (Optional.ofNullable(criteria.totalTo()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.TOTAL_VALUE.column(), Condition.EQUALS_LESS, criteria.totalTo().amount())
                    .AND(MetaColumnWiwaOrder.TOTAL_UNIT.column(), Condition.EQUALS, criteria.totalTo().currency().name());
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "userId" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.USER_ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "created" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.CREATED.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "status" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.STATUS.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightValue" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.NET_WEIGHT_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightUnit" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.NET_WEIGHT_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "totalValue" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.TOTAL_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "totalUnit" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.TOTAL_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private OrderDo toOrderDo(final WiwaOrderDto wiwaOrderDto) {
        return OrderDo.builder()
                .id(wiwaOrderDto.id())
                .userId(wiwaOrderDto.userId())
                .created(wiwaOrderDto.created())
                .status(OrderStatus.valueOf(wiwaOrderDto.status()))
                .orderNumber(wiwaOrderDto.orderNumber())
                .netWeight(new Quantity(wiwaOrderDto.netWeightValue(), Unit.valueOf(wiwaOrderDto.netWeightUnit())))
                .total(new Money(wiwaOrderDto.totalValue(), Unit.valueOf(wiwaOrderDto.totalUnit())))
                .deliveryDate(wiwaOrderDto.delivery())
                .ready(wiwaOrderDto.ready())
                .finished(wiwaOrderDto.finished())
                .build();
    }

    private WiwaOrderDto toWiwaOrderDto(final OrderDo orderDo) {
        return new WiwaOrderDto(
                orderDo.getId(),
                orderDo.getUserId(),
                orderDo.getCreated(),
                orderDo.getStatus().name(),
                orderDo.getOrderNumber(),
                orderDo.getNetWeight().quantity(),
                orderDo.getNetWeight().unit().name(),
                orderDo.getTotal().amount(),
                orderDo.getTotal().currency().name(),
                orderDo.getDeliveryDate(),
                orderDo.getReady(),
                orderDo.getFinished()
        );
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
