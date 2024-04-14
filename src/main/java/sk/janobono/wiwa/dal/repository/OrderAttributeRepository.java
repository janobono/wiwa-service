package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderAttributeDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaOrderAttribute;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.mapper.OrderAttributeDoMapper;
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
public class OrderAttributeRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderAttributeDoMapper mapper;

    public List<OrderAttributeDo> findAllByOrderId(final Long orderId) {
        log.debug("findAllByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderAttribute.columns())
                            .FROM(MetaTable.WIWA_ORDER_ATTRIBUTE.table())
                            .WHERE(MetaColumnWiwaOrderAttribute.ORDER_ID.column(), Condition.EQUALS, orderId)
                            .ORDER_BY(MetaColumnWiwaOrderAttribute.ATTRIBUTE_KEY.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaOrderAttributeDto::toObject)
                    .map(mapper::toOrderAttributeDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OrderAttributeDo save(final OrderAttributeDo orderAttributeDo) {
        log.debug("save({})", orderAttributeDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderAttributeDto result;
            if (countByOrderIdAndAttributeKey(connection, orderAttributeDo.getOrderId(), orderAttributeDo.getAttributeKey().name()) == 0) {
                result = insert(connection, mapper.toWiwaOrderAttributeDto(orderAttributeDo));
            } else {
                result = update(connection, mapper.toWiwaOrderAttributeDto(orderAttributeDo));
            }
            return mapper.toOrderAttributeDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByOrderIdAndAttributeKey(final Connection connection, final Long orderId, final String attributeKey) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaOrderAttribute.ORDER_ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_CODE_LIST.table())
                        .WHERE(MetaColumnWiwaOrderAttribute.ORDER_ID.column(), Condition.EQUALS, orderId)
                        .AND(MetaColumnWiwaOrderAttribute.ATTRIBUTE_KEY.column(), Condition.EQUALS, attributeKey)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaOrderAttributeDto insert(final Connection connection, final WiwaOrderAttributeDto wiwaOrderAttributeDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_ATTRIBUTE.table(), MetaColumnWiwaOrderAttribute.columns())
                        .VALUES(WiwaOrderAttributeDto.toArray(wiwaOrderAttributeDto)));

        return wiwaOrderAttributeDto;
    }

    private WiwaOrderAttributeDto update(final Connection connection, final WiwaOrderAttributeDto wiwaOrderAttributeDto) throws SQLException {
        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_ATTRIBUTE.table())
                        .SET(MetaColumnWiwaOrderAttribute.ATTRIBUTE_VALUE.column(), wiwaOrderAttributeDto.attributeValue())
                        .WHERE(MetaColumnWiwaOrderAttribute.ORDER_ID.column(), Condition.EQUALS, wiwaOrderAttributeDto.orderId())
                        .AND(MetaColumnWiwaOrderAttribute.ATTRIBUTE_KEY.column(), Condition.EQUALS, wiwaOrderAttributeDto.attributeKey())
        );

        return wiwaOrderAttributeDto;
    }
}
