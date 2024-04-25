package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.api.model.edge.EdgeCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeCategoryItemWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeWebDto;
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

class EdgeControllerTest extends BaseControllerTest {


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

        final List<EdgeWebDto> edges = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            edges.add(addEdge(headers, new EdgeChangeWebDto(
                    "code-edge-" + i,
                    "name-edge-" + i,
                    "this is edge " + i,
                    new BigDecimal("100.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("2070.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("18.000").add(BigDecimal.valueOf(i)),
                    new BigDecimal("50.000").add(BigDecimal.valueOf(i))
            )));
        }

        for (final EdgeWebDto edge : edges) {
            assertThat(edge).usingRecursiveComparison().isEqualTo(getEdge(headers, edge.id()));
        }

        Page<EdgeWebDto> searchResult = getEdges(headers,
                "edge-1",
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

        searchResult = getEdges(headers,
                null,
                "code-edge-1",
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

        searchResult = getEdges(headers,
                null,
                null,
                "edge-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);

        searchResult = getEdges(headers,
                null,
                null,
                null,
                new BigDecimal(2075),
                null,
                null,
                null,
                null,
                null,
                null,
                Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(5);

        searchResult = getEdges(headers,
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

        searchResult = getEdges(headers,
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

        searchResult = getEdges(headers,
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

        searchResult = getEdges(headers,
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

        searchResult = getEdges(headers,
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

        EdgeWebDto testEdge = edges.stream()
                .filter(p -> p.code().equals("code-edge-0"))
                .findFirst()
                .orElseThrow();
        final Long testEdgeId = testEdge.id();
        final int edgeIndex = edges.indexOf(testEdge);
        assertThat(edgeIndex).isNotEqualTo(-1);

        testEdge = setEdge(headers, testEdge.id(), new EdgeChangeWebDto(
                "SP01",
                "SPBC01",
                "This is test edge",
                new BigDecimal("100.000"),
                new BigDecimal("2070.000"),
                new BigDecimal("18.000"),
                new BigDecimal("50.000")
        ));
        edges.set(edgeIndex, testEdge);

        for (final EdgeWebDto edge : edges) {
            assertThat(edge).usingRecursiveComparison().isEqualTo(getEdge(headers, edge.id()));
        }

        setEdgeImage(token, testEdgeId, "test01.png");
        setEdgeImage(token, testEdgeId, "test02.png");
        setEdgeImage(token, testEdgeId, "test03.png");

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
        List<EdgeCategoryItemWebDto> categoryItems = setEdgeCodeListItems(headers, testEdgeId,
                List.of(new EdgeCategoryItemChangeWebDto(codeList.getId(), codeListItem01.getId()))).categoryItems();
        List<EdgeCategoryItemWebDto> savedCategoryItems = getEdge(headers, testEdgeId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(1);
        assertThat(categoryItems.getFirst().id()).isEqualTo(codeListItem01.getId());
        assertThat(categoryItems.getFirst()).isEqualTo(savedCategoryItems.getFirst());

        categoryItems = setEdgeCodeListItems(headers, testEdgeId,
                List.of(new EdgeCategoryItemChangeWebDto(codeList.getId(), codeListItem02.getId()))).categoryItems();
        savedCategoryItems = getEdge(headers, testEdgeId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(1);
        assertThat(categoryItems.getFirst().id()).isEqualTo(codeListItem02.getId());
        assertThat(categoryItems.getFirst()).isEqualTo(savedCategoryItems.getFirst());

        categoryItems = setEdgeCodeListItems(headers, testEdgeId,
                List.of(new EdgeCategoryItemChangeWebDto(codeList.getId(), codeListItem01.getId()),
                        new EdgeCategoryItemChangeWebDto(codeList.getId(), codeListItem02.getId()))).categoryItems();
        savedCategoryItems = getEdge(headers, testEdgeId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(2);
        assertThat(categoryItems.get(0).id()).isEqualTo(savedCategoryItems.get(0).id());
        assertThat(categoryItems.get(1)).isEqualTo(savedCategoryItems.get(1));

        searchResult = getEdges(headers,
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

        searchResult = getEdges(headers,
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

        categoryItems = setEdgeCodeListItems(headers, testEdgeId, Collections.emptyList()).categoryItems();
        savedCategoryItems = getEdge(headers, testEdgeId).categoryItems();
        assertThat(categoryItems.size()).isEqualTo(0);
        assertThat(categoryItems.size()).isEqualTo(savedCategoryItems.size());

        deleteEdgeImage(headers, testEdgeId);
        for (final EdgeWebDto edge : edges) {
            deleteEdge(headers, edge.id());
        }
    }

    private EdgeWebDto getEdge(final HttpHeaders headers, final Long id) {
        return getEntity(EdgeWebDto.class, headers, "/edges", id);
    }

    private Page<EdgeWebDto> getEdges(final HttpHeaders headers,
                                      final String searchField,
                                      final String code,
                                      final String name,
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
        addToParams(params, "widthFrom", widthFrom);
        addToParams(params, "widthTo", widthTo);
        addToParams(params, "thicknessFrom", thicknessFrom);
        addToParams(params, "thicknessTo", thicknessTo);
        addToParams(params, "priceFrom", priceFrom);
        addToParams(params, "priceTo", priceTo);
        addToParams(params, "codeListItems", codeListItems);
        return getEntities(EdgeWebDto.class, headers, "/edges", params, pageable);
    }

    private EdgeWebDto addEdge(final HttpHeaders headers, final EdgeChangeWebDto edgeChange) {
        return addEntity(EdgeWebDto.class, headers, "/edges", edgeChange);
    }

    private EdgeWebDto setEdge(final HttpHeaders headers, final Long id, final EdgeChangeWebDto edgeChange) {
        return setEntity(EdgeWebDto.class, headers, "/edges", id, edgeChange);
    }

    private void deleteEdge(final HttpHeaders headers, final Long id) {
        deleteEntity(headers, "/edges", id);
    }

    private byte[] getEdgeImage(final Long id, final String fileName) {
        return restTemplate.getForObject(
                getURI("/ui/edge-images/{id}/{fileName}", Map.of("id", Long.toString(id), "fileName", fileName)),
                byte[].class
        );
    }

    private void setEdgeImage(final String token, final Long id, final String fileName) {
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

        final ResponseEntity<Void> response = restTemplate.exchange(
                getURI("/edges/{id}/images", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                httpEntity,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public void deleteEdgeImage(final HttpHeaders headers, final Long id) {
        final ResponseEntity<Void> response = restTemplate.exchange(
                getURI("/edges/{id}/images", Map.of("id", Long.toString(id))),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public EdgeWebDto setEdgeCodeListItems(final HttpHeaders headers, final Long id, final List<EdgeCategoryItemChangeWebDto> categoryItems) {
        final ResponseEntity<EdgeWebDto> response = restTemplate.exchange(
                getURI("/edges/{id}/category-items", Map.of("id", Long.toString(id))),
                HttpMethod.POST,
                new HttpEntity<>(categoryItems, headers),
                EdgeWebDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }
}
