package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.ApplicationPropertyDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaApplicationPropertyDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaApplicationProperty;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ApplicationPropertyRepositoryImpl implements ApplicationPropertyRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final ApplicationPropertyDoMapper mapper;

    @Transactional
    @Override
    public void deleteByKey(final String key) {
        log.debug("deleteByKey({})", key);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Optional<ApplicationPropertyDo> findByKey(final String key) {
        log.debug("findByKey({})", key);

        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaApplicationProperty.columns())
                .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaApplicationProperty.columns());

        return rows.stream()
                .findFirst()
                .map(WiwaApplicationPropertyDto::toObject)
                .map(mapper::toApplicationPropertyDo);
    }

    @Transactional
    @Override
    public ApplicationPropertyDo save(final ApplicationPropertyDo applicationPropertyDo) {
        log.debug("save({})", applicationPropertyDo);
        final WiwaApplicationPropertyDto result;
        if (exists(applicationPropertyDo.getKey())) {
            result = update(mapper.toWiwaApplicationPropertyDto(applicationPropertyDo));
        } else {
            result = insert(mapper.toWiwaApplicationPropertyDto(applicationPropertyDo));
        }
        return mapper.toApplicationPropertyDo(result);
    }

    private boolean exists(final String key) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column()).COUNT()
                .FROM(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, key)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    private WiwaApplicationPropertyDto insert(final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_APPLICATION_PROPERTY.table(), MetaColumnWiwaApplicationProperty.columns())
                .VALUES(WiwaApplicationPropertyDto.toArray(wiwaApplicationPropertyDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaApplicationPropertyDto;
    }

    private WiwaApplicationPropertyDto update(final WiwaApplicationPropertyDto wiwaApplicationPropertyDto) {
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_APPLICATION_PROPERTY.table())
                .SET(MetaColumnWiwaApplicationProperty.PROPERTY_VALUE.column(), wiwaApplicationPropertyDto.propertyValue())
                .WHERE(MetaColumnWiwaApplicationProperty.PROPERTY_KEY.column(), Condition.EQUALS, wiwaApplicationPropertyDto.propertyKey())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaApplicationPropertyDto;
    }
}
