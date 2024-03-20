package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.FreeSaleProductViewDo;
import sk.janobono.wiwa.dal.model.FreeSaleProductSearchCriteriaDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaFreeSaleProductViewDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaFreeSaleProductView;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProductCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.FreeSaleProductViewDoMapper;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class FreeSaleProductViewRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final FreeSaleProductViewDoMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    public Page<FreeSaleProductViewDo> findAll(final String categoryItemCode, final FreeSaleProductSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query.SELECT(MetaColumnWiwaFreeSaleProductView.ID.column()).DISTINCT()
                    .COUNT()
                    .FROM(MetaTable.WIWA_FREE_SALE_PRODUCT_VIEW.table());
            mapCriteria(categoryItemCode, criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
            if (totalRows > 0) {
                final List<Object[]> rows;
                if (pageable.isPaged()) {
                    final Query.Select select = Query.SELECT(MetaColumnWiwaFreeSaleProductView.columns()).DISTINCT()
                            .page(pageable.getPageNumber(), pageable.getPageSize())
                            .FROM(MetaTable.WIWA_FREE_SALE_PRODUCT_VIEW.table());
                    mapCriteria(categoryItemCode, criteria, select);
                    mapOrderBy(pageable, select);
                    rows = sqlBuilder.select(connection, select);
                } else {
                    final Query.Select select = Query.SELECT(MetaColumnWiwaFreeSaleProductView.columns()).DISTINCT()
                            .FROM(MetaTable.WIWA_FREE_SALE_PRODUCT_VIEW.table())
                            .ORDER_BY(MetaColumnWiwaFreeSaleProductView.NAME.column(), Order.ASC);
                    mapCriteria(categoryItemCode, criteria, select);
                    rows = sqlBuilder.select(connection, select);
                }
                final List<FreeSaleProductViewDo> content = rows.stream()
                        .map(WiwaFreeSaleProductViewDto::toObject)
                        .map(mapper::toFreeSaleProductViewDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<FreeSaleProductViewDo> findByIdAndCategoryItemCode(final Long id, final String categoryItemCode) {
        log.debug("findByIdAndCategoryItemCode({},{})", id, categoryItemCode);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select select = Query.SELECT(MetaColumnWiwaFreeSaleProductView.columns()).DISTINCT()
                    .FROM(MetaTable.WIWA_FREE_SALE_PRODUCT_VIEW.table())
                    .WHERE(MetaColumnWiwaFreeSaleProductView.ID.column(), Condition.EQUALS, id);
            mapCategory(categoryItemCode, select);
            final List<Object[]> rows = sqlBuilder.select(connection, select);
            return rows.stream()
                    .findFirst()
                    .map(WiwaFreeSaleProductViewDto::toObject)
                    .map(mapper::toFreeSaleProductViewDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mapCriteria(final String categoryItemCode, final FreeSaleProductSearchCriteriaDo criteria, final Query.Select select) {
        // category item
        mapCategory(categoryItemCode, select);

        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaFreeSaleProductView.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaFreeSaleProductView.NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.CODE.column(), Condition.EQUALS, criteria.code());
        }

        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    criteriaUtil.scDf("NM", MetaColumnWiwaFreeSaleProductView.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }

        // stock status
        if (Optional.ofNullable(criteria.stockStatus()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.STOCK_STATUS.column(), Condition.EQUALS, criteria.stockStatus().name());
        }

        // length from
        if (Optional.ofNullable(criteria.lengthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.LENGTH_VALUE.column(), Condition.EQUALS_MORE, criteria.lengthFrom());
        }

        // length to
        if (Optional.ofNullable(criteria.lengthTo()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.LENGTH_VALUE.column(), Condition.EQUALS_LESS, criteria.lengthTo());
        }

        // length unit
        if (Optional.ofNullable(criteria.lengthUnit()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.LENGTH_UNIT.column(), Condition.EQUALS, criteria.lengthUnit().name());
        }

        // width from
        if (Optional.ofNullable(criteria.widthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.WIDTH_VALUE.column(), Condition.EQUALS_MORE, criteria.widthFrom());
        }

        // width to
        if (Optional.ofNullable(criteria.widthTo()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.WIDTH_VALUE.column(), Condition.EQUALS_LESS, criteria.widthTo());
        }

        // width unit
        if (Optional.ofNullable(criteria.widthUnit()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.WIDTH_UNIT.column(), Condition.EQUALS, criteria.widthUnit().name());
        }

        // thickness from
        if (Optional.ofNullable(criteria.thicknessFrom()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.THICKNESS_VALUE.column(), Condition.EQUALS_MORE, criteria.thicknessFrom());
        }

        // thickness to
        if (Optional.ofNullable(criteria.thicknessTo()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.THICKNESS_VALUE.column(), Condition.EQUALS_LESS, criteria.thicknessTo());
        }

        // thickness unit
        if (Optional.ofNullable(criteria.thicknessUnit()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.THICKNESS_UNIT.column(), Condition.EQUALS, criteria.thicknessUnit().name());
        }

        // price from
        if (Optional.ofNullable(criteria.priceFrom()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.PRICE_VALUE.column(), Condition.EQUALS_MORE, criteria.priceFrom());
        }

        // price to
        if (Optional.ofNullable(criteria.priceTo()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.PRICE_VALUE.column(), Condition.EQUALS_LESS, criteria.priceTo());
        }

        // price unit
        if (Optional.ofNullable(criteria.priceUnit()).isPresent()) {
            select.AND(MetaColumnWiwaFreeSaleProductView.PRICE_UNIT.column(), Condition.EQUALS, criteria.priceUnit().name());
        }

        // code list items
        if (Optional.ofNullable(criteria.codeListItems()).filter(l -> !l.isEmpty()).isPresent()) {
            int index = 0;
            for (final String code : criteria.codeListItems()) {
                final String alias = "CLIT" + index++;
                select.LEFT_JOIN(
                                MetaTable.WIWA_CODE_LIST_ITEM.table(alias),
                                MetaColumnWiwaCodeListItem.ID.column(alias),
                                MetaColumnWiwaProductCodeListItem.CODE_LIST_ITEM_ID.column()
                        ).AND_IN()
                        .OR(MetaColumnWiwaCodeListItem.CODE.column(alias), Condition.EQUALS, code)
                        .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(alias), Condition.LIKE, "%::" + code)
                        .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(alias), Condition.LIKE, "%::" + code + "::%")
                        .OUT();
            }
        }
    }

    private void mapCategory(final String categoryItemCode, final Query.Select select) {
        select.LEFT_JOIN(
                MetaTable.WIWA_PRODUCT_CODE_LIST_ITEM.table(),
                MetaColumnWiwaFreeSaleProductView.ID.column(),
                MetaColumnWiwaProductCodeListItem.PRODUCT_ID.column()
        );
        select.LEFT_JOIN(
                MetaTable.WIWA_CODE_LIST_ITEM.table(),
                MetaColumnWiwaCodeListItem.ID.column(),
                MetaColumnWiwaProductCodeListItem.CODE_LIST_ITEM_ID.column()
        );
        select.AND_IN()
                .OR(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, categoryItemCode)
                .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(), Condition.LIKE, "%::" + categoryItemCode)
                .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(), Condition.LIKE, "%::" + categoryItemCode + "::%")
                .OUT();
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "description" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.DESCRIPTION.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "stockStatus" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.STOCK_STATUS.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "saleValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.SALE_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "saleUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.SALE_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "weightValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.WEIGHT_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "weightUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.WEIGHT_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.NET_WEIGHT_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.NET_WEIGHT_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "lengthValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.LENGTH_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "lengthUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.LENGTH_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "widthValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.WIDTH_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "widthUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.WIDTH_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thicknessValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.THICKNESS_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thicknessUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.THICKNESS_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "priceValue" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.PRICE_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "priceUnit" -> select.ORDER_BY(
                                MetaColumnWiwaFreeSaleProductView.PRICE_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }
}
