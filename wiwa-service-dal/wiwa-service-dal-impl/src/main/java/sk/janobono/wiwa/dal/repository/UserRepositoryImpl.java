package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.mapper.UserDoMapper;
import sk.janobono.wiwa.dal.model.UserProfileDo;
import sk.janobono.wiwa.dal.model.UserSearchCriteriaDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaAuthorityDto;
import sk.janobono.wiwa.dal.r3n.dto.WiwaUserDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaAuthority;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaUser;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaUserAuthority;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.sql.impl.ColumnFunction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final UserDoMapper userDoMapper;

    @Override
    public long count() {
        log.debug("count()");
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
            );
            return (Integer) rows.get(0)[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(Long id) {
        log.debug("exists({})", id);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            return ((Integer) rows.get(0)[0]) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("exists({})", username);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.USERNAME.column(), Condition.EQUALS, username)
            );
            return ((Integer) rows.get(0)[0]) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("exists({})", email);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.EMAIL.column(), Condition.EQUALS, email)
            );
            return ((Integer) rows.get(0)[0]) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<UserDo> getUsers(Pageable pageable) {
        log.debug("getUsers({})", pageable);
        try (Connection connection = dataSource.getConnection()) {
            int totalRows = (Integer) sqlBuilder.select(connection, Query
                    .SELECT(MetaColumnWiwaUser.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_USER.table())).get(0)[0];
            List<Object[]> rows;
            if (pageable.isPaged()) {
                Query.Select select = Query
                        .SELECT(MetaColumnWiwaUser.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                        .FROM(MetaTable.WIWA_USER.table());
                mapOrderBy(pageable, select);
                rows = sqlBuilder.select(connection, select);
            } else {
                rows = sqlBuilder.select(connection,
                        Query.SELECT(MetaColumnWiwaUser.columns())
                                .FROM(MetaTable.WIWA_USER.table())
                                .ORDER_BY(MetaColumnWiwaUser.USERNAME.column(), Order.ASC));
            }
            List<UserDo> users = new ArrayList<>();
            for (Object[] row : rows) {
                WiwaUserDto wiwaUserDto = WiwaUserDto.toObject(row);
                users.add(mapToDo(wiwaUserDto, getAuthorities(connection, wiwaUserDto.id())));
            }
            return new PageImpl<>(users, pageable, totalRows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<UserDo> getUsers(UserSearchCriteriaDo userSearchCriteriaDo, Pageable pageable) {
        log.debug("getUsers({},{})", userSearchCriteriaDo, pageable);
        try (Connection connection = dataSource.getConnection()) {
            Query.Select selectTotalRows = Query
                    .SELECT(MetaColumnWiwaUser.ID.column())
                    .COUNT()
                    .FROM(MetaTable.WIWA_USER.table());
            mapSearchCriteria(userSearchCriteriaDo, selectTotalRows);
            int totalRows = (Integer) sqlBuilder.select(connection, selectTotalRows).get(0)[0];
            List<Object[]> rows;
            if (pageable.isPaged()) {
                Query.Select select = Query
                        .SELECT(MetaColumnWiwaUser.columns()).page(pageable.getPageNumber(), pageable.getPageSize())
                        .FROM(MetaTable.WIWA_USER.table());
                mapSearchCriteria(userSearchCriteriaDo, select);
                mapOrderBy(pageable, select);
                rows = sqlBuilder.select(connection, select);
            } else {
                Query.Select select = Query.SELECT(MetaColumnWiwaUser.columns())
                        .FROM(MetaTable.WIWA_USER.table())
                        .ORDER_BY(MetaColumnWiwaUser.USERNAME.column(), Order.ASC);
                mapSearchCriteria(userSearchCriteriaDo, select);
                rows = sqlBuilder.select(connection, select);
            }
            List<UserDo> users = new ArrayList<>();
            for (Object[] row : rows) {
                WiwaUserDto wiwaUserDto = WiwaUserDto.toObject(row);
                users.add(mapToDo(wiwaUserDto, getAuthorities(connection, wiwaUserDto.id())));
            }
            return new PageImpl<>(users, pageable, totalRows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserDo> getUser(Long id) {
        log.debug("getUser({})", id);
        try (Connection connection = dataSource.getConnection()) {
            Optional<WiwaUserDto> wiwaUserDto = getWiwaUserDto(connection, id);
            if (wiwaUserDto.isEmpty()) {
                return Optional.empty();
            }
            Set<Authority> authorities = getAuthorities(connection, id);
            return Optional.of(mapToDo(wiwaUserDto.get(), authorities));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserDo> getUserByEmail(String email) {
        log.debug("getUserByEmail({})", email);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.columns())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.EMAIL.column(), Condition.EQUALS, email)
            );
            if (rows.size() == 1) {
                WiwaUserDto wiwaUserDto = WiwaUserDto.toObject(rows.get(0));
                return Optional.of(mapToDo(wiwaUserDto, getAuthorities(connection, wiwaUserDto.id())));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserDo> getUserByUsername(String username) {
        log.debug("getUserByUsername({})", username);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.columns())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.USERNAME.column(), Condition.EQUALS, username)
            );
            if (rows.size() == 1) {
                WiwaUserDto wiwaUserDto = WiwaUserDto.toObject(rows.get(0));
                return Optional.of(mapToDo(wiwaUserDto, getAuthorities(connection, wiwaUserDto.id())));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> getUserPassword(Long id) {
        log.debug("getUserPassword({})", id);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.PASSWORD.column())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            if (rows.size() == 1) {
                return Optional.of((String) rows.get(0)[0]);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Boolean> getUserEnabled(Long id) {
        log.debug("isUserEnabled({})", id);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaUser.ENABLED.column())
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            if (rows.size() == 1) {
                return Optional.of((Boolean) rows.get(0)[0]);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo addUser(UserDo userDo) {
        log.debug("addUser({})", userDo);
        try (Connection connection = dataSource.getConnection()) {
            Object[] row = WiwaUserDto.toArray(userDoMapper.mapToDto(userDo));
            row = Arrays.copyOfRange(row, 1, row.length);

            Column[] columns = MetaColumnWiwaUser.columns();
            columns = Arrays.copyOfRange(columns, 1, columns.length);

            Long id = (Long) sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_USER.table(), columns)
                            .VALUES(row).RETURNING(MetaColumnWiwaUser.ID.column()));

            setUserAuthorities(connection, id, userDo.authorities());

            return new UserDo(
                    id,
                    userDo.username(),
                    userDo.password(),
                    userDo.titleBefore(),
                    userDo.firstName(),
                    userDo.midName(),
                    userDo.lastName(),
                    userDo.titleAfter(),
                    userDo.email(),
                    userDo.gdpr(),
                    userDo.confirmed(),
                    userDo.enabled(),
                    userDo.authorities()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUser(UserDo userDo) {
        log.debug("addUser({})", userDo);
        try (Connection connection = dataSource.getConnection()) {
            Object[] row = WiwaUserDto.toArray(userDoMapper.mapToDto(userDo));
            row = Arrays.copyOfRange(row, 1, row.length);

            Column[] columns = MetaColumnWiwaUser.columns();
            columns = Arrays.copyOfRange(columns, 1, columns.length);

            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(columns, row)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, userDo.id())
            );

            setUserAuthorities(connection, userDo.id(), userDo.authorities());

            return userDo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserAuthorities(Long id, Set<Authority> authorities) {
        log.debug("setUserAuthorities({},{})", id, authorities);
        try (Connection connection = dataSource.getConnection()) {
            setUserAuthorities(connection, id, authorities);
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, authorities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserConfirmed(Long id, Boolean confirmed) {
        log.debug("setUserConfirmed({},{})", id, confirmed);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.CONFIRMED.column(), confirmed)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, getAuthorities(connection, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserConfirmedAndAuthorities(Long id, Boolean confirmed, Set<Authority> authorities) {
        log.debug("setUserConfirmedAndAuthorities({},{},{})", id, confirmed, authorities);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.CONFIRMED.column(), confirmed)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );

            setUserAuthorities(connection, id, authorities);

            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, authorities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserEmail(Long id, String email) {
        log.debug("setUserEmail({},{})", id, email);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.EMAIL.column(), email)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, getAuthorities(connection, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserEnabled(Long id, Boolean enabled) {
        log.debug("setUserEnabled({},{})", id, enabled);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.ENABLED.column(), enabled)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, getAuthorities(connection, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserPassword(Long id, String password) {
        log.debug("setUserPassword({},{})", id, password);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.PASSWORD.column(), password)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, getAuthorities(connection, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserProfile(Long id, UserProfileDo userProfileDo) {
        log.debug("setUserProfile({},{})", id, userProfileDo);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.TITLE_BEFORE.column(), userProfileDo.titleBefore())
                    .SET(MetaColumnWiwaUser.FIRST_NAME.column(), userProfileDo.firstName())
                    .SET(MetaColumnWiwaUser.MID_NAME.column(), userProfileDo.midName())
                    .SET(MetaColumnWiwaUser.LAST_NAME.column(), userProfileDo.lastName())
                    .SET(MetaColumnWiwaUser.TITLE_AFTER.column(), userProfileDo.titleAfter())
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, getAuthorities(connection, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDo setUserProfileAndGdpr(Long id, UserProfileDo userProfileDo, Boolean gdpr) {
        log.debug("setUserProfileAndGdpr({},{},{})", id, userProfileDo, gdpr);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection, Query
                    .UPDATE(MetaTable.WIWA_USER.table())
                    .SET(MetaColumnWiwaUser.TITLE_BEFORE.column(), userProfileDo.titleBefore())
                    .SET(MetaColumnWiwaUser.FIRST_NAME.column(), userProfileDo.firstName())
                    .SET(MetaColumnWiwaUser.MID_NAME.column(), userProfileDo.midName())
                    .SET(MetaColumnWiwaUser.LAST_NAME.column(), userProfileDo.lastName())
                    .SET(MetaColumnWiwaUser.TITLE_AFTER.column(), userProfileDo.titleAfter())
                    .SET(MetaColumnWiwaUser.GDPR.column(), gdpr)
                    .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
            WiwaUserDto wiwaUserDto = getWiwaUserDto(connection, id).orElseThrow();
            return mapToDo(wiwaUserDto, getAuthorities(connection, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("deleteUser({})", id);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_USER.table())
                            .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<WiwaUserDto> getWiwaUserDto(Connection connection, Long id) throws SQLException {
        Optional<WiwaUserDto> result;
        List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaUser.columns())
                        .FROM(MetaTable.WIWA_USER.table())
                        .WHERE(MetaColumnWiwaUser.ID.column(), Condition.EQUALS, id)
        );
        if (rows.size() == 1) {
            result = Optional.of(WiwaUserDto.toObject(rows.get(0)));
        } else {
            result = Optional.empty();
        }
        return result;
    }

    private Set<Authority> getAuthorities(Connection connection, Long userId) throws SQLException {
        List<Object[]> rows = sqlBuilder.select(connection, Query
                .SELECT(MetaColumnWiwaAuthority.AUTHORITY.column())
                .FROM(MetaTable.WIWA_AUTHORITY.table())
                .LEFT_JOIN(
                        MetaTable.WIWA_USER_AUTHORITY.table(),
                        MetaColumnWiwaUserAuthority.AUTHORITY_ID.column(),
                        MetaColumnWiwaAuthority.ID.column()
                )
                .WHERE(MetaColumnWiwaUserAuthority.USER_ID.column(), Condition.EQUALS, userId)
        );
        return rows.stream().map(objects -> (String) objects[0]).map(Authority::valueOf).collect(Collectors.toSet());
    }

    private UserDo mapToDo(WiwaUserDto wiwaUserDto, Set<Authority> authorities) {
        return new UserDo(
                wiwaUserDto.id(),
                wiwaUserDto.username(),
                wiwaUserDto.password(),
                wiwaUserDto.titleBefore(),
                wiwaUserDto.firstName(),
                wiwaUserDto.midName(),
                wiwaUserDto.lastName(),
                wiwaUserDto.titleAfter(),
                wiwaUserDto.email(),
                wiwaUserDto.gdpr(),
                wiwaUserDto.confirmed(),
                wiwaUserDto.enabled(),
                authorities
        );
    }

    private void setUserAuthorities(Connection connection, Long userId, Set<Authority> authorities) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_USER_AUTHORITY.table())
                        .WHERE(MetaColumnWiwaUserAuthority.USER_ID.column(), Condition.EQUALS, userId)
        );

        List<WiwaAuthorityDto> dbAuthorities = getAuthorities(connection, authorities);

        for (WiwaAuthorityDto dbAuthority : dbAuthorities) {
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_USER_AUTHORITY.table(),
                                    MetaColumnWiwaUserAuthority.USER_ID.column(),
                                    MetaColumnWiwaUserAuthority.AUTHORITY_ID.column()
                            )
                            .VALUES(userId, dbAuthority.id())
            );
        }
    }

    private List<WiwaAuthorityDto> getAuthorities(Connection connection, Set<Authority> authorities) throws SQLException {
        if (CollectionUtils.isEmpty(authorities)) {
            return List.of();
        } else {
            List<Object[]> rows = sqlBuilder.select(connection, Query
                    .SELECT(MetaColumnWiwaAuthority.columns())
                    .FROM(MetaTable.WIWA_AUTHORITY.table())
                    .WHERE(MetaColumnWiwaAuthority.AUTHORITY.column(), Condition.IN,
                            authorities.stream().map(Authority::name).toList()
                    )
                    .ORDER_BY(MetaColumnWiwaAuthority.ID.column(), Order.ASC)
            );
            return rows.stream().map(WiwaAuthorityDto::toObject).toList();
        }
    }

    private void mapSearchCriteria(UserSearchCriteriaDo criteria, Query.Select select) {
        if (StringUtils.hasLength(criteria.searchField())) {
            String function = "lower(unaccent({0}))";
            String value = "%" + criteria.searchField() + "%";
            select.AND_IN()
                    .OR(
                            new ColumnFunction(
                                    "FUNC1",
                                    MetaColumnWiwaUser.USERNAME.column().dataType(),
                                    function,
                                    MetaColumnWiwaUser.USERNAME.column()
                            ),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            new ColumnFunction(
                                    "FUNC2",
                                    MetaColumnWiwaUser.FIRST_NAME.column().dataType(),
                                    function,
                                    MetaColumnWiwaUser.FIRST_NAME.column()
                            ),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            new ColumnFunction(
                                    "FUNC3",
                                    MetaColumnWiwaUser.MID_NAME.column().dataType(),
                                    function,
                                    MetaColumnWiwaUser.MID_NAME.column()
                            ),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            new ColumnFunction(
                                    "FUNC4",
                                    MetaColumnWiwaUser.LAST_NAME.column().dataType(),
                                    function,
                                    MetaColumnWiwaUser.LAST_NAME.column()
                            ),
                            Condition.LIKE,
                            value
                    )
                    .OR(
                            new ColumnFunction(
                                    "FUNC5",
                                    MetaColumnWiwaUser.EMAIL.column().dataType(),
                                    function,
                                    MetaColumnWiwaUser.EMAIL.column()
                            ),
                            Condition.LIKE,
                            value
                    ).OUT();
        }
        if (StringUtils.hasLength(criteria.username())) {
            select.AND(MetaColumnWiwaUser.USERNAME.column(), Condition.LIKE, "%" + criteria.username() + "%");
        }
        if (StringUtils.hasLength(criteria.email())) {
            select.AND(MetaColumnWiwaUser.EMAIL.column(), Condition.LIKE, "%" + criteria.email() + "%");
        }
    }

    private void mapOrderBy(Pageable pageable, Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "id" -> select.ORDER_BY(
                                MetaColumnWiwaUser.ID.column(),
                                mapDirection(order)
                        );
                        case "username" -> select.ORDER_BY(
                                MetaColumnWiwaUser.USERNAME.column(),
                                mapDirection(order)
                        );
                        case "password" -> select.ORDER_BY(
                                MetaColumnWiwaUser.PASSWORD.column(),
                                mapDirection(order)
                        );
                        case "titleBefore" -> select.ORDER_BY(
                                MetaColumnWiwaUser.TITLE_BEFORE.column(),
                                mapDirection(order)
                        );
                        case "firstName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.FIRST_NAME.column(),
                                mapDirection(order)
                        );
                        case "midName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.MID_NAME.column(),
                                mapDirection(order)
                        );
                        case "lastName" -> select.ORDER_BY(
                                MetaColumnWiwaUser.LAST_NAME.column(),
                                mapDirection(order)
                        );
                        case "titleAfter" -> select.ORDER_BY(
                                MetaColumnWiwaUser.TITLE_AFTER.column(),
                                mapDirection(order)
                        );
                        case "email" -> select.ORDER_BY(
                                MetaColumnWiwaUser.EMAIL.column(),
                                mapDirection(order)
                        );
                        case "gdpr" -> select.ORDER_BY(
                                MetaColumnWiwaUser.GDPR.column(),
                                mapDirection(order)
                        );
                        case "confirmed" -> select.ORDER_BY(
                                MetaColumnWiwaUser.CONFIRMED.column(),
                                mapDirection(order)
                        );
                        case "enabled" -> select.ORDER_BY(
                                MetaColumnWiwaUser.ENABLED.column(),
                                mapDirection(order)
                        );
                    }
                }
        );
    }

    private Order mapDirection(Sort.Order order) {
        return order.getDirection() == Sort.Direction.ASC ? Order.ASC : Order.DESC;
    }
}
