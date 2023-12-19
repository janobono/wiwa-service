package sk.janobono.wiwa.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.dal.domain.ProductImageDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductImageDto;
import sk.janobono.wiwa.dal.r3n.meta.MetaColumnWiwaProductImage;
import sk.janobono.wiwa.dal.r3n.meta.MetaTable;
import sk.janobono.wiwa.dal.repository.component.CriteriaUtil;
import sk.janobono.wiwa.dal.repository.mapper.ProductImageMapper;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.r3n.jdbc.SqlBuilder;
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
public class ProductImageRepository {

    private final DataSource dataSource;
    private final SqlBuilder sqlBuilder;
    private final ProductImageMapper mapper;
    private final ImageUtil imageUtil;
    private final CriteriaUtil criteriaUtil;

    public void deleteById(final Long id) {
        log.debug("deleteById({})", id);
        try (final Connection connection = dataSource.getConnection()) {
            sqlBuilder.delete(connection,
                    Query.DELETE()
                            .FROM(MetaTable.WIWA_PRODUCT_IMAGE.table())
                            .WHERE(MetaColumnWiwaProductImage.ID.column(), Condition.EQUALS, id));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ApplicationImage> findAllByProductId(final Long productId) {
        log.debug("findAllByProductId({})", productId);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(
                                    MetaColumnWiwaProductImage.FILE_NAME.column(),
                                    MetaColumnWiwaProductImage.FILE_TYPE.column(),
                                    MetaColumnWiwaProductImage.THUMBNAIL.column()
                            )
                            .FROM(MetaTable.WIWA_PRODUCT_IMAGE.table())
                            .WHERE(MetaColumnWiwaProductImage.PRODUCT_ID.column(), Condition.EQUALS, productId)
                            .ORDER_BY(MetaColumnWiwaProductImage.FILE_NAME.column(), Order.ASC)
            );
            return rows.stream()
                    .map(row -> new ApplicationImage(
                            (String) row[0],
                            (String) row[1],
                            imageUtil.toThumbnail((String) row[1], (byte[]) row[2])
                    ))
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ProductImageDo> findByProductIdAndFileName(final Long productId, final String fileName) {
        log.debug("findByProductIdAndFileName({},{})", productId, fileName);
        try (final Connection connection = dataSource.getConnection()) {
            final List<Object[]> rows = sqlBuilder.select(connection,
                    Query.SELECT(MetaColumnWiwaProductImage.columns())
                            .FROM(MetaTable.WIWA_PRODUCT_IMAGE.table())
                            .WHERE(MetaColumnWiwaProductImage.PRODUCT_ID.column(), Condition.EQUALS, productId)
                            .AND(MetaColumnWiwaProductImage.FILE_NAME.column(), Condition.EQUALS, fileName));
            return rows.stream()
                    .findFirst()
                    .map(WiwaProductImageDto::toObject)
                    .map(mapper::toProductImageDo);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ProductImageDo save(final ProductImageDo productImageDo) {
        log.debug("save({})", productImageDo);
        try (final Connection connection = dataSource.getConnection()) {
            final WiwaProductImageDto result;
            if (productImageDo.getId() == null) {
                result = insert(connection, mapper.toWiwaProductImageDto(productImageDo));
            } else {
                result = update(connection, mapper.toWiwaProductImageDto(productImageDo));
            }
            return mapper.toProductImageDo(result);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WiwaProductImageDto insert(final Connection connection, final WiwaProductImageDto wiwaProductImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProductImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaProductImageDto.toArray(wiwaProductImageDto), 1);

        final Long id = (Long) sqlBuilder.insert(connection,
                Query.INSERT()
                        .INTO(MetaTable.WIWA_PRODUCT_IMAGE.table(), columns)
                        .VALUES(values).RETURNING(MetaColumnWiwaProductImage.ID.column()));

        return WiwaProductImageDto.toObject(criteriaUtil.concat(new Object[]{id}, values));
    }

    private WiwaProductImageDto update(final Connection connection, final WiwaProductImageDto wiwaProductImageDto) throws SQLException {
        final Column[] columns = criteriaUtil.removeFirst(MetaColumnWiwaProductImage.columns(), 1);
        final Object[] values = criteriaUtil.removeFirst(WiwaProductImageDto.toArray(wiwaProductImageDto), 1);

        sqlBuilder.update(connection,
                Query.UPDATE(MetaTable.WIWA_PRODUCT_IMAGE.table())
                        .SET(columns, values)
                        .WHERE(MetaColumnWiwaProductImage.ID.column(), Condition.EQUALS, wiwaProductImageDto.id())
        );

        return wiwaProductImageDto;
    }
}
