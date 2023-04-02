package sk.janobono.wiwa.api.controller.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.component.WebImageUtil;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.business.service.ApplicationImageService;

@Slf4j
@RequiredArgsConstructor
@RestController("config-application-images")
@RequestMapping(path = "/config/application-images")
public class ApplicationImageController {

    private final ApplicationImageService applicationImageService;
    private final WebImageUtil webImageUtil;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public Page<ApplicationImageWeb> getApplicationImages(Pageable pageable) {
        log.debug("getApplicationImages({})", pageable);
        return applicationImageService.getApplicationImages(pageable).map(webImageUtil::toWeb);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public ApplicationImageWeb upload(@RequestParam("file") MultipartFile multipartFile) {
        log.debug("upload({})", multipartFile.getOriginalFilename());
        return webImageUtil.toWeb(applicationImageService.setApplicationImage(multipartFile));
    }

    @DeleteMapping("/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public void deleteApplicationImage(@PathVariable("fileName") String fileName) {
        log.debug("deleteApplicationImage({})", fileName);
        applicationImageService.deleteApplicationImage(fileName);
    }
}
