package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.ApplicationImageDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaApplicationImageDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaApplicationImage;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ApplicationImageRepositoryImpl implements ApplicationImageRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final ApplicationImageDoMapper mapper;
    private final R3nUtil r3nUtil;

    @Transactional
    @Override
    public void deleteById(final String id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Page<ApplicationImageInfoDo> findAll(final Pageable pageable) {
        log.debug("findAll({})", pageable);

        final Sql totalRowsSql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaApplicationImage.FILE_NAME.column())
                .COUNT()
                .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
        );

        final Integer totalRows = jdbcTemplate.query(totalRowsSql.toSql(),
                (resultSet) -> {
                    if (resultSet.next()) {
                        return (Integer) sqlBuilder.getColumn(resultSet, 1, DataType.INTEGER);
                    }
                    return 0;
                }
        );
        assert totalRows != null;

        if (totalRows > 0) {
            final Query.Select select = Query.SELECT(
                    MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                    MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                    MetaColumnWiwaApplicationImage.THUMBNAIL.column()
            ).FROM(MetaTable.WIWA_APPLICATION_IMAGE.table());

            if (pageable.isPaged()) {
                select.page(pageable.getPageNumber(), pageable.getPageSize());
            }

            if (pageable.getSort().isSorted()) {
                mapOrderBy(pageable, select);
            } else {
                select.ORDER_BY(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Order.ASC);
            }

            final Sql sql = sqlBuilder.select(select);
            final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, new Column[]{
                    MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                    MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                    MetaColumnWiwaApplicationImage.THUMBNAIL.column()
            });

            return new PageImpl<>(
                    rows.stream()
                            .map(row -> new ApplicationImageInfoDo(
                                    (String) row[0],
                                    (String) row[1],
                                    (byte[]) row[2])
                            )
                            .toList()
                    , pageable, totalRows);
        }
        return new PageImpl<>(Collections.emptyList(), pageable, totalRows);
    }

    @Override
    public Optional<ApplicationImageDo> findById(final String id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaApplicationImage.columns())
                .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, id)
        );

        return r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaApplicationImage.columns())
                .stream()
                .findFirst()
                .map(WiwaApplicationImageDto::toObject)
                .map(mapper::toApplicationImageDo);
    }

    @Transactional
    @Override
    public ApplicationImageDo save(final ApplicationImageDo applicationImageDo) {
        log.debug("save({})", applicationImageDo);

        final WiwaApplicationImageDto result;
        if (existsById(applicationImageDo.getFileName())) {
            result = update(mapper.toWiwaApplicationImageDto(applicationImageDo));
        } else {
            result = insert(mapper.toWiwaApplicationImageDto(applicationImageDo));
        }
        return mapper.toApplicationImageDo(result);
    }

    private boolean existsById(final String id) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaApplicationImage.FILE_NAME.column()).COUNT()
                .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, id)
        );
        return r3nUtil.exists(jdbcTemplate, sql);
    }

    private WiwaApplicationImageDto insert(final WiwaApplicationImageDto wiwaApplicationImageDto) {
        final Sql sql = sqlBuilder.insert(Query.INSERT()
                .INTO(MetaTable.WIWA_APPLICATION_IMAGE.table(), MetaColumnWiwaApplicationImage.columns())
                .VALUES(WiwaApplicationImageDto.toArray(wiwaApplicationImageDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaApplicationImageDto;
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    if (order.getProperty().equals("fileType")) {
                        select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                                r3nUtil.mapDirection(order)
                        );
                    } else {
                        select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                                r3nUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaApplicationImageDto update(final WiwaApplicationImageDto wiwaApplicationImageDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaApplicationImage.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaApplicationImageDto.toArray(wiwaApplicationImageDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_APPLICATION_IMAGE.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, wiwaApplicationImageDto.fileName())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());

        return wiwaApplicationImageDto;
    }
}
