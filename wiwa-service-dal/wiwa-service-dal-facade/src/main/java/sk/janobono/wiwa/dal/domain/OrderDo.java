package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.OrderPackageType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class OrderDo {
    private Long id;
    private Long userId;
    private LocalDateTime created;
    private Long orderNumber;
    private LocalDate delivery;
    private OrderPackageType packageType;
    private BigDecimal weight;
    private BigDecimal total;
    private String data;
}
