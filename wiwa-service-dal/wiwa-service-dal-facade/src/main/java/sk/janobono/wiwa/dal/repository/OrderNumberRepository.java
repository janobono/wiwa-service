package sk.janobono.wiwa.dal.repository;

public interface OrderNumberRepository {

    Long getNextOrderNumber(final Long userId);
}