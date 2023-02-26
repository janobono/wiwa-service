package sk.janobono.wiwa.api.controller.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.service.UiService;
import sk.janobono.wiwa.common.model.ResourceEntitySo;

@Slf4j
@RequiredArgsConstructor
@RestController("ui")
@RequestMapping(path = "/ui")
public class IndexController {

    private final UiService uiService;

    @GetMapping(value = "/logo")
    public ResponseEntity<Resource> getLogo() {
        log.debug("getLogo()");
        ResourceEntitySo resourceEntityDto = uiService.getLogo();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntityDto.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntityDto.fileName() + "\"")
                .body(resourceEntityDto.resource());
    }

    @GetMapping(value = "/title", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTitle() {
        log.debug("getTitle()");
        return new ResponseEntity<>(uiService.getTitle(), HttpStatus.OK);
    }

    @GetMapping(value = "/welcome-text", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getWelcomeText() {
        log.debug("getWelcomeText()");
        return new ResponseEntity<>(uiService.getWelcomeText(), HttpStatus.OK);
    }

    @GetMapping("/application-info")
    public ResponseEntity<ApplicationInfoSo> getApplicationInfo() {
        log.debug("getApplicationInfo()");
        return new ResponseEntity<>(uiService.getApplicationInfo(), HttpStatus.OK);
    }

    @GetMapping("/company-info")
    public ResponseEntity<CompanyInfoSo> getCompanyInfo() {
        log.debug("getCompanyInfo()");
        return new ResponseEntity<>(uiService.getCompanyInfo(), HttpStatus.OK);
    }

    @GetMapping(value = "/cookies-info", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public ResponseEntity<String> getCookiesInfo() {
        log.debug("getCookiesInfo()");
        return new ResponseEntity<>(uiService.getCookiesInfo(), HttpStatus.OK);
    }

    @GetMapping(value = "/gdpr-info", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public ResponseEntity<String> getGdprInfo() {
        log.debug("getGdprInfo()");
        return new ResponseEntity<>(uiService.getGdprInfo(), HttpStatus.OK);
    }

    @GetMapping(value = "/working-hours", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public ResponseEntity<String> getWorkingHours() {
        log.debug("getWorkingHours()");
        return new ResponseEntity<>(uiService.getWorkingHours(), HttpStatus.OK);
    }
}
