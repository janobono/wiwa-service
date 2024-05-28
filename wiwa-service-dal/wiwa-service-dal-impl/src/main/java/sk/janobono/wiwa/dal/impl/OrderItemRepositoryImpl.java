package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderItemDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderItem;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.OderItemSortNumDo;
import sk.janobono.wiwa.dal.model.OrderItemInfoDo;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderItemDoMapper mapper;

    @Override
    public int countByOrderId(final long orderId) {
        log.debug("countByOrderId({})", orderId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderItem.ID.column()).COUNT()
                .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        log.debug("deleteById({})", id);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Optional<OrderItemDo> findById(final long id) {
        log.debug("findById({})", id);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderItem.columns())
                .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderItem.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderItemDto::toObject)
                .map(mapper::toOrderItemDo);
    }

    @Override
    public Optional<OrderItemDo> findByOrderIdAndSortNum(final long orderId, final int sortNum) {
        log.debug("findByOrderIdAndSortNum({},{})", orderId, sortNum);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderItem.columns())
                .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
                .AND(MetaColumnWiwaOrderItem.SORT_NUM.column(), Condition.EQUALS, sortNum)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderItem.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderItemDto::toObject)
                .map(mapper::toOrderItemDo);
    }

    @Override
    public List<OrderItemDo> findAllByOrderId(final long orderId) {
        log.debug("findAllByOrderId({})", orderId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderItem.columns())
                .FROM(MetaTable.WIWA_ORDER_ITEM.table())
                .WHERE(MetaColumnWiwaOrderItem.ORDER_ID.column(), Condition.EQUALS, orderId)
                .ORDER_BY(MetaColumnWiwaOrderItem.SORT_NUM.column(), Order.ASC)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderItem.columns());
        return rows.stream()
                .map(WiwaOrderItemDto::toObject)
                .map(mapper::toOrderItemDo)
                .toList();
    }

    @Transactional
    @Override
    public OrderItemDo insert(final OrderItemDo orderItemDo) {
        log.debug("insert({})", orderItemDo);
        final Column[] columns = r3nUtil.removeFirst(MetaColumnWiwaOrderItem.columns(), 1);
        final Object[] values = r3nUtil.removeFirst(WiwaOrderItemDto.toArray(mapper.toWiwaOrderItemDto(orderItemDo)), 1);

        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_ITEM.table(), columns)
                .VALUES(values).RETURNING(MetaColumnWiwaOrderItem.ID.column())
        );
        final Long id = r3nUtil.insert(jdbcTemplate, sql);
        return mapper.toOrderItemDo(WiwaOrderItemDto.toObject(r3nUtil.concat(new Object[]{id}, values)));
    }

    @Transactional
    @Override
    public void setSortNums(final List<OderItemSortNumDo> sortNums) {
        log.debug("setSortNums({})", sortNums);
        for (final OderItemSortNumDo oderItemSortNumDo : sortNums) {
            final Sql sql = sqlBuilder.update(Query
                    .UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                    .SET(MetaColumnWiwaOrderItem.SORT_NUM.column(), oderItemSortNumDo.sortNum())
                    .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, oderItemSortNumDo.id())
            );
            jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        }
    }

    @Transactional
    @Override
    public void setOrderItemInfo(final long id, final OrderItemInfoDo orderItemInfo) {
        log.debug("setOrderItemInfo({},{})", id, orderItemInfo);
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                .SET(MetaColumnWiwaOrderItem.NAME.column(), orderItemInfo.name())
                .SET(MetaColumnWiwaOrderItem.DESCRIPTION.column(), orderItemInfo.description())
                .SET(MetaColumnWiwaOrderItem.QUANTITY.column(), orderItemInfo.quantity())
                .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Transactional
    @Override
    public void setPart(final long id, final String part) {
        log.debug("setPart({},{})", id, part);
        final Sql sql = sqlBuilder.update(Query.UPDATE(MetaTable.WIWA_ORDER_ITEM.table())
                .SET(MetaColumnWiwaOrderItem.PART.column(), part)
                .WHERE(MetaColumnWiwaOrderItem.ID.column(), Condition.EQUALS, id)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }
}
