package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.EdgeDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaEdgeDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdge;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdgeCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.EdgeRepository;
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
public class EdgeRepositoryImpl implements EdgeRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final EdgeDoMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaEdge.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_EDGE.table())
                            .WHERE(MetaColumnWiwaEdge.CODE.column(), Condition.EQUALS, code)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countByIdNotAndCode(final Long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaEdge.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_EDGE.table())
                            .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS_NOT, id)
                            .AND(MetaColumnWiwaEdge.CODE.column(), Condition.EQUALS, code)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(final Long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_EDGE.table())
                            .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(final Long id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaEdge.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_EDGE.table())
                            .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, id)
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
    public Page<EdgeDo> findAll(final EdgeSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaEdge.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_EDGE.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
            if (totalRows > 0) {
                final List<Object[]> rows;
                if (pageable.isPaged()) {
                    final Query.Select select = Query
                            .SELECT(MetaColumnWiwaEdge.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                            .FROM(MetaTable.WIWA_EDGE.table());
                    mapCriteria(criteria, select);
                    mapOrderBy(pageable, select);
                    rows = sqlBuilder.select(connection, select);
                } else {
                    final Query.Select select = Query.SELECT(MetaColumnWiwaEdge.columns())
                            .FROM(MetaTable.WIWA_EDGE.table())
                            .ORDER_BY(MetaColumnWiwaEdge.NAME.column(), Order.ASC);
                    mapCriteria(criteria, select);
                    rows = sqlBuilder.select(connection, select);
                }
                final List<EdgeDo> content = rows.stream()
                        .map(WiwaEdgeDto::toObject)
                        .map(mapper::toEdgeDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EdgeDo> findById(final Long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaEdge.columns())
                            .FROM(MetaTable.WIWA_EDGE.table())
                            .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaEdgeDto::toObject)
                    .map(mapper::toEdgeDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EdgeDo save(final EdgeDo edgeDo) {
        log.debug("save({})", edgeDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaEdgeDto wiwaEdgeDto;
            if (edgeDo.getId() == null) {
                wiwaEdgeDto = insert(connection, mapper.toWiwaEdgeDto(edgeDo));
            } else {
                wiwaEdgeDto = update(connection, mapper.toWiwaEdgeDto(edgeDo));
            }
            return mapper.toEdgeDo(wiwaEdgeDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaEdgeDto insert(final Connection connection, final WiwaEdgeDto wiwaEdgeDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaEdge.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaEdgeDto.toArray(wiwaEdgeDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_EDGE.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaEdge.ID.column()));

        return WiwaEdgeDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final EdgeSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaEdge.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaEdge.NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.CODE.column(), Condition.EQUALS, criteria.code());
        }

        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    criteriaUtil.scDf("NM", MetaColumnWiwaEdge.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }

        // width from
        if (Optional.ofNullable(criteria.widthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.WIDTH_VALUE.column(), Condition.EQUALS_MORE, criteria.widthFrom());
        }

        // width to
        if (Optional.ofNullable(criteria.widthTo()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.WIDTH_VALUE.column(), Condition.EQUALS_LESS, criteria.widthTo());
        }

        // width unit
        if (Optional.ofNullable(criteria.widthUnit()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.WIDTH_UNIT.column(), Condition.EQUALS, criteria.widthUnit().name());
        }

        // thickness from
        if (Optional.ofNullable(criteria.thicknessFrom()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.THICKNESS_VALUE.column(), Condition.EQUALS_MORE, criteria.thicknessFrom());
        }

        // thickness to
        if (Optional.ofNullable(criteria.thicknessTo()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.THICKNESS_VALUE.column(), Condition.EQUALS_LESS, criteria.thicknessTo());
        }

        // thickness unit
        if (Optional.ofNullable(criteria.thicknessUnit()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.THICKNESS_UNIT.column(), Condition.EQUALS, criteria.thicknessUnit().name());
        }

        // price from
        if (Optional.ofNullable(criteria.priceFrom()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.PRICE_VALUE.column(), Condition.EQUALS_MORE, criteria.priceFrom());
        }

        // price to
        if (Optional.ofNullable(criteria.priceTo()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.PRICE_VALUE.column(), Condition.EQUALS_LESS, criteria.priceTo());
        }

        // price unit
        if (Optional.ofNullable(criteria.priceUnit()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.PRICE_UNIT.column(), Condition.EQUALS, criteria.priceUnit().name());
        }

        // code list items
        if (Optional.ofNullable(criteria.codeListItems()).filter(l -> !l.isEmpty()).isPresent()) {
            select.DISTINCT()
                    .LEFT_JOIN(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table(), MetaColumnWiwaEdgeCodeListItem.EDGE_ID.column(), MetaColumnWiwaEdge.ID.column());
            int index = 0;
            for (final String code : criteria.codeListItems()) {
                final String alias = "CLIT" + index++;
                select.LEFT_JOIN(
                                MetaTable.WIWA_CODE_LIST_ITEM.table(alias),
                                MetaColumnWiwaCodeListItem.ID.column(alias),
                                MetaColumnWiwaEdgeCodeListItem.CODE_LIST_ITEM_ID.column()
                        ).AND_IN()
                        .OR(MetaColumnWiwaCodeListItem.CODE.column(alias), Condition.EQUALS, code)
                        .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(alias), Condition.LIKE, "%::" + code)
                        .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(alias), Condition.LIKE, "%::" + code + "::%")
                        .OUT();
            }
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "description" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.DESCRIPTION.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "saleValue" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.SALE_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "saleUnit" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.SALE_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "weightValue" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.WEIGHT_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "weightUnit" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.WEIGHT_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightValue" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.NET_WEIGHT_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightUnit" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.NET_WEIGHT_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "widthValue" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.WIDTH_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "widthUnit" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.WIDTH_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thicknessValue" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.THICKNESS_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thicknessUnit" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.THICKNESS_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "priceValue" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.PRICE_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "priceUnit" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.PRICE_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaEdgeDto update(final Connection connection, final WiwaEdgeDto wiwaEdgeDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaEdge.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaEdgeDto.toArray(wiwaEdgeDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_EDGE.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, wiwaEdgeDto.id())
        );

        return wiwaEdgeDto;
    }
}
