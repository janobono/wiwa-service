package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.OrderItemWebMapper;
import sk.janobono.wiwa.business.service.OrderItemService;

@RequiredArgsConstructor
@Service
public class OrderItemApiService {

    private final OrderItemService orderItemService;
    private final OrderItemWebMapper orderItemWebMapper;
}
