package sk.janobono.wiwa.business.model.application;

import lombok.Builder;

@Builder
public record OrderStatusMailData(
        String productionSubject,
        String productionTitle,
        String productionMessage,
        String readySubject,
        String readyTitle,
        String readyMessage,
        String finishedSubject,
        String finishedTitle,
        String finishedMessage,
        String cancelledSubject,
        String cancelledTitle,
        String cancelledMessage,
        String link,
        String attachment
) {
}
