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
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.BoardDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaBoard;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaBoardCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.BoardSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.BoardRepository;
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
public class BoardRepositoryImpl implements BoardRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final BoardDoMapper mapper;
    private final ScDf scDf;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaBoard.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_BOARD.table())
                .WHERE(MetaColumnWiwaBoard.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public int countByIdNotAndCode(final long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaBoard.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_BOARD.table())
                .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS_NOT, id)
                .AND(MetaColumnWiwaBoard.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_BOARD.table())
                .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public boolean existsById(final long id) {
        log.debug("existsById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaBoard.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_BOARD.table())
                .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, id)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Override
    public Page<BoardDo> findAll(final BoardSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);

        final Query.Select selectTotalRows = Query
                .SELECT(MetaColumnWiwaBoard.ID.column())
                .COUNT()
                .FROM(MetaTable.WIWA_BOARD.table());
        mapCriteria(criteria, selectTotalRows);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

        if (totalRows > 0) {
            final Query.Select select = Query.SELECT(MetaColumnWiwaBoard.columns())
                    .FROM(MetaTable.WIWA_BOARD.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaBoard.NAME.column(), Order.ASC);
            }

            mapCriteria(criteria, select);

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), MetaColumnWiwaBoard.columns());

            final List<BoardDo> content = rows.stream()
                    .map(WiwaBoardDto::toObject)
                    .map(mapper::toBoardDo)
                    .toList();
            return new PageImpl<>(content, pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    @Override
    public Optional<BoardDo> findById(final long id) {
        log.debug("findById({})", id);

        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaBoard.columns())
                .FROM(MetaTable.WIWA_BOARD.table())
                .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, id)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaBoard.columns());

        return rows.stream()
                .findFirst()
                .map(WiwaBoardDto::toObject)
                .map(mapper::toBoardDo);
    }

    @Transactional
    @Override
    public BoardDo save(final BoardDo boardDo) {
        log.debug("save({})", boardDo);
        final WiwaBoardDto wiwaBoardDto;
        if (boardDo.getId() == null) {
            wiwaBoardDto = insert(mapper.toWiwaBoardDto(boardDo));
        } else {
            wiwaBoardDto = update(mapper.toWiwaBoardDto(boardDo));
        }
        return mapper.toBoardDo(wiwaBoardDto);
    }

    private WiwaBoardDto insert(final WiwaBoardDto wiwaBoardDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaBoard.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaBoardDto.toArray(wiwaBoardDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_BOARD.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaBoard.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaBoardDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final BoardSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            r3nUtil.scDf("SF1", MetaColumnWiwaBoard.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF2", MetaColumnWiwaBoard.NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.CODE.column(), Condition.EQUALS, criteria.code());
        }

        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    r3nUtil.scDf("NM", MetaColumnWiwaBoard.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }

        // board code
        if (Optional.ofNullable(criteria.boardCode()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    r3nUtil.scDf("BC", MetaColumnWiwaBoard.BOARD_CODE.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.boardCode()) + "%"
            );
        }

        // structure code
        if (Optional.ofNullable(criteria.structureCode()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    r3nUtil.scDf("SC", MetaColumnWiwaBoard.STRUCTURE_CODE.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.structureCode()) + "%"
            );
        }

        // orientation
        if (Optional.ofNullable(criteria.orientation()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.ORIENTATION.column(), Condition.EQUALS, criteria.orientation());
        }

        // length from
        if (Optional.ofNullable(criteria.lengthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.LENGTH.column(), Condition.EQUALS_MORE, criteria.lengthFrom());
        }

        // length to
        if (Optional.ofNullable(criteria.lengthTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.LENGTH.column(), Condition.EQUALS_LESS, criteria.lengthTo());
        }

        // width from
        if (Optional.ofNullable(criteria.widthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.WIDTH.column(), Condition.EQUALS_MORE, criteria.widthFrom());
        }

        // width to
        if (Optional.ofNullable(criteria.widthTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.WIDTH.column(), Condition.EQUALS_LESS, criteria.widthTo());
        }

        // thickness from
        if (Optional.ofNullable(criteria.thicknessFrom()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.THICKNESS.column(), Condition.EQUALS_MORE, criteria.thicknessFrom());
        }

        // thickness to
        if (Optional.ofNullable(criteria.thicknessTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.THICKNESS.column(), Condition.EQUALS_LESS, criteria.thicknessTo());
        }

        // price from
        if (Optional.ofNullable(criteria.priceFrom()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.PRICE.column(), Condition.EQUALS_MORE, criteria.priceFrom());
        }

        // price to
        if (Optional.ofNullable(criteria.priceTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.PRICE.column(), Condition.EQUALS_LESS, criteria.priceTo());
        }

        // code list items
        if (Optional.ofNullable(criteria.codeListItems()).filter(l -> !l.isEmpty()).isPresent()) {
            select.DISTINCT()
                    .LEFT_JOIN(MetaTable.WIWA_BOARD_CODE_LIST_ITEM.table(), MetaColumnWiwaBoardCodeListItem.BOARD_ID.column(), MetaColumnWiwaBoard.ID.column());
            int index = 0;
            for (final String code : criteria.codeListItems()) {
                final String alias = "CLIT" + index++;
                select.LEFT_JOIN(
                                MetaTable.WIWA_CODE_LIST_ITEM.table(alias),
                                MetaColumnWiwaCodeListItem.ID.column(alias),
                                MetaColumnWiwaBoardCodeListItem.CODE_LIST_ITEM_ID.column()
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
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "description" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.DESCRIPTION.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "boardCode" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.BOARD_CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "structureCode" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.STRUCTURE_CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "orientation" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.ORIENTATION.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "weight" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.WEIGHT.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "length" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.LENGTH.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "width" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.WIDTH.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "thickness" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.THICKNESS.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "price" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.PRICE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        default -> select.ORDER_BY(
                                MetaColumnWiwaBoard.ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaBoardDto update(final WiwaBoardDto wiwaBoardDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaBoard.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaBoardDto.toArray(wiwaBoardDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_BOARD.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, wiwaBoardDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaBoardDto;
    }
}
