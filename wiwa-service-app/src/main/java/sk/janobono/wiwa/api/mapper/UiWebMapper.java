package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.*;
import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.model.captcha.CaptchaData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UiWebMapper {
    CaptchaWebDto mapToWebDto(CaptchaData captcha);

    UnitData mapToData(UnitWebDto unit);

    UnitWebDto mapToWebDto(UnitData unit);

    CompanyInfoData mapToData(CompanyInfoWebDto companyInfo);

    CompanyInfoWebDto mapToWebDto(CompanyInfoData companyInfo);

    ApplicationPropertiesWebDto mapToWebDto(ApplicationPropertiesData applicationProperties);

    ResetPasswordMailWebDto mapToWebDto(ResetPasswordMailData resetPasswordMail);

    ResetPasswordMailData mapToData(ResetPasswordMailWebDto resetPasswordMail);

    SignUpMailWebDto mapToWebDto(SignUpMailData signUpMail);

    SignUpMailData mapToData(SignUpMailWebDto signUpMail);
}
