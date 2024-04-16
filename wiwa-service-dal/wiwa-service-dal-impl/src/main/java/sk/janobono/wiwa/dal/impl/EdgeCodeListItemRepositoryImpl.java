package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.impl.mapper.CodeListItemDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListItemDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaEdgeCodeListItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.EdgeCodeListItemRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class EdgeCodeListItemRepositoryImpl implements EdgeCodeListItemRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final CodeListItemDoMapper mapper;

    @Override
    public List<CodeListItemDo> findByEdgeId(final Long edgeId) {
        log.debug("findByEdgeId({})", edgeId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.columns())
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .LEFT_JOIN(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table(), MetaColumnWiwaEdgeCodeListItem.CODE_LIST_ITEM_ID.column(), MetaColumnWiwaCodeListItem.ID.column())
                            .WHERE(MetaColumnWiwaEdgeCodeListItem.EDGE_ID.column(), Condition.EQUALS, edgeId)
            );
            return rows.stream()
                    .map(WiwaCodeListItemDto::toObject)
                    .map(mapper::toCodeListItemDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAll(final Long edgeId, final List<Long> itemIds) {
        log.debug("saveEdgeCodeListItems({},{})", edgeId, itemIds);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            delete(connection, edgeId);
            insert(connection, edgeId, itemIds);

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void delete(final Connection connection, final Long edgeId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table())
                        .WHERE(MetaColumnWiwaEdgeCodeListItem.EDGE_ID.column(), Condition.EQUALS, edgeId)
        );
    }

    private void insert(final Connection connection, final Long edgeId, final List<Long> itemIds) throws SQLException {
        for (final Long itemId : itemIds) {
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_EDGE_CODE_LIST_ITEM.table(), MetaColumnWiwaEdgeCodeListItem.columns())
                            .VALUES(edgeId, itemId)
            );
        }
    }
}
