package sk.janobono.wiwa.business.model.ui;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyInfoSo(
        @NotBlank String name,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String zipCode,
        @NotBlank String state,
        @NotBlank String phone,
        @NotBlank @Email String mail,
        @NotNull String businessId,
        @NotNull String taxId,
        @NotNull String vatRegNo,
        @NotNull String commercialRegisterInfo,
        @NotBlank String mapUrl
) {
}
