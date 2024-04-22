package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.ApplicationImageWebMapper;
import sk.janobono.wiwa.api.mapper.UiWebMapper;
import sk.janobono.wiwa.api.model.*;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ConfigApiService {

    private final ApplicationImageService applicationImageService;
    private final ApplicationPropertyService applicationPropertyService;
    private final ApplicationImageWebMapper applicationImageWebMapper;
    private final UiWebMapper uiWebMapper;

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

    public SingleValueBodyWebDto<String> setBusinessConditions(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setBusinessConditions(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setCookiesInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setCookiesInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setGdprInfo(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setGdprInfo(singleValueBody.value()));
    }

    public SingleValueBodyWebDto<String> setWorkingHours(final SingleValueBodyWebDto<String> singleValueBody) {
        return new SingleValueBodyWebDto<>(applicationPropertyService.setWorkingHours(singleValueBody.value()));
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

    public ResetPasswordMailWebDto getResetPasswordMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getResetPasswordMail());
    }

    public ResetPasswordMailWebDto setResetPasswordMail(final ResetPasswordMailWebDto resetPasswordMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setResetPasswordMail(uiWebMapper.mapToData(resetPasswordMail)));
    }

    public SignUpMailWebDto getSignUpMail() {
        return uiWebMapper.mapToWebDto(applicationPropertyService.getSignUpMail());
    }

    public SignUpMailWebDto setSignUpMail(final SignUpMailWebDto signUpMail) {
        return uiWebMapper.mapToWebDto(applicationPropertyService.setSignUpMail(uiWebMapper.mapToData(signUpMail)));
    }
}
