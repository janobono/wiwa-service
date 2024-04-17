package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.CodeListDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeList;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.CodeListSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
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
public class CodeListRepositoryImpl implements CodeListRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final CodeListDoMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    @Override
    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeList.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST.table())
                            .WHERE(MetaColumnWiwaCodeList.CODE.column(), Condition.EQUALS, code)
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
                    Query.SELECT(MetaColumnWiwaCodeList.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST.table())
                            .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS_NOT, id)
                            .AND(MetaColumnWiwaCodeList.CODE.column(), Condition.EQUALS, code)
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
                            .FROM(MetaTable.WIWA_CODE_LIST.table())
                            .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, id)
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
                    Query.SELECT(MetaColumnWiwaCodeList.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST.table())
                            .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, id)
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
    public Page<CodeListDo> findAll(final CodeListSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaCodeList.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_CODE_LIST.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
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
                    select.ORDER_BY(MetaColumnWiwaCodeList.NAME.column(), Order.ASC);
                }

                mapCriteria(criteria, select);

                final List<Object[]> rows = sqlBuilder.select(connection, select);
                final List<CodeListDo> content = rows.stream()
                        .map(WiwaCodeListDto::toObject)
                        .map(mapper::toCodeListDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CodeListDo> findById(final Long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeList.columns())
                            .FROM(MetaTable.WIWA_CODE_LIST.table())
                            .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaCodeListDto::toObject)
                    .map(mapper::toCodeListDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CodeListDo save(final CodeListDo codeListDo) {
        log.debug("save({})", codeListDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaCodeListDto result;
            if (codeListDo.getId() == null) {
                result = insert(connection, mapper.toWiwaCodeListDto(codeListDo));
            } else {
                result = update(connection, mapper.toWiwaCodeListDto(codeListDo));
            }
            return mapper.toCodeListDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaCodeListDto insert(final Connection connection, final WiwaCodeListDto wiwaCodeListDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaCodeList.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaCodeListDto.toArray(wiwaCodeListDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_CODE_LIST.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaCodeList.ID.column()));

        return WiwaCodeListDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final CodeListSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaCodeList.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaCodeList.NAME.column()),
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
                    criteriaUtil.scDf("NM", MetaColumnWiwaCodeList.NAME.column()),
                    Condition.LIKE,
                    "%" + scDf.toScDf(criteria.name()) + "%"
            );
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaCodeList.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaCodeList.CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "name" -> select.ORDER_BY(
                                MetaColumnWiwaCodeList.NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaCodeListDto update(final Connection connection, final WiwaCodeListDto wiwaCodeListDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaCodeList.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaCodeListDto.toArray(wiwaCodeListDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_CODE_LIST.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaCodeList.ID.column(), Condition.EQUALS, wiwaCodeListDto.id())
        );

        return wiwaCodeListDto;
    }
}
