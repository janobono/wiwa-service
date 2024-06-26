package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.*;
import sk.janobono.wiwa.api.model.CategoryWebDto;
import sk.janobono.wiwa.api.model.ResourceEntityWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.api.model.captcha.CaptchaWebDto;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.CaptchaService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UiApiService {

    private final ApplicationImageService applicationImageService;
    private final ApplicationPropertyService applicationPropertyService;
    private final CaptchaService captchaService;
    private final ApplicationImageWebMapper applicationImageWebMapper;
    private final UiWebMapper uiWebMapper;
    private final CategoryWebMapper categoryWebMapper;

    public CaptchaWebDto getCaptcha() {
        return uiWebMapper.mapToWebDto(captchaService.getCaptcha());
    }

    public ResourceEntityWebDto getLogo() {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getLogo());
    }

    public ResourceEntityWebDto getApplicationImage(final String fileName) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getApplicationImage(fileName));
    }

    public ResourceEntityWebDto getBoardImage(final Long boardId) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getBoardImage(boardId));
    }

    public ResourceEntityWebDto getEdgeImage(final Long edgeId) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getEdgeImage(edgeId));
    }

    public SingleValueBodyWebDto<Boolean> getMaintenance() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getMaintenance());
    }

    public ApplicationPropertiesWebDto getApplicationProperties() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getApplicationProperties());
    }

    public SingleValueBodyWebDto<String> getTitle() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getTitle());
    }

    public SingleValueBodyWebDto<String> getWelcomeText() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getWelcomeText());
    }

    public List<String> getApplicationInfo() {
        return applicationPropertyService.getApplicationInfo();
    }

    public CompanyInfoWebDto getCompanyInfo() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getCompanyInfo());
    }

    public List<UnitWebDto> getUnits() {
        return applicationPropertyService.getUnits().stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public SingleValueBodyWebDto<String> getBusinessConditions() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getBusinessConditions());
    }

    public SingleValueBodyWebDto<String> getCookiesInfo() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getCookiesInfo());
    }

    public SingleValueBodyWebDto<String> getGdprInfo() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getGdprInfo());
    }

    public SingleValueBodyWebDto<String> getOrderInfo() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getOrderInfo());
    }

    public SingleValueBodyWebDto<String> getWorkingHours() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getWorkingHours());
    }

    public List<FreeDayWebDto> getFreeDays() {
        return applicationPropertyService.getFreeDays().stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public List<CategoryWebDto> getBoardCategories() {
        return applicationPropertyService.getBoardCategories().stream().map(categoryWebMapper::mapToWebDto).toList();
    }

    public List<CategoryWebDto> getEdgeCategories() {
        return applicationPropertyService.getEdgeCategories().stream().map(categoryWebMapper::mapToWebDto).toList();
    }

    public OrderPropertiesWebDto getOrderProperties() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getOrderProperties());
    }
}
