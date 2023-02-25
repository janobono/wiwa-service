package sk.janobono.wiwa.api.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.common.model.ResourceEntitySo;

@Slf4j
@RequiredArgsConstructor
@RestController("ui-application-image")
@RequestMapping(path = "/ui/application-images")
public class ApplicationImageController {

    private final ApplicationImageService applicationImageService;

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getApplicationImage(@PathVariable("fileName") String fileName) {
        log.debug("getApplicationImage({})", fileName);
        ResourceEntitySo resourceEntityDto = applicationImageService.getApplicationImage(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntityDto.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntityDto.fileName() + "\"")
                .body(resourceEntityDto.resource());
    }
}
