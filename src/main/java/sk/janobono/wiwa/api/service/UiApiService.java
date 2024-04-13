package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ApplicationImageWebMapper;
import sk.janobono.wiwa.api.mapper.UiWebMapper;
import sk.janobono.wiwa.api.model.*;
import sk.janobono.wiwa.business.service.UiService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UiApiService {

    private final UiService uiService;
    private final ApplicationImageWebMapper applicationImageWebMapper;
    private final UiWebMapper uiWebMapper;

    public ApplicationPropertiesWebDto getApplicationProperties() {
        return uiWebMapper.mapToWebDto(uiService.getApplicationProperties());
    }

    public CaptchaWebDto getCaptcha() {
        return uiWebMapper.mapToWebDto(uiService.getCaptcha());
    }

    public ResourceEntityWebDto getLogo() {
        return applicationImageWebMapper.mapToWebDto(uiService.getLogo());
    }

    public ResourceEntityWebDto getApplicationImage(final String fileName) {
        return applicationImageWebMapper.mapToWebDto(uiService.getApplicationImage(fileName));
    }

    public ResourceEntityWebDto getBoardImage(final Long boardId, final String fileName) {
        return applicationImageWebMapper.mapToWebDto(uiService.getBoardImage(boardId, fileName));
    }

    public ResourceEntityWebDto getEdgeImage(final Long edgeId, final String fileName) {
        return applicationImageWebMapper.mapToWebDto(uiService.getEdgeImage(edgeId, fileName));
    }

    public SingleValueBodyWebDto<String> getTitle() {
        return new SingleValueBodyWebDto<>(uiService.getTitle());
    }

    public SingleValueBodyWebDto<String> getWelcomeText() {
        return new SingleValueBodyWebDto<>(uiService.getWelcomeText());
    }

    public List<String> getApplicationInfo() {
        return uiService.getApplicationInfo();
    }

    public CompanyInfoWebDto getCompanyInfo() {
        return uiWebMapper.mapToWebDto(uiService.getCompanyInfo());
    }

    public SingleValueBodyWebDto<String> getBusinessConditions() {
        return new SingleValueBodyWebDto<>(uiService.getBusinessConditions());
    }

    public SingleValueBodyWebDto<String> getCookiesInfo() {
        return new SingleValueBodyWebDto<>(uiService.getCookiesInfo());
    }

    public SingleValueBodyWebDto<String> getGdprInfo() {
        return new SingleValueBodyWebDto<>(uiService.getGdprInfo());
    }

    public SingleValueBodyWebDto<String> getWorkingHours() {
        return new SingleValueBodyWebDto<>(uiService.getWorkingHours());
    }

    public List<UnitWebDto> getUnits() {
        return uiService.getUnits().stream().map(uiWebMapper::mapToWebDto).toList();
    }
}
