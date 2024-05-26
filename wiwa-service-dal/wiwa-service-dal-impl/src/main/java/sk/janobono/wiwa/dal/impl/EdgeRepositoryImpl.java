package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.EdgeDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaEdgeDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdge;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdgeCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.EdgeRepository;
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
public class EdgeRepositoryImpl implements EdgeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final EdgeDoMapper mapper;
    private final ScDf scDf;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaEdge.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_EDGE.table())
                .WHERE(MetaColumnWiwaEdge.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public int countByIdNotAndCode(final long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaEdge.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_EDGE.table())
                .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS_NOT, id)
                .AND(MetaColumnWiwaEdge.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_EDGE.table())
                .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public boolean existsById(final long id) {
        log.debug("existsById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaEdge.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_EDGE.table())
                .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, id)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Override
    public Page<EdgeDo> findAll(final EdgeSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        final Query.Select selectTotalRows = Query
                .SELECT(MetaColumnWiwaEdge.ID.column())
                .COUNT()
                .FROM(MetaTable.WIWA_EDGE.table());
        mapCriteria(criteria, selectTotalRows);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

        if (totalRows > 0) {
            final Query.Select select = Query.SELECT(MetaColumnWiwaEdge.columns())
                    .FROM(MetaTable.WIWA_EDGE.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaEdge.NAME.column(), Order.ASC);
            }

            mapCriteria(criteria, select);

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), MetaColumnWiwaEdge.columns());

            final List<EdgeDo> content = rows.stream()
                    .map(WiwaEdgeDto::toObject)
                    .map(mapper::toEdgeDo)
                    .toList();
            return new PageImpl<>(content, pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    @Override
    public Optional<EdgeDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaEdge.columns())
                .FROM(MetaTable.WIWA_EDGE.table())
                .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaEdge.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaEdgeDto::toObject)
                .map(mapper::toEdgeDo);
    }

    @Transactional
    @Override
    public EdgeDo save(final EdgeDo edgeDo) {
        log.debug("save({})", edgeDo);
        final WiwaEdgeDto wiwaEdgeDto;
        if (edgeDo.getId() == null) {
            wiwaEdgeDto = insert(mapper.toWiwaEdgeDto(edgeDo));
        } else {
            wiwaEdgeDto = update(mapper.toWiwaEdgeDto(edgeDo));
        }
        return mapper.toEdgeDo(wiwaEdgeDto);
    }

    private WiwaEdgeDto insert(final WiwaEdgeDto wiwaEdgeDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaEdge.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaEdgeDto.toArray(wiwaEdgeDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_EDGE.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaEdge.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaEdgeDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final EdgeSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            r3nUtil.scDf("SF1", MetaColumnWiwaEdge.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF2", MetaColumnWiwaEdge.NAME.column()),
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
                    r3nUtil.scDf("NM", MetaColumnWiwaEdge.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }

        // width from
        if (Optional.ofNullable(criteria.widthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.WIDTH.column(), Condition.EQUALS_MORE, criteria.widthFrom());
        }

        // width to
        if (Optional.ofNullable(criteria.widthTo()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.WIDTH.column(), Condition.EQUALS_LESS, criteria.widthTo());
        }

        // thickness from
        if (Optional.ofNullable(criteria.thicknessFrom()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.THICKNESS.column(), Condition.EQUALS_MORE, criteria.thicknessFrom());
        }

        // thickness to
        if (Optional.ofNullable(criteria.thicknessTo()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.THICKNESS.column(), Condition.EQUALS_LESS, criteria.thicknessTo());
        }

        // price from
        if (Optional.ofNullable(criteria.priceFrom()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.PRICE.column(), Condition.EQUALS_MORE, criteria.priceFrom());
        }

        // price to
        if (Optional.ofNullable(criteria.priceTo()).isPresent()) {
            select.AND(MetaColumnWiwaEdge.PRICE.column(), Condition.EQUALS_LESS, criteria.priceTo());
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
                                r3nUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "description" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.DESCRIPTION.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "weight" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.WEIGHT.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "width" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.WIDTH.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "thickness" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.THICKNESS.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "price" -> select.ORDER_BY(
                                MetaColumnWiwaEdge.PRICE.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaEdgeDto update(final WiwaEdgeDto wiwaEdgeDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaEdge.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaEdgeDto.toArray(wiwaEdgeDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_EDGE.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaEdge.ID.column(), Condition.EQUALS, wiwaEdgeDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaEdgeDto;
    }
}
