package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.component.TimeUtil;
import sk.janobono.wiwa.business.model.product.ProductDataSo;
import sk.janobono.wiwa.business.model.product.ProductSearchCriteriaSo;
import sk.janobono.wiwa.business.model.product.ProductSo;
import sk.janobono.wiwa.business.model.product.ProductUnitPriceSo;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.LocalStorage;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.ProductSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.mapper.ApplicationImageMapper;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.janobono.wiwa.model.ProductAttributeKey;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.ResourceEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CommonConfigProperties commonConfigProperties;
    private final ImageUtil imageUtil;
    private final LocalStorage localStorage;
    private final ScDf scDf;
    private final TimeUtil timeUtil;
    private final ApplicationImageMapper applicationImageMapper;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductUnitPriceRepository productUnitPriceRepository;

    public Page<ProductSo> getProducts(final ProductSearchCriteriaSo criteria, final Pageable pageable) {
        return productRepository.findAll(new ProductSearchCriteriaDo(scDf, criteria), pageable).map(this::toProductSo);
    }

    public ProductSo getProduct(final Long id) {
        return toProductSo(getProductDo(id));
    }

    @Transactional
    public ProductSo addProduct(final ProductDataSo productData) {
        if (isCodeUsed(null, productData.code())) {
            throw WiwaException.CODE_IS_USED.exception("Product code {0} is used", productData.code());
        }
        ProductDo productDo = new ProductDo();
        productDo.setType(productData.type());
        productDo.setCode(productData.code());
        productDo.setName(productData.name());
        productDo.setStockStatus(productData.stockStatus());
        updateProductData(productDo, productData);
        productDo = productRepository.save(productDo);

        productDo.getAttributes().addAll(productAttributeRepository.saveAll(toProductAttributes(productDo, productData)));
        return toProductSo(productDo);
    }

    @Transactional
    public ProductSo setProduct(final Long id, final ProductDataSo productData) {
        ProductDo productDo = getProductDo(id);
        if (isCodeUsed(id, productData.code())) {
            throw WiwaException.CODE_IS_USED.exception("Product code {0} is used", productData.code());
        }
        productAttributeRepository.deleteByProductId(productDo.getId());

        updateProductData(productDo, productData);
        productDo = productRepository.save(productDo);

        productDo.getAttributes().addAll(productAttributeRepository.saveAll(toProductAttributes(productDo, productData)));
        return toProductSo(productDo);
    }

    @Transactional
    public void deleteProduct(final Long id) {
        getProductDo(id);
        productRepository.deleteById(id);
    }

    public List<ApplicationImage> getProductImages(final Long productId) {
        getProductDo(productId);
        return productImageRepository.findAllByProductId(productId).stream().map(applicationImageMapper::map).toList();
    }

    public ResourceEntity getProductImage(final Long productId, final String fileName) {
        getProductDo(productId);
        return productImageRepository.findByProductIdAndFileName(productId, scDf.toStripAndLowerCase(fileName)).map(productImageDo -> new ResourceEntity(productImageDo.getFileName(), productImageDo.getFileType(), localStorage.getDataResource(productImageDo.getData()))).orElse(new ResourceEntity(fileName, MediaType.IMAGE_PNG_VALUE, localStorage.getDataResource(imageUtil.generateMessageImage(null))));
    }

    @Transactional
    public ApplicationImage setProductImage(final Long productId, final MultipartFile multipartFile) {
        getProductDo(productId);
        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!localStorage.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final ProductImageDo productImageDo = productImageRepository.findByProductIdAndFileName(productId, fileName).orElse(new ProductImageDo());
        productImageDo.setProductId(productId);
        productImageDo.setFileName(fileName);
        productImageDo.setFileType(fileType);
        productImageDo.setThumbnail(imageUtil.scaleImage(fileType, localStorage.getFileData(multipartFile), commonConfigProperties.maxThumbnailResolution(), commonConfigProperties.maxThumbnailResolution()));
        productImageDo.setData(imageUtil.scaleImage(fileType, localStorage.getFileData(multipartFile), commonConfigProperties.maxImageResolution(), commonConfigProperties.maxImageResolution()));
        return applicationImageMapper.map(productImageRepository.save(productImageDo));
    }

    @Transactional
    public void deleteProductImage(final Long productId, final String fileName) {
        productImageRepository.findByProductIdAndFileName(productId, fileName).ifPresent(productImageDo -> productImageRepository.deleteById(productImageDo.getId()));
    }

    public List<ProductUnitPriceSo> getProductUnitPrices(final Long productId) {
        return getProductDo(productId).getProductUnitPrices().stream().map(this::toProductUnitPriceSo).toList();
    }

    @Transactional
    public List<ProductUnitPriceSo> setProductUnitPrices(final Long productId, final List<ProductUnitPriceSo> productUnitPrices) {
        final ProductDo productDo = getProductDo(productId);
        productUnitPriceRepository.deleteByProductId(productId);
        final List<ProductUnitPriceDo> result = new ArrayList<>();

        if (!productUnitPrices.isEmpty()) {
            final List<ProductUnitPriceSo> orderedPrices = productUnitPrices.stream().sorted((o1, o2) -> o2.validFrom().compareTo(o1.validFrom())).toList();

            ProductUnitPriceSo previous = null;
            for (final ProductUnitPriceSo current : orderedPrices) {
                final ProductUnitPriceDo productUnitPriceDo = new ProductUnitPriceDo();
                productUnitPriceDo.setProductId(productDo.getId());
                productUnitPriceDo.setValidFrom(timeUtil.toLocalDate(current.validFrom()));
                productUnitPriceDo.setValidTo(previous != null ? timeUtil.toLocalDate(previous.validFrom()) : null);
                productUnitPriceDo.setPrice(toQuantityDo(current.price()).orElse(null));
                result.add(productUnitPriceRepository.save(productUnitPriceDo));
                previous = current;
            }
        }
        return result.stream().map(this::toProductUnitPriceSo).toList();
    }

    public List<Long> getProductCategoryIds(final Long productId) {
        final ProductDo productDo = getProductDo(productId);
        return productDo.getProductCategories().stream().map(ProductCategoryDo::getId).toList();
    }

    public List<Long> setProductCategoryIds(final Long productId, final List<Long> productCategoryIds) {
        final ProductDo productDo = getProductDo(productId);
        final List<ProductCategoryDo> productCategories = productCategoryIds.stream().map(id -> productCategoryRepository.findById(id).orElseThrow(() -> WiwaException.PRODUCT_CATEGORY_NOT_FOUND.exception("Product category with id {0} not found", id))).toList();
        productDo.getProductCategories().clear();
        productDo.getProductCategories().addAll(productCategories);
        return productRepository.save(productDo).getProductCategories().stream().map(ProductCategoryDo::getId).toList();
    }

    private ProductSo toProductSo(final ProductDo product) {
        return new ProductSo(
                product.getId(),
                product.getType(),
                product.getCode(),
                getAttributeValue(product, ProductAttributeKey.BOARD_CODE).orElse(null),
                getAttributeValue(product, ProductAttributeKey.STRUCTURE_CODE).orElse(null),
                product.getName(),
                product.getNote(),
                toQuantity(product.getSaleUnit()).orElse(null),
                getCurrentUnitPrice(product).orElse(null),
                toQuantity(product.getWeight()).orElse(null),
                toQuantity(product.getNetWeight()).orElse(null),
                toQuantity(product.getLength()).orElse(null),
                toQuantity(product.getWidth()).orElse(null),
                toQuantity(product.getThickness()).orElse(null),
                getAttributeValue(product, ProductAttributeKey.ORIENTATION).map(Boolean::parseBoolean).orElse(null),
                product.getStockStatus());
    }

    private boolean isCodeUsed(final Long id, final String code) {
        return Optional.ofNullable(id).map(productId -> productRepository.countByIdNotAndCode(productId, code) > 0).orElse(productRepository.countByCode(code) > 0);
    }

    private ProductDo getProductDo(final Long id) {
        return productRepository.findById(id).orElseThrow(() -> WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", id));
    }

    private ProductUnitPriceSo toProductUnitPriceSo(final ProductUnitPriceDo productUnitPrice) {
        return new ProductUnitPriceSo(timeUtil.toZonedDateTime(productUnitPrice.getValidFrom()), toQuantity(productUnitPrice.getPrice()).orElse(null));
    }

    private Optional<String> getAttributeValue(final ProductDo product, final ProductAttributeKey key) {
        return product.getAttributes().stream().filter(attribute -> key.name().equals(attribute.getKey())).findFirst().map(ProductAttributeDo::getValue);
    }

    private Optional<Quantity> toQuantity(final QuantityDo quantityDo) {
        return Optional.ofNullable(quantityDo).map(quantity -> new Quantity(quantity.getValue(), quantity.getUnit()));
    }

    private Optional<Quantity> getCurrentUnitPrice(final ProductDo product) {
        final LocalDate date = LocalDate.now();
        return product.getProductUnitPrices().stream().filter(productUnitPrice -> timeUtil.isBeforeOrEquals(date, productUnitPrice.getValidFrom())).filter(productUnitPrice -> productUnitPrice.getValidTo() == null || timeUtil.isAfterOrEquals(date, productUnitPrice.getValidTo())).min(Comparator.comparing(ProductUnitPriceDo::getValidFrom)).map(ProductUnitPriceDo::getPrice).map(this::toQuantity).filter(Optional::isPresent).map(Optional::get);
    }

    private void updateProductData(final ProductDo product, final ProductDataSo productData) {
        product.setType(productData.type());
        product.setCode(productData.code());
        product.setName(productData.name());
        product.setNote(productData.note());
        product.setSaleUnit(toQuantityDo(productData.saleUnit()).orElse(null));
        product.setWeight(toQuantityDo(productData.weight()).orElse(null));
        product.setNetWeight(toQuantityDo(productData.netWeight()).orElse(null));
        product.setLength(toQuantityDo(productData.length()).orElse(null));
        product.setWidth(toQuantityDo(productData.width()).orElse(null));
        product.setThickness(toQuantityDo(productData.thickness()).orElse(null));
        product.setStockStatus(productData.stockStatus());
    }

    private List<ProductAttributeDo> toProductAttributes(final ProductDo product, final ProductDataSo productData) {
        return Stream.of(
                toProductAttribute(product, ProductAttributeKey.BOARD_CODE, productData.boardCode()),
                toProductAttribute(product, ProductAttributeKey.STRUCTURE_CODE, productData.structureCode()),
                toProductAttribute(product, ProductAttributeKey.ORIENTATION, Optional.ofNullable(productData.orientation()).map(Object::toString).orElse(null))
        ).filter(Optional::isPresent).map(Optional::get).toList();
    }

    private Optional<QuantityDo> toQuantityDo(final Quantity quantity) {
        return Optional.ofNullable(quantity).map(q -> {
            final QuantityDo quantityDo = new QuantityDo();
            quantityDo.setValue(q.value());
            quantityDo.setUnit(q.unit());
            return quantityDo;
        });
    }

    private Optional<ProductAttributeDo> toProductAttribute(final ProductDo product, final ProductAttributeKey key, final String value) {
        return Optional.ofNullable(value).map(v -> {
            final ProductAttributeDo productAttributeDo = new ProductAttributeDo();
            productAttributeDo.setProductId(product.getId());
            productAttributeDo.setKey(key.name());
            productAttributeDo.setValue(v);
            return productAttributeDo;
        });
    }
}
