package sk.janobono.wiwa.api.controller.config;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.component.WebImageUtil;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.model.ui.LocalizedDataSo;
import sk.janobono.wiwa.business.service.UiService;

@Slf4j
@RequiredArgsConstructor
@RestController("config")
@RequestMapping(path = "/config")
public class IndexController {

    private final UiService uiService;
    private final WebImageUtil webImageUtil;

    @PostMapping("/logo")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ApplicationImageWeb setLogo(final @RequestParam("file") MultipartFile multipartFile) {
        log.debug("setLogo({})", multipartFile.getOriginalFilename());
        return webImageUtil.toWeb(uiService.setLogo(multipartFile));
    }

    @PostMapping("/title")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<String> setTitle(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setTitle({})", data);
        return uiService.setTitle(data);
    }

    @PostMapping("/welcome-text")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<String> setWelcomeText(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setWelcomeText({})", data);
        return uiService.setWelcomeText(data);
    }

    @PostMapping("/application-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<ApplicationInfoSo> setApplicationInfo(
            @Valid @RequestBody LocalizedDataSo<ApplicationInfoSo> data) {
        log.debug("setApplicationInfo({})", data);
        return uiService.setApplicationInfo(data);
    }

    @PostMapping("/company-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<CompanyInfoSo> setCompanyInfo(@Valid @RequestBody LocalizedDataSo<CompanyInfoSo> data) {
        log.debug("setCompanyInfo({})", data);
        return uiService.setCompanyInfo(data);
    }

    @PostMapping("/cookies-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<String> setCookiesInfo(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setCookiesInfo({})", data);
        return uiService.setCookiesInfo(data);
    }

    @PostMapping("/gdpr-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<String> setGdprInfo(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setGdprInfo({})", data);
        return uiService.setGdprInfo(data);
    }

    @PostMapping("/working-hours")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public LocalizedDataSo<String> setWorkingHours(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setWorkingHours({})", data);
        return uiService.setWorkingHours(data);
    }
}
