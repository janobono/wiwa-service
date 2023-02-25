package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.mapper.ApplicationPropertyDoMapper;
import sk.janobono.wiwa.dal.r3n.dto.WiwaApplicationPropertyDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaApplicationProperty;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ApplicationPropertyRepositoryImpl implements ApplicationPropertyRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ApplicationPropertyDoMapper applicationPropertyDoMapper;

    @Override
    public long count() {
        log.debug("count()");
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column()).COUNT()
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
            );
            return (Integer) rows.get(0)[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String group, String key, String language) {
        log.debug("exists({},{},{})", group, key, language);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column()).COUNT()
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, group)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_LANGUAGE.column(), Condition.EQUALS, language)
            );
            return ((Integer) rows.get(0)[0]) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ApplicationPropertyDo> getApplicationProperty(String group, String key, String language) {
        log.debug("getApplicationProperty({},{},{})", group, key, language);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationProperty.columns())
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, group)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_LANGUAGE.column(), Condition.EQUALS, language)
            );
            if (rows.size() == 1) {
                return Optional.of(WiwaApplicationPropertyDto.toObject(rows.get(0))).map(applicationPropertyDoMapper::mapToDo);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationPropertyDo addApplicationProperty(ApplicationPropertyDo applicationPropertyDo) {
        log.debug("addApplicationProperty({})", applicationPropertyDo);
        try (Connection connection = dataSource.getConnection()) {
            Object[] row = WiwaApplicationPropertyDto.toArray(applicationPropertyDoMapper.mapToDto(applicationPropertyDo));
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_APPLICATION_PROPERTY.table(), MetaColumnWiwaApplicationProperty.columns())
                            .VALUES(row));
            return applicationPropertyDo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationPropertyDo setApplicationProperty(ApplicationPropertyDo applicationPropertyDo) {
        log.debug("setApplicationProperty({})", applicationPropertyDo);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection,
                    Query.UPDATE(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .SET(new Column[]{MetaColumnWiwaApplicationProperty.PROPERTY_VALUE.column()}, new Object[]{applicationPropertyDo.value()})
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, applicationPropertyDo.group())
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, applicationPropertyDo.key())
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_LANGUAGE.column(), Condition.EQUALS, applicationPropertyDo.language())
            );
            return applicationPropertyDo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteApplicationProperty(String group, String key, String language) {
        log.debug("deleteApplicationProperty({},{},{})", group, key, language);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                            .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_GROUP.column(), Condition.EQUALS, group)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
                            .AND(MetaColumnWiwaApplicationProperty.PROPERTY_LANGUAGE.column(), Condition.EQUALS, language)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
