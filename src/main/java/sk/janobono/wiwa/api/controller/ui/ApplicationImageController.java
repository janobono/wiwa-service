package sk.janobono.wiwa.api.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.model.ResourceEntity;

@RequiredArgsConstructor
@RestController("ui-application-images")
@RequestMapping(path = "/ui/application-images")
public class ApplicationImageController {

    private final ApplicationImageService applicationImageService;

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getApplicationImage(@PathVariable("fileName") final String fileName) {
        final ResourceEntity resourceEntity = applicationImageService.getApplicationImage(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }
}
