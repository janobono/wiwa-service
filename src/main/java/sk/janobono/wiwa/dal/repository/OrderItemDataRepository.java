package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderItemDataDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderItemDataDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaOrderItemData;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.OrderItemDataDoMapper;
import sk.janobono.wiwa.model.OrderItemDataKey;
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
public class OrderItemDataRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderItemDataDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public Optional<OrderItemDataDo> findByOrderItemIdAndKey(final Long orderItemId, final OrderItemDataKey key) {
        log.debug("findByOrderItemIdAndKey({},{})", orderItemId, key);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItemData.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM_DATA.table())
                            .WHERE(MetaColumnWiwaOrderItemData.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
                            .AND(MetaColumnWiwaOrderItemData.KEY.column(), Condition.EQUALS, key.name())
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderItemDataDto::toObject)
                    .map(mapper::toOrderItemDataDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OrderItemDataDo save(final OrderItemDataDo orderDataDo) {
        log.debug("save({})", orderDataDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderItemDataDto wiwaOrderItemDataDto;
            if (orderDataDo.getId() == null) {
                wiwaOrderItemDataDto = insert(connection, mapper.toWiwaOrderItemDataDto(orderDataDo));
            } else {
                wiwaOrderItemDataDto = update(connection, mapper.toWiwaOrderItemDataDto(orderDataDo));
            }
            return mapper.toOrderItemDataDo(wiwaOrderItemDataDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaOrderItemDataDto insert(final Connection connection, final WiwaOrderItemDataDto wiwaOrderItemDataDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderItemData.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderItemDataDto.toArray(wiwaOrderItemDataDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_ITEM_DATA.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrderItemData.ID.column()));

        return WiwaOrderItemDataDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderItemDataDto update(final Connection connection, final WiwaOrderItemDataDto wiwaOrderItemDataDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderItemData.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderItemDataDto.toArray(wiwaOrderItemDataDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_ITEM_DATA.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderItemData.ID.column(), Condition.EQUALS, wiwaOrderItemDataDto.id())
        );

        return wiwaOrderItemDataDto;
    }
}
