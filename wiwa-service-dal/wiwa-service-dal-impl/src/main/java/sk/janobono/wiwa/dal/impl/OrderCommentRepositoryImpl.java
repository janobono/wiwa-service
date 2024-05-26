package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderCommentDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderCommentDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderCommentDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderComment;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderCommentRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderCommentRepositoryImpl implements OrderCommentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderCommentDoMapper mapper;

    @Override
    public List<OrderCommentDo> findAllByOrderId(final long orderId) {
        log.debug("findByOrderId({})", orderId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderComment.columns())
                .FROM(MetaTable.WIWA_ORDER_COMMENT.table())
                .WHERE(MetaColumnWiwaOrderComment.ORDER_ID.column(), Condition.EQUALS, orderId)
                .ORDER_BY(MetaColumnWiwaOrderComment.CREATED.column(), Order.ASC)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderComment.columns());
        return rows.stream()
                .map(WiwaOrderCommentDto::toObject)
                .map(mapper::toOrderCommentDo)
                .toList();
    }

    @Transactional
    @Override
    public OrderCommentDo save(final OrderCommentDo orderCommentDo) {
        log.debug("save({})", orderCommentDo);
        final WiwaOrderCommentDto wiwaOrderCommentDto;
        if (orderCommentDo.getId() == null) {
            wiwaOrderCommentDto = insert(mapper.toWiwaOrderCommentDto(orderCommentDo));
        } else {
            wiwaOrderCommentDto = update(mapper.toWiwaOrderCommentDto(orderCommentDo));
        }
        return mapper.toOrderCommentDo(wiwaOrderCommentDto);
    }

    private WiwaOrderCommentDto insert(final WiwaOrderCommentDto wiwaOrderCommentDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderComment.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderCommentDto.toArray(wiwaOrderCommentDto), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_COMMENT.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaOrderComment.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return WiwaOrderCommentDto.toObject(r3nUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderCommentDto update(final WiwaOrderCommentDto wiwaOrderCommentDto) {
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderComment.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderCommentDto.toArray(wiwaOrderCommentDto), 1);

        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER_COMMENT.table())
                .SET(columns, values)
                .WHERE(MetaColumnWiwaOrderComment.ID.column(), Condition.EQUALS, wiwaOrderCommentDto.id())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderCommentDto;
    }
}
