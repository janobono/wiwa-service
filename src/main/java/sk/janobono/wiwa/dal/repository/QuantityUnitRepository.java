package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.QuantityUnitDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaQuantityUnitDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaQuantityUnit;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.QuantityUnitMapper;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class QuantityUnitRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final QuantityUnitMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public int count() {
        log.debug("count()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaQuantityUnit.ID.column()).COUNT()
                            .FROM(MetaTable.WIWA_QUANTITY_UNIT.table())
            );
            return rows.stream()
                    .findFirst()
                    .map(row -> (Integer) row[0])
                    .orElse(0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(final String id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_QUANTITY_UNIT.table())
                            .WHERE(MetaColumnWiwaQuantityUnit.ID.column(), Condition.EQUALS, id)
            );
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(final String id) {
        log.debug("existsById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            return existsById(connection, id);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QuantityUnitDo> findAll() {
        log.debug("findAll()");
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaQuantityUnit.columns())
                            .FROM(MetaTable.WIWA_QUANTITY_UNIT.table())
                            .ORDER_BY(MetaColumnWiwaQuantityUnit.ID.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaQuantityUnitDto::toObject)
                    .map(mapper::toQuantityUnitDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<QuantityUnitDo> findById(final String id) {
        log.debug("findById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaQuantityUnit.columns())
                            .FROM(MetaTable.WIWA_QUANTITY_UNIT.table())
                            .WHERE(MetaColumnWiwaQuantityUnit.ID.column(), Condition.EQUALS, id)
            );
            return rows.stream()
                    .findFirst()
                    .map(WiwaQuantityUnitDto::toObject)
                    .map(mapper::toQuantityUnitDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public QuantityUnitDo save(final QuantityUnitDo quantityUnitDo) {
        log.debug("save({})", quantityUnitDo);
        try (final Connection connection = dataSource.getConnection()) {
            return save(connection, quantityUnitDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAll(final List<QuantityUnitDo> batch) {
        log.debug("saveAll({})", batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            for (final QuantityUnitDo quantityUnitDo : batch) {
                save(connection, quantityUnitDo);
            }

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private boolean existsById(final Connection connection, final String id) throws SQLException {
        final List<Object[]> rows = sqlBuilder.select(connection,
                Query.SELECT(MetaColumnWiwaQuantityUnit.ID.column()).COUNT()
                        .FROM(MetaTable.WIWA_QUANTITY_UNIT.table())
                        .WHERE(MetaColumnWiwaQuantityUnit.ID.column(), Condition.EQUALS, id));
        return rows.stream()
                .findFirst()
                .map(row -> (Integer) row[0])
                .map(i -> i > 0)
                .orElse(false);
    }

    private WiwaQuantityUnitDto insert(final Connection connection, final WiwaQuantityUnitDto wiwaQuantityUnitDto) throws SQLException {
        sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_QUANTITY_UNIT.table(), MetaColumnWiwaQuantityUnit.columns())
                        .VALUES(WiwaQuantityUnitDto.toArray(wiwaQuantityUnitDto))
        );
        return wiwaQuantityUnitDto;
    }

    private QuantityUnitDo save(final Connection connection, final QuantityUnitDo quantityUnitDo) throws SQLException {
        final WiwaQuantityUnitDto result;
        if (existsById(connection, quantityUnitDo.getId())) {
            result = update(connection, mapper.toWiwaQuantityUnitDto(quantityUnitDo));
        } else {
            result = insert(connection, mapper.toWiwaQuantityUnitDto(quantityUnitDo));
        }
        return mapper.toQuantityUnitDo(result);
    }

    private WiwaQuantityUnitDto update(final Connection connection, final WiwaQuantityUnitDto wiwaQuantityUnitDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaQuantityUnit.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaQuantityUnitDto.toArray(wiwaQuantityUnitDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_QUANTITY_UNIT.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaQuantityUnit.ID.column(), Condition.EQUALS, wiwaQuantityUnitDto.id())
        );
        return wiwaQuantityUnitDto;
    }
}
