package sk.janobono.wiwa.business.service;

import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.business.model.ui.LocalizedDataSo;
import sk.janobono.wiwa.common.model.ResourceEntitySo;

public interface UiService {

    ResourceEntitySo getLogo();

    String getTitle();

    String getWelcomeText();

    ApplicationInfoSo getApplicationInfo();

    CompanyInfoSo getCompanyInfo();

    String getCookiesInfo();

    String getGdprInfo();

    String getWorkingHours();

    ApplicationImageSo setLogo(MultipartFile multipartFile);

    LocalizedDataSo<String> setTitle(LocalizedDataSo<String> data);

    LocalizedDataSo<String> setWelcomeText(LocalizedDataSo<String> data);

    LocalizedDataSo<ApplicationInfoSo> setApplicationInfo(LocalizedDataSo<ApplicationInfoSo> data);

    LocalizedDataSo<CompanyInfoSo> setCompanyInfo(LocalizedDataSo<CompanyInfoSo> data);

    LocalizedDataSo<String> setCookiesInfo(LocalizedDataSo<String> data);

    LocalizedDataSo<String> setGdprInfo(LocalizedDataSo<String> data);

    LocalizedDataSo<String> setWorkingHours(LocalizedDataSo<String> data);
}
