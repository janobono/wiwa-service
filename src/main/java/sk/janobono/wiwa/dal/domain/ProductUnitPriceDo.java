package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString
@Entity
@Table(name = "wiwa_product_unit_price")
public class ProductUnitPriceDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "value")),
            @AttributeOverride(name = "unit", column = @Column(name = "unit"))
    })
    private QuantityDo price;
}
