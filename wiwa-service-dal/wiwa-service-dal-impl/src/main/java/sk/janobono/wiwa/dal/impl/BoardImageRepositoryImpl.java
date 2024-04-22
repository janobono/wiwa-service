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
import sk.janobono.wiwa.dal.model.ApplicationImageInfoDo;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
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
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                            .WHERE(MetaColumnWiwaBoardImage.ID.column(), Condition.EQUALS, id));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ApplicationImageInfoDo> findAllByBoardId(final long boardId) {
        log.debug("findAllByBoardId({})", boardId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(
                                    MetaColumnWiwaBoardImage.FILE_NAME.column(),
                                    MetaColumnWiwaBoardImage.FILE_TYPE.column(),
                                    MetaColumnWiwaBoardImage.THUMBNAIL.column()
                            )
                            .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                            .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId)
                            .ORDER_BY(MetaColumnWiwaBoardImage.FILE_NAME.column(), Order.ASC)
            );
            return rows.stream()
                    .map(row -> new ApplicationImageInfoDo(
                            (String) row[0],
                            (String) row[1],
                            (byte[]) row[2])
                    )
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BoardImageDo> findByBoardIdAndFileName(final long boardId, final String fileName) {
        log.debug("findByBoardIdAndFileName({},{})", boardId, fileName);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaBoardImage.columns())
                            .FROM(MetaTable.WIWA_BOARD_IMAGE.table())
                            .WHERE(MetaColumnWiwaBoardImage.BOARD_ID.column(), Condition.EQUALS, boardId)
                            .AND(MetaColumnWiwaBoardImage.FILE_NAME.column(), Condition.EQUALS, fileName));
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
            if (boardImageDo.getId() == null) {
                result = insert(connection, mapper.toWiwaBoardImageDto(boardImageDo));
            } else {
                result = update(connection, mapper.toWiwaBoardImageDto(boardImageDo));
            }
            return mapper.toBoardImageDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaBoardImageDto insert(final Connection connection, final WiwaBoardImageDto wiwaBoardImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaBoardImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaBoardImageDto.toArray(wiwaBoardImageDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_BOARD_IMAGE.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaBoardImage.ID.column()));

        return WiwaBoardImageDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaBoardImageDto update(final Connection connection, final WiwaBoardImageDto wiwaBoardImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaBoardImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaBoardImageDto.toArray(wiwaBoardImageDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_BOARD_IMAGE.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaBoardImage.ID.column(), Condition.EQUALS, wiwaBoardImageDto.id())
        );

        return wiwaBoardImageDto;
    }
}
