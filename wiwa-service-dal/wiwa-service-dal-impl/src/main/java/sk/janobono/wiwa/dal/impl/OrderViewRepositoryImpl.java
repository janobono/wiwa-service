package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderViewDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderViewDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderView;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.OrderViewSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.OrderViewRepository;
import sk.janobono.wiwa.model.OrderStatus;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderViewRepositoryImpl implements OrderViewRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderViewDoMapper mapper;

    @Override
    public Optional<OrderViewDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderView.columns())
                .FROM(MetaTable.WIWA_ORDER_VIEW.table())
                .WHERE(MetaColumnWiwaOrderView.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderView.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderViewDto::toObject)
                .map(mapper::toOrderViewDo);
    }

    @Override
    public Page<OrderViewDo> findAll(final OrderViewSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        final Query.Select selectTotalRows = Query
                .SELECT(MetaColumnWiwaOrderView.ID.column())
                .COUNT()
                .FROM(MetaTable.WIWA_ORDER_VIEW.table());
        mapCriteria(criteria, selectTotalRows);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

        if (totalRows > 0) {
            final Query.Select select = Query.SELECT(MetaColumnWiwaOrderView.columns())
                    .FROM(MetaTable.WIWA_ORDER_VIEW.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaOrderView.ID.column(), Order.ASC);
            }

            mapCriteria(criteria, select);

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), MetaColumnWiwaOrderView.columns());

            final List<OrderViewDo> content = rows.stream()
                    .map(WiwaOrderViewDto::toObject)
                    .map(mapper::toOrderViewDo)
                    .toList();
            return new PageImpl<>(content, pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    private void mapCriteria(final OrderViewSearchCriteriaDo criteria, final Query.Select select) {
        // user ids
        if (Optional.ofNullable(criteria.userIds()).filter(s -> !s.isEmpty()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.USER_ID.column(), Condition.IN, criteria.userIds());
        }

        // created from
        if (Optional.ofNullable(criteria.createdFrom()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.CREATED.column(), Condition.EQUALS_MORE, criteria.createdFrom());
        }

        // created to
        if (Optional.ofNullable(criteria.createdTo()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.CREATED.column(), Condition.EQUALS_LESS, criteria.createdTo());
        }

        // delivery from
        if (Optional.ofNullable(criteria.deliveryFrom()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.DELIVERY.column(), Condition.EQUALS_MORE, criteria.deliveryFrom());
        }

        // delivery to
        if (Optional.ofNullable(criteria.deliveryTo()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.DELIVERY.column(), Condition.EQUALS_LESS, criteria.deliveryTo());
        }

        // statuses
        if (Optional.ofNullable(criteria.statuses()).filter(s -> !s.isEmpty()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.STATUS.column(), Condition.IN,
                    criteria.statuses().stream().map(OrderStatus::name).toList()
            );
        }

        // total from
        if (Optional.ofNullable(criteria.totalFrom()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.TOTAL.column(), Condition.EQUALS_MORE, criteria.totalFrom());
        }

        // total to
        if (Optional.ofNullable(criteria.totalTo()).isPresent()) {
            select.AND(MetaColumnWiwaOrderView.TOTAL.column(), Condition.EQUALS_LESS, criteria.totalTo());
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "userId" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.USER_ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "created" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.CREATED.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "orderNumber" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.ORDER_NUMBER.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "delivery" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.DELIVERY.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "packageType" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.PACKAGE_TYPE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "status" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.STATUS.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "weight" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.WEIGHT.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "total" -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.TOTAL.column(),
                                r3nUtil.mapDirection(order)
                        );
                        default -> select.ORDER_BY(
                                MetaColumnWiwaOrderView.ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }
}
