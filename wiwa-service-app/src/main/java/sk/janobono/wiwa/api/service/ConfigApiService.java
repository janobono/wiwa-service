package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.ApplicationImageWebMapper;
import sk.janobono.wiwa.api.mapper.CategoryWebMapper;
import sk.janobono.wiwa.api.mapper.UiWebMapper;
import sk.janobono.wiwa.api.model.CategoryWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ConfigApiService {

    private final ApplicationImageService applicationImageService;
    private final ApplicationPropertyService applicationPropertyService;
    private final ApplicationImageWebMapper applicationImageWebMapper;
    private final UiWebMapper uiWebMapper;
    private final CategoryWebMapper categoryWebMapper;

    public Page<ApplicationImageInfoWebDto> getApplicationImages(final Pageable pageable) {
        return applicationImageService.getApplicationImages(pageable)
                .map(applicationImageWebMapper::mapToWebDto);
    }

    public ApplicationImageInfoWebDto setApplicationImage(final MultipartFile multipartFile) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.setApplicationImage(multipartFile));
    }

    public void deleteApplicationImage(final String fileName) {
        applicationImageService.deleteApplicationImage(fileName);
    }

    public ApplicationImageInfoWebDto setLogo(final MultipartFile multipartFile) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.setLogo(multipartFile));
    }

    public SingleValueBodyWebDto<Boolean> setMaintenance(final SingleValueBodyWebDto<Boolean> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setMaintenance(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setTitle(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setTitle(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setWelcomeText(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setWelcomeText(singleValueBody.value()));
    }

    public List<String> setApplicationInfo(final List<String> data) {
        return applicationPropertyService.setApplicationInfo(data);
    }

    public CompanyInfoWebDto setCompanyInfo(final CompanyInfoWebDto companyInfo) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setCompanyInfo(uiWebMapper.mapToData(companyInfo)));
    }

    public List<UnitWebDto> setUnits(final List<UnitWebDto> units) {
        return applicationPropertyService.setUnits(
                units.stream().map(uiWebMapper::mapToData).toList()
        ).stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public SingleValueBodyWebDto<BigDecimal> getVatRate() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getVatRate());
    }

    public SingleValueBodyWebDto<BigDecimal> setVatRate(final SingleValueBodyWebDto<BigDecimal> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setVatRate(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setBusinessConditions(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setBusinessConditions(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setCookiesInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setCookiesInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setGdprInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setGdprInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setOrderInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setOrderInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setWorkingHours(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setWorkingHours(singleValueBody.value()));
    }

    public SignUpMailWebDto getSignUpMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getSignUpMail());
    }

    public SignUpMailWebDto setSignUpMail(final SignUpMailWebDto signUpMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setSignUpMail(uiWebMapper.mapToData(signUpMail)));
    }

    public ResetPasswordMailWebDto getResetPasswordMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getResetPasswordMail());
    }

    public ResetPasswordMailWebDto setResetPasswordMail(final ResetPasswordMailWebDto resetPasswordMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setResetPasswordMail(uiWebMapper.mapToData(resetPasswordMail)));
    }

    public ManufacturePropertiesWebDto getManufactureProperties() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getManufactureProperties());
    }

    public ManufacturePropertiesWebDto setManufactureProperties(final ManufacturePropertiesWebDto manufactureProperties) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setManufactureProperties(uiWebMapper.mapToData(manufactureProperties)));
    }

    public PriceForGluingLayerWebDto getPriceForGluingLayer() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getPriceForGluingLayer());
    }

    public PriceForGluingLayerWebDto setPriceForGluingLayer(final PriceForGluingLayerWebDto priceForGluingLayer) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setPriceForGluingLayer(uiWebMapper.mapToData(priceForGluingLayer)));
    }

    public List<PriceForGluingEdgeWebDto> getPricesForGluingEdge() {
        return applicationPropertyService.getPricesForGluingEdge().stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public List<PriceForGluingEdgeWebDto> setPricesForGluingEdge(final List<PriceForGluingEdgeWebDto> pricesForGluingEdge) {
        return applicationPropertyService.setPricesForGluingEdge(
                pricesForGluingEdge.stream().map(uiWebMapper::mapToData).toList()
        ).stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public List<PriceForCuttingWebDto> getPricesForCutting() {
        return applicationPropertyService.getPricesForCutting().stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public List<PriceForCuttingWebDto> setPricesForCutting(final List<PriceForCuttingWebDto> pricesForCutting) {
        return applicationPropertyService.setPricesForCutting(
                pricesForCutting.stream().map(uiWebMapper::mapToData).toList()
        ).stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public List<FreeDayWebDto> setFreeDays(final List<FreeDayWebDto> freeDays) {
        return applicationPropertyService.setFreeDays(
                freeDays.stream().map(uiWebMapper::mapToData).toList()
        ).stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public OrderCommentMailWebDto getOrderCommentMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getOrderCommentMail());
    }

    public OrderCommentMailWebDto setOrderCommentMail(final OrderCommentMailWebDto orderCommentMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setOrderCommentMail(uiWebMapper.mapToData(orderCommentMail)));
    }

    public OrderSendMailWebDto getOrderSendMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getOrderSendMail());
    }

    public OrderSendMailWebDto setOrderSendMail(final OrderSendMailWebDto orderSendMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setOrderSendMail(uiWebMapper.mapToData(orderSendMail)));
    }

    public OrderStatusMailWebDto getOrderStatusMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getOrderStatusMail());
    }

    public OrderStatusMailWebDto setOrderStatusMail(final OrderStatusMailWebDto orderStatusMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setOrderStatusMail(uiWebMapper.mapToData(orderStatusMail)));
    }

    public CategoryWebDto getBoardMaterialCategory() {
        return categoryWebMapper.mapToWebDto(applicationPropertyService.getBoardMaterialCategory());
    }

    public CategoryWebDto setBoardMaterialCategory(final SingleValueBodyWebDto<Long> categoryId) {
        return categoryWebMapper.mapToWebDto(applicationPropertyService.setBoardMaterialCategory(categoryId.value()));
    }

    public List<CategoryWebDto> setBoardCategories(final Set<Long> categoryIds) {
        return applicationPropertyService.setBoardCategories(categoryIds).stream().map(categoryWebMapper::mapToWebDto).toList();
    }

    public List<CategoryWebDto> setEdgeCategories(final Set<Long> categoryIds) {
        return applicationPropertyService.setEdgeCategories(categoryIds).stream().map(categoryWebMapper::mapToWebDto).toList();
    }

    public OrderPropertiesWebDto setOrderProperties(final OrderPropertiesWebDto orderProperties) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setOrderProperties(uiWebMapper.mapToData(orderProperties)));
    }
}
