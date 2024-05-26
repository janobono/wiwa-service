package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderContactDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderContactDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrder;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderContact;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.BaseOrderContactDo;
import sk.janobono.wiwa.dal.repository.OrderContactRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderContactRepositoryImpl implements OrderContactRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderContactDoMapper mapper;

    @Override
    public Page<BaseOrderContactDo> findAllByUserId(final long userId, final Pageable pageable) {
        log.debug("findByUserId({})", userId);
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderContact.columns(), 1);

        final Query.Select selectTotalRows = Query
                .SELECT(columns).DISTINCT().COUNT()
                .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                .LEFT_JOIN(MetaTable.WIWA_ORDER.table(), MetaColumnWiwaOrder.ID.column(), MetaColumnWiwaOrderContact.ORDER_ID.column())
                .WHERE(MetaColumnWiwaOrder.USER_ID.column(), Condition.EQUALS, userId);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

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

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), columns);

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
    }

    @Override
    public Optional<OrderContactDo> findByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderContact.columns())
                .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, orderId)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderContact.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderContactDto::toObject)
                .map(mapper::toOrderContactDo);
    }

    @Transactional
    @Override
    public OrderContactDo save(final OrderContactDo orderContactDo) {
        log.debug("save({})", orderContactDo);
        final WiwaOrderContactDto result;
        if (countByOrderId(orderContactDo.getOrderId()) == 0) {
            result = insert(mapper.toWiwaOrderContactDto(orderContactDo));
        } else {
            result = update(mapper.toWiwaOrderContactDto(orderContactDo));
        }
        return mapper.toOrderContactDo(result);
    }

    private int countByOrderId(final long orderId) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderContact.ORDER_ID.column()).COUNT()
                .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, orderId)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    private WiwaOrderContactDto insert(final WiwaOrderContactDto wiwaOrderContactDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_CONTACT.table(), MetaColumnWiwaOrderContact.columns())
                .VALUES(WiwaOrderContactDto.toArray(wiwaOrderContactDto)));
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderContactDto;
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "orderId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.ORDER_ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "street" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.STREET.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "zipCode" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.ZIP_CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "city" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.CITY.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "state" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.STATE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "phone" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.PHONE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "email" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.EMAIL.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "businessId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.BUSINESS_ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "taxId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderContact.TAX_ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaOrderContactDto update(final WiwaOrderContactDto wiwaOrderContactDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderContact.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderContactDto.toArray(wiwaOrderContactDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER_CONTACT.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, wiwaOrderContactDto.orderId())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderContactDto;
    }
}
