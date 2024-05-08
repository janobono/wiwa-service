package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.model.board.BoardCategoryData;

import java.math.BigDecimal;
import java.util.List;

public interface ApplicationPropertyService {

    ApplicationPropertiesData getApplicationProperties();

    String getTitle();

    String setTitle(String title);

    String getWelcomeText();

    String setWelcomeText(String welcomeText);

    List<String> getApplicationInfo();

    List<String> setApplicationInfo(List<String> applicationInfo);

    CompanyInfoData getCompanyInfo();

    CompanyInfoData setCompanyInfo(CompanyInfoData companyInfo);

    List<UnitData> getUnits();

    List<UnitData> setUnits(List<UnitData> units);

    BigDecimal getVatRate();

    BigDecimal setVatRate(BigDecimal value);

    String getBusinessConditions();

    String setBusinessConditions(String businessConditions);

    String getCookiesInfo();

    String setCookiesInfo(String cookiesInfo);

    String getGdprInfo();

    String setGdprInfo(String gdprInfo);

    String getWorkingHours();

    String setWorkingHours(String workingHours);

    SignUpMailData getSignUpMail();

    SignUpMailData setSignUpMail(SignUpMailData signUpMail);

    ResetPasswordMailData getResetPasswordMail();

    ResetPasswordMailData setResetPasswordMail(ResetPasswordMailData resetPasswordMail);

    ManufacturePropertiesData getManufactureProperties();

    ManufacturePropertiesData setManufactureProperties(ManufacturePropertiesData manufactureProperties);

    PriceForGluingLayerData getPriceForGluingLayer();

    PriceForGluingLayerData setPriceForGluingLayer(PriceForGluingLayerData priceForGluingLayer);

    List<PriceForGluingEdgeData> getPricesForGluingEdge();

    List<PriceForGluingEdgeData> setPricesForGluingEdge(List<PriceForGluingEdgeData> pricesForGluingEdge);

    List<PriceForCuttingData> getPricesForCutting();

    List<PriceForCuttingData> setPricesForCutting(List<PriceForCuttingData> pricesForCutting);

    List<FreeDayData> getFreeDays();

    List<FreeDayData> setFreeDays(List<FreeDayData> freeDays);

    OrderCommentMailData getOrderCommentMail();

    OrderCommentMailData setOrderCommentMail(OrderCommentMailData orderCommentMail);

    OrderSendMailData getOrderSendMail();

    OrderSendMailData setOrderSendMail(OrderSendMailData orderSendMail);

    OrderStatusMailData getOrderStatusMail();

    OrderStatusMailData setOrderStatusMail(OrderStatusMailData orderStatusMail);

    CSVPropertiesData getCSVProperties();

    CSVPropertiesData setCSVProperties(CSVPropertiesData csvProperties);

    BoardCategoryData getBoardMaterialCategory();

    BoardCategoryData setBoardMaterialCategory(final long categoryId);

    PDFPropertiesData getPDFProperties();

    PDFPropertiesData setPDFProperties(PDFPropertiesData pdfProperties);
}
