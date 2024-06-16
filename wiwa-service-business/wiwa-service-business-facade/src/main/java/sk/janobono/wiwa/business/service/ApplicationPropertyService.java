package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.CategoryData;
import sk.janobono.wiwa.business.model.application.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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

    CategoryData getBoardMaterialCategory();

    CategoryData setBoardMaterialCategory(final long categoryId);

    List<CategoryData> getBoardCategories();

    List<CategoryData> setBoardCategories(final Set<Long> categoryIds);

    List<CategoryData> getEdgeCategories();

    List<CategoryData> setEdgeCategories(final Set<Long> categoryIds);

    OrderPropertiesData getOrderProperties();

    OrderPropertiesData setOrderProperties(OrderPropertiesData orderProperties);
}
