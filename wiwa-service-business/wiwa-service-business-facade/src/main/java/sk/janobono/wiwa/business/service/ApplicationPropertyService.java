package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.model.board.BoardCategoryData;
import sk.janobono.wiwa.business.model.edge.EdgeCategoryData;

import java.math.BigDecimal;
import java.util.List;

public interface ApplicationPropertyService {

    boolean getMaintenance();

    boolean setMaintenance(boolean maintenance);

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

    String getOrderInfo();

    String setOrderInfo(String orderInfo);

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

    BoardCategoryData getBoardMaterialCategory();

    BoardCategoryData setBoardMaterialCategory(final long categoryId);

    List<BoardCategoryData> getBoardCategories();

    List<BoardCategoryData> setBoardCategories(final List<Long> categoryIds);

    List<EdgeCategoryData> getEdgeCategories();

    List<EdgeCategoryData> setEdgeCategories(final List<Long> categoryIds);

    OrderPropertiesData getOrderProperties();

    OrderPropertiesData setOrderProperties(OrderPropertiesData orderProperties);
}
