package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.model.OrderSearchCriteriaDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaOrder;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.OrderDoMapper;
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
public class OrderRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderDoMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    public void deleteById(final Long id) {
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

    public boolean existsById(final Long id) {
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
                final Query.Select select = Query
                        .SELECT(MetaColumnWiwaOrder.columns())
                        .FROM(MetaTable.WIWA_ORDER.table());

                if (pageable.isPaged()) {
                    select.page(pageable.getPageNumber(), pageable.getPageSize());
                }

                if (pageable.getSort().isSorted()) {
                    mapOrderBy(pageable, select);
                } else {
                    select.ORDER_BY(MetaColumnWiwaOrder.NAME.column(), Order.ASC);
                }

                mapCriteria(criteria, select);
                final List<Object[]> rows = sqlBuilder.select(connection, select);
                final List<OrderDo> content = rows.stream()
                        .map(WiwaOrderDto::toObject)
                        .map(mapper::toOrderDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<OrderDo> findById(final Long id) {
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

    public OrderDo save(final OrderDo codeListDo) {
        log.debug("save({})", codeListDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderDto result;
            if (codeListDo.getId() == null) {
                result = insert(connection, mapper.toWiwaOrderDto(codeListDo));
            } else {
                result = update(connection, mapper.toWiwaOrderDto(codeListDo));
            }
            return mapper.toOrderDo(result);
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
        // creator
        if (Optional.ofNullable(criteria.creator()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.CREATOR.column(), Condition.EQUALS, criteria.creator());
        }

        // status
        if (Optional.ofNullable(criteria.status()).isPresent()) {
            select.AND(MetaColumnWiwaOrder.STATUS.column(), Condition.EQUALS, criteria.status().name());
        }

        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaOrder.NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "creator" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.CREATOR.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "created" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.CREATED.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "modifier" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.MODIFIER.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "modified" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.MODIFIED.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "status" -> select.ORDER_BY(
                                MetaColumnWiwaOrder.STATUS.column(),
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
