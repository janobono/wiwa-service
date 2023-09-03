package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ProductType;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@ToString(exclude = {"attributes", "productUnitPrices", "productCategories"})
@Entity
@Table(name = "wiwa_product")
public class ProductDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProductType type;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "note")
    private String note;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "sale_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "sale_unit"))
    })
    private QuantityDo saleUnit;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "weight_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "weight_unit"))
    })
    private QuantityDo weight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "net_weight_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "net_weight_unit"))
    })
    private QuantityDo netWeight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "length_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "length_unit"))
    })
    private QuantityDo length;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "width_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "width_unit"))
    })
    private QuantityDo width;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "thickness_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "thickness_unit"))
    })
    private QuantityDo thickness;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status")
    private ProductStockStatus stockStatus;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<ProductAttributeDo> attributes;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<ProductUnitPriceDo> productUnitPrices;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "wiwa_product_categories",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "product_category_id")}
    )
    private List<ProductCategoryDo> productCategories;

    public List<ProductAttributeDo> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    public List<ProductUnitPriceDo> getProductUnitPrices() {
        if (productUnitPrices == null) {
            productUnitPrices = new ArrayList<>();
        }
        return productUnitPrices;
    }

    public List<ProductCategoryDo> getProductCategories() {
        if (productUnitPrices == null) {
            productUnitPrices = new ArrayList<>();
        }
        return productCategories;
    }
}
