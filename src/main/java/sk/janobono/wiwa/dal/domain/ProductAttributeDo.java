package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString
@Entity
@Table(name = "wiwa_product_attribute")
public class ProductAttributeDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;
}
