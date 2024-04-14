package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.OrderWebMapper;
import sk.janobono.wiwa.business.service.OrderService;

@RequiredArgsConstructor
@Service
public class OrderApiService {

    private final OrderService orderService;
    private final OrderWebMapper orderWebMapper;
}
