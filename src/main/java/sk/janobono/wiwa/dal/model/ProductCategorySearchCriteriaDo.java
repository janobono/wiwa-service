package sk.janobono.wiwa.dal.model;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import sk.janobono.wiwa.business.model.product.ProductCategorySearchCriteriaSo;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.ProductCategoryDo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public record ProductCategorySearchCriteriaDo(ScDf scDf, ProductCategorySearchCriteriaSo criteria)
        implements Specification<ProductCategoryDo> {

    public Predicate toPredicate(final Root<ProductCategoryDo> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        criteriaQuery.distinct(true);
        if (Optional.ofNullable(criteria.rootCategories()).isEmpty()
                && Optional.ofNullable(criteria.parentCategoryId()).isEmpty()
                && Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isEmpty()
                && Optional.ofNullable(criteria.treeCode()).filter(s -> !s.isBlank()).isEmpty()
        ) {
            log.debug("Empty criteria.");
            return criteriaQuery.getRestriction();
        }

        final List<Predicate> predicates = new ArrayList<>();
        // root categories
        if (Optional.ofNullable(criteria.rootCategories()).orElse(false)) {
            predicates.add(criteriaBuilder.isNull(root.get("parentId")));
        }
        // parent category id
        if (Optional.ofNullable(criteria.parentCategoryId()).isPresent()) {
            predicates.add(criteriaBuilder.equal(root.get("parentId"), criteria.parentCategoryId()));
        }
        // search field
        if (Optional.ofNullable(criteria.searchField()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(searchFieldToPredicate(criteria.searchField(), root, criteriaBuilder));
        }
        // code
        if (Optional.ofNullable(criteria.code()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(criteriaBuilder.equal(root.get("code"), criteria.code()));
        }
        // name
        if (Optional.ofNullable(criteria.name()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(criteriaBuilder.like(toScDf(root.get("name"), criteriaBuilder), "%" + scDf.toScDf(criteria.name()) + "%"));
        }
        // treeCode
        if (Optional.ofNullable(criteria.treeCode()).filter(s -> !s.isBlank()).isPresent()) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("code"), criteria.treeCode()),
                    criteriaBuilder.like(root.get("treeCode"), "%" + criteria.treeCode() + "::%")
            ));
        }
        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new))).getRestriction();
    }

    private Predicate searchFieldToPredicate(final String searchField, final Root<ProductCategoryDo> root, final CriteriaBuilder criteriaBuilder) {
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
}
