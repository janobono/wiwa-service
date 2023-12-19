package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaUserDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaUser;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaUserCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.UserMapper;
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
public class UserRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final UserMapper mapper;
    private final ScDf scDf;
    private final CriteriaUtil criteriaUtil;

    public int count() {
        log.debug("count()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
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
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsByEmail(final String email) {
        log.debug("existsByEmail({})", email);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.EMAIL.column(), Condition.EQUALS, email)
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

    public boolean existsById(final Long id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
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

    public boolean existsByUsername(final String username) {
        log.debug("existsByUsername({})", username);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.USERNAME.column(), Condition.EQUALS, username)
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

    public Page<UserDo> findAll(final UserSearchCriteriaSo criteria, final Pageable pageable) {
        log.debug("findAll({},{})", criteria, pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaUser.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_USER.table());
            mapCriteria(criteria, selectTotalRows);
            final int totalRows = sqlBuilder.select(connection, selectTotalRows).stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
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

                final List<Object[]> rows = sqlBuilder.select(connection, select);
                final List<UserDo> content = rows.stream()
                        .map(WiwaUserDto::toObject)
                        .map(mapper::toUserDo)
                        .toList();
                return new PageImpl<>(content, pageable, totalRows);
            }
            return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserDo> findByEmail(final String email) {
        log.debug("findByEmail({})", email);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.columns())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.EMAIL.column(), Condition.EQUALS, email)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaUserDto::toObject)
                    .map(mapper::toUserDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserDo> findById(final Long id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.columns())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaUserDto::toObject)
                    .map(mapper::toUserDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserDo> findByUsername(final String username) {
        log.debug("findByUsername({})", username);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.columns())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.USERNAME.column(), Condition.EQUALS, username)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaUserDto::toObject)
                    .map(mapper::toUserDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDo save(final UserDo userDo) {
        log.debug("save({})", userDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaUserDto wiwaUserDto;
            if (userDo.getId() == null) {
                wiwaUserDto = insert(connection, mapper.toWiwaUserDto(userDo));
            } else {
                wiwaUserDto = update(connection, mapper.toWiwaUserDto(userDo));
            }
            return mapper.toUserDo(wiwaUserDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaUserDto insert(final Connection connection, final WiwaUserDto wiwaUserDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaUser.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaUserDto.toArray(wiwaUserDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_USER.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaUser.ID.column()));

        return WiwaUserDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private void mapCriteria(final UserSearchCriteriaSo criteria, final Query.Select select) {
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            final String value = "%" + scDf.toScDf(criteria.searchField()) + "%";
            select.AND_IN()
                    .OR(
                            criteriaUtil.scDf("SF1", MetaColumnWiwaUser.USERNAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF2", MetaColumnWiwaUser.FIRST_NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF3", MetaColumnWiwaUser.MID_NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF4", MetaColumnWiwaUser.LAST_NAME.column()),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            criteriaUtil.scDf("SF4", MetaColumnWiwaUser.EMAIL.column()),
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

        // code list items
        if (Optional.ofNullable(criteria.codeListItems()).filter(l -> !l.isEmpty()).isPresent()) {
            select.DISTINCT()
                    .LEFT_JOIN(MetaTable.WIWA_USER_CODE_LIST_ITEM.table(), MetaColumnWiwaUserCodeListItem.USER_ID.column(), MetaColumnWiwaUser.ID.column());
            int index = 0;
            for (final String code : criteria.codeListItems()) {
                final String alias = "CLIT" + index++;
                select.LEFT_JOIN(
                                MetaTable.WIWA_CODE_LIST_ITEM.table(alias),
                                MetaColumnWiwaCodeListItem.ID.column(alias),
                                MetaColumnWiwaUserCodeListItem.CODE_LIST_ITEM_ID.column()
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
                                MetaColumnWiwaUser.ID.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "username" -> select.ORDER_BY(
                                MetaColumnWiwaUser.USERNAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "titleBefore" -> select.ORDER_BY(
                                MetaColumnWiwaUser.TITLE_BEFORE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "firstName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.FIRST_NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "midName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.MID_NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "lastName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.LAST_NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "titleAfter" -> select.ORDER_BY(
                                MetaColumnWiwaUser.TITLE_AFTER.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "email" -> select.ORDER_BY(
                                MetaColumnWiwaUser.EMAIL.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "gdpr" -> select.ORDER_BY(
                                MetaColumnWiwaUser.GDPR.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "confirmed" -> select.ORDER_BY(
                                MetaColumnWiwaUser.CONFIRMED.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "enabled" -> select.ORDER_BY(
                                MetaColumnWiwaUser.ENABLED.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaUserDto update(final Connection connection, final WiwaUserDto wiwaUserDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaUser.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaUserDto.toArray(wiwaUserDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_USER.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, wiwaUserDto.id())
        );

        return wiwaUserDto;
    }
}
