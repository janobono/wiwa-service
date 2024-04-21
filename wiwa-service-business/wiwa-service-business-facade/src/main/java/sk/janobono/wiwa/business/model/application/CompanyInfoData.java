package sk.janobono.wiwa.business.model.application;

public record CompanyInfoData(
        String name,
        String street,
        String city,
        String zipCode,
        String state,
        String phone,
        String mail,
        String businessId,
        String taxId,
        String vatRegNo,
        String commercialRegisterInfo,
        String mapUrl
) {
}
