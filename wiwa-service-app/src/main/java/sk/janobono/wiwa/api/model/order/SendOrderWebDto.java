package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import sk.janobono.wiwa.model.OrderPackageType;

import java.time.LocalDate;

public record SendOrderWebDto(
        @NotNull OrderContactWebDto contact,
        @NotNull Boolean gdprAgreement,
        @NotNull Boolean businessConditionsAgreement,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate,
        @NotNull OrderPackageType packageType
) {
}
