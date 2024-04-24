package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.application.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.board.BoardCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardCategoryItemWebDto;
import sk.janobono.wiwa.api.model.board.BoardChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardWebDto;
import sk.janobono.wiwa.business.impl.component.PriceUtil;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                    new BigDecimal("100.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("2800.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("2070.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("18.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("50.000").add(BigDecimal.valueOf(i))
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
                new BigDecimal(2075),
                null, null,
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
                new BigDecimal(2074),
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
                new BigDecimal(23),
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
                new BigDecimal(22),
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
                priceUtil.countVatValue(new BigDecimal(55), applicationPropertyService.getVatRate()),
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
                priceUtil.countVatValue(new BigDecimal(54), applicationPropertyService.getVatRate()),
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
                new BigDecimal("100.000"),
                new BigDecimal("2800.000"),
                new BigDecimal("2070.000"),
                new BigDecimal("18.000"),
                new BigDecimal("50.000")
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
                                        final BigDecimal widthFrom,
                                        final BigDecimal widthTo,
                                        final BigDecimal thicknessFrom,
                                        final BigDecimal thicknessTo,
                                        final BigDecimal priceFrom,
                                        final BigDecimal priceTo,
                                        final List<String> codeListItems,
                                        final Pageable pageable) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addToParams(params, "searchField", searchField);
        addToParams(params, "code", code);
        addToParams(params, "name", name);
        addToParams(params, "boardCode", boardCode);
        addToParams(params, "structureCode", structureCode);
        addToParams(params, "orientation", orientation);
        addToParams(params, "lengthFrom", lengthFrom);
        addToParams(params, "lengthTo", lengthTo);
        addToParams(params, "widthFrom", widthFrom);
        addToParams(params, "widthTo", widthTo);
        addToParams(params, "thicknessFrom", thicknessFrom);
        addToParams(params, "thicknessTo", thicknessTo);
        addToParams(params, "priceFrom", priceFrom);
        addToParams(params, "priceTo", priceTo);
        addToParams(params, "codeListItems", codeListItems);
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
}
