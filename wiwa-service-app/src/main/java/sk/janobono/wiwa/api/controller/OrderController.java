package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.api.service.OrderApiService;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    private final OrderApiService orderApiService;

    @GetMapping
    public Page<OrderWebDto> getOrders(
            @RequestParam(value = "userIds", required = false) final List<Long> userIds,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime createdTo,
            @RequestParam(value = "statuses", required = false) final List<OrderStatus> statuses,
            @RequestParam(value = "totalFrom", required = false) final BigDecimal totalFrom,
            @RequestParam(value = "totalTo", required = false) final BigDecimal totalTo,
            @RequestParam(value = "totalUnit", required = false) final Unit totalUnit,
            final Pageable pageable
    ) {
        return orderApiService.getOrders(
                userIds,
                createdFrom,
                createdTo,
                statuses,
                totalFrom,
                totalTo,
                totalUnit,
                pageable
        );
    }

    @GetMapping("/contacts")
    public Page<OrderContactWebDto> getOrderContacts(final Pageable pageable) {
        return orderApiService.getOrderContacts(pageable);
    }

    @GetMapping("/{id}")
    public OrderWebDto getOrder(@PathVariable("id") final Long id) {
        return orderApiService.getOrder(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderWebDto addOrder() {
        return orderApiService.addOrder();
    }

    @PostMapping("/{id}/send")
    public OrderWebDto sendOrder(@PathVariable("id") final Long id, @Valid @RequestBody final SendOrderWebDto sendOrder) {
        return orderApiService.sendOrder(id, sendOrder);
    }

    @PutMapping("/{id}/status")
    public OrderWebDto setOrderStatus(@PathVariable("id") final Long id, @Valid @RequestBody final OrderStatusChangeWebDto orderStatusChange) {
        return orderApiService.setOrderStatus(id, orderStatusChange);
    }

    @GetMapping("/{id}/comments")
    public List<OrderCommentWebDto> getComments(@PathVariable("id") final Long id) {
        return orderApiService.getComments(id);
    }

    @PostMapping("/{id}/comments")
    public List<OrderCommentWebDto> addComment(@PathVariable("id") final Long id, @Valid @RequestBody final OrderCommentChangeWebDto commentChange) {
        return orderApiService.addComment(id, commentChange);
    }

    @GetMapping("/{id}/item")
    public List<OrderItemWebDto> getItems(@PathVariable("id") final Long id) {
        return orderApiService.getItems(id);
    }

    @PostMapping("/{id}/item")
    public OrderItemWebDto addItem(@PathVariable("id") final Long id, @Valid @RequestBody final OrderItemChangeWebDto orderItemChange) {
        return orderApiService.addItem(id, orderItemChange);
    }

    @PutMapping("/{id}/item/{itemId}")
    public OrderItemWebDto setItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId, @Valid @RequestBody final OrderItemChangeWebDto orderItemChange) {
        return orderApiService.setItem(id, itemId, orderItemChange);
    }

    @PatchMapping("/{id}/item/{itemId}/move-up")
    public OrderItemWebDto moveUpItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        return orderApiService.moveUpItem(id, itemId);
    }

    @PatchMapping("/{id}/item/{itemId}/move-down")
    public OrderItemWebDto moveDownItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        return orderApiService.moveDownItem(id, itemId);
    }

    @DeleteMapping("/{id}/item/{itemId}")
    public void deleteItem(@PathVariable("id") final Long id, @PathVariable("itemId") final Long itemId) {
        orderApiService.deleteItem(id, itemId);
    }

    @GetMapping("/{id}/summary")
    public OrderSummaryWebDto getOrderSummary(@PathVariable("id") final Long id) {
        return orderApiService.getOrderSummary(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") final Long id) {
        orderApiService.deleteOrder(id);
    }
}
