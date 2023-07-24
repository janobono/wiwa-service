package sk.janobono.wiwa.api.controller.config;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.component.WebImageUtil;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.service.UiService;

@RequiredArgsConstructor
@RestController("config")
@RequestMapping(path = "/config")
public class IndexController {

    private final UiService uiService;
    private final WebImageUtil webImageUtil;

    @PostMapping("/logo")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ApplicationImageWeb setLogo(final @RequestParam("file") MultipartFile multipartFile) {
        return webImageUtil.toWeb(uiService.setLogo(multipartFile));
    }

    @PostMapping(value = "/title", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public String setTitle(@RequestBody final SingleValueBody<String> data) {
        return uiService.setTitle(data.value());
    }

    @PostMapping(value = "/welcome-text", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public String setWelcomeText(@RequestBody final SingleValueBody<String> data) {
        return uiService.setWelcomeText(data.value());
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

    @PostMapping(value = "/cookies-info", produces = MediaType.TEXT_MARKDOWN_VALUE)
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public String setCookiesInfo(@RequestBody final SingleValueBody<String> data) {
        return uiService.setCookiesInfo(data.value());
    }

    @PostMapping(value = "/gdpr-info", produces = MediaType.TEXT_MARKDOWN_VALUE)
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public String setGdprInfo(@RequestBody final SingleValueBody<String> data) {
        return uiService.setGdprInfo(data.value());
    }

    @PostMapping(value = "/working-hours", produces = MediaType.TEXT_MARKDOWN_VALUE)
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public String setWorkingHours(@RequestBody final SingleValueBody<String> data) {
        return uiService.setWorkingHours(data.value());
    }
}
