package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.CompanyInfoWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.UnitWebDto;
import sk.janobono.wiwa.api.service.ConfigApiService;

import java.util.List;

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
    public ApplicationImageInfoWebDto upload(@RequestParam("file") final MultipartFile multipartFile) {
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

    @PostMapping(value = "/working-hours")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBodyWebDto<String> setWorkingHours(@Valid @RequestBody final SingleValueBodyWebDto<String> singleValueBody) {
        return configApiService.setWorkingHours(singleValueBody);
    }

    @PostMapping(value = "/units")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public List<UnitWebDto> setUnit(@Valid @RequestBody final List<UnitWebDto> units) {
        return configApiService.setUnits(units);
    }
}
