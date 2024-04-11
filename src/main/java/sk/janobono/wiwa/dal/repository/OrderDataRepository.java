package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderDataDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderDataDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaOrderData;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.OrderDataDoMapper;
import sk.janobono.wiwa.model.OrderDataKey;
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
public class OrderDataRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderDataDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public Optional<OrderDataDo> findByOrderIdAndKey(final Long orderId, final OrderDataKey key) {
        log.debug("findByOrderIdAndKey({},{})", orderId, key);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderData.columns())
                            .FROM(MetaTable.WIWA_ORDER_DATA.table())
                            .WHERE(MetaColumnWiwaOrderData.ORDER_ID.column(), Condition.EQUALS, orderId)
                            .AND(MetaColumnWiwaOrderData.KEY.column(), Condition.EQUALS, key.name())
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderDataDto::toObject)
                    .map(mapper::toOrderDataDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OrderDataDo save(final OrderDataDo orderDataDo) {
        log.debug("save({})", orderDataDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderDataDto wiwaOrderDataDto;
            if (orderDataDo.getId() == null) {
                wiwaOrderDataDto = insert(connection, mapper.toWiwaOrderDataDto(orderDataDo));
            } else {
                wiwaOrderDataDto = update(connection, mapper.toWiwaOrderDataDto(orderDataDo));
            }
            return mapper.toOrderDataDo(wiwaOrderDataDto);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaOrderDataDto insert(final Connection connection, final WiwaOrderDataDto wiwaOrderDataDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderData.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderDataDto.toArray(wiwaOrderDataDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_DATA.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaOrderData.ID.column()));

        return WiwaOrderDataDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaOrderDataDto update(final Connection connection, final WiwaOrderDataDto wiwaOrderDataDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderData.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderDataDto.toArray(wiwaOrderDataDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_DATA.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderData.ID.column(), Condition.EQUALS, wiwaOrderDataDto.id())
        );

        return wiwaOrderDataDto;
    }
}
