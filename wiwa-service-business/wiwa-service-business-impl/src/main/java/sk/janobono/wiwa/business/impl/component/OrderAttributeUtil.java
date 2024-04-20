package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.model.OrderAttributeKey;

@RequiredArgsConstructor
@Component
public class OrderAttributeUtil {

    private final ObjectMapper objectMapper;

    public <T> T parseValue(final OrderAttributeDo orderAttributeDo, final Class<T> clazz) {
        try {
            return objectMapper.readValue(orderAttributeDo.getAttributeValue(), clazz);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> OrderAttributeDo serializeValue(final Long orderId, final OrderAttributeKey key, final T value) {
        try {
            return OrderAttributeDo.builder()
                    .orderId(orderId)
                    .attributeKey(key)
                    .attributeValue(objectMapper.writeValueAsString(value))
                    .build();
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
