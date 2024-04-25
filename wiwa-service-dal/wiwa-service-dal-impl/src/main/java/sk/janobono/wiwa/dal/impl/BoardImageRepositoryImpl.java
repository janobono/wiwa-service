package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.BoardImageDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardImageDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaBoardImage;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
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
public class BoardImageRepositoryImpl implements BoardImageRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final BoardImageDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public void deleteByBoardId(final long boardId) {
        log.debug("deleteByBoardId({})", boardId);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                            .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BoardImageDo> findByBoardId(final long boardId) {
        log.debug("findByBoardId({})", boardId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaBoardImage.columns())
                            .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                            .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId));
            return rows.stream()
                    .findFirst()
                    .map(WiwaBoardImageDto::toObject)
                    .map(mapper::toBoardImageDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BoardImageDo save(final BoardImageDo boardImageDo) {
        log.debug("save({})", boardImageDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaBoardImageDto result;
            if (countByBoardId(connection, boardImageDo.getBoardId()) == 0) {
                result = insert(connection, mapper.toWiwaBoardImageDto(boardImageDo));
            } else {
                result = update(connection, mapper.toWiwaBoardImageDto(boardImageDo));
            }
            return mapper.toBoardImageDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByBoardId(final Connection connection, final long boardId) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaBoardImage.BOARD_ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                        .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaBoardImageDto insert(final Connection connection, final WiwaBoardImageDto wiwaBoardImageDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_BOARD_IMAGE.table(), MetaColumnWiwaBoardImage.columns())
                        .VALUES(WiwaBoardImageDto.toArray(wiwaBoardImageDto)));

        return wiwaBoardImageDto;
    }

    private WiwaBoardImageDto update(final Connection connection, final WiwaBoardImageDto wiwaBoardImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaBoardImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaBoardImageDto.toArray(wiwaBoardImageDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_BOARD_IMAGE.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, wiwaBoardImageDto.boardId())
        );

        return wiwaBoardImageDto;
    }
}
