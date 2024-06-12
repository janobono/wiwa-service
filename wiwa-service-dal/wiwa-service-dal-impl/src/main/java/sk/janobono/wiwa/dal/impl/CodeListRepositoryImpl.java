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
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.CodeListDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeList;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
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
public class CodeListRepositoryImpl implements CodeListRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final CodeListDoMapper mapper;
    private final ScDf scDf;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeList.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST.table())
                .WHERE(MetaColumnWiwaCodeList.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public int countByIdNotAndCode(final long id, final String code) {
        log.debug("countByIdNotAndCode({},{})", id, code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeList.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST.table())
                .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS_NOT, id)
                .AND(MetaColumnWiwaCodeList.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_CODE_LIST.table())
                .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public boolean existsById(final long id) {
        log.debug("existsById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeList.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST.table())
                .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, id)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Override
    public Page<CodeListDo> findAll(final CodeListSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        final Query.Select selectTotalRows = Query
                .SELECT(MetaColumnWiwaCodeList.ID.column())
                .COUNT()
                .FROM(MetaTable.WIWA_CODE_LIST.table());
        mapCriteria(criteria, selectTotalRows);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

        if (totalRows > 0) {
            final Query.Select select = Query
                    .SELECT(MetaColumnWiwaCodeList.columns())
                    .FROM(MetaTable.WIWA_CODE_LIST.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaCodeList.ID.column(), Order.ASC);
            }

            mapCriteria(criteria, select);

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), MetaColumnWiwaCodeList.columns());

            final List<CodeListDo> content = rows.stream()
                    .map(WiwaCodeListDto::toObject)
                    .map(mapper::toCodeListDo)
                    .toList();
            return new PageImpl<>(content, pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    @Override
    public Optional<CodeListDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeList.columns())
                .FROM(MetaTable.WIWA_CODE_LIST.table())
                .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaCodeList.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaCodeListDto::toObject)
                .map(mapper::toCodeListDo);
    }

    @Transactional
    @Override
    public CodeListDo save(final CodeListDo codeListDo) {
        log.debug("save({})", codeListDo);
        final WiwaCodeListDto result;
        if (codeListDo.getId() == null) {
            result = insert(mapper.toWiwaCodeListDto(codeListDo));
        } else {
            result = update(mapper.toWiwaCodeListDto(codeListDo));
        }
        return mapper.toCodeListDo(result);
    }

    private WiwaCodeListDto insert(final WiwaCodeListDto wiwaCodeListDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaCodeList.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaCodeListDto.toArray(wiwaCodeListDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_CODE_LIST.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaCodeList.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaCodeListDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final CodeListSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            r3nUtil.scDf("SF1", MetaColumnWiwaCodeList.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF2", MetaColumnWiwaCodeList.NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaCodeList.CODE.column(), Condition.EQUALS, criteria.code());
        }

        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(
                    r3nUtil.scDf("NM", MetaColumnWiwaCodeList.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaCodeList.CODE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaCodeList.NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        default -> select.ORDER_BY(
                                MetaColumnWiwaCodeList.ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaCodeListDto update(final WiwaCodeListDto wiwaCodeListDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaCodeList.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaCodeListDto.toArray(wiwaCodeListDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_CODE_LIST.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, wiwaCodeListDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaCodeListDto;
    }
}
