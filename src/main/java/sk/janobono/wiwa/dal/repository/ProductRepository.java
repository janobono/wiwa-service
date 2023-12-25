package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.business.model.product.ProductSearchCriteriaSo;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.ProductDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProduct;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProductCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.ProductMapper;
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
public class ProductRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ProductMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProduct.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_PRODUCT.table())
                            .WHERE(MetaColumnWiwaProduct.CODE.column(), Condition.EQUALS, code)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int countByIdNotAndCode(final Long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProduct.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_PRODUCT.table())
                            .WHERE(MetaColumnWiwaProduct.ID.column(), Condition.EQUALS_NOT, id)
                            .AND(MetaColumnWiwaProduct.CODE.column(), Condition.EQUALS, code)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(final Long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_PRODUCT.table())
                            .WHERE(MetaColumnWiwaProduct.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(final Long id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProduct.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_PRODUCT.table())
                            .WHERE(MetaColumnWiwaProduct.ID.column(), Condition.EQUALS, id)
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

    public Page<ProductDo> findAll(final ProductSearchCriteriaSo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaProduct.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_PRODUCT.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
            if (totalRows > 0) {
                final List<Object[]> rows;
                if (pageable.isPaged()) {
                    final Query.Select select = Query
                            .SELECT(MetaColumnWiwaProduct.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                            .FROM(MetaTable.WIWA_PRODUCT.table());
                    mapCriteria(criteria, select);
                    mapOrderBy(pageable, select);
                    rows = sqlBuilder.select(connection, select);
                } else {
                    final Query.Select select = Query.SELECT(MetaColumnWiwaProduct.columns())
                            .FROM(MetaTable.WIWA_PRODUCT.table())
                            .ORDER_BY(MetaColumnWiwaProduct.NAME.column(), Order.ASC);
                    mapCriteria(criteria, select);
                    rows = sqlBuilder.select(connection, select);
                }
                final List<ProductDo> content = rows.stream()
                        .map(WiwaProductDto::toObject)
                        .map(mapper::toProductDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ProductDo> findById(final Long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProduct.columns())
                            .FROM(MetaTable.WIWA_PRODUCT.table())
                            .WHERE(MetaColumnWiwaProduct.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaProductDto::toObject)
                    .map(mapper::toProductDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ProductDo save(final ProductDo productDo) {
        log.debug("save({})", productDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaProductDto wiwaProductDto;
            if (productDo.getId() == null) {
                wiwaProductDto = insert(connection, mapper.toWiwaProductDto(productDo));
            } else {
                wiwaProductDto = update(connection, mapper.toWiwaProductDto(productDo));
            }
            return mapper.toProductDo(wiwaProductDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaProductDto insert(final Connection connection, final WiwaProductDto wiwaProductDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProduct.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaProductDto.toArray(wiwaProductDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_PRODUCT.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaProduct.ID.column()));

        return WiwaProductDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final ProductSearchCriteriaSo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaProduct.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaProduct.NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaProduct.CODE.column(), Condition.EQUALS, criteria.code());
        }

        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    criteriaUtil.scDf("NM", MetaColumnWiwaProduct.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }

        // stock status
        if (Optional.ofNullable(criteria.stockStatus()).isPresent()) {
            select.AND(MetaColumnWiwaProduct.STOCK_STATUS.column(), Condition.EQUALS, criteria.stockStatus().name());
        }

        // code list items
        if (Optional.ofNullable(criteria.codeListItems()).filter(l -> !l.isEmpty()).isPresent()) {
            select.DISTINCT()
                    .LEFT_JOIN(MetaTable.WIWA_PRODUCT_CODE_LIST_ITEM.table(), MetaColumnWiwaProductCodeListItem.PRODUCT_ID.column(), MetaColumnWiwaProduct.ID.column());
            int index = 0;
            for (final String code : criteria.codeListItems()) {
                final String alias = "CLIT" + index++;
                select.LEFT_JOIN(
                                MetaTable.WIWA_CODE_LIST_ITEM.table(alias),
                                MetaColumnWiwaCodeListItem.ID.column(alias),
                                MetaColumnWiwaProductCodeListItem.CODE_LIST_ITEM_ID.column()
                        ).AND_IN()
                        .OR(MetaColumnWiwaCodeListItem.CODE.column(alias), Condition.EQUALS, code)
                        .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(alias), Condition.LIKE, "%" + code + "::%")
                        .OUT();
            }
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaProduct.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaProduct.CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaProduct.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "description" -> select.ORDER_BY(
                                MetaColumnWiwaProduct.DESCRIPTION.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "stockStatus" -> select.ORDER_BY(
                                MetaColumnWiwaProduct.STOCK_STATUS.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaProductDto update(final Connection connection, final WiwaProductDto wiwaProductDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProduct.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaProductDto.toArray(wiwaProductDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_PRODUCT.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaProduct.ID.column(), Condition.EQUALS, wiwaProductDto.id())
        );

        return wiwaProductDto;
    }
}


//
//    private Predicate unitPriceFromToPredicate(final BigDecimal value, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
//        final Join<ProductDo, ProductUnitPriceDo> join = root.join("productUnitPrices");
//        return criteriaBuilder.and(
//                criteriaBuilder.greaterThanOrEqualTo(join.get("validFrom"), LocalDate.now()),
//                criteriaBuilder.or(
//                        criteriaBuilder.lessThanOrEqualTo(join.get("validTo"), LocalDate.now()),
//                        criteriaBuilder.isNull(join.get("validTo"))
//                ),
//                criteriaBuilder.greaterThanOrEqualTo(join.get("price").get("value"), value)
//        );
//    }
//
//    private Predicate unitPriceToToPredicate(final BigDecimal value, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
//        final Join<ProductDo, ProductUnitPriceDo> join = root.join("productUnitPrices");
//        return criteriaBuilder.and(
//                criteriaBuilder.greaterThanOrEqualTo(join.get("validFrom"), LocalDate.now()),
//                criteriaBuilder.or(
//                        criteriaBuilder.lessThanOrEqualTo(join.get("validTo"), LocalDate.now()),
//                        criteriaBuilder.isNull(join.get("validTo"))
//                ),
//                criteriaBuilder.lessThanOrEqualTo(join.get("price").get("value"), value)
//        );
//    }
//
//    private Predicate productAttributeToPredicate(final ProductAttributeKey key, final String value, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
//        final Join<ProductDo, ProductAttributeDo> join = root.join("attributes");
//        return criteriaBuilder.and(
//                criteriaBuilder.equal(join.get("key"), key.name()),
//                criteriaBuilder.equal(join.get("value"), value)
//        );
//    }
