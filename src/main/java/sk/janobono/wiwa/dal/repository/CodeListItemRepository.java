package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.CodeListItemSearchCriteriaDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaCodeListItemDto;
import sk.janobono.wiwa.dal.r3n.meta.*;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.CodeListItemDoMapper;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
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
public class CodeListItemRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final CodeListItemDoMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    public int countByCode(final String code) {
        log.debug("countByCode({})", code);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, code)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int countByCodeListIdAndParentIdNull(final Long codeListId) {
        log.debug("countByCodeListIdAndParentIdNull({})", codeListId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.CODE_LIST_ID.column(), Condition.EQUALS, codeListId)
                            .AND(MetaColumnWiwaCodeListItem.PARENT_ID.column(), Condition.IS_NULL, null)
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
                    Query.SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS_NOT, id)
                            .AND(MetaColumnWiwaCodeListItem.CODE.column(), Condition.EQUALS, code)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int countByParentId(final Long parentId) {
        log.debug("countByParentId({})", parentId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.PARENT_ID.column(), Condition.EQUALS, parentId)
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(final Long id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, id)
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

    public void deleteById(final Long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<CodeListItemDo> findAll(final CodeListItemSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaCodeListItem.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
            if (totalRows > 0) {
                final Query.Select select = Query.SELECT(MetaColumnWiwaCodeListItem.columns())
                        .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table());

                if (pageable.isPaged()) {
                    select.page(pageable.getPageNumber(), pageable.getPageSize());
                }

                if (pageable.getSort().isSorted()) {
                    mapOrderBy(pageable, select);
                } else {
                    select.ORDER_BY(MetaColumnWiwaCodeListItem.SORT_NUM.column(), Order.ASC);
                }

                mapCriteria(criteria, select);
                final List<Object[]> rows = sqlBuilder.select(connection, select);
                final List<CodeListItemDo> content = rows.stream()
                        .map(WiwaCodeListItemDto::toObject)
                        .map(mapper::toCodeListItemDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<CodeListItemDo> findById(final Long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.columns())
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaCodeListItemDto::toObject)
                    .map(mapper::toCodeListItemDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CodeListItemDo save(final CodeListItemDo codeListItemDo) {
        log.debug("save({})", codeListItemDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaCodeListItemDto result = save(connection, mapper.toWiwaCodeListItemDto(codeListItemDo));
            return mapper.toCodeListItemDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(final List<CodeListItemDo> batch) {
        log.debug("saveAll({})", batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            for (final CodeListItemDo codeListItemDo : batch) {
                save(connection, mapper.toWiwaCodeListItemDto(codeListItemDo));
            }

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private WiwaCodeListItemDto insert(final Connection connection, final WiwaCodeListItemDto wiwaCodeListItemDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaCodeListItem.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaCodeListItemDto.toArray(wiwaCodeListItemDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_CODE_LIST_ITEM.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaCodeListItem.ID.column()));

        return WiwaCodeListItemDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
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
                            criteriaUtil.scDf("SF1", MetaColumnWiwaCodeListItem.CODE.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaCodeListItem.VALUE.column()),
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
                    criteriaUtil.scDf("VL", MetaColumnWiwaCodeListItem.VALUE.column()),
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
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "codeListId" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.CODE_LIST_ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "parentId" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.PARENT_ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "treeCode" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.TREE_CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "code" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.CODE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "value" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.VALUE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "sortNum" -> select.ORDER_BY(
                                MetaColumnWiwaCodeListItem.SORT_NUM.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaCodeListItemDto save(final Connection connection, final WiwaCodeListItemDto wiwaCodeListItemDto) throws SQLException {
        if (wiwaCodeListItemDto.id() == null) {
            return insert(connection, wiwaCodeListItemDto);
        } else {
            return update(connection, wiwaCodeListItemDto);
        }
    }

    private WiwaCodeListItemDto update(final Connection connection, final WiwaCodeListItemDto wiwaCodeListItemDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaCodeListItem.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaCodeListItemDto.toArray(wiwaCodeListItemDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_CODE_LIST_ITEM.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaCodeListItem.ID.column(), Condition.EQUALS, wiwaCodeListItemDto.id())
        );

        return wiwaCodeListItemDto;
    }
}
