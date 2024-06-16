package sk.janobono.wiwa.business.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wiwa.business.impl.util.PropertyUtilService;
import sk.janobono.wiwa.business.model.CategoryData;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.OrderContent;
import sk.janobono.wiwa.model.OrderPattern;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ApplicationPropertyServiceImpl implements ApplicationPropertyService {

    private static final String MAINTENANCE = "MAINTENANCE";
    private static final String TITLE = "TITLE";
    private static final String WELCOME_TEXT = "WELCOME_TEXT";
    private static final String APP_INFO = "APP_INFO";
    private static final String COMPANY_INFO = "COMPANY_INFO";
    private static final String UNITS = "UNITS";
    private static final String VAT_RATE = "VAT_RATE";
    private static final String BUSINESS_CONDITIONS = "BUSINESS_CONDITIONS";
    private static final String COOKIES_INFO = "COOKIES_INFO";
    private static final String GDPR_INFO = "GDPR_INFO";
    private static final String ORDER_INFO = "ORDER_INFO";
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
    private static final String BOARD_MATERIAL_CATEGORY = "BOARD_MATERIAL_CATEGORY";
    private static final String BOARD_CATEGORIES = "BOARD_CATEGORIES";
    private static final String EDGE_CATEGORIES = "EDGE_CATEGORIES";
    private static final String ORDER_PROPERTIES = "ORDER_PROPERTIES";

    private final ObjectMapper objectMapper;

    private final CommonConfigProperties commonConfigProperties;
    private final JwtConfigProperties jwtConfigProperties;

    private final PropertyUtilService propertyUtilService;

    private final CodeListRepository codeListRepository;

    @Override
    public boolean getMaintenance() {
        return propertyUtilService.getProperty(Boolean::valueOf, MAINTENANCE).orElse(true);
    }

    @Transactional
    @Override
    public boolean setMaintenance(final boolean maintenance) {
        propertyUtilService.setProperty(Object::toString, MAINTENANCE, maintenance);
        return maintenance;
    }

    @Override
    public ApplicationPropertiesData getApplicationProperties() {
        return new ApplicationPropertiesData(commonConfigProperties.defaultLocale(),
                commonConfigProperties.appTitle(),
                commonConfigProperties.appDescription(),
                jwtConfigProperties.expiration(),
                commonConfigProperties.currency());
    }

    @Override
    public String getTitle() {
        return propertyUtilService.getProperty(TITLE).orElse("");
    }

    @Transactional
    @Override
    public String setTitle(final String title) {
        propertyUtilService.setProperty(TITLE, title);
        return title;
    }

    @Override
    public String getWelcomeText() {
        return propertyUtilService.getProperty(WELCOME_TEXT).orElse("");
    }

    @Transactional
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
        }, APP_INFO).orElseGet(Collections::emptyList);
    }

    @Transactional
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
        }, COMPANY_INFO).orElseGet(() -> CompanyInfoData.builder()
                .name("name")
                .street("street")
                .city("city")
                .zipCode("zipCode")
                .state("state")
                .phone("phone")
                .mail("mail")
                .businessId("businessId")
                .taxId("taxId")
                .vatRegNo("vatRegNo")
                .commercialRegisterInfo("commercialRegisterInfo")
                .mapUrl("mapUrl")
                .build()
        );
    }

    @Transactional
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
        }, UNITS).orElseGet(() -> List.of(
                new UnitData(Unit.MILLIMETER, "mm"),
                new UnitData(Unit.METER, "m"),
                new UnitData(Unit.SQUARE_METER, "㎡"),
                new UnitData(Unit.KILOGRAM, "kg"),
                new UnitData(Unit.PIECE, "p"))
        );
    }

    @Transactional
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
        return propertyUtilService.getProperty(BigDecimal::new, VAT_RATE).orElseGet(() -> new BigDecimal("20"));
    }

    @Transactional
    @Override
    public BigDecimal setVatRate(final BigDecimal value) {
        propertyUtilService.setProperty(BigDecimal::toPlainString, VAT_RATE, value);
        return value;
    }

    @Override
    public String getBusinessConditions() {
        return propertyUtilService.getProperty(BUSINESS_CONDITIONS).orElse("");
    }

    @Transactional
    @Override
    public String setBusinessConditions(final String businessConditions) {
        propertyUtilService.setProperty(BUSINESS_CONDITIONS, businessConditions);
        return businessConditions;
    }

    @Override
    public String getCookiesInfo() {
        return propertyUtilService.getProperty(COOKIES_INFO).orElse("");
    }

    @Transactional
    @Override
    public String setCookiesInfo(final String cookiesInfo) {
        propertyUtilService.setProperty(COOKIES_INFO, cookiesInfo);
        return cookiesInfo;
    }

    @Override
    public String getGdprInfo() {
        return propertyUtilService.getProperty(GDPR_INFO).orElse("");
    }

    @Transactional
    @Override
    public String setGdprInfo(final String gdprInfo) {
        propertyUtilService.setProperty(GDPR_INFO, gdprInfo);
        return gdprInfo;
    }

    @Override
    public String getOrderInfo() {
        return propertyUtilService.getProperty(ORDER_INFO).orElse("");
    }

    @Transactional
    @Override
    public String setOrderInfo(final String orderInfo) {
        propertyUtilService.setProperty(ORDER_INFO, orderInfo);
        return orderInfo;
    }

    @Override
    public String getWorkingHours() {
        return propertyUtilService.getProperty(WORKING_HOURS).orElse("");
    }

    @Transactional
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
        }, SIGN_UP_MAIL).orElseGet(() -> SignUpMailData.builder()
                .subject("Account activation")
                .title("Account activation")
                .message("Your account has been created. Please do not reply to this message.")
                .link("Click to activate your account.")
                .build()
        );
    }

    @Transactional
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
        }, RESET_PASSWORD_MAIL).orElseGet(() -> ResetPasswordMailData.builder()
                .subject("Password activation")
                .title("Password activation")
                .message("We have generated a new password for you. Please do not reply to this message.")
                .passwordMessage("New password: {0}")
                .link("Click to activate the password.")
                .build()
        );
    }

    @Transactional
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
        }, MANUFACTURE_PROPERTIES).orElseGet(() -> ManufacturePropertiesData.builder()
                .minimalSystemDimensions(new DimensionsData(new BigDecimal("50"), new BigDecimal("50")))
                .minimalEdgedBoardDimensions(new DimensionsData(new BigDecimal("250"), new BigDecimal("60")))
                .minimalLayeredBoardDimensions(new DimensionsData(new BigDecimal("250"), new BigDecimal("80")))
                .minimalFrameBoardDimensions(new DimensionsData(new BigDecimal("250"), new BigDecimal("80")))
                .edgeWidthAppend(new BigDecimal("8"))
                .edgeLengthAppend(new BigDecimal("40"))
                .duplicatedBoardAppend(new BigDecimal("10"))
                .build()
        );
    }

    @Transactional
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
        }, PRICE_FOR_GLUING_LAYER).orElseGet(() -> new PriceForGluingLayerData(new BigDecimal("10")));
    }

    @Transactional
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
        }, PRICES_FOR_GLUING_EDGE).orElseGet(() -> List.of(
                new PriceForGluingEdgeData(new BigDecimal("23.00"), new BigDecimal("0.700")),
                new PriceForGluingEdgeData(new BigDecimal("33.00"), new BigDecimal("0.850")),
                new PriceForGluingEdgeData(new BigDecimal("45.00"), new BigDecimal("0.980")),
                new PriceForGluingEdgeData(new BigDecimal("65.00"), new BigDecimal("1.680"))
        ));
    }

    @Transactional
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
        }, PRICES_FOR_CUTTING).orElseGet(() -> List.of(
                new PriceForCuttingData(new BigDecimal("19.00"), new BigDecimal("0.630")),
                new PriceForCuttingData(new BigDecimal("45.00"), new BigDecimal("0.720")),
                new PriceForCuttingData(new BigDecimal("65.00"), new BigDecimal("2.000"))
        ));
    }

    @Transactional
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
        }, FREE_DAYS).orElseGet(() -> List.of(
                new FreeDayData("Day of the Establishment of the Slovak Republic", 1, 1),
                new FreeDayData("Epiphany", 6, 1),
                new FreeDayData("Good Friday", 29, 3),
                new FreeDayData("Easter Monday", 1, 4),
                new FreeDayData("International Workers Day", 1, 5),
                new FreeDayData("Day of victory over fascism", 8, 5),
                new FreeDayData("St. Cyril and Methodius Day", 5, 6),
                new FreeDayData("Slovak National Uprising Anniversary", 29, 8),
                new FreeDayData("Day of the Constitution of the Slovak Republic", 1, 9),
                new FreeDayData("Day of Our Lady of the Seven Sorrows, patron saint of Slovakia", 15, 9),
                new FreeDayData("All Saints Day", 1, 11),
                new FreeDayData("Struggle for Freedom and Democracy Day", 17, 11),
                new FreeDayData("Christmas Eve", 24, 12),
                new FreeDayData("Christmas Day", 25, 12),
                new FreeDayData("St. Stephens Day", 26, 12)
        ));
    }

    @Transactional
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
        }, ORDER_COMMENT_MAIL).orElseGet(() -> OrderCommentMailData.builder()
                .subject("Order comment - order No.{0,number,###000}")
                .title("Order comment - order No.{0,number,###000}")
                .message("New comment was added. Please do not reply to this message.")
                .link("Click to see order details.")
                .build()
        );
    }

    @Transactional
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
        }, ORDER_SEND_MAIL).orElseGet(() -> OrderSendMailData.builder()
                .subject("Order send - order No.{0,number,###000}")
                .title("Order send - order No.{0,number,###000}")
                .message("Order was send. Please do not reply to this message.")
                .link("Click to see order details.")
                .attachment("detail{0,number,###000}.html")
                .build()
        );
    }

    @Transactional
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
        }, ORDER_STATUS_MAIL).orElseGet(() -> OrderStatusMailData.builder()
                .productionSubject("Order in production - order No.{0,number,###000}")
                .productionTitle("Order in production - order No.{0,number,###000}")
                .productionMessage("Your order is in production. Please do not reply to this message.")
                .readySubject("Order is ready for pickup - order No.{0,number,###000}")
                .readyTitle("Order is ready for pickup - order No.{0,number,###000}")
                .readyMessage("Your order is ready for pickup. Please do not reply to this message.")
                .finishedSubject("Thank you - order No.{0,number,###000}")
                .finishedTitle("Thank you - order No.{0,number,###000}")
                .finishedMessage("Thank you for your order. Please do not reply to this message.")
                .cancelledSubject("Order cancelled - order No.{0,number,###000}")
                .cancelledTitle("Order cancelled - order No.{0,number,###000}")
                .cancelledMessage("We are sorry, your order was cancelled. Please do not reply to this message.")
                .link("Click to see order details.")
                .attachment("detail{0,number,###000}.html")
                .build()
        );
    }

    @Transactional
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
    public CategoryData getBoardMaterialCategory() {
        final long categoryId = propertyUtilService.getProperty(Long::valueOf, BOARD_MATERIAL_CATEGORY).orElse(-1L);

        return codeListRepository.findById(categoryId).map(codeListDo -> new CategoryData(codeListDo.getId(),
                codeListDo.getCode(),
                codeListDo.getName())).orElseGet(() -> new CategoryData(categoryId, "NOT FOUND", "NOT FOUND"));
    }

    @Transactional
    @Override
    public CategoryData setBoardMaterialCategory(final long categoryId) {
        if (!codeListRepository.existsById(categoryId)) {
            throw WiwaException.CODE_LIST_NOT_FOUND.exception("Category does not exist");
        }
        propertyUtilService.setProperty(BOARD_MATERIAL_CATEGORY, Long.toString(categoryId));
        return getBoardMaterialCategory();
    }

    @Override
    public List<CategoryData> getBoardCategories() {
        return getCategories(BOARD_CATEGORIES);
    }

    @Override
    public List<CategoryData> setBoardCategories(final Set<Long> categoryIds) {
        checkCategories(categoryIds);
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, BOARD_CATEGORIES, categoryIds);
        return getBoardCategories();
    }

    @Override
    public List<CategoryData> getEdgeCategories() {
        return getCategories(EDGE_CATEGORIES);
    }

    @Override
    public List<CategoryData> setEdgeCategories(final Set<Long> categoryIds) {
        checkCategories(categoryIds);
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, EDGE_CATEGORIES, categoryIds);
        return getEdgeCategories();
    }

    @Override
    public OrderPropertiesData getOrderProperties() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, OrderPropertiesData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_PROPERTIES).orElseGet(() -> OrderPropertiesData.builder()
                .dimensions(Map.of())
                .boards(Map.of())
                .edges(Map.of())
                .corners(Map.of())
                .pattern(new HashMap<>() {{
                    put(OrderPattern.CSV_NUMBER, "{0} {1}");
                    put(OrderPattern.CSV_BASIC, "{0} (basic {1}-{2}x{3}mm-{4}p)");
                    put(OrderPattern.CSV_FRAME, "{0} (frame {1}-{2}x{3}mm-{4}p)");
                    put(OrderPattern.CSV_DUPLICATED_BASIC, "{0} (duplicated basic {1}-{2}x{3}mm-{4}p)");
                    put(OrderPattern.CSV_DUPLICATED_FRAME, "{0} (duplicated frame {1}-{2}x{3}mm-{4}p)");
                    put(OrderPattern.CSV_DECOR, "{0} {1} {2}");
                    put(OrderPattern.CSV_EDGE, "{0} {1}x{2,number,0.0}");
                    put(OrderPattern.CSV_CORNER_STRAIGHT, "{0} {1}x{2}");
                    put(OrderPattern.CSV_CORNER_ROUNDED, "{0} r{1}");
                    put(OrderPattern.PDF_TITLE, "Order No.{0}");
                    put(OrderPattern.PDF_ORDER_NUMBER, "{0,number,###000}");
                    put(OrderPattern.PDF_INTEGER, "{0} {1}");
                    put(OrderPattern.PDF_UNIT, "{0,number,###0.000} {1}");
                    put(OrderPattern.PDF_PRICE, "{0,number,###0.00} {1}");
                    put(OrderPattern.PDF_DECOR, "{0} {1} {2}");
                    put(OrderPattern.PDF_EDGE, "{0} {1}x{2,number,###0.0}");
                    put(OrderPattern.PDF_CORNER_STRAIGHT, "{0} {1} x {2} {3}");
                    put(OrderPattern.PDF_CORNER_ROUNDED, "r {0} {1}");
                }})
                .content(new HashMap<>() {{
                    put(OrderContent.MATERIAL_NOT_FOUND, "Material not found");
                    put(OrderContent.BOARD_NOT_FOUND, "Board not found");
                    put(OrderContent.EDGE_NOT_FOUND, "Edge not found");
                    put(OrderContent.CREATOR, "creator:");
                    put(OrderContent.CREATED, "created:");
                    put(OrderContent.ORDER_NUMBER, "order number:");
                    put(OrderContent.DELIVERY_DATE, "delivery date:");
                    put(OrderContent.PACKAGE_TYPE, "package type:");
                    put(OrderContent.CONTACT_INFO, "Contact info");
                    put(OrderContent.NAME, "name");
                    put(OrderContent.STREET, "street");
                    put(OrderContent.ZIP_CODE, "zip code");
                    put(OrderContent.CITY, "city");
                    put(OrderContent.STATE, "state");
                    put(OrderContent.PHONE, "phone");
                    put(OrderContent.EMAIL, "email");
                    put(OrderContent.BUSINESS_ID, "business id");
                    put(OrderContent.TAX_ID, "tax id");
                    put(OrderContent.ORDER_SUMMARY, "Order summary");
                    put(OrderContent.BOARD_SUMMARY, "Board consumption");
                    put(OrderContent.BOARD_SUMMARY_MATERIAL, "material");
                    put(OrderContent.BOARD_SUMMARY_NAME, "name");
                    put(OrderContent.BOARD_SUMMARY_AREA, "area");
                    put(OrderContent.BOARD_SUMMARY_COUNT, "board count");
                    put(OrderContent.BOARD_SUMMARY_WEIGHT, "weight");
                    put(OrderContent.BOARD_SUMMARY_PRICE, "price");
                    put(OrderContent.BOARD_SUMMARY_VAT_PRICE, "vat price");
                    put(OrderContent.EDGE_SUMMARY, "Edge consumption");
                    put(OrderContent.EDGE_SUMMARY_NAME, "name");
                    put(OrderContent.EDGE_SUMMARY_LENGTH, "length");
                    put(OrderContent.EDGE_SUMMARY_GLUE_LENGTH, "glue length");
                    put(OrderContent.EDGE_SUMMARY_WEIGHT, "weight");
                    put(OrderContent.EDGE_SUMMARY_EDGE_PRICE, "edge price");
                    put(OrderContent.EDGE_SUMMARY_EDGE_VAT_PRICE, "edge vat price");
                    put(OrderContent.EDGE_SUMMARY_GLUE_PRICE, "glue price");
                    put(OrderContent.EDGE_SUMMARY_GLUE_VAT_PRICE, "glue vat price");
                    put(OrderContent.GLUE_SUMMARY, "Glue area");
                    put(OrderContent.GLUE_SUMMARY_AREA, "area");
                    put(OrderContent.GLUE_SUMMARY_PRICE, "price");
                    put(OrderContent.GLUE_SUMMARY_VAT_PRICE, "vat price");
                    put(OrderContent.CUT_SUMMARY, "Formatting");
                    put(OrderContent.CUT_SUMMARY_THICKNESS, "thickness");
                    put(OrderContent.CUT_SUMMARY_AMOUNT, "amount");
                    put(OrderContent.CUT_SUMMARY_PRICE, "price");
                    put(OrderContent.CUT_SUMMARY_VAT_PRICE, "vat price");
                    put(OrderContent.TOTAL_SUMMARY, "Total");
                    put(OrderContent.TOTAL_SUMMARY_WEIGHT, "weight");
                    put(OrderContent.TOTAL_SUMMARY_PRICE, "price");
                    put(OrderContent.TOTAL_SUMMARY_VAT_PRICE, "vat price");
                    put(OrderContent.PARTS_LIST, "Part list");
                    put(OrderContent.PARTS_LIST_NAME, "name");
                    put(OrderContent.PARTS_LIST_NUMBER, "part number");
                    put(OrderContent.PARTS_LIST_X, "x");
                    put(OrderContent.PARTS_LIST_Y, "y");
                    put(OrderContent.PARTS_LIST_QUANTITY, "quantity");
                    put(OrderContent.PARTS_LIST_DESCRIPTION, "description");
                    put(OrderContent.PARTS_LIST_EDGES, "edges");
                    put(OrderContent.PARTS_LIST_CORNERS, "corners");
                    put(OrderContent.PARTS_LIST_BOARDS, "boards");
                    put(OrderContent.PARTS_LIST_POSITION, "position");
                }})
                .packageType(Map.of())
                .csvSeparator(";")
                .csvReplacements(Map.of("<.*?>", "", "\\s+", "_"))
                .csvColumns(Map.of())
                .build()
        );
    }

    @Transactional
    @Override
    public OrderPropertiesData setOrderProperties(final OrderPropertiesData orderProperties) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, ORDER_PROPERTIES, orderProperties);
        return orderProperties;
    }

    private void checkCategories(final Set<Long> categoryIds) {
        for (final Long categoryId : categoryIds) {
            if (!codeListRepository.existsById(categoryId)) {
                throw WiwaException.CODE_LIST_NOT_FOUND.exception("Category does not exist");
            }
        }
    }

    private List<CategoryData> getCategories(final String key) {
        final List<Long> categoryIds = propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, Long[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, key).orElseGet(Collections::emptyList);

        return categoryIds.stream()
                .map(codeListRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(cl -> new CategoryData(cl.getId(), cl.getCode(), cl.getName()))
                .toList();
    }
}
