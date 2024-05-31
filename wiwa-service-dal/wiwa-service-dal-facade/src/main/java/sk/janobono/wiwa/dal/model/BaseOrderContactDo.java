package sk.janobono.wiwa.dal.model;

import lombok.Builder;

@Builder
public record BaseOrderContactDo(
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
