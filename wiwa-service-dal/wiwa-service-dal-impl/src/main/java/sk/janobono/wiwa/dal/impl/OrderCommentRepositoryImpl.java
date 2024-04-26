package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderCommentDo;
import sk.janobono.wiwa.dal.impl.component.CriteriaUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderCommentDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderCommentDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderComment;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderCommentRepository;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderCommentRepositoryImpl implements OrderCommentRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderCommentDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    @Override
    public List<OrderCommentDo> findAllByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderComment.columns())
                            .FROM(MetaTable.WIWA_ORDER_COMMENT.table())
                            .WHERE(MetaColumnWiwaOrderComment.ORDER_ID.column(), Condition.EQUALS, orderId)
                            .ORDER_BY(MetaColumnWiwaOrderComment.CREATED.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderCommentDto::toObject)
                    .map(mapper::toOrderCommentDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderCommentDo save(final OrderCommentDo orderCommentDo) {
        log.debug("save({})", orderCommentDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderCommentDto wiwaOrderCommentDto;
            if (orderCommentDo.getId() == null) {
                wiwaOrderCommentDto = insert(connection, mapper.toWiwaOrderCommentDto(orderCommentDo));
            } else {
                wiwaOrderCommentDto = update(connection, mapper.toWiwaOrderCommentDto(orderCommentDo));
            }
            return mapper.toOrderCommentDo(wiwaOrderCommentDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaOrderCommentDto insert(final Connection connection, final WiwaOrderCommentDto wiwaOrderCommentDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderComment.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderCommentDto.toArray(wiwaOrderCommentDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_COMMENT.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrderComment.ID.column()));

        return WiwaOrderCommentDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderCommentDto update(final Connection connection, final WiwaOrderCommentDto wiwaOrderCommentDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderComment.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderCommentDto.toArray(wiwaOrderCommentDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_COMMENT.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderComment.ID.column(), Condition.EQUALS, wiwaOrderCommentDto.id())
        );

        return wiwaOrderCommentDto;
    }
}
