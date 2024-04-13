package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.ApplicationImageWebMapper;
import sk.janobono.wiwa.api.mapper.UiWebMapper;
import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.CompanyInfoWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.UnitWebDto;
import sk.janobono.wiwa.business.service.ConfigService;
import sk.janobono.wiwa.business.service.UiService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ConfigApiService {

    private final ConfigService configService;
    private final UiService uiService;
    private final ApplicationImageWebMapper applicationImageWebMapper;
    private final UiWebMapper uiWebMapper;

    public Page<ApplicationImageInfoWebDto> getApplicationImages(final Pageable pageable) {
        return uiService.getApplicationImages(pageable)
                .map(applicationImageWebMapper::mapToWebDto);
    }

    public ApplicationImageInfoWebDto setApplicationImage(final MultipartFile multipartFile) {
        return applicationImageWebMapper.mapToWebDto(uiService.setApplicationImage(multipartFile));
    }

    public void deleteApplicationImage(final String fileName) {
        uiService.deleteApplicationImage(fileName);
    }

    public ApplicationImageInfoWebDto setLogo(final MultipartFile multipartFile) {
        return applicationImageWebMapper.mapToWebDto(uiService.setLogo(multipartFile));
    }

    public SingleValueBodyWebDto<String> setTitle(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(uiService.setTitle(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setWelcomeText(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(uiService.setWelcomeText(singleValueBody.value()));
    }

    public List<String> setApplicationInfo(final List<String> data) {
        return uiService.setApplicationInfo(data);
    }

    public CompanyInfoWebDto setCompanyInfo(final CompanyInfoWebDto companyInfo) {
        return uiWebMapper.mapToWebDto(uiService.setCompanyInfo(uiWebMapper.mapToData(companyInfo)));
    }

    public SingleValueBodyWebDto<String> setBusinessConditions(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(uiService.setBusinessConditions(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setCookiesInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(uiService.setCookiesInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setGdprInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(uiService.setGdprInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setWorkingHours(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(uiService.setWorkingHours(singleValueBody.value()));
    }

    public List<UnitWebDto> setUnits(final List<UnitWebDto> units) {
        return uiService.setUnits(
                units.stream().map(uiWebMapper::mapToData).toList()
        ).stream().map(uiWebMapper::mapToWebDto).toList();
    }

    public SingleValueBodyWebDto<BigDecimal> getVatRate() {
        return new SingleValueBodyWebDto<>(configService.getVatRate());
    }

    public SingleValueBodyWebDto<BigDecimal> setVatRate(final SingleValueBodyWebDto<BigDecimal> singleValueBody) {
        return new SingleValueBodyWebDto<>(configService.setVatRate(singleValueBody.value()));
    }
}
