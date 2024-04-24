package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

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
    private String data;
}
