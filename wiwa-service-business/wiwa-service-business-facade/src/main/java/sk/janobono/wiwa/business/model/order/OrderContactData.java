package sk.janobono.wiwa.business.model.order;

import lombok.Builder;

@Builder
public record OrderContactData(
        String name,
        String street,
        String zipCode,
        String city,
        String state,
        String phone,
        String email,
        String businessId,
        String taxId
) {
}
