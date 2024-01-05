package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.api.model.ApplicationPropertiesWeb;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.model.ui.UnitSo;
import sk.janobono.wiwa.business.service.UiService;
import sk.janobono.wiwa.model.ResourceEntity;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/ui")
public class UiController {

    private final UiService uiService;

    @GetMapping("/application-properties")
    public ApplicationPropertiesWeb getApplicationProperties() {
        return uiService.getApplicationProperties();
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> getLogo() {
        final ResourceEntity resourceEntity = uiService.getLogo();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping(value = "/title")
    public SingleValueBody<String> getTitle() {
        return new SingleValueBody<>(uiService.getTitle());
    }

    @GetMapping(value = "/welcome-text")
    public SingleValueBody<String> getWelcomeText() {
        return new SingleValueBody<>(uiService.getWelcomeText());
    }

    @GetMapping("/application-info")
    public ApplicationInfoSo getApplicationInfo() {
        return uiService.getApplicationInfo();
    }

    @GetMapping("/company-info")
    public CompanyInfoSo getCompanyInfo() {
        return uiService.getCompanyInfo();
    }

    @GetMapping(value = "/business-conditions")
    public SingleValueBody<String> getBusinessConditions() {
        return new SingleValueBody<>(uiService.getBusinessConditions());
    }

    @GetMapping(value = "/cookies-info")
    public SingleValueBody<String> getCookiesInfo() {
        return new SingleValueBody<>(uiService.getCookiesInfo());
    }

    @GetMapping(value = "/gdpr-info")
    public SingleValueBody<String> getGdprInfo() {
        return new SingleValueBody<>(uiService.getGdprInfo());
    }

    @GetMapping(value = "/working-hours")
    public SingleValueBody<String> getWorkingHours() {
        return new SingleValueBody<>(uiService.getWorkingHours());
    }

    @GetMapping(value = "/units")
    public List<UnitSo> getUnits() {
        return uiService.getUnits();
    }

    @GetMapping("/application-images/{fileName}")
    public ResponseEntity<Resource> getApplicationImage(@PathVariable("fileName") final String fileName) {
        final ResourceEntity resourceEntity = uiService.getApplicationImage(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping("/product-images/{id}/{fileName}")
    public ResponseEntity<Resource> getProductImage(@PathVariable("id") final Long productId, @PathVariable("fileName") final String fileName) {
        final ResourceEntity resourceEntity = uiService.getProductImage(productId, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }
}
