package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.PDFFormat;
import sk.janobono.wiwa.model.PdfContent;

import java.util.Map;

public record PDFPropertiesWebDto(
        @NotNull Map<PDFFormat, String> format,
        @NotNull Map<PdfContent, String> content,
        @NotNull Map<OrderPackageType, String> packageType
) {
}
