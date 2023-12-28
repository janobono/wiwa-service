package sk.janobono.wiwa.api.controller.config;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.service.UiService;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.janobono.wiwa.model.Unit;

import java.util.Map;

@RequiredArgsConstructor
@RestController("config")
@RequestMapping(path = "/config")
public class IndexController {

    private final UiService uiService;

    @PostMapping("/logo")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ApplicationImage setLogo(final @RequestParam("file") MultipartFile multipartFile) {
        return uiService.setLogo(multipartFile);
    }

    @PostMapping(value = "/title")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<String> setTitle(@RequestBody final SingleValueBody<String> data) {
        return new SingleValueBody<>(uiService.setTitle(data.value()));
    }

    @PostMapping(value = "/welcome-text")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<String> setWelcomeText(@RequestBody final SingleValueBody<String> data) {
        return new SingleValueBody<>(uiService.setWelcomeText(data.value()));
    }

    @PostMapping("/application-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ApplicationInfoSo setApplicationInfo(@Valid @RequestBody final ApplicationInfoSo data) {
        return uiService.setApplicationInfo(data);
    }

    @PostMapping("/company-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public CompanyInfoSo setCompanyInfo(@Valid @RequestBody final CompanyInfoSo data) {
        return uiService.setCompanyInfo(data);
    }

    @PostMapping(value = "/business-conditions")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<String> setBusinessConditions(@RequestBody final SingleValueBody<String> data) {
        return new SingleValueBody<>(uiService.setBusinessConditions(data.value()));
    }

    @PostMapping(value = "/cookies-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<String> setCookiesInfo(@RequestBody final SingleValueBody<String> data) {
        return new SingleValueBody<>(uiService.setCookiesInfo(data.value()));
    }

    @PostMapping(value = "/gdpr-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<String> setGdprInfo(@RequestBody final SingleValueBody<String> data) {
        return new SingleValueBody<>(uiService.setGdprInfo(data.value()));
    }

    @PostMapping(value = "/working-hours")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public SingleValueBody<String> setWorkingHours(@RequestBody final SingleValueBody<String> data) {
        return new SingleValueBody<>(uiService.setWorkingHours(data.value()));
    }

    @PostMapping(value = "/units")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public Map<Unit, String> setUnits(@RequestBody final Map<Unit, String> data) {
        return uiService.setUnits(data);
    }
}
