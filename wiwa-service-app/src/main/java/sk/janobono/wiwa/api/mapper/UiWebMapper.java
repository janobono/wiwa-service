package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.DimensionsWebDto;
import sk.janobono.wiwa.api.model.EntryWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.api.model.captcha.CaptchaWebDto;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.model.captcha.CaptchaData;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    default OrderPropertiesWebDto mapToWebDto(final OrderPropertiesData orderProperties) {
        if (orderProperties == null) {
            return null;
        }
        return OrderPropertiesWebDto.builder()
                .dimensions(
                        Optional.ofNullable(orderProperties.dimensions()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .boards(
                        Optional.ofNullable(orderProperties.boards()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .edges(
                        Optional.ofNullable(orderProperties.edges()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .corners(
                        Optional.ofNullable(orderProperties.corners()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .pattern(
                        Optional.ofNullable(orderProperties.pattern()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .content(
                        Optional.ofNullable(orderProperties.content()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .packageType(
                        Optional.ofNullable(orderProperties.packageType()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .csvSeparator(orderProperties.csvSeparator())
                .csvReplacements(
                        Optional.ofNullable(orderProperties.csvReplacements()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .csvColumns(
                        Optional.ofNullable(orderProperties.csvColumns()).stream()
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .map(item -> new EntryWebDto<>(item.getKey(), item.getValue()))
                                .collect(Collectors.toSet())
                )
                .build();
    }

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

    default OrderPropertiesData mapToData(final OrderPropertiesWebDto orderProperties) {
        if (orderProperties == null) {
            return null;
        }
        return OrderPropertiesData.builder()
                .dimensions(
                        Optional.ofNullable(orderProperties.dimensions()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .boards(
                        Optional.ofNullable(orderProperties.boards()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .edges(
                        Optional.ofNullable(orderProperties.edges()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .corners(
                        Optional.ofNullable(orderProperties.corners()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .pattern(
                        Optional.ofNullable(orderProperties.pattern()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .content(
                        Optional.ofNullable(orderProperties.content()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .packageType(
                        Optional.ofNullable(orderProperties.packageType()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .csvSeparator(orderProperties.csvSeparator())
                .csvReplacements(
                        Optional.ofNullable(orderProperties.csvReplacements()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .csvColumns(
                        Optional.ofNullable(orderProperties.csvColumns()).stream()
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(EntryWebDto::key, EntryWebDto::value))
                )
                .build();
    }
}
