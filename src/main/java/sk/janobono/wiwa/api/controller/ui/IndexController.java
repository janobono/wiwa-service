package sk.janobono.wiwa.api.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.service.UiService;
import sk.janobono.wiwa.model.ResourceEntity;

@RequiredArgsConstructor
@RestController("ui")
@RequestMapping(path = "/ui")
public class IndexController {

    private final UiService uiService;

    @GetMapping(value = "/default-locale", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getDefaultLocale() {
        return uiService.getDefaultLocale();
    }


    @GetMapping("/logo")
    public ResponseEntity<Resource> getLogo() {
        final ResourceEntity resourceEntity = uiService.getLogo();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping(value = "/title", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTitle() {
        return uiService.getTitle();
    }

    @GetMapping(value = "/welcome-text", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getWelcomeText() {
        return uiService.getWelcomeText();
    }

    @GetMapping("/application-info")
    public ApplicationInfoSo getApplicationInfo() {
        return uiService.getApplicationInfo();
    }

    @GetMapping("/company-info")
    public CompanyInfoSo getCompanyInfo() {
        return uiService.getCompanyInfo();
    }

    @GetMapping(value = "/cookies-info", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String getCookiesInfo() {
        return uiService.getCookiesInfo();
    }

    @GetMapping(value = "/gdpr-info", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String getGdprInfo() {
        return uiService.getGdprInfo();
    }

    @GetMapping(value = "/working-hours", produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String getWorkingHours() {
        return uiService.getWorkingHours();
    }
}
