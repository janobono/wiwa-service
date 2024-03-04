package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ProductUnitPriceDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductUnitPriceDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProductUnitPrice;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.ProductUnitPriceDoMapper;
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
public class ProductUnitPriceRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ProductUnitPriceDoMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public List<ProductUnitPriceDo> findAllByProductId(final Long productId) {
        log.debug("findAllByProductId({})", productId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProductUnitPrice.columns())
                            .FROM(MetaTable.WIWA_PRODUCT_UNIT_PRICE.table())
                            .WHERE(MetaColumnWiwaProductUnitPrice.PRODUCT_ID.column(), Condition.EQUALS, productId)
                            .ORDER_BY(MetaColumnWiwaProductUnitPrice.VALID_FROM.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaProductUnitPriceDto::toObject)
                    .map(mapper::toProductUnitPriceDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProductUnitPrices(final Long productId, final List<ProductUnitPriceDo> batch) {
        log.debug("saveProductUnitPrices({},{})", productId, batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            deleteProductUnitPrices(connection, productId);
            insertProductUnitPrices(connection, productId, batch);

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void deleteProductUnitPrices(final Connection connection, final Long productId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_PRODUCT_UNIT_PRICE.table())
                        .WHERE(MetaColumnWiwaProductUnitPrice.PRODUCT_ID.column(), Condition.EQUALS, productId)
        );
    }

    private void insertProductUnitPrices(final Connection connection, final Long productId, final List<ProductUnitPriceDo> batch) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProductUnitPrice.columns(), 1);

        for (final ProductUnitPriceDo price : batch) {
            price.setProductId(productId);
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_PRODUCT_UNIT_PRICE.table(), columns)
                            .VALUES(criteriaUtil.removeFirst(WiwaProductUnitPriceDto.toArray(mapper.toWiwaProductUnitPriceDto(price)), 1))
            );
        }
    }
}
