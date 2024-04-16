package sk.janobono.wiwa.business.service;

import sk.janobono.wiwa.business.model.*;

import java.math.BigDecimal;
import java.util.List;

public interface ApplicationPropertyService {

    ApplicationPropertiesData getApplicationProperties();

    String getTitle();

    String setTitle(final String title);

    String getWelcomeText();

    String setWelcomeText(final String welcomeText);

    List<String> getApplicationInfo();

    List<String> setApplicationInfo(final List<String> applicationInfo);

    CompanyInfoData getCompanyInfo();

    CompanyInfoData setCompanyInfo(final CompanyInfoData companyInfo);

    List<UnitData> getUnits();

    List<UnitData> setUnits(final List<UnitData> units);

    BigDecimal getVatRate();

    BigDecimal setVatRate(final BigDecimal value);

    String getBusinessConditions();

    String setBusinessConditions(final String businessConditions);

    String getCookiesInfo();

    String setCookiesInfo(final String cookiesInfo);

    String getGdprInfo();

    String setGdprInfo(final String gdprInfo);

    String getWorkingHours();

    String setWorkingHours(final String workingHours);

    SignUpMailData getSignUpMail();

    SignUpMailData setSignUpMail(final SignUpMailData signUpMail);

    ResetPasswordMailData getResetPasswordMail();

    ResetPasswordMailData setResetPasswordMail(final ResetPasswordMailData resetPasswordMail);
}
