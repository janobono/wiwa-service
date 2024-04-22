package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.model.board.BoardCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardWebDto;
import sk.janobono.wiwa.api.service.BoardApiService;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/boards")
public class BoardController {

    private final BoardApiService boardApiService;

    @GetMapping
    public Page<BoardWebDto> getBoards(
            @RequestParam(value = "searchField", required = false) final String searchField,
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "boardCode", required = false) final String boardCode,
            @RequestParam(value = "structureCode", required = false) final String structureCode,
            @RequestParam(value = "orientation", required = false) final Boolean orientation,
            @RequestParam(value = "lengthFrom", required = false) final BigDecimal lengthFrom,
            @RequestParam(value = "lengthTo", required = false) final BigDecimal lengthTo,
            @RequestParam(value = "widthFrom", required = false) final BigDecimal widthFrom,
            @RequestParam(value = "widthTo", required = false) final BigDecimal widthTo,
            @RequestParam(value = "thicknessFrom", required = false) final BigDecimal thicknessFrom,
            @RequestParam(value = "thicknessTo", required = false) final BigDecimal thicknessTo,
            @RequestParam(value = "priceFrom", required = false) final BigDecimal priceFrom,
            @RequestParam(value = "priceTo", required = false) final BigDecimal priceTo,
            @RequestParam(value = "codeListItems", required = false) final List<String> codeListItems,
            final Pageable pageable
    ) {
        return boardApiService.getBoards(
                searchField,
                code,
                name,
                boardCode,
                structureCode,
                orientation,
                lengthFrom,
                lengthTo,
                widthFrom,
                widthTo,
                thicknessFrom,
                thicknessTo,
                priceFrom,
                priceTo,
                codeListItems,
                pageable
        );
    }

    @GetMapping("/{id}")
    public BoardWebDto getBoard(@PathVariable("id") final long id) {
        return boardApiService.getBoard(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardWebDto addBoard(@Valid @RequestBody final BoardChangeWebDto boardChange) {
        return boardApiService.addBoard(boardChange);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public BoardWebDto setBoard(@PathVariable("id") final long id, @Valid @RequestBody final BoardChangeWebDto boardChange) {
        return boardApiService.setBoard(id, boardChange);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public void deleteBoard(@PathVariable("id") final long id) {
        boardApiService.deleteBoard(id);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public BoardWebDto setBoardImage(@PathVariable("id") final long id, @RequestParam("file") final MultipartFile multipartFile) {
        return boardApiService.setBoardImage(id, multipartFile);
    }

    @DeleteMapping("/{id}/images/{fileName}")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public BoardWebDto deleteBoardImage(@PathVariable("id") final long id, @PathVariable("fileName") final String fileName) {
        return boardApiService.deleteBoardImage(id, fileName);
    }

    @PostMapping("/{id}/category-items")
    @PreAuthorize("hasAnyAuthority('p2-admin', 'p2-manager')")
    public BoardWebDto setBoardCategoryItems(@PathVariable("id") final long id, @RequestBody final List<BoardCategoryItemChangeWebDto> categoryItems) {
        return boardApiService.setBoardCategoryItems(id, categoryItems);
    }
}
