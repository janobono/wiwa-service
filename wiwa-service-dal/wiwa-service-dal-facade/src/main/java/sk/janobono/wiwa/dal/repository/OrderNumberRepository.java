package sk.janobono.wiwa.dal.repository;

public interface OrderNumberRepository {

    long getNextOrderNumber(long userId);
}
