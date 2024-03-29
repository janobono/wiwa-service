package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.product.*;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.ProductSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CommonConfigProperties commonConfigProperties;

    private final ImageUtil imageUtil;
    private final PriceUtil priceUtil;
    private final ScDf scDf;

    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductQuantityRepository productQuantityRepository;
    private final ProductUnitPriceRepository productUnitPriceRepository;
    private final CodeListRepository codeListRepository;
    private final CodeListItemRepository codeListItemRepository;

    private final ApplicationPropertyService applicationPropertyService;

    public Page<ProductData> getProducts(final ProductSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = getVatRate();
        return productRepository.findAll(mapToDo(criteria), pageable).map(value -> toProductSo(value, vatRate));
    }

    public ProductData getProduct(final Long id) {
        return toProductSo(getProductDo(id), getVatRate());
    }

    public ProductData addProduct(final ProductChangeData data) {
        if (isCodeUsed(null, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Product code {0} is used", data.code());
        }
        final ProductDo productDo = productRepository.save(ProductDo.builder()
                .code(data.code())
                .name(data.name())
                .description(data.description())
                .stockStatus(data.stockStatus())
                .build()
        );
        setProductAttributes(productDo.getId(), data);
        setProductQuantities(productDo.getId(), data);
        return toProductSo(productDo, getVatRate());
    }

    public ProductData setProduct(final Long id, final ProductChangeData data) {
        final ProductDo productDo = getProductDo(id);
        if (isCodeUsed(id, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Product code {0} is used", data.code());
        }
        productDo.setCode(data.code());
        productDo.setName(data.name());
        productDo.setDescription(data.description());
        productDo.setStockStatus(data.stockStatus());
        productRepository.save(productDo);
        setProductAttributes(id, data);
        setProductQuantities(id, data);
        return toProductSo(productDo, getVatRate());
    }

    public void deleteProduct(final Long id) {
        getProductDo(id);
        productRepository.deleteById(id);
    }

    public ProductData setProductImage(final Long productId, final MultipartFile multipartFile) {
        if (!productRepository.existsById(productId)) {
            throw WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", productId);
        }

        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!imageUtil.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final ProductImageDo productImageDo = productImageRepository.findByProductIdAndFileName(productId, fileName)
                .orElse(ProductImageDo.builder().build());
        productImageDo.setProductId(productId);
        productImageDo.setFileName(fileName);
        productImageDo.setFileType(fileType);
        productImageDo.setThumbnail(imageUtil.scaleImage(fileType,
                imageUtil.getFileData(multipartFile),
                commonConfigProperties.maxThumbnailResolution(),
                commonConfigProperties.maxThumbnailResolution()));
        productImageDo.setData(imageUtil.scaleImage(fileType,
                imageUtil.getFileData(multipartFile),
                commonConfigProperties.maxImageResolution(),
                commonConfigProperties.maxImageResolution()));
        productImageRepository.save(productImageDo);

        return toProductSo(getProductDo(productId), getVatRate());
    }

    public ProductData deleteProductImage(final Long productId, final String fileName) {
        if (!productRepository.existsById(productId)) {
            throw WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", productId);
        }
        productImageRepository.findByProductIdAndFileName(productId, fileName)
                .ifPresent(productImageDo -> productImageRepository.deleteById(productImageDo.getId()));
        return toProductSo(getProductDo(productId), getVatRate());
    }

    public ProductData setProductUnitPrices(final Long productId, final List<ProductUnitPriceChangeData> productUnitPrices) {
        if (!productRepository.existsById(productId)) {
            throw WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", productId);
        }

        final List<ProductUnitPriceDo> batch = new ArrayList<>();
        if (!productUnitPrices.isEmpty()) {
            final List<ProductUnitPriceChangeData> orderedPrices = productUnitPrices.stream()
                    .sorted((o1, o2) -> o2.validFrom().compareTo(o1.validFrom())).toList();

            ProductUnitPriceChangeData previous = null;
            for (final ProductUnitPriceChangeData current : orderedPrices) {
                batch.add(ProductUnitPriceDo.builder()
                        .productId(productId)
                        .unit(current.unit())
                        .validFrom(current.validFrom())
                        .validTo(previous != null ? previous.validFrom() : null)
                        .value(current.value())
                        .build()
                );
                previous = current;
            }
        }
        productUnitPriceRepository.saveProductUnitPrices(productId, batch);
        return toProductSo(getProductDo(productId), getVatRate());
    }

    public ProductData setProductCategoryItems(final Long productId, final List<ProductCategoryItemChangeData> categoryItems) {
        final ProductDo productDo = getProductDo(productId);

        codeListItemRepository.saveProductCodeListItems(productDo.getId(),
                categoryItems.stream()
                        .map(ProductCategoryItemChangeData::itemId)
                        .toList()
        );

        return toProductSo(productDo, getVatRate());
    }

    private ProductSearchCriteriaDo mapToDo(final ProductSearchCriteriaData criteria) {
        return new ProductSearchCriteriaDo(
                criteria.searchField(),
                criteria.code(),
                criteria.name(),
                criteria.stockStatus(),
                criteria.codeListItems()
        );
    }

    private ProductDo getProductDo(final Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", id));
    }

    private ProductData toProductSo(final ProductDo productDo, final BigDecimal vatRate) {
        return ProductData.builder()
                .id(productDo.getId())
                .code(productDo.getCode())
                .name(productDo.getName())
                .description(productDo.getDescription())
                .stockStatus(productDo.getStockStatus())
                .attributes(toAttributes(productDo.getId()))
                .images(toImages(productDo.getId()))
                .quantities(toQuantities(productDo.getId()))
                .unitPrices(toUnitPrices(productDo.getId(), vatRate))
                .categoryItems(toProductCategoryItems(productDo.getId()))
                .build();
    }

    private List<ProductAttributeData> toAttributes(final Long productId) {
        return productAttributeRepository.findAllByProductId(productId).stream()
                .map(attribute -> new ProductAttributeData(attribute.getKey(), attribute.getValue()))
                .sorted(Comparator.comparingInt(a -> a.key().ordinal()))
                .toList();
    }

    private List<ApplicationImageInfoData> toImages(final Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
    }

    private List<ProductQuantityData> toQuantities(final Long productId) {
        return productQuantityRepository.findAllByProductId(productId).stream()
                .map(quantity -> new ProductQuantityData(quantity.getKey(), quantity.getValue(), quantity.getUnit()))
                .sorted(Comparator.comparingInt(o -> o.key().ordinal()))
                .toList();
    }

    private List<ProductUnitPriceData> toUnitPrices(final Long productId, final BigDecimal vatRate) {
        return productUnitPriceRepository.findAllByProductId(productId).stream()
                .map(price -> new ProductUnitPriceData(
                        price.getValidFrom(),
                        price.getValue(),
                        priceUtil.countVatValue(price.getValue(), vatRate),
                        price.getUnit())
                ).toList();
    }

    private List<ProductCategoryItemData> toProductCategoryItems(final Long productId) {
        return codeListItemRepository.findByProductId(productId).stream()
                .map(this::toProductCategoryItem)
                .toList();
    }

    private ProductCategoryItemData toProductCategoryItem(final CodeListItemDo codeListItemDo) {
        final CodeListDo codeList = codeListRepository.findById(codeListItemDo.getCodeListId())
                .orElseThrow(() -> WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", codeListItemDo.getCodeListId()));
        return new ProductCategoryItemData(
                codeListItemDo.getId(),
                codeListItemDo.getCode(),
                codeListItemDo.getValue(),
                new ProductCategoryData(codeList.getId(), codeList.getCode(), codeList.getName())
        );
    }

    private boolean isCodeUsed(final Long id, final String code) {
        return Optional.ofNullable(id).map(productId -> productRepository.countByIdNotAndCode(productId, code) > 0)
                .orElse(productRepository.countByCode(code) > 0);
    }

    private void setProductAttributes(final Long productId, final ProductChangeData data) {
        final List<ProductAttributeDo> attributes = Optional.ofNullable(data.attributes()).stream()
                .flatMap(Collection::stream)
                .map(attribute -> ProductAttributeDo.builder()
                        .productId(productId)
                        .key(attribute.key())
                        .value(attribute.value())
                        .build()
                )
                .toList();
        productAttributeRepository.saveProductAttributes(productId, attributes);
    }

    private void setProductQuantities(final Long productId, final ProductChangeData data) {
        final List<ProductQuantityDo> quantities = Optional.ofNullable(data.quantities()).stream()
                .flatMap(Collection::stream)
                .map(quantity -> ProductQuantityDo.builder()
                        .productId(productId)
                        .unit(quantity.unit())
                        .key(quantity.key())
                        .value(quantity.value())
                        .build()
                )
                .toList();
        productQuantityRepository.saveProductQuantities(productId, quantities);
    }

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }
}
