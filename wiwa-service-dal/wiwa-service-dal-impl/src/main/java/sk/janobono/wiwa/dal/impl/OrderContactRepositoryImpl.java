package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderContactDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderContactDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrder;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderContact;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.BaseOrderContactDo;
import sk.janobono.wiwa.dal.repository.OrderContactRepository;
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
public class OrderContactRepositoryImpl implements OrderContactRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderContactDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public Page<BaseOrderContactDo> findAllByUserId(final long userId, final Pageable pageable) {
        log.debug("findByUserId({})", userId);
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderContact.columns(), 1);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(columns).DISTINCT().COUNT()
                    .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                    .LEFT_JOIN(MetaTable.WIWA_ORDER.table(), MetaColumnWiwaOrder.ID.column(), MetaColumnWiwaOrderContact.ORDER_ID.column())
                    .WHERE(MetaColumnWiwaOrder.USER_ID.column(), Condition.EQUALS, userId);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
            if (totalRows > 0) {
                final Query.Select select = Query
                        .SELECT(columns).DISTINCT().page(pageable.getPageNumber(), pageable.getPageSize())
                        .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                        .LEFT_JOIN(MetaTable.WIWA_ORDER.table(), MetaColumnWiwaOrder.ID.column(), MetaColumnWiwaOrderContact.ORDER_ID.column())
                        .WHERE(MetaColumnWiwaOrder.USER_ID.column(), Condition.EQUALS, userId);

                if (pageable.isPaged()) {
                    select.page(pageable.getPageNumber(), pageable.getPageSize());
                }

                if (pageable.getSort().isSorted()) {
                    mapOrderBy(pageable, select);
                } else {
                    select.ORDER_BY(MetaColumnWiwaOrderContact.NAME.column(), Order.ASC);
                }

                final List<Object[]> rows = sqlBuilder.select(connection, select);

                final List<BaseOrderContactDo> content = rows.stream()
                        .map(row -> new BaseOrderContactDo(
                                (String) row[0],
                                (String) row[1],
                                (String) row[2],
                                (String) row[3],
                                (String) row[4],
                                (String) row[5],
                                (String) row[6],
                                (String) row[7],
                                (String) row[8]
                        ))
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderContactDo> findByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderContact.columns())
                            .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                            .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, orderId)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderContactDto::toObject)
                    .map(mapper::toOrderContactDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderContactDo save(final OrderContactDo orderContactDo) {
        log.debug("save({})", orderContactDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderContactDto result;
            if (countByOrderId(connection, orderContactDo.getOrderId()) == 0) {
                result = insert(connection, mapper.toWiwaOrderContactDto(orderContactDo));
            } else {
                result = update(connection, mapper.toWiwaOrderContactDto(orderContactDo));
            }
            return mapper.toOrderContactDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByOrderId(final Connection connection, final long orderId) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaOrderContact.ORDER_ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                        .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, orderId)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaOrderContactDto insert(final Connection connection, final WiwaOrderContactDto wiwaOrderContactDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_CONTACT.table(), MetaColumnWiwaOrderContact.columns())
                        .VALUES(WiwaOrderContactDto.toArray(wiwaOrderContactDto)));

        return wiwaOrderContactDto;
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "orderId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.ORDER_ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "street" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.STREET.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "zipCode" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.ZIP_CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "city" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.CITY.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "state" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.STATE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "phone" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.PHONE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "email" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.EMAIL.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "businessId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.BUSINESS_ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "taxId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.TAX_ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaOrderContactDto update(final Connection connection, final WiwaOrderContactDto wiwaOrderContactDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderContact.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderContactDto.toArray(wiwaOrderContactDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_CONTACT.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, wiwaOrderContactDto.orderId())
        );

        return wiwaOrderContactDto;
    }
}
