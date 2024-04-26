package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.impl.mapper.OrderMaterialDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderMaterialDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderMaterial;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.model.OrderMaterialIdDo;
import sk.janobono.wiwa.dal.repository.OrderMaterialRepository;
import sk.r3n.jdbc.SqlBuilder;
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
public class OrderMaterialRepositoryImpl implements OrderMaterialRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final OrderMaterialDoMapper mapper;

    @Override
    public int countById(final OrderMaterialIdDo id) {
        log.debug("countByOrderIdAndMaterialIdAndCode({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            return countByOrderIdAndMaterialIdAndCode(connection, id.orderId(), id.materialId(), id.code());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(final OrderMaterialIdDo id) {
        log.debug("deleteByOrderIdAndMaterialIdAndCode({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                            .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, id.orderId())
                            .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, id.materialId())
                            .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, id.code())
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderMaterialDo> findById(final OrderMaterialIdDo id) {
        log.debug("findByOrderIdAndMaterialIdAndCode({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderMaterial.columns())
                            .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                            .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, id.orderId())
                            .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, id.materialId())
                            .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, id.code())
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaOrderMaterialDto::toObject)
                    .map(mapper::toOrderMaterialDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OrderMaterialDo> findAllByOrderId(final long orderId) {
        log.debug("findAllByOrderId({})", orderId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaOrderMaterial.columns())
                            .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                            .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, orderId)
            );
            return rows.stream()
                    .map(WiwaOrderMaterialDto::toObject)
                    .map(mapper::toOrderMaterialDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OrderMaterialDo save(final OrderMaterialDo orderMaterialDo) {
        log.debug("save({})", orderMaterialDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaOrderMaterialDto result;
            if (countByOrderIdAndMaterialIdAndCode(connection, orderMaterialDo.getOrderId(), orderMaterialDo.getMaterialId(), orderMaterialDo.getCode()) == 0) {
                result = insert(connection, mapper.toWiwaOrderMaterialDto(orderMaterialDo));
            } else {
                result = update(connection, mapper.toWiwaOrderMaterialDto(orderMaterialDo));
            }
            return mapper.toOrderMaterialDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int countByOrderIdAndMaterialIdAndCode(final Connection connection, final long orderId, final long materialId, final String code) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaOrderMaterial.ORDER_ID.column(),
                                MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(),
                                MetaColumnWiwaOrderMaterial.CODE.column()
                        ).COUNT()
                        .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                        .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, orderId)
                        .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, materialId)
                        .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, code)
        );
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .orElse(0);
    }

    private WiwaOrderMaterialDto insert(final Connection connection, final WiwaOrderMaterialDto wiwaOrderMaterialDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_ORDER_MATERIAL.table(), MetaColumnWiwaOrderMaterial.columns())
                        .VALUES(WiwaOrderMaterialDto.toArray(wiwaOrderMaterialDto)));

        return wiwaOrderMaterialDto;
    }

    private WiwaOrderMaterialDto update(final Connection connection, final WiwaOrderMaterialDto wiwaOrderMaterialDto) throws SQLException {
        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_ORDER_MATERIAL.table())
                        .SET(MetaColumnWiwaOrderMaterial.DATA.column(), wiwaOrderMaterialDto.data())
                        .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, wiwaOrderMaterialDto.orderId())
                        .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, wiwaOrderMaterialDto.materialId())
                        .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, wiwaOrderMaterialDto.code())
        );

        return wiwaOrderMaterialDto;
    }
}
