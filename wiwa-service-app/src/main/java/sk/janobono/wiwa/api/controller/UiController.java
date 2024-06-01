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
import sk.janobono.wiwa.api.model.ResourceEntityWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.api.model.captcha.CaptchaWebDto;
import sk.janobono.wiwa.api.service.UiApiService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/ui")
public class UiController {

    private final UiApiService uiApiService;

    @GetMapping("/captcha")
    public CaptchaWebDto getCaptcha() {
        return uiApiService.getCaptcha();
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> getLogo() {
        final ResourceEntityWebDto resourceEntity = uiApiService.getLogo();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping("/application-images/{fileName}")
    public ResponseEntity<Resource> getApplicationImage(@PathVariable("fileName") final String fileName) {
        final ResourceEntityWebDto resourceEntity = uiApiService.getApplicationImage(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping("/board-images/{id}")
    public ResponseEntity<Resource> getBoardImage(@PathVariable("id") final Long boardId) {
        final ResourceEntityWebDto resourceEntity = uiApiService.getBoardImage(boardId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping("/edge-images/{id}")
    public ResponseEntity<Resource> getEdgeImage(@PathVariable("id") final Long edgeId) {
        final ResourceEntityWebDto resourceEntity = uiApiService.getEdgeImage(edgeId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping("/maintenance")
    public SingleValueBodyWebDto<Boolean> getMaintenance() {
        return uiApiService.getMaintenance();
    }

    @GetMapping("/application-properties")
    public ApplicationPropertiesWebDto getApplicationProperties() {
        return uiApiService.getApplicationProperties();
    }

    @GetMapping(value = "/title")
    public SingleValueBodyWebDto<String> getTitle() {
        return uiApiService.getTitle();
    }

    @GetMapping(value = "/welcome-text")
    public SingleValueBodyWebDto<String> getWelcomeText() {
        return uiApiService.getWelcomeText();
    }

    @GetMapping("/application-info")
    public List<String> getApplicationInfo() {
        return uiApiService.getApplicationInfo();
    }

    @GetMapping("/company-info")
    public CompanyInfoWebDto getCompanyInfo() {
        return uiApiService.getCompanyInfo();
    }

    @GetMapping(value = "/units")
    public List<UnitWebDto> getUnits() {
        return uiApiService.getUnits();
    }

    @GetMapping(value = "/business-conditions")
    public SingleValueBodyWebDto<String> getBusinessConditions() {
        return uiApiService.getBusinessConditions();
    }

    @GetMapping(value = "/cookies-info")
    public SingleValueBodyWebDto<String> getCookiesInfo() {
        return uiApiService.getCookiesInfo();
    }

    @GetMapping(value = "/gdpr-info")
    public SingleValueBodyWebDto<String> getGdprInfo() {
        return uiApiService.getGdprInfo();
    }

    @GetMapping(value = "/order-info")
    public SingleValueBodyWebDto<String> getOrderInfo() {
        return uiApiService.getOrderInfo();
    }

    @GetMapping(value = "/working-hours")
    public SingleValueBodyWebDto<String> getWorkingHours() {
        return uiApiService.getWorkingHours();
    }

    @GetMapping(value = "/free-days")
    public List<FreeDayWebDto> getFreeDays() {
        return uiApiService.getFreeDays();
    }

    @GetMapping(value = "/order-properties")
    public OrderPropertiesWebDto getOrderProperties() {
        return uiApiService.getOrderProperties();
    }
}
