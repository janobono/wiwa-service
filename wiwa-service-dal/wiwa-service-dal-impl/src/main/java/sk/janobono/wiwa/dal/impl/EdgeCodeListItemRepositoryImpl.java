package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.CodeListItemDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListItemDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdgeCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.EdgeCodeListItemRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class EdgeCodeListItemRepositoryImpl implements EdgeCodeListItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final CodeListItemDoMapper mapper;

    @Override
    public List<CodeListItemDo> findByEdgeId(final long edgeId) {
        log.debug("findByEdgeId({})", edgeId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaCodeListItem.columns())
                .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                .LEFT_JOIN(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table(), MetaColumnWiwaEdgeCodeListItem.CODE_LIST_ITEM_ID.column(), MetaColumnWiwaCodeListItem.ID.column())
                .WHERE(MetaColumnWiwaEdgeCodeListItem.EDGE_ID.column(), Condition.EQUALS, edgeId)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaCodeListItem.columns());

        return rows.stream()
                .map(WiwaCodeListItemDto::toObject)
                .map(mapper::toCodeListItemDo)
                .toList();
    }

    @Transactional
    @Override
    public void saveAll(final long edgeId, final List<Long> itemIds) {
        log.debug("saveEdgeCodeListItems({},{})", edgeId, itemIds);
        delete(edgeId);
        insert(edgeId, itemIds);
    }

    private void delete(final long edgeId) {
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table())
                .WHERE(MetaColumnWiwaEdgeCodeListItem.EDGE_ID.column(), Condition.EQUALS, edgeId)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    private void insert(final long edgeId, final List<Long> itemIds) {
        for (final Long itemId : itemIds) {
            final Sql sql = sqlBuilder.insert(Query
                    .INSERT()
                    .INTO(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table(), MetaColumnWiwaEdgeCodeListItem.columns())
                    .VALUES(edgeId, itemId)
            );
            jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        }
    }
}
