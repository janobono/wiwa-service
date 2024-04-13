package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.board.BoardCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardCategoryItemWebDto;
import sk.janobono.wiwa.api.model.board.BoardChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardWebDto;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class BoardControllerTest extends BaseControllerTest {

    @Autowired
    public CommonConfigProperties commonConfigProperties;

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public PriceUtil priceUtil;

    @Autowired
    public ApplicationPropertyService applicationPropertyService;

    @Autowired
    public CodeListRepository codeListRepository;

    @Autowired
    public CodeListItemRepository codeListItemRepository;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        final List<BoardWebDto> boards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            boards.add(addBoard(headers, new BoardChangeWebDto(
                    "code-board-" + i,
                    "name-board-" + i,
                    "this is board " + i,
                    "BC" + i,
                    "SC" + i,
                    i % 2 == 0,
                    new BigDecimal("1.000"),
                    Unit.PIECE,
                    new BigDecimal("120.000").add(BigDecimal.valueOf(i)),
                    Unit.KILOGRAM,
                    new BigDecimal("100.000").add(BigDecimal.valueOf(i)),
                    Unit.KILOGRAM,
                    new BigDecimal("2800.000").add(BigDecimal.valueOf(i)),
                    Unit.MILLIMETER,
                    new BigDecimal("2070.000").add(BigDecimal.valueOf(i)),
                    Unit.MILLIMETER,
                    new BigDecimal("18.000").add(BigDecimal.valueOf(i)),
                    Unit.MILLIMETER,
                    new BigDecimal("50.000").add(BigDecimal.valueOf(i)),
                    Unit.EUR
            )));
        }

        for (final BoardWebDto board : boards) {
            assertThat(board).usingRecursiveComparison().isEqualTo(getBoard(headers, board.id()));
        }

        Page<BoardWebDto> searchResult = getBoards(headers,
                "board-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getBoards(headers,
                null,
                "code-board-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getBoards(headers,
                null,
                null,
                "board-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                "BC1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                "SC1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal(2805),
                null,
                Unit.MILLIMETER,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal(2804),
                Unit.MILLIMETER,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal(2075),
                null,
                Unit.MILLIMETER,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal(2074),
                Unit.MILLIMETER,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal(23),
                null,
                Unit.MILLIMETER,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new BigDecimal(22),
                Unit.MILLIMETER,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                priceUtil.countVatValue(new BigDecimal(55), getVatRate()),
                null,
                Unit.EUR,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                priceUtil.countVatValue(new BigDecimal(54), getVatRate()),
                Unit.EUR,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        BoardWebDto testBoard = boards.stream()
                .filter(p -> p.code().equals("code-board-0"))
                .findFirst()
                .orElseThrow();
        final Long testBoardId = testBoard.id();
        final int boardIndex = boards.indexOf(testBoard);
        assertThat(boardIndex).isNotEqualTo(-1);

        testBoard = setBoard(headers, testBoard.id(), new BoardChangeWebDto(
                "SP01",
                "SPBC01",
                "This is test board",
                "SPSC01",
                "Test board",
                false,
                new BigDecimal("1.000"),
                Unit.PIECE,
                new BigDecimal("120.000"),
                Unit.KILOGRAM,
                new BigDecimal("100.000"),
                Unit.KILOGRAM,
                new BigDecimal("2800.000"),
                Unit.MILLIMETER,
                new BigDecimal("2070.000"),
                Unit.MILLIMETER,
                new BigDecimal("18.000"),
                Unit.MILLIMETER,
                new BigDecimal("50.000"),
                Unit.EUR
        ));
        boards.set(boardIndex, testBoard);

        for (final BoardWebDto board : boards) {
            assertThat(board).usingRecursiveComparison().isEqualTo(getBoard(headers, board.id()));
        }

        setBoardImage(token, testBoardId, "test01.png");
        setBoardImage(token, testBoardId, "test02.png");
        final List<ApplicationImageInfoWebDto> boardImages = setBoardImage(token, testBoardId, "test03.png").images();
        final List<ApplicationImageInfoWebDto> savedBoardImages = getBoard(headers, testBoardId).images();

        for (final ApplicationImageInfoWebDto originalImage : boardImages) {
            final ApplicationImageInfoWebDto savedImage = savedBoardImages.stream()
                    .filter(s -> s.fileName().equals(originalImage.fileName()))
                    .findFirst()
                    .orElseThrow();
            assertThat(savedImage).usingRecursiveComparison().isEqualTo(originalImage);
            assertThat(getBoardImage(testBoardId, savedImage.fileName()))
                    .isEqualTo(imageUtil.scaleImage(
                            "png",
                            imageUtil.generateMessageImage(savedImage.fileName()),
                            commonConfigProperties.maxImageResolution(),
                            commonConfigProperties.maxImageResolution()
                    ));
        }

        final CodeListDo codeList = codeListRepository.save(CodeListDo.builder()
                .code("code")
                .name("test-code-list")
                .build());
        final CodeListItemDo codeListItem01 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code1")
                .code("code1")
                .value("test-item1")
                .sortNum(1)
                .build());
        final CodeListItemDo codeListItem02 = codeListItemRepository.save(CodeListItemDo.builder()
                .codeListId(codeList.getId())
                .treeCode("code2")
                .code("code2")
                .value("test-item2")
                .sortNum(2)
                .build());
        List<BoardCategoryItemWebDto> categoryItems = setBoardCodeListItems(headers, testBoardId,
                List.of(new BoardCategoryItemChangeWebDto(codeList.getId(), codeListItem01.getId()))).categoryItems();
        List<BoardCategoryItemWebDto> savedCategoryItems = getBoard(headers, testBoardId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(1);
        assertThat(categoryItems.getFirst().id()).isEqualTo(codeListItem01.getId());
        assertThat(categoryItems.getFirst()).isEqualTo(savedCategoryItems.getFirst());

        categoryItems = setBoardCodeListItems(headers, testBoardId,
                List.of(new BoardCategoryItemChangeWebDto(codeList.getId(), codeListItem02.getId()))).categoryItems();
        savedCategoryItems = getBoard(headers, testBoardId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(1);
        assertThat(categoryItems.getFirst().id()).isEqualTo(codeListItem02.getId());
        assertThat(categoryItems.getFirst()).isEqualTo(savedCategoryItems.getFirst());

        categoryItems = setBoardCodeListItems(headers, testBoardId,
                List.of(new BoardCategoryItemChangeWebDto(codeList.getId(), codeListItem01.getId()),
                        new BoardCategoryItemChangeWebDto(codeList.getId(), codeListItem02.getId()))).categoryItems();
        savedCategoryItems = getBoard(headers, testBoardId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(2);
        assertThat(categoryItems.get(0).id()).isEqualTo(savedCategoryItems.get(0).id());
        assertThat(categoryItems.get(1)).isEqualTo(savedCategoryItems.get(1));

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of("code1"),
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getBoards(headers,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of("code2"),
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        categoryItems = setBoardCodeListItems(headers, testBoardId, Collections.emptyList()).categoryItems();
        savedCategoryItems = getBoard(headers, testBoardId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(0);
        assertThat(categoryItems.size()).isEqualTo(savedCategoryItems.size());

        for (final ApplicationImageInfoWebDto originalImage : boardImages) {
            deleteBoardImage(headers, testBoardId, originalImage.fileName());
        }
        for (final BoardWebDto board : boards) {
            deleteBoard(headers, board.id());
        }
    }

    private BoardWebDto getBoard(final HttpHeaders headers, final Long id) {
        return getEntity(BoardWebDto.class, headers, "/boards", id);
    }

    private Page<BoardWebDto> getBoards(final HttpHeaders headers,
                                        final String searchField,
                                        final String code,
                                        final String name,
                                        final String boardCode,
                                        final String structureCode,
                                        final Boolean orientation,
                                        final BigDecimal lengthFrom,
                                        final BigDecimal lengthTo,
                                        final Unit lengthUnit,
                                        final BigDecimal widthFrom,
                                        final BigDecimal widthTo,
                                        final Unit widthUnit,
                                        final BigDecimal thicknessFrom,
                                        final BigDecimal thicknessTo,
                                        final Unit thicknessUnit,
                                        final BigDecimal priceFrom,
                                        final BigDecimal priceTo,
                                        final Unit priceUnit,
                                        final List<String> codeListItems,
                                        final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Optional.ofNullable(searchField).ifPresent(v -> addToParams(params, "searchField", v));
        Optional.ofNullable(code).ifPresent(v -> addToParams(params, "code", v));
        Optional.ofNullable(name).ifPresent(v -> addToParams(params, "name", v));
        Optional.ofNullable(boardCode).ifPresent(v -> addToParams(params, "boardCode", v));
        Optional.ofNullable(structureCode).ifPresent(v -> addToParams(params, "structureCode", v));
        Optional.ofNullable(orientation).ifPresent(v -> addToParams(params, "orientation", v.toString()));
        Optional.ofNullable(lengthFrom).ifPresent(v -> addToParams(params, "lengthFrom", v.toPlainString()));
        Optional.ofNullable(lengthTo).ifPresent(v -> addToParams(params, "lengthTo", v.toPlainString()));
        Optional.ofNullable(lengthUnit).ifPresent(v -> addToParams(params, "lengthUnit", v.name()));
        Optional.ofNullable(widthFrom).ifPresent(v -> addToParams(params, "widthFrom", v.toPlainString()));
        Optional.ofNullable(widthTo).ifPresent(v -> addToParams(params, "widthTo", v.toPlainString()));
        Optional.ofNullable(widthUnit).ifPresent(v -> addToParams(params, "widthUnit", v.name()));
        Optional.ofNullable(thicknessFrom).ifPresent(v -> addToParams(params, "thicknessFrom", v.toPlainString()));
        Optional.ofNullable(thicknessTo).ifPresent(v -> addToParams(params, "thicknessTo", v.toPlainString()));
        Optional.ofNullable(thicknessUnit).ifPresent(v -> addToParams(params, "thicknessUnit", v.name()));
        Optional.ofNullable(priceFrom).ifPresent(v -> addToParams(params, "priceFrom", v.toPlainString()));
        Optional.ofNullable(priceTo).ifPresent(v -> addToParams(params, "priceTo", v.toPlainString()));
        Optional.ofNullable(priceUnit).ifPresent(v -> addToParams(params, "priceUnit", v.name()));
        Optional.ofNullable(codeListItems).ifPresent(l -> addToParams(params, "codeListItems", l));
        return getEntities(BoardWebDto.class, headers, "/boards", params, pageable);
    }

    private BoardWebDto addBoard(final HttpHeaders headers, final BoardChangeWebDto boardChange) {
        return addEntity(BoardWebDto.class, headers, "/boards", boardChange);
    }

    private BoardWebDto setBoard(final HttpHeaders headers, final Long id, final BoardChangeWebDto boardChange) {
        return setEntity(BoardWebDto.class, headers, "/boards", id, boardChange);
    }

    private void deleteBoard(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/boards", id);
    }

    private byte[] getBoardImage(final Long id, final String fileName) {
        return restTemplate.getForObject(
                getURI("/ui/board-images/{id}/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                byte[].class
        );
    }

    private BoardWebDto setBoardImage(final String token, final Long id, final String fileName) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        final MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage(fileName)) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        final ResponseEntity<BoardWebDto> response = restTemplate.exchange(
                getURI("/boards/{id}/images", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                httpEntity,
                BoardWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public BoardWebDto deleteBoardImage(final HttpHeaders headers, final Long id, final String fileName) {
        final ResponseEntity<BoardWebDto> response = restTemplate.exchange(
                getURI("/boards/{id}/images/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                BoardWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    public BoardWebDto setBoardCodeListItems(final HttpHeaders headers, final Long id, final List<BoardCategoryItemChangeWebDto> categoryItems) {
        final ResponseEntity<BoardWebDto> response = restTemplate.exchange(
                getURI("/boards/{id}/category-items", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                new HttpEntity<>(categoryItems, headers),
                BoardWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }
}
