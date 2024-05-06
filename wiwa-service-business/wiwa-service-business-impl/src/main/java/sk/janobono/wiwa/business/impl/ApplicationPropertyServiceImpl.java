package sk.janobono.wiwa.business.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.util.PropertyUtilService;
import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.model.board.BoardCategoryData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ApplicationPropertyServiceImpl implements ApplicationPropertyService {

    private static final String TITLE = "TITLE";
    private static final String WELCOME_TEXT = "WELCOME_TEXT";
    private static final String APP_INFO = "APP_INFO";
    private static final String COMPANY_INFO = "COMPANY_INFO";
    private static final String UNITS = "UNITS";
    private static final String VAT_RATE = "VAT_RATE";
    private static final String BUSINESS_CONDITIONS = "BUSINESS_CONDITIONS";
    private static final String COOKIES_INFO = "COOKIES_INFO";
    private static final String GDPR_INFO = "GDPR_INFO";
    private static final String WORKING_HOURS = "WORKING_HOURS";
    private static final String SIGN_UP_MAIL = "SIGN_UP_MAIL";
    private static final String RESET_PASSWORD_MAIL = "RESET_PASSWORD_MAIL";
    private static final String MANUFACTURE_PROPERTIES = "MANUFACTURE_PROPERTIES";
    private static final String PRICE_FOR_GLUING_LAYER = "PRICE_FOR_GLUING_LAYER";
    private static final String PRICES_FOR_GLUING_EDGE = "PRICES_FOR_GLUING_EDGE";
    private static final String PRICES_FOR_CUTTING = "PRICES_FOR_CUTTING";
    private static final String FREE_DAYS = "FREE_DAYS";
    private static final String ORDER_COMMENT_MAIL = "ORDER_COMMENT_MAIL";
    private static final String ORDER_SEND_MAIL = "ORDER_SEND_MAIL";
    private static final String ORDER_STATUS_MAIL = "ORDER_STATUS_MAIL";
    private static final String CSV_PROPERTIES = "CSV_PROPERTIES";
    private static final String BOARD_MATERIAL_CATEGORY = "BOARD_MATERIAL_CATEGORY";

    private final ObjectMapper objectMapper;

    private final CommonConfigProperties commonConfigProperties;
    private final JwtConfigProperties jwtConfigProperties;

    private final PropertyUtilService propertyUtilService;

    private final CodeListRepository codeListRepository;

    @Override
    public ApplicationPropertiesData getApplicationProperties() {
        return new ApplicationPropertiesData(
                commonConfigProperties.defaultLocale(),
                commonConfigProperties.appTitle(),
                commonConfigProperties.appDescription(),
                jwtConfigProperties.expiration(),
                commonConfigProperties.currency()
        );
    }

    @Override
    public String getTitle() {
        return propertyUtilService.getProperty(TITLE);
    }

    @Override
    public String setTitle(final String title) {
        propertyUtilService.setProperty(TITLE, title);
        return title;
    }

    @Override
    public String getWelcomeText() {
        return propertyUtilService.getProperty(WELCOME_TEXT);
    }

    @Override
    public String setWelcomeText(final String welcomeText) {
        propertyUtilService.setProperty(WELCOME_TEXT, welcomeText);
        return welcomeText;
    }

    @Override
    public List<String> getApplicationInfo() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, String[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, APP_INFO);
    }

    @Override
    public List<String> setApplicationInfo(final List<String> applicationInfo) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, APP_INFO, applicationInfo);
        return applicationInfo;
    }

    @Override
    public CompanyInfoData getCompanyInfo() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, CompanyInfoData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, COMPANY_INFO);
    }

    @Override
    public CompanyInfoData setCompanyInfo(final CompanyInfoData companyInfo) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, COMPANY_INFO, companyInfo);
        return companyInfo;
    }

    @Override
    public List<UnitData> getUnits() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, UnitData[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, UNITS);
    }

    @Override
    public List<UnitData> setUnits(final List<UnitData> units) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, UNITS, units);
        return units;
    }

    @Override
    public BigDecimal getVatRate() {
        return propertyUtilService.getProperty(BigDecimal::new, VAT_RATE);
    }

    @Override
    public BigDecimal setVatRate(final BigDecimal value) {
        propertyUtilService.setProperty(BigDecimal::toPlainString, VAT_RATE, value);
        return value;
    }

    @Override
    public String getBusinessConditions() {
        return propertyUtilService.getProperty(BUSINESS_CONDITIONS);
    }

    @Override
    public String setBusinessConditions(final String businessConditions) {
        propertyUtilService.setProperty(BUSINESS_CONDITIONS, businessConditions);
        return businessConditions;
    }

    @Override
    public String getCookiesInfo() {
        return propertyUtilService.getProperty(COOKIES_INFO);
    }

    @Override
    public String setCookiesInfo(final String cookiesInfo) {
        propertyUtilService.setProperty(COOKIES_INFO, cookiesInfo);
        return cookiesInfo;
    }

    @Override
    public String getGdprInfo() {
        return propertyUtilService.getProperty(GDPR_INFO);
    }

    @Override
    public String setGdprInfo(final String gdprInfo) {
        propertyUtilService.setProperty(GDPR_INFO, gdprInfo);
        return gdprInfo;
    }

    @Override
    public String getWorkingHours() {
        return propertyUtilService.getProperty(WORKING_HOURS);
    }

    @Override
    public String setWorkingHours(final String workingHours) {
        propertyUtilService.setProperty(WORKING_HOURS, workingHours);
        return workingHours;
    }

    @Override
    public SignUpMailData getSignUpMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, SignUpMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, SIGN_UP_MAIL);
    }

    @Override
    public SignUpMailData setSignUpMail(final SignUpMailData signUpMail) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, SIGN_UP_MAIL, signUpMail);
        return signUpMail;
    }

    @Override
    public ResetPasswordMailData getResetPasswordMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, ResetPasswordMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, RESET_PASSWORD_MAIL);
    }

    @Override
    public ResetPasswordMailData setResetPasswordMail(final ResetPasswordMailData resetPasswordMail) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, RESET_PASSWORD_MAIL, resetPasswordMail);
        return resetPasswordMail;
    }

    @Override
    public ManufacturePropertiesData getManufactureProperties() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, ManufacturePropertiesData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, MANUFACTURE_PROPERTIES);
    }

    @Override
    public ManufacturePropertiesData setManufactureProperties(final ManufacturePropertiesData manufactureProperties) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, MANUFACTURE_PROPERTIES, manufactureProperties);
        return manufactureProperties;
    }

    @Override
    public PriceForGluingLayerData getPriceForGluingLayer() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, PriceForGluingLayerData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, PRICE_FOR_GLUING_LAYER);
    }

    @Override
    public PriceForGluingLayerData setPriceForGluingLayer(final PriceForGluingLayerData priceForGluingLayer) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, PRICE_FOR_GLUING_LAYER, priceForGluingLayer);
        return priceForGluingLayer;
    }

    @Override
    public List<PriceForGluingEdgeData> getPricesForGluingEdge() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, PriceForGluingEdgeData[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, PRICES_FOR_GLUING_EDGE);
    }

    @Override
    public List<PriceForGluingEdgeData> setPricesForGluingEdge(final List<PriceForGluingEdgeData> pricesForGluingEdge) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, PRICES_FOR_GLUING_EDGE, pricesForGluingEdge);
        return pricesForGluingEdge;
    }

    @Override
    public List<PriceForCuttingData> getPricesForCutting() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, PriceForCuttingData[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, PRICES_FOR_CUTTING);
    }

    @Override
    public List<PriceForCuttingData> setPricesForCutting(final List<PriceForCuttingData> pricesForCutting) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, PRICES_FOR_CUTTING, pricesForCutting);
        return pricesForCutting;
    }

    @Override
    public List<FreeDayData> getFreeDays() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, FreeDayData[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, FREE_DAYS);
    }

    @Override
    public List<FreeDayData> setFreeDays(final List<FreeDayData> freeDays) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, FREE_DAYS, freeDays);
        return freeDays;
    }

    @Override
    public OrderCommentMailData getOrderCommentMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, OrderCommentMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_COMMENT_MAIL);
    }

    @Override
    public OrderCommentMailData setOrderCommentMail(final OrderCommentMailData orderCommentMailData) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_COMMENT_MAIL, orderCommentMailData);
        return orderCommentMailData;
    }

    @Override
    public OrderSendMailData getOrderSendMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, OrderSendMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_SEND_MAIL);
    }

    @Override
    public OrderSendMailData setOrderSendMail(final OrderSendMailData orderSendMail) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_SEND_MAIL, orderSendMail);
        return orderSendMail;
    }

    @Override
    public OrderStatusMailData getOrderStatusMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, OrderStatusMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_STATUS_MAIL);
    }

    @Override
    public OrderStatusMailData setOrderStatusMail(final OrderStatusMailData orderStatusMail) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_STATUS_MAIL, orderStatusMail);
        return orderStatusMail;
    }

    @Override
    public CSVPropertiesData getCSVProperties() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, CSVPropertiesData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, CSV_PROPERTIES);
    }

    @Override
    public CSVPropertiesData setCSVProperties(final CSVPropertiesData csvProperties) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, CSV_PROPERTIES, csvProperties);
        return csvProperties;
    }

    @Override
    public BoardCategoryData getBoardMaterialCategory() {
        final long categoryId = Long.parseLong(propertyUtilService.getProperty(BOARD_MATERIAL_CATEGORY));

        return codeListRepository.findById(categoryId)
                .map(codeListDo -> new BoardCategoryData(codeListDo.getId(), codeListDo.getCode(), codeListDo.getName()))
                .orElseGet(() -> new BoardCategoryData(categoryId, "NOT FOUND", "NOT FOUND"));
    }

    @Override
    public BoardCategoryData setBoardMaterialCategory(final long categoryId) {
        if (!codeListRepository.existsById(categoryId)) {
            throw WiwaException.CODE_LIST_NOT_FOUND.exception("Category does not exist");
        }
        propertyUtilService.setProperty(BOARD_MATERIAL_CATEGORY, Long.toString(categoryId));
        return getBoardMaterialCategory();
    }
}
