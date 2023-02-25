package sk.janobono.wiwa.api.uimanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.business.service.ApplicationImageService;

@Slf4j
@RequiredArgsConstructor
@RestController("ui-management-application-image")
@RequestMapping(path = "/ui-management/application-images")
public class ApplicationImageController {

    private final ApplicationImageService applicationImageService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ResponseEntity<Page<ApplicationImageSo>> getApplicationImages(Pageable pageable) {
        log.debug("getApplicationImages({})", pageable);
        return new ResponseEntity<>(applicationImageService.getApplicationImages(pageable), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ResponseEntity<ApplicationImageSo> upload(final @RequestParam("file") MultipartFile multipartFile) {
        log.debug("upload({})", multipartFile.getOriginalFilename());
        return new ResponseEntity<>(applicationImageService.setApplicationImage(multipartFile), HttpStatus.OK);
    }
}
