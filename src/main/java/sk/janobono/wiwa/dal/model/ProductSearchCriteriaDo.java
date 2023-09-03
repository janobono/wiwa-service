package sk.janobono.wiwa.dal.model;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import sk.janobono.wiwa.business.model.product.ProductSearchCriteriaSo;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.ProductAttributeDo;
import sk.janobono.wiwa.dal.domain.ProductCategoryDo;
import sk.janobono.wiwa.dal.domain.ProductDo;
import sk.janobono.wiwa.dal.domain.ProductUnitPriceDo;
import sk.janobono.wiwa.model.ProductAttributeKey;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public record ProductSearchCriteriaDo(ScDf scDf, ProductSearchCriteriaSo criteria) implements Specification<ProductDo> {

    public Predicate toPredicate(final Root<ProductDo> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        criteriaQuery.distinct(true);
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.type()).isEmpty()
                && Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.categoryCode()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.boardCode()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.structureCode()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.productStockStatus()).isEmpty()
                && Optional.ofNullable(criteria.unitPriceFrom()).isEmpty()
                && Optional.ofNullable(criteria.unitPriceTo()).isEmpty()
                && Optional.ofNullable(criteria.thickness()).isEmpty()
                && Optional.ofNullable(criteria.orientation()).isEmpty()
        ) {
            log.debug("Empty criteria.");
            return criteriaQuery.getRestriction();
        }

        final List<Predicate> predicates = new ArrayList<>();
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(searchFieldToPredicate(criteria.searchField(), root, criteriaBuilder));
        }
        // type
        if (Optional.ofNullable(criteria.type()).isPresent()) {
            predicates.add(criteriaBuilder.equal(root.get("type"), criteria.type()));
        }
        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(criteriaBuilder.equal(root.get("code"), criteria.code()));
        }
        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(criteriaBuilder.like(toScDf(root.get("name"), criteriaBuilder), "%" + scDf.toScDf(criteria.name()) + "%"));
        }
        // product stock status
        if (Optional.ofNullable(criteria.productStockStatus()).isPresent()) {
            predicates.add(criteriaBuilder.equal(root.get("stockStatus"), criteria.productStockStatus()));
        }

        // unit price from
        if (Optional.ofNullable(criteria.unitPriceFrom()).isPresent()) {
            predicates.add(unitPriceFromToPredicate(criteria.unitPriceFrom(), root, criteriaBuilder));
        }

        // unit price to
        if (Optional.ofNullable(criteria.unitPriceTo()).isPresent()) {
            predicates.add(unitPriceToToPredicate(criteria.unitPriceTo(), root, criteriaBuilder));
        }

        // thickness
        if (Optional.ofNullable(criteria.thickness()).isPresent()) {
            predicates.add(criteriaBuilder.equal(root.get("thickness").get("value"), criteria.thickness().value()));
            predicates.add(criteriaBuilder.equal(root.get("thickness").get("unit"), criteria.thickness().unit()));
        }

        // category code
        if (Optional.ofNullable(criteria.categoryCode()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(categoryCodeToPredicate(criteria.categoryCode(), root, criteriaBuilder));
        }

        // board code
        if (Optional.ofNullable(criteria.boardCode()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(productAttributeToPredicate(ProductAttributeKey.BOARD_CODE, criteria.boardCode(), root, criteriaBuilder));
        }
        // structure code
        if (Optional.ofNullable(criteria.structureCode()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(productAttributeToPredicate(ProductAttributeKey.STRUCTURE_CODE, criteria.structureCode(), root, criteriaBuilder));
        }
        // orientation
        if (Optional.ofNullable(criteria.orientation()).isPresent()) {
            predicates.add(productAttributeToPredicate(ProductAttributeKey.ORIENTATION, criteria.orientation().toString(), root, criteriaBuilder));
        }

        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new))).getRestriction();
    }

    private Predicate searchFieldToPredicate(final String searchField, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();
        final String[] fieldValues = searchField.split(" ");
        for (String fieldValue : fieldValues) {
            fieldValue = "%" + scDf.toScDf(fieldValue) + "%";
            final List<Predicate> subPredicates = new ArrayList<>();
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("code"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("name"), criteriaBuilder), fieldValue));
            predicates.add(criteriaBuilder.or(subPredicates.toArray(Predicate[]::new)));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Expression<String> toScDf(final Path<String> path, final CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lower(criteriaBuilder.function("unaccent", String.class, path));
    }

    private Predicate unitPriceFromToPredicate(final BigDecimal value, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
        final Join<ProductDo, ProductUnitPriceDo> join = root.join("productUnitPrices");
        return criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(join.get("validFrom"), LocalDate.now()),
                criteriaBuilder.or(
                        criteriaBuilder.lessThanOrEqualTo(join.get("validTo"), LocalDate.now()),
                        criteriaBuilder.isNull(join.get("validTo"))
                ),
                criteriaBuilder.greaterThanOrEqualTo(join.get("price").get("value"), value)
        );
    }

    private Predicate unitPriceToToPredicate(final BigDecimal value, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
        final Join<ProductDo, ProductUnitPriceDo> join = root.join("productUnitPrices");
        return criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(join.get("validFrom"), LocalDate.now()),
                criteriaBuilder.or(
                        criteriaBuilder.lessThanOrEqualTo(join.get("validTo"), LocalDate.now()),
                        criteriaBuilder.isNull(join.get("validTo"))
                ),
                criteriaBuilder.lessThanOrEqualTo(join.get("price").get("value"), value)
        );
    }

    private Predicate categoryCodeToPredicate(final String code, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
        final Join<ProductDo, ProductCategoryDo> join = root.join("productCategories");
        return criteriaBuilder.or(
                criteriaBuilder.equal(join.get("code"), code),
                criteriaBuilder.like(join.get("treeCode"), "%" + code + "::%")
        );
    }

    private Predicate productAttributeToPredicate(final ProductAttributeKey key, final String value, final Root<ProductDo> root, final CriteriaBuilder criteriaBuilder) {
        final Join<ProductDo, ProductAttributeDo> join = root.join("attributes");
        return criteriaBuilder.and(
                criteriaBuilder.equal(join.get("key"), key.name()),
                criteriaBuilder.equal(join.get("value"), value)
        );
    }
}
