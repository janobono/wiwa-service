package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.PDFFormat;
import sk.janobono.wiwa.model.PdfContent;

import java.util.Map;

public record PDFPropertiesData(
        Map<PDFFormat, String> format,
        Map<PdfContent, String> content,
        Map<OrderPackageType, String> packageType
) {
}
