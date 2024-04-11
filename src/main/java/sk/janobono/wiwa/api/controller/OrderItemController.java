package sk.janobono.wiwa.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wiwa.api.service.OrderItemApiService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/order-items")
public class OrderItemController {

    private final OrderItemApiService orderItemApiService;


}
