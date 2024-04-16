package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderItemAttributeDo;
import sk.janobono.wiwa.dal.impl.mapper.OrderItemAttributeDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemAttributeDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderItemAttribute;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderItemAttributeRepository;
import sk.r3n.jdbc.SqlBuilder;
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
public class OrderItemAttributeRepositoryImpl implements OrderItemAttributeRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderItemAttributeDoMapper mapper;

    @Override
    public List<OrderItemAttributeDo> findAllByOrderItemId(final Long orderItemId) {
        log.debug("findAllByOrderItemId({})", orderItemId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderItemAttribute.columns())
                            .FROM(MetaTable.WIWA_ORDER_ITEM_ATTRIBUTE.table())
                            .WHERE(MetaColumnWiwaOrderItemAttribute.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
                            .ORDER_BY(MetaColumnWiwaOrderItemAttribute.ATTRIBUTE_KEY.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderItemAttributeDto::toObject)
                    .map(mapper::toOrderItemAttributeDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderItemAttributeDo save(final OrderItemAttributeDo orderItemAttributeDo) {
        log.debug("save({})", orderItemAttributeDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderItemAttributeDto result;
            if (countByOrderItemIdAndAttributeKey(connection, orderItemAttributeDo.getOrderItemId(), orderItemAttributeDo.getAttributeKey().name()) == 0) {
                result = insert(connection, mapper.toWiwaOrderItemAttributeDto(orderItemAttributeDo));
            } else {
                result = update(connection, mapper.toWiwaOrderItemAttributeDto(orderItemAttributeDo));
            }
            return mapper.toOrderItemAttributeDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByOrderItemIdAndAttributeKey(final Connection connection, final Long orderItemId, final String attributeKey) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaOrderItemAttribute.ORDER_ITEM_ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_ORDER_ITEM_ATTRIBUTE.table())
                        .WHERE(MetaColumnWiwaOrderItemAttribute.ORDER_ITEM_ID.column(), Condition.EQUALS, orderItemId)
                        .AND(MetaColumnWiwaOrderItemAttribute.ATTRIBUTE_KEY.column(), Condition.EQUALS, attributeKey)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaOrderItemAttributeDto insert(final Connection connection, final WiwaOrderItemAttributeDto wiwaOrderItemAttributeDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_ITEM_ATTRIBUTE.table(), MetaColumnWiwaOrderItemAttribute.columns())
                        .VALUES(WiwaOrderItemAttributeDto.toArray(wiwaOrderItemAttributeDto)));

        return wiwaOrderItemAttributeDto;
    }

    private WiwaOrderItemAttributeDto update(final Connection connection, final WiwaOrderItemAttributeDto wiwaOrderItemAttributeDto) throws SQLException {
        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_ITEM_ATTRIBUTE.table())
                        .SET(MetaColumnWiwaOrderItemAttribute.ATTRIBUTE_VALUE.column(), wiwaOrderItemAttributeDto.attributeValue())
                        .WHERE(MetaColumnWiwaOrderItemAttribute.ORDER_ITEM_ID.column(), Condition.EQUALS, wiwaOrderItemAttributeDto.orderItemId())
                        .AND(MetaColumnWiwaOrderItemAttribute.ATTRIBUTE_KEY.column(), Condition.EQUALS, wiwaOrderItemAttributeDto.attributeKey())
        );

        return wiwaOrderItemAttributeDto;
    }
}
