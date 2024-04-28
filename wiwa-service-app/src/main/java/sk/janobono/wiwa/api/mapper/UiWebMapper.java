package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.DimensionsWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.api.model.captcha.CaptchaWebDto;
import sk.janobono.wiwa.business.model.DimensionsData;
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

    SignUpMailWebDto mapToWebDto(SignUpMailData signUpMail);

    DimensionsWebDto mapToWebDto(DimensionsData dimensions);

    ManufacturePropertiesWebDto mapToWebDto(ManufacturePropertiesData manufactureProperties);

    PriceForCuttingWebDto mapToWebDto(PriceForCuttingData priceForCutting);

    PriceForGluingEdgeWebDto mapToWebDto(PriceForGluingEdgeData priceForGluingEdge);

    PriceForGluingLayerWebDto mapToWebDto(PriceForGluingLayerData priceForGluingLayer);

    FreeDayWebDto mapToWebDto(FreeDayData freeDayData);

    OrderCommentMailWebDto mapToWebDto(OrderCommentMailData orderCommentMail);

    OrderSendMailWebDto mapToWebDto(OrderSendMailData orderSendMail);

    OrderStatusMailWebDto mapToWebDto(OrderStatusMailData orderStatusMail);

    ResetPasswordMailData mapToData(ResetPasswordMailWebDto resetPasswordMail);

    SignUpMailData mapToData(SignUpMailWebDto signUpMail);

    default DimensionsData mapToData(final DimensionsWebDto dimensions) {
        if (dimensions == null) {
            return null;
        }
        return new DimensionsData(dimensions.x(), dimensions.y());
    }

    ManufacturePropertiesData mapToData(ManufacturePropertiesWebDto manufactureProperties);

    PriceForCuttingData mapToData(PriceForCuttingWebDto priceForCutting);

    PriceForGluingEdgeData mapToData(PriceForGluingEdgeWebDto priceForGluingEdge);

    PriceForGluingLayerData mapToData(PriceForGluingLayerWebDto priceForGluingLayer);

    FreeDayData mapToData(FreeDayWebDto freeDay);

    OrderCommentMailData mapToData(OrderCommentMailWebDto orderCommentMail);

    OrderSendMailData mapToData(OrderSendMailWebDto orderSendMail);

    OrderStatusMailData mapToData(OrderStatusMailWebDto orderStatusMail);
}
