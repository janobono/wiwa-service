package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.impl.mapper.ApplicationPropertyDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaApplicationPropertyDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaApplicationProperty;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.r3n.jdbc.SqlBuilder;
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
public class ApplicationPropertyRepositoryImpl implements ApplicationPropertyRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ApplicationPropertyDoMapper mapper;

    @Override
    public void deleteByKey(final String key) {
        log.debug("deleteByGroupAndKey({})", key);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ApplicationPropertyDo> findByKey(final String key) {
        log.debug("findByGroupAndKey({})", key);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationProperty.columns())
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaApplicationPropertyDto::toObject)
                    .map(mapper::toApplicationPropertyDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationPropertyDo save(final ApplicationPropertyDo applicationPropertyDo) {
        log.debug("save({})", applicationPropertyDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaApplicationPropertyDto result;
            if (exists(connection, applicationPropertyDo.getKey())) {
                result = update(connection, mapper.toWiwaApplicationPropertyDto(applicationPropertyDo));
            } else {
                result = insert(connection, mapper.toWiwaApplicationPropertyDto(applicationPropertyDo));
            }
            return mapper.toApplicationPropertyDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean exists(final Connection connection, final String key) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column()).COUNT()
                        .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                        .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
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

    private WiwaApplicationPropertyDto update(final Connection connection, final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) throws SQLException {
        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                        .SET(MetaColumnWiwaApplicationProperty.PROPERTY_VALUE.column(), wiwaApplicationPropertyDto.propertyValue())
                        .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, wiwaApplicationPropertyDto.propertyKey())
        );
        return wiwaApplicationPropertyDto;
    }
}
