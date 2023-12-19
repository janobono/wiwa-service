package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.dal.domain.ProductAttributeDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductAttributeDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProductAttribute;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.ProductAttributeMapper;
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
public class ProductAttributeRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ProductAttributeMapper mapper;
    private final CriteriaUtil criteriaUtil;

    public List<ProductAttributeDo> findAllByProductId(final Long productId) {
        log.debug("findAllByProductId({})", productId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProductAttribute.columns())
                            .FROM(MetaTable.WIWA_PRODUCT_ATTRIBUTE.table())
                            .WHERE(MetaColumnWiwaProductAttribute.PRODUCT_ID.column(), Condition.EQUALS, productId)
                            .ORDER_BY(MetaColumnWiwaProductAttribute.KEY.column(), Order.ASC)
            );
            return rows.stream()
                    .map(WiwaProductAttributeDto::toObject)
                    .map(mapper::toProductAttributeDo)
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProductAttributes(final Long productId, final List<ProductAttributeDo> batch) {
        log.debug("saveProductAttributes({},{})", productId, batch);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            deleteProductAttributes(connection, productId);
            insertProductAttributes(connection, productId, batch);

            connection.commit();
        } catch (final Exception e) {
            SqlUtil.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            SqlUtil.enableAutoCommit(connection);
            SqlUtil.close(connection);
        }
    }

    private void deleteProductAttributes(final Connection connection, final Long productId) throws SQLException {
        sqlBuilder.delete(connection,
                Query.DELETE()
                        .FROM(MetaTable.WIWA_PRODUCT_ATTRIBUTE.table())
                        .WHERE(MetaColumnWiwaProductAttribute.PRODUCT_ID.column(), Condition.EQUALS, productId)
        );
    }

    private void insertProductAttributes(final Connection connection, final Long productId, final List<ProductAttributeDo> batch) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProductAttribute.columns(), 1);

        for (final ProductAttributeDo attribute : batch) {
            attribute.setProductId(productId);
            sqlBuilder.insert(connection,
                    Query.INSERT()
                            .INTO(MetaTable.WIWA_PRODUCT_ATTRIBUTE.table(), columns)
                            .VALUES(criteriaUtil.removeFirst(WiwaProductAttributeDto.toArray(mapper.toWiwaProductAttributeDto(attribute)), 1))
            );
        }
    }
}
