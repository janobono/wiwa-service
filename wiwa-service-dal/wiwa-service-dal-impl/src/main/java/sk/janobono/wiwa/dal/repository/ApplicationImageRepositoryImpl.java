package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.mapper.ApplicationImageDoMapper;
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaApplicationImageDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaApplicationImage;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ApplicationImageRepositoryImpl implements ApplicationImageRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ApplicationImageDoMapper applicationImageDoMapper;

    @Override
    public boolean exists(String fileName) {
        log.debug("exists({})", fileName);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationImage.FILE_NAME.column()).COUNT()
                            .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                            .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, fileName));
            return ((Integer) rows.get(0)[0]) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<ApplicationImageInfoDo> getApplicationImages(Pageable pageable) {
        log.debug("getApplicationImages({})", pageable);
        try (Connection connection = dataSource.getConnection()) {
            int totalRows = (Integer) sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationImage.FILE_NAME.column())
                            .COUNT()
                            .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())).get(0)[0];
            List<Object[]> rows;
            if (pageable.isPaged()) {
                Query.Select select = Query.SELECT(
                                MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                                MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                                MetaColumnWiwaApplicationImage.THUMBNAIL.column()
                        ).page(pageable.getPageNumber(), pageable.getPageSize())
                        .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table());
                mapOrderBy(pageable, select);
                rows = sqlBuilder.select(connection, select);
            } else {
                rows = sqlBuilder.select(connection,
                        Query.SELECT(
                                        MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                                        MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                                        MetaColumnWiwaApplicationImage.THUMBNAIL.column()
                                )
                                .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                                .ORDER_BY(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Order.ASC));
            }
            return new PageImpl<>(
                    rows.stream()
                            .map(row -> new ApplicationImageInfoDo(
                                    (String) row[0],
                                    (String) row[1],
                                    (byte[]) row[2]
                            ))
                            .toList()
                    , pageable, totalRows);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ApplicationImageDo> getApplicationImage(String fileName) {
        log.debug("getApplicationImage({})", fileName);
        try (Connection connection = dataSource.getConnection()) {
            List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaApplicationImage.columns())
                            .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                            .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, fileName));
            if (rows.size() == 1) {
                return Optional.of(WiwaApplicationImageDto.toObject(rows.get(0))).map(applicationImageDoMapper::mapToDo);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationImageInfoDo addApplicationImage(ApplicationImageDo applicationImageDo) {
        log.debug("addApplicationImage({})", applicationImageDo);
        try (Connection connection = dataSource.getConnection()) {
            Object[] row = WiwaApplicationImageDto.toArray(applicationImageDoMapper.mapToDto(applicationImageDo));
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_APPLICATION_IMAGE.table(), MetaColumnWiwaApplicationImage.columns())
                            .VALUES(row));
            return new ApplicationImageInfoDo(
                    applicationImageDo.fileName(), applicationImageDo.fileType(), applicationImageDo.thumbnail()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApplicationImageInfoDo setApplicationImage(ApplicationImageDo applicationImageDo) {
        log.debug("setApplicationImage({})", applicationImageDo);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.update(connection,
                    Query.UPDATE(MetaTable.WIWA_APPLICATION_IMAGE.table())
                            .SET(new Column[]{
                                            MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                                            MetaColumnWiwaApplicationImage.DATA.column()
                                    },
                                    new Object[]{
                                            applicationImageDo.fileType(),
                                            applicationImageDo.data()
                                    })
                            .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, applicationImageDo.fileName()));
            return new ApplicationImageInfoDo(
                    applicationImageDo.fileName(), applicationImageDo.fileType(), applicationImageDo.thumbnail()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteApplicationImage(String fileName) {
        log.debug("deleteApplicationImage({})", fileName);
        try (Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_APPLICATION_IMAGE.table())
                            .WHERE(MetaColumnWiwaApplicationImage.FILE_NAME.column(), Condition.EQUALS, fileName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mapOrderBy(Pageable pageable, Query.Select select) {
        pageable.getSort().stream().forEach(order -> {
                    switch (order.getProperty()) {
                        case "fileName" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.FILE_NAME.column(),
                                mapDirection(order)
                        );
                        case "fileType" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.FILE_TYPE.column(),
                                mapDirection(order)
                        );
                        case "thumbnail" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.THUMBNAIL.column(),
                                mapDirection(order)
                        );
                        case "data" -> select.ORDER_BY(
                                MetaColumnWiwaApplicationImage.DATA.column(),
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
