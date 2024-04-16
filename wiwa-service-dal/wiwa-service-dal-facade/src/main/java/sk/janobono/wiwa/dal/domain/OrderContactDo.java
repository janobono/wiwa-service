package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderContactDo {
    private Long orderId;
    private String name;
    private String street;
    private String zipCode;
    private String city;
    private String state;
    private String phone;
    private String email;
    private String businessId;
    private String taxId;
}
