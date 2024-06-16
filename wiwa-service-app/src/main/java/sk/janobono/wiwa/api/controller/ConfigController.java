package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.CategoryWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.api.service.ConfigApiService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/config")
public class ConfigController {

    private final ConfigApiService configApiService;

    @GetMapping("/application-images")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public Page<ApplicationImageInfoWebDto> getApplicationImages(final Pageable pageable) {
        return configApiService.getApplicationImages(pageable);
    }

    @PostMapping("/application-images")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ApplicationImageInfoWebDto setApplicationImage(@RequestParam("file") final MultipartFile multipartFile) {
        return configApiService.setApplicationImage(multipartFile);
    }

    @DeleteMapping("/application-images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public void deleteApplicationImage(@PathVariable("fileName") final String fileName) {
        configApiService.deleteApplicationImage(fileName);
    }

    @PostMapping("/logo")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ApplicationImageInfoWebDto setLogo(final @RequestParam("file") MultipartFile multipartFile) {
        return configApiService.setLogo(multipartFile);
    }

    @PostMapping("/maintenance")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<Boolean> setMaintenance(@Valid @RequestBody final SingleValueBodyWebDto<Boolean> singleValueBody) {
        return configApiService.setMaintenance(singleValueBody);
    }

    @PostMapping(value = "/title")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setTitle(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setTitle(singleValueBody);
    }

    @PostMapping(value = "/welcome-text")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setWelcomeText(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setWelcomeText(singleValueBody);
    }

    @PostMapping("/application-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<String> setApplicationInfo(@Valid @RequestBody final List<String> data) {
        return configApiService.setApplicationInfo(data);
    }

    @PostMapping("/company-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CompanyInfoWebDto setCompanyInfo(@Valid @RequestBody final CompanyInfoWebDto companyInfo) {
        return configApiService.setCompanyInfo(companyInfo);
    }

    @PostMapping(value = "/units")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<UnitWebDto> setUnits(@Valid @RequestBody final List<UnitWebDto> units) {
        return configApiService.setUnits(units);
    }

    @GetMapping(value = "/vat-rate")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<BigDecimal> getVatRate() {
        return configApiService.getVatRate();
    }

    @PostMapping(value = "/vat-rate")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<BigDecimal> setVatRate(@Valid @RequestBody final SingleValueBodyWebDto<BigDecimal> singleValueBody) {
        return configApiService.setVatRate(singleValueBody);
    }

    @PostMapping(value = "/business-conditions")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setBusinessConditions(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setBusinessConditions(singleValueBody);
    }

    @PostMapping(value = "/cookies-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setCookiesInfo(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setCookiesInfo(singleValueBody);
    }

    @PostMapping(value = "/gdpr-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setGdprInfo(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setGdprInfo(singleValueBody);
    }

    @PostMapping(value = "/order-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setOrderInfo(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setOrderInfo(singleValueBody);
    }

    @PostMapping(value = "/working-hours")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setWorkingHours(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setWorkingHours(singleValueBody);
    }

    @GetMapping(value = "/sign-up-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SignUpMailWebDto getSignUpMail() {
        return configApiService.getSignUpMail();
    }

    @PostMapping(value = "/sign-up-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SignUpMailWebDto setSignUpMail(@Valid @RequestBody final SignUpMailWebDto signUpMail) {
        return configApiService.setSignUpMail(signUpMail);
    }

    @GetMapping(value = "/reset-password-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResetPasswordMailWebDto getResetPasswordMail() {
        return configApiService.getResetPasswordMail();
    }

    @PostMapping(value = "/reset-password-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResetPasswordMailWebDto setResetPasswordMail(@Valid @RequestBody final ResetPasswordMailWebDto resetPasswordMail) {
        return configApiService.setResetPasswordMail(resetPasswordMail);
    }

    @GetMapping(value = "/manufacture-properties")
    public ManufacturePropertiesWebDto getManufactureProperties() {
        return configApiService.getManufactureProperties();
    }

    @PostMapping(value = "/manufacture-properties")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ManufacturePropertiesWebDto setManufactureProperties(@Valid @RequestBody final ManufacturePropertiesWebDto manufactureProperties) {
        return configApiService.setManufactureProperties(manufactureProperties);
    }

    @GetMapping(value = "/price-for-gluing-layer")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public PriceForGluingLayerWebDto getPriceForGluingLayer() {
        return configApiService.getPriceForGluingLayer();
    }

    @PostMapping(value = "/price-for-gluing-layer")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public PriceForGluingLayerWebDto setPriceForGluingLayer(@Valid @RequestBody final PriceForGluingLayerWebDto priceForGluingLayer) {
        return configApiService.setPriceForGluingLayer(priceForGluingLayer);
    }

    @GetMapping(value = "/prices-for-gluing-edge")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<PriceForGluingEdgeWebDto> getPricesForGluingEdge() {
        return configApiService.getPricesForGluingEdge();
    }

    @PostMapping(value = "/prices-for-gluing-edge")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<PriceForGluingEdgeWebDto> setPricesForGluingEdge(@Valid @RequestBody final List<PriceForGluingEdgeWebDto> pricesForGluingEdge) {
        return configApiService.setPricesForGluingEdge(pricesForGluingEdge);
    }

    @GetMapping(value = "/prices-for-cutting")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<PriceForCuttingWebDto> getPricesForCutting() {
        return configApiService.getPricesForCutting();
    }

    @PostMapping(value = "/prices-for-cutting")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<PriceForCuttingWebDto> setPricesForCutting(@Valid @RequestBody final List<PriceForCuttingWebDto> pricesForCutting) {
        return configApiService.setPricesForCutting(pricesForCutting);
    }

    @PostMapping(value = "/free-days")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<FreeDayWebDto> setFreeDays(@Valid @RequestBody final List<FreeDayWebDto> freeDays) {
        return configApiService.setFreeDays(freeDays);
    }

    @GetMapping(value = "/order-comment-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderCommentMailWebDto getOrderCommentMail() {
        return configApiService.getOrderCommentMail();
    }

    @PostMapping(value = "/order-comment-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderCommentMailWebDto setOrderCommentMail(@Valid @RequestBody final OrderCommentMailWebDto orderCommentMail) {
        return configApiService.setOrderCommentMail(orderCommentMail);
    }

    @GetMapping(value = "/order-send-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderSendMailWebDto getOrderSendMail() {
        return configApiService.getOrderSendMail();
    }

    @PostMapping(value = "/order-send-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderSendMailWebDto setOrderSendMail(@Valid @RequestBody final OrderSendMailWebDto orderSendMail) {
        return configApiService.setOrderSendMail(orderSendMail);
    }

    @GetMapping(value = "/order-status-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderStatusMailWebDto getOrderStatusMail() {
        return configApiService.getOrderStatusMail();
    }

    @PostMapping(value = "/order-status-mail")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderStatusMailWebDto setOrderStatusMail(@Valid @RequestBody final OrderStatusMailWebDto orderStatusMail) {
        return configApiService.setOrderStatusMail(orderStatusMail);
    }

    @GetMapping(value = "/board-material-category")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CategoryWebDto getBoardMaterialCategory() {
        return configApiService.getBoardMaterialCategory();
    }

    @PostMapping(value = "/board-material-category")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CategoryWebDto setBoardMaterialCategory(@Valid @RequestBody final SingleValueBodyWebDto<Long> categoryId) {
        return configApiService.setBoardMaterialCategory(categoryId);
    }

    @PostMapping(value = "/board-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<CategoryWebDto> setBoardCategories(@Valid @RequestBody final Set<Long> categoryIds) {
        return configApiService.setBoardCategories(categoryIds);
    }

    @PostMapping(value = "/edge-categories")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<CategoryWebDto> setEdgeCategories(@Valid @RequestBody final Set<Long> categoryIds) {
        return configApiService.setEdgeCategories(categoryIds);
    }

    @PostMapping(value = "/order-properties")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderPropertiesWebDto setOrderProperties(@Valid @RequestBody final OrderPropertiesWebDto orderProperties) {
        return configApiService.setOrderProperties(orderProperties);
    }
}
