package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ApplicationImageWebMapper;
import sk.janobono.wiwa.api.mapper.UiWebMapper;
import sk.janobono.wiwa.api.model.*;
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

    public ApplicationPropertiesWebDto getApplicationProperties() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getApplicationProperties());
    }

    public CaptchaWebDto getCaptcha() {
        return uiWebMapper.mapToWebDto(captchaService.getCaptcha());
    }

    public ResourceEntityWebDto getLogo() {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getLogo());
    }

    public ResourceEntityWebDto getApplicationImage(final String fileName) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getApplicationImage(fileName));
    }

    public ResourceEntityWebDto getBoardImage(final Long boardId, final String fileName) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getBoardImage(boardId, fileName));
    }

    public ResourceEntityWebDto getEdgeImage(final Long edgeId, final String fileName) {
        return applicationImageWebMapper.mapToWebDto(applicationImageService.getEdgeImage(edgeId, fileName));
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

    public SingleValueBodyWebDto<String> getBusinessConditions() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getBusinessConditions());
    }

    public SingleValueBodyWebDto<String> getCookiesInfo() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getCookiesInfo());
    }

    public SingleValueBodyWebDto<String> getGdprInfo() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getGdprInfo());
    }

    public SingleValueBodyWebDto<String> getWorkingHours() {
        return new SingleValueBodyWebDto<>(applicationPropertyService.getWorkingHours());
    }

    public List<UnitWebDto> getUnits() {
        return applicationPropertyService.getUnits().stream().map(uiWebMapper::mapToWebDto).toList();
    }
}
