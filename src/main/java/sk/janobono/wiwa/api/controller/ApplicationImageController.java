package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.service.ApplicationImageService;
import sk.janobono.wiwa.model.ApplicationImage;

@RequiredArgsConstructor
@RestController
public class ApplicationImageController {

    private final ApplicationImageService applicationImageService;

    @GetMapping("/config/application-images")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public Page<ApplicationImage> getApplicationImages(final Pageable pageable) {
        return applicationImageService.getApplicationImages(pageable);
    }

    @PostMapping("/config/application-images")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ApplicationImage upload(@RequestParam("file") final MultipartFile multipartFile) {
        return applicationImageService.setApplicationImage(multipartFile);
    }

    @DeleteMapping("/config/application-images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public void deleteApplicationImage(@PathVariable("fileName") final String fileName) {
        applicationImageService.deleteApplicationImage(fileName);
    }
}
