package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaAuthorityDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaAuthority;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaUserAuthority;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.mapper.AuthorityDoMapper;
import sk.janobono.wiwa.model.Authority;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Condition;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.sql.impl.ColumnSelect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class AuthorityRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final AuthorityDoMapper mapper;

    public int count() {
        log.debug("count()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaAuthority.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_AUTHORITY.table())
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<AuthorityDo> findAll() {
        log.debug("findAll()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaAuthority.columns())
                            .FROM(MetaTable.WIWA_AUTHORITY.table())
                            .ORDER_BY(MetaColumnWiwaAuthority.AUTHORITY.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaAuthorityDto::toObject)
                    .map(mapper::toAuthorityDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<AuthorityDo> findByUserId(final Long userId) {
        log.debug("findByUserId({})", userId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaAuthority.columns())
                            .FROM(MetaTable.WIWA_AUTHORITY.table())
                            .LEFT_JOIN(MetaTable.WIWA_USER_AUTHORITY.table(), MetaColumnWiwaUserAuthority.AUTHORITY_ID.column(), MetaColumnWiwaAuthority.ID.column())
                            .WHERE(MetaColumnWiwaUserAuthority.USER_ID.column(), Condition.EQUALS, userId)
                            .ORDER_BY(MetaColumnWiwaAuthority.AUTHORITY.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaAuthorityDto::toObject)
                    .map(mapper::toAuthorityDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AuthorityDo save(final AuthorityDo authorityDo) {
        log.debug("save({})", authorityDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaAuthorityDto result;
            if (authorityDo.getId() == null) {
                result = insert(connection, mapper.toWiwaAuthorityDto(authorityDo));
            } else {
                result = update(connection, mapper.toWiwaAuthorityDto(authorityDo));
            }
            return mapper.toAuthorityDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUserAuthorities(final Long userId, final List<Authority> authorities) {
        log.debug("saveUserAuthorities({},{})", userId, authorities);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            deleteUserAuthorities(connection, userId);
            insertUserAuthorities(connection, userId, authorities);

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void deleteUserAuthorities(final Connection connection, final Long userId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_USER_AUTHORITY.table())
                        .WHERE(MetaColumnWiwaUserAuthority.USER_ID.column(), Condition.EQUALS, userId)
        );
    }

    private WiwaAuthorityDto insert(final Connection connection, final WiwaAuthorityDto wiwaAuthorityDto) throws SQLException {
        final long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_AUTHORITY.table(), MetaColumnWiwaAuthority.AUTHORITY.column())
                        .VALUES(wiwaAuthorityDto.authority())
                        .RETURNING(MetaColumnWiwaAuthority.ID.column())
        );
        return new WiwaAuthorityDto(id, wiwaAuthorityDto.authority());
    }

    private void insertUserAuthorities(final Connection connection, final Long userId, final List<Authority> authorities) throws SQLException {
        for (final Authority authority : authorities) {
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_USER_AUTHORITY.table(), MetaColumnWiwaUserAuthority.columns())
                            .VALUES(userId, new ColumnSelect("AUTH_ID", DataType.LONG,
                                    Query.SELECT(MetaColumnWiwaAuthority.ID.column())
                                            .FROM(MetaTable.WIWA_AUTHORITY.table())
                                            .WHERE(MetaColumnWiwaAuthority.AUTHORITY.column(), Condition.EQUALS, authority.toString())
                            ))
            );
        }
    }

    private WiwaAuthorityDto update(final Connection connection, final WiwaAuthorityDto wiwaAuthorityDto) throws SQLException {
        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_AUTHORITY.table())
                        .SET(MetaColumnWiwaAuthority.AUTHORITY.column(), wiwaAuthorityDto.authority())
                        .WHERE(MetaColumnWiwaAuthority.ID.column(), Condition.EQUALS, wiwaAuthorityDto.id())
        );
        return wiwaAuthorityDto;
    }
}
