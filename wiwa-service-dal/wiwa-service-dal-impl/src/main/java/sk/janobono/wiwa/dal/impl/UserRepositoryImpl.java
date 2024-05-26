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
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.UserDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaUserDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaUser;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.UserRepository;
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
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final UserDoMapper mapper;
    private final ScDf scDf;

    @Override
    public int count() {
        log.debug("count()");
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_USER.table())
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public boolean existsByEmail(final String email) {
        log.debug("existsByEmail({})", email);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.EMAIL.column(), Condition.EQUALS, email)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Override
    public boolean existsById(final long id) {
        log.debug("existsById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Override
    public boolean existsByUsername(final String username) {
        log.debug("existsByUsername({})", username);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.USERNAME.column(), Condition.EQUALS, username)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    @Override
    public Page<UserDo> findAll(final UserSearchCriteriaDo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        final Query.Select selectTotalRows = Query
                .SELECT(MetaColumnWiwaUser.ID.column())
                .COUNT()
                .FROM(MetaTable.WIWA_USER.table());
        mapCriteria(criteria, selectTotalRows);
        final int totalRows = r3nUtil.count(jdbcTemplate, sqlBuilder.select(selectTotalRows));

        if (totalRows > 0) {
            final Query.Select select = Query
                    .SELECT(MetaColumnWiwaUser.columns())
                    .FROM(MetaTable.WIWA_USER.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaUser.USERNAME.column(), Order.ASC);
            }

            mapCriteria(criteria, select);

            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sqlBuilder.select(select), MetaColumnWiwaUser.columns());

            final List<UserDo> content = rows.stream()
                    .map(WiwaUserDto::toObject)
                    .map(mapper::toUserDo)
                    .toList();
            return new PageImpl<>(content, pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    @Override
    public Optional<UserDo> findByEmail(final String email) {
        log.debug("findByEmail({})", email);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.columns())
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.EMAIL.column(), Condition.EQUALS, email)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaUser.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaUserDto::toObject)
                .map(mapper::toUserDo);
    }

    @Override
    public Optional<UserDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.columns())
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaUser.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaUserDto::toObject)
                .map(mapper::toUserDo);
    }

    @Override
    public Optional<UserDo> findByUsername(final String username) {
        log.debug("findByUsername({})", username);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaUser.columns())
                .FROM(MetaTable.WIWA_USER.table())
                .WHERE(MetaColumnWiwaUser.USERNAME.column(), Condition.EQUALS, username)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaUser.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaUserDto::toObject)
                .map(mapper::toUserDo);
    }

    @Transactional
    @Override
    public UserDo save(final UserDo userDo) {
        log.debug("save({})", userDo);
        final WiwaUserDto wiwaUserDto;
        if (userDo.getId() == null) {
            wiwaUserDto = insert(mapper.toWiwaUserDto(userDo));
        } else {
            wiwaUserDto = update(mapper.toWiwaUserDto(userDo));
        }
        return mapper.toUserDo(wiwaUserDto);
    }

    private WiwaUserDto insert(final WiwaUserDto wiwaUserDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaUser.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaUserDto.toArray(wiwaUserDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_USER.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaUser.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaUserDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final UserSearchCriteriaDo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            r3nUtil.scDf("SF1", MetaColumnWiwaUser.USERNAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF2", MetaColumnWiwaUser.FIRST_NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF3", MetaColumnWiwaUser.MID_NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF4", MetaColumnWiwaUser.LAST_NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            r3nUtil.scDf("SF4", MetaColumnWiwaUser.EMAIL.column()),
                            Condition.LIKE,
                            value
                    )
                    .OUT();
        }

        // username
        if (Optional.ofNullable(criteria.username()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaUser.USERNAME.column(), Condition.LIKE, "%" + scDf.toStripAndLowerCase(criteria.username()) + "%");
        }

        // email
        if (Optional.ofNullable(criteria.email()).filter(s -> !s.isBlank()).isPresent()) {
            select.AND(MetaColumnWiwaUser.EMAIL.column(), Condition.LIKE, "%" + scDf.toStripAndLowerCase(criteria.email()) + "%");
        }
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaUser.ID.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "username" -> select.ORDER_BY(
                                MetaColumnWiwaUser.USERNAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "titleBefore" -> select.ORDER_BY(
                                MetaColumnWiwaUser.TITLE_BEFORE.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "firstName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.FIRST_NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "midName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.MID_NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "lastName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.LAST_NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "titleAfter" -> select.ORDER_BY(
                                MetaColumnWiwaUser.TITLE_AFTER.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "email" -> select.ORDER_BY(
                                MetaColumnWiwaUser.EMAIL.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "gdpr" -> select.ORDER_BY(
                                MetaColumnWiwaUser.GDPR.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "confirmed" -> select.ORDER_BY(
                                MetaColumnWiwaUser.CONFIRMED.column(),
                                r3nUtil.mapDirection(order)
                        );
                        case "enabled" -> select.ORDER_BY(
                                MetaColumnWiwaUser.ENABLED.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaUserDto update(final WiwaUserDto wiwaUserDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaUser.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaUserDto.toArray(wiwaUserDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_USER.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, wiwaUserDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaUserDto;
    }
}
