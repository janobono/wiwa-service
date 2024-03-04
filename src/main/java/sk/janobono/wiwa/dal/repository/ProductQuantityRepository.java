package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ProductQuantityDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductQuantityDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProductQuantity;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.ProductQuantityDoMapper;
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

@RequiredArgsConstructor
@Slf4j
@Repository
public class ProductQuantityRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ProductQuantityDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public List<ProductQuantityDo> findAllByProductId(final Long productId) {
        log.debug("findAllByProductId({})", productId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProductQuantity.columns())
                            .FROM(MetaTable.WIWA_PRODUCT_QUANTITY.table())
                            .WHERE(MetaColumnWiwaProductQuantity.PRODUCT_ID.column(), Condition.EQUALS, productId)
                            .ORDER_BY(MetaColumnWiwaProductQuantity.KEY.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaProductQuantityDto::toObject)
                    .map(mapper::toProductQuantityDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProductQuantities(final Long productId, final List<ProductQuantityDo> batch) {
        log.debug("saveProductQuantities({},{})", productId, batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            deleteProductQuantities(connection, productId);
            insertProductQuantities(connection, productId, batch);

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void deleteProductQuantities(final Connection connection, final Long productId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_PRODUCT_QUANTITY.table())
                        .WHERE(MetaColumnWiwaProductQuantity.PRODUCT_ID.column(), Condition.EQUALS, productId)
        );
    }

    private void insertProductQuantities(final Connection connection, final Long productId, final List<ProductQuantityDo> batch) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProductQuantity.columns(), 1);

        for (final ProductQuantityDo quantity : batch) {
            quantity.setProductId(productId);
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_PRODUCT_QUANTITY.table(), columns)
                            .VALUES(criteriaUtil.removeFirst(WiwaProductQuantityDto.toArray(mapper.toWiwaProductQuantityDto(quantity)), 1))
            );
        }
    }
}
