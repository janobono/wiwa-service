package sk.janobono.wiwa.dal.model;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sk.janobono.wiwa.business.model.user.UserSearchCriteriaSo;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.UserDo;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public record UserSearchCriteriaDo(ScDf scDf, UserSearchCriteriaSo criteria) implements Specification<UserDo> {

    public Predicate toPredicate(final Root<UserDo> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        criteriaQuery.distinct(true);
        if (!StringUtils.hasLength(criteria.searchField())
                && !StringUtils.hasLength(criteria.username())
                && !StringUtils.hasLength(criteria.email())
        ) {
            log.debug("Empty criteria.");
            return criteriaQuery.getRestriction();
        }

        final List<Predicate> predicates = new ArrayList<>();
        // search field
        if (StringUtils.hasLength(criteria.searchField())) {
            predicates.add(searchFieldToPredicate(criteria.searchField(), root, criteriaBuilder));
        }
        // username
        if (StringUtils.hasLength(criteria.username())) {
            predicates.add(criteriaBuilder.like(root.get("username"), "%" + criteria.username() + "%"));
        }
        // email
        if (StringUtils.hasLength(criteria.username())) {
            predicates.add(criteriaBuilder.like(toScDf(root.get("email"), criteriaBuilder), "%" + scDf.toScDf(criteria.email()) + "%"));
        }
        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new))).getRestriction();
    }

    private Predicate searchFieldToPredicate(final String searchField, final Root<UserDo> root, final CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();
        final String[] fieldValues = searchField.split(" ");
        for (String fieldValue : fieldValues) {
            fieldValue = "%" + scDf.toScDf(fieldValue) + "%";
            final List<Predicate> subPredicates = new ArrayList<>();
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("username"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("firstName"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("midName"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("lastName"), criteriaBuilder), fieldValue));
            subPredicates.add(criteriaBuilder.like(toScDf(root.get("email"), criteriaBuilder), fieldValue));
            predicates.add(criteriaBuilder.or(subPredicates.toArray(Predicate[]::new)));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Expression<String> toScDf(final Path<String> path, final CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lower(criteriaBuilder.function("unaccent", String.class, path));
    }
}
