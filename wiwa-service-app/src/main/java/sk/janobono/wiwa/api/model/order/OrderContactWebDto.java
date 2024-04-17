package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrderContactWebDto(
        @NotBlank String name,
        @NotBlank String street,
        @NotBlank String zipCode,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String phone,
        @NotBlank @Email String email,
        String businessId,
        String taxId
) {
}
