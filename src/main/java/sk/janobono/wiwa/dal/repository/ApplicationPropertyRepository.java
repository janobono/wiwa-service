package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaApplicationPropertyDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaApplicationProperty;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.ApplicationPropertyMapper;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ApplicationPropertyRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ApplicationPropertyMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public int count() {
        log.debug("count()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column()).COUNT()
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByGroupAndKey(final String group, final String key) {
        log.debug("deleteByGroupAndKey({},{})", group, key);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, group)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ApplicationPropertyDo> findByGroupAndKey(final String group, final String key) {
        log.debug("findByGroupAndKey({},{})", group, key);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationProperty.columns())
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, group)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaApplicationPropertyDto::toObject)
                    .map(mapper::toApplicationPropertyDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ApplicationPropertyDo save(final ApplicationPropertyDo applicationPropertyDo) {
        log.debug("save({})", applicationPropertyDo);
        try (final Connection connection = dataSource.getConnection()) {
            return save(connection, applicationPropertyDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(final List<ApplicationPropertyDo> batch) {
        log.debug("saveAll({})", batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            for (final ApplicationPropertyDo applicationPropertyDo : batch) {
                save(connection, applicationPropertyDo);
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

    private boolean exists(final Connection connection, final String group, final String key) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column()).COUNT()
                        .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                        .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, group)
                        .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .map(i -> i > 0)
                .orElse(false);
    }

    private WiwaApplicationPropertyDto insert(final Connection connection, final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_APPLICATION_PROPERTY.table(), MetaColumnWiwaApplicationProperty.columns())
                        .VALUES(WiwaApplicationPropertyDto.toArray(wiwaApplicationPropertyDto)));
        return wiwaApplicationPropertyDto;
    }

    private ApplicationPropertyDo save(final Connection connection, final ApplicationPropertyDo applicationPropertyDo) throws SQLException {
        final WiwaApplicationPropertyDto result;
        if (exists(connection, applicationPropertyDo.getGroup(), applicationPropertyDo.getKey())) {
            result = update(connection, mapper.toWiwaApplicationPropertyDto(applicationPropertyDo));
        } else {
            result = insert(connection, mapper.toWiwaApplicationPropertyDto(applicationPropertyDo));
        }
        return mapper.toApplicationPropertyDo(result);
    }

    private WiwaApplicationPropertyDto update(final Connection connection, final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaApplicationProperty.columns(), 2);
        final Object[] values = criteriaUtil.removeFirst(WiwaApplicationPropertyDto.toArray(wiwaApplicationPropertyDto), 2);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, wiwaApplicationPropertyDto.propertyGroup())
                        .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, wiwaApplicationPropertyDto.propertyKey())
        );
        return wiwaApplicationPropertyDto;
    }
}
