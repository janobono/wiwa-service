package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.ApplicationImageDoMapper;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaApplicationImageDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaApplicationImage;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;
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
public class ApplicationImageRepositoryImpl implements ApplicationImageRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ApplicationImageDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public void deleteById(final String id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                            .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, id));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<ApplicationImageInfoDo> findAll(final Pageable pageable) {
        log.debug("findAll({})", pageable);
        try (final Connection connection = dataSource.getConnection()) {
            final int totalRows = sqlBuilder.select(connection,
                            Query.SELECT(MetaColumnWiwaApplicationImage.FILE_NAME.column())
                                    .COUNT()
                                    .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table()))
                    .stream()
                    .findFirst()
                    .map(data -> (Integer) data[0])
                    .orElse(0);
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

                final List<Object[]> rows = sqlBuilder.select(connection, select);
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
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ApplicationImageDo> findById(final String id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationImage.columns())
                            .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                            .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, id));
            return rows.stream()
                    .findFirst()
                    .map(WiwaApplicationImageDto::toObject)
                    .map(mapper::toApplicationImageDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationImageDo save(final ApplicationImageDo applicationImageDo) {
        log.debug("save({})", applicationImageDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaApplicationImageDto result;
            if (existsById(connection, applicationImageDo.getFileName())) {
                result = update(connection, mapper.toWiwaApplicationImageDto(applicationImageDo));
            } else {
                result = insert(connection, mapper.toWiwaApplicationImageDto(applicationImageDo));
            }
            return mapper.toApplicationImageDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean existsById(final Connection connection, final String id) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaApplicationImage.FILE_NAME.column()).COUNT()
                        .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                        .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, id));
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .map(i -> i > 0)
                .orElse(false);
    }

    private WiwaApplicationImageDto insert(final Connection connection, final WiwaApplicationImageDto wiwaApplicationImageDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_APPLICATION_IMAGE.table(), MetaColumnWiwaApplicationImage.columns())
                        .VALUES(WiwaApplicationImageDto.toArray(wiwaApplicationImageDto)));
        return wiwaApplicationImageDto;
    }

    private void mapOrderBy(final Pageable pageable, final Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "fileName" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "fileType" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "thumbnail" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.THUMBNAIL.column(),
                                criteriaUtil.mapDirection(order)
                        );
                        case "data" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.DATA.column(),
                                criteriaUtil.mapDirection(order)
                        );
                    }
                }
        );
    }

    private WiwaApplicationImageDto update(final Connection connection, final WiwaApplicationImageDto wiwaApplicationImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaApplicationImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaApplicationImageDto.toArray(wiwaApplicationImageDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_APPLICATION_IMAGE.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, wiwaApplicationImageDto.fileName()));

        return wiwaApplicationImageDto;
    }
}
