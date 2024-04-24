package sk.janobono.wiwa.business.impl.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataUtil {

    private final ObjectMapper objectMapper;

    public <T> T parseValue(final String data, final Class<T> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> String serializeValue(final T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
