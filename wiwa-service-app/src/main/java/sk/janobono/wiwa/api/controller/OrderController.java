package sk.janobono.wiwa.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wiwa.api.model.order.OrderChangeWebDto;
import sk.janobono.wiwa.api.model.order.OrderContactWebDto;
import sk.janobono.wiwa.api.model.order.OrderWebDto;
import sk.janobono.wiwa.api.service.OrderApiService;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    private final OrderApiService orderApiService;

    @GetMapping
    public Page<OrderWebDto> getOrders(
            @RequestParam(value = "userIds", required = false) final List<Long> userIds,
            @RequestParam(value = "createdFrom", required = false) final ZonedDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) final ZonedDateTime createdTo,
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
    public OrderWebDto addOrder(@Valid @RequestBody final OrderChangeWebDto orderChange) {
        return orderApiService.addOrder(orderChange);
    }

    // TODO addComment
    // TODO send
    // TODO cancel
    // TODO ready
    // TODO finish

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") final Long id) {
        orderApiService.deleteOrder(id);
    }
}
