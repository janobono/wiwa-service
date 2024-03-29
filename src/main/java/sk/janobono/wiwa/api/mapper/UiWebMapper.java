package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.ApplicationPropertiesWebDto;
import sk.janobono.wiwa.api.model.CaptchaWebDto;
import sk.janobono.wiwa.api.model.CompanyInfoWebDto;
import sk.janobono.wiwa.api.model.UnitWebDto;
import sk.janobono.wiwa.business.model.captcha.CaptchaData;
import sk.janobono.wiwa.business.model.ui.ApplicationPropertiesData;
import sk.janobono.wiwa.business.model.ui.CompanyInfoData;
import sk.janobono.wiwa.business.model.ui.UnitData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UiWebMapper {
    CaptchaWebDto mapToWebDto(CaptchaData captcha);

    UnitData mapToData(UnitWebDto unit);

    UnitWebDto mapToWebDto(UnitData unit);

    CompanyInfoData mapToData(CompanyInfoWebDto companyInfo);

    CompanyInfoWebDto mapToWebDto(CompanyInfoData companyInfo);

    ApplicationPropertiesWebDto mapToWebDto(ApplicationPropertiesData applicationProperties);
}
