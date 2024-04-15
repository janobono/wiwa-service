package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
