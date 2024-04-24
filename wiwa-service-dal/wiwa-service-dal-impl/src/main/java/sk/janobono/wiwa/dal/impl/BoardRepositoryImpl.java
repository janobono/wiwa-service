package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaBoard;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaBoardCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.BoardSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.BoardRepository;
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
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaBoard.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_BOARD.table())
                            .WHERE(MetaColumnWiwaBoard.CODE.column(), Condition.EQUALS, code)
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
    public int countByIdNotAndCode(final long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaBoard.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_BOARD.table())
                            .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS_NOT, id)
                            .AND(MetaColumnWiwaBoard.CODE.column(), Condition.EQUALS, code)
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
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_BOARD.table())
                            .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, id)
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
                    Query.SELECT(MetaColumnWiwaBoard.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_BOARD.table())
                            .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, id)
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
    public Page<BoardDo> findAll(final BoardSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaBoard.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_BOARD.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
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

                final List<Object[]> rows = sqlBuilder.select(connection, select);
                final List<BoardDo> content = rows.stream()
                        .map(WiwaBoardDto::toObject)
                        .map(this::toBoardDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BoardDo> findById(final long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaBoard.columns())
                            .FROM(MetaTable.WIWA_BOARD.table())
                            .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaBoardDto::toObject)
                    .map(this::toBoardDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BoardDo> findAllByIds(final Set<Long> ids) {
        log.debug("findAllByIds({})", ids);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaBoard.columns())
                            .FROM(MetaTable.WIWA_BOARD.table())
                            .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.IN, ids)
            );
            return rows.stream()
                    .map(WiwaBoardDto::toObject)
                    .map(this::toBoardDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BoardDo save(final BoardDo boardDo) {
        log.debug("save({})", boardDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaBoardDto wiwaBoardDto;
            if (boardDo.getId() == null) {
                wiwaBoardDto = insert(connection, toWiwaBoardDto(boardDo));
            } else {
                wiwaBoardDto = update(connection, toWiwaBoardDto(boardDo));
            }
            return toBoardDo(wiwaBoardDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaBoardDto insert(final Connection connection, final WiwaBoardDto wiwaBoardDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaBoard.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaBoardDto.toArray(wiwaBoardDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_BOARD.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaBoard.ID.column()));

        return WiwaBoardDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final BoardSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaBoard.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaBoard.NAME.column()),
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
                    criteriaUtil.scDf("NM", MetaColumnWiwaBoard.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }

        // board code
        if (Optional.ofNullable(criteria.boardCode()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    criteriaUtil.scDf("BC", MetaColumnWiwaBoard.BOARD_CODE.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.boardCode()) + "%"
            );
        }

        // structure code
        if (Optional.ofNullable(criteria.structureCode()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    criteriaUtil.scDf("SC", MetaColumnWiwaBoard.STRUCTURE_CODE.column()),
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
            select.AND(MetaColumnWiwaBoard.LENGTH_VALUE.column(), Condition.EQUALS_MORE, criteria.lengthFrom().quantity())
                    .AND(MetaColumnWiwaBoard.LENGTH_UNIT.column(), Condition.EQUALS, criteria.lengthFrom().unit().name());
        }

        // length to
        if (Optional.ofNullable(criteria.lengthTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.LENGTH_VALUE.column(), Condition.EQUALS_LESS, criteria.lengthTo().quantity())
                    .AND(MetaColumnWiwaBoard.LENGTH_UNIT.column(), Condition.EQUALS, criteria.lengthTo().unit().name());
        }

        // width from
        if (Optional.ofNullable(criteria.widthFrom()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.WIDTH_VALUE.column(), Condition.EQUALS_MORE, criteria.widthFrom().quantity())
                    .AND(MetaColumnWiwaBoard.WIDTH_UNIT.column(), Condition.EQUALS, criteria.widthFrom().unit().name());
        }

        // width to
        if (Optional.ofNullable(criteria.widthTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.WIDTH_VALUE.column(), Condition.EQUALS_LESS, criteria.widthTo().quantity())
                    .AND(MetaColumnWiwaBoard.WIDTH_UNIT.column(), Condition.EQUALS, criteria.widthTo().unit().name());
        }

        // thickness from
        if (Optional.ofNullable(criteria.thicknessFrom()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.THICKNESS_VALUE.column(), Condition.EQUALS_MORE, criteria.thicknessFrom().quantity())
                    .AND(MetaColumnWiwaBoard.THICKNESS_UNIT.column(), Condition.EQUALS, criteria.thicknessFrom().unit().name());
        }

        // thickness to
        if (Optional.ofNullable(criteria.thicknessTo()).isPresent()) {
            select.AND(MetaColumnWiwaBoard.THICKNESS_VALUE.column(), Condition.EQUALS_LESS, criteria.thicknessTo().quantity())
                    .AND(MetaColumnWiwaBoard.THICKNESS_UNIT.column(), Condition.EQUALS, criteria.thicknessTo().unit().name());
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
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "description" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.DESCRIPTION.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "boardCode" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.BOARD_CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "structureCode" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.STRUCTURE_CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "orientation" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.ORIENTATION.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "saleValue" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.SALE_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "saleUnit" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.SALE_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightValue" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.NET_WEIGHT_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "netWeightUnit" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.NET_WEIGHT_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "lengthValue" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.LENGTH_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "lengthUnit" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.LENGTH_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "widthValue" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.WIDTH_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "widthUnit" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.WIDTH_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thicknessValue" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.THICKNESS_VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thicknessUnit" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.THICKNESS_UNIT.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "priceValue" -> select.ORDER_BY(
                                MetaColumnWiwaBoard.PRICE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private BoardDo toBoardDo(final WiwaBoardDto wiwaBoardDto) {
        return BoardDo.builder()
                .id(wiwaBoardDto.id())
                .code(wiwaBoardDto.code())
                .boardCode(wiwaBoardDto.boardCode())
                .structureCode(wiwaBoardDto.structureCode())
                .name(wiwaBoardDto.name())
                .description(wiwaBoardDto.description())
                .orientation(wiwaBoardDto.orientation())
                .sale(new Quantity(wiwaBoardDto.saleValue(), Unit.valueOf(wiwaBoardDto.saleUnit())))

                .netWeight(Optional.ofNullable(wiwaBoardDto.netWeightValue())
                        .map(v -> new Quantity(v, Optional.ofNullable(wiwaBoardDto.netWeightUnit()).map(Unit::valueOf).orElse(Unit.KILOGRAM)))
                        .orElse(null))

                .length(new Quantity(wiwaBoardDto.lengthValue(), Unit.valueOf(wiwaBoardDto.lengthUnit())))
                .width(new Quantity(wiwaBoardDto.widthValue(), Unit.valueOf(wiwaBoardDto.widthUnit())))
                .thickness(new Quantity(wiwaBoardDto.thicknessValue(), Unit.valueOf(wiwaBoardDto.thicknessUnit())))
                .price(wiwaBoardDto.price())
                .build();
    }

    private WiwaBoardDto toWiwaBoardDto(final BoardDo boardDo) {
        return new WiwaBoardDto(
                boardDo.getId(),
                boardDo.getCode(),
                boardDo.getBoardCode(),
                boardDo.getStructureCode(),
                boardDo.getName(),
                boardDo.getDescription(),
                boardDo.getOrientation(),
                boardDo.getSale().quantity(),
                boardDo.getSale().unit().name(),
                Optional.ofNullable(boardDo.getNetWeight()).map(Quantity::quantity).orElse(null),
                Optional.ofNullable(boardDo.getNetWeight()).map(Quantity::unit).map(Unit::name).orElse(null),
                boardDo.getLength().quantity(),
                boardDo.getLength().unit().name(),
                boardDo.getWidth().quantity(),
                boardDo.getWidth().unit().name(),
                boardDo.getThickness().quantity(),
                boardDo.getThickness().unit().name(),
                boardDo.getPrice()
        );
    }

    private WiwaBoardDto update(final Connection connection, final WiwaBoardDto wiwaBoardDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaBoard.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaBoardDto.toArray(wiwaBoardDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_BOARD.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaBoard.ID.column(), Condition.EQUALS, wiwaBoardDto.id())
        );

        return wiwaBoardDto;
    }
}
