package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.EdgeImageDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaEdgeImageDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdgeImage;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;
import sk.r3n.jdbc.SqlBuilder;
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
public class EdgeImageRepositoryImpl implements EdgeImageRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final EdgeImageDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public void deleteByEdgeId(final long edgeId) {
        log.debug("deleteByEdgeId({})", edgeId);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_EDGE_IMAGE.table())
                            .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, edgeId));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EdgeImageDo> findByEdgeId(final long edgeId) {
        log.debug("findByEdgeId({})", edgeId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaEdgeImage.columns())
                            .FROM(MetaTable.WIWA_EDGE_IMAGE.table())
                            .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, edgeId));
            return rows.stream()
                    .findFirst()
                    .map(WiwaEdgeImageDto::toObject)
                    .map(mapper::toEdgeImageDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EdgeImageDo save(final EdgeImageDo edgeImageDo) {
        log.debug("save({})", edgeImageDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaEdgeImageDto result;
            if (countByEdgeId(connection, edgeImageDo.getEdgeId()) == 0) {
                result = insert(connection, mapper.toWiwaEdgeImageDto(edgeImageDo));
            } else {
                result = update(connection, mapper.toWiwaEdgeImageDto(edgeImageDo));
            }
            return mapper.toEdgeImageDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByEdgeId(final Connection connection, final long edgeId) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaEdgeImage.EDGE_ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_EDGE_IMAGE.table())
                        .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, edgeId)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaEdgeImageDto insert(final Connection connection, final WiwaEdgeImageDto wiwaEdgeImageDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_EDGE_IMAGE.table(), MetaColumnWiwaEdgeImage.columns())
                        .VALUES(WiwaEdgeImageDto.toArray(wiwaEdgeImageDto)));

        return wiwaEdgeImageDto;
    }

    private WiwaEdgeImageDto update(final Connection connection, final WiwaEdgeImageDto wiwaEdgeImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaEdgeImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaEdgeImageDto.toArray(wiwaEdgeImageDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_EDGE_IMAGE.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, wiwaEdgeImageDto.edgeId())
        );

        return wiwaEdgeImageDto;
    }
}
