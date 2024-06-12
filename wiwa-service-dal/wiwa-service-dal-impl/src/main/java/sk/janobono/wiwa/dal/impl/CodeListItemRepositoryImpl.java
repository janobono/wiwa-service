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
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.CodeListItemDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListItemDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
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
public class CodeListItemRepositoryImpl implements CodeListItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final CodeListItemDoMapper mapper;
    private final ScDf scDf;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public int countByCodeListIdAndParentIdNull(final long codeListId) {
        log.debug("countByCodeListIdAndParentIdNull({})", codeListId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.CODE_LIST_ID.column(), Condition.EQUALS, codeListId)
                .AND(MetaColumnWiwaCodeListItem.PARENT_ID.column(), Condition.IS_NULL, null)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public int countByIdNotAndCode(final long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS_NOT, id)
                .AND(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public int countByParentId(final long parentId) {
        log.debug("countByParentId({})", parentId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.PARENT_ID.column(), Condition.EQUALS, parentId)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public boolean existsById(final long id) {
        log.debug("existsById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, id)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Page<CodeListItemDo> findAll(final CodeListItemSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        final Query.Select selectTotalRows = Query
                .SELECT(MetaColumnWiwaCodeListItem.ID.column())
                .COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table());
        mapCriteria(criteria, selectTotalRows);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

        if (totalRows > 0) {
            final Query.Select select = Query.SELECT(MetaColumnWiwaCodeListItem.columns())
                    .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaCodeListItem.ID.column(), Order.ASC);
            }

            mapCriteria(criteria, select);

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), MetaColumnWiwaCodeListItem.columns());

            final List<CodeListItemDo> content = rows.stream()
                    .map(WiwaCodeListItemDto::toObject)
                    .map(mapper::toCodeListItemDo)
                    .toList();
            return new PageImpl<>(content, pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    @Override
    public Optional<CodeListItemDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.columns())
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, id)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaCodeListItem.columns());

        return rows.stream()
                .findFirst()
                .map(WiwaCodeListItemDto::toObject)
                .map(mapper::toCodeListItemDo);
    }

    @Transactional
    @Override
    public CodeListItemDo save(final CodeListItemDo codeListItemDo) {
        log.debug("save({})", codeListItemDo);
        final WiwaCodeListItemDto result;
        if (codeListItemDo.getId() == null) {
            result = insert(mapper.toWiwaCodeListItemDto(codeListItemDo));
        } else {
            result = update(mapper.toWiwaCodeListItemDto(codeListItemDo));
        }
        return mapper.toCodeListItemDo(result);
    }

    private WiwaCodeListItemDto insert(final WiwaCodeListItemDto wiwaCodeListItemDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaCodeListItem.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaCodeListItemDto.toArray(wiwaCodeListItemDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_CODE_LIST_ITEM.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaCodeListItem.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaCodeListItemDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final CodeListItemSearchCriteriaDo criteria, final Query.Select select) {
        // code list id
        if (Optional.ofNullable(criteria.codeListId()).isPresent()) {
            select.AND(MetaColumnWiwaCodeListItem.CODE_LIST_ID.column(), Condition.EQUALS, criteria.codeListId());
        }

        // root
        if (Optional.ofNullable(criteria.root()).orElse(false)) {
            select.AND(MetaColumnWiwaCodeListItem.PARENT_ID.column(), Condition.IS_NULL, null);
        }

        // parent category id
        if (Optional.ofNullable(criteria.parentId()).isPresent()) {
            select.AND(MetaColumnWiwaCodeListItem.PARENT_ID.column(), Condition.EQUALS, criteria.parentId());
        }

        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            r3nUtil.scDf("SF1", MetaColumnWiwaCodeListItem.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF2", MetaColumnWiwaCodeListItem.VALUE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, criteria.code());
        }

        // value
        if (Optional.ofNullable(criteria.value()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    r3nUtil.scDf("VL", MetaColumnWiwaCodeListItem.VALUE.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.value()) + "%"
            );
        }

        // treeCode
        if (Optional.ofNullable(criteria.treeCode()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND_IN()
                    .OR(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, criteria.treeCode())
                    .OR(MetaColumnWiwaCodeListItem.TREE_CODE.column(), Condition.LIKE, "%" + criteria.treeCode() + "::%")
                    .OUT();
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "codeListId" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.CODE_LIST_ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "parentId" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.PARENT_ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "treeCode" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.TREE_CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "value" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.VALUE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "sortNum" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.SORT_NUM.column(),
                                r3nUtil.mapDirection(order)
                        );
                        default -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaCodeListItemDto update(final WiwaCodeListItemDto wiwaCodeListItemDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaCodeListItem.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaCodeListItemDto.toArray(wiwaCodeListItemDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, wiwaCodeListItemDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaCodeListItemDto;
    }
}
