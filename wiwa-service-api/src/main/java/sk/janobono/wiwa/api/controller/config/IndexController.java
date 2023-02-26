package sk.janobono.wiwa.api.controller.config;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApplicationImageWeb> setLogo(final @RequestParam("file") MultipartFile multipartFile) {
        log.debug("setLogo({})", multipartFile.getOriginalFilename());
        return new ResponseEntity<>(webImageUtil.toWeb(uiService.setLogo(multipartFile)), HttpStatus.OK);
    }

    @PostMapping("/title")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<String>> setTitle(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setTitle({})", data);
        return new ResponseEntity<>(uiService.setTitle(data), HttpStatus.OK);
    }

    @PostMapping("/welcome-text")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<String>> setWelcomeText(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setWelcomeText({})", data);
        return new ResponseEntity<>(uiService.setWelcomeText(data), HttpStatus.OK);
    }

    @PostMapping("/application-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<ApplicationInfoSo>> setApplicationInfo(
            @Valid @RequestBody LocalizedDataSo<ApplicationInfoSo> data) {
        log.debug("setApplicationInfo({})", data);
        return new ResponseEntity<>(uiService.setApplicationInfo(data), HttpStatus.OK);
    }

    @PostMapping("/company-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<CompanyInfoSo>> setCompanyInfo(
            @Valid @RequestBody LocalizedDataSo<CompanyInfoSo> data) {
        log.debug("setCompanyInfo({})", data);
        return new ResponseEntity<>(uiService.setCompanyInfo(data), HttpStatus.OK);
    }

    @PostMapping("/cookies-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<String>> setCookiesInfo(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setCookiesInfo({})", data);
        return new ResponseEntity<>(uiService.setCookiesInfo(data), HttpStatus.OK);
    }

    @PostMapping("/gdpr-info")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<String>> setGdprInfo(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setGdprInfo({})", data);
        return new ResponseEntity<>(uiService.setGdprInfo(data), HttpStatus.OK);
    }

    @PostMapping("/working-hours")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<LocalizedDataSo<String>> setWorkingHours(@RequestBody LocalizedDataSo<String> data) {
        log.debug("setWorkingHours({})", data);
        return new ResponseEntity<>(uiService.setWorkingHours(data), HttpStatus.OK);
    }
}
