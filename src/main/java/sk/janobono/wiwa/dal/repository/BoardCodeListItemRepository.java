package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaCodeListItemDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaBoardCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaCodeListItem;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.mapper.CodeListItemDoMapper;
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
public class BoardCodeListItemRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final CodeListItemDoMapper mapper;

    public List<CodeListItemDo> findByBoardId(final Long boardId) {
        log.debug("findByBoardId({})", boardId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaCodeListItem.columns())
                            .FROM(MetaTable.WIWA_CODE_LIST_ITEM.table())
                            .LEFT_JOIN(MetaTable.WIWA_BOARD_CODE_LIST_ITEM.table(), MetaColumnWiwaBoardCodeListItem.CODE_LIST_ITEM_ID.column(), MetaColumnWiwaCodeListItem.ID.column())
                            .WHERE(MetaColumnWiwaBoardCodeListItem.BOARD_ID.column(), Condition.EQUALS, boardId)
            );
            return rows.stream()
                    .map(WiwaCodeListItemDto::toObject)
                    .map(mapper::toCodeListItemDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(final Long boardId, final List<Long> itemIds) {
        log.debug("saveBoardCodeListItems({},{})", boardId, itemIds);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            delete(connection, boardId);
            insert(connection, boardId, itemIds);

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void delete(final Connection connection, final Long boardId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_BOARD_CODE_LIST_ITEM.table())
                        .WHERE(MetaColumnWiwaBoardCodeListItem.BOARD_ID.column(), Condition.EQUALS, boardId)
        );
    }

    private void insert(final Connection connection, final Long boardId, final List<Long> itemIds) throws SQLException {
        for (final Long itemId : itemIds) {
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_BOARD_CODE_LIST_ITEM.table(), MetaColumnWiwaBoardCodeListItem.columns())
                            .VALUES(boardId, itemId)
            );
        }
    }
}
