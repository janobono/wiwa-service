package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.AuthorityDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaAuthorityDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaAuthority;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaUserAuthority;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.model.Authority;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.sql.impl.ColumnSelect;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class AuthorityRepositoryImpl implements AuthorityRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final AuthorityDoMapper mapper;

    @Override
    public int count() {
        log.debug("count()");
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaAuthority.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_AUTHORITY.table())
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Override
    public List<AuthorityDo> findAll() {
        log.debug("findAll()");

        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaAuthority.columns())
                .FROM(MetaTable.WIWA_AUTHORITY.table())
                .ORDER_BY(MetaColumnWiwaAuthority.AUTHORITY.column(), Order.ASC)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaAuthority.columns());

        return rows.stream()
                .map(WiwaAuthorityDto::toObject)
                .map(mapper::toAuthorityDo)
                .toList();
    }

    @Override
    public List<AuthorityDo> findByUserId(final long userId) {
        log.debug("findByUserId({})", userId);

        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaAuthority.columns())
                .FROM(MetaTable.WIWA_AUTHORITY.table())
                .LEFT_JOIN(MetaTable.WIWA_USER_AUTHORITY.table(), MetaColumnWiwaUserAuthority.AUTHORITY_ID.column(), MetaColumnWiwaAuthority.ID.column())
                .WHERE(MetaColumnWiwaUserAuthority.USER_ID.column(), Condition.EQUALS, userId)
                .ORDER_BY(MetaColumnWiwaAuthority.AUTHORITY.column(), Order.ASC)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaAuthority.columns());

        return rows.stream()
                .map(WiwaAuthorityDto::toObject)
                .map(mapper::toAuthorityDo)
                .toList();
    }

    @Transactional
    @Override
    public AuthorityDo save(final AuthorityDo authorityDo) {
        log.debug("save({})", authorityDo);

        final WiwaAuthorityDto result;
        if (authorityDo.getId() == null) {
            result = insert(mapper.toWiwaAuthorityDto(authorityDo));
        } else {
            result = update(mapper.toWiwaAuthorityDto(authorityDo));
        }
        return mapper.toAuthorityDo(result);
    }

    @Transactional
    @Override
    public void saveUserAuthorities(final long userId, final List<Authority> authorities) {
        log.debug("saveUserAuthorities({},{})", userId, authorities);
        deleteUserAuthorities(userId);
        insertUserAuthorities(userId, authorities);
    }

    private void deleteUserAuthorities(final Long userId) {
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_USER_AUTHORITY.table())
                .WHERE(MetaColumnWiwaUserAuthority.USER_ID.column(), Condition.EQUALS, userId)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    private WiwaAuthorityDto insert(final WiwaAuthorityDto wiwaAuthorityDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_AUTHORITY.table(), MetaColumnWiwaAuthority.AUTHORITY.column())
                .VALUES(wiwaAuthorityDto.authority())
                .RETURNING(MetaColumnWiwaAuthority.ID.column())
        );
        final long id = r3nUtil.insert(jdbcTemplate, sql);
        return new WiwaAuthorityDto(id, wiwaAuthorityDto.authority());
    }

    private void insertUserAuthorities(final Long userId, final List<Authority> authorities) {
        for (final Authority authority : authorities) {
            final Sql sql = sqlBuilder.insert(Query
                    .INSERT()
                    .INTO(MetaTable.WIWA_USER_AUTHORITY.table(), MetaColumnWiwaUserAuthority.columns())
                    .VALUES(userId, new ColumnSelect("AUTH_ID", DataType.LONG,
                            Query.SELECT(MetaColumnWiwaAuthority.ID.column())
                                    .FROM(MetaTable.WIWA_AUTHORITY.table())
                                    .WHERE(MetaColumnWiwaAuthority.AUTHORITY.column(), Condition.EQUALS, authority.toString())
                    ))
            );
            jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        }
    }

    private WiwaAuthorityDto update(final WiwaAuthorityDto wiwaAuthorityDto) {
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_AUTHORITY.table())
                .SET(MetaColumnWiwaAuthority.AUTHORITY.column(), wiwaAuthorityDto.authority())
                .WHERE(MetaColumnWiwaAuthority.ID.column(), Condition.EQUALS, wiwaAuthorityDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaAuthorityDto;
    }
}
