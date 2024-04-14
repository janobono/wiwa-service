package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderContactDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaOrderContact;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.OrderContactDoMapper;
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
public class OrderContactRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderContactDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public Optional<OrderContactDo> findByOrderId(final Long orderId) {
        log.debug("findByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderContact.columns())
                            .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                            .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, orderId)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderContactDto::toObject)
                    .map(mapper::toOrderContactDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OrderContactDo save(final OrderContactDo orderContactDo) {
        log.debug("save({})", orderContactDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderContactDto result;
            if (countByOrderId(connection, orderContactDo.getOrderId()) == 0) {
                result = insert(connection, mapper.toWiwaOrderContactDto(orderContactDo));
            } else {
                result = update(connection, mapper.toWiwaOrderContactDto(orderContactDo));
            }
            return mapper.toOrderContactDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByOrderId(final Connection connection, final Long orderId) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaOrderContact.ORDER_ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_ORDER_CONTACT.table())
                        .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, orderId)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaOrderContactDto insert(final Connection connection, final WiwaOrderContactDto wiwaOrderContactDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_CONTACT.table(), MetaColumnWiwaOrderContact.columns())
                        .VALUES(WiwaOrderContactDto.toArray(wiwaOrderContactDto)));

        return wiwaOrderContactDto;
    }

    private WiwaOrderContactDto update(final Connection connection, final WiwaOrderContactDto wiwaOrderContactDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaOrderContact.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaOrderContactDto.toArray(wiwaOrderContactDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_CONTACT.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaOrderContact.ORDER_ID.column(), Condition.EQUALS, wiwaOrderContactDto.orderId())
        );

        return wiwaOrderContactDto;
    }
}
