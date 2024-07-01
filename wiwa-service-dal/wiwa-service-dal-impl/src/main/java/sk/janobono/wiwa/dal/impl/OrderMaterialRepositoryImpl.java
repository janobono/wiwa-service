package sk.janobono.wiwa.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.impl.component.R3nUtil;
import sk.janobono.wiwa.dal.impl.mapper.OrderMaterialDoMapper;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderMaterialDto;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaColumnWiwaOrderMaterial;
import sk.janobono.wiwa.dal.impl.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.OrderMaterialRepository;
import sk.r3n.jdbc.Sql;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class OrderMaterialRepositoryImpl implements OrderMaterialRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SqlBuilder sqlBuilder;
    private final R3nUtil r3nUtil;
    private final OrderMaterialDoMapper mapper;

    @Override
    public int countById(final long orderId, final long materialId, final String code) {
        log.debug("countById({},{},{})", orderId, materialId, code);
        return countByOrderIdAndMaterialIdAndCode(orderId, materialId, code);
    }

    @Transactional
    @Override
    public void deleteById(final long orderId, final long materialId, final String code) {
        log.debug("deleteById({},{},{})", orderId, materialId, code);
        final Sql sql = sqlBuilder.delete(Query
                .DELETE()
                .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, orderId)
                .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, materialId)
                .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, code)
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
    }

    @Override
    public Optional<OrderMaterialDo> findById(final long orderId, final long materialId, final String code) {
        log.debug("findById({},{},{})", orderId, materialId, code);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderMaterial.columns())
                .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, orderId)
                .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, materialId)
                .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, code)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderMaterial.columns());
        return rows.stream()
                .findFirst()
                .map(WiwaOrderMaterialDto::toObject)
                .map(mapper::toOrderMaterialDo);
    }

    @Override
    public List<OrderMaterialDo> findAllByOrderId(final long orderId) {
        log.debug("findAllByOrderId({})", orderId);
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderMaterial.columns())
                .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, orderId)
        );
        final List<Object[]> rows = r3nUtil.query(jdbcTemplate, sql, MetaColumnWiwaOrderMaterial.columns());
        return rows.stream()
                .map(WiwaOrderMaterialDto::toObject)
                .map(mapper::toOrderMaterialDo)
                .toList();
    }

    @Transactional
    @Override
    public OrderMaterialDo save(final OrderMaterialDo orderMaterialDo) {
        log.debug("save({})", orderMaterialDo);
        final WiwaOrderMaterialDto result;
        if (countByOrderIdAndMaterialIdAndCode(orderMaterialDo.getOrderId(), orderMaterialDo.getMaterialId(), orderMaterialDo.getCode()) == 0) {
            result = insert(mapper.toWiwaOrderMaterialDto(orderMaterialDo));
        } else {
            result = update(mapper.toWiwaOrderMaterialDto(orderMaterialDo));
        }
        return mapper.toOrderMaterialDo(result);
    }

    private int countByOrderIdAndMaterialIdAndCode(final long orderId, final long materialId, final String code) {
        final Sql sql = sqlBuilder.select(Query
                .SELECT(MetaColumnWiwaOrderMaterial.ORDER_ID.column(),
                        MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(),
                        MetaColumnWiwaOrderMaterial.CODE.column()
                ).COUNT()
                .FROM(MetaTable.WIWA_ORDER_MATERIAL.table())
                .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, orderId)
                .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, materialId)
                .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, code)
        );
        return r3nUtil.count(jdbcTemplate, sql);
    }

    private WiwaOrderMaterialDto insert(final WiwaOrderMaterialDto wiwaOrderMaterialDto) {
        final Sql sql = sqlBuilder.insert(Query
                .INSERT()
                .INTO(MetaTable.WIWA_ORDER_MATERIAL.table(), MetaColumnWiwaOrderMaterial.columns())
                .VALUES(WiwaOrderMaterialDto.toArray(wiwaOrderMaterialDto))
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderMaterialDto;
    }

    private WiwaOrderMaterialDto update(final WiwaOrderMaterialDto wiwaOrderMaterialDto) {
        final Sql sql = sqlBuilder.update(Query
                .UPDATE(MetaTable.WIWA_ORDER_MATERIAL.table())
                .SET(MetaColumnWiwaOrderMaterial.DATA.column(), wiwaOrderMaterialDto.data())
                .WHERE(MetaColumnWiwaOrderMaterial.ORDER_ID.column(), Condition.EQUALS, wiwaOrderMaterialDto.orderId())
                .AND(MetaColumnWiwaOrderMaterial.MATERIAL_ID.column(), Condition.EQUALS, wiwaOrderMaterialDto.materialId())
                .AND(MetaColumnWiwaOrderMaterial.CODE.column(), Condition.EQUALS, wiwaOrderMaterialDto.code())
        );
        jdbcTemplate.update(sql.toSql(), sql.getParamsObjects());
        return wiwaOrderMaterialDto;
    }
}
