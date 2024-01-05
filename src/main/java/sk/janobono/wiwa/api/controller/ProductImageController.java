package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.business.service.ProductService;
import sk.janobono.wiwa.model.ResourceEntity;

@RequiredArgsConstructor
@RestController
public class ProductImageController {

    private final ProductService productService;

    @GetMapping("/ui/product-images/{id}/{fileName}")
    public ResponseEntity<Resource> getProductImage(@PathVariable("id") final Long productId, @PathVariable("fileName") final String fileName) {
        final ResourceEntity resourceEntity = productService.getProductImage(productId, fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }
}
