package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfItemData(
        String partNum,
        String name,
        String description,
        String quantity,
        String type


) {
}
