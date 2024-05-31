package sk.janobono.wiwa.business.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sk.janobono.wiwa.business.TestConfigProperties;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.business.impl.util.PropertyUtilService;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationPropertyServiceTest {

    private ApplicationPropertyService applicationPropertyService;

    @BeforeEach
    void setUp() {
        final CommonConfigProperties commonConfigProperties = Mockito.mock(CommonConfigProperties.class);
        final JwtConfigProperties jwtConfigProperties = Mockito.mock(JwtConfigProperties.class);
        final TestConfigProperties testConfigProperties = new TestConfigProperties();
        testConfigProperties.mock(commonConfigProperties);
        testConfigProperties.mock(jwtConfigProperties);

        final ApplicationPropertyRepository applicationPropertyRepository = Mockito.mock(ApplicationPropertyRepository.class);
        final CodeListRepository codeListRepository = Mockito.mock(CodeListRepository.class);
        final TestRepositories testRepositories = new TestRepositories();
        testRepositories.mock(applicationPropertyRepository);
        testRepositories.mock(codeListRepository);

        codeListRepository.save(CodeListDo.builder().code("code").name("name").build());

        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        applicationPropertyService = new ApplicationPropertyServiceImpl(
                objectMapper,
                commonConfigProperties,
                jwtConfigProperties,
                new PropertyUtilService(applicationPropertyRepository),
                codeListRepository
        );
    }

    @Test
    void maintenance_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getMaintenance()).isTrue();
        assertThat(applicationPropertyService.setMaintenance(false)).isFalse();
        assertThat(applicationPropertyService.getMaintenance()).isFalse();
    }

    @Test
    void getApplicationProperties_whenValidData_thenTheseResults() {
        final ApplicationPropertiesData applicationPropertiesData = applicationPropertyService.getApplicationProperties();
        assertThat(applicationPropertiesData.defaultLocale()).isEqualTo(TestConfigProperties.DEFAULT_LOCALE);
        assertThat(applicationPropertiesData.appTitle()).isEqualTo(TestConfigProperties.APP_TITLE);
        assertThat(applicationPropertiesData.appDescription()).isEqualTo(TestConfigProperties.APP_DESCRIPTION);
        assertThat(applicationPropertiesData.tokenExpiresIn()).isEqualTo(TestConfigProperties.EXPIRES_IN);
        assertThat(applicationPropertiesData.currency()).isEqualTo(TestConfigProperties.CURRENCY);
    }

    @Test
    void title_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getTitle()).isBlank();
        assertThat(applicationPropertyService.setTitle("title")).isEqualTo("title");
        assertThat(applicationPropertyService.getTitle()).isEqualTo("title");
    }

    @Test
    void welcomeText_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getWelcomeText()).isBlank();
        assertThat(applicationPropertyService.setWelcomeText("Welcome Text")).isEqualTo("Welcome Text");
        assertThat(applicationPropertyService.getWelcomeText()).isEqualTo("Welcome Text");
    }

    @Test
    void applicationInfo_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getApplicationInfo()).isEmpty();
        assertThat(applicationPropertyService.setApplicationInfo(List.of("App info"))).isEqualTo(List.of("App info"));
        assertThat(applicationPropertyService.getApplicationInfo()).isEqualTo(List.of("App info"));
    }

    @Test
    void companyInfo_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getCompanyInfo()).isNotNull();
        assertThat(applicationPropertyService.getCompanyInfo().name()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().street()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().city()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().zipCode()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().state()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().phone()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().mail()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().businessId()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().taxId()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().vatRegNo()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().commercialRegisterInfo()).isNotBlank();
        assertThat(applicationPropertyService.getCompanyInfo().mapUrl()).isNotBlank();
        assertThat(applicationPropertyService.setCompanyInfo(CompanyInfoData.builder()
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
                .build())).isNotNull();
        assertThat(applicationPropertyService.getCompanyInfo().name()).isEqualTo("name");
        assertThat(applicationPropertyService.getCompanyInfo().street()).isEqualTo("street");
        assertThat(applicationPropertyService.getCompanyInfo().city()).isEqualTo("city");
        assertThat(applicationPropertyService.getCompanyInfo().zipCode()).isEqualTo("zipCode");
        assertThat(applicationPropertyService.getCompanyInfo().state()).isEqualTo("state");
        assertThat(applicationPropertyService.getCompanyInfo().phone()).isEqualTo("phone");
        assertThat(applicationPropertyService.getCompanyInfo().mail()).isEqualTo("mail");
        assertThat(applicationPropertyService.getCompanyInfo().businessId()).isEqualTo("businessId");
        assertThat(applicationPropertyService.getCompanyInfo().taxId()).isEqualTo("taxId");
        assertThat(applicationPropertyService.getCompanyInfo().vatRegNo()).isEqualTo("vatRegNo");
        assertThat(applicationPropertyService.getCompanyInfo().commercialRegisterInfo()).isEqualTo("commercialRegisterInfo");
        assertThat(applicationPropertyService.getCompanyInfo().mapUrl()).isEqualTo("mapUrl");
    }

    @Test
    void units_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getUnits()).isNotEmpty();
        applicationPropertyService.setUnits(Stream.of(Unit.values()).map(u -> new UnitData(u, u.name())).toList());
        final List<UnitData> units = applicationPropertyService.getUnits();
        assertThat(units).isNotEmpty();
        assertThat(units).hasSize(Unit.values().length);
    }

    @Test
    void vatRate_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getVatRate()).isEqualTo(new BigDecimal("20"));
        assertThat(applicationPropertyService.setVatRate(new BigDecimal("23"))).isEqualTo(new BigDecimal("23"));
        assertThat(applicationPropertyService.getVatRate()).isEqualTo(new BigDecimal("23"));
    }

    @Test
    void businessConditions_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getBusinessConditions()).isBlank();
        assertThat(applicationPropertyService.setBusinessConditions("business conditions")).isEqualTo("business conditions");
        assertThat(applicationPropertyService.getBusinessConditions()).isEqualTo("business conditions");
    }

    @Test
    void cookiesInfo_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getCookiesInfo()).isBlank();
        assertThat(applicationPropertyService.setCookiesInfo("cookies info")).isEqualTo("cookies info");
        assertThat(applicationPropertyService.getCookiesInfo()).isEqualTo("cookies info");
    }

    @Test
    void gdprInfo_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getGdprInfo()).isBlank();
        assertThat(applicationPropertyService.setGdprInfo("gdpr info")).isEqualTo("gdpr info");
        assertThat(applicationPropertyService.getGdprInfo()).isEqualTo("gdpr info");
    }

    @Test
    void orderInfo_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getOrderInfo()).isBlank();
        assertThat(applicationPropertyService.setOrderInfo("order info")).isEqualTo("order info");
        assertThat(applicationPropertyService.getOrderInfo()).isEqualTo("order info");
    }

    @Test
    void workingHours_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getWorkingHours()).isBlank();
        assertThat(applicationPropertyService.setWorkingHours("working hours")).isEqualTo("working hours");
        assertThat(applicationPropertyService.getWorkingHours()).isEqualTo("working hours");
    }

    @Test
    void signUpMail_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getSignUpMail()).isNotNull();
        assertThat(applicationPropertyService.getSignUpMail().subject()).isNotBlank();
        assertThat(applicationPropertyService.getSignUpMail().title()).isNotBlank();
        assertThat(applicationPropertyService.getSignUpMail().message()).isNotBlank();
        assertThat(applicationPropertyService.getSignUpMail().link()).isNotBlank();
        assertThat(applicationPropertyService.setSignUpMail(SignUpMailData.builder()
                .subject("subject")
                .title("title")
                .message("message")
                .link("link")
                .build())).isNotNull();
        assertThat(applicationPropertyService.getSignUpMail().subject()).isEqualTo("subject");
        assertThat(applicationPropertyService.getSignUpMail().title()).isEqualTo("title");
        assertThat(applicationPropertyService.getSignUpMail().message()).isEqualTo("message");
        assertThat(applicationPropertyService.getSignUpMail().link()).isEqualTo("link");
    }

    @Test
    void resetPasswordMail_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getResetPasswordMail()).isNotNull();
        assertThat(applicationPropertyService.getResetPasswordMail().subject()).isNotBlank();
        assertThat(applicationPropertyService.getResetPasswordMail().title()).isNotBlank();
        assertThat(applicationPropertyService.getResetPasswordMail().message()).isNotBlank();
        assertThat(applicationPropertyService.getResetPasswordMail().passwordMessage()).isNotBlank();
        assertThat(applicationPropertyService.getResetPasswordMail().link()).isNotBlank();
        assertThat(applicationPropertyService.setResetPasswordMail(ResetPasswordMailData.builder()
                .subject("subject")
                .title("title")
                .message("message")
                .passwordMessage("password message")
                .link("link")
                .build())).isNotNull();
        assertThat(applicationPropertyService.getResetPasswordMail().subject()).isEqualTo("subject");
        assertThat(applicationPropertyService.getResetPasswordMail().title()).isEqualTo("title");
        assertThat(applicationPropertyService.getResetPasswordMail().message()).isEqualTo("message");
        assertThat(applicationPropertyService.getResetPasswordMail().passwordMessage()).isEqualTo("password message");
        assertThat(applicationPropertyService.getResetPasswordMail().link()).isEqualTo("link");
    }

    @Test
    void manufactureProperties_whenValidData_thenTheseResults() {
        ManufacturePropertiesData manufactureProperties = applicationPropertyService.getManufactureProperties();
        assertThat(manufactureProperties).isNotNull();
        assertThat(manufactureProperties.minimalSystemDimensions()).isNotNull();
        assertThat(manufactureProperties.minimalEdgedBoardDimensions()).isNotNull();
        assertThat(manufactureProperties.minimalLayeredBoardDimensions()).isNotNull();
        assertThat(manufactureProperties.minimalFrameBoardDimensions()).isNotNull();
        assertThat(manufactureProperties.edgeWidthAppend()).isNotNull();
        assertThat(manufactureProperties.edgeLengthAppend()).isNotNull();
        assertThat(manufactureProperties.duplicatedBoardAppend()).isNotNull();
        applicationPropertyService.setManufactureProperties(ManufacturePropertiesData.builder()
                .minimalSystemDimensions(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE))
                .minimalEdgedBoardDimensions(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE))
                .minimalLayeredBoardDimensions(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE))
                .minimalFrameBoardDimensions(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE))
                .edgeWidthAppend(BigDecimal.ONE)
                .edgeLengthAppend(BigDecimal.ONE)
                .duplicatedBoardAppend(BigDecimal.ONE)
                .build());
        manufactureProperties = applicationPropertyService.getManufactureProperties();
        assertThat(manufactureProperties).isNotNull();
        assertThat(manufactureProperties.minimalSystemDimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(manufactureProperties.minimalEdgedBoardDimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(manufactureProperties.minimalLayeredBoardDimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(manufactureProperties.minimalFrameBoardDimensions()).isEqualTo(new DimensionsData(BigDecimal.ONE, BigDecimal.ONE));
        assertThat(manufactureProperties.edgeWidthAppend()).isEqualTo(BigDecimal.ONE);
        assertThat(manufactureProperties.edgeLengthAppend()).isEqualTo(BigDecimal.ONE);
        assertThat(manufactureProperties.duplicatedBoardAppend()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void priceForGluingLayer_whenValidData_thenTheseResults() {
        PriceForGluingLayerData priceForGluingLayerData = applicationPropertyService.getPriceForGluingLayer();
        assertThat(priceForGluingLayerData).isNotNull();
        assertThat(priceForGluingLayerData.price()).isEqualTo(new BigDecimal("10"));
        applicationPropertyService.setPriceForGluingLayer(new PriceForGluingLayerData(BigDecimal.ONE));
        priceForGluingLayerData = applicationPropertyService.getPriceForGluingLayer();
        assertThat(priceForGluingLayerData).isNotNull();
        assertThat(priceForGluingLayerData.price()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void pricesForGluingEdge_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getPricesForGluingEdge()).isNotEmpty();
        applicationPropertyService.setPricesForGluingEdge(List.of(new PriceForGluingEdgeData(BigDecimal.ZERO, BigDecimal.ZERO)));
        assertThat(applicationPropertyService.getPricesForGluingEdge()).hasSize(1);
        assertThat(applicationPropertyService.getPricesForGluingEdge().getFirst().width()).isEqualTo(BigDecimal.ZERO);
        assertThat(applicationPropertyService.getPricesForGluingEdge().getFirst().price()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void pricesForCutting_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getPricesForCutting()).isNotEmpty();
        applicationPropertyService.setPricesForCutting(List.of(new PriceForCuttingData(BigDecimal.ZERO, BigDecimal.ZERO)));
        assertThat(applicationPropertyService.getPricesForCutting()).hasSize(1);
        assertThat(applicationPropertyService.getPricesForCutting().getFirst().thickness()).isEqualTo(BigDecimal.ZERO);
        assertThat(applicationPropertyService.getPricesForCutting().getFirst().price()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void freeDays_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getFreeDays()).isNotEmpty();
        applicationPropertyService.setFreeDays(List.of(new FreeDayData("name", 0, 0)));
        assertThat(applicationPropertyService.getFreeDays()).hasSize(1);
        assertThat(applicationPropertyService.getFreeDays().getFirst().name()).isEqualTo("name");
        assertThat(applicationPropertyService.getFreeDays().getFirst().day()).isEqualTo(0);
        assertThat(applicationPropertyService.getFreeDays().getFirst().month()).isEqualTo(0);
    }

    @Test
    void orderCommentMail_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getOrderCommentMail()).isNotNull();
        assertThat(applicationPropertyService.getOrderCommentMail().subject()).isNotBlank();
        assertThat(applicationPropertyService.getOrderCommentMail().title()).isNotBlank();
        assertThat(applicationPropertyService.getOrderCommentMail().message()).isNotBlank();
        assertThat(applicationPropertyService.getOrderCommentMail().link()).isNotBlank();
        assertThat(applicationPropertyService.setOrderCommentMail(OrderCommentMailData.builder()
                .subject("subject")
                .title("title")
                .message("message")
                .link("link")
                .build())).isNotNull();
        assertThat(applicationPropertyService.getOrderCommentMail().subject()).isEqualTo("subject");
        assertThat(applicationPropertyService.getOrderCommentMail().title()).isEqualTo("title");
        assertThat(applicationPropertyService.getOrderCommentMail().message()).isEqualTo("message");
        assertThat(applicationPropertyService.getOrderCommentMail().link()).isEqualTo("link");
    }

    @Test
    void orderSendMail_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getOrderSendMail()).isNotNull();
        assertThat(applicationPropertyService.getOrderSendMail().subject()).isNotBlank();
        assertThat(applicationPropertyService.getOrderSendMail().title()).isNotBlank();
        assertThat(applicationPropertyService.getOrderSendMail().message()).isNotBlank();
        assertThat(applicationPropertyService.getOrderSendMail().link()).isNotBlank();
        assertThat(applicationPropertyService.getOrderSendMail().attachment()).isNotBlank();
        assertThat(applicationPropertyService.setOrderSendMail(OrderSendMailData.builder()
                .subject("subject")
                .title("title")
                .message("message")
                .link("link")
                .attachment("attachment")
                .build())).isNotNull();
        assertThat(applicationPropertyService.getOrderSendMail().subject()).isEqualTo("subject");
        assertThat(applicationPropertyService.getOrderSendMail().title()).isEqualTo("title");
        assertThat(applicationPropertyService.getOrderSendMail().message()).isEqualTo("message");
        assertThat(applicationPropertyService.getOrderSendMail().link()).isEqualTo("link");
        assertThat(applicationPropertyService.getOrderSendMail().attachment()).isEqualTo("attachment");
    }

    @Test
    void orderStatusMail_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getOrderStatusMail()).isNotNull();
        assertThat(applicationPropertyService.getOrderStatusMail().productionSubject()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().productionTitle()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().productionMessage()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().readySubject()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().readyTitle()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().readyMessage()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().finishedSubject()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().finishedTitle()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().finishedMessage()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().cancelledSubject()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().cancelledTitle()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().cancelledMessage()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().link()).isNotBlank();
        assertThat(applicationPropertyService.getOrderStatusMail().attachment()).isNotBlank();
        assertThat(applicationPropertyService.setOrderStatusMail(OrderStatusMailData.builder()
                .productionSubject("productionSubject")
                .productionTitle("productionTitle")
                .productionMessage("productionMessage")
                .readySubject("readySubject")
                .readyTitle("readyTitle")
                .readyMessage("readyMessage")
                .finishedSubject("finishedSubject")
                .finishedTitle("finishedTitle")
                .finishedMessage("finishedMessage")
                .cancelledSubject("cancelledSubject")
                .cancelledTitle("cancelledTitle")
                .cancelledMessage("cancelledMessage")
                .link("link")
                .attachment("attachment")
                .build())).isNotNull();
        assertThat(applicationPropertyService.getOrderStatusMail().productionSubject()).isEqualTo("productionSubject");
        assertThat(applicationPropertyService.getOrderStatusMail().productionTitle()).isEqualTo("productionTitle");
        assertThat(applicationPropertyService.getOrderStatusMail().productionMessage()).isEqualTo("productionMessage");
        assertThat(applicationPropertyService.getOrderStatusMail().readySubject()).isEqualTo("readySubject");
        assertThat(applicationPropertyService.getOrderStatusMail().readyTitle()).isEqualTo("readyTitle");
        assertThat(applicationPropertyService.getOrderStatusMail().readyMessage()).isEqualTo("readyMessage");
        assertThat(applicationPropertyService.getOrderStatusMail().finishedSubject()).isEqualTo("finishedSubject");
        assertThat(applicationPropertyService.getOrderStatusMail().finishedTitle()).isEqualTo("finishedTitle");
        assertThat(applicationPropertyService.getOrderStatusMail().finishedMessage()).isEqualTo("finishedMessage");
        assertThat(applicationPropertyService.getOrderStatusMail().cancelledSubject()).isEqualTo("cancelledSubject");
        assertThat(applicationPropertyService.getOrderStatusMail().cancelledTitle()).isEqualTo("cancelledTitle");
        assertThat(applicationPropertyService.getOrderStatusMail().cancelledMessage()).isEqualTo("cancelledMessage");
        assertThat(applicationPropertyService.getOrderStatusMail().link()).isEqualTo("link");
        assertThat(applicationPropertyService.getOrderStatusMail().attachment()).isEqualTo("attachment");
    }

    @Test
    void boardMaterialCategory_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getBoardMaterialCategory()).isNotNull();
        assertThat(applicationPropertyService.getBoardMaterialCategory().id()).isEqualTo(-1L);
        assertThat(applicationPropertyService.getBoardMaterialCategory().code()).isEqualTo("NOT FOUND");
        assertThat(applicationPropertyService.getBoardMaterialCategory().name()).isEqualTo("NOT FOUND");
        applicationPropertyService.setBoardMaterialCategory(1L);
        assertThat(applicationPropertyService.getBoardMaterialCategory().id()).isEqualTo(1L);
        assertThat(applicationPropertyService.getBoardMaterialCategory().code()).isEqualTo("code");
        assertThat(applicationPropertyService.getBoardMaterialCategory().name()).isEqualTo("name");
    }

    @Test
    void orderProperties_whenValidData_thenTheseResults() {
        assertThat(applicationPropertyService.getOrderProperties()).isNotNull();
        applicationPropertyService.setOrderProperties(OrderPropertiesData.builder()
                .dimensions(Map.of())
                .boards(Map.of())
                .edges(Map.of())
                .corners(Map.of())
                .pattern(Map.of())
                .content(Map.of())
                .packageType(Map.of())
                .csvSeparator(";")
                .csvReplacements(Map.of())
                .csvColumns(Map.of())
                .build());
        assertThat(applicationPropertyService.getOrderProperties().csvSeparator()).isEqualTo(";");
    }
}
