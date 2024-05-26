package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.EdgeImageDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaEdgeImageDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdgeImage;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class EdgeImageRepositoryImpl implements EdgeImageRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final EdgeImageDoMapper mapper;

    @Transactional
    @Override
    public void deleteByEdgeId(final long edgeId) {
        log.debug("deleteByEdgeId({})", edgeId);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_EDGE_IMAGE.table())
                .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, edgeId));
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Optional<EdgeImageDo> findByEdgeId(final long edgeId) {
        log.debug("findByEdgeId({})", edgeId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaEdgeImage.columns())
                .FROM(MetaTable.WIWA_EDGE_IMAGE.table())
                .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, edgeId)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaEdgeImage.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaEdgeImageDto::toObject)
                .map(mapper::toEdgeImageDo);
    }

    @Transactional
    @Override
    public EdgeImageDo save(final EdgeImageDo edgeImageDo) {
        log.debug("save({})", edgeImageDo);
        final WiwaEdgeImageDto result;
        if (countByEdgeId(edgeImageDo.getEdgeId()) == 0) {
            result = insert(mapper.toWiwaEdgeImageDto(edgeImageDo));
        } else {
            result = update(mapper.toWiwaEdgeImageDto(edgeImageDo));
        }
        return mapper.toEdgeImageDo(result);
    }

    private int countByEdgeId(final long edgeId) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaEdgeImage.EDGE_ID.column()).COUNT()
                .FROM(MetaTable.WIWA_EDGE_IMAGE.table())
                .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, edgeId)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    private WiwaEdgeImageDto insert(final WiwaEdgeImageDto wiwaEdgeImageDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_EDGE_IMAGE.table(), MetaColumnWiwaEdgeImage.columns())
                .VALUES(WiwaEdgeImageDto.toArray(wiwaEdgeImageDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaEdgeImageDto;
    }

    private WiwaEdgeImageDto update(final WiwaEdgeImageDto wiwaEdgeImageDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaEdgeImage.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaEdgeImageDto.toArray(wiwaEdgeImageDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_EDGE_IMAGE.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaEdgeImage.EDGE_ID.column(), Condition.EQUALS, wiwaEdgeImageDto.edgeId())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaEdgeImageDto;
    }
}
