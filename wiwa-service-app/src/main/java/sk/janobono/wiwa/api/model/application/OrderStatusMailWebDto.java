package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusMailWebDto(
        @NotBlank String productionSubject,
        @NotBlank String productionTitle,
        @NotBlank String productionMessage,
        @NotBlank String readySubject,
        @NotBlank String readyTitle,
        @NotBlank String readyMessage,
        @NotBlank String finishedSubject,
        @NotBlank String finishedTitle,
        @NotBlank String finishedMessage,
        @NotBlank String cancelledSubject,
        @NotBlank String cancelledTitle,
        @NotBlank String cancelledMessage,
        @NotBlank String link,
        @NotBlank String attachment
) {
}
