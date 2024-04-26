package sk.janobono.wiwa.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.ResourceEntityWebDto;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.api.model.order.OrderItemChangeWebDto;
import sk.janobono.wiwa.api.service.OrderApiService;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    private final OrderApiService orderApiService;

    @Operation(parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page", content = @Content(schema = @Schema(type = "integer"))),
            @Parameter(in = ParameterIn.QUERY, name = "size", content = @Content(schema = @Schema(type = "integer"))),
            @Parameter(in = ParameterIn.QUERY, name = "sort",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))
            )
    })
    @GetMapping
    public Page<OrderWebDto> getOrders(
            @RequestParam(value = "userIds", required = false) final Set<Long> userIds,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdTo,
            @RequestParam(value = "statuses", required = false) final Set<OrderStatus> statuses,
            @RequestParam(value = "totalFrom", required = false) final BigDecimal totalFrom,
            @RequestParam(value = "totalTo", required = false) final BigDecimal totalTo,
            final Pageable pageable
    ) {
        return orderApiService.getOrders(
                userIds,
                createdFrom,
                createdTo,
                statuses,
                totalFrom,
                totalTo,
                pageable
        );
    }

    @GetMapping("/contacts")
    public Page<OrderContactWebDto> getOrderContacts(final Pageable pageable) {
        return orderApiService.getOrderContacts(pageable);
    }

    @PostMapping("/{id}/contact")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public OrderWebDto setOrderContact(@PathVariable("id") final long id, @Valid @RequestBody final OrderContactWebDto orderContact) {
        return orderApiService.setOrderContact(id, orderContact);
    }

    @GetMapping("/{id}")
    public OrderWebDto getOrder(@PathVariable("id") final long id) {
        return orderApiService.getOrder(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderWebDto addOrder() {
        return orderApiService.addOrder();
    }

    @PostMapping("/{id}/recount")
    public OrderWebDto recountOrder(@PathVariable("id") final long id) {
        return orderApiService.recountOrder(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getPdf(@PathVariable("id") final long id) {
        final ResourceEntityWebDto resourceEntity = orderApiService.getPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @GetMapping("/{id}/csv")
    @PreAuthorize("hasAnyAuthority('w-admin', 'w-manager')")
    public ResponseEntity<Resource> getCsv(@PathVariable("id") final long id) {
        final ResourceEntityWebDto resourceEntity = orderApiService.getCsv(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceEntity.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceEntity.fileName() + "\"")
                .body(resourceEntity.resource());
    }

    @PostMapping("/{id}/send")
    public OrderWebDto sendOrder(@PathVariable("id") final long id, @Valid @RequestBody final SendOrderWebDto sendOrder) {
        return orderApiService.sendOrder(id, sendOrder);
    }

    @PutMapping("/{id}/status")
    public OrderWebDto setOrderStatus(@PathVariable("id") final long id, @Valid @RequestBody final OrderStatusChangeWebDto orderStatusChange) {
        return orderApiService.setOrderStatus(id, orderStatusChange);
    }

    @PostMapping("/{id}/comments")
    public OrderWebDto addComment(@PathVariable("id") final long id, @Valid @RequestBody final OrderCommentChangeWebDto orderCommentChange) {
        return orderApiService.addComment(id, orderCommentChange);
    }

    @PostMapping("/{id}/item")
    public OrderWebDto addItem(@PathVariable("id") final long id, @Valid @RequestBody final OrderItemChangeWebDto orderItemChange) {
        return orderApiService.addItem(id, orderItemChange);
    }

    @PutMapping("/{id}/item/{itemId}")
    public OrderWebDto setItem(@PathVariable("id") final long id, @PathVariable("itemId") final long itemId, @Valid @RequestBody final OrderItemChangeWebDto orderItemChange) {
        return orderApiService.setItem(id, itemId, orderItemChange);
    }

    @PatchMapping("/{id}/item/{itemId}/move-up")
    public OrderWebDto moveUpItem(@PathVariable("id") final long id, @PathVariable("itemId") final long itemId) {
        return orderApiService.moveUpItem(id, itemId);
    }

    @PatchMapping("/{id}/item/{itemId}/move-down")
    public OrderWebDto moveDownItem(@PathVariable("id") final long id, @PathVariable("itemId") final long itemId) {
        return orderApiService.moveDownItem(id, itemId);
    }

    @DeleteMapping("/{id}/item/{itemId}")
    public OrderWebDto deleteItem(@PathVariable("id") final long id, @PathVariable("itemId") final long itemId) {
        return orderApiService.deleteItem(id, itemId);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") final long id) {
        orderApiService.deleteOrder(id);
    }
}
