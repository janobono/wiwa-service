package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaAuthority;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class AuthorityRepositoryImpl implements AuthorityRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;

    @Override
    public long count() {
        log.debug("count()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaAuthority.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_AUTHORITY.table())
            );
            return (Integer) rows.get(0)[0];
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long addAuthority(final Authority authority) {
        log.debug("addAuthority({})", authority);
        try (final Connection connection = dataSource.getConnection()) {
            return (Long) sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_AUTHORITY.table(), MetaColumnWiwaAuthority.AUTHORITY.column())
                            .VALUES(authority.name())
                            .RETURNING(MetaColumnWiwaAuthority.ID.column())
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
