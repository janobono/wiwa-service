package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.BoardImageDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardImageDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaBoardImage;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
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
public class BoardImageRepositoryImpl implements BoardImageRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final BoardImageDoMapper mapper;

    @Transactional
    @Override
    public void deleteByBoardId(final long boardId) {
        log.debug("deleteByBoardId({})", boardId);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Optional<BoardImageDo> findByBoardId(final long boardId) {
        log.debug("findByBoardId({})", boardId);

        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaBoardImage.columns())
                .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId)
        );

        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaBoardImage.columns());

        return rows.stream()
                .findFirst()
                .map(WiwaBoardImageDto::toObject)
                .map(mapper::toBoardImageDo);
    }

    @Transactional
    @Override
    public BoardImageDo save(final BoardImageDo boardImageDo) {
        log.debug("save({})", boardImageDo);
        final WiwaBoardImageDto result;
        if (countByBoardId(boardImageDo.getBoardId()) == 0) {
            result = insert(mapper.toWiwaBoardImageDto(boardImageDo));
        } else {
            result = update(mapper.toWiwaBoardImageDto(boardImageDo));
        }
        return mapper.toBoardImageDo(result);
    }

    private int countByBoardId(final long boardId) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaBoardImage.BOARD_ID.column()).COUNT()
                .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    private WiwaBoardImageDto insert(final WiwaBoardImageDto wiwaBoardImageDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_BOARD_IMAGE.table(), MetaColumnWiwaBoardImage.columns())
                .VALUES(WiwaBoardImageDto.toArray(wiwaBoardImageDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaBoardImageDto;
    }

    private WiwaBoardImageDto update(final WiwaBoardImageDto wiwaBoardImageDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaBoardImage.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaBoardImageDto.toArray(wiwaBoardImageDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_BOARD_IMAGE.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, wiwaBoardImageDto.boardId())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaBoardImageDto;
    }
}
